// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.application;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.model.output.TokenUsage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sparkx.common.constant.SparkXConstant;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationChatLogEntity;
import sparkx.service.entity.application.ApplicationChatSessionEntity;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.system.SystemTeamUserEntity;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.mapper.application.ApplicationChatLogMapper;
import sparkx.service.mapper.application.ApplicationChatSessionMapper;
import sparkx.service.mapper.application.ApplicationMapper;
import sparkx.service.mapper.system.SystemTeamUserMapper;
import sparkx.service.service.interfaces.application.IApplicationChatService;
import sparkx.service.vo.application.*;
import sparkx.service.vo.system.LocalUserVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class ApplicationChatServiceImpl implements IApplicationChatService {

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    ApplicationChatSessionMapper applicationChatSessionMapper;

    @Autowired
    ApplicationChatLogMapper applicationChatLogMapper;

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 获取应用信息
     * @param chatInfoVo ChatInfoVo
     * @return ApplicationSimpleVo
     */
    @Override
    public ApplicationSimpleVo getChatInfo(ChatInfoVo chatInfoVo) {

        // 设置应用信息
        ApplicationEntity application = applicationMapper.selectOne(
                new QueryWrapper<ApplicationEntity>().eq("access_token", chatInfoVo.getAccessToken()));
        if (application.getStatus().equals(1) && !chatInfoVo.getDebug()) {
            throw new BusinessException("该应用尚未发布");
        }

        // 如果开启debug，判断当前用户是否有权限
        LocalUserVo userData = applicationHelper.getUserData();
        if (chatInfoVo.getDebug() && !application.getUserId().equals(userData.getUserId())) {
            SystemTeamUserEntity teamUser = systemTeamUserMapper.selectOne(
                    new QueryWrapper<SystemTeamUserEntity>().eq("user_id", userData.getUserId()));
            if (teamUser == null) {
                throw new BusinessException("登录过期");
            }

            PermissionVo permissionVo = JSONUtil.toBean(teamUser.getAppPermission(), PermissionVo.class);
            if (!permissionVo.getView().contains(application.getAppId())) {
                throw new BusinessException("您无权调试该应用");
            }
        }

        ApplicationSimpleVo applicationVo = new ApplicationSimpleVo();
        BeanUtils.copyProperties(application, applicationVo);

        return applicationVo;
    }

    /**
     * 获取会话记录
     * @param chatInfoVo ChatInfoVo
     * @return List<ApplicationSimpleSessionVo>
     */
    @Override
    public List<ApplicationSimpleSessionVo> getChatSesstionList(ChatInfoVo chatInfoVo) {

        LocalUserVo userData = applicationHelper.getUserData();

        // 设置应用信息
        ApplicationEntity application = applicationMapper.selectOne(
                new QueryWrapper<ApplicationEntity>().eq("access_token", chatInfoVo.getAccessToken()));
        if (application.getStatus().equals(1) && !chatInfoVo.getDebug()) {
            throw new BusinessException("该应用尚未发布");
        }

        // 设置会话信息
        List<ApplicationChatSessionEntity> sessionList = applicationChatSessionMapper
                .selectList(new QueryWrapper<ApplicationChatSessionEntity>()
                        .eq("user_id", userData.getUserId())
                        .eq("app_id", application.getAppId()).orderByDesc("create_time").last("LIMIT 20"));
        List<ApplicationSimpleSessionVo> sessionVoList = new LinkedList<>();
        for (ApplicationChatSessionEntity entity : sessionList) {
            ApplicationSimpleSessionVo vo = new ApplicationSimpleSessionVo();
            BeanUtils.copyProperties(entity, vo);

            sessionVoList.add(vo);
        }

        return sessionVoList;
    }

    /**
     * 创建会话
     * @param sessionVo SessionVo
     * @return String
     */
    @Override
    public String createSession(SessionVo sessionVo) {

        LocalUserVo userData = applicationHelper.getUserData();
        ApplicationChatSessionEntity entity = new ApplicationChatSessionEntity();
        entity.setAppId(sessionVo.getAppId());
        entity.setSessionId(IdUtil.randomUUID());
        entity.setUserId(userData.getUserId());
        entity.setCreateTime(Tool.nowDateTime());

        applicationChatSessionMapper.insert(entity);

        return entity.getSessionId();
    }

    /**
     * 更新会话
     * @param sessionVo SessionVo
     */
    @Override
    public void updateSession(SessionVo sessionVo) {

        ApplicationChatSessionEntity entity = applicationChatSessionMapper.selectById(sessionVo.getSessionId());

        String title = sessionVo.getTitle();
        if (title.length() > SparkXConstant.CommonData.defaultLen) {
            title = title.substring(0, SparkXConstant.CommonData.defaultLen);
        }
        entity.setTitle(title);
        entity.setUpdateTime(Tool.nowDateTime());

        applicationChatSessionMapper.updateById(entity);
    }

    /**
     * 记录对话日志
     * @param logVo ApplicationLogVo
     */
    @Override
    public Integer writeLog(ApplicationLogVo logVo) {

        LocalUserVo userData = applicationHelper.getUserData();

        ApplicationChatLogEntity entity = new ApplicationChatLogEntity();
        entity.setAppId(logVo.getAppId());
        entity.setUserId(userData.getUserId());
        entity.setSessionId(logVo.getSessionId());
        entity.setQuestion(logVo.getQuestion());
        entity.setContent(JSONUtil.toJsonStr(logVo.getAnswer()));
        entity.setTime(logVo.getTime());
        entity.setTokens(logVo.getTotalTokens());
        entity.setRetrievedList(logVo.getRetrievedList());
        entity.setToolUse(logVo.getToolUse());
        entity.setCreateTime(Tool.nowDateTime());

        applicationChatLogMapper.insert(entity);

        // 应用信息
        ApplicationEntity appInfo = applicationMapper.selectById(logVo.getAppId());
        // 记录token使用情况
        TokenUsage tokenUsage = new TokenUsage(logVo.getInputTokens(), logVo.getOutputTokens(), logVo.getTotalTokens());
        if (appInfo.getType().equals(1)) {
            applicationHelper.writeTokenLog("chat", appInfo.getModelName(), tokenUsage);
        } else {
            applicationHelper.writeTokenLog("chat", "APPID:" + appInfo.getAppId(), tokenUsage);
        }

        return entity.getLogId();
    }

    /**
     * 评价回答
     * @param appraiseVo AppraiseVo
     */
    @Override
    public void appraise(AppraiseVo appraiseVo) {

        ApplicationChatLogEntity entity = applicationChatLogMapper.selectOne(
                new QueryWrapper<ApplicationChatLogEntity>()
                        .eq("log_id", appraiseVo.getLogId()).eq("session_id", appraiseVo.getSessionId()));
        entity.setAppraise(appraiseVo.getAppraise());
        entity.setUpdateTime(Tool.nowDateTime());

        applicationChatLogMapper.updateById(entity);
    }

    /**
     * 删除会话
     * @param sessionId String
     */
    @Override
    @Transactional
    public void delSession(String sessionId) {

        LocalUserVo userData = applicationHelper.getUserData();
        int num = applicationChatSessionMapper.delete(new QueryWrapper<ApplicationChatSessionEntity>()
                .eq("session_id", sessionId).eq("user_id", userData.getUserId()));

        if (num > 0) {
            applicationChatLogMapper.delete(new QueryWrapper<ApplicationChatLogEntity>().eq("session_id", sessionId));
        }
    }

    /**
     * 获取聊天记录
     * @param sessionId String
     * @return List<ApplicationLogVo>
     */
    @Override
    public List<ApplicationLogVo> getChatLog(String sessionId) {

        LocalUserVo userData = applicationHelper.getUserData();
        ApplicationChatSessionEntity sessionInfo = applicationChatSessionMapper.selectOne(new QueryWrapper<ApplicationChatSessionEntity>()
                .eq("session_id", sessionId).eq("user_id", userData.getUserId()));
        if (sessionInfo == null) {
            throw new BusinessException("暂无记录");
        }

        List<ApplicationChatLogEntity> resultList = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .eq("session_id", sessionId).orderByAsc("log_id"));
        List<ApplicationLogVo> voList = new LinkedList<>();
        for (ApplicationChatLogEntity entity : resultList) {

            ApplicationLogVo vo = new ApplicationLogVo();
            BeanUtils.copyProperties(entity, vo);
            vo.setTotalTokens(entity.getTokens());
            vo.setAnswer(JSONUtil.toList(entity.getContent(), AnswerVo.class));
            vo.setToolUse(entity.getToolUse());

            voList.add(vo);
        }

        return voList;
    }
}