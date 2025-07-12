// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend.chat;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import dev.langchain4j.service.TokenStream;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.constant.SparkXConstant;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeEntity;
import sparkx.service.extend.workflow.FlowNodeParser;
import sparkx.service.helper.ApplicationHelper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeMapper;
import sparkx.service.mapper.workflow.ApplicationWorkflowMapper;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.vo.system.LocalUserVo;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class WorkflowChat implements IChat {

    @Autowired
    ApplicationWorkflowMapper applicationWorkflowMapper;

    @Autowired
    FlowNodeParser flowNodeParser;

    @Autowired
    ApplicationWorkflowRuntimeMapper applicationWorkflowRuntimeMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    @Setter
    public SseEmitter emitter;

    @Autowired
    ApplicationHelper applicationHelper;

    /**
     * 编排模式聊天
     * @param applicationInfo ApplicationEntity
     * @param validate ApplicationSaveValidate
     * @return TokenStream
     */
    @Override
    public TokenStream streamChat(ApplicationEntity applicationInfo, ApplicationChatValidate validate) {

        // 流程配置
        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", applicationInfo.getAppId()));
        if (info == null) {
            throw new BusinessException("流程未配置");
        }

        // 记录开始节点数据
        ApplicationWorkflowRuntimeEntity runtimeEntity = new ApplicationWorkflowRuntimeEntity();
        String title = validate.getContent();
        if (title.length() > SparkXConstant.CommonData.defaultLen) {
            title = title.substring(0, SparkXConstant.CommonData.defaultLen);
        }
        runtimeEntity.setTitle(title);
        LocalUserVo userData = applicationHelper.getUserData();
        runtimeEntity.setUserId(userData.getUserId());
        runtimeEntity.setFlowId(info.getId());
        runtimeEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeMapper.insert(runtimeEntity);

        ApplicationWorkflowRuntimeContextEntity contextEntity = new ApplicationWorkflowRuntimeContextEntity();
        contextEntity.setStep(1);
        contextEntity.setNodeType("start-node");
        contextEntity.setRuntimeId(runtimeEntity.getId());

        JSONObject outputData = JSONUtil.createObj();
        outputData.set("sys.question", validate.getContent());
        outputData.set("sys.time", Tool.nowDateTime());
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            outputData.set("sys.ip", inetAddress.getHostAddress());
        } catch (UnknownHostException e) {
            outputData.set("sys.ip", SparkXConstant.CommonData.defaultIp);
        }

        outputData.set("sys.appId", info.getAppId());
        outputData.set("sys.sessionId", validate.getSessionId());
        contextEntity.setOutputData(outputData.toString());

        contextEntity.setCreateTime(Tool.nowDateTime());
        applicationWorkflowRuntimeContextMapper.insert(contextEntity);

        // 启动节点执行
        flowNodeParser.setEmitter(this.emitter);
        flowNodeParser.setRuntimeId(runtimeEntity.getId());
        flowNodeParser.run(info.getFlowData(), userData.getUserId(), validate.getSessionId());

        return null;
    }
}