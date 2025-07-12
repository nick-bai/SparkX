<template>
    <div style="background: #fff;border-radius: 10px;padding: 10px 5px">
        <div class="search-box">
            <div>
                <el-button icon="el-icon-plus" style="margin-top: -10px;" type="primary" @click="add">创建问题
                </el-button>
                <el-button
					:disabled="selectedQuestionId.length === 0"
					icon="el-icon-link"
                    style="margin-top: -10px;"
                    type="primary"
                    @click="setting">关联分段
                </el-button>
                <el-button
					:disabled="selectedQuestionId.length === 0"
					icon="el-icon-Delete"
					style="margin-top: -10px;"
                    type="primary"
                    @click="delAll">删除
                </el-button>
            </div>

            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
                <el-form-item>
                    <el-input v-model="searchForm.content" clearable placeholder="问题信息"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button icon="el-icon-search" type="primary" @click="onSubmit">查询</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div style="border-radius: 10px;background: #fff;padding: 0 5px 5px 5px">
            <el-table
                :data="tableData"
                :header-cell-style="{background:'#f4f4f4', color:'#646a73'}"
                style="width: 100%"
                @selection-change="handleSelectionChange">
                <el-table-column
                    type="selection"
                    width="55">
                </el-table-column>
                <el-table-column
                    label="问题">
                    <template #default="scope">
						<div v-if="scope.$index === nowIndex" style="display:flex;align-items: center;">
							<el-input v-model="scope.row.content" clearable style="width: 400px;"></el-input>
							<el-icon size="16" style="margin-left: 10px;cursor: pointer;color: #67C23A" @click="edit(scope.row)"><Check /></el-icon>
							<el-icon size="16" style="margin-left: 10px;cursor: pointer;color: #909399" @click="nowIndex = -1"><Close /></el-icon>
						</div>
						<div v-else>
							<span style="cursor: pointer" @click="showQuestion(scope.row)">{{ scope.row.content }}</span>
							<el-icon style="margin-left: 5px;cursor: pointer" @click="editContent(scope.$index, scope.row)"><Edit /></el-icon>
						</div>
                    </template>
                </el-table-column>
                <el-table-column
                    label="分段数"
                    prop="linkNum"
                    width="80">
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
                    label="操作"
                    prop="operation"
                    width="120">
                    <template #default="scope">
                        <div style="display: flex;align-items: center;color: var(--el-color-theme);cursor: pointer">
                            <div style="margin-right: 8px;display: flex;align-items: center" @click="linkParagraph(scope.row)">
                                <el-tooltip class="item" content="关联">
									<el-icon size="16"><Link /></el-icon>
                                </el-tooltip>
                            </div>
                            <div style="margin-right: 8px;display: flex;align-items: center" @click="del(scope.row.questionId)">
                                <el-tooltip class="item" content="删除">
									<el-icon size="16"><Delete /></el-icon>
                                </el-tooltip>
                            </div>
                        </div>

                    </template>
                </el-table-column>
            </el-table>
        </div>

        <Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
    </div>

    <!-- 添加问题 -->
    <el-dialog title="创建问题" v-model="dialogVisible" width="1000px" ref="saveDialog" :close-on-click-modal="false">
        <el-form :model="form" label-width="10px">
            <el-form-item>
                <el-input type="textarea" :rows="8" v-model="form.content"  placeholder="请输入问题，支持输入多个，一行一个。" style="width: 100%"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="optSubmit()" :loading="loading">确 定</el-button>
            </div>
        </template>
    </el-dialog>

    <!-- 关联问题 -->
    <el-dialog title="关联分段" v-model="linkVisible" width="1000px" ref="save2Dialog" :close-on-click-modal="false">
        <link-paragraph :dataset-id="linkForm.datasetId" :question-ids="linkForm.questionIds" :key="randomKey" :show-num="showNum" @linkComplete="getList"></link-paragraph>
    </el-dialog>

    <!-- 关联的段落 -->
    <el-drawer
        size="1000"
        v-model="drawer"
        title="问题关联信息"
        :direction="direction"
    >
        <question-link :question-id="nowQuestionId" :dataset-id="form.datasetId" :title="title" :key="randomKey" @unLinkComplete="$emit('linkComplete')"></question-link>
    </el-drawer>
