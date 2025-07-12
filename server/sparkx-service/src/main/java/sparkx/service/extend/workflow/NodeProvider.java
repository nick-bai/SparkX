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

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import sparkx.common.exception.BusinessException;

@Slf4j
@Component
public class NodeProvider {

    @Autowired
    private ApplicationContext context;

    /**
     * 根据节点名称去节点
     * @param nodeName String
     * @return IWorkflow
     */
    public IWorkflowNode handle(String nodeName) {

        try {

            String className = "sparkx.service.extend.workflow.node." + kebabToPascalCase(nodeName);
            // 使用Class.forName()获取Class对象
            Class<?> clazz = Class.forName(className);

            return (IWorkflowNode) context.getBean(clazz);

        } catch (ClassNotFoundException e) {
            log.error("Class not found: {}", e.getMessage());
        }

        throw new BusinessException("系统异常");
    }

    private String kebabToPascalCase(String kebabCase) {
        if (StrUtil.isBlank(kebabCase)) {
            return "";
        }

        // Split the string by '-'
        String[] parts = kebabCase.split("-");
        StringBuilder pascalCase = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                // Capitalize the first letter and append to the result
                pascalCase.append(StrUtil.upperFirst(part));
            }
        }

        return pascalCase.toString();
    }
}