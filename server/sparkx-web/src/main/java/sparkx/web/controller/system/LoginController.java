// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.system.ILoginService;
import sparkx.service.vo.system.AuthLoginVo;
import sparkx.service.vo.system.LoginVo;

import java.util.Map;

@RequestMapping("/api/login")
@RestController
public class LoginController {

    @Autowired
    ILoginService iLoginService;

    /**
     * 登录
     */
    @PostMapping("/doLogin")
    public AjaxResult<Map<String, String>> login(@RequestBody LoginVo loginVo) {

        return AjaxResult.success(iLoginService.doLogin(loginVo));
    }

    /**
     * 部署模式下的鉴权登录
     */
    @PostMapping("/authLogin")
    public AjaxResult<Map<String, Object>> authLogin(@RequestBody AuthLoginVo loginVo) {

        return AjaxResult.success(iLoginService.doAuthLogin(loginVo));
    }
}