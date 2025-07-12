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

import sparkx.service.vo.system.AuthLoginVo;
import sparkx.service.vo.system.LoginVo;

import java.util.Map;

public interface ILoginService {

    /**
     * 登录操作
     * @param loginVo LoginVo
     * @return Map<String, String>
     */
    Map<String, String> doLogin(LoginVo loginVo);

    /**
     * 部署模式下的鉴权登录
     * @param loginVo AuthLoginVo
     * @return Map<String, String>
     */
    Map<String, Object> doAuthLogin(AuthLoginVo loginVo);
}
