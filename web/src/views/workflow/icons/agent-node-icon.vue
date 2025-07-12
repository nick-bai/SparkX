<template>
	<div class="menu-icon" style="background: #17b26a;">
		<span class="iconfont icon-a-agent1" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">Agent</span>
</template>

<script>
export default {
	props: {
		showName: {
			type: Boolean,
			default: true
		}
	}
}
</script>

<style scoped>

</style>
