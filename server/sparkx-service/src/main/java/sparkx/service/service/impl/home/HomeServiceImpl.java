// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.service.impl.home;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparkx.common.exception.BusinessException;
import sparkx.common.utils.Tool;
import sparkx.service.entity.system.SystemUsersEntity;
import sparkx.service.helper.UserContextHelper;
import sparkx.service.mapper.system.SystemUserMapper;
import sparkx.service.service.interfaces.home.IHomeService;
import sparkx.service.validate.system.PasswordValidate;
import sparkx.service.vo.system.LocalUserVo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class HomeServiceImpl implements IHomeService {

    @Value("${upload.upload-path}/")
    private String uploadPath;

    @Autowired
    SystemUserMapper systemUserMapper;

    // 定义允许的文件后缀
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("png", "jpg", "jpeg", "gif", "webp")
    );

    /**
     * 上传图片
     * @param file MultipartFile
     * @return String
     */
    @Override
    public String uploadImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取文件名并提取后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件名无效");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 校验文件后缀
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new BusinessException("只支持 png,jpg,jpeg,gif,webp");
        }

        try {

            String path = uploadPath + "image/" + DateUtil.today().replace("-", "") + "/";
            Path imagePath = Paths.get(path);
            if (!Files.exists(imagePath)) {
                Files.createDirectories(imagePath);
            }

            String newFileName = IdUtil.fastSimpleUUID() + "." + fileExtension;
            Path targetLocation = imagePath.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation);

            return path.substring(1) + newFileName;
        } catch (IOException ex) {
            throw new BusinessException("上传失败" + ex.getMessage());
        }
    }

    /**
     * 更改密码
     * @param validate PasswordValidate
     */
    @Override
    public void changePassword(PasswordValidate validate) {

        LocalUserVo userData = UserContextHelper.getUser();
        SystemUsersEntity userInfo = systemUserMapper.selectById(userData.getUserId());

        // 对比密码
        if (!Tool.verifyPassword(userInfo.getPassword(), validate.getOldPwd(), userInfo.getSalt())) {
            throw new BusinessException("旧密码错误");
        }

        userInfo.setPassword(Tool.makePassword(validate.getNewPwd(), userInfo.getSalt()));
        userInfo.setUpdateTime(Tool.nowDateTime());
        systemUserMapper.updateById(userInfo);
    }
}
