import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/models/list`,
		name: "获取模型列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	info: {
		url: `${config.API_URL}/models/info`,
		name: "获取模型列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	edit: {
		url: `${config.API_URL}/models/edit`,
		name: "编辑模型",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
