// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.interfaces.home;

import org.springframework.web.multipart.MultipartFile;
import sparkx.service.validate.system.PasswordValidate;

public interface IHomeService {

    /**
     * 上传图片
     * @param file MultipartFile
     * @return String
     */
    String uploadImage(MultipartFile file);

    /**
     * 修改密码
     * @param validate PasswordValidate
     */
    void changePassword(PasswordValidate validate);
}