package sparkx.service.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.system_tokens")
public class SystemTokensEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(value="id", type= IdType.AUTO)
    @TableField(value = "id")
    private Integer id;

    /**
    * 消耗来源
    */
    @TableField(value = "source")
    private String source;

    /**
    * 模型平台
    */
    @TableField(value = "platform")
    private String platform;

    /**
    * 输入token数
    */
    @TableField(value = "input_token")
    private Integer inputToken;

    /**
    * 输出token数
    */
    @TableField(value = "output_token")
    private Integer outputToken;

    /**
    * 累计消耗数
    */
    @TableField(value = "total_token")
    private Integer totalToken;

    /**
    * 创建时间
    */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
    * 更新时间
    */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}