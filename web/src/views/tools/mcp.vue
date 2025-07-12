<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="700" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="插件名称" prop="title">
				<el-input v-model="form.title" maxlength="25" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="插件标识" prop="name">
				<el-input v-model="form.name" maxlength="25" show-word-limit placeholder="传给ai的标识，仅可包含字母和下划线"></el-input>
			</el-form-item>
			<el-form-item label="插件描述" prop="description">
				<el-input type="textarea" v-model="form.description" maxlength="125" show-word-limit :rows="4"></el-input>
			</el-form-item>
			<el-form-item label="SSE地址" prop="apiUrl">
				<el-input v-model="form.apiUrl"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
export default {
	emits: ['success', 'closed'],
	data() {
		return {
			mode: "add",
			titleMap: {
				add: '新增MCP插件',
				edit: '编辑MCP插件',
				show: '查看'
			},
			loading: false,
			visible: false,
			noticeVisible: false,
			form: {
				name: '',
				title: '',
				description: '',
				apiUrl: '',
				type: 2
			},
			rules: {
				title: [
					{required: true, message: '插件名称不能为空', trigger: 'blur'}
				],
				name: [
					{required: true, message: '插件标识不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '插件描述不能为空', trigger: 'blur'}
				],
				apiUrl: [
					{required: true, message: '接口地址不能为空', trigger: 'blur'}
				]
			}
		}
	},
	methods: {
		// 显示
		open(mode = 'add') {
			this.mode = mode
			this.visible = true

			return this
		},
		// 表单提交方法
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res
					if (this.mode === "add") {
						res = await this.$API.tool.addMcp.post(this.form)
					} else {
						res = await this.$API.tool.editMcp.post(this.form)
					}

					this.loading = false
					if (res.code === 0) {
						this.$message.success(res.msg)
						this.$emit('success')
					} else {
						this.$message.error(res.msg)
					}
				} else {
					return false;
				}
			})
		},
		setData(row) {
			this.form = row
		}
	}
}
</script>

<style scoped>

</style>
