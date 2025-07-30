<template>
	<el-config-provider :locale="locale" :size="config.size" :zIndex="config.zIndex" :button="config.button">
		<router-view></router-view>
	</el-config-provider>
</template>

<script>
import '@/assets/alifont/iconfont.css'
import colorTool from '@/utils/color'

export default {
	name: 'App',
	data() {
		return {
			config: {
				size: "default",
				zIndex: 2000,
				button: {
					autoInsertSpace: false
				}
			}
		}
	},
	computed: {
		locale(){
			return this.$i18n.messages[this.$i18n.locale].el
		},
	},
	created() {
		//设置主题颜色
		const app_color = this.$CONFIG.COLOR || this.$TOOL.data.get('APP_COLOR')
		if(app_color){
			document.documentElement.style.setProperty('--el-color-primary', app_color);
			for (let i = 1; i <= 9; i++) {
				document.documentElement.style.setProperty(`--el-color-primary-light-${i}`, colorTool.lighten(app_color,i/10));
			}
			for (let i = 1; i <= 9; i++) {
				document.documentElement.style.setProperty(`--el-color-primary-dark-${i}`, colorTool.darken(app_color,i/10));
			}
		}
	}
}
</script>

<style lang="scss">
body {
	font-family: PingFang SC, AlibabaPuHuiTi !important;
}
.line1 {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
.base-style {
	display: flex;
	align-items: center;
	margin-top: 10px;
}
.flex-center {
	display: flex;
	align-items: center;
}
.flex-center-all {
	display: flex;
	align-items: center;
	justify-content: center;
}
.el-card {
	border-radius: 10px !important;
}
.code-bg {
	background: #e2e2e2;
	padding: 10px;
	color: #303133;
	border-radius: 5px;
}
.node-base {
	display: flex;
	flex-direction: column;
	width: 230px;
	min-height: 40px;
	padding: 10px;
	border: 1px solid #f4f4f4;
	border-radius: 16px;
	box-shadow: 0 0 #0000, 0 0 #0000, 0 1px 2px 0 rgba(16, 24, 40, 0.05);
	background: #fff;
}
.node-active {
	border: 1px solid var(--el-color-theme);
}
.node-name {
	margin-left: 10px;
	font-weight: bold;
}
.set-content-box {
	padding: 15px;
	border-radius: 5px;
	background: #fff;
	margin-top: 10px;
}
.menu-icon {
	color: #fff;
	padding: 3px;
	border-radius: 5px;
	margin-left: 10px;
}
.logo-txt {
	font-size: 28px;
	font-weight: bold;
	background-image: linear-gradient(to right, #3072EB, #5E17EB);
	background-clip: text;
	-webkit-text-fill-color: transparent;
}
.md-editor div.default-theme {
	--md-theme-quote-border: 5px solid #E9E9EB !important;
}
.md-editor-preview blockquote {
	background-color: #f4f4f4 !important;
}
@import '@/style/style.scss';
</style>
