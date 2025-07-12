<!-- branch.vue -->
<template>
	<div class="node-base" :class="{'node-active': active}">
		<div class="flex-center">
			<component :is="iconComponent(`start-node-icon`)"/>
		</div>
	</div>
</template>

<script>
import {HomeFilled} from "@element-plus/icons-vue";
import {iconComponent} from "@/views/workflow/icons/index.js";

export default {
	methods: {iconComponent},
	components: {HomeFilled},
	inject: ["getNode"],
	data() {
		return {
			name: '开始',
			active: false,
		}
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

</style>
