export default {
	// 开始节点初始数据
	startData: {
		type: 'start',
		sysData: [
			{field: 'sys.question', name: '用户问题'},
			{field: 'sys.time', name: '当前时间'},
			{field: 'sys.ip', name: '用户IP'},
			{field: 'sys.appId', name: '应用ID'},
		]
	},
	// 意图分类节点初始数据
	purposeData: {
		type: 'purpose',
		cateList: [
			{name: '分类1'}
		],
		modelInfo: {
			modelId: "",
			modelName: "",
			temperature: 0
		},
		inputData: [],
		outData: [
			{field: 'sys.purposeName', name: '意图分类名'},
		]
	},
	// llm节点初始数据
	llmData: {
		type: 'llm',
		modelInfo: {
			modelId: "",
			modelName: "",
			temperature: 0
		},
		userPrompt: "", // 插入变量
		memory: 2,
		systemMsg: "",
		outData: [
			{field: 'sys.content', name: '生成内容'},
		]
	},
	// 知识检索节点初始数据
	datasetData: {
		type: 'dataset',
		inputData: [],
		datasets: [],
		topRank: 3,
		similarity: 0.90,
		rerankModelId: "",
		outData: [
			{field: 'sys.result', name: '检索结果'},
		]
	},
	// 回复节点的初始数据
	answerData: {
		type: 'answer',
		inputData: [],
		answerType: 1,
		answer: "",
		outData: [
			{field: 'sys.answer', name: '回复内容'},
		]
	},
	// 条件分支节点的初始数据
	switchData: {
		type: 'switch',
		inputData: [],
		ifBranch: [
			{type: 'if', data:[{input: [], tips: "", value: ""}], switch: 1}
		],
		elseBranch: "",
		outData: [
			//{field: 'sys.branchName', name: '分支名称'},
		]
	},
	// agent节点的初始数据
	agentData: {
		type: 'agent',
		inputData: [],
		agentId: "",
		agentName: "",
		agentLogo: "",
		outData: [
			{field: 'sys.agentContent', name: 'agent输出内容'},
		]
	},
	// 基础桩点
	ports: {
		groups: {
			leftPorts: {
				position: 'left', // 端口位于节点左侧
				attrs: {
					circle: {
						style: {visibility: 'hidden'},
						r: 4,          // 端口半径
						magnet: true,  // 启用磁吸
						stroke: 'var(--el-color-theme)', // 边框颜色
						strokeWidth: 1, // 边框宽度
						fill: '#fff'    // 填充颜色
					}
				}
			},
			rightPorts: {
				position: 'right', // 端口位于节点左侧
				attrs: {
					circle: {
						style: {visibility: 'hidden'},
						r: 4,          // 端口半径
						magnet: true,  // 启用磁吸
						stroke: 'var(--el-color-theme)', // 边框颜色
						strokeWidth: 1, // 边框宽度
						fill: '#fff'    // 填充颜色
					}
				}
			},
		},
		items: [
			{ group: 'leftPorts', type: 'input' }, // 将端口分配到左侧分组
			{ group: 'rightPorts', type: 'output' }
		]
	},
	// 左边桩点
	leftPorts: {
		position: 'left', // 端口位于节点左侧
		attrs: {
			circle: {
				style: {visibility: 'hidden'},
				r: 4,          // 端口半径
				magnet: true,  // 启用磁吸
				stroke: 'var(--el-color-theme)', // 边框颜色
				strokeWidth: 1, // 边框宽度
				fill: '#fff'    // 填充颜色
			}
		}
	},
	// 选择条件
	switchOptions: [
		{type: 1, label: '为空', value: ''},
		{type: 2, label: '不为空', value: ''},
		{type: 3, label: '包含', value: ''},
		{type: 4, label: '不包含', value: ''},
		{type: 5, label: '等于', value: ''},
		{type: 6, label: '大于等于', value: ''},
		{type: 7, label: '小于', value: ''},
		{type: 8, label: '长度等于', value: ''},
		{type: 9, label: '长度大于等于', value: ''},
		{type: 10, label: '长度大于', value: ''},
		{type: 11, label: '长度小于等于', value: ''},
		{type: 12, label: '长度小于', value: ''},
	]
}
