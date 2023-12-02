package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.mapper.TeamMapper;
import com.yupi.usercenter.model.domain.Team;
import com.yupi.usercenter.model.request.AddRTeamReq;
import com.yupi.usercenter.service.TeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author huawei
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTeam(AddRTeamReq teamReq) {
        Team team = new Team();
        BeanUtils.copyProperties(teamReq,team);
        int insert = this.baseMapper.insert(team);
        if (insert != 1) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return insert;
    }
}




