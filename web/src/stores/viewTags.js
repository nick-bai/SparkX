import {defineStore} from "pinia";
import router from '@/router'

export const useViewTagsStore = defineStore('viewTags', {
	state: () => {
		return {
			viewTags: []
		}
	},
	actions: {
		pushViewTags(route) {
			let backPathIndex = this.viewTags.findIndex(item => item.fullPath == router.options.history.state.back)
			let target = this.viewTags.find((item) => item.fullPath === route.fullPath)
			let isName = route.name
			if (!target && isName) {
				if (backPathIndex == -1) {
					this.viewTags.push(route)
				} else {
					this.viewTags.splice(backPathIndex + 1, 0, route)
				}
			}
		},
		removeViewTags(route) {
			this.viewTags.forEach((item, index) => {
				if (item.fullPath === route.fullPath) {
					this.viewTags.splice(index, 1)
				}
			})
		},
		updateViewTags(route){
			this.viewTags.forEach((item) => {
				if (item.fullPath == route.fullPath){
					item = Object.assign(item, route)
				}
			})
		},
		updateViewTagsTitle(title = '') {
			const nowFullPath = location.hash.substring(1)
			this.viewTags.forEach((item) => {
				if (item.fullPath == nowFullPath) {
					item.meta.title = title
				}
			})
		},
		clearViewTags() {
			this.viewTags = []
		}
	}
})
