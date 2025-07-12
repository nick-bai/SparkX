<template>
	<div class="adminui-tags">
		<ul ref="tags">
			<li v-for="tag in viewTagsStore.viewTags" v-bind:key="tag"
				:class="[isActive(tag)?'active':'',tag.meta.affix?'affix':'' ]"
				@contextmenu.prevent="openContextMenu($event, tag)">
				<router-link :to="tag">
					<span>{{ tag.meta.title }}</span>
					<el-icon v-if="!tag.meta.affix" @click.prevent.stop='closeSelectedTag(tag)'>
						<el-icon-close/>
					</el-icon>
				</router-link>
			</li>
		</ul>
	</div>

	<transition name="el-zoom-in-top">
		<ul v-if="contextMenuVisible" :style="{left:left+'px',top:top+'px'}" class="contextmenu" id="contextmenu">
			<li @click="refreshTab()">
				<el-icon>
					<el-icon-refresh/>
				</el-icon>
				刷新
			</li>
			<hr>
			<li @click="closeTabs()" :class="contextMenuItem.meta.affix?'disabled':''">
				<el-icon>
					<el-icon-close/>
				</el-icon>
				关闭标签
			</li>
			<li @click="closeOtherTabs()">
				<el-icon>
					<el-icon-folder-delete/>
				</el-icon>
				关闭其他标签
			</li>
			<hr>
			<li @click="maximize()">
				<el-icon>
					<el-icon-full-screen/>
				</el-icon>
				最大化
			</li>
			<li @click="openWindow()">
				<el-icon>
					<el-icon-copy-document/>
				</el-icon>
				在新的窗口中打开
			</li>
		</ul>
	</transition>
</template>

<script setup>
import {useViewTagsStore} from "@/stores/viewTags.js";
import {useKeepAliveStore} from "@/stores/keepAlive.js";
import {useIframeStore} from "@/stores/iframe.js";
import {useRoute, useRouter} from "vue-router";
import config from "@/config/index.js";
import tool from "@/utils/tool.js";

const {proxy} = getCurrentInstance()

const route = useRoute()
const router = useRouter()

const viewTagsStore = useViewTagsStore();
const keepAliveStore = useKeepAliveStore();
const iframeStore = useIframeStore();

const contextMenuVisible = ref(false)
const contextMenuItem = ref(null)
const left = ref(0)
const top = ref(0)
const tagList = ref(viewTagsStore.viewTags)
const tipDisplayed = ref(false)

const tags = ref(null)

// 限制路由比生命函数先执行
const routeFlag = ref(false)

// 增加tag
const addViewTags = (route_) => {
	if (route_.name && !route_.meta.fullpage) {
		const viewTagsStore = useViewTagsStore();
		const keepAliveStore = useKeepAliveStore();
		viewTagsStore.pushViewTags(route_)
		keepAliveStore.pushKeepLive(route_?.name)
	}
}

onMounted(() => {

	const menu = router.sc_getMenu()
	let dashboardRoute = tool.treeFind(menu, node => node.path == config.DASHBOARD_URL)
	if (dashboardRoute) {
		dashboardRoute.fullPath = dashboardRoute.path
		addViewTags(dashboardRoute)
		addViewTags(proxy.$route)

		routeFlag.value = true
	}

	tagDrop();
	scrollInit()
})

watch(
	() => proxy.$route,
	(val) => {
		if (!routeFlag.value) {
			return;
		}
		addViewTags(val);
		//判断标签容器是否出现滚动条
		nextTick(() => {
			if (tags.value && tags.value.scrollWidth > tags.value.clientWidth) {
				//确保当前标签在可视范围内
				let targetTag = tags.querySelector(".active")
				targetTag.scrollIntoView()
				//显示提示
				if (!tipDisplayed.value) {
					this.$msgbox({
						type: 'warning',
						center: true,
						title: '提示',
						message: '当前标签数量过多，可通过鼠标滚轴滚动标签栏。关闭标签数量可减少系统性能消耗。',
						confirmButtonText: '知道了'
					})
					tipDisplayed.value = true
				}
			}
		})
	}, {immediate: true, deep: true}
)

watch(contextMenuVisible, (val) => {
	const cm = (e) => {
		const sp = document.getElementById("contextmenu");
		if (sp && !sp.contains(e.target)) {
			closeMenu()
		}
	}
	if (val) {
		document.body.addEventListener('click', e => cm(e))
	} else {
		document.body.removeEventListener('click', e => cm(e))
	}
})

// 标签拖拽排序
const tagDrop = () => {
	const target = tags.value
	/*Sortable.create(target, {
		draggable: 'li',
		animation: 300
	})*/
}
//高亮tag
const isActive = (route_) => {
	return route_.fullPath === route.fullPath
}

