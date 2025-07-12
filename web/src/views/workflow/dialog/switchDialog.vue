<template>
	<div class="opt-form">
		<div class="flex-center title" style="justify-content: space-between">
			<div class="flex-center">
				<component :is="iconComponent(`switch-node-icon`)"/>
			</div>
		</div>

		<div class="set-content-box">
			<div>条件分支</div>
			<div class="item-box" v-for="(item2, index) in form.ifBranch" :key="index" style="display: flex;flex-direction: column;">
				<div class="flex-center" style="justify-content: space-between">
					<div style="width: 60px" v-if="index === 0">IF</div>
					<div style="width: 60px" v-else>ELSEIF</div>
					<el-switch
						size="small"
						v-model="item2.switch"
						:active-value="1"
						:inactive-value="2"
						active-text="AND"
						inactive-text="OR">
					</el-switch>
				</div>

				<div style="flex-direction: column;display: flex;">
					<div v-for="(item3, index2) in item2.data" :key="index2" style="margin-top: 10px">
						<div class="tips-data flex-center">
							<el-cascader
								v-model="item3.input"
								:options="inputOptions"
								@change="$emit('dataChange', form)"
								style="width: 150px"
								clearable>
								<template #default="{ node, data }">
									<div class="flex-center">
										<span :class="data.icon" style="font-size: 18px !important;" :style="{color: data.color}"></span>
										<span style="margin-left: 5px">{{ data.label }}</span>
									</div>
								</template>
							</el-cascader>
							<el-select
								v-model="item3.tips"
								placeholder="请选择"
								style="width: 130px;margin-left: 5px;"
								@change="$emit('dataChange', form)"
								clearable
							>
								<el-option
									v-for="item in options"
									:key="item.type"
									:label="item.label"
									:value="item.type"
								/>
							</el-select>
							<el-input v-model="item3.value" style="width: 100px;margin-left: 5px" placeholder="" v-if="item3.tips > 2" @blur="$emit('dataChange', form)"/>
							<div style="width: 40px;" v-if="index === 0 && index2 === 0"></div>
							<el-icon style="width: 40px;cursor: pointer;color: #F56C6C" v-else @click="delBranch(index, index2)">
								<Delete />
							</el-icon>
						</div>
					</div>
					<div class="flex-center" style="margin-top: 10px;cursor: pointer" @click="addTips(index)">
						<el-icon style="margin-right: 5px;">
							<Plus />
						</el-icon> 添加条件
					</div>
				</div>

			</div>

			<div class="flex-center-all item-box" style="cursor: pointer" @click="addBranch">
				<el-icon style="margin-right: 5px;margin-top: 3px">
					<Plus />
				</el-icon> 添加 ELSEIF
			</div>

			<div class="flex-center item-box">
				<div style="width: 50px">ELSE</div>
			</div>
		</div>

		<div class="set-content-box" v-if="form.outData.length > 0">
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
import {Delete, MoreFilled, Plus} from "@element-plus/icons-vue";
import initConfig from "@/views/workflow/initConfig.js";
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
			value: "",
			input: "",
			options: JSON.parse(JSON.stringify(initConfig.switchOptions)),
			inputData: [], // 入参
		}
	},
	created() {
		this.form = this.formData
		this.inputData = this.formData.inputData
	},
	methods: {
		iconComponent,
		// 添加分支
		addBranch() {
			this.form.ifBranch.push({type: 'elseif', data: [{input: [], tips: "", value: ""}], switch: 1})
			this.$emit('portAdd', this.form)
		},
		// 删除分支
		delBranch(index, index2) {
			this.form.ifBranch[index].data.splice(index2, 1)
			if (this.form.ifBranch[index].data.length === 0) {
				this.form.ifBranch.splice(index, 1)
				this.$emit('portDel', this.form)
			} else {
				this.$emit('portUpdate', this.form, index)
			}
		},
		// 添加条件
		addTips(index) {
			this.form.ifBranch[index].data.push({input: [], tips: "", value: ""})
			this.$emit('portUpdate', this.form, index)
		}
	}
}
</script>

<style scoped>
.opt-form {
	width: 100%;
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
.item-box {
	margin-top: 10px;
	background: #f4f4f4;
	padding: 10px;
	border-radius: 5px;
}
</style>
