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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sparkx.common.core.AjaxResult;
import sparkx.service.service.interfaces.system.ITeamService;
import sparkx.service.validate.system.AddTeamUserValidate;
import sparkx.service.validate.system.PermissionValidate;
import sparkx.service.vo.system.TeamUserVo;
import sparkx.service.vo.system.UsersVo;

import java.util.List;

@RequestMapping("api/team")
@RestController
public class TeamController {

    @Autowired
    ITeamService iTeamService;

    /**
     * 获取团队成员
     */
    @GetMapping("/userList")
    public AjaxResult<List<TeamUserVo>> getTeamUser() {

        return AjaxResult.success(iTeamService.getTeamUserList());
    }

    /**
     * 搜索用户
     */
    @GetMapping("/searchUser")
    public AjaxResult<List<UsersVo>> searchUser(@RequestParam("nickname") String nickname) {

        return AjaxResult.success(iTeamService.searchUser(nickname));
    }

    /**
     * 添加用户
     */
    @PostMapping("/addUser")
    public AjaxResult<Object> addUser(@RequestBody AddTeamUserValidate validate) {

        return AjaxResult.success(iTeamService.addUser(validate));
    }

    /**
     * 更新权限
     */
    @PostMapping("/updatePermission")
    public AjaxResult<Object> updatePermission(@RequestBody @Validated PermissionValidate validate) {

        iTeamService.updateUserPermission(validate);
        return AjaxResult.success();
    }

    /**
     * 删除用户
     */
    @GetMapping("/delUser")
    public AjaxResult<Object> delUser(@RequestParam("userId") String userId) {

        iTeamService.delUser(userId);
        return AjaxResult.success();
    }
}