<template>
	<div class="menu-icon" style="background: #06ae4d;">
		<span class="iconfont icon-pinglun3-copy" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">回复</span>
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
