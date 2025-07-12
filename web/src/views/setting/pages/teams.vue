<template>
	<div style="width: 100%;height: calc(100% - 20px);margin-top: 10px">
		<el-row style="height: 100%;">
			<el-col :span="4">
				<div class="user-list">
					<el-button type="primary" icon="el-icon-plus" circle style="float: right" size="small" @click="addUser"></el-button>
					<el-input
						style="margin-top: 10px;margin-bottom: 10px;"
						placeholder="请输入用户名"
						suffix-icon="el-icon-search"
						v-model="nickName">
					</el-input>

					<div class="user-item"
						 v-for="item in userList"
						 :key="item.userId"
						 :class="{'span-between': item.isAdmin === 2, 'user-active': nowUserId === item.userId}"
						 @click="selectUser(item)"
					>{{ item.name }}
						<el-tag style="margin-left: 10px" v-if="item.isAdmin === 1">创始人</el-tag>
						<el-icon style="margin-right: 10px" v-if="item.isAdmin === 2" @click="delUser($event, item.userId)">
							<Delete />
						</el-icon>
					</div>
				</div>
			</el-col>
			<el-col :span="19" style="margin-left: 20px">
				<div class="data-list">
					<el-button
						type="primary"
						icon="el-icon-Document"
						@click="savePermission"
						:disabled="isAdmin"
					>
						保存权限
					</el-button>
					<el-tabs v-model="activeName" style="margin-top: 10px" @tabChange="tabChange">
						<el-tab-pane label="知识库" name="1">
							<permission
								activeName="1"
								:is-admin="isAdmin"
								:now-user-id="nowUserId"
								:key="datasetKey"
								:permission-data="userPermissionData"
								:table-data="datasetTableData"
								@update="dataChange"
							></permission>
						</el-tab-pane>
						<el-tab-pane label="应用" name="2">
							<permission
								activeName="2"
								:is-admin="isAdmin"
								:now-user-id="nowUserId"
								:key="appKey"
								:permission-data="userPermissionData"
								:table-data="appTableData"
								@update="dataChange"
							></permission>
						</el-tab-pane>
					</el-tabs>
				</div>
			</el-col>
		</el-row>
	</div>

	<add-user ref="addUser" v-if="dialogVisible" @success="handleSuccess" @closed="dialogVisible=false" :close-on-click-modal="false"></add-user>
</template>

<script>
import {Delete} from "@element-plus/icons-vue";
import permission from "@/views/setting/sub/permission.vue"
import addUser from "@/views/setting/sub/addUser.vue"
import saveDialog from "@/views/dataset/save.vue";

