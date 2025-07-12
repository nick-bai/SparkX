<template>
	<div class="chat-content-box">
		<div class="chat-msg" ref="chatContainer">
			<div class="panel" style="background: #f4f4f4" v-if="welcomeWord.title.length > 0 && chatLogList.length === 0">
				<div class="flex-x-between">
					<div class="chat-msg-content" style="width: 50px">
						<div class="chat-user">
							<div class="user-icon">
								<img :src="logo" style="width: 50px;height: 45px;"/>
							</div>
							<div class="chat-user-name"></div>
						</div>
					</div>
					<div class="answer-content">
						<div class="hello-word">
							<p style="font-size: 14px;font-weight: 500">{{ welcomeWord.title }}</p>
							<div class="hello-word-list">
								<div class="hello-word-item" v-for="(item, index) in welcomeWord.question" :key="index">{{ item.content }}</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<!-- 循环对话开始 -->
			<div class="panel"
				 :style="{background: (item.source === 'user') ? '#f4f4f4' : '#fff' }"
				 v-for="(item, index) in chatLogList"
				 :key="index">
				<div class="flex-x-between">
					<div class="chat-msg-content">
						<div class="chat-user">
							<div class="user-icon">
								<img src="/src/assets/user.png" style="width: 30px;height: 30px;" v-if="item.source === 'user'"/>
								<img :src="logo" style="width: 50px;height: 45px;" v-if="item.source === 'ai' || item.source === 'system'"/>
							</div>
							<div class="chat-user-name"></div>
						</div>
						<div class="answer-content">
							<div class="code-box flex-center">
								<div class="answer-content-wrap" style="width: 100%">
									<p v-if="item.source === 'user'">{{ item.content }}</p>
									<p v-else-if="item.source === 'system'" style="display: flex;align-items: center">{{ item.content }}
										<el-icon style="margin-left: 5px"><Loading class="rotate-loading"/></el-icon></p>
									<div v-for="(item2, index2) in item.content" :key="index2" v-else>
										<MdPreview noIconfont noPrettier :codeFoldable="false" v-model="item.content[index2].content" />
									</div>
								</div>
							</div>

							<div class="menu-list" v-if="item.source === 'ai' && item.answerIng === 3">
								<div class="menu-left-side" v-if="!debug">
									<el-tag bordered style="margin-left: 10px" v-if="setting.showTime === 1">{{ item.meta.time }} s</el-tag>
									<el-tag bordered style="margin-left: 10px" v-if="setting.showTokens === 1">{{ item.meta.totalTokens }} tokens</el-tag>
								</div>
								<div class="menu-right-side" v-if="!debug">
									<el-tooltip
										effect="dark"
										content="换一个答案"
										placement="bottom"
									>
										<el-icon size="16" style="margin-left: 10px;cursor: pointer" @click="reChat(item)"><Refresh /></el-icon>
									</el-tooltip>
									<el-tooltip
										effect="dark"
										content="复制"
										placement="bottom"
									>
										<el-icon size="16" style="margin-left: 10px;cursor: pointer" @click="copyText(item.content)"><CopyDocument /></el-icon>
									</el-tooltip>
									<el-tooltip
										v-if="setting.showAppraise === 1 && item.appraise !== 2"
										effect="dark"
										content="答的不错"
										placement="bottom"
									>
										<span class="iconfont icon-zan icon-style" @click="appraise(item, 1)" :style="{'color': item.appraise === 1 ? 'var(--el-color-theme)' : ''}"></span>
									</el-tooltip>
									<el-tooltip
										v-if="setting.showAppraise === 1 && item.appraise !== 1"
										effect="dark"
										content="还不够好"
										placement="bottom"
									>
										<span class="iconfont icon-cai icon-style" @click="appraise(item, 2)" :style="{'color': item.appraise === 2 ? 'var(--el-color-theme)' : ''}"></span>
									</el-tooltip>
									<el-tooltip
										v-if="setting.voiceOut === 1"
										effect="dark"
										content="播报"
										placement="bottom"
									>
										<span class="iconfont icon-bobao icon-style"></span>
									</el-tooltip>
								</div>
								<div class="menu-right-side" v-else>
									<el-button type="text" style="font-size: 13px" @click="showDetail(item.runtimeId)" v-if="item.runtimeId">执行详情</el-button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<el-button
				v-if="answerIng === 2"
				style="margin-top: 10px;font-size: 14px;"
				@click="stopAnswer"
				link
			>
				停止生成
			</el-button>
		</div>

		<div class="chat-area">
			<div class="input-box">
				<el-input
					resize="none"
					@keyup.enter.native="send"
					type="textarea"
					placeholder="输入你的问题或需求"
					v-model="chatMsg"
					max-length="3000"
					allow-clear
					show-word-limit
					:rows="3"
					class="no-border"/>
			</div>
			<div class="send-btn" @click="send">
				<div class="send-icon">
					<el-icon size="28" :style="{color: chatMsg.length > 0 ? 'var(--el-color-theme)' : '#909399'}" ><Promotion /></el-icon>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import {CopyDocument, Loading, Promotion, Refresh} from "@element-plus/icons-vue";
