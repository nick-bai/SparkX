<template>
	<div class="base-div">
		<div class="search-box">
			<el-form :inline="true" :model="searchForm" class="demo-form-inline">
				<el-form-item>
					<el-date-picker
						v-model="dayRange"
						type="daterange"
						value-format="YYYY-MM-DD"
						unlink-panels
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
					/>
				</el-form-item>
				<el-form-item>
					<el-button type="primary" @click="onSubmit" icon="el-icon-search">查询</el-button>
				</el-form-item>
			</el-form>
		</div>
		<el-table
			:header-cell-style="{background:'#f4f4f4'}"
			:data="tableData"
			style="width: 100%">
			<el-table-column
				prop="title"
				label="摘要">
			</el-table-column>
			<el-table-column
				prop="num"
				label="对话提问数">
			</el-table-column>
			<el-table-column
				label="对话时间">
				<template #default="scope">
					{{ scope.row.createTime.replace("T", " ") }}
				</template>
			</el-table-column>
			<el-table-column
				prop="operation"
				width="120"
				label="操作">
				<template #default="scope">
					<el-button @click="handlePreview(scope.row)" type="text" size="small">查看详情</el-button>
				</template>
			</el-table-column>
		</el-table>
		<Pages :form="searchForm" :page-obj="page" @pageChange="handlePageChange" @pageJump="getList"></Pages>
	</div>

	<el-drawer
		title="记录详情"
		v-model="drawer"
		size="800"
		:with-header="false">
		<chat-log :session-id="sessionId" :key="randomKey"></chat-log>
	</el-drawer>
</template>

<script>
import Pages from "@/components/pages/index.vue"
import chatLog from "@/components/chatContent/log.vue"

export default {
	components: {Pages, chatLog},
	data() {
		return {
			searchForm: {
				appId: "",
				startTime: "",
				endTime: "",
				page: 1,
				limit: 15
			},
			dayRange: [],
			tableData: [],
			page: {
				total: 0
			},
			drawer: false,
			randomKey: Math.random(),
			sessionId: ""
		}
	},
	mounted() {
		this.searchForm.appId = this.$route.query.appId
		this.getDefaultDate()
		this.getList()
	},
	methods: {
		// 设置默认日期
		getDefaultDate() {
			let today = new Date()
			let defaultStart = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
			let defaultEnd = this.$TOOL.dateFormat(today, "yyyy-MM-dd")
			defaultStart = this.$TOOL.dateFormat(defaultStart, "yyyy-MM-dd")
			this.dayRange = [defaultStart, defaultEnd]
		},
		// 查询
		onSubmit() {
			this.getList()
		},
		async getList() {
			if (this.dayRange.length > 0) {
				this.searchForm.startTime = this.dayRange[0]
				this.searchForm.endTime = this.dayRange[1]
			}

			let res = await this.$API.application.log.get(this.searchForm)
			this.tableData = res.data.data
			this.page.total = res.data.total
		},
		handlePageChange(page) {
			this.searchForm.page = page
			this.getList()
		},
		handlePreview(row) {
			this.drawer = true
			this.sessionId = row.sessionId
			this.randomKey = Math.random()
		}
	}
}
</script>

<style scoped>
.base-div {
	background: #fff;
	border-radius: 10px;
	padding: 20px;
	height: calc(100vh - 110px);
}
.search-box {
	border-radius: 10px;
	margin-bottom: 20px;
	padding-top: 20px;
	padding-left: 20px;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
</style>
