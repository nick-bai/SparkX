<template>
	<div class="detail-box">
		<div class="detail-title">本次对话</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['agent.input'] }}
			</div>
		</div>
	</div>
	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">Agent回复</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['sys.agentContent'] }}
			</div>
		</div>
	</div>
	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">上下文记忆</div>
		<div class="detail-content">

			<div class="content-item" v-for="(item, index) in runtimeData.outputData['agent.context']" :key="index">
				<div class="item-left" v-if="item.type === 'SYSTEM'">系统角色：</div>
				<div class="item-left" v-if="item.type === 'AI'">Agent：</div>
				<div class="item-left" v-if="item.type === 'USER'">用户：</div>
				<div class="item-right" v-if="item.type !== 'USER'">{{ item.text }}</div>
				<div class="item-right" v-else>
					<p v-for="(item2, index2) in item.contents" :key="index2">{{ item2.text }}</p>
				</div>
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
		if (this.runtimeData.outputData['log.context'] !== '') {
			let context = decodeURIComponent(this.runtimeData.outputData['log.context'])
			if (context !== 'undefined') {
				this.runtimeData.outputData['agent.context'] = JSON.parse(context || '{}');
			}
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
