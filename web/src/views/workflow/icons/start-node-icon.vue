<template>
	<div class="menu-icon" style="background: var(--el-color-theme);">
		<span class="iconfont icon-ai23" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">开始</span>
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
