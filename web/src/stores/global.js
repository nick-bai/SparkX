import {defineStore} from 'pinia'
import config from "@/config";

export const useGlobalStore = defineStore('global', {
	state: () => {
		return {
			ismobile: false,
			//布局
			layout: config.LAYOUT,
			//菜单是否折叠 toggle
			menuIsCollapse: config.MENU_IS_COLLAPSE,
			//多标签栏
			layoutTags: config.LAYOUT_TAGS,
			//主题
			theme: config.THEME,
		}
	},
	actions: {
		SET_ismobile(key) {
			this.ismobile = key
		},
		SET_layout(key) {
			this.layout = key
		},
		SET_theme(key) {
			this.theme = key
		},
		TOGGLE_menuIsCollapse() {
			this.menuIsCollapse = !this.menuIsCollapse
		},
		TOGGLE_layoutTags() {
			this.layoutTags = !this.layoutTags
		}
	}
})
