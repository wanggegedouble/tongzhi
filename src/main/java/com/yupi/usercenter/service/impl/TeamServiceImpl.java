package com.yupi.usercenter.service.impl;
import com.yupi.usercenter.mapper.UserMapper;
import com.yupi.usercenter.model.Resp.UserResp;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.contant.TeamStatusEnums;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.mapper.UserTeamMapper;
import com.yupi.usercenter.model.Resp.TeamPage;
import com.yupi.usercenter.model.Resp.TeamUserResp;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.domain.User;
import com.yupi.usercenter.model.domain.UserTeam;
import com.yupi.usercenter.model.request.AddRTeamReq;
import com.yupi.usercenter.model.request.QueryTeamReq;
import com.yupi.usercenter.service.TeamService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.stream.Collectors;

/**
* @author huawei
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Resource
    private UserTeamMapper userTeamMapper;
    @Resource
    private UserMapper userMapper;

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
        Page<Team> pageParam = new Page<>(queryTeamReq.getPageNo(), queryTeamReq.getPageSize());
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        if (id != null && id > 0) {
            wrapper.eq(Team::getId,id);
        }
        if (CollectionUtils.isNotEmpty(idList)) {
            wrapper.eq(Team::getId,idList);
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
                BeanUtils.copyProperties(team,resTeam);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            User user = this.userMapper.selectById(team.getUserId());
            UserResp respUser = new UserResp();
            try {
                BeanUtils.copyProperties(user,respUser);
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
            BeanUtils.copyProperties(pageParam,resTeamPage);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("BeanUtilsException",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        resTeamPage.setRecords(collect);
        return resTeamPage;
    }
}




