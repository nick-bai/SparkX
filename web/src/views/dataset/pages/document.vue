<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="search-box">
			<div>
				<el-button type="primary" icon="el-icon-UploadFilled" @click="uploadFile" style="margin-top: -10px;">上传文档</el-button>
				<el-button type="primary" icon="el-icon-Switch" @click="switchFile" style="margin-top: -10px;"
						   :disabled="selectedDocumentIds.length === 0">迁移文档</el-button>
				<el-button type="primary" @click="embeddingAll" style="margin-top: -10px;" :disabled="selectedDocumentIds.length === 0">
					<span class="iconfont icon-vuesax-linear-convert-3d-cube" style="font-size: 14px;margin-right: 5px"></span>向量文档</el-button>
				<el-button type="primary" icon="el-icon-QuestionFilled" @click="makeQuestion" style="margin-top: -10px;"
						   :disabled="selectedDocumentIds.length === 0">生成问题</el-button>
				<!--<el-button type="primary" icon="el-icon-Setting" @click="setting" style="margin-top: -10px;"
						   :disabled="selectedDocumentIds.length === 0">设置</el-button>-->
				<el-button type="primary" icon="el-icon-Delete" @click="delDocument" style="margin-top: -10px;"
						   :disabled="selectedDocumentIds.length === 0">删除</el-button>
			</div>

			<el-form :inline="true" :model="searchForm" class="demo-form-inline">
				<el-form-item>
					<el-input v-model="searchForm.name" placeholder="文档名称" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
		</div>
		<div style="border-radius: 10px;background: #fff;padding: 0 5px 5px 5px">
			<el-table
				:header-cell-style="{background:'#f4f4f4', color:'#646a73'}"
				:data="tableData"
				@selection-change="handleSelectionChange"
				style="width: 100%">
				<el-table-column
					type="selection"
					width="55">
				</el-table-column>
				<el-table-column
					label="文档名称">
					<template #default="scope">
						<span style="cursor: pointer" @click="showParagraph(scope.row)">{{ scope.row.name }}</span>
						<el-icon style="margin-left: 5px;">
							<Edit />
						</el-icon>
					</template>
				</el-table-column>
				<el-table-column
					width="100"
					label="文件大小">
					<template #default="scope">
						<span>{{ $TOOL.formatBytes(scope.row.fileSize) }}</span>
					</template>
				</el-table-column>
				<el-table-column
					width="80"
					prop="paragraphNum"
					label="分段数">
				</el-table-column>
				<el-table-column
					width="100"
					label="向量化">
					<template #default="scope">
						<span v-if="scope.row.status === 1" style="color: #999;cursor: pointer">待生成</span>
						<span v-if="scope.row.status === 2" style="display: flex;align-items: center;color: #409EFF;cursor: pointer">
							<el-icon class="custom-loading-icon">
								<Loading />
							</el-icon>
							向量化中
						</span>
						<span v-if="scope.row.status === 3" style="color: #67C23A;cursor: pointer">已完成</span>
					</template>
				</el-table-column>
				<el-table-column
					width="100"
					label="生成问题">
					<template #default="scope">
						<span v-if="scope.row.questionStatus === 1" style="color: #999;cursor: pointer">待生成</span>
						<span v-if="scope.row.questionStatus === 2" style="display: flex;align-items: center;color: #409EFF;cursor: pointer">
							<el-icon class="custom-loading-icon">
								<Loading />
							</el-icon>
							生成中
						</span>
						<span v-if="scope.row.questionStatus === 3" style="color: #67C23A;cursor: pointer">已生成</span>
					</template>
				</el-table-column>
				<el-table-column
					width="100"
					label="状态">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.active === 1">正常</el-tag>
						<el-tag type="danger" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					width="100"
					label="命中处理">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.answerType === 'model'">模型优化</el-tag>
						<el-tag type="danger" v-if="scope.row.answerType === 'direct'">直接返回</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					width="160"
					label="创建时间">
					<template #default="scope">
						{{ scope.row.createTime.replace('T', " ") }}
					</template>
				</el-table-column>
				<el-table-column
					width="160"
					label="更新时间">
					<template #default="scope">
						{{ scope.row.updateTime && scope.row.updateTime.replace('T', " ") }}
					</template>
				</el-table-column>
				<el-table-column
					prop="operation"
					width="120"
					label="操作">
					<template #default="scope">
						<div style="display: flex;align-items: center;color: var(--el-color-theme);cursor: pointer">
							<div style="margin-right: 8px;display: flex;align-items: center" @click="embedding(scope.row)">
								<el-tooltip class="item" content="向量化文档">
									<span class="iconfont icon-vuesax-linear-convert-3d-cube" style="font-size: 14px;margin-right: 5px"></span>
								</el-tooltip>
							</div>
							<!--<div style="margin-right: 8px;display: flex;align-items: center" @click="settingOne(scope.row)">
								<el-tooltip class="item" content="设置">
									<el-icon size="14">
										<Setting />
									</el-icon>
								</el-tooltip>
							</div>-->
							<div style="display: flex;align-items: center;color: var(--el-color-theme)">
								<el-dropdown trigger="click" @command="handleCommand($event, scope.row)">
									<el-icon color="var(--el-color-theme)">
										<MoreFilled />
									</el-icon>
									<template #dropdown>
										<el-dropdown-menu>
											<el-dropdown-item command="question">
												<el-icon>
													<QuestionFilled />
												</el-icon>
												生成问题
											</el-dropdown-item>
											<el-dropdown-item command="transfer">
												<el-icon>
													<Switch />
												</el-icon> 迁移
											</el-dropdown-item>
											<el-dropdown-item command="del">
												<el-icon>
													<Delete />
												</el-icon> 删除</el-dropdown-item>
										</el-dropdown-menu>
									</template>
								</el-dropdown>
							</div>
						</div>

					</template>
				</el-table-column>
			</el-table>
		</div>

		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
	</div>

	<!-- 段落信息 -->
	<el-drawer
		size="1000"
		v-model="drawer"
		:title="documentTitle"
		:direction="direction"
	>
		<paragraph :document-id="nowDocumentId" :dataset-id="datasetId" :key="nowDocumentId"></paragraph>
	</el-drawer>

	<!-- 设置 -->
	<el-dialog title="设置命中模式" v-model="dialogVisible" width="500px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="settingForm" label-width="10px">
			<el-form-item label="">
				<el-radio v-model="settingForm.answerType" label="model">模型优化</el-radio>
				<el-radio v-model="settingForm.answerType" label="direct">直接回答</el-radio>
			</el-form-item>
			<el-form-item v-if="settingForm.answerType === 'direct'">
				相似度高于&nbsp;&nbsp;<el-input-number v-model="settingForm.redirectSimilar" :min="0" :precision="3"></el-input-number>&nbsp;&nbsp; 直接返回分段内容
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>

	<!-- 迁移文档 -->
	<el-dialog
		title="迁移文档"
		v-model="datasetVisible"
		width="800px"
		destroy-on-close
		:close-on-click-modal="false">
		<dataset-dialog
			@success="handleSuccess"
			:diff-dataset-id="datasetId"
			:document-ids="selectedDocumentIds.join(',')"
			@doClose="datasetVisible=false">
		</dataset-dialog>
	</el-dialog>

	<question-dialog
		v-if="questionVisible"
		ref="questionDialog"
		@success="handleQuestionSuccess"
		@closed="questionVisible=false"
		:close-on-click-modal="false">
	</question-dialog>
