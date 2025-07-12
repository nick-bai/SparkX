import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/application/list`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/application/add`,
		name: "添加应用",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	info: {
		url: `${config.API_URL}/application/detail`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	chatDetail: {
		url: `${config.API_URL}/application/chatDetail`,
		name: "获取应用列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	save: {
		url: `${config.API_URL}/application/save`,
		name: "设置应用",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	sseChat: {
		url: `${config.API_URL}/application/sseChat`,
		name: "聊天测试",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	census: {
		url: `${config.API_URL}/application/census`,
		name: "统计数据",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	del: {
		url: `${config.API_URL}/application/del`,
		name: "删除应用",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
