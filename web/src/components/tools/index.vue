<template>
	<div class="tools-list">
		<el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
			<el-tab-pane label="自定义插件" name="first">
				<el-checkbox-group v-model="selectedToolsIds" class="too-radio-list" v-if="toolsList.length > 0">
					<el-checkbox :label="item.id" border class="radio-item" v-for="item in toolsList" :key="item.id">
						<div style="display: flex;align-items: center;">
							<el-icon size="26" color="var(--el-color-theme)"><ElementPlus /></el-icon>
							<div class="line1 name">{{ item.title }}</div>
						</div>
					</el-checkbox>
				</el-checkbox-group>
				<div v-else>
					<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
						暂无插件
						<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
					</div>
				</div>
			</el-tab-pane>
			<el-tab-pane label="MCP插件" name="second">
				<el-checkbox-group v-model="selectedToolsIds" class="too-radio-list" v-if="toolsList.length > 0">
					<el-checkbox :label="item.id" border class="radio-item" v-for="item in toolsList" :key="item.id">
						<div style="display: flex;align-items: center;">
							<el-icon size="26" color="var(--el-color-theme)"><ElementPlus /></el-icon>
							<div class="line1 name">{{ item.title }}</div>
						</div>
					</el-checkbox>
				</el-checkbox-group>
				<div v-else>
					<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
						暂无插件
						<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
					</div>
				</div>
			</el-tab-pane>
		</el-tabs>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()">确认选择</el-button>
	</div>
</template>

<script>
import {ElementPlus} from "@element-plus/icons-vue";

export default {
	props: {
		toolIds: {
			type: Array,
			default: []
		}
	},
	components: {ElementPlus},
	data() {
		return {
			selectedToolsIds: [],
			activeName: 'first',
			toolsList: []
		}
	},
	mounted() {
		this.selectedToolsIds = this.toolIds
		this.getToolList(1)
	},
	methods: {
		// 获取插件列表
		async getToolList(type) {
			let res = await this.$API.tool.toolList.get({type: type})
			this.toolsList = res.data
		},
		goTo() {
			this.$router.push({
				path: '/tools/index'
			})
		},
		// 选择插件
		optSubmit() {
			let selectedTools = []

			this.toolsList.forEach(item => {
				if (this.selectedToolsIds.indexOf(item.id) !== -1) {
					selectedTools.push(item)
				}
			})

			this.$emit('success', selectedTools)
		},
		handleClick() {
			let type = this.activeName === 'first' ? 2 : 1
			this.getToolList(type)
		}
	}
}
</script>

<style scoped>
.tools-list {
	padding: 0 10px;
}
.radio-item {
	margin-top: 20px;
	width: 207px;
	font-size: 13px;
	height: 60px;
	background: #fff;
}
.name {
	width: 140px;
	font-size: 13px;
	margin-left: 5px;
}
.dialog-footer {
	text-align: center;
	margin-top: 20px;
}
.notice {
	font-size: 13px;
	position: relative;
	left: 10px;
	color: #E6A23C;
}
</style>