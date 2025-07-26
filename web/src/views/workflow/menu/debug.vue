<template>
	<div class="customer-chat-box">
		<div class="header">
			<div class="title-box">
				<div class="title-label">{{ title.substring(0, 1) }}</div>
				<div class="title">
					{{ title }}
				</div>
			</div>
			<el-icon style="cursor: pointer" size="18" @click="$emit('closeDebug')">
				<Close />
			</el-icon>
		</div>
		<div class="content">
			<flow-chat
				:logo="logo"
				:setting="setting"
				:welcome-word="welcomeWord"
				:key="randomKey"
				:debug="true"
				api-url="/application/sseChat"
				@show-detail="showDetailHandle"
			>
			</flow-chat>
		</div>
	</div>
</template>

<script>
import {Close} from "@element-plus/icons-vue"
import flowChat from "@/components/chatContent/flow.vue"
import chatBox from "@/components/chatContent/index.vue"
import config from "@/config"

export default {
	components: {chatBox, Close, flowChat},
	props: {
		accessToken: {
			type: String,
			default: "",
		}
	},
	data() {
		return {
			setting: {},
			welcomeWord: {
				title: '',
				question: []
			},
			logo: "",
			randomKey: Math.random(),
			title: "",
			domain: config.API_URL.replace("/api", ""),
		}
	},
	mounted() {
		this.getChatInfo()
	},
	methods: {
		// 获取应用聊天详情
		async getChatInfo() {
			let res = await this.$API.chat.getInfo.get({accessToken: this.accessToken, debug: true})
			if (res.code === 0) {
				let appInfo = res.data
				if (appInfo.prologue !== '') {
					this.welcomeWord = JSON.parse(appInfo.prologue)
					appInfo.prologue = JSON.parse(appInfo.prologue)
				}
				this.setting = appInfo
				this.randomKey = Math.random()
				this.title = appInfo.name
				if (appInfo.icon === '/icons/default_logo.png') {
					this.logo = this.domain + appInfo.icon
				} else {
					this.logo = appInfo.icon
				}
			}
		},
		showDetailHandle(runtimeId) {
			this.$emit("showDetail", runtimeId)
		}
	}
}
</script>

<style scoped>
.customer-chat-box {
	z-index: 1999;
	border-radius: 8px;
	border: 1px solid #ffffff;
	background: rgb(244, 244, 244);
	box-shadow: 0 4px 8px #1f23291a;
	position: fixed;
	bottom: 16px;
	right: 16px;
	overflow: hidden;
	width: 450px;
	height: 600px;
}
.header {
	width: 100%;
	height: 56px;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 16px;
}
.title-box {
	display: flex;
	align-items: center;
}
.header .title {
	font-size: 14px;
	margin-left: 10px;
}
.title-label {
	background: var(--el-color-theme);
	color: #fff;
	border-radius: 8px;
	height: 35px;
	width: 35px;
	line-height: 35px;
	text-align: center;
	font-weight: bold;
}
.customer-chat-box .content {
	padding: 10px 20px;
	width: 100%;
	height: calc(100% - 56px);
}
</style>
