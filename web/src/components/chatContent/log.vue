<template>
	<div class="chat-content-box">
		<div class="chat-msg" ref="chatContainer">
			<!-- 循环对话开始 -->
			<div class="panel" :style="{background: (item.source === 'user') ? '#f4f4f4' : '#fff' }" v-for="(item, index) in chatLogList" :key="index">
				<div class="flex-x-between">
					<div class="chat-msg-content">
						<div class="chat-user">
							<div class="user-icon">
								<img src="/src/assets/user.png" style="width: 30px;height: 30px;" v-if="item.source === 'user'"/>
								<img src="/src/assets/logo.png" style="width: 30px;height: 30px;" v-if="item.source === 'ai' || item.source === 'system'"/>
							</div>
							<div class="chat-user-name"></div>
						</div>
						<div class="answer-content">
							<div class="code-box flex-center">
								<div class="answer-content-wrap" style="width: 100%">
									<p v-if="item.source === 'user'">{{ item.content }}</p>
									<MdPreview v-else noIconfont noPrettier :codeFoldable="false" v-model="item.content"/>
								</div>
							</div>

							<div class="menu-list">
								<div class="menu-right-side">
									<el-tooltip
										effect="dark"
										content="复制"
										placement="bottom"
									>
										<el-icon size="16" style="margin-left: 10px;cursor: pointer" @click="copyText(item.content)"><CopyDocument /></el-icon>
									</el-tooltip>
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import {CopyDocument} from "@element-plus/icons-vue";
import { config, MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

export default {
	components: { CopyDocument, MdPreview},
	props: {
		sessionId: {
			type: String,
			default: ""
		}
	},
	data() {
		return {
			chatLogList: []
		}
	},
	mounted() {
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

		this.getChatLog()
		this.$nextTick(() => {
			this.sliderBottom()
		})
	},
	methods: {
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
		// 获取聊天记录
		async getChatLog() {
			let res = await this.$API.chat.getChatLog.get({sessionId: this.sessionId})
			if (res.code === 0) {

				let chatLogDataList = []
				res.data.forEach((item) => {

					chatLogDataList.push({
						appId: item.appId,
						sessionId: item.sessionId,
						source: 'user',
						content: item.question
					})

					chatLogDataList.push({
						appId: item.appId,
						sessionId: item.sessionId,
						source: 'ai',
						content: item.answer,
						appraise: item.appraise,
						meta: {
							time: item.time,
							tokens: item.tokens,
						},
						retrievedList: JSON.parse(item.retrievedList),
						answerIng: 3
					})
				})

				this.chatLogList = chatLogDataList
			}
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
	background: #f4f4f4;
	padding: 10px;
	margin: 0 auto;
	overflow-y: scroll;

	.chat-msg {
		height: calc(100% - 83px);
		width: 100%;
		overflow-y: scroll;
		overflow-x: hidden;
		padding-bottom: 20px;

		.panel {
			background: #fff;
			padding: 10px;
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
.item-box::-webkit-scrollbar  {
	width: 0 !important;
}
</style>
