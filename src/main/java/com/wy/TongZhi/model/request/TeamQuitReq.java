package com.wy.TongZhi.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wy
 * @CreateTime: 2023-12-05  01:39
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class TeamQuitReq implements Serializable {
    private static final long serialVersionUID = -3601039506186052333L;

    private Long teamId;
    private Long userId;
}
