<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div style="width: 100%;height: 40px;">
			<el-button type="primary" style="float: right" @click="saveApp(2)">保存并发布</el-button>
			<el-button style="float: right;margin-right: 20px" @click="saveApp(1)">仅保存</el-button>
		</div>
		<el-row class="setting-div">
			<el-col :span="10" class="setting-div-setting">
				<el-form ref="form" :model="form" :rules="rules" label-position="top" label-width="80px" style="padding: 10px 20px">
					<el-form-item label="应用名称" prop="name">
						<div class="flex-center" style="width: 100%">
							<el-input v-model="form.name" maxlength="25" show-word-limit style="width: calc(100% - 80px)"></el-input>
							<ul class="img-list" style="margin-left: 10px">
								<li v-if="form.icon">
									<img :src="form.icon" alt="图片" style="width: 58px;height: 58px">
									<div class="img-tools" @click="delImg">
										<el-icon color="#fff">
											<Delete />
										</el-icon>
									</div>
								</li>
								<li v-else>
									<el-upload
										class="upload-demo"
										:action="uploadImgUrl"
										:headers="header"
										:on-success="handleUploadSuccess"
										:show-file-list="false"
										:limit="1"
									>
										<div class="addImg">
											<el-icon>
												<Plus />
											</el-icon>
										</div>
									</el-upload>
								</li>
							</ul>
						</div>
					</el-form-item>
					<el-form-item label="应用描述" prop="description">
						<el-input type="textarea" v-model="form.description" rows="3" maxlength="255" show-word-limit></el-input>
					</el-form-item>
					<el-form-item v-if="form.type === 1">
						<template #label>
							<div class="flex-center">
								<div><span style="color: var(--el-color-danger);">*</span> AI模型</div>
								<div class="flex-center setting-btn">
									<el-button
										icon="el-icon-Setting"
										type="primary"
										link
										@click="dialogVisible = true"
										:disabled="!modelId"
									>
										参数
									</el-button>
								</div>
							</div>
						</template>
						<el-cascader
							v-model="modelId"
							:options="options"
							:show-all-levels="false"
							style="width: 100%"
							@change="handleChange"
							clearable>
						</el-cascader>
					</el-form-item>
					<el-form-item v-if="showTools">
						<template #label>
							<div class="flex-center" style="width: 100%">
								<div>使用的插件</div>
								<div class="flex-center setting-btn">
									<div class="flex-center setting-btn" style="margin-left: 10px">
										<el-button
											icon="el-icon-Plus"
											type="primary"
											link
											@click="toolsVisible = true"
										>
											选择
										</el-button>
									</div>
								</div>
							</div>
						</template>
						<div class="dataset-list" v-if="relationToolList.length > 0">
							<div class="dataset-item" v-for="(item, index) in relationToolList" :key="index">
								<div class="dataset-item-div">
									<el-icon size="20" color="var(--el-color-theme)" style="margin-right: 5px">
										<ElementPlus />
									</el-icon>
									<div class="line1">{{ item.title }}</div>
								</div>
								<el-icon style="margin-left: 5px" @click="delTool(index)">
									<Delete />
								</el-icon>
							</div>
						</div>
						<div class="notice-dataset" v-else>请选择使用的插件</div>
					</el-form-item>
					<el-form-item v-if="form.type === 1">
						<template #label>
							设定角色
							<el-tooltip effect="dark" content="例如: 你是一位资深的数据分析专员，请根据用户提出的数据,给出最专业的解答,要求回答言简意赅" placement="top-start">
								<el-icon size="16"><InfoFilled /></el-icon>
							</el-tooltip>
						</template>
						<el-input type="textarea" v-model="form.prompt" rows="4" maxlength="1000" show-word-limit></el-input>
					</el-form-item>
					<el-form-item v-if="form.type === 1">
						<template #label>
							<div class="flex-center" style="width: 100%">
								<div>关联知识库</div>
								<div class="flex-center setting-btn">
									<div class="flex-center setting-btn">
										<el-button
											icon="el-icon-Setting"
											type="primary"
											link
											@click="setParams"
										>
											参数
										</el-button>
									</div>

									<div class="flex-center setting-btn" style="margin-left: 10px">
										<el-button
											icon="el-icon-Plus"
											type="primary"
											link
											@click="datasetVisible = true"
										>
											添加
										</el-button>
									</div>
								</div>
							</div>
						</template>
						<div class="dataset-list" v-if="relationDataList.length > 0">
							<div class="dataset-item" v-for="(item, index) in relationDataList" :key="index">
								<div class="dataset-item-div">
									<el-icon size="20" color="var(--el-color-theme)" style="margin-right: 5px">
										<Document />
									</el-icon>
									<div class="line1">{{ item.title }}</div>
								</div>
								<el-icon style="margin-left: 5px" @click="delDataset(index)">
									<Delete />
								</el-icon>
							</div>
						</div>
						<div class="notice-dataset" v-else>请选择关联的知识库</div>
					</el-form-item>
					<el-form-item label="开场白标题">
						<el-input v-model="welcomeList.title" maxlength="155" show-word-limit></el-input>
					</el-form-item>
					<el-form-item label="开场白问题">
						<div class="question-list">
							<div class="question-item" v-for="(item, index) in welcomeList.question" :key="index">
								<el-input v-model="item.content" maxlength="155" show-word-limit style="width: calc(100% - 40px)"></el-input>
								<el-icon class="delete-icon" @click="delQuestion(index)">
									<Delete />
								</el-icon>
							</div>
						</div>
						<el-button
							@click="addQuestion"
							icon="el-icon-Plus"
							type="primary"
							link
						>
							添加问题
						</el-button>
					</el-form-item>
				</el-form>
				<div class="setting-box-list">
					<div class="setting-box">
						<div class="setting-title">显示引用片段</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.showRelation">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示耗时</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.showTime">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示消耗token</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.showTokens">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">显示评价</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.showAppraise">
						</el-switch>
					</div>
					<!--<div class="setting-box">
						<div class="setting-title">语音输入</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.voiceInput">
						</el-switch>
					</div>
					<div class="setting-box">
						<div class="setting-title">语音输出</div>
						<el-switch
							:active-value="1"
							:inactive-value="2"
							v-model="form.voiceOut">
						</el-switch>
					</div>-->
				</div>
			</el-col>
			<el-col :span="14" style="background: #f4f4f4;padding: 20px;height: 100%">
				<chat-box
					:welcome-word="welcomeList"
					:key="chatBoxKey"
					:setting="form"
					:logo="form.icon"
					:chat-session-id="Math.random()"
					api-url="/application/sseChat">
				</chat-box>
			</el-col>
		</el-row>
	</div>

	<!-- 模型设置 -->
	<el-dialog title="AI参数设置" v-model="dialogVisible" width="500px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="form" ref="ruleForm" label-width="120px">
			<el-form-item>
				<template #label>
					温度
					<el-tooltip effect="dark" content="较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定" placement="top-start">
						<el-icon size="16"><InfoFilled /></el-icon>
					</el-tooltip>
				</template>
				<el-slider
					v-model="form.temperature"
					:min="0"
					:step="0.01"
					:max="1"
					show-input>
				</el-slider>
			</el-form-item>
			<el-form-item label="历史聊天记录数">
				<el-slider
					v-model="form.memoryNum"
					:min="0"
					:max="10"
					show-input>
				</el-slider>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="dialogVisible = false">确 定</el-button>
			</div>
		</template>
	</el-dialog>

	<el-dialog title="选择知识库" v-model="datasetVisible" width="800px" destroy-on-close :close-on-click-modal="false" class="select-dataset">
		<dataset-dialog @success="handleSuccess" @doClose="datasetVisible=false" :dataset-ids="relationDataIds"></dataset-dialog>
	</el-dialog>
	<!-- 参数设置 -->
	<save-dialog
		v-if="paramsVisible"
		ref="paramsDialog"
		@success="handleDatasetSuccess"
		@closed="paramsVisible=false"
		:close-on-click-modal="false">
	</save-dialog>
	<!-- 关联插件 -->
	<el-dialog title="关联插件" v-model="toolsVisible" width="800px" destroy-on-close :close-on-click-modal="false" class="select-dataset">
		<tools-dialog @success="handleToolsSuccess" @doClose="toolsVisible=false" :tool-ids="relationToolIds"></tools-dialog>
	</el-dialog>
