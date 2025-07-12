import {nextTick} from 'vue'
import {useViewTagsStore} from "@/stores/viewTags.js";

export function beforeEach(to, from) {

	const viewTagsStore = useViewTagsStore()

	let adminMain = document.querySelector('#adminui-main')
	if (!adminMain) {
		return false
	}
	viewTagsStore.updateViewTags({
		fullPath: from.fullPath, scrollTop: adminMain.scrollTop
	})
}

export function afterEach(to) {

	const viewTagsStore = useViewTagsStore()

	let adminMain = document.querySelector('#adminui-main')
	if (!adminMain) {
		return false
	}
	nextTick(() => {
		let beforeRoute = viewTagsStore.viewTags.filter(v => v.fullPath == to.fullPath)[0]
		if (beforeRoute) {
			adminMain.scrollTop = beforeRoute.scrollTop || 0
		}
	})
}
