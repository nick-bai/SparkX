<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="600" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="AI模型" prop="modelId">
				<el-cascader
					v-model="modelId"
					:options="options"
					:show-all-levels="false"
					style="width: 100%"
					clearable>
				</el-cascader>
			</el-form-item>
			<el-form-item label="提示词" prop="prompt">
				<el-input v-model="form.prompt" type="textarea" :rows="8"></el-input>
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
				add: '生成问题',
			},
			form: {
				documentIds: '',
				modelId: '',
				prompt: '内容：{data}\n' +
					'\n' +
					'请总结上面的内容，并根据内容总结生成 5 个问题。\n' +
					'回答要求：\n' +
					'- 请只输出问题；\n' +
					'- 请将每个问题放置<question></question>标签中。'
			},
			rules: {
				modelId: [
					{required: true, message: 'ai模型不能为空', trigger: 'blur'}
				],
				prompt: [
					{required: true, message: '提示词不能为空', trigger: 'blur'}
				]
			},
			loading: false,
			visible: false,
			modelId: [],
			options: [], // 模型列表
		}
	},
	methods: {
		//显示
		open(mode = 'add') {
			this.mode = mode
			this.visible = true
			this.getModelsList()

			return this
		},
		// 获取模型列表
		async getModelsList() {
			let res = await this.$API.models.list.get({type: 1, status: 1})
			this.options = []
			res.data.forEach(item => {

				let info = {
					label: item.name,
					value: item.modelId,
					children: []
				}
				let option = []
				item.models.split(",").forEach(item => {
					option.push({
						label: item,
						value: item
					})
				})
				info.children = option

				this.options.push(info)
			})
		},
		// 表单提交方法
		optSubmit(formName) {
			this.form.modelId = this.modelId.join(',')

			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res = await this.$API.document.question.post(this.form);

					this.loading = false
					if (res.code == 0) {
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
			this.form.documentIds = row.join(',')
		}
	}
}
</script>

<style scoped>

</style>