import { config, MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { fetchEventSource } from '@microsoft/fetch-event-source';
import configInfo from "@/config"
import tool from "@/utils/tool.js";

export default {
	components: {Refresh, Loading, CopyDocument, Promotion, MdPreview},
	props: {
		chatLogMsg: {
			type: Array,
			default: []
		},
		setting: {
			type: Object,
			default: () => {}
		},
		welcomeWord: {
			type: Object,
			default: () => {}
		},
		apiUrl: {
			type: String,
			default: ""
		},
		chatSessionId: {
			type: String,
			default: ""
		},
		writeLog: {
			type: Boolean,
			default: false
		},
		logo: {
			type: String,
			default: "/src/assets/logo.png"
		},
		debug: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			chatMsg: "",
			compiledMarkdown: "",
			chatLogList: [],
			answerIng: 0,
			ctrl: null,
			nowIndex: -1, // 当前交流的下表
			dialogVisible: false,
			retrievedList: [], // 召回文档列表
			sessionId: "",
			startReceive: false
		}
	},
	mounted() {
		this.sessionId = this.chatSessionId
		config({
			markdownItConfig(md) {
				md.renderer.rules.image = (tokens, idx, options, env, self) => {
					tokens[idx].attrSet('style', 'display:inline-block;min-height:33px;padding:0;margin:0')
					if (tokens[idx].content) {
						tokens[idx].attrSet('title', tokens[idx].content)
					}
					tokens[idx].attrSet(
						'onerror',
						'this.src="/src/assets/load_error.png";this.onerror=null;this.height="33px"'
					)
					return md.renderer.renderToken(tokens, idx, options)
				}
				md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
					tokens[idx].attrSet('target', '_blank')
					return md.renderer.renderToken(tokens, idx, options)
				}
				document.appendChild
			}
		})

		if (this.debug) {
			this.sessionId = "debug_" + Math.floor(Math.random() * 10) + 1
		}

		this.chatLogList = this.chatLogMsg
		this.$nextTick(() => {
			this.sliderBottom()
		})
		this.ctrl = new AbortController()
	},
	methods: {
		// 发送消息
		async send() {
			let that = this
			let data = this.setting
			if (this.chatMsg.charAt(this.chatMsg.length - 1) === '\n') {
				data.content = this.chatMsg.slice(0, -1) // 移除最后的回车符号
			} else {
				data.content = this.chatMsg
			}

			if (data.content.length === 0) {
				this.$message.error('请输入问题')
				return false
			}

			// 检测会话
			if (this.sessionId === "" && !this.debug) {
				let res2 = await this.$API.chat.createSession.post({appId: data.appId})
				if (res2.code !== 0) {
					return false
				} else {
					data.sessionId = res2.msg
				}
			} else {
				data.sessionId = this.sessionId
			}

			this.chatLogList.push({source: 'user', content: data.content });
			this.chatLogList.push({source: 'system', question: data.content, appraise: 0, content: '思考中'});
			this.chatMsg = ''
			this.answerIng = 1

			fetchEventSource(`${configInfo.API_URL}` + this.apiUrl, {
				method: 'POST',
				signal: that.ctrl.signal,
				headers: {
					'Content-Type': 'application/json',
					'Authorization': 'Bearer ' + tool.cookie.get("TOKEN")
				},
				openWhenHidden: true, // 解决浏览器tab切换重复请求问题 https://blog.csdn.net/weixin_42029374/article/details/131935713
				body: JSON.stringify({
					sessionId: data.sessionId,
					content: data.content,
					appId: data.appId
				}),
				onmessage(ev) {
					let event = ev.event
					if (event === '[START]') { // 回答开始
						that.nowIndex = that.chatLogList.length - 1
						that.chatLogList[that.nowIndex].source = 'ai'
						that.answerIng = that.chatLogList[that.nowIndex].answerIng = 2
					} else if (event === '[DONE]') { // 回答结束
						that.startReceive = false
						that.answerIng = that.chatLogList[that.nowIndex].answerIng = 3
						if (ev.data !== '') {
							that.chatLogList[that.nowIndex].meta = JSON.parse(ev.data)
						}

						if (that.sessionId === '') {
							that.$emit("sessionCreate", {sessionId: data.sessionId, title: data.content})
							that.sessionId = data.sessionId
						}

						let nowLog = that.chatLogList[that.nowIndex]
						// 插件外决定是否保存
						if (that.writeLog) {
							let meta = JSON.parse(ev.data)
							let row = {
								sessionId: data.sessionId,
								question: data.content,
								answer: nowLog.content,
								time: meta.time,
								inputTokens: meta.inputTokens,
								outputTokens: meta.outputTokens,
								totalTokens: meta.totalTokens,
								retrieved_list: nowLog.retrievedList,
								toolUse: ((nowLog.toolUse !== '') && (nowLog.toolUse !== undefined)) ? nowLog.toolUse.join(",") : ""
							}

							row.retrievedList = JSON.stringify(row.retrieved_list)
							delete row.retrieved_list
							row.appId = data.appId

							let logRes = that.$API.chat.writeLog.post(row)
							logRes.then(result => {
								that.chatLogList[that.nowIndex].logId = result.code
							})
						}

						that.sliderBottom()
					} else if (event === '[ERROR]') {
						that.nowIndex = that.chatLogList.length - 1
						that.chatLogList[that.nowIndex].source = 'ai'
						that.chatLogList[that.nowIndex].content = '系统配置出现了错误: ' + ev.data
						that.stopAnswer()
					} else if (event === '[META]') { // 通知召回数据
						that.chatLogList[that.nowIndex].retrievedList = JSON.parse(ev.data)
					} else if (event === '[LOGIN_OUT]') {
						that.nowIndex = that.chatLogList.length - 1
						that.chatLogList[that.nowIndex].source = 'ai'
						that.chatLogList[that.nowIndex].content = '登录过期，请重新登录'
						that.stopAnswer()
					} else if (event === '[TOOL]') {
						if (!that.chatLogList[that.nowIndex].toolUse) {
							that.chatLogList[that.nowIndex].toolUse = [ev.data]
						} else {
							that.chatLogList[that.nowIndex].toolUse.push(ev.data)
						}
					} else {
						if (!that.startReceive) {
							that.chatLogList[that.nowIndex].content = [] // 清理默认思考中... 提示
						}

						that.startReceive = true
						let resData = JSON.parse(ev.data)
						let has = false
						that.chatLogList[that.nowIndex].content.forEach((item, index) => {

							if (item.nodeId === resData.nodeId) {
								has = true
								that.chatLogList[that.nowIndex].content[index] = {
									nodeId: resData.nodeId,
									content: item.content + resData.content.replace("-_-_wrap_-_-", "\n")
								}
							}
						})

						if (!has) {
							that.chatLogList[that.nowIndex].runtimeId = resData.runtimeId
							that.chatLogList[that.nowIndex].content.push({
								nodeId: resData.nodeId,
								content: resData.content.replace("-_-_wrap_-_-", "\n")
							})
						}

						that.sliderBottom()
					}
				},
				onclose() {
					console.log('Connection closed by server')
				},
				onerror(err) {
					console.log('错误原因', err)
					that.$message.error(err)
					throw new Error("终止连接")
				},
			});
		},
		stopAnswer() {
			this.ctrl.abort()
			this.answerIng = 0
		},
		sliderBottom() {
			this.$nextTick(() => {
				this.$refs.chatContainer.scrollTop = this.$refs.chatContainer.scrollHeight;
			});
		},
		// 复制内容
		copyText(text) {
			navigator.clipboard.writeText(text).then(() => {
				this.$message.success('复制成功')
			}).catch(error => {
				console.log('错误', error)
				this.$message.error('复制错误')
			});
		},
		// 评价
		async appraise(row, type) {
			if (row.appraise !== 0) {
				type = 0
			}

			let res = await this.$API.chat.appraise.post({
				sessionId: this.sessionId,
				logId: row.logId,
				appraise: type
			})
			if (res.code !== 0) {
				this.$message.error(res.msg)
			} else {
				this.chatLogList[this.nowIndex].appraise = type
			}
		},
		// 换一个答案
		reChat(row) {
			this.chatMsg = row.question + '\n'
			this.send()
		},
		// 显示详情
		showDetail(runtimeId) {
			this.$emit('showDetail', runtimeId)
		}
	}
}
</script>

