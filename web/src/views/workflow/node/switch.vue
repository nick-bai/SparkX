<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<component :is="iconComponent(`switch-node-icon`)" :show-name="false"/>
			<span class="node-name" v-if="no === 1">{{ name }}</span>
			<span class="node-name" v-else>{{ name }}{{ no - 1 }}</span>
		</div>

		<div class="tips-text" style="flex-direction: column;display: flex" v-for="(item, index) in nodeInnerData.ifBranch" :key="index">
			<div class="flex-center" style="justify-content: space-between">
				<div v-if="index === 0">IF</div>
				<div v-else>ELSEIF</div>
				<div style="font-size: 12px" v-if="item.switch === 1">AND</div>
				<div style="font-size: 12px" v-if="item.switch === 2">OR</div>
			</div>
			<div class="flex-center tips-item" v-for="(item2, index2) in item.data" :key="index2">
				<span class="iconfont icon-bianliang" style="font-size: 20px !important;color: #6172f3"></span>
				<div class="line1">{{ item2.input[1] }} {{ optionsMap.get(item2.tips) }} {{ item2.value }}</div>
			</div>
		</div>

		<div class="tips-text">
			<div>ELSE</div>
		</div>
	</div>
</template>

<script>
import initConfig from '@/views/workflow/initConfig.js';
import {iconComponent} from "@/views/workflow/icons/index.js";

export default {
	inject: ["getGraph", "getNode"],
	data() {
		return {
			no: 0,
			name: "条件分支",
			nodeInnerData: {},
			active: false,
			optionsMap: new Map()
		}
	},
	created() {
		let nodeData = this.getNode().store.data.data
		this.no = nodeData.no
		this.nodeInnerData = nodeData

		let options = JSON.parse(JSON.stringify(initConfig.switchOptions))
		options.forEach(item => {
			this.optionsMap.set(item.type, item.label)
		})
	},
	mounted() {
		const node = this.getNode();
		// 监听数据
		node.on('change:data', ({ current }) => {
			this.active = current.checked
			this.nodeInnerData = current
		})
	},
	methods: {
		iconComponent

	}
}
</script>

<style scoped>
.tips-text {
	width: 100%;
	background: #f4f4f4;
	padding: 5px 10px;
	border-radius: 5px;
	margin-top: 10px;
}
.tips-item {
	background: #fff;
	padding: 5px 10px;
	border-radius: 5px;
	font-size: 12px;
	margin-top: 5px;
}
</style>