</template>

<script>
import Pages from "@/components/pages/index.vue";
import Paragraph from "@/views/dataset/pages/docsub/paragraph.vue";
import {Delete, Loading, MoreFilled, QuestionFilled, Setting, Switch} from "@element-plus/icons-vue";
import datasetDialog from "@/components/dataset/index.vue";
import questionDialog from "@/views/dataset/questionDialog.vue";

export default {
	components: {
		questionDialog,
		datasetDialog,
		Delete,
		Switch,
		QuestionFilled,
		MoreFilled,
		Setting,
		Loading,
		Paragraph,
		Pages
	},
	data() {
		return {
			tableData: [],
			searchForm: {
				name: '',
				datasetId: '',
				page: 1,
				limit: 10
			},
			page: {
				total: 0
			},
			datasetId: '',
			direction: "rtl",
			drawer: false,
			documentTitle: "",
			nowDocumentId: "",
			selectedDocumentIds: [], // 已选择的文档
			dialogVisible: false,
			questionVisible: false,
			loading: false,
			settingForm: {
				documentIds: "",
				answerType: "model",
				redirectSimilar: 0.900
			},
			datasetVisible: false
		}
	},
	mounted() {

		this.datasetId = this.$route.query.datasetId;
		this.searchForm.datasetId = this.datasetId

		this.getList()
	},
	methods: {
		async getList() {
			let res = await this.$API.document.getList.get(this.searchForm)
			this.tableData = res.data.data
			this.page.total = res.data.total

			// 没有在向量化的文档，则清理定时器
			let running = false
			res.data.data.forEach(item => {
				if (item.status === 2 || item.questionStatus === 2) {
					running = true
				}
			})

			if (running) {
				setTimeout(() => {
					this.getList()
				}, 1500)
			}
		},
		onSubmit() {
			this.getList()
		},
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		uploadFile() {
			this.$router.push('/dataset/upload?datasetId=' + this.datasetId)
		},
		// 多选
		handleSelectionChange(row) {
			this.selectedDocumentIds = []
			row.forEach(item => {
				this.selectedDocumentIds.push(item.documentId)
			})
		},
		// 向量化文本
		async embedding(row) {
			let res = await this.$API.document.embedding.get({documentIds: row.documentId})
			if (res.code === 0) {
				this.getList()
			} else {
				this.$message.error(res.msg)
			}
		},
		// 显示段落
		showParagraph(row) {
			this.documentTitle = row.name
			this.nowDocumentId = row.documentId

			this.$nextTick(() => {
				this.drawer = true
			})
		},
		// 批量进化
		async embeddingAll() {
			if (this.selectedDocumentIds.length === 0) {
				this.$message.error('请勾选文档')
				return false
			}

			let res = await this.$API.document.embedding.get({documentIds: this.selectedDocumentIds.join(",")})
			if (res.code === 0) {
				this.$message.success(res.msg)
				setTimeout(() => {
					this.getList()
				}, 1500)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 操作栏
		handleCommand(event, row) {
			switch (event) {
				case 'del':
					this.selectedDocumentIds = [row.documentId]
					this.delDocument()
					break;
				case 'transfer':
					this.selectedDocumentIds = [row.documentId]
					this.datasetVisible = true
					break;
			}
		},
		// 设置单个文档
		settingOne(row) {

			this.settingForm.documentIds = row.documentId
			this.settingForm.answerType = row.answerType
			this.settingForm.redirectSimilar = row.redirectSimilar
			this.dialogVisible = true
		},
		// 设置文档
		setting() {
			if (this.selectedDocumentIds.length === 0) {
				this.$message.error('请勾选文档')
				return false
			}

			this.settingForm.documentIds = this.selectedDocumentIds.join(",")
			this.dialogVisible = true
		},
		// 提交设置
		async optSubmit() {
			let res = await this.$API.document.setting.post(this.settingForm)
			if (res.code === 0) {
				this.$message.success(res.msg)
				this.getList()
				this.dialogVisible = false
			} else {
				this.$message.error(res.msg)
			}
		},
		// 删除文档
		async delDocument() {
			if (this.selectedDocumentIds.length === 0) {
				this.$message.error('请勾选文档')
				return false
			}

			this.$confirm('此操作将永久删除这些文档 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.document.del.get({documentIds: this.selectedDocumentIds.join(",")})
				if (res.code === 0) {
					this.getList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {});
		},
		async handleSuccess(data) {
			let res = await this.$API.dataset.transfer.post(data)

			if (res.code === 0) {
				this.$message.success(res.msg)
				this.datasetVisible = false
				this.getList()
			} else {
				this.$message.error(res.msg)
			}
		},
		// 生成问题
		makeQuestion() {
			if (this.selectedDocumentIds.length === 0) {
				this.$message.error('请勾选文档')
				return false
			}

			this.questionVisible = true
			this.$nextTick(() => {
				this.$refs.questionDialog.open('add').setData(this.selectedDocumentIds)
			})
		},
		// 问题生成成功
		handleQuestionSuccess() {
			this.questionVisible = false
			setTimeout(() => {
				this.getList()
			}, 1500)
		},
		// 迁移文档
		switchFile() {
			if (this.selectedDocumentIds.length === 0) {
				this.$message.error('请勾选文档')
				return false
			}

			this.datasetVisible = true
		}
	}
}
</script>

<style>
	.search-box {
		border-radius: 10px;
		margin-bottom: 20px;
		padding-top: 20px;
		padding-left: 20px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.custom-loading-icon {
		animation: spin 1s linear infinite;
		margin-right: 5px;
	}

	@keyframes spin {
		from { transform: rotate(0deg); }
		to { transform: rotate(360deg); }
	}
	.el-table .cell {
		font-size: 13px; /* 或者你想要的任何大小 */
		color: #172329;
	}
</style>
