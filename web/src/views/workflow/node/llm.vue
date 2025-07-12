<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<component :is="iconComponent(`llm-node-icon`)" :show-name="false"/>
			<span class="node-name" v-if="no === 1">{{ name }}</span>
			<span class="node-name" v-else>{{ name }}{{ no - 1 }}</span>
		</div>

		<div class="flex-center tips-text">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 2px;border-radius: 5px;">
				<span class="iconfont icon-a-zhuliudeLLM"></span>
			</div>
			<span style="font-size: 13px;margin-left: 5px" class="line1" v-if="nodeInnerData.modelInfo.modelName !== ''">{{ nodeInnerData.modelInfo.modelName }}</span>
			<span style="font-size: 13px;margin-left: 5px" class="line1" v-else>请设置语言模型</span>
		</div>
	</div>
</template>

<script>
import {iconComponent} from "@/views/workflow/icons/index.js";

export default {
	methods: {iconComponent},
	inject: ["getNode"],
	data() {
		return {
			no: 0,
			name: 'LLM',
			nodeInnerData: {},
			active: false,
		}
	},
	created() {
		let nodeData = this.getNode().store.data.data
		this.no = nodeData.no
		this.nodeInnerData = nodeData
	},
	mounted() {
		const node = this.getNode();
		// 监听数据
		node.on('change:data', ({ current }) => {
			this.active = current.checked
			this.nodeInnerData = current
		})
	}
}
</script>

<style scoped>
.tips-text {
	width: 100%;
	height: 30px;
	background: #f4f4f4;
	padding: 5px 10px;
	border-radius: 5px;
	margin-top: 10px;
}
</style>
