<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`dataset-node-icon`)"/>
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
			<div>知识库</div>
			<div class="param-data">
				<div class="flex-center" v-for="(item, index) in form.datasets" :key="index" style="padding: 0 0 10px 0;" v-if="form.datasets.length > 0">
					<div class="flex-center dataset-item">
						<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
							<span class="iconfont icon-zhishiku" style="font-size: 18px !important;"></span>
						</div>
						<span class="node-name">{{ item.title }}</span>
					</div>
					<el-icon style="width: 40px;cursor: pointer;color: #F56C6C" @click="delDataset(index)">
						<Delete />
					</el-icon>
				</div>
				<div class="no-param flex-center-all" style="background: #f4f4f4;margin-right: 10px;">
					<div class="flex-center" @click="datasetVisible=true">
						<el-icon style="margin-top: 3px">
							<Plus />
						</el-icon>
						<div style="margin-left: 5px">添加知识库</div>
					</div>
				</div>
			</div>

			<div class="flex-center no-param" style="margin-top: 10px;background: #fff">
				<span style="width: 80px;color: #1a1a1a">相似度</span>
				<el-slider
					:step="0.01"
					v-model="form.similarity"
					:min="0"
					:max="1"
					@change="$emit('dataChange', form)"
					show-input>
				</el-slider>
			</div>
			<div class="flex-center no-param" style="margin-top: 10px;background: #fff">
				<span style="width: 80px;color: #1a1a1a">召回数量</span>
				<el-slider
					v-model="form.topRank"
					:min="1"
					:max="10"
					@change="$emit('dataChange', form)"
					show-input>
				</el-slider>
			</div>
		</div>

		<el-dialog
			title="选择知识库"
			v-model="datasetVisible"
			width="800px"
			destroy-on-close
			:close-on-click-modal="false"
			class="select-dataset">
			<dataset-dialog @success="handleSuccess" @doClose="datasetVisible=false"></dataset-dialog>
		</el-dialog>
	</div>

</template>

<script>

import {Delete, MoreFilled, Plus} from "@element-plus/icons-vue"
import datasetDialog from "@/components/dataset/multiple.vue"
import {iconComponent} from "@/views/workflow/icons/index.js"

export default {
	components: {MoreFilled, datasetDialog, Plus, Delete},
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
			options: [],
			inputData: [], // 入参
			datasetVisible: false
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
		// 处理选择的知识库
		handleSuccess(datasets) {
			this.form.datasets = []
			datasets.forEach(dataset => {
				this.form.datasets.push({
					datasetId: dataset.datasetId,
					title: dataset.title
				})
			})
			this.datasetVisible = false

			this.$emit("dataChange", this.form)
		},
		// 删除知识库
		delDataset(index) {
			this.form.datasets.splice(index, 1)
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
.dataset-item {
	width: calc(100% - 40px);
	padding: 10px;
	background: #f4f4f4;
	border-radius: 5px;
}
.no-param {
	width: 100%;
	height: 30px;
	border-radius: 5px;
	color: #98A2B2;
	cursor: pointer;
}
</style>
