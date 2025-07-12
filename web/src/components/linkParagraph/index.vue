<template>
	<div class="link-box">
		<div class="document-list">
			<el-autocomplete
				style="width: 100%"
				class="inline-input"
				v-model="searchTitle"
				:fetch-suggestions="querySearch"
				placeholder="文档名称"
				:trigger-on-focus="false"
				@select="handleSelect"
			></el-autocomplete>
			<div class="document-item"
				 @click="selectDocument(item, index)"
				 v-for="(item, index) in documentList" :key="item.documentId"
				 :class="{'document-active': item.documentId === this.selectedDocumentId}">
				<span style="width: 200px;" class="line1">{{ item.name }}</span>
				<span class="label" v-if="item.linkNum > 0 && showNum">{{ item.linkNum }}</span>
			</div>
		</div>
		<div class="paragraph-list" v-loading="loading">
			<div class="paragraph-item"
				 :class="{active: item.relationed}"
				 v-for="(item, index) in paragraphList"
				 :key="item.paragraphId"
				 @click="linkParagraph(item, index)">
				<div class="paragraph-title">
					<div class="title-left line1" v-if="item.title.length > 0">{{ item.title }}</div>
					<div class="title-left line1" v-else>--</div>
				</div>
				<el-scrollbar class="paragraph-doc">
					{{ item.content }}
				</el-scrollbar>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		datasetId: {
			type: String,
			default: ''
		},
		questionIds: {
			type: String,
			default: ''
		},
		showNum: {
			type: Boolean,
			default: true
		}
	},
	data() {
		return {
			documentList: [],
			paragraphList: [],
			selectedDocumentId: "",
			searchTitle: "",
			nowDocumentIndex: 0,
			loading: false
		}
	},
	mounted() {
		this.getDocumentList()
	},
	methods: {
		// 获取关联
		async getRelationList() {
			let res = await this.$API.question.getRelation.get({questionIds: this.questionIds, datasetId: this.datasetId})
			let relationData = res.data
			// 计算信息
			let document2Map = new Map();
			relationData.forEach(item => {
				let arr = document2Map.get(item.documentId)
				if (arr && arr.length > 0) {
					arr.push(item.paragraphId)
					document2Map.set(item.documentId, arr)
				} else {
					arr = []
					arr.push(item.paragraphId)
					document2Map.set(item.documentId, arr)
				}
			});

			// 确定文档关联的段落数
			this.documentList.forEach((item, index) => {
				this.documentList[index].linkNum = 0
				if (document2Map.get(item.documentId)) {
					this.documentList[index].linkNum = document2Map.get(item.documentId).length
				}
			})

			// 确定选择的段落
			let documentId = this.documentList[this.nowDocumentIndex].documentId
			let selectedParagraph = document2Map.get(documentId)

			this.paragraphList.forEach((item, index) => {
				this.paragraphList[index].relationed = !!selectedParagraph?.includes(item.paragraphId);
			})
		},
		// 获取文档列表
		async getDocumentList() {
			let res = await this.$API.document.getList.get({
				datasetId: this.datasetId,
				name: "",
				page: 1,
				limit: 2000
			})
			this.documentList = res.data.data
			if (this.documentList.length > 0) {
				this.selectedDocumentId = this.documentList[0].documentId
				this.getParagraphList()
			}
		},
		// 获取段落列表
		async getParagraphList() {
			let res = await this.$API.paragraph.getList.get({
				documentId: this.selectedDocumentId,
				page: 1,
				limit: 2000
			})
			this.paragraphList = res.data.data

			this.getRelationList()
		},
		// 关联分段
		async linkParagraph(row, index) {

			let type = 1
			if (this.paragraphList[index].relationed) {
				type = 2
				this.paragraphList[index].relationed = false
			} else {
				this.paragraphList[index].relationed = true
			}

			this.loading = true
			let res = await this.$API.question.doRelation.post({
				datasetId: this.datasetId,
				questionIds: this.questionIds,
				documentId: this.selectedDocumentId,
				paragraphId: row.paragraphId,
				type: type, // 1:新增 2:删除
			})
			setTimeout(() => {
				this.loading = false
			}, 300)

			if (res.code === 0) {
				this.$message.success(res.msg)
				this.$emit("linkComplete")
			} else {
				this.$message.error(res.msg)
			}
		},
		// 选择文档
		selectDocument(row, index) {
			this.nowDocumentIndex = index
			this.selectedDocumentId = row.documentId
			this.getParagraphList()
		},
		querySearch(queryString, cb) {
			var restaurants = this.documentList.map(item => {
				return {value: item.name, documentId: item.documentId}
			});

			var results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
			cb(results);
		},
		createFilter(queryString) {
			return (restaurant) => {
				return (restaurant.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
			};
		},
		handleSelect(row) {
			this.selectDocument(row)
		}
	}
}
</script>
<style>
.loading-class .el-loading-text {
	color: #fff !important;
}
</style>
<style scoped>
.link-box {
	width: 100%;
	height: 100%;
	border-top: 1px solid #DCDFE6;
	display: flex;
}
.document-list {
	width: 250px;
	border-right: 1px solid #DCDFE6;
	padding: 10px;
	height: 600px;
	overflow-y: auto;
}
.document-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.paragraph-list {
	width: calc(100% - 249px);
	height: 600px;
	overflow-y: auto;
	display: flex;
	flex-wrap: wrap;
	background: #f4f4f4;
	justify-content: space-between;
	padding: 5px;
}
.paragraph-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.paragraph-item {
	background: #fff;
	height: 200px;
	width: 49%;
	border-radius: 5px;
	margin-bottom: 10px;
	cursor: pointer;
	display: flex;
	flex-direction: column;
	padding: 10px;
	line-height: 22px;
	font-size: 13px;
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
	height: calc(100% - 20px);
	padding: 5px 0;
	color: #606266;
	margin-top: 10px;
}
.active {
	border: 1px solid var(--el-color-theme);
}
.document-item {
	width: 100%;
	height: 30px;
	margin-top: 5px;
	padding: 5px 5px;
	cursor: pointer;
	display: flex;
	align-items: center;
	font-size: 13px;
}
.document-item:hover {
	background: #eee7fd;
	border-radius: 5px;
	color: var(--el-color-theme);
}
.document-active {
	background: #eee7fd;
	color: var(--el-color-theme);
	border-radius: 5px;
}
.label {
	width: 20px;
	height: 20px;
	border-radius: 20px;
	line-height: 20px;
	text-align: center;
	color: #fff;
	background: var(--el-color-theme);
	font-size: 11px;
	margin-left: 5px;
}
</style>
