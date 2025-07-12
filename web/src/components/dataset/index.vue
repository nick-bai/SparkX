<template>
	<div class="dataset-list" v-if="datasetList.length > 0">
		<el-radio-group v-model="datasetId" class="too-radio-list">
			<el-radio :label="item.datasetId" border class="radio-item" v-for="item in datasetList" :key="item.datasetId">
				<div style="display: flex;align-items: center;">
					<el-icon size="26" color="var(--el-color-theme)"><Management /></el-icon>
					<div class="line1 name">{{ item.title }}</div>
				</div>
			</el-radio>
		</el-radio-group>
	</div>
	<div v-else>
		<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
			暂无知识库
			<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
		</div>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()" :loading="loading">确认选择</el-button>
	</div>
</template>

<script>
import {Management} from "@element-plus/icons-vue";

export default {
	components: {Management},
	props: {
		diffDatasetId: {
			type: String,
			default: ''
		},
		documentIds: {
			type: String,
			default: ''
		}
	},
	data() {
		return {
			datasetId: "",
			datasetList: [],
			loading: false
		}
	},
	mounted() {
		this.getDatasetList();
	},
	methods: {
		// 获取知识库列表
		async getDatasetList() {
			let res = await this.$API.dataset.otherDataset.get({datasetId: this.diffDatasetId})
			this.datasetList = res.data
		},
		// 保存
		async optSubmit() {
			this.$emit('success', {
				datasetId: this.datasetId,
				documentIds: this.documentIds,
				oldDatasetId: this.diffDatasetId
			})
		},
		goTo() {
			this.$router.push({
				path: '/dataset/index'
			})
		}
	}
}
</script>

<style scoped>
.dataset-list {
	width: 100%;
	height: 100%;
	display: flex;
	flex-wrap: wrap;
	padding: 10px;
	justify-content: space-between;
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
	margin-top: 10px;
}
</style>
