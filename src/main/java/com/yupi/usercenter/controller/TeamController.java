package com.yupi.usercenter.controller;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.usercenter.common.BaseResponse;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.common.ResultUtils;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.model.Resp.TeamPage;
import com.yupi.usercenter.model.Resp.TeamUserResp;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.request.AddRTeamReq;
import com.yupi.usercenter.model.request.QueryTeamReq;
import com.yupi.usercenter.model.request.UpdateTeamReq;
import com.yupi.usercenter.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
@Tag(name = "team")
@Slf4j
@Validated
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    @Operation(summary = "添加队伍")
    public BaseResponse<?> addTeam(@RequestBody @Valid AddRTeamReq teamReq) {
        log.info("team : {}",teamReq);
        int i = this.teamService.addTeam(teamReq);
        return ResultUtils.success((long) i);
    }

    @DeleteMapping("/delete/{teamId}")
    @Operation(summary = "删除队伍")
    public BaseResponse<Boolean> deleteTeam(@PathVariable @NotNull(message = "不能为空") Long teamId) {
        boolean b = this.teamService.removeById(teamId);
        return ResultUtils.success(b);
    }

    @PutMapping("/update")
    @Operation(summary = "更新队伍")
    public BaseResponse<Boolean> updateTeam(@RequestBody @Valid UpdateTeamReq updateTeamReq) {
        Team team = new Team();
        try {
            BeanUtils.copyProperties(updateTeamReq,team);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("IllegalAccessException | InvocationTargetException",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        boolean b = this.teamService.updateById(team);
        if (!b) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR);
        }
        return ResultUtils.success(b);
    }

    @GetMapping("/PageList")
    @Operation(summary = "分页查询")
    public BaseResponse<Page<TeamUserResp>> list(@RequestBody QueryTeamReq queryTeamReq) {
        Page<Team> page = this.teamService.page(new Page<>(queryTeamReq.getPageNo(), queryTeamReq.getPageSize()), null);
        List<Team> records = page.getRecords();
        if (records.isEmpty()) {
            return ResultUtils.success(new Page<>());
        }
        List<TeamPage> collect = records.stream().map(team -> {
            TeamPage teamPage = new TeamPage();
            try {
                BeanUtils.copyProperties(team, teamPage);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("IllegalAccessException | InvocationTargetException",e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
            return teamPage;
        }).collect(Collectors.toList());
        Page<TeamPage> resPage = new Page<>();
        try {
            BeanUtils.copyProperties(page,resPage);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("IllegalAccessException | InvocationTargetException",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        resPage.setRecords(collect);
        Page<TeamUserResp> resp = this.teamService.getTeamListPage(queryTeamReq);
        return ResultUtils.success(resp);
    }

    @Operation(summary = "根据队伍名称查询")
    @GetMapping("/getTeamByName/{teamName}")
    public BaseResponse<Team> getTeamByName(@PathVariable @NotBlank(message = "teamName 不能为空") String teamName) {
        Team team = this.teamService.getOne(Wrappers.<Team>lambdaQuery().eq(Team::getName, teamName));
        if (Optional.ofNullable(team).isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }
}
