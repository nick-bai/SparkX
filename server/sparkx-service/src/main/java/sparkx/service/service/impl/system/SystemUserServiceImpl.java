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

import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkx.common.core.PageResult;
import sparkx.common.enums.StatusEnum;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.system.SystemTeamEntity;
import sparkx.service.entity.system.SystemTeamUserEntity;
import sparkx.service.entity.system.SystemUsersEntity;
import sparkx.service.helper.LicenseHelper;
import sparkx.service.mapper.system.SystemTeamMapper;
import sparkx.service.mapper.system.SystemTeamUserMapper;
import sparkx.service.mapper.system.SystemUserMapper;
import sparkx.service.service.interfaces.system.ISystemUserService;
import sparkx.service.validate.system.UserValidate;
import sparkx.service.vo.system.UserQueryVo;
import sparkx.service.vo.system.UsersVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class SystemUserServiceImpl implements ISystemUserService {

    @Autowired
    SystemUserMapper userMapper;

    @Autowired
    SystemTeamMapper systemTeamMapper;

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    LicenseHelper licenseHelper;

    /**
     * 获取用户列表
     * @return List<KnowledgeUsersEntity>
     */
    @Override
    public PageResult<UsersVo> getUserList(UserQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<SystemUsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", StatusEnum.YES.getCode());

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("nickname", queryVo.getName());
        }

        if (queryVo.getStatus() != null && queryVo.getStatus() > 0) {
            queryWrapper.eq("status", queryVo.getStatus());
        }

        queryWrapper.orderByDesc("create_time");

        IPage<SystemUsersEntity> userListRes = userMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<UsersVo> usersList = new LinkedList<>();

        for (SystemUsersEntity entity : userListRes.getRecords()) {
            UsersVo vo = new UsersVo();
            BeanUtils.copyProperties(entity, vo);

            usersList.add(vo);
        }

        return PageResult.iPageHandle(userListRes.getTotal(), pageNo, pageSize, usersList);
    }

    /**
     * 添加用户
     * @param validate UserValidate
     */
    @Override
    @Transactional
    public void addUser(UserValidate validate) {

        if (validate.getPassword().isBlank()) {
            throw new BusinessException("密码不能为空");
        }

        int pwdLength = validate.getPassword().length();
        if (pwdLength < 6 || pwdLength > 15) {
            throw new BusinessException("密码长度应该在6~15位");
        }

        // 检测账号
        QueryWrapper<SystemUsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", validate.getName());
        SystemUsersEntity userRes = userMapper.selectOne(queryWrapper);
        if (userRes != null) {
            throw new BusinessException("该账号已经被使用");
        }

        // 查看授权信息
        long totalUser = userMapper.selectCount(new QueryWrapper<>());
        if (totalUser >= licenseHelper.getUserNum()) {
            throw new BusinessException(403, "社区版本最多可添加" + licenseHelper.getUserNum() + "名用户，如需更多用户，请购买授权版本！");
        }

        SystemUsersEntity usersEntity = new SystemUsersEntity();
        usersEntity.setUserId(IdUtil.randomUUID());
        usersEntity.setName(validate.getName());
        usersEntity.setNickname(validate.getNickname());
        usersEntity.setAvatar(validate.getAvatar());
        usersEntity.setStatus(validate.getStatus());
        String salt = ObjectId.next();
        usersEntity.setPassword(Tool.makePassword(validate.getPassword(), salt));
        usersEntity.setSalt(salt);
        usersEntity.setCreateTime(Tool.nowDateTime());
        userMapper.insert(usersEntity);

        // 建立属于这个用户的工作团队，并让该用户加入
        SystemTeamEntity teamEntity = new SystemTeamEntity();
        teamEntity.setTeamCode(IdUtil.simpleUUID());
        teamEntity.setUserId(usersEntity.getUserId());
        teamEntity.setCreateTime(Tool.nowDateTime());
        systemTeamMapper.insert(teamEntity);

        SystemTeamUserEntity teamUserEntity = new SystemTeamUserEntity();
        teamUserEntity.setTeamId(teamEntity.getTeamId());
        teamUserEntity.setUserId(usersEntity.getUserId());
        teamUserEntity.setCreateTime(Tool.nowDateTime());
        systemTeamUserMapper.insert(teamUserEntity);
    }

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    @Override
    public void editUser(UserValidate validate) {

        SystemUsersEntity usersEntity = userMapper.selectById(validate.getUserId());
        String password = usersEntity.getPassword();
        BeanUtils.copyProperties(validate, usersEntity);

        // 检测账号
        QueryWrapper<SystemUsersEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", validate.getName());
        queryWrapper.ne("user_id", validate.getUserId());
        SystemUsersEntity userRes = userMapper.selectOne(queryWrapper);
        if (userRes != null) {
            throw new BusinessException("该账号已经被使用");
        }

        // 重置密码
        if (!validate.getPassword().isBlank()) {

            int pwdLength = validate.getPassword().length();
            if (pwdLength < 6 || pwdLength > 15) {
                throw new BusinessException("密码长度应该在6~15位");
            }

            String salt = ObjectId.next();
            usersEntity.setPassword(Tool.makePassword(validate.getPassword(), salt));
            usersEntity.setSalt(salt);
        } else {
            usersEntity.setPassword(password);
        }

        usersEntity.setUpdateTime(Tool.nowDateTime());

        userMapper.update(usersEntity, new QueryWrapper<SystemUsersEntity>().eq("user_id", validate.getUserId()));
    }

    /**
     * 删除用户
     * @param userId String
     */
    @Override
    public void delUser(String userId) {

        SystemUsersEntity usersEntity = userMapper.selectById(userId);
        usersEntity.setUserId(userId);
        usersEntity.setDeleted(StatusEnum.NO.getCode());

        userMapper.update(usersEntity, new QueryWrapper<SystemUsersEntity>().eq("user_id", userId));
    }
}
