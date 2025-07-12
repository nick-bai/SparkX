// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.tool;

import sparkx.common.core.PageResult;
import sparkx.service.validate.tool.AddMcpToolsValidate;
import sparkx.service.validate.tool.AddToolsValidate;
import sparkx.service.validate.tool.EditMcpToolsValidate;
import sparkx.service.validate.tool.EditToolsValidate;
import sparkx.service.vo.tool.ToolQueryVo;
import sparkx.service.vo.tool.ToolsListVo;
import sparkx.service.vo.tool.ToolsSimpleListVo;

import java.util.List;

public interface IToolService {

    /**
     * 获取工具列表
     * @param queryVo ToolQueryVo
     */
    PageResult<ToolsListVo> getToolList(ToolQueryVo queryVo);

    /**
     * 添加插件
     * @param validate AddToolsValidate
     */
    void addTools(AddToolsValidate validate);

    /**
     * 添加mcp插件
     * @param validate AddMcpToolsValidate
     */
    void addMcpTools(AddMcpToolsValidate validate);

    /**
     * 编辑插件
     * @param validate EditToolsValidate
     */
    void editTools(EditToolsValidate validate);

    /**
     * 编辑MCP插件
     * @param validate EditMcpToolsValidate
     */
    void editMcpTools(EditMcpToolsValidate validate);

    /**
     * 删除插件
     * @param id Integer
     */
    void delTool(Integer id);

    /**
     * 获取全部的插件列表
     * @param type Integer
     * @return List<ToolsSimpleListVo>
     */
    List<ToolsSimpleListVo> getAllToolList(Integer type);
}
