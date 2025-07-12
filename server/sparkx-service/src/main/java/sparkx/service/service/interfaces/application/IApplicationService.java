// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.application;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkx.common.core.PageResult;
import sparkx.service.validate.application.ApplicationAddValidate;
import sparkx.service.validate.application.ApplicationChatValidate;
import sparkx.service.validate.application.ApplicationSaveValidate;
import sparkx.service.vo.application.*;

/**
 * <p>
 * 系统应用表 服务类
 * </p>
 *
 * @author NickBai
 * @since 2025-03-18
 */
public interface IApplicationService {

    /**
     * 获取应用列表
     * @param queryVo ApplicationQueryVo
     * @return PageResult<ApplicationListVo>
     */
    PageResult<ApplicationListVo> getApplicationList(ApplicationQueryVo queryVo);

    /**
     * 添加应用
     * @param validate ApplicationAddValidate
     * @return String
     */
    String addApplication(ApplicationAddValidate validate);

    /**
     * 获取应用信息
     * @param appId String
     * @return ApplicationVo
     */
    ApplicationVo getApplicationInfo(String appId);

    /**
     * 获取应用信息
     * @param accessToken String
     * @return ApplicationVo
     */
    ApplicationVo getApplicationInfoByToken(String accessToken);

    /**
     * 编辑应用
     * @param validate ApplicationSaveValidate
     */
    void saveApplication(ApplicationSaveValidate validate);

    /**
     * 应用内聊天测试
     * @param validate ApplicationSaveValidate
     */
    SseEmitter sseChat(ApplicationChatValidate validate);

    /**
     * 获取统计数据
     * @param days Integer
     * @param startTime String
     * @param endTime String
     * @return CensusVo
     */
    CensusVo getCensusData(Integer days, String startTime, String endTime);

    /**
     * 获取对话记录
     * @param queryVo SessionQueryVo
     * @return PageResult<SessionListVo>
     */
    PageResult<SessionListVo> getSessionLog(SessionQueryVo queryVo);

    /**
     * 删除应用
     * @param appId String
     */
    void deleteApp(String appId);
}
