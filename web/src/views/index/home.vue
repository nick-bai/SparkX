<template>
	<el-container style="padding: 20px" class='store-div-box'>
		<el-card style="height: 900px" shadow="never">
			<div class="title">应用</div>
			<el-form :inline="true" :model="searchForm" class="demo-form-inline" style="float: right;margin-top: 10px" label-width="5px">
				<el-form-item style="margin-right: 10px !important;">
					<el-select v-model="searchForm.type" placeholder="选择范围" style="width: 110px" clearable>
						<el-option label="全部" value="0"></el-option>
						<el-option label="我的" value="1"></el-option>
						<el-option label="我的团队" value="2"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-input v-model="searchForm.name" placeholder="应用名称" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
			<el-row class="store-list">
				<el-col :span="6" class="store-item">
					<el-card class="add-box" shadow="never" @click="addApplication">
						<div class="add-item-box">
							<div class="add-icon">
								<el-icon class="icon-color">
									<Plus />
								</el-icon>
							</div>
							<div class="add-store-name"> 创建应用</div>
						</div>
					</el-card>
				</el-col>

				<el-col :span="6" class="store-item" v-for="item in applicationList" :key="item.code">
					<el-card style="height: 170px;padding: 10px" shadow="never">
						<div class="title-box" @click="goDetail(item.appId, item.manage)">
							<div class="title-left">
								<div class="title-label">
									<img :src="domain + item.icon" style="width: 45px;height: 45px"
										 v-if="item.icon === '/icons/default_logo.png'"/>
									<img :src="item.icon" style="width: 45px;height: 45px" v-else/>
								</div>
								<div class="title-info">
									<div class="line1 knowledge-title">{{ item.name }}</div>
									<div class="author">创建者: {{ item.author }}</div>
								</div>
							</div>
							<div class="title-right">
								<el-tag type="success" v-if="item.type === 1">简单Agent</el-tag>
								<el-tag v-else>高级编排</el-tag>
							</div>
						</div>
						<div class="desc-box" @click="goDetail(item.appId, item.manage)">
							{{ item.description }}
						</div>
						<div class="tool-box">
							<div class="tool-box-left">
								<div class="box-item" v-if="item.status === 2">
									<el-tooltip
										effect="dark"
										content="演示"
									>
										<el-icon size="16" @click="goChat(item.accessToken)"><VideoPlay /></el-icon>
									</el-tooltip>
									<el-divider direction="vertical"></el-divider>
								</div>
								<div class="box-item" v-if="item.type === 2">
									<el-tooltip
										effect="dark"
										content="工作流"
									>
										<el-icon size="16" @click="goFlow(item.appId)"><Share /></el-icon>
									</el-tooltip>
									<el-divider direction="vertical"></el-divider>
								</div>

								<div class="box-item" v-if="item.manage">
									<el-tooltip
										effect="dark"
										content="设置"
									>
										<el-icon size="16" @click="goDetail(item.appId, item.manage)"><Setting /></el-icon>
									</el-tooltip>
									<el-divider direction="vertical"></el-divider>
								</div>

								<div class="box-item" v-if="item.manage">
									<el-tooltip
										effect="dark"
										content="删除"
									>
										<el-icon size="16" @click="deleteApp(item.appId)"><Delete /></el-icon>
									</el-tooltip>
								</div>
							</div>
						</div>
					</el-card>
				</el-col>
			</el-row>
		</el-card>
		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
	</el-container>

	<save-dialog v-if="dialogVisible" ref="saveDialog" @success="handleSuccess" @closed="dialogVisible=false" :close-on-click-modal="false"></save-dialog>
</template>

<script>
import saveDialog from './save.vue';
import Pages from "@/components/pages/index.vue";
import {Delete, Plus, Setting, Share, VideoPlay} from "@element-plus/icons-vue";
import config from "@/config"

export default{
	components: {
		Share,
		VideoPlay,
		Delete,
		Setting,
		Plus,
		saveDialog,
		Pages
	},
	data() {
		return {
			searchForm: {
				type: '0',
				name: '',
				exclude: "",
				page: 1,
				limit: 15
			},
			page: {
				total: 0
			},
			applicationList: [],
			dialogVisible: false,
			mode: 'add',
			domain: config.API_URL.replace("/api", ""),
		}
	},
	mounted() {
		this.getList()
	},
	methods: {
		async getList() {
			let res = await this.$API.application.list.get(this.searchForm)
			this.applicationList = res.data.data
			this.page.total = res.data.total
		},
		addApplication() {

			this.dialogVisible = true
			this.mode = 'add'
			this.$nextTick(() => {
				this.$refs.saveDialog.open('add')
			})
		},
		handleSuccess(row) {
			this.dialogVisible = false
			if (this.mode === 'add') {
				this.goDetail(row, true)
			}
			this.getList()
		},
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		onSubmit() {
			this.getList()
		},
		// 应用详情
		goDetail(appId, isManage) {
			if (!isManage) {
				return
			}

			this.$router.push('/index/detail?appId=' + appId)
		},
		// 前往聊天
		goChat(accessToken) {
			this.$router.push('/chat/' + accessToken)
		},
		// 工作流
		goFlow(appId) {
			this.$router.push('/workflow/index?appId=' + appId)
		},
		// 删除应用
		async deleteApp(appId) {
			this.$confirm('此操作将永久删除该应用 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.application.del.get({appId: appId})
				if (res.code === 0) {
					this.$message.success(res.msg)
					this.getList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {});
		}
	}
}
</script>

<style scoped>
.title {
	font-size: 18px;
	font-weight: bold;
	padding-bottom: 20px;
	border-bottom: 1px solid #f4f4f4;
}
.store-list {
	width: 100%;
}
.store-item {
	height: 100%;
	margin-top: 20px;
	padding: 0 20px;
	cursor: pointer;
}
.add-box {
	height: 170px;
	background: #eff0f1;
	display: flex;
	align-items: center;
	justify-content: center;
}
.add-icon {
	height: 25px;
	width: 25px;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
}
.title-box {
	width: 100%;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.title-left {
	display: flex;
	height: 40px;
	align-items: center;
	color: #1f2329;
}
.knowledge-title {
	margin-left: 10px;
	width: 250px;
}
.author {
	margin-left: 10px;
	color: #646a73;
	margin-top: 5px;
	font-size: 12px;
}
.title-label {
	border-radius: 10px;
	height: 45px;
	width: 40px;
}
.add-store-name {
	font-size: 16px;margin-left: 10px
}
.add-box:hover {
	border: 1px dashed var(--el-color-theme);
	background: #fff;
}
.add-box:hover .add-store-name {
	color: var(--el-color-theme);
}
.add-box:hover .add-icon {
	border: 1px solid var(--el-color-theme);
}
.add-box:hover .icon-color {
	color: var(--el-color-theme);
}
.desc-box {
	color: #646a73;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	overflow: hidden;
	padding-top: 20px;
	height: 60px;
}
.add-item-box {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 170px;
}
.tool-box {
	width: 100%;
	height: 40px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.tool-box-left {
	display: flex;
	align-items: center;
}
.title-info {
	margin-left: 10px;
}
.box-item {
	display: flex;
	align-items: center;
}
</style>