export default {
	components: {
		saveDialog,
		Delete,
		permission,
		addUser
	},
	data() {
		return {
			searchForm: {
				name: ""
			},
			form: {
				teamId: 0,
				nickname: "",
			},
			permissionForm: {
				type: "",
				userId: "",
				permissionData: []
			},
			activeName: "1",
			dialogVisible: false,
			userList: [],
			nowUserId: "",
			isAdmin: false,
			datasetKey: Math.random(),
			appKey: Math.random(),
			userPermissionData: {},
			datasetTableData: [],
			appTableData: [],
			datasetPermission: {},
			appPermission: {},
			nickName: "",
			originalUserList: []
		}
	},
	watch: {
		nickName: {
			handler(val) {
				if (val) {
					this.userList = this.userList.filter((v) =>
						v.name.toLowerCase().includes(val.toLowerCase())
					)
				} else {
					this.userList = this.originalUserList
				}
			},
			deep: true
		}
	},
	mounted() {
		this.getTeamUserList()
		this.getDatabaseList()
		this.getAppList()
	},
	methods: {
		// 添加用成员
		addUser() {
			this.dialogVisible = true

			this.$nextTick(() => {
				this.$refs.addUser.open()
			})
		},
		// 获取知识库列表
		async getDatabaseList() {
			let res = await this.$API.dataset.list.get({page: 1, limit: 1000, title: '', type: 0})
			this.datasetTableData = res.data.data
			this.datasetKey = Math.random()
		},
		// 获取应用列表
		async getAppList() {
			let res = await this.$API.application.list.get({page: 1, limit: 1000, name: '', type: 0})
			this.appTableData = res.data.data

			this.appKey = Math.random()
		},
		// 获取团队成员
		async getTeamUserList() {
			let res = await this.$API.team.userList.get()
			this.originalUserList = this.userList = res.data

			if (this.userList.length > 0 && this.nowUserId === '') {
				this.nowUserId = this.userList[0].userId
				this.isAdmin = this.userList[0].isAdmin === 1
			}

			this.permissionForm.permissionData = []
			this.datasetKey = Math.random()
		},
		// 保存权限
		async savePermission() {
			this.permissionForm.type = this.activeName
			this.permissionForm.userId = this.nowUserId

			let res = await this.$API.team.updatePermission.post(this.permissionForm)
			if (res.code === 0) {
				this.$message.success("保存成功")
			} else {
				this.$message.error(res.msg)
			}

			let permissionData = {
				manage: [],
				view: []
			}
			let data = this.permissionForm.permissionData
			for (let i = 0; i < data.length; i++) {
				if (data[i].manage) {
					permissionData.manage.push(data[i].id)
				}

				if (data[i].view) {
					permissionData.view.push(data[i].id)
				}
			}

			this.appPermission = this.userPermissionData = permissionData
			if (this.activeName === "1") {
				this.getDatabaseList()
			} else {
				this.getAppList()
			}
		},
		// 权限数据
		dataChange(data) {
			let saveData = []
			data.forEach((item) => {
				if (item.manage || item.view) {
					saveData.push({
						id: (this.activeName === '1') ? item.datasetId : item.appId,
						manage: (typeof item.manage === 'undefined') ? false : item.manage,
						view: (typeof item.view === 'undefined') ? false : item.view,
					})
				}
			})

			this.permissionForm.permissionData = saveData
		},
		// 添加用户成功
		handleSuccess(userId) {
			this.dialogVisible = false

			this.nowUserId = userId
			this.isAdmin = false
			this.getTeamUserList()
		},
		// 选择团队用户
		selectUser(row) {
			this.nowUserId = row.userId
			this.isAdmin = row.isAdmin === 1

			if (row.datasetPermission !== '') {
				this.datasetPermission = this.userPermissionData = JSON.parse(row.datasetPermission)
			}

			if (row.appPermission !== '') {
				this.appPermission = JSON.parse(row.appPermission)
				if (this.activeName === '2') {
					this.userPermissionData = this.appPermission
				}
			}

			if (this.activeName === '1') {
				this.datasetKey = Math.random()
			} else {
				this.appKey = Math.random()
			}
		},
		// tab切换
		tabChange(row) {
			if (row === '1') {
				this.userPermissionData = this.datasetPermission
				this.datasetKey = Math.random()
			} else {
				this.userPermissionData = this.appPermission
				this.appKey = Math.random()
			}
		},
		// 删除用户
		async delUser(event, userId) {
			event.stopPropagation()

			this.$confirm('此操作将永久删除该用户 是否继续?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(async () => {
				let res = await this.$API.team.delUser.get({userId: userId})
				if (res.code === 0) {
					this.nowUserId = ''
					this.$message.success(res.msg)
					this.getTeamUserList()
				} else {
					this.$message.error(res.msg)
				}
			}).catch(() => {});
		},
	}
}
</script>

<style scoped>
.user-list {
	width: 100%;
	height: 100%;
	background: #fff;
	border-radius: 6px;
	padding: 20px;
}
.user-item {
	width: 100%;
	height: 40px;
	overflow: hidden;
	display: flex;
	align-items: center;
	padding-left: 10px;
	cursor: pointer;
}
.user-item:hover {
	background: rgba(238, 231, 253, 0.5);
}
.user-active {
	background: #EEE7fd;
	color: var(--el-color-theme);
	border-radius: 5px;
}
.span-between {
	justify-content: space-between;
}
.data-list {
	width: 100%;
	height: 100%;
	background: #fff;
	border-radius: 6px;
	padding: 20px;
}
</style>
