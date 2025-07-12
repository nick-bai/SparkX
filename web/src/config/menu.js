const menu = [{
	"id": 1,
	"pid": 0,
	"name": "SparkX",
	"auth": "",
	"path": "/home",
	"icon": "el-icon-chat-dot-square",
	"meta": {
		"icon": "el-icon-chat-dot-square",
		"title": "应用",
		"type": "menu",
		"hidden": false
	},
	"children": [{
		"id": 2,
		"pid": 1,
		"name": "应用",
		"auth": "index/home",
		"path": "/index/home",
		"icon": "el-icon-Comment",
		"component": "index/home",
		"meta": {
			"icon": "el-icon-Comment",
			"title": "应用",
			"type": "menu",
			"hidden": false
		}
	}, {
		"id": 3,
		"pid": 1,
		"name": "知识库",
		"auth": "/dataset/index",
		"path": "/dataset/index",
		"icon": "el-icon-Coin",
		"component": "dataset",
		"meta": {
			"icon": "el-icon-Coin",
			"title": "知识库",
			"type": "menu",
			"hidden": false
		}
	}, {
		"id": 4,
		"pid": 1,
		"name": "工具箱",
		"auth": "/tools/index",
		"path": "/tools/index",
		"icon": "el-icon-Box",
		"component": "tools",
		"meta": {
			"icon": "el-icon-Box",
			"title": "工具箱",
			"type": "menu",
			"hidden": false
		}
	}, {
		"id": 5,
		"pid": 1,
		"name": "系统",
		"auth": "/setting/index",
		"path": "/setting/index",
		"icon": "el-icon-Setting",
		"component": "setting",
		"meta": {
			"icon": "el-icon-Setting",
			"title": "系统",
			"type": "menu",
			"hidden": false
		}
	}]
}]

export default menu
