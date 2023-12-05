package com.wy.TongZhi.model.request;

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
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
