import config from "@/config"
import http from "@/utils/request"

export default {
	info: {
		url: `${config.API_URL}/workflow/info`,
		name: "获取流程信息",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	save: {
		url: `${config.API_URL}/workflow/save`,
		name: "保存流程",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	runDetail: {
		url: `${config.API_URL}/workflow/runDetail`,
		name: "获取执行详情",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
