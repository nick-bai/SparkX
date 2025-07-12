<template>
	<el-container style="padding: 20px" class='store-div-box'>
		<el-card style="height: 900px" shadow="never">
			<div class="title">工具箱</div>

			<el-tabs v-model="activeName" style="padding: 20px;">
				<el-tab-pane label="自定义插件" name="first">
					<el-row class="store-list">
						<el-col :span="4" class="store-item">
							<el-card class="add-box" shadow="never" @click="addTools(1)">
								<div class="add-item-box">
									<div class="add-icon">
										<el-icon class="icon-color">
											<Plus />
										</el-icon>
									</div>
									<div class="add-store-name"> 创建插件</div>
								</div>
							</el-card>
						</el-col>

						<el-col :span="4" class="store-item" v-for="item in commonToolsList" :key="item.id">
							<el-card style="height: 170px;" shadow="never">
								<div class="title-box">
									<div class="title-left">
										<div class="title-label">{{ item.title.substring(0, 1) }}</div>
										<div class="title-info">
											<div class="line1 knowledge-title">{{ item.title }}</div>
										</div>
									</div>
								</div>
								<div class="description">{{ item.description }}</div>
								<div class="tool-bar">
									<div class="tool-time">创建时间: {{ item.createTime }}</div>
									<el-dropdown trigger="click" @command="handleClick($event, item)">
										<el-icon>
											<MoreFilled />
										</el-icon>
										<template #dropdown>
											<el-dropdown-menu>
												<el-dropdown-item command="edit">
													<el-icon>
														<Edit />
													</el-icon> 编辑
												</el-dropdown-item>
												<el-dropdown-item command="delete">
													<el-icon>
														<Delete />
													</el-icon> 删除</el-dropdown-item>
											</el-dropdown-menu>
										</template>
									</el-dropdown>
								</div>
							</el-card>
						</el-col>
					</el-row>
				</el-tab-pane>
				<el-tab-pane label="MCP插件" name="second">
					<el-row class="store-list">
						<el-col :span="4" class="store-item">
							<el-card class="add-box" shadow="never" @click="addTools(2)">
								<div class="add-item-box">
									<div class="add-icon">
										<el-icon class="icon-color">
											<Plus />
										</el-icon>
									</div>
									<div class="add-store-name"> 创建插件</div>
								</div>
							</el-card>
						</el-col>

						<el-col :span="4" class="store-item" v-for="item in mcpToolsList" :key="item.id">
							<el-card style="height: 170px;" shadow="never">
								<div class="title-box">
									<div class="title-left">
										<div class="title-label">{{ item.title.substring(0, 1) }}</div>
										<div class="title-info">
											<div class="line1 knowledge-title">{{ item.title }}</div>
										</div>
									</div>
								</div>
								<div class="description">{{ item.description }}</div>
								<div class="tool-bar">
									<div class="tool-time">创建时间: {{ item.createTime }}</div>
									<el-dropdown trigger="click" @command="handleClick($event, item)">
										<el-icon>
											<MoreFilled />
										</el-icon>
										<template #dropdown>
											<el-dropdown-menu>
												<el-dropdown-item command="edit">
													<el-icon>
														<Edit />
													</el-icon> 编辑
												</el-dropdown-item>
												<el-dropdown-item command="delete">
													<el-icon>
														<Delete />
													</el-icon> 删除</el-dropdown-item>
											</el-dropdown-menu>
										</template>
									</el-dropdown>
								</div>
							</el-card>
						</el-col>
					</el-row>
				</el-tab-pane>
			</el-tabs>
		</el-card>

		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList" v-if="activeName === 'first'"></Pages>
		<Pages :form="searchForm" :page-obj="page2" @pageChange="handlePageChange" @pageJump="getMCPToolsList" v-if="activeName === 'second'"></Pages>
	</el-container>

	<el-drawer
		:size="1000"
		v-model="drawer"
		:title="title"
		append-to-body
		destroy-on-close>
		<save-dialog ref="saveDialog" @closed="drawer = false" @success="handleSuccess"></save-dialog>
	</el-drawer>

	<mcp-dialog v-if="mcpDialogVisible" ref="mcpDialog" @success="handleMcpSuccess"></mcp-dialog>
