import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/tool/list`,
		name: "获取插件列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	toolList: {
		url: `${config.API_URL}/tool/toolList`,
		name: "获取可用的插件列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/tool/add`,
		name: "创建插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	addMcp: {
		url: `${config.API_URL}/tool/addMcp`,
		name: "创建MCP插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	editMcp: {
		url: `${config.API_URL}/tool/editMcp`,
		name: "编辑MCP插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	edit: {
		url: `${config.API_URL}/tool/edit`,
		name: "编辑插件",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	del: {
		url: `${config.API_URL}/tool/del`,
		name: "删除插件",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
}
