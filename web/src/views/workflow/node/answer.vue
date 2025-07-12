<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<component :is="iconComponent(`answer-node-icon`)" :show-name="false"/>
			<span class="node-name" v-if="no === 1">{{ name }}</span>
			<span class="node-name" v-else>{{ name }}{{ no - 1 }}</span>
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
			name: '回复',
			active: false,
		}
	},
	created() {
		this.no = this.getNode().store.data.data.no
	},
	mounted() {
		const node = this.getNode();
		// 监听数据
		node.on('change:data', ({ current }) => {
			this.active = current.checked
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
