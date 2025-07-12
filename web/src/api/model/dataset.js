import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/dataset/list`,
		name: "获取知识库列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/dataset/add`,
		name: "添加知识库",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	edit: {
		url: `${config.API_URL}/dataset/edit`,
		name: "修改知识库",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	hitTest: {
		url: `${config.API_URL}/dataset/hitTest`,
		name: "命中测试",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	embedding: {
		url: `${config.API_URL}/dataset/embedding`,
		name: "向量化知识库",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	del: {
		url: `${config.API_URL}/dataset/del`,
		name: "删除知识库",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	otherDataset: {
		url: `${config.API_URL}/dataset/otherDataset`,
		name: "获取其他知识库",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	transfer: {
		url: `${config.API_URL}/dataset/transfer`,
		name: "迁移文本",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
