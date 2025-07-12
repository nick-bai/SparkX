<template>
	<div style="background: #fff;border-radius: 10px;padding: 10px 5px">
		<div class="tool-bar">
			<div class="search-type" @click="dialogVisible = true">
				<el-icon>
					<Setting />
				</el-icon>
				<span v-if="searchForm.type === 'embedding'">向量检索</span>
				<span v-if="searchForm.type === 'text'">全文检索</span>
				<span v-if="searchForm.type === 'mix'">混合检索</span>
			</div>
			<div class="search-input" style="width: calc(100% - 200px);">
				<el-input v-model="searchForm.keyword" suffix-icon="el-icon-search" @keyup.enter.native="query" clearable/>
			</div>
		</div>
		<div class="hit-list">
			<div class="content" v-if="hitList.length > 0">
				<div class="paragraph-item" v-for="(item, index) in hitList" :key="index">
					<div class="paragraph-id">段落ID: {{ item.paragraphId }}</div>
					<div class="paragraph-title">
						<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
						<div class="title-left line1" v-else>--</div>
					</div>
					<el-scrollbar class="paragraph-doc">
						{{ item.content }}
					</el-scrollbar>
					<div class="paragraph-bottom">
						<span>相似度：{{ item.comprehensiveScore.toFixed(3) }}</span>
						<span>来源文档：{{ item.documentName }}</span>
					</div>
				</div>
			</div>
			<el-empty style="margin-top: 10%" description="无命中段落" v-loading="loading" v-else></el-empty>
		</div>
	</div>

	<!-- 设置检索模式 -->
	<el-dialog title="设置检索模式" v-model="dialogVisible" width="600px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="searchForm" label-width="10px">
			<el-form-item label="">
				<el-radio-group v-model="searchForm.type" class="too-radio-list">
					<el-radio border label="embedding" class="radio-item">
						<div class="radio-title">向量检索</div>
						<div class="radio-desc">通过向量距离计算与用户问题最相似的文本分段</div>
					</el-radio>
					<el-radio border label="text" class="radio-item">
						<div class="radio-title">全文检索</div>
						<div class="radio-desc">通过关键词检索，返回包含关键词最多的文本分段</div>
					</el-radio>
					<el-radio border label="mix" class="radio-item">
						<div class="radio-title">混合检索</div>
						<div class="radio-desc">同时执行全文检索和向量检索，再进行重排序，从两类查询结果中选择匹配用户问题的最佳结果</div>
					</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="">
				<div class="score-list">
					<div class="score-item">
						<div class="item-title">置信度高于</div>
						<div class="item-input"><el-input-number v-model="searchForm.similarity" :min="0" :max="1" step="0.01"></el-input-number></div>
					</div>
					<div class="score-item">
						<div class="item-title">召回数量</div>
						<div class="item-input"><el-input-number v-model="searchForm.topRank" :min="1"></el-input-number></div>
					</div>
				</div>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="dialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="dialogVisible = false">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
import {Setting} from "@element-plus/icons-vue";

export default {
	components: {Setting},
	data() {
		return {
			searchForm: {
				keyword: "",
				type: "embedding",
				similarity: 0.600,
				topRank: 5,
				datasetIds: ""
			},
			hitList: [],
			loading: false,
			dialogVisible: false
		}
	},
	watch: {
		"searchForm.type": function(val) {
			if (val === "embedding" || val === 'mix') {
				this.searchForm.similarity = 0.600
			} else if (val === "text") {
				this.searchForm.similarity = 0
			}
		}
	},
	mounted() {
		this.searchForm.datasetIds = this.$route.query.datasetId
	},
	methods: {
		// 查询测试
		async query() {
			this.loading = true
			let res = await this.$API.dataset.hitTest.post(this.searchForm)
			setTimeout(() => {
				this.loading = false
			}, 800)
			this.hitList = res.data
		}
	}
}
</script>

<style scoped>
.tool-bar {
	display: flex;
	align-items: center;
	padding: 10px;
}
.search-type {
	border: 1px solid var(--el-color-theme);
	border-radius: 5px;
	color: var(--el-color-theme);
	padding: 5px 10px;
	cursor: pointer;
	display: flex;
	align-items: center;
}
.search-input {
	margin-left: 20px;
}
.hit-list {
	height: calc(100vh - 210px);
	width: 100%;
	background: #fff;
	padding: 10px;
	overflow-y: scroll;
}
.hit-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.hit-list .content {
	background: #f4f4f4;
	width: 100%;
	padding: 20px 10px;
	display: grid;
	box-sizing: border-box;
	margin-top: 16px;
	gap: 16px;
	grid-template-columns: repeat(auto-fill, minmax(24%, 1fr));
}
.paragraph-item {
	background: #fff;
	height: 250px;
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
.too-radio-list {
	width: 100%;
	display: flex;
}
.radio-item {
	margin-bottom: 10px;
	width: calc(100% - 40px);
	height: 85px;
	font-size: 13px;
}
.radio-desc {
	margin-top: 3px;
	margin-left: 10px;
	color: #8f959e;
	text-wrap: wrap;
	line-height: 17px;
}
.radio-title {
	margin-left: 10px;
}
.score-list {
	display: flex;
	width: 100%;
}
.score-item {
	width: 50%;
	display: flex;
	flex-direction: column;
}
</style>