//关闭tag
const closeSelectedTag = (tag, autoPushLatestView = true) => {
	const nowTagIndex = tagList.value.findIndex(item => item.fullPath == tag.fullPath)
	const viewTagsStore = useViewTagsStore();
	const iframeStore = useIframeStore();
	const keepAliveStore = useKeepAliveStore();
	viewTagsStore.removeViewTags(tag)
	iframeStore.removeIframeList(tag)
	keepAliveStore.removeKeepLive(tag.name)
	if (autoPushLatestView && isActive(tag)) {
		const leftView = tagList.value[nowTagIndex - 1]
		if (leftView) {
			router.push(leftView)
		} else {
			router.push('/')
		}
	}
}

//tag右键
const openContextMenu = (e, tag) => {
	contextMenuItem.value = tag;
	contextMenuVisible.value = true;
	left.value = e.clientX + 1;
	top.value = e.clientY + 1;

	//FIX 右键菜单边缘化位置处理
	nextTick(() => {
		let sp = document.getElementById("contextmenu");
		if (document.body.offsetWidth - e.clientX < sp.offsetWidth) {
			left.value = document.body.offsetWidth - sp.offsetWidth + 1;
			top.value = e.clientY + 1;
		}
	})
}

// 关闭右键菜单
const closeMenu = () => {
	contextMenuItem.value = null;
	contextMenuVisible.value = false
}

//TAB 刷新
const refreshTab = () => {
	contextMenuVisible.value = false
	const nowTag = contextMenuItem.value;
	//判断是否当前路由，否的话跳转
	if (route.fullPath !== nowTag.fullPath) {
		router.push({
			path: nowTag.fullPath,
			query: nowTag.query
		})
	}
	iframeStore.refreshIframe(nowTag)
	setTimeout(() => {
		keepAliveStore.removeKeepLive(nowTag.name)
		keepAliveStore.setRouteShow(false)
		nextTick(() => {
			keepAliveStore.pushKeepLive(nowTag.name)
			keepAliveStore.setRouteShow(true)
		})
	}, 0);
}

// TAB 关闭
const closeTabs = () => {
	let nowTag = contextMenuItem.value;
	if (!nowTag.meta.affix) {
		closeSelectedTag(nowTag)
		contextMenuVisible.value = false
	}
}

// TAB 关闭其他
const closeOtherTabs = () => {
	let nowTag = contextMenuItem.value;
	//判断是否当前路由，否的话跳转
	if (route.fullPath != nowTag.fullPath) {
		router.push({
			path: nowTag.fullPath,
			query: nowTag.query
		})
	}
	let tags = [...tagList.value];
	tags.forEach(tag => {
		if (tag.meta && tag.meta.affix || nowTag.fullPath == tag.fullPath) {
			return true
		} else {
			closeSelectedTag(tag, false)
		}
	})
	contextMenuVisible.value = false
}

//TAB 最大化
const maximize = () => {
	let nowTag = contextMenuItem.value;
	contextMenuVisible.value = false
	//判断是否当前路由，否的话跳转
	if (route.fullPath != nowTag.fullPath) {
		router.push({
			path: nowTag.fullPath,
			query: nowTag.query
		})
	}
	document.getElementById('app').classList.add('main-maximize')
}

//新窗口打开
const openWindow = () => {
	let nowTag = contextMenuItem.value;
	let url = nowTag.href || '/';
	if (!nowTag.meta.affix) {
		closeSelectedTag(nowTag)
	}
	window.open(url);
	contextMenuVisible.value = false
}

//横向滚动
const scrollInit = () => {
	const scrollDiv = tags.value;
	scrollDiv.addEventListener('mousewheel', handler, false) || scrollDiv.addEventListener("DOMMouseScroll", handler, false)

	function handler(event) {
		const detail = event.wheelDelta || event.detail;
		//火狐上滚键值-3 下滚键值3，其他内核上滚键值120 下滚键值-120
		const moveForwardStep = 1;
		const moveBackStep = -1;
		let step = 0;
		if (detail == 3 || detail < 0 && detail != -3) {
			step = moveForwardStep * 50;
		} else {
			step = moveBackStep * 50;
		}
		scrollDiv.scrollLeft += step;
	}
}
</script>

<style>
.contextmenu {
	position: fixed;
	width: 200px;
	margin: 0;
	border-radius: 0px;
	background: var(--el-bg-color-overlay);
	border: 1px solid var(--el-border-color-light);
	box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
	z-index: 3000;
	list-style-type: none;
	padding: 10px 0;
}

.contextmenu hr {
	margin: 5px 0;
	border: none;
	height: 1px;
	font-size: 0px;
	background-color: var(--el-border-color-light);
}

.contextmenu li {
	display: flex;
	align-items: center;
	margin: 0;
	cursor: pointer;
	line-height: 30px;
	padding: 0 17px;
	color: #606266;
}

.contextmenu li i {
	font-size: 14px;
	margin-right: 10px;
}

.contextmenu li:hover {
	background-color: #ecf5ff;
	color: #66b1ff;
}

.contextmenu li.disabled {
	cursor: not-allowed;
	color: #bbb;
	background: transparent;
}

.tags-tip {
	padding: 5px;
}

.tags-tip p {
	margin-bottom: 10px;
}

.dark .contextmenu li {
	color: var(--el-text-color-primary);
}

</style>
