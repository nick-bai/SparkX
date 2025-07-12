<template>
	<div class="dataset-list" v-if="appList.length > 0">
		<el-radio-group v-model="appIndex" class="too-radio-list">
			<el-radio :label="index" border class="radio-item" v-for="(item, index) in appList" :key="item.appId">
				<div style="display: flex;align-items: center;">
					<img :src="domain + item.icon" style="width: 45px;height: 45px;"
						 v-if="item.icon === '/icons/default_logo.png'"/>
					<img :src="item.icon" style="width: 45px;height: 45px;" v-else/>
					<div class="line1 name">{{ item.name }}</div>
				</div>
			</el-radio>
		</el-radio-group>
	</div>
	<div v-else>
		<div class="flex-center-all" style="padding: 20px 10px;background: #f4f4f4">
			暂无Agent应用
			<el-button @click="goTo" type="text" style="margin-top: 3px;margin-left: 10px">创建</el-button>
		</div>
	</div>
	<div class="dialog-footer">
		<el-button @click="$emit('doClose')">取 消</el-button>
		<el-button type="primary" @click="optSubmit()" :loading="loading">确认选择</el-button>
	</div>
</template>

<script>
import config from "@/config"

export default {
	props: {
		type: {
			type: Number,
			default: 0
		},
		exclude: {
			type: String,
			default: ""
		}
	},
	data() {
		return {
			appIndex: "",
			appList: [],
			loading: false,
			domain: config.API_URL.replace("/api", ""),
		}
	},
	mounted() {
		this.getAppList();
	},
	methods: {
		// 获取应用列表
		async getAppList() {
			let res = await this.$API.application.list.get({
				page: 1,
				limit: 1000,
				name: '',
				type: this.type,
				exclude: this.exclude
			})
			this.appList = res.data.data
		},
		// 保存
		optSubmit() {
			this.$emit('success', [{appInfo: this.appList[this.appIndex]}])
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
