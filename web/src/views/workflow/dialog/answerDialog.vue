<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`answer-node-icon`)"/>
			</div>
		</div>

		<div class="set-content-box">
			<div>回复内容</div>
			<div class="flex-center" style="background: #f4f4f4;padding: 10px;margin-top: 10px;">
				<div>内容来源</div>
				<el-switch
					style="margin-left: 20px"
					size="small"
					v-model="form.answerType"
					:active-value="1"
					:inactive-value="2"
					active-text="引用变量"
					inactive-text="自定义"
				/>
			</div>
			<div class="flex-center" style="background: #f4f4f4;padding: 10px;" v-if="form.answerType === 1">
				<div>引用变量</div>
				<el-cascader
					v-model="inputData"
					:options="inputOptions"
					@change="inputChange"
					style="margin-left: 20px;width: calc(100% - 80px)" clearable>
					<template #default="{ node, data }">
						<div class="flex-center">
							<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
							<span style="margin-left: 5px">{{ data.label }}</span>
						</div>
					</template>
				</el-cascader>
			</div>
			<div class="flex-center" style="background: #f4f4f4;padding: 10px;" v-if="form.answerType === 2">
				<div style="justify-content: space-between;width: 80px" class="flex-center">
					<span>自定义</span>
				</div>
				<el-input v-model="form.answer" type="textarea" placeholder="自定义回复内容" :rows="4" style="margin-top: 10px"/>
			</div>
		</div>

		<div class="set-content-box">
			<div>输出参数</div>
			<div class="param-data">
				<div class="flex-center data-item" v-for="(item, index) in form.outData" :key="index">
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
	</div>

</template>

<script>

import {MoreFilled} from "@element-plus/icons-vue";
import {iconComponent} from "@/views/workflow/icons/index.js"

export default {
	components: {MoreFilled},
	props: {
		formData: {
			type: Object,
			default: () => {}
		},
		inputOptions: {
			type: Array,
			default: []
		}
	},
	watch: {
		'form.answer':  {
			handler(val) {
				this.$emit("dataChange", this.form)
			}
		}
	},
	data() {
		return {
			dialogVisible: false,
			form: {},
			inputData: [], // 入参
		}
	},
	created() {
		this.form = this.formData
		this.inputData = this.formData.inputData
	},
	methods: {
		// 输入选择
		inputChange(val) {
			this.form.inputData = val
			this.$emit("dataChange", this.form)
		},
		iconComponent
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
	height: 30px;
	background: #f4f4f4;
	border-radius: 5px;
	color: #98A2B2;
	cursor: pointer;
}
</style>
