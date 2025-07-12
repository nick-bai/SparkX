<template>
	<el-dialog title="添加成员" v-model="visible" width="500px" destroy-on-close :close-on-click-modal="false">
		<el-form :model="form" label-width="80px">
			<el-form-item label="用户昵称" prop="nickname">
				<el-autocomplete
					style="width: 500px"
					v-model="nickname"
					placeholder="请输入用户昵称"
					:fetch-suggestions="querySearchAsync"
					@select="handleSelect">
				</el-autocomplete>
				<div class="flex gap-2">
					<el-tag
						v-for="(tag, index) in form.users"
						:key="tag.userId"
						closable
						style="margin-right: 5px"
						@close="delUser(index)">
						{{ tag.value }}
					</el-tag>
				</div>
			</el-form-item>
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data() {
		return {
			visible: false,
			rules: {
				nickname: [
					{required: true, message: '请输入昵称', trigger: 'blur'}
				]
			},
			form: {
				users: [],
			},
			nickname: ''
		}
	},
	mounted() {

	},
	methods: {
		//显示
		open() {
			this.visible = true
			return this
		},
		// 保存添加用户
		async optSubmit() {
			if (this.form.users.length === 0) {
				this.$message.error("请添加用户")
				return false;
			}

			let res = await this.$API.team.addUser.post({userIds: this.form.users.map(item => item.userId).join(',')})
			if (res.code === 0) {
				this.$message.success('操作成功')
				this.$emit("success", res.msg)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 异步查询
		async querySearchAsync(queryString, cb) {
			if (queryString === '') {
				cb([])
				return false
			}

			let res = await this.$API.team.searchUser.get({nickname: queryString})
			cb(res.data.map(item => ({
				value: item.nickname,
				userId: item.userId
			})))

		},
		// 确认选择
		handleSelect(row) {
			let hasAdded = false
			this.form.users.forEach(item => {
				if (item.userId === row.userId) {
					hasAdded = true
					this.$message.error('该用户已经添加')
					this.nickname = ''
				}
			})

			if (!hasAdded) {
				this.form.users.push(row)
				this.nickname = ''
			}
		},
		delUser(index) {
			this.form.users.splice(index, 1)
		}
	}
}
</script>

<style scoped>

</style>
