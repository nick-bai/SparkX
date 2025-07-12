import {createApp} from 'vue'
import ElementPlus from 'element-plus'
import scui from './scui'
import i18n from './locales'
import router from './router'
import App from './App.vue'
import 'element-plus/dist/index.css';
import { config } from 'md-editor-v3'

import highlight from 'highlight.js' // 代码高亮
import 'highlight.js/styles/atom-one-dark.css'

import katex from 'katex' // 科学算式
import 'katex/dist/katex.min.css'

import Cropper from 'cropperjs' // 截图
import 'cropperjs/dist/cropper.css'

import {createPinia} from "pinia";
import directives from './directives/directives.js';

config({
	editorExtensions: {
		highlight: {
			instance: highlight
		},
		katex: {
			instance: katex
		},
		cropper: {
			instance: Cropper
		}
	}
})

const pinia = createPinia()
const app = createApp(App);

app.use(pinia);

app.use(router);
app.use(ElementPlus);
app.use(i18n);
app.use(scui);
app.directive('click-outside', directives);

//挂载app
app.mount('#app');
