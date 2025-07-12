<template>
	<el-dialog :title="titleMap[mode]" v-model="visible" :width="700" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" label-position="top" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="应用名称" prop="name">
				<el-input v-model="form.name" maxlength="25" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="应用描述" prop="description">
				<el-input v-model="form.description" type="textarea" maxlength="255" show-word-limit :rows="5"></el-input>
			</el-form-item>
			<el-form-item label="应用类型" prop="type">
				<el-radio-group v-model="form.type" class="tool-radio-list">
					<el-radio :label="1" border class="radio-item">
						<div class="radio-title">简单Agent</div>
						<div class="radio-desc">适合新手创建小助手</div>
					</el-radio>
					<el-radio :label="2" border class="radio-item">
						<div class="radio-title">高级编排</div>
						<div class="radio-desc">适合高级用户自定义小助手的工作流</div>
					</el-radio>
				</el-radio-group>
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
import {Delete, Plus} from "@element-plus/icons-vue"
import noticeDialog from "@/components/notice/index.vue"

export default {
	components: {Plus, Delete, noticeDialog},
	emits: ['success', 'closed'],
	data() {
		return {
			mode: "add",
			titleMap: {
				add: '新增应用',
				edit: '编辑应用',
				show: '查看'
			},
			form: {
				appId: "",
				name: '',
				description: '',
				type: 1,
			},
			rules: {
				name: [
					{required: true, message: '应用标题不能为空', trigger: 'blur'}
				],
				description: [
					{required: true, message: '应用不能为空', trigger: 'blur'}
				],
				type: [
					{required: true, message: '应用类型', trigger: 'blur'}
				]
			},
			loading: false,
			visible: false,
			noticeVisible: false,
			notice: ""
		}
	},
	methods: {
		//显示
		open(mode = 'add') {
			this.mode = mode;
			this.visible = true;
			return this
		},
		// 表单提交方法
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					let res;
					if (this.mode === "add") {
						res = await this.$API.application.add.post(this.form);
					} else {
						res = await this.$API.application.edit.post(this.form);
					}

					this.loading = false
					if (res.code === 0) {
						this.$message.success('操作成功')
						this.$emit('success', res.msg)
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
		setData(row) {
			this.form.datasetId = row.datasetId
			this.form.title = row.title
			this.form.description = row.description
		}
	}
}
</script>

<style scoped>
.tool-radio-list {
	width: 100%;
	display: flex;
	padding-left: 10px;
}
.radio-item {
	width: 47%;
	padding: 40px 10px !important;
	font-size: 13px;
}
.radio-desc {
	margin-left: 10px;
}
.radio-title {
	margin-left: 10px;
}
</style>

