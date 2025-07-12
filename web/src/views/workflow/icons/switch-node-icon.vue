<template>
	<div class="menu-icon" style="background: #6172f3;">
		<span class="iconfont icon-fenzhi" style="font-size: 18px !important;"></span>
	</div>
	<span class="node-name" v-if="showName">条件分支</span>
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
