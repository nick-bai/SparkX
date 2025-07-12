// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.extend.workflow;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.service.vo.workflow.EdgeVo;
import sparkx.service.vo.workflow.NodeRuntimeVo;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface IWorkflowNode {

    void setEmitter(SseEmitter emitter);

    void setLatch(CountDownLatch latch);

    List<EdgeVo> handle(NodeRuntimeVo runtimeVo);
}
