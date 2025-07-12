<template>
	<el-scrollbar class="runtime-list">
		<el-card shadow="never" class="runtime-item" v-for="(item, index) in runtimeData" :key="index">
			<div class="runtime-title" @click="showDetail(index)">
				<div class="runtime-icon">
					<el-icon :style="{transform: currentIndex === index ? 'rotate(90deg)' : ''}">
						<CaretRight />
					</el-icon>
					<component :is="iconComponent(`${item.nodeType}-icon`)"/>
				</div>
				<div class="runtime-status">
					第<div class="run-step">{{ item.step }}</div>步
				</div>
			</div>
			<el-collapse-transition>
				<div class="runtime-content-body" v-show="currentIndex === index">
					<component :is="runtimeComponent(`${item.nodeType}-runtime`)" :runtime-data="runtimeData[index]"/>
				</div>
			</el-collapse-transition>
		</el-card>
	</el-scrollbar>
</template>

<script>
import {CaretRight} from '@element-plus/icons-vue'
import {iconComponent} from "@/views/workflow/icons/index.js"
import {runtimeComponent} from "@/views/workflow/runtime/index.js"

export default {
	props: {
		runtimeId: {
			type: Number,
			default: 0
		}
	},
	components: {
		CaretRight
	},
	data() {
		return {
			currentIndex: 0,
			runtimeData: []
		}
	},
	mounted() {
		this.getRuntimeData()
	},
	methods: {
		iconComponent,
		runtimeComponent,
		// 获取执行详情
		async getRuntimeData() {
			let res = await this.$API.workflow.runDetail.get({runtimeId: this.runtimeId})
			res.data.forEach(item => {
				item.outputData = JSON.parse(item.outputData)
				item.modelData = JSON.parse(item.modelData)
			})

			console.log(res.data)
			this.runtimeData = res.data
		},
		// 展示详情
		showDetail(index) {
			if (this.currentIndex === index) {
				this.currentIndex = -1
			} else {
				this.currentIndex = index
			}
		}
	}
}
</script>
<style>
.runtime-item .el-card__body {
	padding: 12px 16px;
}
</style>
<style scoped>
.runtime-list {
	display: flex;
	flex-direction: column;
	width: 100%;
}
.runtime-item {
	margin-top: 10px;
	cursor: pointer;
}
.runtime-title {
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.runtime-icon {
	display: flex;
	align-items: center;
}
.runtime-status {
	display: flex;
	align-items: center;
}
.runtime-status .run-step {
	background: #646a73;
	width: 20px;
	height: 20px;
	border-radius: 50%;
	margin-right: 5px;
	margin-left: 5px;
	line-height: 20px;
	text-align: center;
	color: #fff;
}
.success {
	color: #17b26a;
}
.runtime-content-body {
	font-size: 14px;
	margin-top: 10px;
}
</style>