<style>
.no-border .el-textarea__inner {
	box-shadow: none !important; /* 使用 !important 来确保覆盖默认样式 */
	padding: 13px !important;
}
.md-editor-preview {
	font-size: 14px !important;
}
</style>

<style lang="scss" scoped>
.chat-content-box {
	width: 100%;
	height: 100%;
	margin: 0 auto;

	.chat-msg {
		height: calc(100% - 83px);
		width: 100%;
		overflow-y: scroll;
		overflow-x: hidden;
		padding-bottom: 20px;

		.panel {
			background: #fff;
			padding: 20px;
			border-radius: 5px;

			.flex-x-between {
				display: flex;
				align-items: flex-start;
				justify-content: space-between;

				.chat-msg-content {
					display: flex;
					justify-content: space-between;
					width: 100%;
					.chat-user {
						display: flex;
					}

					.chat-user-name {
						margin-left: 10px;
						font-weight: 700;
						font-size: 15px;
					}

					.user-icon {
						width: 40px;
						height: 40px;
						display: flex;
						justify-content: center;
						align-items: center;
					}
				}
			}

			.answer-content {
				gap: 0;
				overflow: hidden;
				display: flex;
				flex-direction: column;
				flex: 1;
				width: 100%;

				.code-box {
					padding-left: 8px;
					margin-top: 3px;
					overflow: hidden;
					flex: 1;
				}

				.answer-content-wrap {
					font-style: normal;
					font-size: 14px;
					line-height: 1.5;
					word-wrap: break-word;
				}
			}
		}
	}
}

