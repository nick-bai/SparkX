import config from "@/config"
import http from "@/utils/request"

export default {
	list: {
		url: `${config.API_URL}/question/list`,
		name: "获取问题列表",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	add: {
		url: `${config.API_URL}/question/add`,
		name: "添加问题",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	getRelation: {
		url: `${config.API_URL}/question/getRelation`,
		name: "获取问题关联信息",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	doRelation: {
		url: `${config.API_URL}/question/doRelation`,
		name: "关联问题",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	getRelationData: {
		url: `${config.API_URL}/question/getRelationData`,
		name: "获取问题关联信息",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	},
	editQuestion: {
		url: `${config.API_URL}/question/edit`,
		name: "编辑问题",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	delQuestion: {
		url: `${config.API_URL}/question/del`,
		name: "删除问题",
		get: async function(data={}){

			return await http.get(this.url, data);
		}
	}
}
