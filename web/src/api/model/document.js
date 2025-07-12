import config from "@/config"
import http from "@/utils/request"

export default {
	getList: {
		url: `${config.API_URL}/document/list`,
		name: "获取文档列表",
		get: async function(data={}){
			return await http.get(this.url, data);
		}
	},
	preview: {
		url: `${config.API_URL}/document/preview`,
		name: "上传文档",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	},
	uploadFile: {
		url: `${config.API_URL}/document/uploadFile`,
		name: "上传文档",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	},
	save: {
		url: `${config.API_URL}/document/save`,
		name: "保存文档",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	},
	embedding: {
		url: `${config.API_URL}/document/embedding`,
		name: "文本向量化",
		get: async function(data={}){
			return await http.get(this.url, data);
		}
	},
	setting: {
		url: `${config.API_URL}/document/setting`,
		name: "设置文档",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	},
	del: {
		url: `${config.API_URL}/document/del`,
		name: "删除文档",
		get: async function(data={}){
			return await http.get(this.url, data);
		}
	},
	question: {
		url: `${config.API_URL}/document/question`,
		name: "生成问题",
		post: async function(data={}){
			return await http.post(this.url, data);
		}
	}
}