.chat-area {
	width: 100%;
	height: 88px;
	border-radius: 5px;
	border: 1px solid var(--color-border-3);
	background: #fff;
	display: flex;
	.input-box {
		width: 95%;
	}
}
.send-btn {
	width: 100px;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;

	.send-icon {
		width: 45px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		background: rgb(var(--primary-5));
		border-radius: 50%;
		height: 45px;
	}
}
.chat-msg::-webkit-scrollbar { width: 0 !important }
.chat-msg { -ms-overflow-style: none; }
.icon-style {
	font-size: 16px;margin-left: 10px;cursor: pointer;
	color: #3f4a54;
}
.menu-left-side {
	display: flex;
	align-items: center;
}
.menu-right-side {
	display: flex;
	align-items: center;
	float: right;
}
.hello-word {
	width: 100%;
	background-image: linear-gradient(137deg, rgb(229, 244, 255) 0%, rgb(239, 231, 255) 100%);
	border: 0;
	display: flex;
	flex-direction: column;
	padding: 20px 30px;
	border-radius: 5px;
}
.hello-word-list {
	width: 100%;
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	margin-top: 10px;
}
.hello-word-item {
	display: flex;
	align-items: center;
	padding: 10px;
	font-size: 13px;
	background: rgba(255, 255, 255, 0.45);
	border: 1px solid rgb(255, 255, 255);
	margin-right: 10px;
	margin-bottom: 10px;
	cursor: pointer;
}
.menu-list {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-top: 10px;
}

@keyframes rotate {
	from {
		transform: rotate(0deg);
	}
	to {
		transform: rotate(360deg);
	}
}

.rotate-loading {
	animation: rotate 2s linear infinite;
}
.relation-item {
	background: #fff;
	border-radius: 5px;
	padding: 10px;
	margin-top: 10px;
	margin-left: 20px;
}
.relation-item .item-box {
	width: 100%;
	height: 200px;
	overflow-y: scroll;
}
.item-box::-webkit-scrollbar  {
	width: 0 !important;
}
</style>