</template>

<script>
import Pages from "@/components/pages/index.vue";
import linkParagraph from "@/components/linkParagraph/index.vue";
import Paragraph from "@/views/dataset/pages/docsub/paragraph.vue";
import questionLink from "@/views/dataset/pages/docsub/questionLink.vue"
import {Check, Close, Link, Edit, Delete} from "@element-plus/icons-vue";

export default {
    components: {Delete, Close, Check, Paragraph, Pages, Link, Edit, linkParagraph, questionLink},
    data() {
        return {
            searchForm: {
                datasetId: "",
                content: "",
                page: 1,
                limit: 10
            },
            selectedQuestionId: [],
            tableData: [],
            page: {
                total: 0
            },
            dialogVisible: false,
            form: {
                datasetId: "",
                content: ""
            },
            loading: false,
            linkVisible: false,
            linkForm: {
                questionIds: "",
                datasetId: ""
            },
            randomKey: 0,
            drawer: false,
            direction: "rtl",
            nowQuestionId: "",
            title: "",
			nowIndex: -1,
			editForm: {
				questionId: "",
				content: ""
			},
			showNum: true
        }
    },
    mounted() {
        this.searchForm.datasetId = this.$route.query.datasetId
        this.form.datasetId = this.$route.query.datasetId
        this.getList();
    },
    methods: {
        // 获取列表
        async getList() {
            let res = await this.$API.question.list.get(this.searchForm)
            this.tableData = res.data.data
            this.page.total = res.data.total
        },
        onSubmit() {
            this.getList()
        },
        // 创建问题
        add() {
            this.dialogVisible = true
        },
        // 关联分段
        setting() {
			if (this.selectedQuestionId.length === 0) {
				this.$message.error("请够选问题")
				return false
			}

			this.randomKey = Math.random()
			this.linkForm.datasetId = this.$route.query.datasetId
			this.linkForm.questionIds = this.selectedQuestionId.join(",")
			this.linkVisible = true
			this.showNum = false
        },
        // 批量删除
        delAll() {
			if (this.selectedQuestionId.length === 0) {
				this.$message.error("请够选问题")
				return false
			}

			this.del(this.selectedQuestionId.join(","))
        },
		// 删除单个
		del(ids) {
			this.$confirm('此操作将永久删除该问题 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.question.delQuestion.get({questionIds: ids})
				if (res.code === 0) {
					this.$message.success(res.msg)
					this.getList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {
			});
		},
		// 编辑content内容
		editContent(index, row) {
			this.editForm.content = row.content
			this.editForm.questionId = row.questionId
			this.nowIndex = index
		},
		// 保存编辑内容
		async edit(row) {
			let res = await this.$API.question.editQuestion.post({
				questionId: row.questionId,
				content: row.content
			})
			if (res.code === 0) {
				this.$message.success(res.msg)
				this.nowIndex = -1
			} else {
				this.$message.error(res.msg)
			}
		},
        // 显示问题
        showQuestion(row) {
			this.randomKey = Math.random()
            this.nowQuestionId = row.questionId
            this.title = row.content
            this.drawer = true
        },
        // 添加问题
        async optSubmit() {
            let res = await this.$API.question.add.post(this.form)
            if (res.code === 0) {
                this.$message.success(res.msg)
                this.dialogVisible = false
                this.getList()
            } else {
                this.$message.error(res.msg)
            }
        },
        // 勾选
        handleSelectionChange(row) {
			this.selectedQuestionId = []
            row.forEach(item => {
				this.selectedQuestionId.push(item.questionId)
            })
        },
        // 翻页
        handlePageChange(page) {
            this.searchForm.page = page;
            this.getList();
        },
        // 链接
        linkParagraph(row) {
			this.showNum = true
            this.randomKey = Math.random()
            this.linkForm.datasetId = this.$route.query.datasetId
            this.linkForm.questionIds = row.questionId
            this.linkVisible = true
        }
    }
}
</script>

<style scoped>
.el-table .cell {
    font-size: 13px; /* 或者你想要的任何大小 */
    color: #172329;
}
</style>
