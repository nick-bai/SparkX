<template>
	<div class="dataset-list" v-if="appList.length > 0">
		<el-checkbox-group v-model="selectedAppIds" class="too-radio-list">
			<el-checkbox :label="item.appId" border class="radio-item" v-for="item in appList" :key="item.appId">
				<div style="display: flex;align-items: center;">
					<img :src="item.icon" style="width: 45px;height: 45px;" />
					<div class="line1 name">{{ item.name }}</div>
				</div>
			</el-checkbox>
		</el-checkbox-group>
	</div>
	<div v-else>
		<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
			暂无Agent应用
			<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
		</div>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()">确认选择</el-button>
	</div>
</template>

<script>
export default {
	props: {
		applicationIds: {
			type: Array,
			default: []
		},
		type: {
			type: Number,
			default: 0
		}
	},
	data() {
		return {
			appList: [],
			selectedAppIds: [],
		}
	},
	mounted() {
		this.selectedAppIds = this.applicationIds;
		this.getAppList();
	},
	methods: {
		// 获取应用列表
		async getAppList() {
			let res = await this.$API.application.list.get({page: 1, limit: 1000, name: '', type: this.type})
			this.appList = res.data.data
		},
		// 保存
		async optSubmit() {
			let selectedApplication = []
			this.appList.forEach(item => {

				if (this.selectedAppIds.indexOf(item.appId) !== -1) {
					selectedApplication.push(item)
				}
			})

			this.$emit('success', selectedApplication)
		},
		goTo() {
			this.$router.push({
				path: '/index/home'
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
	padding: 0 10px;
	justify-content: space-between;
	margin-top: -20px;
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
	color: #646a73;
	font-size: 13px;
	top: -20px;
	position: relative;
	left: 10px;
}
</style>
