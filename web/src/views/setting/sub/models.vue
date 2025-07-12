<template>
	<div class="flex-center model-list">
		<div class="flex-center model-item" v-for="item in modelsList" :key="item.modelId">
			<img :src="domain + item.icon" style="width:44px;height:44px"/>
			<div class="info">
				<span class="line1 model-name">{{ item.name }}</span>
				<div class="status flex-center">
					<span class="success" v-if="item.status === 1"></span>
					<span class="text" v-if="item.status === 1">已启用</span>
					<span class="danger" v-if="item.status === 2"></span>
					<span class="text" v-if="item.status === 2">已禁用</span>
				</div>
			</div>
			<el-button type="primary" plain @click="edit(item)">编辑</el-button>
		</div>
	</div>

	<save-dialog v-if="dialogVisible" ref="saveDialog" @success="$emit('success')" @closed="dialogVisible=false"
				 :close-on-click-modal="false"></save-dialog>
</template>

<script>
import config from "@/config"
import saveDialog from "@/views/setting/sub/edit.vue";

export default {
	components: {saveDialog},
	props: {
		modelsList: {
			type: Array,
			default: []
		}
	},
	data() {
		return {
			domain: config.API_URL.replace("/api", ""),
			dialogVisible: false
		}
	},
	mounted() {
	},
	methods: {
		// 编辑模型
		edit(row) {
			this.dialogVisible = true

			this.$nextTick(() => {
				this.$refs.saveDialog.open().setData(row)
			})
		}
	}
}
</script>

<style scoped>
.model-list {
	width: 100%;
	flex-wrap: wrap;
}
.model-item {
	width: 300px;
	height: 100px;
	background: #f6f6f6;
	border-radius: 10px;
	margin: 10px;
	margin-right: 10px;
	padding: 0 10px;
}
.info {
	width: 160px;
	height: 50px;
	padding-left: 10px;
}
.model-name {
	font-size: 14px;
	font-weight: 700;
	width: 100%;
}
.status {
	margin-top: 10px;
}
.success {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: #67C23A;
}
.danger {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: #F56C6C;
}
.status .text {
	margin-left: 6px;
	color: #999;
}
</style>
