<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="500" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="字段名" prop="field">
				<el-input v-model="form.field" placeholder="只包含英文和下划线"></el-input>
			</el-form-item>
			<el-form-item label="字段类型" prop="type">
				<el-select v-model="form.type" style="width: 100%">
					<el-option label="String" value="String" />
					<el-option label="Int" value="Int" />
					<el-option label="Double" value="Double" />
					<el-option label="Boolean" value="Boolean" />
				</el-select>
			</el-form-item>
			<el-form-item label="字段描述" prop="desc">
				<el-input v-model="form.desc" type="textarea" :rows="3" placeholder="尽量描述的精确以便ai理解"></el-input>
			</el-form-item>
			<el-form-item label="是否必填" prop="required">
				<el-radio :label="1" v-model="form.required">必填</el-radio>
				<el-radio :label="2" v-model="form.required">非必填</el-radio>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')">确 定</el-button>
			</div>
		</template>
	</el-dialog>
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
				add: '添加字段'
			},
			form: {
				field: "",
				type: "",
				desc: "",
				required: 1
			},
			rules: {
				field: [
					{required: true, message: '知识库标题不能为空', trigger: 'blur'}
				],
				type: [
					{required: true, message: '字段类型不能为空', trigger: 'blur'}
				],
				desc: [
					{required: true, message: '字段描述不能为空', trigger: 'blur'}
				],
				required: [
					{required: true, message: '是否必填不能为空', trigger: 'blur'}
				]
			},
			visible: false
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
					if (!/^[a-zA-Z_]+$/.test(this.form.field)) {
						this.$message.error('字段名只包含英文字母和下划线')
						return false
					}

					this.$emit('success', this.form)
				}
			})
		},
	}
}
</script>

<style scoped>

</style>
