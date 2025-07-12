// 静态路由配置
// 书写格式与动态路由格式一致，全部经由框架统一转换
// 比较动态路由在meta中多加入了role角色权限，为数组类型。一个菜单是否有权限显示，取决于它以及后代菜单是否有权限。
// routes 显示在左侧菜单中的路由(显示顺序在动态路由之前)
// 示例如下

// const routes = [
// 	{
// 		name: "demo",
// 		path: "/demo",
// 		meta: {
// 			icon: "el-icon-eleme-filled",
// 			title: "演示",
// 			role: ["SA"]
// 		},
// 		children: [{
// 			name: "demopage",
// 			path: "/demopage",
// 			component: "test/autocode/index",
// 			meta: {
// 				icon: "el-icon-menu",
// 				title: "演示页面",
// 				role: ["SA"]
// 			}
// 		}]
// 	}
// ]
import menu from '@/config/menu.js'
menu[0].children.push({
	"id": 100,
	"pid": 1,
	"name": '知识库详情',
	"path": "/dataset/detail",
	"component": "dataset/detail",
	"meta": {
		"title": "知识库详情",
		"hidden": true,
		"active": "/dataset/index",
	}
})
menu[0].children.push({
	"id": 101,
	"pid": 1,
	"name": '上传文档',
	"path": "/dataset/upload",
	"component": "dataset/upload",
	"meta": {
		"title": "上传文档",
		"hidden": true,
		"active": "/dataset/index",
	}
})
menu[0].children.push({
	"id": 102,
	"pid": 1,
	"name": '应用详情',
	"path": "/index/detail",
	"component": "index/detail",
	"meta": {
		"title": "应用详情",
		"hidden": true,
		"active": "/index/home",
	}
})
menu[0].children.push({
	"id": 104,
	"pid": 1,
	"name": '流程设计',
	"path": "/workflow/index",
	"component": "workflow",
	"meta": {
		"title": "应用详情",
		"hidden": true,
		"active": "/index/home",
	}
})

const routes = menu

export default routes;
