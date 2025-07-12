import config from "@/config"
import http from "@/utils/request"

export default {
	userList: {
		url: `${config.API_URL}/team/userList`,
		name: "获取用户列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	searchUser: {
		url: `${config.API_URL}/team/searchUser`,
		name: "查询用户",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	addUser: {
		url: `${config.API_URL}/team/addUser`,
		name: "添加成员",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	delUser: {
		url: `${config.API_URL}/team/delUser`,
		name: "删除用户",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	updatePermission: {
		url: `${config.API_URL}/team/updatePermission`,
		name: "更新权限",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
}
