// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.workflow;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sparkx.common.utils.Tool;
import sparkx.service.entity.application.ApplicationEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowEntity;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;
import sparkx.service.mapper.application.ApplicationMapper;
import sparkx.service.mapper.application.ApplicationWorkflowRuntimeContextMapper;
import sparkx.service.mapper.workflow.ApplicationWorkflowMapper;
import sparkx.service.service.interfaces.workflow.IWorkflowService;
import sparkx.service.validate.workflow.SaveWorkflowValidate;
import sparkx.service.vo.workflow.RuntimeContextVo;
import sparkx.service.vo.workflow.SaveWorkflowVo;

import java.util.LinkedList;
import java.util.List;

@Service
public class WorkflowServiceImpl implements IWorkflowService {

    @Autowired
    ApplicationWorkflowMapper applicationWorkflowMapper;

    @Autowired
    ApplicationMapper applicationMapper;

    @Autowired
    ApplicationWorkflowRuntimeContextMapper applicationWorkflowRuntimeContextMapper;

    /**
     * 获取流程数据
     * @param appId String
     * @return SaveWorkflowVo
     */
    @Override
    public SaveWorkflowVo getFlowInfo(String appId) {

        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", appId));

        SaveWorkflowVo vo = new SaveWorkflowVo();
        if (info == null) {
            return vo;
        }

        BeanUtils.copyProperties(info, vo);

        ApplicationEntity appInfo = applicationMapper.selectById(appId);
        vo.setAccessToken(appInfo.getAccessToken());

        return vo;
    }

    /**
     * 保存流程设计
     * @param validate SaveWorkflowValidate
     */
    @Override
    public void saveWorkflow(SaveWorkflowValidate validate) {

        ApplicationWorkflowEntity info = applicationWorkflowMapper.selectOne(new QueryWrapper<ApplicationWorkflowEntity>()
                .eq("app_id", validate.getAppId()));
        if (info == null) {
            ApplicationWorkflowEntity addEntity = new ApplicationWorkflowEntity();
            addEntity.setAppId(validate.getAppId());
            addEntity.setFlowData(validate.getFlowData());
            addEntity.setCreateTime(Tool.nowDateTime());

            applicationWorkflowMapper.insert(addEntity);
        } else {

            info.setFlowData(validate.getFlowData());
            info.setUpdateTime(Tool.nowDateTime());

            applicationWorkflowMapper.updateById(info);
        }
    }

    /**
     * 获取执行详情
     * @param runtimeId long
     * @return List<RuntimeContextVo>
     */
    @Override
    public List<RuntimeContextVo> getRuntimeDetail(long runtimeId) {

        List<ApplicationWorkflowRuntimeContextEntity> runtimeContextList =
                applicationWorkflowRuntimeContextMapper.selectList(
                        new QueryWrapper<ApplicationWorkflowRuntimeContextEntity>().eq("runtime_id", runtimeId).orderByAsc("step"));

        List<RuntimeContextVo> returnList = new LinkedList<>();
        for (ApplicationWorkflowRuntimeContextEntity context : runtimeContextList) {
            RuntimeContextVo contextVo = new RuntimeContextVo();
            BeanUtils.copyProperties(context, contextVo);

            returnList.add(contextVo);
        }

        return returnList;
    }
}