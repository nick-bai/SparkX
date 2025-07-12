<template>
	<el-dialog title="嵌入三方" v-model="visible" :width="800" destroy-on-close @closed="$emit('closed')">
		<div class="deploy-div">
			<div class="deploy-item">
				<img src="@/assets/dialog2.png" alt=""/>
				<span>全屏模式</span>
				<div class="deploy-code">
					<div class="bar">
						<div class="title">部署代码</div>
						<el-icon size="16" style="margin-left: 10px;cursor: pointer" @click="copyText(1)"><CopyDocument /></el-icon>
					</div>

					&lt;iframe
					src="{{ url1 }}"<br/>
					style="width: 100%; height: 100%;"<br/>
					frameborder="0"<br/>
					allow="microphone"&gt;<br/>
					&lt;/iframe&gt;
				</div>
			</div>
			<div class="deploy-item">
				<img src="@/assets/dialog1.png" alt=""/>
				<span>浮窗模式</span>
				<div class="deploy-code">
					<div class="bar">
						<div class="title">部署代码</div>
						<el-icon size="16" style="margin-left: 10px;cursor: pointer" @click="copyText(2)"><CopyDocument /></el-icon>
					</div>
					&lt;script<br/>
						async<br/>
						defer<br/>
						src="{{ url2 }}"&gt;<br/>
					&lt;/script&gt;
				</div>
			</div>
		</div>
	</el-dialog>
</template>

<script>
import {CopyDocument} from "@element-plus/icons-vue";

export default {
	components: {CopyDocument},
	data() {
		return {
			url1: "",
			url2: "",
			visible: false
		}
	},
	methods: {
		// 显示
		open(urlData) {
			this.visible = true
			this.url1 = urlData.url1
			this.url2 = urlData.url2

			return this
		},
		copyText(type) {
			let text = ""
			if (type === 1) {
				text = '<iframe src="' + this.url1 + '" style="width: 100%; height: 100%;" frameborder="0" allow="microphone"></iframe>'
			} else {
				text = '<script async defer src="' + this.url2 + '"><\/script>'
			}

			navigator.clipboard.writeText(text).then(() => {
				this.$message.success('复制成功')
			}).catch(error => {
				console.log('错误', error)
				this.$message.error('复制错误')
			});
		}
	}
}
</script>

<style scoped>
.deploy-div {
	width: 100%;
	height: 450px;
	display: flex;
}
.deploy-item {
	background: #f4f4f4;
	width: 50%;
	height: 100%;
	padding: 20px;
	display: flex;
	flex-direction: column;
}
.deploy-item img {
	width: 250px;
	height: 150px;
	margin: 0 auto;
	margin-top: 10px;
}
.deploy-item span {
	font-size: 14px;
	font-weight: bold;
	margin: 0 auto;
	margin-top: 10px;
}
.deploy-code {
	margin-top: 10px;
	width: 100%;
	background: #fff;
	padding: 10px 20px;
	border-radius: 5px;
	height: 190px;
	word-wrap: break-word;
}
.bar {
	display: flex;
	margin-bottom: 15px;
	justify-content: space-between;
}
</style>
