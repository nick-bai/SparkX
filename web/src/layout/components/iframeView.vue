<!--
 * @Descripttion: 处理iframe持久化，涉及store(VUEX)
 * @version: 1.0
 * @Author: sakuya
 * @Date: 2021年6月30日13:20:41
 * @LastEditors:
 * @LastEditTime:
-->

<template>
	<div v-show="route.meta.type=='iframe'" class="iframe-pages">
		<iframe v-for="item in iframeList" :key="item.meta.url" v-show="route.meta.url==item.meta.url"
				:src="item.meta.url" frameborder='0'></iframe>
	</div>
</template>

<script setup>
import {useIframeStore} from "@/stores/iframe.js";
import {useGlobalStore} from "@/stores/global.js";
import {useRoute} from "vue-router";

const route = useRoute();

const iframeStore = useIframeStore();
const globalStore = useGlobalStore();

const {iframeList} = iframeStore
const {ismobile, layoutTags} = globalStore
onMounted(() => {
	push(route)
})

const push = (route) => {
	if (route.meta.type == 'iframe') {
		if (ismobile || !layoutTags) {
			iframeStore.setIframeList(route)
		} else {
			iframeStore.pushIframeList(route)
		}
	} else {
		if (ismobile || !layoutTags) {
			iframeStore.clearIframeList()
		}
	}
}

watch(route, (val) => {
	push(val)
})
</script>

<style scoped>
.iframe-pages {
	width: 100%;
	height: 100%;
	background: #fff;
}

iframe {
	border: 0;
	width: 100%;
	height: 100%;
	display: block;
}
</style>
