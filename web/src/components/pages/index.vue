<template>
	<div class="page-div">
		<div>
			<span class="logo-txt" style="font-size: 18px;margin-top: 5px;">SparkX</span>
			<span style="font-size: 12px;margin-left: 5px;margin-top: 5px;">开源AI知识库系统</span>
		</div>
		<div style="display: flex; justify-content: center;">
			<div class="page-jump">
				<div>共 {{ page.total }} 条</div>
			</div>
			<el-pagination
				background
				layout="->,prev, pager, next"
				:page-size="searchForm.limit"
				@current-change="pageChangeHandle"
				:total="page.total"
			>
			</el-pagination>
			<div class="page-jump">
				<div>每页 &nbsp;</div>
				<div><el-input v-model="searchForm.limit" placeholder="请输入内容" type="number" style="width: 60px" @blur="jump"></el-input></div>
				<div>&nbsp;条</div>
			</div>
			<div class="page-jump">
				<div>前往 &nbsp;</div>
				<div><el-input v-model="searchForm.page" placeholder="请输入内容" type="number" style="width: 60px" @blur="jump"></el-input></div>
				<div>&nbsp;页</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: {
		pageObj: {
			type: Object
		},
		form: {
			type: Object
		}
	},
	mounted() {
		this.page = this.pageObj
		this.searchForm = this.form
	},
	data() {
		return {
			page: {
				total: 0
			},
			searchForm: {
				limit: 10,
				page: 1
			}
		}
	},
	methods: {
		jump() {
			this.$emit('pageJump')
		},
		pageChangeHandle(val) {
			this.$emit('pageChange', val)
		}
	}
}
</script>

<style scoped>
.page-div {
	margin-top: 20px;
	display: flex;
	justify-content: space-between;
	background: #fff;
	padding: 10px 20px;
	border-radius: 10px;
}
.page-jump {
	display: flex;
	align-items: center;
	margin-left: 10px;
}
</style>


