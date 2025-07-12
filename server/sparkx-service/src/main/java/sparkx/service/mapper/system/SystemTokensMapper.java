package sparkx.service.mapper.system;

import org.apache.ibatis.annotations.Mapper;
import sparkx.common.core.IBaseMapper;
import sparkx.service.entity.system.SystemTokensEntity;

/**
 * 系统消耗token表 Mapper
 */
@Mapper
public interface SystemTokensMapper extends IBaseMapper<SystemTokensEntity> {

}
