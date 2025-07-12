<template>
	<div style="width:100%;height:100%;position: relative">
		<!-- 顶部菜单栏 -->
		<top-menu
			class="top-menu"
			@debug="debugHandle"
			@save="saveHandle"
			@back="backHandle"
		>
		</top-menu>
		<div ref="containerRef" class="container"/>
		<!-- 组件菜单 -->
		<menu-box
			v-if="visible"
			@add-node="addNodeHandle"
			class="add-menu-box">
		</menu-box>
		<!-- 执行详情 -->
		<el-dialog
			width="800px"
			title="执行详情"
			:close-on-click-modal="false"
			v-model="runtimeVisible">
			<runtime-box
				:key="runtimeKey"
				:runtime-id="runtimeId">
			</runtime-box>
		</el-dialog>
		<!-- 调试聊天窗口 -->
		<debug-chat
			:access-token="accessToken"
			:key="debugKey"
			@close-debug="chatVisible=false"
			@show-detail="showDetailHandle"
			v-if="chatVisible">
		</debug-chat>
		<!-- 底部菜单栏 -->
		<bottom-menu
			:key="randomKey"
			:out-open="outOpen"
			class="bottom-menu"
			@open-menu="openMenuHandle"
			@center="centerHandle"
			@zoom-in="zoomInHandle"
			@zoom-out="zoomOutHandle">
		</bottom-menu>
		<!-- 菜单设置 -->
		<el-drawer
			:size="600"
			v-model="drawer"
			append-to-body
			destroy-on-close>
			<div class="pages">
				<el-button type="text" @click="delNodeHandle" style="font-size: 12px;float: right;margin-right: 30px;margin-top: 20px;" v-if="nowNode.shape !== 'start-node'">删除节点</el-button>
				<component
					:key="componentsKey"
					:form-data="formData"
					@port-del="portDelHandle"
					@port-add="portAddHandle"
					@port-update="portUpdate"
					@data-change="dataChangeHandle"
					:input-options="inputOptions"
					:is="page"
				/>
			</div>
		</el-drawer>
	</div>
</template>
<script>
// 引入 AntV X6 库
import {Graph, Shape} from '@antv/x6'
import defaultNodeConfig from './node.js'
import bottomMenu from './menu/bottomMenu.vue'
import topMenu from './menu/topMenu.vue'
import menuBox from './menu/menuBox.vue'
import {defineAsyncComponent} from "vue";
import inputDataUtil from './inputData.js'
import debugChat from './menu/debug.vue'
import runtimeBox from './menu/runtime.vue'
import {MoreFilled} from "@element-plus/icons-vue"
import nodeCheck from './nodeCheck.js'

