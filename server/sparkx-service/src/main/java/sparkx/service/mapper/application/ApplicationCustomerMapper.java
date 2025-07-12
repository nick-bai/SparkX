package sparkx.service.mapper.application;

import org.apache.ibatis.annotations.Mapper;
import sparkx.common.core.IBaseMapper;
import sparkx.service.entity.application.ApplicationCustomerEntity;

/**
 * 应用访客 Mapper
 */
@Mapper
public interface ApplicationCustomerMapper extends IBaseMapper<ApplicationCustomerEntity> {

}
