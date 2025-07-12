import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/user/index`,
		name: "获取用户列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/user/add`,
		name: "添加用户",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	edit: {
		url: `${config.API_URL}/user/edit`,
		name: "编辑用户",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	del: {
		url: `${config.API_URL}/user/del`,
		name: "删除用户",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
