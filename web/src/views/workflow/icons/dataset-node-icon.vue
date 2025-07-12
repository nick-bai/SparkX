<template>
	<div class="menu-icon" style="background: #6172f3;">
		<span class="iconfont icon-zhishiku" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">知识检索</span>
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
