package com.wy.TongZhi.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-04  20:47
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class TeamJoinReq implements Serializable {
    private static final long serialVersionUID = -1420405802088420838L;

    private Long teamId;
    private String password;

}
