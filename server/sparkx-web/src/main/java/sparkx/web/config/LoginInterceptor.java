// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.config;

import cn.hutool.jwt.JWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import sparkx.common.constant.SparkXConstant;
import sparkx.service.helper.UserContextHelper;
import sparkx.service.vo.system.LocalUserVo;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 创建登录身份校验拦截器
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 令牌验证
        String token = request.getHeader("Authorization");
        try {

            token = token.replace("Bearer ", "");
            boolean validate = JWT.of(token).setKey(SparkXConstant.CommonData.passwordSalt.getBytes()).validate(0);
            if (!validate) {
               throw new Exception();
            }

            JWT jwt = JWT.of(token);
            LocalUserVo localUser = new LocalUserVo();
            localUser.setUserId(String.valueOf(jwt.getPayload("userId")));
            localUser.setTeamId(Integer.valueOf(String.valueOf(jwt.getPayload("teamId"))));
            UserContextHelper.setUser(localUser);

            // 放行
            return true;

        } catch (Exception e) {
            // 设置响应状态码
            response.setStatus(401);
            // 设置响应字符集和响应内容
            response.setCharacterEncoding("UTF-8");
            String errorMessage = "登录过期";

            if (request.getHeader("Accept").equals("text/event-stream")) {
                response.setContentType("text/event-stream; charset=UTF-8");
                response.getWriter().write("event: [LOGIN_OUT]\n");
                response.getWriter().write("data: " + errorMessage + "\n\n");
            } else {
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"code\": 401, \"data\": \"\", \"msg\": \"" + errorMessage + "\"}");
            }

            // 不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除 ThreadLocal 中的用户数据，避免内存泄漏
        UserContextHelper.clearUser();
    }
}