</template>

<script>
import Pages from "@/components/pages/index.vue"
import {Delete, MoreFilled, Plus, Edit} from "@element-plus/icons-vue"
import SaveDialog from "./save.vue"
import McpDialog from "./mcp.vue"

export default {
	components: {Delete, MoreFilled, Plus, Edit, Pages, SaveDialog, McpDialog},
	data() {
		return {
			searchForm: {
				title: '',
				type: 1,
				page: 1,
				limit: 14
			},
			page: {
				total: 0
			},
			searchForm2: {
				title: '',
				type: 2,
				page: 1,
				limit: 14
			},
			page2: {
				total: 0
			},
			activeName: 'first',
			commonToolsList: [],
			mcpToolsList: [],
			drawer: false,
			mcpDialogVisible: false,
			title: "创建插件"
		}
	},
	mounted() {
		this.getList()
		this.getMCPToolsList()
	},
	methods: {
		// 获取列表
		async getList() {
			let res = await this.$API.tool.list.get(this.searchForm)
			if (res.code === 0) {
				this.commonToolsList = res.data.data
				this.page.total = res.data.total
			}
		},
		// 获取mcp服务列表
		async getMCPToolsList() {
			let res = await this.$API.tool.list.get(this.searchForm2)
			if (res.code === 0) {
				this.mcpToolsList = res.data.data
				this.page2.total = res.data.total
			}
		},
		// 分页
		handlePageChange(page) {
			if (this.activeName === 'first') {
				this.searchForm.page = page
				this.getList()
			} else {
				this.searchForm2.page = page
				this.getMCPToolsList()
			}
		},
		// 创建插件
		addTools(type) {
			if (type === 1) {
				this.drawer = true
				this.title = "创建插件"
				this.$nextTick(() => {
					this.$refs.saveDialog.open()
				})
			} else {
				this.mcpDialogVisible = true
				this.$nextTick(() => {
					this.$refs.mcpDialog.open('add')
				})
			}
		},
		// 添加插件成功
		handleSuccess() {
			this.drawer = false
			this.getList()
		},
		// 添加mcp成功
		handleMcpSuccess() {
			this.mcpDialogVisible = false
			this.getMCPToolsList()
		},
		// 操作菜单
		handleClick(event, row) {
			if (event === 'edit') {
				if (this.activeName === 'first') {

					this.drawer = true
					this.title = "编辑插件"
					this.$nextTick(() => {
						this.$refs.saveDialog.open('edit').setData(row)
					})
				} else {

					this.mcpDialogVisible = true
					this.$nextTick(() => {
						this.$refs.mcpDialog.open('edit').setData(row)
					})
				}

			} else if (event === 'delete') {
				this.$confirm('此操作将永久删除该插件 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(async () => {
					let res = await this.$API.tool.del.get({id: row.id})
					if (res.code === 0) {
						this.$message.success(res.msg)
						this.getList()
					} else {
						this.$message.error(res.msg)
					}
				}).catch(() => {})
			}
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
	cursor: pointer;
	margin-right: 20px;
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
.add-item-box {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 170px;
}
.add-store-name {
	font-size: 16px;margin-left: 10px
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
.title-label {
	background: var(--el-color-theme);
	color: #fff;
	border-radius: 10px;
	height: 40px;
	width: 40px;
	line-height: 40px;
	text-align: center;
	font-weight: bold;
}
.description {
	margin-top: 15px;
	color: #606266;
	height: 55px;
	width: 100%;
	overflow: hidden;
}
.tool-bar {
	width: 100%;
	border-top: 1px solid #e2e2e2;
	padding-top: 10px;
	display: flex;
	justify-content: space-between;
}
.tool-time {
	font-size: 12px;
	color: #606266;
}
</style>
