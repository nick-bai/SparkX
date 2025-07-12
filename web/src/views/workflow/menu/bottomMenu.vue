<template>
	<div class="flex-center menu-box">
		<el-tooltip
			class="box-item"
			effect="dark"
			content="缩小"
			placement="top-start"
		>
			<el-icon size="16" @click="$emit('zoomOut')"><ZoomOut /></el-icon>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="放大"
			placement="top-start"
		>
			<el-icon size="16" @click="$emit('zoomIn')"><ZoomIn /></el-icon>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="添加节点"
			placement="top-start"
		>
			<div :class="{'add-active': open}" class="add-btn">
				<el-icon size="16" @click="openMenu"><CirclePlus /></el-icon>
			</div>
		</el-tooltip>
		<el-tooltip
			class="box-item"
			effect="dark"
			content="居中显示"
			placement="top-start"
		>
			<el-icon size="16" @click="$emit('center')"><SetUp /></el-icon>
		</el-tooltip>
	</div>
</template>

<script>
import {CirclePlus, SetUp, ZoomIn, ZoomOut} from "@element-plus/icons-vue";

export default {
	components: {ZoomOut, ZoomIn, SetUp, CirclePlus},
	props: {
		outOpen: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			open: false
		}
	},
	mounted() {
		this.open = this.outOpen
	},
	methods: {
		openMenu() {
			this.open = !this.open;
			this.$emit("openMenu", this.open)
		}
	}
}
</script>

<style scoped>
.menu-box {
	width: 200px;
	height: 40px;
	border-radius: 10px;
	background: #fff;
	box-shadow: 0 0 #0000, 0 0 #0000, 0 1px 2px 0 rgba(16, 24, 40, 0.05);
	justify-content: space-between;
	padding: 0 20px;
}
.add-btn {
	padding: 5px;
	border-radius: 3px;
	display: flex;
	align-items: center;
}
.add-active {
	background-color: #155aef14;
	color: var(--el-color-theme);
}
</style>
