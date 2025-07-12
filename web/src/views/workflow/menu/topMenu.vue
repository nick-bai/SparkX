<template>
	<div class="top-menu flex-center">
		<div class="menu-btn flex-center" @click="$emit('back')">
			<el-icon size="20" style="margin-right: 5px" color="var(--el-color-theme)"><ArrowLeft /></el-icon> 返回
		</div>
		<div class="menu-btn flex-center" @click="$emit('debug')">
			<el-icon size="22" style="margin-right: 5px" color="#17b26a"><VideoPlay /></el-icon> 调试
		</div>
		<div class="menu-btn flex-center" @click="$emit('save')">
			<el-icon size="21" style="margin-right: 5px" color="#6172f3"><Document /></el-icon> 保存
		</div>
		<!--<div class="menu-btn flex-center" @click="$emit('publish')">
			<el-icon size="22" style="margin-right: 5px" color="#6172f3"><Promotion /></el-icon> 发布
		</div>-->
	</div>
</template>

<script>
import {Promotion, VideoPlay, Document, ArrowLeft} from "@element-plus/icons-vue";

export default {
	components: {ArrowLeft, Document, Promotion, VideoPlay},
	data() {
		return {

		}
	}
}
</script>

<style scoped>
.top-menu {
	right: 0;
	padding-right: 20px;
	margin-top: 10px;
}
.menu-btn {
	padding: 5px 20px;
	border-radius: 5px;
	background: #fff;
	box-shadow: 0 0 #0000, 0 0 #0000, 0 1px 2px 0 rgba(16, 24, 40, 0.05);
	margin-left: 10px;
	cursor: pointer;
}
</style>
