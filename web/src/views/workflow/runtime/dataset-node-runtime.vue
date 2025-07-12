<template>
	<div class="detail-box">
		<div class="detail-title">检索内容</div>
		<div class="detail-content">
			<div class="content-item">
				{{ runtimeData.outputData['sys.question'] }}
			</div>
		</div>
	</div>

	<div class="detail-box" style="margin-top: 10px">
		<div class="detail-title">检索结果</div>
		<div class="detail-content">

			<div class="content-item" v-for="item in datasetsList" :key="item.paragraphId">
				<div class="paragraph-item">
					<div class="paragraph-id">段落ID: {{ item.paragraphId }}</div>
					<div class="paragraph-title">
						<div class="title-left line1" v-if="item.title !== ''">{{ item.title }}</div>
						<div class="title-left line1" v-else>--</div>
					</div>
					<el-scrollbar class="paragraph-doc">
						{{ item.content }}
					</el-scrollbar>
					<div class="paragraph-bottom">
						<span>相似度：{{ item.similarity.toFixed(2) }}</span>
						<span>来源文档：{{ item.documentName }}</span>
					</div>
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
			show: false,
			datasetsList: []
		}
	},
	mounted() {
		if (this.runtimeData.outputData['datasets.search'] !== '') {
			this.datasetsList = JSON.parse(this.runtimeData.outputData['datasets.search'])
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
.paragraph-item {
	width: 100%;
	background: #fff;
	border-radius: 5px;
	cursor: pointer;
	padding: 15px 12px 15px 12px;
	margin-right: 5px;
	flex-direction: column;
	align-items: flex-start;
}
.paragraph-title {
	width: 100%;
	height: 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.title-left {
	width: 320px;
}
.paragraph-doc {
	width: 100%;
	height: calc(100% - 83px);
	color: #606266;
	margin-top: 10px;
}
.paragraph-id {
	color: #5c5f66;
	border-bottom: 1px solid #e2e2e2;
	font-size: 13px;
	padding-bottom: 5px;
}
.paragraph-bottom {
	width: 100%;
	height: 30px;
	margin-top: 10px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-bottom: 10px;
	color: #5c5f66;
}
</style>
