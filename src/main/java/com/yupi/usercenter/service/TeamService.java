package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.model.Resp.TeamUserResp;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.request.AddRTeamReq;
import com.yupi.usercenter.model.request.QueryTeamReq;

/**
* @author huawei
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-12-01 06:21:55
*/
public interface TeamService extends IService<Team> {

    int addTeam(AddRTeamReq teamReq);

    Page<TeamUserResp> getTeamListPage(QueryTeamReq queryTeamReq);
}
