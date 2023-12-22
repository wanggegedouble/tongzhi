package com.wy.TongZhi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wy.TongZhi.common.ErrorCode;
import com.wy.TongZhi.exception.BusinessException;
import com.wy.TongZhi.model.domain.User;
import com.wy.TongZhi.service.UserService;
import com.wy.TongZhi.mapper.UserMapper;
import com.wy.TongZhi.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wy.TongZhi.contant.UserConstant.*;

/**
 * 用户服务实现类

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 6) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException("密码或账号错误",400,null);
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> searchUserByTags(List<String> tagList) {
        List<User> users = this.userMapper.selectList(Wrappers.<User>lambdaQuery().eq(User::getUserStatus, USER_ENABLE));
        Gson gson = new Gson();
        return users.stream().filter(user -> {
            String tagJson = user.getTags();
            Set<String> tempTageNameSet = gson.fromJson(tagJson,new TypeToken<Set<String>>(){}.getType());
            tempTageNameSet = Optional.ofNullable(tempTageNameSet).orElse(new HashSet<>());
            for (String tagName : tagList) {
                if (!tempTageNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }
    /**
     @Override
     public List<User> searchUserByTags(List<String> tagList) {
     System.out.println("++++++++++++++++++++");
     System.out.println(tagList);
     LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
     for (String tag : tagList) {
     log.info("tag:{}",tag);
     wrapper.like(User::getTags,tag).or();
     }
     List<User> users = this.userMapper.selectList(wrapper);
     return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
     }
     **/

    @Override
    public int updateUser(User user, HttpServletRequest request) {
        User loginUser = this.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 管理员可以修改所有人
        // 其他用户只能修改自己
        if (!isAdmin(loginUser) && user.getId().longValue() != loginUser.getId().longValue()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = this.userMapper.selectById(user.getId());
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return this.userMapper.updateById(user);
    }

    /**
     *  获取当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object user = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) user;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public Page<User> commendsUsers(Integer pageNo, Integer pageSize, HttpServletRequest request) {
        Long userId = this.getLoginUser(request).getId();
        Gson gson = new Gson();
        String redisKey = String.format("yupao:user:commendsUsers:%s", userId);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String redisJson = (String) opsForValue.get(redisKey);
        Page<User> comendsUsersPage = gson.fromJson(redisJson, new TypeToken<Page<User>>() {}.getType());
        if (comendsUsersPage != null) {
            log.info("from redis");
            List<User> records = comendsUsersPage.getRecords();
            log.info(records.toString());
            return comendsUsersPage;
        }
        Page<User> userPage = this.userMapper.selectPage(new Page<>(pageNo, pageSize), null);
        List<User> records = userPage.getRecords();
        List<User> collect = records.stream().map(this::getSafetyUser).collect(Collectors.toList());
        Page<User> safeUserPage = userPage.setRecords(collect);
        try {
            opsForValue.set(redisKey,gson.toJson(safeUserPage),120, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis set key error");
        }
        return safeUserPage;
    }

    @Override
    public List<User> matcherUsers(long numUser, User loginUser) throws JsonProcessingException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(User::getId,User::getTags);
        wrapper.isNotNull(User::getTags);
        // 查询所有数据库所有用户的标签数据
        List<User> users = this.baseMapper.selectList(wrapper);
        String tags = loginUser.getTags();
        log.info("logUserTags :{}",tags);
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> loginUserTagList = objectMapper.readValue(tags, new TypeReference<>() {});
        List<Pair<User,Long>> list = new ArrayList<>();
        // 循环与当前用户进行比较
        for (User user : users) {
            String userTagJson = user.getTags();
            if (StringUtils.isBlank(userTagJson) || Objects.equals(user.getId(), loginUser.getId())) {
                continue;
            }
            List<String> userTagList = objectMapper.readValue(userTagJson, new TypeReference<>() {});
            // 按照算法，推荐排序
            long score = AlgorithmUtils.minDistance(loginUserTagList,userTagList);
            log.info("user{}",user);
            list.add(new Pair<>(user,score));
        }
        log.info("list.size: {}",list.size());
        // 排序后idList
        List<Long> idList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(numUser)
                .map(pair->pair.getKey().getId())
                .collect(Collectors.toList());
        log.info("idList:{}",idList);
        wrapper.clear();
        wrapper.in(User::getId,idList);
        Map<Long, List<User>> userIdListMap = this.baseMapper.selectList(wrapper)
                .stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long id : idList) {
            finalUserList.add(userIdListMap.get(id).get(0));
        }
        return finalUserList;
    }
}

