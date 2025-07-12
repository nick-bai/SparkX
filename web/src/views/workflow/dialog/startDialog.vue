<template>
	<div class="opt-form">
		<div class="flex-center title">
			<component :is="iconComponent(`start-node-icon`)"/>
		</div>

		<div class="set-content-box">
			<div>系统变量</div>
			<div class="param-data">
				<div class="flex-center data-item" v-for="(item, index) in formData.sysData" :key="index">
					<div class="flex-center">
						<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
							<span class="iconfont icon-bianliang" style="font-size: 16px !important;"></span>
						</div>
						<div class="title" style="margin-left: 10px">{{ item.field }}</div>
					</div>
					<div class="field">{{ item.name }}</div>
				</div>
			</div>
		</div>
		<!--<div class="set-content-box">
			<div style="justify-content: space-between" class="flex-center">
				<span>自定义变量</span>
				<el-button
					icon="el-icon-Plus"
					type="primary"
					link
					@click="addParam"
				>
					添加变量
				</el-button>
			</div>
			<div class="param-data">
				<div class="flex-center data-item" v-for="(item, index) in formData.userData" :key="index"
					 v-if="formData.userData && formData.userData.length > 0">
					<div class="flex-center">
						<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
							<span class="iconfont icon-bianliang" style="font-size: 16px !important;"></span>
						</div>
						<div class="title" style="margin-left: 10px">{{ item.field }}</div>
					</div>
					<div class="field">{{ item.name }}</div>
				</div>
				<div class="no-param flex-center-all" v-else>
					设置的变量可在工作流程中使用
				</div>
			</div>
		</div>-->
	</div>

	<!--<el-dialog title="添加变量" v-model="dialogVisible" width="400px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="form" :rules="rules" ref="ruleForm" label-width="80px">
			<el-form-item label="变量字段" prop="field">
				<el-input v-model="form.field" placeholder="例如 user.title"></el-input>
			</el-form-item>
			<el-form-item label="变量名" prop="name">
				<el-input v-model="form.name" placeholder="例如 用户昵称"></el-input>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit('ruleForm')">确 定</el-button>
			</div>
		</template>
	</el-dialog>-->
</template>

<script>
import {HomeFilled, Plus} from "@element-plus/icons-vue"
import {iconComponent} from "@/views/workflow/icons/index.js";

export default {
	components: {Plus, HomeFilled},
	props: {
		formData: {
			type: Object,
			default: () => {}
		}
	},
	data() {
		return {
			dialogVisible: false,
			form: {
				field: '',
				name: ''
			},
			rules: {
				field: [
					{required: true, message: '请输入字段', trigger: 'blur'}
				],
				name: [
					{required: true, message: '请输入名称', trigger: 'blur'}
				]
			},
		}
	},
	mounted() {},
	methods: {
		iconComponent,
		// 创建变量
		optSubmit(formName) {
			this.$refs[formName].validate(async (valid) => {
				if (valid) {
					this.formData.userData.push(this.form)
					this.dialogVisible = false
				} else {
					return false;
				}
			})
		},
		addParam() {
			this.dialogVisible = true
		}
	}
}
</script>

<style scoped>
.opt-form {
	width: 100%;
	height: calc(100vh - 200px);
	background: #f4f4f4;
	border-radius: 5px;
	padding: 20px;
}
.node-name {
	margin-left: 10px;
	font-weight: bold;
}
.param-data {
	width: 100%;
	border-radius: 5px;
	margin-top: 10px;
	display: flex;
	flex-direction: column;
}
.data-item {
	width: 100%;
	padding: 10px;
	height: 40px;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 10px;
	background: #f4f4f4;
	border-radius: 5px;
}
.no-param {
	width: 100%;
	height: 40px;
	background: #f4f4f4;
	border-radius: 5px;
	color: #98A2B2;
}
</style>
