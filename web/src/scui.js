import config from "@/config"
import api from '@/api'
import tool from '@/utils/tool'
import http from "@/utils/request"
import {permission, rolePermission} from '@/utils/permission'

import auth from '@/directives/auth'
import auths from '@/directives/auths'
import authsAll from '@/directives/authsAll'
import role from '@/directives/role'
import time from '@/directives/time'
import copy from '@/directives/copy'
import errorHandler from '@/utils/errorHandler'

import * as elIcons from '@element-plus/icons-vue'

export default {
	install(app) {
		//挂载全局对象
		app.config.globalProperties.$CONFIG = config;
		app.config.globalProperties.$TOOL = tool;
		app.config.globalProperties.$HTTP = http;
		app.config.globalProperties.$API = api;
		app.config.globalProperties.$AUTH = permission;
		app.config.globalProperties.$ROLE = rolePermission;

		//注册全局指令
		app.directive('auth', auth)
		app.directive('auths', auths)
		app.directive('auths-all', authsAll)
		app.directive('role', role)
		app.directive('time', time)
		app.directive('copy', copy)

		//统一注册el-icon图标
		for (let icon in elIcons) {
			app.component(`ElIcon${icon}`, elIcons[icon])
		}

		//关闭async-validator全局控制台警告
		window.ASYNC_VALIDATOR_NO_WARNING = 1

		//全局代码错误捕捉
		app.config.errorHandler = errorHandler
	}
}
