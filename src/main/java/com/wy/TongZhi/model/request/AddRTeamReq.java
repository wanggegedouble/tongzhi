package com.wy.TongZhi.model.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wy
 * @CreateTime: 2023-12-01  06:39
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class AddRTeamReq implements Serializable {

    private static final long serialVersionUID = -4631215945507671186L;
    /**
     * 队伍名称
     */
    @NotNull(message = "空字符串不能为")
    @Length(message = "队伍名称不能超过20个字",max = 20)
    private String name;

    /**
     * 描述
     */
    @NotNull(message = "队伍描述不能为空")
    @Length(message = "描述长度不能超过500个字",max = 500)
    private String description;

    /**
     * 最大人数
     */
    @NotNull
    @Max(message = "最大人数",value = 20)
    @Min(message = "最小人数",value = 1)
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
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */

    private String password;

}