</template>

<script>
import chatBox from '@/components/chatContent/index.vue'
import {Plus, Setting, Document, Delete, InfoFilled, ElementPlus} from "@element-plus/icons-vue"
import datasetDialog from "@/components/dataset/multiple.vue"
import saveDialog from "@/views/index/dialog/params.vue"
import toolsDialog from "@/components/tools/index.vue"
import config from "@/config"
import tool from "@/utils/tool"

export default {
	components: {ElementPlus, saveDialog, datasetDialog, InfoFilled, Document, Plus, chatBox, Setting, Delete, toolsDialog},
	props: {
		accessToken: {
			type: String,
			default: ""
		}
	},
	data() {
		return {
			domain: config.API_URL.replace("/api", ""),
			form: {},
			rules: {
				name: [
					{required: true, message: '应用标题不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '应用不能为空', trigger: 'blur'}
				]
			},
			options: [],
			dialogVisible: false,
			relationDataList: [], // 关联的知识库
			relationDataIds: [],
			welcomeList: {
				title: "您好，我是xxx小助手。",
				question: [
					{"content": "请问xxx怎么购买"},
					{"content": "请问xxx怎么使用"},
				]
			},
			chatBoxKey: Math.random(),
			datasetVisible: false,
			paramsVisible: false,
			modelId: [],
			uploadImgUrl: config.API_URL + "/index/upload",
			header: {
				Authorization:
					config.TOKEN_PREFIX + tool.cookie.get("TOKEN"),
			},
			toolsVisible: false,
			showTools: false,
			relationToolIds: [],
			relationToolList: []
		}
	},
	mounted() {
		this.getInfo()
		this.getModelsList()
	},
	methods: {
		// 获取应用详情
		async getInfo() {
			let res = await this.$API.application.chatDetail.get({accessToken: this.accessToken})
			this.form = res.data
			if (res.data.prologue !== '') {
				this.welcomeList = this.form.prologue = JSON.parse(res.data.prologue)
			}

			if (this.form.icon === '/icons/default_logo.png') {
				this.form.icon = this.domain + '/icons/default_logo.png'
			}

			if (res.data.datasetList && res.data.datasetList.length > 0) {
				this.relationDataList = res.data.datasetList
				this.relationDataIds = []
				this.relationDataList.forEach(item => {
					this.relationDataIds.push(item.datasetId)
				})
			}

			if (res.data.toolList && res.data.toolList.length > 0) {
				this.relationToolList = res.data.toolList
				this.relationToolIds = []
				this.relationToolList.forEach(item => {
					this.relationToolIds.push(item.id)
				})
			}
			this.showTools = res.data.showTools

			this.modelId = [res.data.modelId, res.data.modelName]
		},
		// 删除关联的知识库
		delDataset(index) {
			this.relationDataList.splice(index, 1)
			this.relationDataIds = []
			this.relationDataList.forEach(item => {
				this.relationDataIds.push(item.datasetId)
			})
		},
		// 删除插件
		delTool(index) {
			this.relationToolList.splice(index, 1)
			this.relationToolIds = []
			this.relationToolList.forEach(item => {
				this.relationToolIds.push(item.id)
			})
		},
		// 删除问题
		delQuestion(index) {
			this.welcomeList.question.splice(index, 1)
		},
		// 添加问题
		addQuestion() {
			this.welcomeList.question.push({content: ""})
		},
		// 选择了知识库
		handleSuccess(row) {
			this.relationDataList = row
			this.relationDataIds = []
			row.forEach(item => {
				this.relationDataIds.push(item.datasetId)
			})
			this.datasetVisible = false
		},
		// 设置知识库参数
		setParams() {
			this.paramsVisible = true

			this.$nextTick(() => {
				this.$refs.paramsDialog.open().setData(this.form)
			})
		},
		// 完成参数设定
		handleDatasetSuccess(row) {
			this.form = row
			this.paramsVisible = false
		},
		// 获取模型列表
		async getModelsList() {
			let res = await this.$API.models.list.get({type: 1, status: 1})
			this.options = []
			res.data.forEach(item => {

				let info = {
					label: item.name,
					value: item.modelId,
					children: []
				}
				let option = []
				item.models.split(",").forEach(item => {
					option.push({
						label: item,
						value: item
					})
				})
				info.children = option

				this.options.push(info)
			})
		},
		// 保存应用
		async saveApp(type) {

			// 如果是保存并发布的话，则需要校验参数
			if (type === 2) {

				if (this.form.type === 1 && (this.modelId[0] === '' || this.modelId[1] === '')) {
					this.$message.error('请设置AI模型')
					return
				}

				// 如果是流程编排模式，未编排流程，不允许发布
				if (this.form.type === 2) {
					let flowRes = await this.$API.workflow.info.get({appId: this.form.appId})
					if (!flowRes.data.flowData) {
						this.$message.error('请完成编排才能发布')
						setTimeout(() => {
							this.$router.push('/workflow/index?appId=' + this.form.appId)
						}, 1000)
						return
					}
				}
			}

			// 保存类型
			this.form.saveType = type

			// 组装关联的知识库
			let relationData = []
			this.relationDataList.forEach(item => {
				relationData.push({
					datasetId: item.datasetId,
					title: item.title
				})
			})
			this.form.datasetList = relationData

			// 组装关联的插件
			let relationToolData = []
			this.relationToolList.forEach(item => {
				relationToolData.push(item)
			})
			this.form.toolList = relationToolData

			// 组装开场白
			this.form.prologue = this.welcomeList

			this.form.modelId = this.modelId[0]
			this.form.modelName = this.modelId[1]

			let res = await this.$API.application.save.post(this.form)
			if (res.code === 0) {
				this.$message.success(res.msg)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 选择了模型
		async handleChange(row) {
			if (!row) {
				return
			}

			let res = await this.$API.models.info.get({modelId: row[0]})
			let options = JSON.parse(res.data.options)

			options.forEach(item => {
				if (item.field === 'temperature') {
					this.form.temperature = item.value
				}

				if (item.field === 'maxOutputInput') {
					this.form.maxReplyToken = item.value
				}
			})

			// 该模型是否支持插件
			this.relationToolList = []
			if (res.data.functionCalling !== '') {
				this.showTools = true
			} else {
				this.showTools = false
			}
		},
		// 选择了插件
		handleToolsSuccess(row) {
			this.relationToolList = row
			this.relationToolIds = []
			row.forEach(item => {
				this.relationToolIds.push(item.id)
			})
			this.toolsVisible = false
		},
		// 删除logo
		delImg() {
			this.form.icon = ''
		},
		// 上传文件
		handleUploadSuccess(res) {
			if (res.code !== 0) {
				this.$message.error(res.msg)
				return
			}
			this.form.icon = this.domain + res.msg
		}
	}
}
</script>
<style>
.setting-div .el-form-item__label {
	width: 100%;
}
</style>
<style scoped>
.flex-center {
	display: flex;align-items: center;justify-content: space-between
}
.setting-btn {
	cursor: pointer;
	color: var(--el-color-theme);
}
.dataset-list {
	width: 100%;
	display: flex;
	flex-wrap: wrap;
}
.dataset-item {
	width: 30%;
	height: 50px;
	display: flex;
	align-items: center;
	padding: 20px;
	border: 1px solid #ddd;
	border-radius: 5px;
	font-size: 13px;
	cursor: pointer;
	margin-bottom: 10px;
	margin-right: 10px;
}
.dataset-item-div {
	width: calc(100% - 20px);
	display: flex;
	align-items: center;
}
.setting-box-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
}
.setting-box {
	width: 40%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin: 0 auto;
	margin-bottom: 10px;

}
.setting-div {
	height: calc(100vh - 160px);
}
.setting-div-setting {
	height: calc(100vh - 160px);
	overflow-y: scroll;
}
.question-list {
	display: flex;
	flex-direction: column;
	width: 100%;
}
.question-item {
	width: 100%;
	display: flex;
	align-items: center;
	margin-bottom: 10px;
}
.delete-icon {
	margin-left: 20px;
	cursor: pointer;
}
.notice-dataset {
	background: #f4f4f4;
	width: 100%;
	height: 40px;
	text-align: center;
	line-height: 40px;
	color: #646a73;
	font-size: 13px;
}
.img-list li:first-child {
	margin-left: 0;
}
.img-list li {
	width: 58px;
	height: 58px;
	float: left;
	margin-left: 5px;
	cursor: pointer;
	position: relative;
}
.addImg {
	height: 56px;
	width: 56px;
	line-height: 56px;
	text-align: center;
	border: 1px dashed rgb(221, 221, 221);
}
ul li {list-style: none}
.img-list .img-tools {
	position: absolute;
	width: 58px;
	height: 15px;
	line-height: 15px;
	text-align: center;
	top: 43px;
	background: rgba(0, 0, 0, 0.6);
	cursor: pointer;
}
</style>
