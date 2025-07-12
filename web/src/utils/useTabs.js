import {nextTick} from 'vue'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import router from '@/router'
import {useKeepAliveStore} from "@/stores/keepAlive.js";
import {useIframeStore} from "@/stores/iframe.js";
import {useViewTagsStore} from "@/stores/viewTags.js";

export default {
	//刷新标签
	async refresh() {

		const keepAliveStore = useKeepAliveStore()

		NProgress.start()

		const route = router.currentRoute.value
		keepAliveStore.removeKeepLive(route.name)
		keepAliveStore.setRouteShow(false)
		await nextTick(() => {
			keepAliveStore.pushKeepLive(route.name)
			keepAliveStore.setRouteShow(true)
			NProgress.done()
		})
	},
	//关闭标签
	async close(tag) {
		const viewTagsStore = useViewTagsStore()
		const iframeStore = useIframeStore()
		const keepAliveStore = useKeepAliveStore()

		const route = tag || router.currentRoute.value
		viewTagsStore.removeViewTags(route)
		iframeStore.removeIframeList(route)
		keepAliveStore.removeKeepLive(route.name)
		const tagList = viewTagsStore.viewTags
		const latestView = tagList.slice(-1)[0]
		if (latestView) {
			await router.push(latestView)
		} else {
			await router.push('/')
		}
	},
	//关闭标签后处理
	closeNext(next) {
		const route = router.currentRoute.value
		const viewTagsStore = useViewTagsStore()
		const iframeStore = useIframeStore()
		const keepAliveStore = useKeepAliveStore()

		viewTagsStore.removeViewTags(route)
		iframeStore.removeIframeList(route)
		keepAliveStore.removeKeepLive(route.name)
		if (next) {
			const tagList = viewTagsStore.viewTags
			next(tagList)
		}
	},
	//关闭其他
	closeOther() {
		const viewTagsStore = useViewTagsStore()

		const route = router.currentRoute.value
		const tagList = [...viewTagsStore.viewTags]
		tagList.forEach(tag => {
			if (tag.meta && tag.meta.affix || route.fullPath == tag.fullPath) {
				return true
			} else {
				this.close(tag)
			}
		})
	},
	//设置标题
	setTitle(title) {
		const viewTagsStore = useViewTagsStore()
		viewTagsStore.updateViewTagsTitle(title)
	}
}
