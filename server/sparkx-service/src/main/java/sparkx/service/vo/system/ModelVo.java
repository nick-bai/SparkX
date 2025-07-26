package sparkx.service.vo.system;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ModelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型id
     */
    private String modelId;

    /**
     * 模型名
     */
    private String name;
}
