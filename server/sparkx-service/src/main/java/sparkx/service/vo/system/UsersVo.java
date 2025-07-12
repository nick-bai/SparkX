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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UsersVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;

    /**
     * 登录账号
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态 1:正常 2:禁用
     */
    private Integer status;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
}
