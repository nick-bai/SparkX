import {defineStore} from "pinia";

export const useIframeStore = defineStore('iframe', {
	state: () => {
		return {
			iframeList: []
		}
	},
	actions: {
		setIframeList(route) {
			this.iframeList = []
			this.iframeList.push(route)
		},
		pushIframeList(route) {
			let target = this.iframeList.find((item) => item.path === route.path)
			if (!target) {
				this.iframeList.push(route)
			}
		},
		removeIframeList(route) {
			this.iframeList.forEach((item, index) => {
				if (item.path === route.path) {
					this.iframeList.splice(index, 1)
				}
			})
		},
		refreshIframe(route) {
			this.iframeList.forEach((item) => {
				if (item.path == route.path) {
					let url = route.meta.url;
					item.meta.url = '';
					setTimeout(function () {
						item.meta.url = url
					}, 200);
				}
			})
		},
		clearIframeList() {
			this.iframeList = []
		}
	}
})
