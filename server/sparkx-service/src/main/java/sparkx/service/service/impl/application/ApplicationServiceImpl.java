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

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.constant.SparkXConstant;
import sparkx.common.core.PageResult;
import sparkx.common.enums.AppType;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.*;
import sparkx.service.entity.system.ModelsEntity;
import sparkx.service.entity.system.SystemTeamUserEntity;
import sparkx.service.entity.system.SystemUsersEntity;
import sparkx.service.extend.chat.AgentChat;
import sparkx.service.extend.chat.WorkflowChat;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.helper.SseEmitterHelper;
import sparkx.service.helper.UserContextHelper;
import sparkx.service.mapper.application.*;
import sparkx.service.mapper.system.ModelsMapper;
import sparkx.service.mapper.system.SystemTeamUserMapper;
import sparkx.service.mapper.system.SystemUserMapper;
import sparkx.service.service.interfaces.application.IApplicationService;
import sparkx.service.validate.application.ApplicationAddValidate;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.validate.application.ApplicationSaveValidate;
import sparkx.service.vo.application.*;
import sparkx.service.vo.system.LocalUserVo;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 系统应用表 服务实现类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-18
 */
@Slf4j
@Service
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    ApplicationDatasetRelationMapper applicationDatasetRelationMapper;

    @Autowired
    ApplicationToolRelationMapper applicationToolRelationMapper;

    @Autowired
    SystemUserMapper systemUserMapper;

    @Autowired
    SseEmitterHelper sseEmitterHelper;

    @Autowired
    ApplicationChatLogMapper applicationChatLogMapper;

    @Autowired
    ApplicationChatSessionMapper applicationChatSessionMapper;

    @Autowired
    AgentChat agentChat;

    @Autowired
    WorkflowChat workflowChat;

    @Autowired
    ApplicationHelper applicationHelper;

    @Autowired
    SystemTeamUserMapper systemTeamUserMapper;

    @Autowired
    ModelsMapper modelsMapper;

    /**
     * 应用列表
     * @param queryVo ApplicationQueryVo
     * @return PageResult<ApplicationListVo>
     */
    @Override
    public PageResult<ApplicationListVo> getApplicationList(ApplicationQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<ApplicationEntity> queryWrapper = new QueryWrapper<>();

        if (!queryVo.getName().isBlank()) {
            queryWrapper.like("name", queryVo.getName());
        }

        if (queryVo.getExclude() != null && !queryVo.getExclude().isBlank()) {
            queryWrapper.notIn("app_id", queryVo.getExclude());
        }

        LocalUserVo userData = UserContextHelper.getUser();
        // 查出当前用户所在的团队
        List<SystemTeamUserEntity> teamListData = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().select("team_id").eq("user_id", userData.getUserId()));
        List<String> viewAppIds = new ArrayList<>();
        List<String> otherManageAppIds = new ArrayList<>();
        List<String> manageAppIds = new ArrayList<>();

        // 全部的数据
        if (queryVo.getType().equals(0) || queryVo.getType().equals(2)) {

            // 获取当前用户不是管理员的团队应用ID
            List<Integer> otherTeamIds = teamListData.stream().map(SystemTeamUserEntity::getTeamId)
                    .filter(item -> !item.equals(userData.getTeamId())).toList();
            if (!CollectionUtils.isEmpty(otherTeamIds)) {
                Map<String, List<String>> otherTeamMap = getTeamAppIds(otherTeamIds);
                viewAppIds.addAll(otherTeamMap.get("viewIds"));
                otherManageAppIds.addAll(otherTeamMap.get("manageIds"));
            }

            // 获取当前用户是管理员的团队应用ID
            List<Integer> userTeamIds = new ArrayList<>();
            userTeamIds.add(userData.getTeamId());
            Map<String, List<String>> userTeamMap = getTeamAppIds(userTeamIds);
            viewAppIds.addAll(userTeamMap.get("viewIds"));
            otherManageAppIds.addAll(userTeamMap.get("manageIds"));

            if (queryVo.getType().equals(0)) {
                // 获取管理员自己的应用ID
                List<ApplicationEntity> mangeAppList = applicationMapper.selectList(
                        new QueryWrapper<ApplicationEntity>().select("app_id").eq("user_id", userData.getUserId()));
                List<String> manageAppIdList = mangeAppList.stream().map(ApplicationEntity::getAppId).toList();
                viewAppIds.addAll(manageAppIdList);
                manageAppIds.addAll(manageAppIdList);
            }

            if (CollectionUtils.isEmpty(viewAppIds)) {
                List<ApplicationListVo> applicationVoList = new LinkedList<>();
                return PageResult.iPageHandle(0L, pageNo, pageSize, applicationVoList);
            }

            // 只查可见的数据
            queryWrapper.in("app_id", viewAppIds);
        } else if (queryVo.getType().equals(1)) { // 自己的数据
            queryWrapper.eq("user_id", userData.getUserId());
        }

        queryWrapper.orderByDesc("create_time");
        IPage<ApplicationEntity> applicationListRes = applicationMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<ApplicationListVo> applicationVoList = new LinkedList<>();

        for (ApplicationEntity entity : applicationListRes.getRecords()) {
            ApplicationListVo vo = new ApplicationListVo();
            BeanUtils.copyProperties(entity, vo);

            SystemUsersEntity userInfo = systemUserMapper.selectById(entity.getUserId());
            vo.setAuthor(userInfo.getNickname());

            // 补充权限
            if (manageAppIds.contains(vo.getAppId())) {
                vo.setView(true);
                vo.setManage(true);
            } else {
                vo.setView(viewAppIds.contains(vo.getAppId()));
                vo.setManage(otherManageAppIds.contains(vo.getAppId()));
            }

            applicationVoList.add(vo);
        }

        return PageResult.iPageHandle(applicationListRes.getTotal(), pageNo, pageSize, applicationVoList);
    }

    /**
     * 添加应用
     * @param validate ApplicationAddValidate
     */
    @Override
    public String addApplication(ApplicationAddValidate validate) {

        ApplicationEntity entity = new ApplicationEntity();
        entity.setAppId(IdUtil.randomUUID());
        entity.setAccessToken(Tool.makeToken());
        entity.setName(validate.getName());
        entity.setIcon("/icons/default_logo.png");
        LocalUserVo userData = UserContextHelper.getUser();
        entity.setUserId(userData.getUserId());
        entity.setDescription(validate.getDescription());
        entity.setType(validate.getType());
        entity.setCreateTime(Tool.nowDateTime());

        applicationMapper.insert(entity);

        return entity.getAppId();
    }

    /**
     * 获取应用信息
     * @param appId String
     * @return ApplicationVo
     */
    @Override
    public ApplicationVo getApplicationInfo(String appId) {

        LocalUserVo userData = UserContextHelper.getUser();

        ApplicationVo applicationVo = new ApplicationVo();
        ApplicationEntity info = applicationMapper.selectOne(
                new QueryWrapper<ApplicationEntity>()
                        .eq("user_id", userData.getUserId())
                        .eq("app_id", appId));
        BeanUtils.copyProperties(info, applicationVo);

        // 查询关联的知识库信息
        applicationVo.setDatasetList(applicationHelper.getRelationDatasetList(appId));
        // 查询关联的插件信息
        applicationVo.setToolList(applicationHelper.getRelationToolList(appId));
        // 查询当前模型是否要显示插件引用
        ModelsEntity modelInfo = modelsMapper.selectById(info.getModelId());
        applicationVo.setShowTools(false);
        if (modelInfo != null && modelInfo.getFunctionCalling().contains(info.getModelName())) {
            applicationVo.setShowTools(true);
        }

        return applicationVo;
    }

    /**
     * 获取应用信息
     * @param accessToken String
     * @return ApplicationVo
     */
    @Override
    public ApplicationVo getApplicationInfoByToken(String accessToken) {

        LocalUserVo userData = UserContextHelper.getUser();

        ApplicationVo applicationVo = new ApplicationVo();
        ApplicationEntity info = applicationMapper.selectOne(
                new QueryWrapper<ApplicationEntity>()
                        .eq("user_id", userData.getUserId())
                        .eq("access_token", accessToken));
        BeanUtils.copyProperties(info, applicationVo);

        // 查询关联的知识库信息
        applicationVo.setDatasetList(applicationHelper.getRelationDatasetList(info.getAppId()));
        // 查询关联的插件信息
        applicationVo.setToolList(applicationHelper.getRelationToolList(info.getAppId()));
        // 查询当前模型是否要显示插件引用
        ModelsEntity modelInfo = modelsMapper.selectById(info.getModelId());
        applicationVo.setShowTools(false);
        if (modelInfo != null && modelInfo.getFunctionCalling().contains(info.getModelName())) {
            applicationVo.setShowTools(true);
        }

        return applicationVo;
    }

    /**
     * 编辑应用
     * @param validate ApplicationSaveValidate
     */
    @Override
    public void saveApplication(ApplicationSaveValidate validate) {

        if (validate.getSaveType().equals(2) && validate.getType().equals(1)) {

            if (validate.getTemperature() <= 0) {
                throw new BusinessException("温度数值应该大于0");
            }

            if (validate.getModelId().isBlank() || validate.getModelName().isBlank()) {
                throw new BusinessException("请设置AI模型");
            }
        }

        if (!validate.getPrologue().getQuestion().isEmpty()
                && validate.getPrologue().getTitle().isBlank()) {
            throw new BusinessException("开场白不能为空");
        }

        if (!validate.getPrologue().getQuestion().isEmpty()) {
            validate.getPrologue().getQuestion().forEach(item -> {
                if (item.getContent().isBlank()) {
                    throw new BusinessException("开场问题不能为空");
                }
            });
        }

        ApplicationEntity applicationInfo = applicationMapper.selectById(validate.getAppId());
        if (applicationInfo == null) {
            throw new BusinessException("应用信息错误");
        }

        // 开始入库
        BeanUtils.copyProperties(validate, applicationInfo);

        applicationInfo.setRelationDataset(!validate.getDatasetList().isEmpty() ? 1 : 2);
        applicationInfo.setPrologue(JSONUtil.toJsonStr(validate.getPrologue()));
        applicationInfo.setStatus(validate.getSaveType());
        applicationInfo.setUpdateTime(Tool.nowDateTime());

        applicationMapper.updateById(applicationInfo);

        // 记录知识库关联表
        applicationDatasetRelationMapper.delete(new QueryWrapper<ApplicationDatasetRelationEntity>()
                .eq("app_id", applicationInfo.getAppId()));

        validate.getDatasetList().forEach(item -> {
            ApplicationDatasetRelationEntity entity = new ApplicationDatasetRelationEntity();
            entity.setAppId(applicationInfo.getAppId());
            entity.setDatasetId(item.getDatasetId());
            entity.setCreateTime(Tool.nowDateTime());

            applicationDatasetRelationMapper.insert(entity);
        });

        // 记录关联的插件
        applicationToolRelationMapper.delete(new QueryWrapper<ApplicationToolRelationEntity>()
                .eq("app_id", applicationInfo.getAppId()));

        validate.getToolList().forEach(item -> {
            ApplicationToolRelationEntity entity = new ApplicationToolRelationEntity();
            entity.setAppId(applicationInfo.getAppId());
            entity.setToolId(item.getId());
            entity.setCreateTime(Tool.nowDateTime());

            applicationToolRelationMapper.insert(entity);
        });
    }

    /**
     * 应用内聊天测试
     * @param validate ApplicationSaveValidate
     */
    @Override
    public SseEmitter sseChat(ApplicationChatValidate validate) {

        SseEmitter emitter = new SseEmitter();

        try {

            // 获取应用信息
            ApplicationEntity applicationInfo = applicationMapper.selectById(validate.getAppId());
            if (applicationInfo == null) {
                throw new BusinessException("应用配置错误");
            }

            // 根据应用模式分流处理
            if (applicationInfo.getType().equals(AppType.AGENT.getCode())) {

                LocalUserVo userData = applicationHelper.getUserData();
                applicationInfo.setUserId(userData.getUserId()); // 设置为运行用户
                validate.setContextId(0); // 不在构建模型的时候记录上下文记录

                TokenStream tokenStream = agentChat.streamChat(applicationInfo, validate);
                // 异步发送消息
                sseEmitterHelper.asyncSend2Client(tokenStream, emitter, 0, "");
            } else {

                sseEmitterHelper.sendStartSse(emitter);
                workflowChat.setEmitter(emitter);
                workflowChat.streamChat(applicationInfo, validate);
            }

        } catch (Exception e) {
            log.error("构建ai服务出现了问题：", e);
            try {

                emitter.send(SseEmitter.event().name(SparkXConstant.SSEEventName.ERROR)
                        .data(e.getMessage()));
            } catch (IOException e2) {
                log.error("startSse error", e2);
                emitter.completeWithError(e);
            }
        }

        return emitter;
    }

    /**
     * 获取统计数据
     * @param days Integer
     * @param startTime String
     * @param endTime String
     * @return CensusVo
     */
    @Override
    public CensusVo getCensusData(Integer days, String startTime, String endTime) {

        CensusVo censusVo = new CensusVo();

        // 时间线
        List<String> timeLine;
        if (days.equals(1)) { // 最近7天

            List<String> offsetDays = getOffsetDay(7);
            startTime = offsetDays.get(0);
            endTime = offsetDays.get(1);
        } else if (days.equals(2)) { // 最近30天

            List<String> offsetDays = getOffsetDay(30);
            startTime = offsetDays.get(0);
            endTime = offsetDays.get(1);
        } else if (days.equals(3)) { // 最近90天

            List<String> offsetDays = getOffsetDay(90);
            startTime = offsetDays.get(0);
            endTime = offsetDays.get(1);
        } else if (days.equals(4)) { // 最近半年

            List<String> offsetDays = getOffsetDay(183);
            startTime = offsetDays.get(0);
            endTime = offsetDays.get(1);
        }

        timeLine = Tool.getDateRange(startTime, endTime);
        censusVo.setTimeLine(timeLine);

        // 初始化日期数据
        Map<String, Long> originalData = new LinkedHashMap<>();
        timeLine.forEach(time -> {
            originalData.put(time, 0L);
        });

        // 用户总数
        ApplicationChatLogEntity totalUserData = applicationChatLogMapper.selectOne(new QueryWrapper<ApplicationChatLogEntity>()
                .select("COUNT(DISTINCT user_id) AS totalData")
                .ge("create_time", startTime + " 00:00:00")
                .le("create_time", endTime + " 23:59:59").groupBy("user_id"));
        censusVo.setUserNum(totalUserData == null ? 0 : totalUserData.getTotalData());

        // 问题总数
        ApplicationChatLogEntity totalQuestionData = applicationChatLogMapper.selectOne(new QueryWrapper<ApplicationChatLogEntity>()
                .select("COUNT(*) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59"));
        censusVo.setQuestionNum(totalQuestionData == null ? 0 : totalQuestionData.getTotalData());

        // tokens总数
        ApplicationChatLogEntity totalTokensData = applicationChatLogMapper.selectOne(new QueryWrapper<ApplicationChatLogEntity>()
                .select("sum(tokens) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59"));
        censusVo.setTokensNum(totalTokensData == null ? 0 : totalTokensData.getTotalData());

        // 赞总数
        ApplicationChatLogEntity totalLikesData = applicationChatLogMapper.selectOne(new QueryWrapper<ApplicationChatLogEntity>()
                .select("count(*) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").eq("appraise", 1));
        censusVo.setLikeNum(totalLikesData == null ? 0 : totalLikesData.getTotalData());

        // 踩总数
        ApplicationChatLogEntity totalDislikesData = applicationChatLogMapper.selectOne(new QueryWrapper<ApplicationChatLogEntity>()
                .select("count(*) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").eq("appraise", 2));
        censusVo.setDislikeNum(totalDislikesData == null ? 0 : totalDislikesData.getTotalData());

        // 用户数基础数据
        List<ApplicationChatLogEntity> userSeriesListData = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .select("DATE(create_time) AS date,COUNT(DISTINCT user_id) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").groupBy("DATE(create_time)")
                .orderByAsc("DATE(create_time)"));
        Map<String, Long> users2data = new HashMap<>();
        userSeriesListData.forEach(item -> {
            users2data.put(item.getDate(), item.getTotalData());
        });

        CensusVo.CensusSeriesVo userSeriesVo = new CensusVo.CensusSeriesVo();
        List<Long> userSeriesVoData = new LinkedList<>();
        for (String key : originalData.keySet()) {
            userSeriesVoData.add(users2data.get(key) != null ? users2data.get(key) : 0L);
        }
        userSeriesVo.setData(userSeriesVoData);
        userSeriesVo.setSmooth(true);
        userSeriesVo.setType("line");
        censusVo.setUserSeries(userSeriesVo);

        // 问题数基础数据
        List<ApplicationChatLogEntity> questionListData = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .select("DATE(create_time) AS date,COUNT(*) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").groupBy("DATE(create_time)")
                .orderByAsc("DATE(create_time)"));
        Map<String, Long> question2data = new HashMap<>();
        questionListData.forEach(item -> {
            question2data.put(item.getDate(), item.getTotalData());
        });

        CensusVo.CensusSeriesVo questionVo = new CensusVo.CensusSeriesVo();
        List<Long> questionVoData = new LinkedList<>();
        for (String key : originalData.keySet()) {
            questionVoData.add(question2data.get(key) != null ? question2data.get(key) : 0L);
        }
        questionVo.setData(questionVoData);
        questionVo.setSmooth(true);
        questionVo.setType("line");
        censusVo.setQuestionSeries(questionVo);

        // tokens数基础数据
        List<ApplicationChatLogEntity> tokensListData = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .select("DATE(create_time) AS date,SUM(tokens) AS totalData")
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").groupBy("DATE(create_time)")
                .orderByAsc("DATE(create_time)"));
        Map<String, Long> tokens2data = new HashMap<>();
        tokensListData.forEach(item -> {
            tokens2data.put(item.getDate(), item.getTotalData());
        });

        CensusVo.CensusSeriesVo tokensVo = new CensusVo.CensusSeriesVo();
        List<Long> tokensVoData = new LinkedList<>();
        for (String key : originalData.keySet()) {
            tokensVoData.add(tokens2data.get(key) != null ? tokens2data.get(key) : 0L);
        }
        tokensVo.setData(tokensVoData);
        tokensVo.setSmooth(true);
        tokensVo.setType("line");
        censusVo.setTokensSeries(tokensVo);

        // 评价点赞的
        List<ApplicationChatLogEntity> appraiseLikeData = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .select("DATE(create_time) AS date,count(*) AS totalData")
                .eq("appraise", 1)
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").groupBy("DATE(create_time)")
                .orderByAsc("DATE(create_time)"));
        Map<String, Long> like2data = new HashMap<>();
        appraiseLikeData.forEach(item -> {
            like2data.put(item.getDate(), item.getTotalData());
        });

        CensusVo.CensusSeriesVo likeVo = new CensusVo.CensusSeriesVo();
        List<Long> likeVoData = new LinkedList<>();
        for (String key : originalData.keySet()) {
            likeVoData.add(like2data.get(key) != null ? like2data.get(key) : 0L);
        }
        likeVo.setData(likeVoData);
        likeVo.setSmooth(true);
        likeVo.setType("line");
        censusVo.setLikeSeries(likeVo);

        // 评价踩的
        List<ApplicationChatLogEntity> appraiseDislikeData = applicationChatLogMapper.selectList(new QueryWrapper<ApplicationChatLogEntity>()
                .select("DATE(create_time) AS date,count(*) AS totalData")
                .eq("appraise", 2)
                .ge("create_time", startTime + " 00:00:00").le("create_time", endTime + " 23:59:59").groupBy("DATE(create_time)")
                .orderByAsc("DATE(create_time)"));
        Map<String, Long> dislike2data = new HashMap<>();
        appraiseDislikeData.forEach(item -> {
            dislike2data.put(item.getDate(), item.getTotalData());
        });

        CensusVo.CensusSeriesVo dislikeVo = new CensusVo.CensusSeriesVo();
        List<Long> dislikeVoData = new LinkedList<>();
        for (String key : originalData.keySet()) {
            dislikeVoData.add(dislike2data.get(key) != null ? dislike2data.get(key) : 0L);
        }
        dislikeVo.setData(dislikeVoData);
        dislikeVo.setSmooth(true);
        dislikeVo.setType("line");
        censusVo.setDislikeSeries(dislikeVo);

        return censusVo;
    }

    /**
     * 获取对话记录
     * @param queryVo SessionQueryVo
     * @return PageResult<SessionListVo>
     */
    @Override
    public PageResult<SessionListVo> getSessionLog(SessionQueryVo queryVo) {

        long pageNo   = queryVo.getPage();
        long pageSize = queryVo.getLimit();

        QueryWrapper<ApplicationChatSessionEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", queryVo.getAppId());

        if (!queryVo.getStartTime().isBlank()) {
            queryWrapper.ge("create_time", queryVo.getStartTime() + " 00:00:00");
            queryWrapper.le("create_time", queryVo.getEndTime() + " 23:59:59");
        }

        LocalUserVo userData = UserContextHelper.getUser();
        queryWrapper.eq("user_id", userData.getUserId());

        queryWrapper.orderByDesc("create_time");
        IPage<ApplicationChatSessionEntity> sessionListRes = applicationChatSessionMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<SessionListVo> sessionList = new LinkedList<>();

        for (ApplicationChatSessionEntity entity : sessionListRes.getRecords()) {
            SessionListVo vo = new SessionListVo();
            BeanUtils.copyProperties(entity, vo);

            // 查询对话次数
            long num = applicationChatLogMapper.selectCount(new QueryWrapper<ApplicationChatLogEntity>()
                    .eq("app_id", entity.getAppId()).eq("session_id", entity.getSessionId()));
            vo.setNum(num);

            sessionList.add(vo);
        }

        return PageResult.iPageHandle(sessionListRes.getTotal(), pageNo, pageSize, sessionList);
    }

    /**
     * 删除应用
     * @param appId String
     */
    @Override
    @Transactional
    public void deleteApp(String appId) {

        LocalUserVo userData = UserContextHelper.getUser();
        String userId = userData.getUserId();

        ApplicationEntity info = applicationMapper.selectOne(new QueryWrapper<ApplicationEntity>()
                .eq("user_id", userId).eq("app_id", appId));
        if (info == null) {
            throw new BusinessException("该应用不存在");
        }

        applicationMapper.delete(new QueryWrapper<ApplicationEntity>()
                .eq("user_id", userId).eq("app_id", appId));

        applicationChatLogMapper.delete(new QueryWrapper<ApplicationChatLogEntity>()
                .eq("app_id", appId));

        applicationChatSessionMapper.delete(new QueryWrapper<ApplicationChatSessionEntity>()
                .eq("user_id", userId).eq("app_id", appId));

        applicationDatasetRelationMapper.delete(new QueryWrapper<ApplicationDatasetRelationEntity>()
                .eq("app_id", appId));
    }

    private List<String> getOffsetDay(Integer offsetDays) {

        Date date = DateUtil.date();
        Date newDate = DateUtil.offset(date, DateField.DAY_OF_MONTH, -offsetDays);
        String startDate = DateUtil.format(newDate, "yyyy-MM-dd");
        String endDate = DateUtil.format(date, "yyyy-MM-dd");

        List<String> days = new LinkedList<>();
        days.add(startDate);
        days.add(endDate);

        return days;
    }

    /**
     * 获取团队下的应用id
     * @param teamIds teamIds
     * @return Map<List<String>, List<String>>
     */
    private Map<String, List<String>> getTeamAppIds(List<Integer> teamIds) {

        Map<String, List<String>> permissionList = new HashMap<>();

        List<SystemTeamUserEntity> teamUserData = systemTeamUserMapper.selectList(
                new QueryWrapper<SystemTeamUserEntity>().select("app_permission").in("team_id", teamIds));

        List<String> viewIds = new ArrayList<>();
        List<String> manageIds = new ArrayList<>();
        for (SystemTeamUserEntity entity : teamUserData) {
            if (entity.getAppPermission().isBlank()) {
                continue;
            }

            PermissionVo permissionData = JSONUtil.toBean(entity.getAppPermission(), PermissionVo.class);

            if (!CollectionUtils.isEmpty(permissionData.getView())) {
                viewIds.addAll(permissionData.getView());
                manageIds.addAll(permissionData.getManage());
            }
        }

        List<String> finalViewIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(viewIds)) {

            // 过滤掉状态不是已发布的应用ID
            List<ApplicationEntity> appList = applicationMapper.selectList(
                    new QueryWrapper<ApplicationEntity>().select("app_id").in("app_id", viewIds).eq("status", 2));
            finalViewIds = appList.stream().map(ApplicationEntity::getAppId).toList();
        }

        permissionList.put("viewIds", finalViewIds);
        permissionList.put("manageIds", manageIds.stream().filter(finalViewIds::contains).distinct().toList());

        return permissionList;
    }
}
