package com.wy.TongZhi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wy.TongZhi.model.Resp.TeamUserResp;
import com.wy.TongZhi.model.domain.Team;
import com.wy.TongZhi.model.domain.User;
import com.wy.TongZhi.model.request.AddRTeamReq;
import com.wy.TongZhi.model.request.QueryTeamReq;
import com.wy.TongZhi.model.request.TeamJoinReq;
import com.wy.TongZhi.model.request.UpdateTeamReq;

import java.util.List;

/**
* @author huawei
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-12-01 06:21:55
*/
public interface TeamService extends IService<Team> {

    int addTeam(AddRTeamReq teamReq);

    Page<TeamUserResp> getTeamListPage(QueryTeamReq queryTeamReq);

    boolean updateTeam(UpdateTeamReq updateTeamReq);

    void joinTeam(TeamJoinReq teamJoinReq, User loginUser);

    boolean quitTeam(Long teamId, User logUser);

    boolean deleteTeamById(Long teamId, User loginUser);

    List<Long> myJoinTeam(Long id);
}
