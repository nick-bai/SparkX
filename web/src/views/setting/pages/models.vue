<template>
	<div style="background: #fff;border-radius: 10px;padding: 20px">
		<el-tabs v-model="activeName" @tab-click="handleClick">
			<el-tab-pane label="语言模型" :name="1">
				<model-list-page :key="randomKey" :models-list="modelsList" @success="getModelList"></model-list-page>
			</el-tab-pane>
			<el-tab-pane label="向量模型" :name="2">
				<model-list-page :key="randomKey" :models-list="modelsList" @success="getModelList"></model-list-page>
			</el-tab-pane>
			<el-tab-pane label="重排模型" :name="3">
				<model-list-page :key="randomKey" :models-list="modelsList" @success="getModelList"></model-list-page>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
import modelListPage from '@/views/setting/sub/models.vue'

export default {
	components: {modelListPage},
	data() {
		return {
			activeName: 1,
			modelsList: [],
			randomKey: Math.random()
		}
	},
	mounted() {
		this.getModelList()
	},
	methods: {
		// tab切换
		handleClick(row) {
			this.activeName = parseInt(row.index) + 1
			this.getModelList()
		},
		// 获取模型列表
		async getModelList() {
			let res = await this.$API.models.list.get({type: this.activeName, status: 0})
			this.modelsList = res.data
			this.randomKey = Math.random()
		},
	}
}
</script>

<style scoped>

</style>
