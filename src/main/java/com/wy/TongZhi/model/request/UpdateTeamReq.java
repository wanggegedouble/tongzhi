package com.wy.TongZhi.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wy
 * @CreateTime: 2023-12-01  06:39
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UpdateTeamReq implements Serializable {

    private static final long serialVersionUID = 5586033264285218724L;

    @NotNull(message = "不能为空")
    private Long id;
    /**
     * 队伍名称
     */
    @NotBlank(message = "队伍名称不能为null")
    @NotNull(message = "不能为空字符串")
    private String name;

    /**
     * 描述
     */
    @NotBlank(message = "队伍描述不能为空")
    @NotNull(message = "不能为空字符串")
    private String description;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    private Date expireTime;

    /**
     * 用户id(队长)
     */
    @NotNull(message = "队长id，不能为空")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;

}
