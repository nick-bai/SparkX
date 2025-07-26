<template>
	<el-container style="padding: 20px">
		<el-card shadow="never">
			<div class="title">系统设置</div>
			<el-row class="tac">
				<el-col :span="2" style="margin-right: 20px">
					<div class="menu">
						<el-menu
							style="background: #f5f5f5"
							default-active="1">
							<el-menu-item index="1" @click="handleClick(1)">
								<el-icon>
									<component :is="userIcon"/>
								</el-icon>
								<span>用户管理</span>
							</el-menu-item>
							<el-menu-item index="2" @click="handleClick(2)">
								<el-icon>
									<component :is="userIcon"/>
								</el-icon>
								<span>团队管理</span>
							</el-menu-item>
							<el-menu-item index="3" @click="handleClick(3)">
								<el-icon>
									<component :is="filesIcon"/>
								</el-icon>
								<span>模型管理</span>
							</el-menu-item>
						</el-menu>
					</div>
				</el-col>
				<el-col :span="21">
					<div class="pages">
						<Suspense>
							<template #default>
								<component :is="page"/>
							</template>
							<template #fallback>
								<el-skeleton :rows="3" />
							</template>
						</Suspense>
					</div>
				</el-col>
			</el-row>
		</el-card>
	</el-container>
</template>

<script>
import { defineAsyncComponent } from 'vue'

export default {
	data() {
		return {
			userIcon: 'el-icon-user',
			filesIcon: 'el-icon-files',
			components: {
				users: defineAsyncComponent(() => import('./pages/users.vue')),
				teams: defineAsyncComponent(() => import('./pages/teams.vue')),
				models: defineAsyncComponent(() => import('./pages/models.vue')),
			},
			page: ''
		}
	},
	mounted() {
		this.page = this.components.users
	},
	methods: {
		handleClick(type) {
			switch (type) {
				case 1:
					this.page = this.components.users
					break;
				case 2:
					this.page = this.components.teams
					break;
				case 3:
					this.page = this.components.models
					break;
			}
		}
	}
}
</script>

<style scoped>
	.title {
		font-size: 18px;
		font-weight: bold;
		padding-bottom: 20px;
		border-bottom: 1px solid #f4f4f4;
	}
	.tac {
		width: 100%;
		height: calc(100vh - 100px);
	}
	.menu {
		width: 100%;
		height: calc(100vh - 200px);
		display: flex;
		justify-content: center;
		background: #f5f5f5;
		border-radius: 6px;
		margin-top: 20px;
		padding-top: 40px;
	}
	.pages {
		width: 100%;
		height: calc(100vh - 200px);
		padding: 20px;
		background: #f5f5f5;
		border-radius: 10px;
		margin-top: 20px;
	}
</style>
