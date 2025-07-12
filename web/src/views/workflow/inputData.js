export default {
	// 遍历节点
	getPreviousNodes(currentNode, graph) {

		// 获取画布中所有边
		const edges = graph.getEdges()

		let nodesArr = []
		let findNodeData = findNode([currentNode])
		while (findNodeData.length > 0) {
			for (let node of findNodeData) {
				nodesArr.push(node)
			}
			findNodeData = findNode(findNodeData)
		}

		function findNode(currentNodes) {

			let arr = []
			for (let currentNode of currentNodes) {

				for (let edge of edges) {
					if (edge.getTargetNode().id === currentNode.id) {
						arr.push(edge.getSourceNode())
					}
				}
			}

			return arr
		}

		// 过滤重复的节点
		let finalArr = []
		let alreadyIn = []
		for (let node of nodesArr) {
			if (alreadyIn.indexOf(node.id) === -1) {
				finalArr.push(node)
				alreadyIn.push(node.id)
			}
		}

		return finalArr
	},
	// 获取节点的输入数据
	getNodeInputData(nowNode, graph) {

		let nodeInputData = []
		const inputParams = this.getPreviousNodes(nowNode, graph)

		function formatData(data) {
			return data.map(item => {
				return {
					label: item.name,
					value: item.field
				}
			})
		}

		inputParams.forEach(param => {
			const data = param.getData()
			if (data.pages === 'start') {

				nodeInputData.push({
					value: param.id,
					label: '开始',
					icon: 'iconfont icon-ai23',
					color: 'var(--el-color-theme)',
					children: formatData(data.sysData.concat(data.userData))
				})
			} else if (data.pages === 'purpose') {

				let label = '意图分类'
				if (data.no > 1) {
					label += parseInt(data.no) - 1
				}

				nodeInputData.push({
					value: param.id,
					label: label,
					icon: 'iconfont icon-fenlei',
					color: '#f79009',
					children: formatData(data.outData)
				})
			} else if (data.pages === 'llm') {

				let label = 'LLM'
				if (data.no > 1) {
					label += parseInt(data.no) - 1
				}

				nodeInputData.push({
					value: param.id,
					label: label,
					icon: 'iconfont icon-a-zhuliudeLLM',
					color: '#6172f3',
					children: formatData(data.outData)
				})
			} else if (data.pages === 'dataset') {

				let label = '知识检索'
				if (data.no > 1) {
					label += parseInt(data.no) - 1
				}

				nodeInputData.push({
					value: param.id,
					label: label,
					icon: 'iconfont icon-zhishiku',
					color: '#6172f3',
					children: formatData(data.outData)
				})
			} else if (data.pages === 'answer') {

				let label = '回复'
				if (data.no > 1) {
					label += parseInt(data.no) - 1
				}

				nodeInputData.push({
					value: param.id,
					label: label,
					icon: 'iconfont icon-pinglun3-copy',
					color: '#06ae4d',
					children: formatData(data.outData)
				})
			} else if (data.pages === 'agent') {

				let label = 'Agent'
				if (data.no > 1) {
					label += parseInt(data.no) - 1
				}

				nodeInputData.push({
					value: param.id,
					label: label,
					icon: 'iconfont icon-a-agent1',
					color: '#17b26a',
					children: formatData(data.outData)
				})
			}
		})

		nodeInputData.reverse()

		return nodeInputData
	}
}
