<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="search-box">
			<el-form :inline="true" :model="searchForm" class="demo-form-inline">
				<el-form-item label="登录账号">
					<el-input v-model="searchForm.name" placeholder="" clearable></el-input>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>

			<el-button type="primary" icon="el-icon-plus" @click="addUser" style="margin-top: -10px;margin-right: 20px">添加用户</el-button>
		</div>
		<div style="border-radius: 10px;background: #fff;padding: 0 5px 5px 5px">
			<el-table
				:header-cell-style="{background:'#f4f4f4'}"
				:data="tableData"
				style="width: 100%">
				<el-table-column
					prop="name"
					label="登录名">
				</el-table-column>
				<el-table-column
					prop="nickname"
					label="昵称">
				</el-table-column>
				<el-table-column
					label="状态">
					<template #default="scope">
						<el-tag type="success" v-if="scope.row.status === 1">正常</el-tag>
						<el-tag type="danger" v-else>禁用</el-tag>
					</template>
				</el-table-column>
				<el-table-column
					prop="operation"
					label="操作">
					<template #default="scope">
						<el-button @click="handleEdit(scope.row)" type="text" size="small">编辑</el-button>
						<el-button @click="handleDel(scope.row)" type="text" size="small">删除</el-button>
					</template>
				</el-table-column>
			</el-table>
		</div>

		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
	</div>

	<el-dialog :title="title" v-model="dialogVisible" width="500px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="80px">
			<el-form-item label="登录账号" prop="name">
				<el-input v-model="form.name" placeholder="建议字母+数字" maxlength="20" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="昵称" prop="nickname">
				<el-input v-model="form.nickname" maxlength="30" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="密码" prop="password" v-if="title === '添加用户'">
				<el-input v-model="form.password" placeholder="请输入密码"></el-input>
			</el-form-item>
			<el-form-item label="密码" v-else>
				<el-input v-model="form.password" placeholder="请输入密码"></el-input>
			</el-form-item>
			<el-form-item label="状态" prop="status">
				<el-radio v-model="form.status" :label="1">正常</el-radio>
				<el-radio v-model="form.status" :label="2">禁用</el-radio>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>

	<notice-dialog v-if="noticeVisible" ref="noticeBoxDialog"></notice-dialog>
</template>

<script>
import Pages from "@/components/pages/index.vue"
import noticeDialog from "@/components/notice/index.vue"

export default {
	components: {Pages, noticeDialog},
	data() {
		return {
			tableData: [],
			searchForm: {
				name: '',
				status: '',
				page: 1,
				limit: 10
			},
			page: {
				total: 0
			},
			title: '添加用户',
			dialogVisible: false,
			noticeVisible: false,
			form: {
				name: '',
				nickname: '',
				password: '',
				status: 1
			},
			loading: false,
			rules: {
				name: [
					{required: true, message: '请输入登录账号', trigger: 'blur'}
				],
				nickname: [
					{required: true, message: '请输入昵称', trigger: 'blur'}
				],
				password: [
					{required: true, message: '请输入密码', trigger: 'blur'}
				],
				status: [
					{required: true, message: '请选择状态', trigger: 'blur'}
				],
			}
		}
	},
	mounted() {
		this.getList()
	},
	methods: {
		async getList() {
			let res = await this.$API.users.list.get(this.searchForm)
			this.tableData = res.data.data
			this.page.total = res.data.total
		},
		// 初始化表单
		initForm() {
			this.form = this.$options.data().form
		},
		onSubmit() {
			this.getList()
		},
		addUser() {

			this.title = '添加用户'
			this.initForm()
			this.dialogVisible = true
		},
		handleEdit(row) {

			this.title = "编辑用户"
			this.initForm()

			this.form.userId = row.userId
			this.form.name = row.name
			this.form.nickname = row.nickname
			this.form.status = row.status

			this.dialogVisible = true
		},
		// 删除用户
		handleDel(item) {
			this.$confirm('此操作将永久删除该用户 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.user.del.get({userId: item.userId})
				if (res.code === 0) {
					this.$message.success(res.msg)
					this.getList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {
			});
		},
		// 分页
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		// 新增或编辑
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res;
					if (this.form.userId) {
						res = await this.$API.users.edit.post(this.form)
					} else {
						res = await this.$API.users.add.post(this.form)
					}
					this.loading = false
					if (res.code === 0) {
						this.dialogVisible = false
						this.$message.success('操作成功')
						this.initForm()
						this.getList()
					} else if (res.code === 403) {
						this.dialogVisible = false
						this.noticeVisible = true

						this.$nextTick(() => {
							this.$refs.noticeBoxDialog.open().setData({
								notice: res.msg
							})
						})

					} else {
						this.$message.error(res.msg)
					}
				}
			});
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
</style>
