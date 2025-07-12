<template>
	<el-form ref="loginForm" :model="form" :rules="rules" label-width="0" size="large" @keyup.enter="login">
		<el-form-item prop="user">
			<el-input v-model="form.user" prefix-icon="el-icon-user" clearable
					  :placeholder="$t('login.userPlaceholder')">
			</el-input>
		</el-form-item>
		<el-form-item prop="password">
			<el-input v-model="form.password" prefix-icon="el-icon-lock" clearable show-password
					  :placeholder="$t('login.PWPlaceholder')"></el-input>
		</el-form-item>
		<el-form-item>
			<el-button type="primary" style="width: 100%;" :loading="islogin" round @click="login">{{
					$t('login.signIn')
				}}
			</el-button>
		</el-form-item>
	</el-form>
</template>

<script>
import menu from '@/config/menu.js'

export default {
	data() {
		return {
			userType: 'admin',
			form: {
				user: "",
				password: "",
				autologin: false
			},
			rules: {
				user: [
					{required: true, message: this.$t('login.userError'), trigger: 'blur'}
				],
				password: [
					{required: true, message: this.$t('login.PWError'), trigger: 'blur'}
				]
			},
			islogin: false
		}
	},
	watch: {
		userType(val) {
			if (val == 'admin') {
				this.form.user = 'admin'
				this.form.password = 'admin'
			} else if (val == 'user') {
				this.form.user = 'user'
				this.form.password = 'user'
			}
		}
	},
	mounted() {

	},
	methods: {
		async login() {

			var validate = await this.$refs.loginForm.validate().catch(() => {})
			if (!validate) {
				return false
			}

			this.islogin = true
			let res = await this.$API.auth.login.post({
				username: this.form.user,
				password: this.form.password
			})
			this.islogin = false

			if (res.code === 0) {
				this.$TOOL.cookie.set("TOKEN", res.data.token, {
					expires: this.form.autologin ? 24 * 60 * 60 : 0
				})
				this.$TOOL.cookie.set("nickname", res.data.name, {
					expires: this.form.autologin ? 24 * 60 * 60 : 0
				})
				localStorage.setItem("license", res.data.license)
				localStorage.setItem("customerId", res.data.customerId)

				await this.$nextTick(() => {
					// 获取菜单
					localStorage.setItem("MENU", menu)

					setTimeout(() => {
						this.$router.replace({
							path: '/'
						})
					}, 300)
				})

				this.$message.success("登录成功")
			} else {
				this.$message.error(res.msg)
			}
		},
	}
}
</script>

<style>
</style>
