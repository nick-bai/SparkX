import {fileURLToPath, URL} from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'

import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default ({mode}) => {

	const env = loadEnv(mode, process.cwd());

	return defineConfig({
		server: {
			proxy: {
				"/api": {
					target: env.VITE_VUE_APP_API_BASEURL,
					changeOrigin: true,
					rewrite: (path) => path.replace(new RegExp('^/api'), '/'),
					secure: false,
					configure: (proxy, options) => {
						// 配置此项可在响应头中看到请求的真实地址
						proxy.on('proxyRes', (proxyRes, req) => {
							proxyRes.headers['x-real-url'] = new URL(req.url || '', options?.target)?.href || ''
						})
					},
				},
			},
		}, plugins: [vue(), AutoImport({
			resolvers: [ElementPlusResolver()], imports: ['vue', 'vue-router',],
		}), Components({

			// 配置需要自动注册的组件
			dts: true, resolvers: [(name) => {
				if (name.startsWith('sc')) {
					return {importName: name.slice(4), path: `@/components/${name}/index.vue`}
				}
			}, ElementPlusResolver(),],
		})], resolve: {
			alias: {
				'@': fileURLToPath(new URL('./src', import.meta.url))
			}
		}, define: {
			'process.env': {...process.env}
		}, build: {
			commonjsOptions: {transformMixedEsModules: true} // Change
		}
	})
}
