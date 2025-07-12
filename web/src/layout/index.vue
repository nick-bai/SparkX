<template>
	<!-- 通栏布局 -->
	<template v-if="globalStore?.layout=='header'">
		<section class="aminui-wrapper">
			<div v-if="!globalStore?.ismobile && nextMenu.length>0 || !pmenu.component"
				 :class="globalStore?.menuIsCollapse?'aminui-side isCollapse':'aminui-side'">

				<div class="adminui-side-scroll">
					<el-scrollbar>
						<el-menu :default-active="active" router :collapse="globalStore?.menuIsCollapse"
								 :unique-opened="config.MENU_UNIQUE_OPENED">
							<div style="text-align: center;margin-top: 20px;margin-bottom: 40px;">
								<img class="logo" src="/img/logo.png" style="width: 40px;height: auto;" />
							</div>
							<NavMenu :navMenus="nextMenu"></NavMenu>
						</el-menu>
					</el-scrollbar>
				</div>
				<el-dropdown @command="handleCommand">
					<div class="user-info" style="margin-bottom: 20px">
						<el-avatar style="width: 35px;height: 35px;margin-left: 13px;cursor: pointer;" :src="userAvatar" />
						<div style="font-size:12px;width: 65px;margin-top:5px;text-align: center;" class="line1">{{ nickname }}</div>
					</div>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item command="password">修改密码</el-dropdown-item>
							<el-dropdown-item command="loginOut">退出登录</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>

				<div class="adminui-side-bottom" @click="showLicense">
					<span>{{ decodeUnicode(license.companyName) }}</span>
				</div>
				<!--<div class="adminui-side-bottom" @click="globalStore.TOGGLE_menuIsCollapse()">
					<el-icon>
						<el-icon-expand v-if="globalStore?.menuIsCollapse"/>
						<el-icon-fold v-else/>
					</el-icon>
				</div>-->
			</div>
			<Side-m v-if="globalStore?.ismobile"></Side-m>
			<div class="aminui-body el-container">
				<div class="adminui-main" id="adminui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepAliveStore?.keepLiveRoute">
							<component :is="Component" :key="$route.fullPath" v-if="keepAliveStore?.routeShow"/>
						</keep-alive>
					</router-view>
					<iframe-view></iframe-view>
				</div>
			</div>
		</section>
	</template>

	<!-- 经典布局 -->
	<template v-else-if="layout=='menu'">
		<header class="adminui-header">
			<div class="adminui-header-left">
				<div class="logo-bar">
					<img class="logo" src="/img/logo.png">
					<span>{{ config.APP_NAME }}</span>
				</div>
			</div>
			<div class="adminui-header-right">
				<userbar></userbar>
			</div>
		</header>
		<section class="aminui-wrapper">
			<div v-if="!globalStore?.ismobile"
				 :class="globalStore?.menuIsCollapse?'aminui-side isCollapse':'aminui-side'">
				<div class="adminui-side-scroll">
					<el-scrollbar>
						<el-menu :default-active="active" router :collapse="globalStore?.menuIsCollapse"
								 :unique-opened="config.MENU_UNIQUE_OPENED">
							<NavMenu :navMenus="menu"></NavMenu>
						</el-menu>
					</el-scrollbar>
				</div>
				<div class="adminui-side-bottom" @click="globalStore.TOGGLE_menuIsCollapse()">
					<el-icon>
						<el-icon-expand v-if="globalStore?.menuIsCollapse"/>
						<el-icon-fold v-else/>
					</el-icon>
				</div>
			</div>
			<Side-m v-if="globalStore?.ismobile"></Side-m>
			<div class="aminui-body el-container">
				<Topbar v-if="!globalStore?.ismobile"></Topbar>
				<Tags v-if="!globalStore?.ismobile && globalStore?.layoutTags"></Tags>
				<div class="adminui-main" id="adminui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepAliveStore?.keepLiveRoute">
							<component :is="Component" :key="$route.fullPath" v-if="keepAliveStore?.routeShow"/>
						</keep-alive>
					</router-view>
					<iframe-view></iframe-view>
				</div>
			</div>
		</section>
	</template>

	<!-- 功能坞布局 -->
	<template v-else-if="globalStore?.layout=='dock'">
		<header class="adminui-header">
			<div class="adminui-header-left">
				<div class="logo-bar">
					<img class="logo" src="/img/logo.png">
					<span>{{ config.APP_NAME }}</span>
				</div>
			</div>
			<div class="adminui-header-right">
				<div v-if="!globalStore?.ismobile" class="adminui-header-menu">
					<el-menu mode="horizontal" :default-active="active" router background-color="#222b45"
							 text-color="#fff" active-text-color="var(--el-color-primary)">
						<NavMenu :navMenus="menu"></NavMenu>
					</el-menu>
				</div>
				<Side-m v-if="globalStore?.ismobile"></Side-m>
				<userbar></userbar>
			</div>
		</header>
		<section class="aminui-wrapper">
			<div class="aminui-body el-container">
				<Tags v-if="!globalStore?.ismobile && globalStore?.layoutTags"></Tags>
				<div class="adminui-main" id="adminui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepAliveStore?.keepLiveRoute">
							<component :is="Component" :key="$route.fullPath" v-if="keepAliveStore?.routeShow"/>
						</keep-alive>
					</router-view>
					<iframe-view></iframe-view>
				</div>
			</div>
		</section>
	</template>

	<!-- 默认布局 -->
	<template v-else>
		<section class="aminui-wrapper">
			<div v-if="!ismobile" class="aminui-side-split">
				<div class="aminui-side-split-top">
					<router-link :to="config.DASHBOARD_URL">
						<img class="logo" :title="config.APP_NAME" src="/img/logo-r.png">
					</router-link>
				</div>
				<div class="adminui-side-split-scroll">
					<el-scrollbar>
						<ul>
							<li v-for="item in menu" :key="item" :class="pmenu.path==item.path?'active':''"
								@click="showMenu(item)">
								<el-icon>
									<component :is="item.meta.icon || el-icon-menu"/>
								</el-icon>
								<p>{{ item.meta.title }}</p>
							</li>
						</ul>
					</el-scrollbar>
				</div>
			</div>
			<div v-if="!globalStore?.ismobile && nextMenu.length>0 || !pmenu.component"
				 :class="globalStore?.menuIsCollapse?'aminui-side isCollapse':'aminui-side'">
				<div v-if="!globalStore?.menuIsCollapse" class="adminui-side-top">
					<h2>{{ pmenu?.meta?.title }}</h2>
				</div>
				<div class="adminui-side-scroll">
					<el-scrollbar>
						<el-menu :default-active="active" router :collapse="globalStore?.menuIsCollapse"
								 :unique-opened="config.MENU_UNIQUE_OPENED">
							<NavMenu :navMenus="nextMenu"></NavMenu>
						</el-menu>
					</el-scrollbar>
				</div>
				<div class="adminui-side-bottom" @click="globalStore.TOGGLE_menuIsCollapse()">
					<el-icon>
						<el-icon-expand v-if="globalStore?.menuIsCollapse"/>
						<el-icon-fold v-else/>
					</el-icon>
				</div>
			</div>
			<Side-m v-if="globalStore?.ismobile"></Side-m>
			<div class="aminui-body el-container">
				<Topbar>
					<userbar></userbar>
				</Topbar>
				<Tags v-if="!globalStore?.ismobile && globalStore?.layoutTags"></Tags>
				<div class="adminui-main" id="adminui-main">
					<router-view v-slot="{ Component }">
						<keep-alive :include="keepAliveStore?.keepLiveRoute">
							<component :is="Component" :key="$route.fullPath" v-if="keepAliveStore?.routeShow"/>
						</keep-alive>
					</router-view>
					<iframe-view></iframe-view>
				</div>
			</div>
		</section>
	</template>

	<div class="main-maximize-exit" @click="exitMaximize">
		<el-icon>
			<el-icon-close/>
		</el-icon>
	</div>

	<!--<div class="layout-setting" @click="openSetting">
		<el-icon>
			<el-icon-brush-filled/>
		</el-icon>
	</div>-->

	<el-drawer title="布局实时演示" v-model="settingDialog" :size="400" append-to-body destroy-on-close>
		<setting></setting>
	</el-drawer>

	<auto-exit></auto-exit>

	<el-dialog title="设置密码" v-model="passwordDialogVisible" width="500px" :close-on-click-modal="false">
		<el-form ref="ruleForm" :model="passwordForm" label-width="80px" :rules="rules">
			<el-form-item label="原密码" prop="oldPwd">
				<el-input v-model="passwordForm.oldPwd"></el-input>
			</el-form-item>
			<el-form-item label="新密码" prop="newPwd">
				<el-input v-model="passwordForm.newPwd"></el-input>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" @click="onSubmit">确认提交</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>

	<el-dialog v-model="showLicenseDialog" width="250px" :close-on-click-modal="false">
		<div class="license-box">
			<span class="logo-txt">SparkX</span>
			<div class="license-item" style="margin-top: 20px">授权公司：{{ decodeUnicode(license.companyName) }}</div>
			<div class="license-item" style="margin-top: 10px">授权码：{{ license.licenseId }}</div>
			<div class="license-item" style="margin-top: 10px">当前版本：{{ license.version }}</div>
			<div class="license-item" style="margin-top: 10px">应用数量：{{ license.appNum }}</div>
			<div class="license-item" style="margin-top: 10px">知识库数量：{{ license.datasetNum }}</div>
			<div class="license-item" style="margin-top: 10px">用户数量：{{ license.userNum }}</div>
		</div>
	</el-dialog>
