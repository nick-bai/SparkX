<template>
	<div class="detail-box">
		<div class="detail-title">判断结果</div>
		<div class="detail-content">
			<div class="content-item">
				{{ convertToUpperCase(runtimeData.outputData['switch.result']) }}
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		runtimeData: {
			type: Object,
			default: () => ({})
		}
	},
	data() {
		return {
			show: false
		}
	},
	mounted() {

	},
	methods: {
		 convertToUpperCase(str) {
			let result = '';
			for (let i = 0; i < str.length; i++) {
				result += str[i].toUpperCase();
			}
			return result;
		}
	}
}
</script>

<style scoped>
.detail-box {
	background: #f5f6f7;
	border-radius: 4px;
	height: 100%;
	width: 100%;
}
.detail-box .detail-title {
	border-bottom: 1px dashed #dee0e3;
	padding: 8px 12px;
	font-weight: bold;
}
.detail-box .detail-content {
	padding: 8px 12px;
}
.content-item {
	display: flex;
	align-items: center;
	margin-top: 5px;
}
.content-item .item-left {
	color: #646a73;
}
</style>
