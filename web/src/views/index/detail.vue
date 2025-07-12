<template>
	<el-container style="padding: 20px">
		<el-card shadow="never">
			<el-row class="detail-box">
				<el-col :span="3" class="box-height-left">
					<div class="menu-bar">
						<el-icon size="18" @click="goBack">
							<Back />
						</el-icon>
						<el-popover placement="bottom" :width="250" trigger="click">
							<template #reference>
								<div style="display: flex;width: calc(100% - 20px)">
									<div class="dataset-title line1">
										<el-icon style="font-size: 18px;margin-right: 5px;margin-top: 5px;top:4px;">
											<Collection />
										</el-icon> {{ appInfo.name }}
									</div>
									<el-icon style="font-size: 18px">
										<CaretBottom />
									</el-icon>
								</div>
							</template>
							<div class="dataset-list">
								<div class="dataset-item" v-for="item in appList" :key="item.appId" @click="selectApp(item)">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<Collection />
									</el-icon>{{ item.name }}
								</div>
								<div class="dataset-item" style="border-top: 1px solid #e2e2e2;" @click="addApplication">
									<el-icon style="font-size: 18px;margin-right: 5px">
										<Collection />
									</el-icon> 创建应用
								</div>
							</div>
						</el-popover>
					</div>
					<el-menu
						style="margin-top: 20px"
						default-active="1">
						<el-menu-item index="1" @click="handleClick(1)">
							<el-icon>
								<Setting />
							</el-icon>
							<span>应用设置</span>
						</el-menu-item>
						<el-menu-item index="2" @click="handleClick(2)" v-if="appInfo.type === 2">
							<el-icon>
								<Setting />
							</el-icon>
							<span>流程编排</span>
						</el-menu-item>
						<el-menu-item index="3" @click="handleClick(3)">
							<el-icon>
								<Odometer />
							</el-icon>
							<span>应用监控</span>
						</el-menu-item>
						<el-menu-item index="4" @click="handleClick(4)">
							<el-icon>
								<Document />
							</el-icon>
							<span>会话记录</span>
						</el-menu-item>
					</el-menu>
				</el-col>
				<el-col :span="21" class="box-height-right">
					<div class="pages">
						<Suspense>
							<template #default>
								<component :is="page" :access-token="accessToken"/>
							</template>
							<template #fallback>
								<el-skeleton :rows="3" />
							</template>
						</Suspense>
					</div>
				</el-col>
			</el-row>
		</el-card>
	</el-container>

	<save-dialog v-if="dialogVisible" ref="saveDialog" @success="handleSuccess" @closed="dialogVisible=false" :close-on-click-modal="false"></save-dialog>
</template>

<script>
import {defineAsyncComponent} from "vue";
import {Back, CaretBottom, Collection, Document, Odometer, Platform, Setting} from "@element-plus/icons-vue";
import saveDialog from "@/views/index/save.vue";
import datasetDialog from "@/components/chatContent/index.vue";

export default {
	components: {Odometer, Platform, saveDialog, CaretBottom, Collection, Back, Document, Setting, datasetDialog},
	data() {
		return {
			components: {
				deploy: defineAsyncComponent(() => import('./pages/deploy.vue')),
				log: defineAsyncComponent(() => import('./pages/log.vue')),
				setting: defineAsyncComponent(() => import('./pages/setting.vue'))
			},
			searchForm: {
				name: '',
				type: 0,
				page: 1,
				limit: 15
			},
			page: '',
			appId: "",
			dialogVisible: false,
			appInfo: {},
			appList: [],
			accessToken: ""
		}
	},
	mounted() {
		this.appId = this.$route.query.appId;
		this.getInfo()
		this.getApplicationList()
	},
	methods: {
		goBack() {
			this.$router.push("/index/home")
		},
		// 菜单选择
		handleClick(index) {
			if (index === 1) {
				this.page = this.components.setting
			} else if (index === 2) {
				this.$router.push("/workflow/index?appId=" + this.appId)
			} else if (index === 3) {
				this.page = this.components.deploy
			} else if (index === 4) {
				this.page = this.components.log
			}
		},
		// 获取应用详情
		async getInfo() {
			let res = await this.$API.application.info.get({appId: this.appId})
			this.appInfo = res.data
			this.accessToken = res.data.accessToken

			this.page = this.components.setting
		},
		// 选择应用
		selectApp(item) {
			this.$router.push('/index/detail?appId=' + item.appId)
		},
		addApplication() {

			this.dialogVisible = true
			this.$nextTick(() => {
				this.$refs.saveDialog.open('add')
			})
		},
		handleSuccess() {
			this.dialogVisible = false
			this.getList()
		},
		// 获取应用列表
		async getApplicationList() {
			let res = await this.$API.application.list.get(this.searchForm)
			this.appList = res.data.data
		}
	}
}

</script>

<style scoped>
.detail-box {
	width: 100%;
	height: calc(100vh - 70px);
}
.box-height-left {
	height: 100%;
}
.box-height-right {
	height: 100%;
	background: #f4f4f4;
	border-radius: 10px;
	padding: 20px;
}
.menu-bar {
	height: 40px;
	display: flex;
	align-items: center;
	padding-right: 10px;
	cursor: pointer;
}
.dataset-title {
	font-weight: bold;
	width: 100%;
	font-size: 14px;
	margin-top: -6px;
	margin-left: 10px;
}
.dataset-item {
	display: flex;
	align-items: center;
	cursor: pointer;
	padding: 10px;
}
.dataset-item:hover {
	background: #eee7fd;
	color: var(--el-color-theme);
}
</style>
