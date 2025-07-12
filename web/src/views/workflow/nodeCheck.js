export default {
	// 检测节点数据是否缺失
	check(graphData) {

		for (let node of graphData.cells) {

			let name = '';
			if (node.data && node.data.no > 1) {
				name = node.data.no
			}

			if (node.shape === "switch-node") { // 分支节点

				for (let branch of node.data.ifBranch) {
					for (let item of branch.data) {
						if (item.input == null || item.tips === '' || item.value === '') {

							return {code: -11, msg: "请设置【条件分支" + name + "】的条件", data: []}
						}
					}
				}
			} else if (node.shape === "dataset-node") { // 知识库检索节点

				if (node.data.inputData.length === 0) {
					return {code: -12, msg: "请设置【知识检索" + name + "】的输入参数", data: []}
				}

				if (node.data.datasets.length === 0) {
					return {code: -13, msg: "请设置【知识检索" + name + "】的关联知识库", data: []}
				}
			} else if (node.shape === "answer-node") { // 回复节点

				if (node.data.answerType === 1 && node.data.inputData.length === 0) {
					return {code: -14, msg: "请设置【回复节点" + name + "】的输入参数", data: []}
				}

				if (node.data.answerType === 2 && node.data.answer === '') {
					return {code: -15, msg: "请设置【回复节点" + name + "】的回复内容", data: []}
				}
			} else if (node.shape === "llm-node") { // LLM节点

				if (node.data.modelInfo.modelId === '') {
					return {code: -16, msg: "请设置【LLM" + name + "】的模型", data: []}
				}
			} else if (node.shape === "purpose-node") { // 意图分类节点

				if (node.data.inputData.length === 0) {
					return {code: -17, msg: "请设置【意图分类" + name + "】的输入参数", data: []}
				}

				if (node.data.modelInfo.modelId === '') {
					return {code: -18, msg: "请设置【意图分类" + name + "】的模型", data: []}
				}

				if (node.data.cateList.length === 0) {
					return {code: -19, msg: "请设置【意图分类" + name + "】的分类", data: []}
				} else {
					for (let i = 0; i < node.data.cateList.length; i++) {
						if (node.data.cateList[i].name === '') {
							return {code: -20, msg: "【意图分类" + name + "】的分类不能为空", data: []}
						}
					}
				}
			} else if (node.shape === "agent-node") { // Agent节点

				if (node.data.inputData.length === 0) {
					return {code: -21, msg: "请设置【Agent" + name + "】的输入参数", data: []}
				}

				if (node.data.agentId === '') {
					return {code: -22, msg: "请设置【Agent" + name + "】的Agent", data: []}
				}
			}
		}

		return {code: 0, msg: "success", data: []}
	}
}
