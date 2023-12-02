package com.yupi.usercenter.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wy
 * @CreateTime: 2023-12-03  00:25
 * @Description: TODO
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryTeamReq extends PageRequest implements Serializable {
    private static final long serialVersionUID = 6104689894159770694L;

    /**
     * id
     */
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;

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
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
