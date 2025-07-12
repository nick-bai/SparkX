<template>
	<div class="menu-icon" style="background: #f79009;">
		<span class="iconfont icon-fenlei" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">意图分类</span>
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
