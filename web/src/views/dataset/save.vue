<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="700" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="知识库标题" prop="title">
				<el-input v-model="form.title" maxlength="25" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="知识库描述" prop="description">
				<el-input v-model="form.description" type="textarea" maxlength="255" show-word-limit :rows="5"></el-input>
			</el-form-item>
			<el-form-item label="向量模型" v-if="mode === 'add'">
				<el-select
					@change="modelChange"
					v-model="form.embedding_model"
					placeholder="请选择"
					style="width:100%" clearable>
					<el-option-group
						v-for="group in modelOptions"
						:key="group.label"
						:label="group.label">
						<el-option
							v-for="item in group.options"
							:key="item.value"
							:label="item.label"
							:value="item">
						</el-option>
					</el-option-group>
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')" :loading="loading">确 定</el-button>
			</div>
		</template>
	</el-dialog>

	<notice-dialog v-if="noticeVisible" ref="noticeBoxDialog"></notice-dialog>
</template>

<script>
import noticeDialog from "@/components/notice/index.vue"

export default {
	components: {noticeDialog},
	emits: ['success', 'closed'],
	data() {
		return {
			mode: "add",
			titleMap: {
				add: '新增知识库',
				edit: '编辑知识库',
				show: '查看'
			},
			form: {
				datasetId: "",
				title: '',
				description: '',
				type: 1,
				embedding_model_id: '',
				embedding_model: ''
			},
			rules: {
				title: [
					{required: true, message: '知识库标题不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '知识库描述不能为空', trigger: 'blur'}
				]
			},
			loading: false,
			visible: false,
			noticeVisible: false,
			modelOptions: [], // 模型列表
		}
	},
	methods: {
		// 显示
		open(mode = 'add') {
			this.mode = mode
			this.visible = true
			this.getEmbeddingModel()

			return this
		},
		// 获取embedding模型
		async getEmbeddingModel() {
			let res = await this.$API.models.list.get({type: 2, status: 1})
			res.data.forEach(item => {

				let info = {
					label: item.name,
					value: item.modelId,
					options: []
				}
				let option = []
				item.models.split(",").forEach(item2 => {
					option.push({
						label: item2,
						value: item.modelId
					})
				})
				info.options = option

				this.modelOptions.push(info)
			})
		},
		// 表单提交方法
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res
					if (this.mode === "add") {
						res = await this.$API.dataset.add.post(this.form)
					} else {
						res = await this.$API.dataset.edit.post(this.form)
					}

					this.loading = false
					if (res.code === 0) {
						this.$message.success(res.msg)
						this.$emit('success')
					} else if (res.code === 403) {

						this.noticeVisible = true

						this.$nextTick(() => {
							this.$refs.noticeBoxDialog.open().setData({
								notice: res.msg
							})
						})
					} else {
						this.$message.error(res.msg)
					}
				} else {
					return false;
				}
			})
		},
		// 选择模型
		modelChange(row) {
			this.form.embedding_model_id = row.value
			this.form.embedding_model = row.label
		},
		setData(row) {
			this.form.datasetId = row.datasetId
			this.form.title = row.title
			this.form.description = row.description
		}
	}
}
</script>

<style scoped>

</style>

