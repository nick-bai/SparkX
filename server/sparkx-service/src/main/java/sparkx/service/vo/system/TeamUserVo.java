// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 团队id
     */
    private Integer teamId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 是否管理员
     */
    private Integer isAdmin;

    /**
     * 知识库权限
     */
    private String datasetPermission;

    /**
     * 应用权限
     */
    private String appPermission;
}