</template>

<script setup>
import SideM from './components/sideM.vue';
import Topbar from './components/topbar.vue';
import Tags from './components/tags.vue';
import NavMenu from './components/NavMenu.vue';
import userbar from './components/userbar.vue';
import setting from './components/setting.vue';
import iframeView from './components/iframeView.vue';
import autoExit from './other/autoExit.js';
import config from "@/config/index.js";
import {useGlobalStore} from "@/stores/global.js";
import {useKeepAliveStore} from "@/stores/keepAlive.js";
import {ref, nextTick} from "vue";
const cns = getCurrentInstance()
import { ElMessage } from 'element-plus'

import {useRoute, useRouter} from 'vue-router'
import tool from "@/utils/tool.js";

const route = useRoute()
const router = useRouter()
const globalStore = useGlobalStore();
const keepAliveStore = useKeepAliveStore();

const settingDialog = ref(false)
const passwordDialogVisible = ref(false)
const menu = ref([])
const nextMenu = ref([])
const pmenu = ref({})
const active = ref('')
const userAvatar = "./img/avatar.png"
const nickname = tool.cookie.get("nickname")
const passwordForm = ref({
	oldPwd: "",
	newPwd: "",
})
const license = ref(JSON.parse(localStorage.getItem("license")))
const showLicenseDialog = ref(false)
const ruleForm = ref()

