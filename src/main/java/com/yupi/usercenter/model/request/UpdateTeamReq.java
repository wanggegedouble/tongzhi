package com.yupi.usercenter.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "队伍id")
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
     * 最大人数
     */
    @Min(message = "最少人数不能低于1", value = 1L)
    @Max(message = "最少人数不能高于50",value = 50)
    @NotNull
    private Integer maxNum;

    /**
     * 过期时间
     */
    @NotNull(message = "过期时间不能为空")
    private Date expireTime;

    /**
     * 用户id(队长)
     */
    @NotNull(message = "队长id，不能为空")
    @Schema(name = "队长id")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @Schema(name = "队伍状态（",description = "0 - 公开，1 - 私有，2 - 加密",requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "0")
    private Integer status;

    /**
     * 密码
     */
    @NotNull(message = "密码不能为空")
    private String password;

}
