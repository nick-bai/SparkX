// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.system;

import sparkx.common.core.PageResult;
import sparkx.service.validate.system.UserValidate;
import sparkx.service.vo.system.UserQueryVo;
import sparkx.service.vo.system.UsersVo;

public interface ISystemUserService {

    /**
     * 用户列表
     * @return List<KnowledgeUsersEntity>
     */
    PageResult<UsersVo> getUserList(UserQueryVo query);

    /**
     * 添加用户
     * @param validate UserValidate
     */
    void addUser(UserValidate validate);

    /**
     * 编辑用户
     * @param validate UserValidate
     */
    void editUser(UserValidate validate);

    /**
     * 删除用户
     * @param userId String
     */
    void delUser(String userId);
}