const rules = ref({
	oldPwd: [
		{required: true, message: '旧密码不能为空', trigger: 'blur'}
	],
	newPwd: [
		{required: true, message: '新密码不能为空', trigger: 'blur'}
	]
})

onMounted(() => {
	onLayoutResize();
	window.addEventListener('resize', onLayoutResize);
	let menu_ = router?.sc_getMenu();
	menu.value = filterUrl(menu_);
	showThis()
})

const openSetting = () => {
	settingDialog.value = true;
}
const onLayoutResize = () => {
	const globalStore = useGlobalStore();
	globalStore.SET_ismobile(document.body.clientWidth < 992)
}
//路由监听高亮
const showThis = () => {
	pmenu.value = route.meta.breadcrumb ? route.meta.breadcrumb[0] : {}
	nextMenu.value = filterUrl(pmenu.value?.children);
	nextTick(() => {
		active.value = route.meta.active || route.fullPath;
	})
}
//点击显示
const showMenu = (route) => {
	pmenu.value = route;
	nextMenu.value = filterUrl(route.children);
	if ((!route.children || route.children.length == 0) && route.component) {
		router.push({path: route.path})
	}
}
//转换外部链接的路由
const filterUrl = (map) => {
	let newMap = []
	map && map.forEach(item => {
		item.meta = item.meta ? item.meta : {};
		//处理隐藏
		if (item.meta.hidden || item.meta.type == "button") {
			return false
		}
		//处理http
		if (item.meta.type == 'iframe') {
			item.path = `/i/${item.name}`;
		}
		//递归循环
		if (item.children && item.children.length > 0) {
			item.children = filterUrl(item.children)
		}
		newMap.push(item)
	})
	return newMap;
}
//退出最大化
const exitMaximize = () => {
	document.getElementById('app').classList.remove('main-maximize')
}
watch(router, () => {
	showThis()
})

watch(() => globalStore.layout, (newVal, oldVal) => {
	document.body.setAttribute('data-layout', newVal)
})

const handleCommand = (event) => {
	if (event === 'password') {
		passwordDialogVisible.value = true
	} else if (event === 'loginOut') {
		tool.cookie.remove('TOKEN')
		router.push('/login')
	}
}

const onSubmit = () => {
	let $api = cns.appContext.config.globalProperties.$API
	ruleForm.value.validate(async (valid) => {
		if (valid) {
			let res = await $api.auth.password.post(passwordForm.value)
			if (res === 0) {
				ElMessage({
					message: '设置成功',
					type: 'success',
				})
				setTimeout(() => {
					tool.cookie.remove('TOKEN')
					router.push('/login')
				}, 1000)
			} else {
				ElMessage({
					message: res.msg,
					type: 'error',
				})
			}
		}
	})
}

const showLicense = () => {
	showLicenseDialog.value = true
}

function decodeUnicode(str) {
	str = str.replace(/\\/g, "%");
	return unescape(str);
}
</script>

<style scoped>
.line1 {
	overflow: hidden;
	text-overflow:ellipsis;
	white-space: nowrap;
}
.license-box {
	display: flex;
	align-items: center;
	flex-direction: column;
	height: 250px;
	padding: 20px;
}
.license-item {
	font-size: 13px;
}
</style>
