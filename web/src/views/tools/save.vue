<template>
	<div style="padding: 10px 30px">
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
			<el-form-item label="接口地址" prop="apiUrl">
				<el-input v-model="form.apiUrl"></el-input>
			</el-form-item>
			<el-form-item label="鉴权方式" prop="authType">
				<el-radio :label="1" v-model="form.authType">无需鉴权</el-radio>
				<el-radio :label="2" v-model="form.authType">API KEY</el-radio>
			</el-form-item>
			<el-form-item v-if="form.authType === 2">
				<template #label>
					<span style="color: var(--el-color-danger);margin-right: 5px;">*</span> 秘钥位置
				</template>
				<el-select v-model="form.authWay" style="width: 100%">
					<el-option label="header" :value="1" />
					<el-option label="body" :value="2" />
				</el-select>
			</el-form-item>
			<el-form-item v-if="form.authType === 2">
				<template #label>
					<span style="color: var(--el-color-danger);margin-right: 5px;">*</span> 秘钥名称
				</template>
				<el-input v-model="form.apiKeyName"></el-input>
			</el-form-item>
			<el-form-item v-if="form.authType === 2">
				<template #label>
					<span style="color: var(--el-color-danger);margin-right: 5px;">*</span> 秘钥值
				</template>
				<el-input v-model="form.apiKeyValue"></el-input>
			</el-form-item>
			<el-form-item label="接口参数" prop="postParams">
				<el-button size="small" type="primary" icon="el-icon-plus" @click="addParam">添加参数</el-button>
				<el-table :data="tableData">
					<el-table-column prop="field" label="字段名称"/>
					<el-table-column prop="type" label="字段类型"/>
					<el-table-column prop="desc" label="字段描述" />
					<el-table-column prop="required" label="是否必填">
						<template #default="scope">
							<span v-if="scope.row.required === 1">必填</span>
							<span v-else>非必填</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="operation"
						label="操作">
						<template #default="scope">
							<el-button @click="handleDel(scope.$index)" type="text" size="small">删除</el-button>
						</template>
					</el-table-column>
				</el-table>
			</el-form-item>
			<div class="dialog-footer" style="float:right;">
				<el-button @click="$emit('closed')">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')" :loading="loading">确 定</el-button>
			</div>
		</el-form>
	</div>

	<params-dialog v-if="paramsVisible" ref="paramsDialog" @success="handleSuccess"></params-dialog>
</template>

<script>
import ParamsDialog from "./param.vue";

export default {
	components: { ParamsDialog },
	emits: ['success', 'closed'],
	data() {
		return {
			mode: "add",
			loading: false,
			paramsVisible: false,
			form: {
				name: '',
				title: '',
				description: '',
				apiUrl: '',
				authType: 2,
				authWay: 1,
				apiKeyName: '',
				apiKeyValue: '',
				postParams: '',
				type: 1
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
				],
				authType: [
					{required: true, message: '鉴权方式不能为空', trigger: 'blur'}
				],
				postParams: [
					{required: true, message: '接口参数不能为空', trigger: 'blur'}
				]
			},
			tableData: []
		}
	},
	methods: {
		// 显示
		open(mode = 'add') {
			this.mode = mode

			return this
		},
		// 提交保存
		optSubmit(formName) {
			this.form.postParams = JSON.stringify(this.tableData)

			this.$refs[formName].validate(async (valid) => {
				if (valid) {

					if (!/^[a-zA-Z_]+$/.test(this.form.name)) {
						this.$message.error('插件标识只包含英文字母和下划线')
						return false
					}

					if (this.form.authType === 2 &&
						(this.form.apiKeyName === ''|| this.form.apiKeyValue === '')) {
						this.$message.error('秘钥信息不能为空')
						return false
					}

					let res
					if (this.mode === 'add') {
						res = await this.$API.tool.add.post(this.form)
					} else {
						res = await this.$API.tool.edit.post(this.form)
					}

					if (res.code === 0) {
						this.$message.success(res.msg)
						this.$emit('success')
					} else {
						this.$message.error(res.msg)
					}
				}
			})
		},
		// 删除字段
		handleDel(index) {
			this.tableData.splice(index, 1)
		},
		// 添加参数
		addParam() {
			this.paramsVisible = true
			this.$nextTick(() => {
				this.$refs.paramsDialog.open()
			})
		},
		// 处理添加字段
		handleSuccess(row) {
			this.paramsVisible = false
			this.tableData.push(row)
		},
		// 设置参数
		setData(row) {
			this.form = row
			this.tableData = JSON.parse(row.postParams)
		}
	}
}
</script>

<style scoped>

</style>
