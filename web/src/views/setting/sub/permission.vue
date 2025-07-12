<template>
	<el-table :data="dataTable" :header-cell-style="{ backgroundColor: '#f5f6f7' }">
		<el-table-column prop="title" :label="title" v-if="activeName === '1'"/>
		<el-table-column prop="name" :label="title" v-else/>
		<el-table-column
			align="center"
			width="100"
			fixed="right"
		>
			<template #header>
				<el-checkbox
					:disabled="isAdmin"
					v-model="checkAdminAll"
					label="管理"
					:indeterminate="allIndeterminate[1]"
				/>
			</template>
			<template #default="{ row, $index }">
				<el-checkbox
					:disabled="isAdmin"
					v-model="row.manage"
					@change="(value) => adminChange($index, value)"
				/>
			</template>
		</el-table-column>
		<el-table-column
			align="center"
			width="100"
			fixed="right"
		>
			<template #header>
				<el-checkbox
					:disabled="isAdmin"
					v-model="checkViewAll"
					label="查看"
					:indeterminate="allIndeterminate[2]"
				/>
			</template>
			<template #default="{ row, $index }">
				<el-checkbox
					:disabled="isAdmin"
					v-model="row.view"
					@change="(value) => viewChange($index, value)"
				/>
			</template>
		</el-table-column>
	</el-table>
</template>

<script>
export default {
	props: {
		activeName: {
			type: String,
			default: '1',
		},
		isAdmin: {
			type: Boolean,
			default: false
		},
		permissionData: {
			type: Object,
			default: {
				manage: [],
				view: []
			}
		},
		nowUserId: {
			type: String,
			default: ''
		},
		tableData: {
			type: Array,
			default: () => []
		}
	},
	data() {
		return {
			title: '',
			dataTable: [],
			checkAdminAll: false,
			checkViewAll: false,
			allIndeterminate: [], // 全选
		}
	},
	watch: {
		checkAdminAll: {
			handler(value) {
				let data = []
				this.dataTable.forEach(item => {
					item.manage = value
					item.view = value

					data.push(item)
				})
				this.dataTable = data
				this.checkAdminAll = this.checkViewAll = value
				this.allIndeterminate[1] = this.allIndeterminate[2] = !value
				this.$emit('update', this.dataTable)
			},
			deep: true,
		},
		checkViewAll: {
			handler(value) {
				let data = []
				this.dataTable.forEach(item => {
					item.view = value

					data.push(item)
				})
				this.dataTable = data
				this.$emit('update', this.dataTable)
			},
			deep: true,
		}
	},
	created() {
		if (this.activeName === '1') {
			this.title = '知识库名称'
		} else {
			this.title = '应用名称'
		}

		this.dataTable = this.tableData
		this.defaultSelect()
	},
	methods: {
		// 默认勾选
		defaultSelect() {
			if (this.isAdmin) {
				this.checkAdminAll = true
				this.checkViewAll = true
			} else {
				let count = 0
				let viewCount = 0
				let len = this.dataTable.length

				this.dataTable.forEach(item => {
					let id = ""
					if (this.activeName === '1') {
						id = item.datasetId
					} else {
						id = item.appId
					}

					if (this.permissionData.manage && this.permissionData.manage.indexOf(id) !== -1) {
						item.manage = true
						count += 1
					} else {
						item.manage = false
					}

					if (this.permissionData.view && this.permissionData.view.indexOf(id) !== -1) {
						item.view = true
						viewCount += 1
					} else {
						item.view = false
					}
				})

				if (viewCount < len && viewCount > 0) {
					this.allIndeterminate[2] = true
				}

				if (viewCount === len) {
					this.checkViewAll = true
					this.allIndeterminate[2] = false
				}

				if (count < len && count > 0) {
					this.allIndeterminate[1] = this.allIndeterminate[2] = true
				}

				if (count === len) {
					this.checkAdminAll = this.checkViewAll = true
					this.allIndeterminate[1] = this.allIndeterminate[2] = false
				}
			}
		},
		// 更选选择
		adminChange(index, value) {
			this.dataTable[index].manage = value
			this.dataTable[index].view = value

			let count = 0;
			this.dataTable.forEach(item => {
				if (item.manage) {
					count += 1
				}
			})

			let len = this.dataTable.length
			if (count < len && count > 0) {
				this.allIndeterminate[1] = this.allIndeterminate[2] = true
			}

			if (count === len) {
				this.checkAdminAll = this.checkViewAll = true
				this.allIndeterminate[1] = this.allIndeterminate[2] = false
			}

			this.$emit('update', this.dataTable)
		},
		// 查看选择
		viewChange(index, value) {
			this.dataTable[index].view = value
			if (!value) {
				this.dataTable[index].manage = value
			}

			let count = 0;
			this.dataTable.forEach(item => {
				if (item.view) {
					count += 1
				}
			})

			let len = this.dataTable.length
			if (count < len && count > 0) {
				this.allIndeterminate[2] = true
			}

			if (count === len) {
				this.checkViewAll = true
				this.allIndeterminate[2] = false
			}

			this.$emit('update', this.dataTable)
		},
	}
}
</script>

<style scoped>

</style>
