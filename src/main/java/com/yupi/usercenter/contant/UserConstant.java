package com.yupi.usercenter.contant;

/**
 * 用户常量
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    //  ------- 权限 --------

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;


    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    // 用户状态 正常
    int USER_ENABLE = 0;
    // 用户状态 被禁止
    int USER_DISENABLE = 1;

}
