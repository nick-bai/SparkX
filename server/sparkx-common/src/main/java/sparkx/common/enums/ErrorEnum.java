// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.common.enums;

public enum ErrorEnum {

    SHOW_MSG(1, "显示信息"),
    HIDE_MSG(0, "隐藏信息"),
    SUCCESS(0, "操作成功"),
    FAILED(-1, "操作失败"),
    PARAMS_VALID_ERROR(0, "参数校验错误"),
    PARAMS_TYPE_ERROR(0, "参数类型错误"),
    REQUEST_METHOD_ERROR(0, "请求方法错误"),
    ASSERT_ARGUMENT_ERROR(0, "断言参数错误"),
    ASSERT_MYBATIS_ERROR(0, "断言Mybatis错误"),

    LOGIN_ACCOUNT_ERROR(0, "登录账号或密码错误"),
    LOGIN_DISABLE_ERROR(0, "登录账号已被禁用了"),
    TOKEN_EMPTY(0, "登录超时，请重新登录"),
    TOKEN_INVALID(-1, "token参数无效"),
    CAPTCHA_ERROR(0, "验证码错误"),
    PAYMENT_ERROR(0, "发起支付失败"),

    NO_PERMISSION(0, "无相关权限"),
    REQUEST_404_ERROR(0, "请求接口不存在"),

    SYSTEM_ERROR(-500, "系统错误");

    /**
     * 构造方法
     */
    private final int code;
    private final String msg;
    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取状态码
     *
     * @author fzr
     * @return Long
     */
    public int getCode() {
        return this.code;
    }

    /**
     * 获取提示
     *
     * @author fzr
     * @return String
     */
    public String getMsg() {
        return this.msg;
    }

}
