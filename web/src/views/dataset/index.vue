<template>
	<el-container style="padding: 20px" class='store-div-box'>
		<el-card style="height: 900px" shadow="never">
			<div class="title">知识库</div>
			<el-form :inline="true" :model="searchForm" class="demo-form-inline" style="float: right;margin-top: 10px" label-width="5px">
				<el-form-item style="margin-right: 10px !important;">
					<el-select v-model="searchForm.type" placeholder="选择范围" style="width: 110px" clearable>
						<el-option label="全部" value="0"></el-option>
						<el-option label="我的" value="1"></el-option>
						<el-option label="我的团队" value="2"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-input v-model="searchForm.title" placeholder="知识库标题" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
			<el-row class="store-list">
				<el-col :span="6" class="store-item">
					<el-card class="add-box" shadow="never" @click="addDataset">
						<div class="add-item-box">
							<div class="add-icon">
								<el-icon class="icon-color">
									<Plus />
								</el-icon>
							</div>
							<div class="add-store-name"> 创建知识库</div>
						</div>
					</el-card>
				</el-col>

				<el-col :span="6" class="store-item" v-for="item in storeList" :key="item.code">
					<el-card style="height: 170px;padding: 10px" shadow="never">
						<div class="title-box" @click="goDetail(item.datasetId, item.manage)">
							<div class="title-left">
								<div class="title-label">{{ item.title.substring(0, 1) }}</div>
								<div class="title-info">
									<div class="line1 knowledge-title">{{ item.title }}</div>
									<div class="author">创建者: {{ item.author }}</div>
								</div>
							</div>
							<div class="title-right">
								<el-tag>通用</el-tag>
							</div>
						</div>
						<div class="desc-box" @click="goDetail(item.datasetId, item.manage)">
							{{ item.description }}
						</div>
						<div class="tool-box">
							<div class="tool-box-left" @click="goDetail(item.datasetId, item.manage)">
								<div class="box-item">
									<span class="num">{{ item.documentNum }}</span>
									<span class="num-label">文档数</span>
								</div>
								<el-divider direction="vertical"></el-divider>
								<div class="box-item">
									<span class="num">{{ $TOOL.formatBytes(item.fileSize) }}</span>
									<span class="num-label"></span>
								</div>
								<el-divider direction="vertical"></el-divider>
								<div class="box-item">
									<span class="num">{{ item.appNum }}</span>
									<span class="num-label">关联应用</span>
								</div>
							</div>
							<div class="tool-box-right" v-if="item.manage">
								<el-dropdown trigger="click" @command="handleClick($event, item)">
									<el-icon>
										<MoreFilled />
									</el-icon>
									<template #dropdown>
										<el-dropdown-menu>
											<el-dropdown-item command="embedding">
												<span class="iconfont icon-vuesax-linear-convert-3d-cube" style="font-size: 14px;margin-right: 5px"></span>向量化
											</el-dropdown-item>
											<el-dropdown-item command="setting">
												<el-icon>
													<Setting />
												</el-icon> 设置
											</el-dropdown-item>
											<el-dropdown-item command="delete">
												<el-icon>
													<Delete />
												</el-icon> 删除</el-dropdown-item>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
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
import saveDialog from '@/views/dataset/save.vue';
import Pages from "@/components/pages/index.vue";
import {Delete, MoreFilled, Plus, Setting} from "@element-plus/icons-vue";

export default{
	components: {
		Delete,
		Setting,
		MoreFilled,
		Plus,
		saveDialog,
		Pages
	},
	data() {
		return {
			searchForm: {
				title: '',
				type: '0',
				page: 1,
				limit: 15
			},
			page: {
				total: 0
			},
			storeList: [],
			dialogVisible: false,
		}
	},
	mounted() {
		this.getList()
	},
	methods: {
		async getList() {
			let res = await this.$API.dataset.list.get(this.searchForm)
			this.storeList = res.data.data
			this.page.total = res.data.total
		},
		addDataset() {

			this.dialogVisible = true

			this.$nextTick(() => {
				this.$refs.saveDialog.open('add')
			})
		},
		handleSuccess() {
			this.dialogVisible = false
			this.getList()
		},
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		onSubmit() {
			this.getList()
		},
		// 知识库详情
		goDetail(datasetId, isManage) {
			if (!isManage) {
				return
			}

			this.$router.push('/dataset/detail?datasetId=' + datasetId)
		},
		// 向量化
		async embedding(datasetId) {
			let res = await this.$API.dataset.embedding.get({datasetId: datasetId})
			if (res.code === 0) {
				this.$message.success(res.msg)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 删除知识库
		async delete(datasetId) {
			this.$confirm('此操作将永久删除该知识库 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.dataset.del.get({datasetId: datasetId})
				if (res.code === 0) {
					this.$message.success(res.msg)
					this.getList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {})
		},
		// 操作知识库
		handleClick(event, row) {
			switch (event) {
				case 'embedding':
					this.embedding(row.datasetId)
					break;
				case 'setting':
					this.dialogVisible = true

					this.$nextTick(() => {
						this.$refs.saveDialog.open('edit').setData(row)
					})
					break;
				case 'delete':
					this.delete(row.datasetId)
					break;
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
	background: var(--el-color-theme);
	color: #fff;
	border-radius: 10px;
	height: 40px;
	width: 40px;
	line-height: 40px;
	text-align: center;
	font-weight: bold;
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
.box-item .num {
	font-weight: bold;
}
.box-item .num-label {
	color: #646a73;
	margin-left: 5px;
}
.add-item-box {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 170px;
}
</style>