export default {
	components: {
		MoreFilled,
		bottomMenu,
		topMenu,
		menuBox,
		debugChat,
		runtimeBox
	},
	data() {
		return {
			nowNode: null,
			visible: false,
			graph: null,
			outOpen: false,
			componentsKey: Math.random(),
			randomKey: Math.random(),
			debugKey: Math.random(),
			drawer: false,
			chatVisible: false,
			runtimeVisible: false,
			page: '', // 当前页面
			pages: { // 设置页面
				start: defineAsyncComponent(() => import('./dialog/startDialog.vue')),
				purpose: defineAsyncComponent(() => import('./dialog/purposeDialog.vue')),
				llm: defineAsyncComponent(() => import('./dialog/llmDialog.vue')),
				dataset: defineAsyncComponent(() => import('./dialog/datasetDialog.vue')),
				answer: defineAsyncComponent(() => import('./dialog/answerDialog.vue')),
				switch: defineAsyncComponent(() => import('./dialog/switchDialog.vue')),
				agent: defineAsyncComponent(() => import('./dialog/agentDialog.vue')),
			},
			formData: {}, // 配置数据
			inputOptions: [], // 入参
			nodeNoData: { // 页面中不同组件的数量
				purpose: 0,
				agent: 0,
				answer: 0,
				llm: 0,
				dataset: 0,
				switch: 0
			},
			appId: '',
			flowData: null,
			runtimeId: 173,
			runtimeKey: Math.random(),
			accessToken: ""
		}
	},
	created() {
		this.appId = this.$route.query.appId
	},
	mounted() {
		this.initGraph()
		this.getWorkflowInfo()
	},
	methods: {
		// 初始化
		initGraph() {
			const containerRef = this.$refs.containerRef
			// 初始化 Graph 对象
			const graph = new Graph({
				container: containerRef, // 容器元素
				selecting: true,
				history: true, // 启动历史记录
				interacting: {
					nodeMovable: true, // 可拖拽节点
					edgeMovable: false // 可拖拽边
				},
				background: {
					color: '#f4f4f4',
				},
				// 网格
				grid: {
					visible: true
				},
				// Scroller 使画布具备滚动、平移、居中、缩放等能力
				scroller: {
					enabled: true,
					pageVisible: true,
					pageBreak: true,
					pannable: true,
				},
				connecting: {
					connector: 'smooth',
					snap: true, // 自动吸附
					allowBlank: false, // 是否允许连接到画布空白位置的点
					allowLoop: false, // 是否允许创建循环连线，即边的起始节点和终止节点为同一节点
					allowNode: false, // 是否允许边链接到节点（非节点上的链接桩）
					createEdge() {
						return new Shape.Edge({
							attrs: {
								line: {
									stroke: '#d0d5dc',
									strokeWidth: 2,    // 设置连接线宽度
									targetMarker: null, // 去掉终点箭头
									sourceMarker: null, // 去掉起点箭头
								}
							},
							// 添加工具（删除按钮）
							tools: [],
						});
					},
					allowPort(arg) { // 验证是否可以连接
						// 通用函数，根据端口 ID 查找端口类型
						const getPortType = (ports, portId) => {
							const port = ports.find(port => port.id === portId);
							return port ? port.type : null;
						};

						const sourcePortType = getPortType(arg.sourceCell.port.ports, arg.sourcePort);
						const targetPortType = getPortType(arg.targetCell.port.ports, arg.targetPort);

						// 不允许反向链接
						if (sourcePortType === 'input') {
							return false;
						}

						// 相同类型的节点不允许连接
						if (sourcePortType === targetPortType) {
							return false;
						}

						return true
					}
				}
			})

			this.graph = graph

			// 创建组件节点
			let startNodeData = defaultNodeConfig.startNode(100, 240)
			graph.addNode(startNodeData)

			// 节点移入
			graph.on('node:mouseenter', () => {
				setVisible('visible')
			})

			// 节点点击
			graph.on('node:click', ({ node }) => {
				resetSel()

				this.nowNode = node
				node.updateData({checked: true})

				this.formData = node.getData()
				this.formData.appId = this.appId
				let nowPage = this.formData.pages
				this.page = this.pages[nowPage]
				if (['start'].indexOf(nowPage) === -1) {
					// 计算节点前的数据
					this.getNodeInputData()
				}

				this.componentsKey = Math.random()
				this.drawer = true
			})

			// 点击空白处
			graph.on('blank:click', () => {
				resetSel()

				this.outOpen = false
				this.randomKey = Math.random()
				this.visible = false
				this.nowNode = null
				setVisible('hidden')
			})

			// 节点移出
			graph.on('node:mouseleave', () => {
				if (!this.nowNode) {
					setVisible('hidden')
				}
			})

			// 连接线移入
			graph.on('edge:mouseenter', ({ edge }) => {
				edge.addTools([
					{ name: 'button-remove' }
				])
				edge.attr('line', { stroke: 'var(--el-color-theme)', strokeWidth: 1 })
			})

			// 连接线移出
			graph.on('edge:mouseleave', ({ edge }) => {
				edge.removeTools()
				edge.attr('line', { stroke: '#d0d5dc', strokeWidth: 2 })
			})

			function setVisible(visibility) {
				setTimeout(() => {
					const ports = document.querySelectorAll(".x6-port-body")
					for (let i = 0, len = ports.length; i < len; i = i + 1) {
						ports[i].style.visibility = visibility;
					}
				}, 100)
			}

			function resetSel() {
				graph.getNodes().forEach(node => {
					node.updateData({checked: false})
				})
			}
		},
		// 居中布局
		centerHandle() {
			this.graph.centerContent();
			this.graph.zoom(0);
		},
		// 放大
		zoomInHandle() {
			this.graph.zoom(0.1);
			this.canZoomOut = true;
		},
		// 缩小
		zoomOutHandle() {
			if (!this.canZoomOut) return;
			const num = Number(this.graph.zoom().toFixed(1));

			if (num > 0.1) {
				this.graph.zoom(-0.1);
			} else {
				this.canZoomOut = false;
			}
		},
		// 操作组件菜单
		openMenuHandle(visible) {
			this.visible = visible
		},
		// 连接桩增加
		portAddHandle(val) {
			if (val.type === 'purpose') {
				let len = val.cateList.length
				let y = (len - 1) * 40 + 100
				this.nowNode.addPort({ group: 'rightPorts', args: { x: 230, y: y }, type: 'output'})
			} else if (val.type === 'switch') {
				let ports = this.nowNode.port.ports
				let data = this.nowNode.store.data.data.ifBranch
				let totalNodes = data[data.length - 2].data.length

				let y = ports[ports.length - 1].args.y + (totalNodes - 1) * 35 + 70

				this.nowNode.addPort({ group: 'rightPorts', args: { x: 230, y: y }, type: 'output'})
				this.endPortUpdate()
			}
		},
		// 连接桩更新
		portUpdate(val, index) {
			if (val.type === 'switch') {

				let ports = this.nowNode.port.ports
				let data = this.nowNode.store.data.data.ifBranch
				if (ports.length >= 3) {
					for (let i = index + 3; i < ports.length; i++) {
						let totalNodes = data[i - 3].data.length
						this.nowNode.port.ports[i].args.y = ports[i - 1].args.y + (totalNodes - 1) * 35 + 70
					}

					this.nowNode.setPropByPath('ports/items', this.nowNode.port.ports)
					this.endPortUpdate()
				}
			}
		},
		// 更新最后一个节点的位置
		endPortUpdate() {
			let data = this.nowNode.store.data.data.ifBranch
			let totalNodes = data[data.length - 1].data.length
			let ports = this.nowNode.port.ports
			this.nowNode.port.ports[1].args.y = ports[ports.length - 1].args.y + (totalNodes - 1) * 35 + 60

			this.nowNode.setPropByPath('ports/items', this.nowNode.port.ports)
		},
		// 连接桩删除
		portDelHandle() {
			const ports = this.nowNode.getPorts()
			if (ports.length) {
				this.nowNode.removePortAt(ports.length - 1)
			}
		},
		// 节点内部设置
		dataChangeHandle(val) {
			this.nowNode.updateData(val)
		},
		// 调试链接
		debugHandle() {
			// 节点参数检测
			let res = nodeCheck.check(this.graph.toJSON())
			if (res.code !== 0) {
				this.$message.error(res.msg)
				return
			}

			this.chatVisible = true
		},
		// 展示执行详情
		showDetailHandle(runtimeId) {
			this.runtimeKey = Math.random()
			this.runtimeId = runtimeId
			this.runtimeVisible = true
		},
		// 获取流程信息
		async getWorkflowInfo() {
			let res = await this.$API.workflow.info.get({appId: this.appId})
			this.accessToken = res.data.accessToken
			if (res.data.flowData) {
				this.flowData = JSON.parse(res.data.flowData)
				this.graph.fromJSON(this.flowData)
				// 记录基础节点
				this.nodeNoData = []
				this.flowData.cells.forEach(node => {
					if (node.shape !== 'edge') {
						let type = node.shape.split("-")[0]
						if (this.nodeNoData[type]) {
							this.nodeNoData[type] += 1
						} else {
							this.nodeNoData[type] = 1
						}
					}
				})
			}
		},
		// 保存设计
		async saveHandle() {
			// 节点参数检测
			let checkRes = nodeCheck.check(this.graph.toJSON())
			if (checkRes.code !== 0) {
				this.$message.error(checkRes.msg)
				return
			}

			let res = await this.$API.workflow.save.post({appId: this.appId, flowData: JSON.stringify(this.graph.toJSON())})
			if (res.code === 0) {
				this.$message.success(res.msg)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 获取节点前数据
		getNodeInputData() {
			this.inputOptions = inputDataUtil.getNodeInputData(this.nowNode, this.graph)
		},
		// 添加节点
		addNodeHandle(type) {
			function getRandomInt(min, max) {
				return Math.floor(Math.random() * (max - min + 1)) + min;
			}

			// 更新节点计数并添加节点
			if (isNaN(this.nodeNoData[type])) {
				this.nodeNoData[type] = 1
			} else {
				this.nodeNoData[type] += 1
			}

			this.graph.addNode(JSON.parse(JSON.stringify(
				defaultNodeConfig[type + 'Node'](
					getRandomInt(300, 600),
					getRandomInt(300, 600),
					this.nodeNoData[type]
				)
			)))
		},
		// 返回列表
		backHandle() {
			this.$router.push('/index/detail?appId=' + this.appId)
		},
		// 删除节点
		delNodeHandle() {
			this.graph.removeNode(this.nowNode.id)
			this.drawer = false
		},
	}
}
</script>
<style scoped>
.container {
	width: 100%;
	height: 100vh;
}
.bottom-menu {
	position: absolute;
	bottom: 20px;
	left: 20px;
	cursor: pointer;
}
.top-menu {
	position: absolute;
	top: 0;
	z-index: 999;
}
.add-menu-box {
	position: absolute;
	bottom: 70px;
	left: 100px;
}
</style>
