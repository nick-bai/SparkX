<template>
	<el-dialog title="友情提示" v-model="visible" :width="350" destroy-on-close @closed="$emit('closed')">
		<span>{{ notice }}</span>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button type="primary" @click="goAuth">前往授权</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data() {
		return {
			visible: false,
			notice: ""
		}
	},
	methods: {
		// 显示
		open() {
			this.visible = true
			return this
		},
		setData(row) {
			this.notice = row.notice
		},
		goAuth() {
			window.location.href = 'https://ap.sparkshop.cn'
		}
	}
}
</script>

<style scoped>

</style>
