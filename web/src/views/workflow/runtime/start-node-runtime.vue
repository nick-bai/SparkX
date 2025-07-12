<template>
	<div class="detail-box">
		<div class="detail-title">输入参数</div>
		<div class="detail-content">
			<div class="content-item">
				<div class="item-left">用户问题：</div>
				<div class="item-right">{{ runtimeData.outputData['sys.question'] }}</div>
			</div>
			<div class="content-item">
				<div class="item-left">提问时间：</div>
				<div class="item-right">{{ runtimeData.outputData['sys.time'] }}</div>
			</div>
			<div class="content-item">
				<div class="item-left">用户IP：</div>
				<div class="item-right">{{ runtimeData.outputData['sys.ip'] }}</div>
			</div>
			<div class="content-item">
				<div class="item-left">应用ID：</div>
				<div class="item-right">{{ runtimeData.outputData['sys.appId'] }}</div>
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
