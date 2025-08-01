// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class EdgeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点id
     */
    private String id;

    /**
     * 起始节点
     */
    private String source;

    /**
     * 起始桩点
     */
    private String sourcePort;

    /**
     * 目标节点
     */
    private List<String> target;
}