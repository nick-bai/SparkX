// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.web.controller.home;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.home.IHomeService;
import sparkx.service.validate.system.PasswordValidate;

@RestController
@RequestMapping("/api/index")
public class IndexController {

    @Autowired
    IHomeService iHomeService;

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public AjaxResult<Object> image(@RequestParam("file") MultipartFile file) {

        return AjaxResult.success(iHomeService.uploadImage(file));
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public AjaxResult<Object> password(@RequestBody @Valid PasswordValidate validate) {

        iHomeService.changePassword(validate);
        return AjaxResult.success();
    }
}
