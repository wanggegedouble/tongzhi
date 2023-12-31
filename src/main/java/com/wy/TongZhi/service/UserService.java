package com.wy.TongZhi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wy.TongZhi.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务

 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 通过标签查询相同爱好用户
     */
    List<User> searchUserByTags(List<String> tagList);

    int updateUser(User user, HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     *  是否是admin
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否是admin
     */
    boolean isAdmin(User user);

    Page<User> commendsUsers(Integer pageNo, Integer pageSize, HttpServletRequest request);

    List<User> matcherUsers(long userNum,User loginUser) throws JsonProcessingException;

}
