<template>
	<div class="container">
		<div class="chat-box">
			<el-row style="width: 100%; height: 100%">
				<el-col :span="3" class="left-side">
					<div style="padding: 20px">
						<div class="logo">
							<img :src="logo" style="width: 30px; height: 30px" alt="" />
							<span class="font-weight-700">{{ title }}</span>
						</div>
						<div class="chat-tool" style="margin-bottom: 10px">
							<div class="flex-center new-chat btn-color" @click="createNewSession">
								<el-icon><Plus /></el-icon>
								<span style="margin-left: 5px">新对话</span>
							</div>
						</div>

						<div class="flex-center log-item-box" v-for="(item, index) in sessionLog" :key="item.sessionId"
							 :class="{'item-active': nowSessionId === item.sessionId}" @mouseover="hoverIndex = index"
							 @mouseleave="hoverIndex = -1"
							 @click="checkSession(item.sessionId)">
							<div class="log-item line1">{{ item.title }}</div>
							<el-icon style="cursor: pointer" @click="delSession(item.sessionId)" v-if="hoverIndex === index">
								<Delete />
							</el-icon>
						</div>
					</div>
				</el-col>
				<el-col :span="21" class="right-side" style="padding: 50px 20%">
					<chat-box
						:logo="logo"
						:setting="setting"
						:chat-log-msg="chatLogMsg"
						:welcome-word="welcomeWord"
						:chat-session-id="nowSessionId"
						:key="randomKey"
						:write-log="true"
						api-url="/application/sseChat"
						@sessionCreate="sessionCreate">
					</chat-box>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
import chatBox from '@/components/chatContent/index.vue'
import {Delete, Plus} from "@element-plus/icons-vue";
import config from "@/config"

export default {
	components: {Delete, Plus, chatBox},
	data() {
		return {
			setting: {},
			welcomeWord: {
				title: '',
				question: []
			},
			chatLogMsg: [],
			sessionLog: [],
			accessToken: "",
			title: '', // 应用标题
			randomKey: Math.random(),
			nowSessionId: "",
			hoverIndex: -1,
			domain: config.API_URL.replace("/api", ""),
			logo: "",
			debug: false,
			appId: ""
		}
	},
	mounted() {
		this.accessToken = this.$route.params.token
		this.debug = this.$route.query.debug ?? false
		this.getChatInfo()
	},
	methods: {
		// 获取应用聊天详情
		async getChatInfo() {
			let res = await this.$API.chat.getInfo.get({accessToken: this.accessToken, debug: this.debug})
			if (res.code === 0) {
				document.title = res.data.name

				let appInfo = res.data
				if (appInfo.prologue !== '') {
					this.welcomeWord = JSON.parse(appInfo.prologue)
					appInfo.prologue = JSON.parse(appInfo.prologue)
				}
				this.setting = appInfo
				this.appId = appInfo.appId
				this.randomKey = Math.random()
				this.title = appInfo.name
				if (appInfo.icon === '/icons/default_logo.png') {
					this.logo = this.domain + appInfo.icon
				} else {
					this.logo = appInfo.icon
				}

				// 非调试模式的部署模式
				if (!this.debug) {
					this.authLogin()
				} else {
					this.getSessionList()
				}
			} else {
				if (res.msg === '登录过期') {
					this.$router.push('/login')
				}
				this.$message.error(res.msg)
			}
		},
		// 非调试模式下的鉴权
		async authLogin() {
			let customerId = localStorage.getItem("customerId")
			let res = await this.$API.auth.authLogin.post({token: this.accessToken, customerId: customerId})
			if (res.code === 0) {
				if (res.data.resetToken) {
					this.$TOOL.cookie.set("TOKEN", res.data.token, {
						expires: 24 * 60 * 60
					})
					localStorage.setItem("customerId", res.data.customerId)
				}

				this.getSessionList()
			} else {
				this.$message.error(res.msg)
			}
		},
		// 获取会话列表
		async getSessionList() {
			let res = await this.$API.chat.getSessionList.get({accessToken: this.accessToken, debug: this.debug})
			if (res.code === 0) {
				this.sessionLog = res.data
			}
		},
		// 新建会话
		async sessionCreate(row) {
			this.nowSessionId = row.sessionId
			let res = await this.$API.chat.updateSession.post(row)
			if (res.code === 0) {
				this.getSessionList()
			}
		},
		// 删除会话
		async delSession(sessionId) {
			let res = await this.$API.chat.delSession.get({sessionId: sessionId})
			if (res.code === 0) {
				this.getSessionList()
				// 删除当前的会话session
				if (sessionId === this.nowSessionId) {
					this.createNewSession()
				}
			} else {
				this.$message.error(res.msg)
			}
		},
		// 创建新的会话
		createNewSession() {
			this.nowSessionId = ''
			this.chatLogMsg = []
			this.randomKey = Math.random()
		},
		// 选择会话
		async checkSession(sessionId) {
			this.nowSessionId = sessionId
			this.randomKey = Math.random()
			let res = await this.$API.chat.getChatLog.get({sessionId: sessionId})
			if (res.code === 0) {

				let chatLogList = []
				res.data.forEach((item) => {

					chatLogList.push({
						appId: item.appId,
						sessionId: item.sessionId,
						source: 'user',
						content: item.question
					})

					chatLogList.push({
						logId: item.logId,
						appId: item.appId,
						sessionId: item.sessionId,
						source: 'ai',
						content: item.answer,
						appraise: item.appraise,
						meta: {
							time: item.time,
							totalTokens: item.totalTokens,
						},
						retrievedList: item.retrievedList !== '' ? JSON.parse(item.retrievedList) : '',
						answerIng: 3,
						toolUse: item.toolUse
					})
				})

				this.chatLogMsg = chatLogList
				this.randomKey = Math.random()
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	background-color: #f4f4f4;
	padding: 16px 20px 0;
	display: flex;
}
.chat-box {
	width: 100%;
	height: calc(100vh - 30px);
	background: #fff;
	border-radius: 10px;
}
.left-side {
	height: 100%;
	border-right: 1px solid #f4f4f4;
}
.right-side {
	background: #f4f4f4;
	height: 100%;
}
.logo {
	display: flex;
	justify-content: left;
	align-items: center;

	span {
		margin-left: 10px;
		font-weight: 700;
	}
}
.chat-tool:hover {
	background: var(--el-color-theme);
	color: #fff;
}
.chat-tool {
	margin-top: 30px;
	width: 100%;
	display: flex;
	align-items: center;
	border: 1px solid var(--el-color-theme);
	border-radius: 5px;
	justify-content: center;
	cursor: pointer;
	padding: 8px 0;
}
.btn-color {
	color: rgb(var(--primary-5));
}
.menu-list {
	display: flex;
	align-items: center;
	width: 100%;
	justify-content: space-between;
	border-top: 1px solid #dee0e3;
	padding-top: 10px;
}
.menu-item {
	margin-left: 10px;cursor: pointer;stroke-width: 3;
}
.log-item {
	height: 40px;
	width: 90%;
	display: flex;
	align-items: center;
	cursor: pointer;
	font-weight: 500;
	color: #1f2329;
	padding-left: 10px;
}
.log-item-box {
	padding-right: 10px;
}
.log-item-box:hover {
	color: var(--el-color-theme);
	background: #eee7fd;
}
.item-active {
	color: var(--el-color-theme);
	background: #eee7fd;
}
</style>

