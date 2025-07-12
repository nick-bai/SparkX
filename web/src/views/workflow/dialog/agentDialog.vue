<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`agent-node-icon`)"/>
			</div>
		</div>

		<div class="set-content-box">
			<div>输入参数</div>
			<div class="flex-center" style="margin-top: 10px;">
				<div>查询内容</div>
				<el-cascader
					v-model="inputData"
					:options="inputOptions"
					@change="inputChange"
					style="margin-left: 20px;width: calc(100% - 80px)" clearable>
					<template #default="{ node, data }">
						<div class="flex-center">
							<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
							<span style="margin-left: 5px">{{ data.label }}</span>
						</div>
					</template>
				</el-cascader>
			</div>
		</div>

		<div class="set-content-box">
			<div>输出参数</div>
			<div class="param-data">
				<div class="flex-center data-item" v-for="(item, index) in form.outData" :key="index">
					<div class="flex-center">
						<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
							<span class="iconfont icon-bianliang" style="font-size: 16px !important;"></span>
						</div>
						<div class="title" style="margin-left: 10px">{{ item.field }}</div>
					</div>
					<div class="field">{{ item.name }}</div>
				</div>
			</div>
		</div>

		<div class="set-content-box">
			<div>Agent</div>
			<div class="param-data">
				<div class="flex-center" v-if="form.agentId !== ''">
					<div class="flex-center app-item">
						<img :src="domain + form.agentLogo" style="width: 45px;height: 45px;"
							 v-if="form.agentLogo === '/icons/default_logo.png'"/>
						<img :src="form.agentLogo" style="width: 45px;height: 45px;" v-else/>
						<span class="node-name">{{ form.agentName }}</span>
					</div>
				</div>
				<div class="no-param flex-center-all" style="background: #f4f4f4;margin-right: 10px;">
					<div class="flex-center" @click="addAgent">
						<el-icon style="margin-top: 3px">
							<Plus />
						</el-icon>
						<div style="margin-left: 5px">选择Agent</div>
					</div>
				</div>
			</div>
		</div>

		<el-dialog
			title="选择Agent"
			v-model="dialogVisible"
			width="800px"
			destroy-on-close
			:close-on-click-modal="false"
			class="select-dataset">
			<agent-dialog @success="handleSuccess" @doClose="dialogVisible=false" :type="1" :exclude="form.appId"></agent-dialog>
		</el-dialog>
	</div>

</template>

<script>

import {MoreFilled, Plus} from "@element-plus/icons-vue"
import agentDialog from "@/components/application/index.vue"
import config from "@/config"
import {iconComponent} from "@/views/workflow/icons/index.js"

export default {
	components: {agentDialog, Plus, MoreFilled},
	props: {
		formData: {
			type: Object,
			default: () => {}
		},
		inputOptions: {
			type: Array,
			default: []
		}
	},
	data() {
		return {
			dialogVisible: false,
			form: {},
			inputData: [], // 入参
			domain: config.API_URL.replace("/api", ""),
		}
	},
	created() {
		this.form = this.formData
		this.inputData = this.formData.inputData
	},
	methods: {
		// 输入选择
		inputChange(val) {
			this.form.inputData = val
			this.$emit("dataChange", this.form)
		},
		// 添加agent
		addAgent() {
			this.dialogVisible = true
		},
		// 选择了应用
		handleSuccess(row) {
			let app = row[0].appInfo
			this.form.agentId = app.appId
			this.form.agentName = app.name
			this.form.agentLogo = app.icon

			this.$emit("dataChange", this.form)
			this.dialogVisible = false
		},
		iconComponent
	}
}
</script>

<style scoped>
.opt-form {
	width: 100%;
	height: calc(100vh - 200px);
	background: #f4f4f4;
	border-radius: 5px;
	padding: 20px;
}
.node-name {
	margin-left: 10px;
	font-weight: bold;
}
.param-data {
	width: 100%;
	border-radius: 5px;
	margin-top: 10px;
	display: flex;
	flex-direction: column;
}
.data-item {
	width: 100%;
	padding: 10px;
	height: 40px;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 10px;
	background: #f4f4f4;
	border-radius: 5px;
}
.no-param {
	width: 100%;
	height: 30px;
	background: #f4f4f4;
	border-radius: 5px;
	color: #98A2B2;
	cursor: pointer;
}
.app-item {
	padding: 10px;
	margin-bottom: 10px;
}
</style>
