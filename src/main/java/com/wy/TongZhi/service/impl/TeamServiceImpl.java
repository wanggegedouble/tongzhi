package com.wy.TongZhi.service.impl;


import com.wy.TongZhi.mapper.UserMapper;
import com.wy.TongZhi.model.Resp.UserResp;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wy.TongZhi.common.ErrorCode;
import com.wy.TongZhi.contant.TeamStatusEnums;
import com.wy.TongZhi.exception.BusinessException;
import com.wy.TongZhi.mapper.TeamMapper;
import com.wy.TongZhi.mapper.UserTeamMapper;
import com.wy.TongZhi.model.Resp.TeamUserResp;
import com.wy.TongZhi.model.domain.Team;
import com.wy.TongZhi.model.domain.User;
import com.wy.TongZhi.model.domain.UserTeam;
import com.wy.TongZhi.model.request.AddRTeamReq;
import com.wy.TongZhi.model.request.QueryTeamReq;
import com.wy.TongZhi.model.request.TeamJoinReq;
import com.wy.TongZhi.model.request.UpdateTeamReq;
import com.wy.TongZhi.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author huawei
*/
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTeam(AddRTeamReq teamReq) {
//      1. 请求参数是否为空？
        if (teamReq == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 如果队伍加密，密码不能为空且长度需要小于32位
        if (TeamStatusEnums.SECRET.getValue().equals(teamReq.getStatus())) {
            String password = teamReq.getPassword();
            if ( StringUtils.isBlank(password) && password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置错误");
            }
        }
//         6. 超时时间 > 当前时间
        Date expireTime = teamReq.getExpireTime();
        if (new Date().before(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间<当前时间");
        }
//         7. 校验用户最多创建 5 个队伍 todo 有 bug，可能同时创建 100 个队伍
        LambdaQueryWrapper<Team> teamWrapper = new LambdaQueryWrapper<>();
        teamWrapper.eq(Team::getUserId,teamReq.getUserId());
        if (this.count(teamWrapper) > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"每个用户最多创建五个队伍");
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamReq);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
//         8. 插入队伍信息到队伍表
        int save = this.baseMapper.insert(team);
        if (save != 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
//         9. 插入用户  => 队伍关系到关系表
        UserTeam userTeam  = new UserTeam();
        userTeam.setUserId(team.getUserId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        int insert = this.userTeamMapper.insert(userTeam);
        if (insert != 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return insert;
    }

    @Override
    public Page<TeamUserResp> getTeamListPage(QueryTeamReq queryTeamReq) {
        Long id = queryTeamReq.getId();
        List<Long> idList = queryTeamReq.getIdList();
        String searchText = queryTeamReq.getSearchText();
        String name = queryTeamReq.getName();
        String description = queryTeamReq.getDescription();
        Integer maxNum = queryTeamReq.getMaxNum();
        Long userId = queryTeamReq.getUserId();
        Integer status = queryTeamReq.getStatus();
        log.info(String.valueOf(queryTeamReq.getPageNo()));
        Page<Team> pageParam = new Page<>(queryTeamReq.getPageNo(), queryTeamReq.getPageSize());
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        if (id != null && id > 0) {
            wrapper.eq(Team::getId,id);
        }
        if (CollectionUtils.isNotEmpty(idList)) {
            wrapper.in(Team::getId,idList);
        }
        if (StringUtils.isNotBlank(searchText)) {
            wrapper.and(it-> it
                    .like(Team::getName,searchText)
                    .or()
                    .like(Team::getDescription,searchText));
        }
        // 组装队伍状态
        TeamStatusEnums teamStatus = TeamStatusEnums.getEnumByValue(status);
        if (teamStatus == null) {
            teamStatus =TeamStatusEnums.PUBLIC;
        }
        // 私有队伍
        if (teamStatus.equals(TeamStatusEnums.PRIVATE)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        wrapper.eq(StringUtils.isNotBlank(name), Team::getName, name)
                .eq(StringUtils.isNotBlank(description), Team::getDescription, description)
                .lt(maxNum != null && maxNum > 0, Team::getMaxNum, maxNum)
                .eq(userId != null && userId > 0, Team::getUserId, userId)
                .eq(Team::getStatus, teamStatus.getValue());
        // 不展示过期队伍
        wrapper.and(it->it
                .gt(Team::getExpireTime,new Date())
                .or()
                .isNull(Team::getExpireTime));
        Page<Team> teamPage = this.baseMapper.selectPage(pageParam, wrapper);
        List<Team> records = teamPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return new Page<>();
        }
        List<TeamUserResp> collect = records.stream().map(team -> {
            TeamUserResp resTeam = new TeamUserResp();
            try {
                BeanUtils.copyProperties(resTeam,team);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            User user = this.userMapper.selectById(team.getUserId());
            UserResp respUser = new UserResp();
            try {
                BeanUtils.copyProperties(respUser,user);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("BeanUtilsException",e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            resTeam.setCreateUser(respUser);
            resTeam.setHasJoinNum(0);
            resTeam.setHasJoin(false);
            return resTeam;
        }).collect(Collectors.toList());
        Page<TeamUserResp> resTeamPage = new Page<>();
        try {
            BeanUtils.copyProperties(resTeamPage,pageParam);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("BeanUtilsException",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        resTeamPage.setRecords(collect);
        return resTeamPage;
    }

    @Override
    public boolean updateTeam(UpdateTeamReq updateTeamReq) {
        Team byId = this.getById(updateTeamReq.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,updateTeamReq);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (byId.equals(team)) {
            return true;
        }
        return this.updateById(team);
    }

    @Override
    public int joinTeam(TeamJoinReq teamJoinReq, User loginUser) {
        if (teamJoinReq == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
        final long teamIdReq = teamJoinReq.getTeamId();
        // 检查队伍是否过期
        Team teamFromDb = this.getById(teamIdReq);
        if (teamFromDb.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍已过期");
        }
        // 队伍是否加密与密码是否正确
        String password = teamJoinReq.getPassword();
        TeamStatusEnums enumByValue = TeamStatusEnums.getEnumByValue(teamFromDb.getStatus());
        if (enumByValue.equals(TeamStatusEnums.SECRET) ) {
            if (StringUtils.isBlank(password) || !password.equals(teamJoinReq.getPassword())) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"加密队伍必须设置密码");
            }
        }
        RLock lock = redissonClient.getLock("tongZhi:join_team");
        try {
            while(true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    // 用户只能加入5个队伍
                    LambdaQueryWrapper<UserTeam> userTeamWrapper = new LambdaQueryWrapper<>();
                    userTeamWrapper.eq(UserTeam::getUserId,userId);
                    userTeamWrapper.eq(UserTeam::getTeamId,teamJoinReq);
                    long count = this.userTeamMapper.selectCount(userTeamWrapper);
                    if (count > 0) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户已加入该队伍");
                    }
                    // 判断队伍是否已满员
                    Integer maxNum = teamFromDb.getMaxNum();
                    userTeamWrapper.clear();
                    userTeamWrapper.eq(UserTeam::getTeamId,teamIdReq);
                    count = this.userTeamMapper.selectCount(userTeamWrapper);
                    if (maxNum < count) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍已满");
                    }
                    // 加入队伍
                    UserTeam userTeam = new UserTeam();
                    userTeam.setTeamId(teamIdReq);
                    userTeam.setUserId(userId);
                    userTeam.setJoinTime(new Date());
                    int insert = this.userTeamMapper.insert(userTeam);
                    if (insert != 1) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                    }
                    return insert;
                }
            }
        }catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                log.info("unLock: {} ",Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * @description 退出队伍
     * @param teamId 队伍id
     * @param logUser 登录用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(Long teamId, User logUser) {
        Team teamFromDB = this.baseMapper.selectById(teamId);
        if (teamFromDB == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍不存在");
        }
        final long leaderId = teamFromDB.getUserId(); // 队长id
        final long userId = logUser.getId();// 登录用户id
        // 是否加入了该队伍
        LambdaQueryWrapper<UserTeam> userTeamWrapper = new LambdaQueryWrapper<>();
        userTeamWrapper.eq(UserTeam::getUserId,userId).eq(UserTeam::getTeamId,teamId);
        Long count = this.userTeamMapper.selectCount(userTeamWrapper);
        if (count  == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未加该队伍");
        }
        userTeamWrapper.clear();
        userTeamWrapper.eq(UserTeam::getTeamId,teamId);
        Long teamHasUserNum = this.userTeamMapper.selectCount(userTeamWrapper);
        // 如果退出的是队长，转给最早加入的人
        if (teamFromDB.getUserId().equals(userId) && teamHasUserNum > 2) {
            userTeamWrapper.clear();
            userTeamWrapper.eq(UserTeam::getTeamId, teamId);
            userTeamWrapper.orderByAsc(UserTeam::getCreateTime).last("LIMIT 2");
            List<UserTeam> userTeams = this.userTeamMapper.selectList(userTeamWrapper);
            if (CollectionUtils.isEmpty(userTeams) || userTeams.size() <= 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            UserTeam userTeam = userTeams.get(1);
            Long nextLeaderId = userTeam.getUserId();
            teamFromDB.setUserId(nextLeaderId);
            int i = this.baseMapper.updateById(teamFromDB);
            if (i != 1) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            userTeamWrapper.clear();
            userTeamWrapper.eq(UserTeam::getUserId,leaderId).eq(UserTeam::getTeamId,teamId);
        } else {
            userTeamWrapper.eq(UserTeam::getUserId,userId).eq(UserTeam::getTeamId,teamHasUserNum);
        }
        int delete = this.userTeamMapper.delete(userTeamWrapper);
        if (delete != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeamById(Long teamId, User loginUser) {
        // 检查队伍是否存在
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getId,teamId);
        Team team = this.baseMapper.selectOne(wrapper);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"没有该队伍");
        }
        // 检查是否为队长
        if (!Objects.equals(loginUser.getId(), team.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 清除所有队员记录
        LambdaQueryWrapper<UserTeam> userTeamWrapper = new LambdaQueryWrapper<>();
        userTeamWrapper.eq(UserTeam::getTeamId,teamId);
        int delete = this.userTeamMapper.delete(userTeamWrapper);
        if (delete < 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    @Override
    public List<Long> myJoinTeam(Long id) {
        LambdaQueryWrapper<UserTeam> usrTeamWrapper = new LambdaQueryWrapper<>();
        usrTeamWrapper.eq(UserTeam::getUserId,id);
        List<UserTeam> userTeams = this.userTeamMapper.selectList(usrTeamWrapper);
        Map<Long, List<UserTeam>> collect = userTeams.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        return new ArrayList<>(collect.keySet());
    }

}




