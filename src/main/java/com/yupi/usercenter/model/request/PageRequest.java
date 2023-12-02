package com.yupi.usercenter.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-03  00:29
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 6342742337110962276L;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "1")
    private Integer pageNo;
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED,defaultValue = "10")
    private Integer pageSize;
}
