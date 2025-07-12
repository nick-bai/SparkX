<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`purpose-node-icon`)"/>
			</div>
		</div>

		<div class="set-content-box">
			<div>输入参数</div>
			<div class="flex-center" style="margin-top: 10px;">
				<div>查询内容</div>
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

		<div class="set-content-box">
			<div>模型</div>
			<div class="param-data">
				<el-cascader
					v-model="modelId"
					:options="options"
					:show-all-levels="false"
					style="width: 100%"
					@change="handleChange"
					clearable>
				</el-cascader>
			</div>
			<div class="flex-center no-param" style="margin-top: 10px;background: #fff">
				<span style="width: 60px;color: #1a1a1a">温度</span>
				<el-slider
					v-model="form.modelInfo.temperature"
					:min="temperatureConfig.range[0]"
					:max="temperatureConfig.range[1]"
					step="0.01"
					show-input>
				</el-slider>
			</div>
		</div>

		<div class="set-content-box">
			<div style="justify-content: space-between" class="flex-center">
				<span>意图分类</span>
			</div>
			<div class="param-data">
				<div class="flex-center" v-for="(item, index) in form.cateList" :key="index" style="padding: 0 0 10px 0;">
					<el-input v-model="item.name" placeholder="请输入内容"></el-input>
					<el-icon style="width: 40px;cursor: pointer;color: #F56C6C" v-if="index > 0" @click="delCate(index)">
						<Delete />
					</el-icon>
					<div style="width: 40px" v-else></div>
				</div>
				<div class="no-param flex-center-all" @click="addCate">
					<el-icon style="margin-right: 10px;">
						<Plus />
					</el-icon> 添加分类
				</div>
			</div>
		</div>
	</div>

</template>

<script>
import {Delete, MoreFilled, Plus} from "@element-plus/icons-vue";
import {iconComponent} from "@/views/workflow/icons/index.js";

export default {
	components: {MoreFilled, Delete, Plus},
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
	data() {
		return {
			dialogVisible: false,
			form: {},
			temperatureConfig: {
				range: [0, 1]
			},
			modelId: [],
			options: [],
			inputData: [], // 入参
		}
	},
	created() {
		this.form = this.formData
		this.modelId = [this.formData.modelInfo.modelId, this.formData.modelInfo.modelName]
		this.inputData = this.formData.inputData
		this.getModelsList()
	},
	methods: {
		iconComponent,
		// 添加分类
		addCate() {
			this.form.cateList.push({
				name: ""
			})

			this.$emit("portAdd", this.form)
		},
		// 删除分类
		delCate(index) {
			this.form.cateList.splice(index, 1)

			this.$emit("portDel", this.form)
		},
		// 获取模型信息
		async getModelInfo(modelId) {
			let res = await this.$API.models.info.get({modelId: modelId})
			let setOptions = JSON.parse(res.data.options)
			setOptions.forEach(item => {
				if (item.field === 'temperature') {
					this.temperatureConfig = item
				}
			})
			this.form.modelInfo.temperature = this.temperatureConfig.value

			this.$emit("dataChange", this.form)
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
		// 选择了模型
		handleChange(val) {
			this.form.modelInfo.modelId = val[0]
			this.form.modelInfo.modelName = val[1]

			this.getModelInfo(val[0])
			this.$emit("dataChange", this.form)
		},
		// 输入选择
		inputChange(val) {
			this.form.inputData = val
			this.$emit("dataChange", this.form)
		},
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
