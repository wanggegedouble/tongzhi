package com.wy.TongZhi.controller;
import java.util.List;
import java.util.Optional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.wy.TongZhi.common.BaseResponse;
import com.wy.TongZhi.common.ErrorCode;
import com.wy.TongZhi.common.ResultUtils;
import com.wy.TongZhi.exception.BusinessException;
import com.wy.TongZhi.model.Resp.TeamUserResp;
import com.wy.TongZhi.model.domain.Team;
import com.wy.TongZhi.model.domain.User;
import com.wy.TongZhi.model.request.*;
import com.wy.TongZhi.service.TeamService;
import com.wy.TongZhi.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: wy
 * @CreateTime: 2023-12-01  06:02
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/team")
@Slf4j
@Validated
@Api("team")
public class TeamController {

    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<?> addTeam(@RequestBody @Valid AddRTeamReq teamReq) {
        log.info("team : {}",teamReq);
        int i = this.teamService.addTeam(teamReq);
        return ResultUtils.success((long) i);
    }

    @DeleteMapping("/delete/{teamId}")
    public BaseResponse<Boolean> deleteTeam(@PathVariable @NotNull(message = "不能为空") Long teamId,HttpServletRequest request) {
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = this.userService.getLoginUser(request);
        boolean b = this.teamService.deleteTeamById(teamId,loginUser);
        return ResultUtils.success(b);
    }

    @PutMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody @Valid UpdateTeamReq updateTeamReq) {
        boolean isSuccess = this.teamService.updateTeam(updateTeamReq);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/PageList")
    @ApiOperation("分页查询")
    public BaseResponse<Page<TeamUserResp>> list(QueryTeamReq queryTeamReq) {
        if (queryTeamReq == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<TeamUserResp> resp = this.teamService.getTeamListPage(queryTeamReq);
        return ResultUtils.success(resp);
    }

    @GetMapping("/getTeamByName/{teamName}")
    public BaseResponse<Team> getTeamByName(@PathVariable @NotBlank(message = "teamName 不能为空") String teamName) {
        Team team = this.teamService.getOne(Wrappers.<Team>lambdaQuery().eq(Team::getName, teamName));
        if (Optional.ofNullable(team).isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @PostMapping("/joinTeam")
    public BaseResponse<String> joinTeam(@RequestBody @NotNull TeamJoinReq teamJoinReq, HttpServletRequest request) {
        User loginUser = this.userService.getLoginUser(request);
        this.teamService.joinTeam(teamJoinReq,loginUser);
        return ResultUtils.success("加入成功");
    }

    @PostMapping("/quitTeam/{teamId}")
    public BaseResponse<String> quitTeam(@PathVariable Long teamId,HttpServletRequest request) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = this.userService.getLoginUser(request);
        boolean isSuccess = this.teamService.quitTeam(teamId,loginUser);
        return ResultUtils.success("退出成功");
    }

    /**
     *  获取我创建的队伍
     */
    @GetMapping("/list/my/create")
    public BaseResponse<Page<TeamUserResp>> listMyCreate(QueryTeamReq queryTeamReq,HttpServletRequest request) {
        User loginUser = this.userService.getLoginUser(request);
        queryTeamReq.setUserId(loginUser.getId());
        return this.list(queryTeamReq);
    }

    /**
     *  获取我加入的队伍
     */
    @GetMapping("/list/my/join")
    public BaseResponse<Page<TeamUserResp>> listMyJoin(QueryTeamReq queryTeamReq,HttpServletRequest request) {
        User loginUser = this.userService.getLoginUser(request);
        List<Long> idList = this.teamService.myJoinTeam(loginUser.getId());
        queryTeamReq.setIdList(idList);
        return this.list(queryTeamReq);
    }
}

