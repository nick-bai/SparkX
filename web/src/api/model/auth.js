import config from "@/config"
import http from "@/utils/request"

export default {
	login: {
		url: `${config.API_URL}/login/doLogin`,
		name: "登录获取TOKEN",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	password: {
		url: `${config.API_URL}/index/password`,
		name: "修改密码",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	},
	authLogin: {
		url: `${config.API_URL}/login/authLogin`,
		name: "部署鉴权登录",
		post: async function(data={}){

			return await http.post(this.url, data);
		}
	}
}
