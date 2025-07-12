package sparkx.service.entity.application;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("public.application_customer")
public class ApplicationCustomerEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
    * 访客标识
    */
    @TableId(value="customer_id", type= IdType.AUTO)
    @TableField(value = "customer_id")
    private String customerId;

    /**
    * 访客的ip
    */
    @TableField(value = "customer_ip")
    private String customerIp;

    /**
    * 关联的应用id
    */
    @TableField(value = "app_token")
    private String appToken;

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