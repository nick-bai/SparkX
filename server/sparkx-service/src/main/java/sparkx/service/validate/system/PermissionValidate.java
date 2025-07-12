// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.validate.system;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import sparkx.service.vo.system.PermissionDataItemVo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class PermissionValidate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @NotEmpty(message = "设置的用户不能为空")
    private String userId;

    /**
     * 权限类型 1:知识库 2:应用
     */
    @NotEmpty(message = "权限类型不能为空")
    private String type;

    /**
     * 权限数据
     */
    private List<PermissionDataItemVo> permissionData;
}
