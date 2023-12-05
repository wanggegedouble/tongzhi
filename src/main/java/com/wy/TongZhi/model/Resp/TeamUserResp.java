package com.wy.TongZhi.model.Resp;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wy
 * @CreateTime: 2023-12-03  01:56
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class TeamUserResp implements Serializable {
    private static final long serialVersionUID = 4480027186650497427L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserResp createUser;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;

    /**
     * 是否已加入队伍
     */
    private  boolean hasJoin = false;
}
