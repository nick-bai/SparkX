// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.mapper.application;

import org.apache.ibatis.annotations.Mapper;
import sparkx.common.core.IBaseMapper;
import sparkx.service.entity.workflow.ApplicationWorkflowRuntimeContextEntity;

/**
 * 工作流运行时上下文 Mapper
 */
@Mapper
public interface ApplicationWorkflowRuntimeContextMapper extends IBaseMapper<ApplicationWorkflowRuntimeContextEntity> {

}
