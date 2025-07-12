<template>
	<el-dialog title="编辑模型" v-model="visible" :width="700" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
			<el-form-item label="模型厂家" prop="name">
				<el-input v-model="form.name" maxlength="25" show-word-limit></el-input>
			</el-form-item>
			<el-form-item label="模型类型" prop="type">
				<el-select v-model="form.type" placeholder="请选择类型" style="width:100%">
					<el-option label="语言模型" value="1"></el-option>
					<el-option label="向量模型" value="2"></el-option>
					<el-option label="重排模型" value="3"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item
				v-for="(item, index) in credential"
				:key="index">
				<template #label>
					<span style="margin-right: 3px;color: var(--el-color-danger)"
						  v-if="form.status === 1">*</span>
					{{ item.field }}
				</template>
				<el-input v-model="item.value"></el-input>
			</el-form-item>
			<el-form-item v-if="url">
				<template #label>
					<span style="margin-right: 3px;color: var(--el-color-danger)">*</span> 模型地址
				</template>
				<el-input v-model="url.value"></el-input>
			</el-form-item>
			<el-form-item label="可用模型" prop="models">
				<el-select v-model="modelsArr" multiple placeholder="请选择" style="width: 100%">
					<el-option
						v-for="item in modelsOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value">
					</el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="函数调用" v-if="form.type == 1">
				<el-select v-model="functionArr" multiple placeholder="请选择" style="width: 100%">
					<el-option
						v-for="item in functionOptions"
						:key="item.value"
						:label="item.label"
						:value="item.value">
					</el-option>
				</el-select>
			</el-form-item>
			<el-form-item :label="temperature.name" v-if="temperature">
				<el-slider
					v-model="temperature.value"
					:min="temperature.range[0]"
					:max="temperature.range[1]"
					show-input>
				</el-slider>
			</el-form-item>
			<el-form-item :label="maxOutputTokens.name" v-if="maxOutputTokens">
				<el-slider
					v-model="maxOutputTokens.value"
					:min="maxOutputTokens.range[0]"
					:max="maxOutputTokens.range[1]"
					show-input>
				</el-slider>
			</el-form-item>
			<el-form-item label="状态">
				<el-switch
					v-model="form.status"
					:active-value="1"
					:inactive-value="2">
				</el-switch>
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
			mode: "edit",
			form: {},
			rules: {
				name: [
					{required: true, message: '知识库标题不能为空', trigger: 'blur'}
				],
				type: [
					{required: true, message: '模型类型不能为空', trigger: 'blur'}
				],
				models: [
					{required: true, message: '可用模型不能为空', trigger: 'blur'}
				]
			},
			loading: false,
			visible: false,
			modelsArr: [],
			modelsOptions: [],
			functionArr: [],
			functionOptions: [],
			url: '',
			credential: [],
			temperature: null, // 温度
			maxOutputTokens: null // 最大输出
		}
	},
	methods: {
		//显示
		open(mode = 'edit') {
			this.mode = mode
			this.visible = true
			return this
		},
		// 获取模型信息
		async getModelInfo(id) {
			let res = await this.$API.models.info.get({modelId: id})
			this.form = res.data

			// 认证信息
			this.credential = JSON.parse(res.data.credential)

			// 基础配置项
			if (res.data.options !== '') {
				let options = JSON.parse(res.data.options)
				options.forEach((item) => {
					if (item.field === 'temperature') {
						this.temperature = item
					}

					if (item.field === 'maxOutputTokens') {
						this.maxOutputTokens = item
					}

					if (item.field === 'url') {
						this.url = item
					}
				})
			}

			this.modelsArr = []
			this.modelsOptions = []
			res.data.models.split(",").forEach(item => {
				this.modelsOptions.push({
					label: item,
					value: item
				})

				this.modelsArr.push(item)
			})

			this.functionArr = []
			this.functionOptions = []
			res.data.functionCalling.split(",").forEach(item => {
				this.functionOptions.push({
					label: item,
					value: item
				})

				this.functionArr.push(item)
			})
		},
		// 表单提交方法
		optSubmit(formName) {

			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.loading = true
					this.form.credential = JSON.stringify(this.credential)
					this.form.models = this.modelsArr.join(",")

					let options = []
					if (this.temperature) {
						options.push(this.temperature)
					}

					if (this.maxOutputTokens) {
						options.push(this.maxOutputTokens)
					}

					if (this.url) {
						options.push(this.url)
					}

					this.form.options = JSON.stringify(options)
					let res = await this.$API.models.edit.post(this.form)

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
			this.getModelInfo(row.modelId)
		}
	}
}
</script>

<style scoped>

</style>
