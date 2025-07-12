// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.system;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkx.common.enums.StatusEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.system.SystemTeamUserEntity;
import sparkx.service.entity.system.SystemUsersEntity;
import sparkx.service.helper.UserContextHelper;
import sparkx.service.mapper.system.SystemTeamUserMapper;
import sparkx.service.mapper.system.SystemUserMapper;
import sparkx.service.service.interfaces.system.ITeamService;
import sparkx.service.validate.system.AddTeamUserValidate;
import sparkx.service.validate.system.PermissionValidate;
import sparkx.service.vo.system.LocalUserVo;
import sparkx.service.vo.system.TeamUserVo;
import sparkx.service.vo.system.UsersVo;

import java.util.*;

@Service
public class TeamImpl implements ITeamService {

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    SystemUserMapper systemUserMapper;

    /**
     * 获取团队成员
     * @return List<TeamUserVo>
     */
    @Override
    public List<TeamUserVo> getTeamUserList() {

        LocalUserVo localUser = UserContextHelper.getUser();
        List<SystemTeamUserEntity> userList = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().eq("team_id", localUser.getTeamId()));

        List<TeamUserVo> teamUserList = new LinkedList<>();
        for (SystemTeamUserEntity teamUserEntity : userList) {

            TeamUserVo teamUserVo = new TeamUserVo();
            teamUserVo.setTeamId(teamUserEntity.getTeamId());
            teamUserVo.setUserId(teamUserEntity.getUserId());

            SystemUsersEntity userInfo = systemUserMapper.selectOne(new QueryWrapper<SystemUsersEntity>()
                    .eq("user_id", teamUserEntity.getUserId()));
            teamUserVo.setName(userInfo.getNickname());

            teamUserVo.setIsAdmin(localUser.getUserId().equals(teamUserEntity.getUserId())
                    ? StatusEnum.YES.getCode() : StatusEnum.NO.getCode());
            teamUserVo.setAppPermission(teamUserEntity.getAppPermission());
            teamUserVo.setDatasetPermission(teamUserEntity.getDatasetPermission());

            teamUserList.add(teamUserVo);
        }

        return teamUserList;
    }

    /**
     * 搜索用户
     * @param nickname String
     * @return List<UsersVo>
     */
    @Override
    public List<UsersVo> searchUser(String nickname) {

        if (nickname.isBlank()) {
            return new LinkedList<>();
        }

        List<SystemUsersEntity> userList = systemUserMapper.selectList(new QueryWrapper<SystemUsersEntity>()
                .like("nickname", nickname).eq("status", StatusEnum.YES.getCode()).eq("deleted", StatusEnum.YES.getCode()));

        List<UsersVo> returnList = new LinkedList<>();
        for (SystemUsersEntity user : userList) {
            UsersVo userVo = new UsersVo();
            BeanUtils.copyProperties(user, userVo);

            returnList.add(userVo);
        }

        return returnList;
    }

    /**
     * 添加团队成员
     * @param validate AddTeamUserValidate
     * @return String
     */
    @Override
    @Transactional
    public String addUser(AddTeamUserValidate validate) {

        if (validate.getUserIds().isBlank()) {
            throw new BusinessException("添加的用户不能为空");
        }

        List<String> addUserIds = Arrays.asList(validate.getUserIds().split(","));

        LocalUserVo localUser = UserContextHelper.getUser();
        for (String userId : addUserIds) {

            SystemTeamUserEntity teamUserEntity = new SystemTeamUserEntity();
            teamUserEntity.setTeamId(localUser.getTeamId());
            teamUserEntity.setUserId(userId);
            teamUserEntity.setCreateTime(Tool.nowDateTime());

            systemTeamUserMapper.insert(teamUserEntity);
        }

        return addUserIds.get(0);
    }

    /**
     * 删除团队用户
     * @param userId String
     */
    @Override
    public void delUser(String userId) {
        if (userId.isBlank()) {
            throw new BusinessException("删除的用户不能为空");
        }

        LocalUserVo localUser = UserContextHelper.getUser();

        systemTeamUserMapper.delete(new QueryWrapper<SystemTeamUserEntity>()
                .eq("user_id", userId).eq("team_id", localUser.getTeamId()));
    }

    /**
     * 更新用户权限
     * @param validate PermissionValidate
     */
    @Override
    public void updateUserPermission(PermissionValidate validate) {

        LocalUserVo localUser = UserContextHelper.getUser();

        Map<String, List<String>> permissionData = new HashMap<>();
        List<String> manageList = new ArrayList<>();
        List<String> viewList = new ArrayList<>();

        validate.getPermissionData().forEach(item -> {

            if (item.getManage()) {
                manageList.add(item.getId());
            }

            if (item.getView()) {
                viewList.add(item.getId());
            }
        });

        permissionData.put("manage", manageList);
        permissionData.put("view", viewList);

        SystemTeamUserEntity teamUserInfo = systemTeamUserMapper.selectOne(
                new QueryWrapper<SystemTeamUserEntity>().eq("user_id", validate.getUserId())
                        .eq("team_id", localUser.getTeamId()));

        if (validate.getType().equals("1")) {
            teamUserInfo.setDatasetPermission(JSONUtil.toJsonStr(permissionData));
        } else {
            teamUserInfo.setAppPermission(JSONUtil.toJsonStr(permissionData));
        }

        teamUserInfo.setUpdateTime(Tool.nowDateTime());
        systemTeamUserMapper.updateById(teamUserInfo);
    }
}