<template>
	<div class="base-div">
		<div class="application-info">
			<div class="application-title">应用信息</div>
			<div class="app-desc">
				<div class="base-info-item">
					<div class="app-title-box">
						<div class="title-label">{{ appInfo.name?.substring(0, 1)  }}</div>
						<div class="app-title">{{ appInfo.name }}</div>
					</div>
					<div class="base-style">
						是否对外发布
						<el-switch
							style="margin-left: 10px"
							v-model="appInfo.status"
							disabled
						>
						</el-switch>
					</div>
					<div class="base-style code-bg" style="width: 600px">
						{{ domain }}/#/chat/{{ accessToken }}
						<el-icon size="16px" style="margin-left: 5px"><CopyDocument @click="copy"/></el-icon>
					</div>
					<div class="base-style">
						<el-button type="danger" @click="goChat">本地调试</el-button>
						<el-button @click="showDeploy">三方嵌入</el-button>
					</div>
				</div>
				<!--<div class="base-info-item">
					<h3 style="margin-bottom: 10px">后端服务API</h3>
					API访问凭据
					<div class="base-style code-bg" style="width: 600px">http://localhost:8090/ui/chat/71c8380fe2196c3a <el-icon size="16px" style="margin-left: 5px"><CopyDocument /></el-icon></div>
					<div class="base-style">
						<el-button>API秘钥</el-button>
					</div>
				</div>-->
			</div>
		</div>
		<div class="census-list">
			<div class="application-title">数据统计</div>
			<div class="flex-center time-select">
				<el-select v-model="searchForm.days" style="width: 150px" @change="dayChange">
					<el-option label="过去7天" :value="1" />
					<el-option label="过去30天" :value="2" />
					<el-option label="过去90天" :value="3" />
					<el-option label="过去半年" :value="4" />
					<el-option label="自定义" :value="5" />
				</el-select>
				<div style="width: 200px;margin-left: 20px" v-if="searchForm.days === 5">
					<el-date-picker
						v-model="dayRange"
						type="daterange"
						value-format="YYYY-MM-DD"
						unlink-panels
						range-separator="至"
						start-placeholder="开始日期"
						end-placeholder="结束日期"
					/>
				</div>
			</div>
			<div class="flex-center census-list">
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(235, 241, 255)">
						<el-icon size="24" color="rgb(51, 112, 255)"><Avatar /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">用户总数</div>
						<span>{{ censusData.userNum }}</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(255, 243, 229)">
						<el-icon size="24" color="rgb(255, 136, 0)"><ChatLineRound /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">提问次数</div>
						<span>{{ censusData.questionNum }}</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(229, 251, 248)">
						<el-icon size="24" color="rgb(0, 214, 185)"><Key /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">Tokens 总数</div>
						<span>{{ censusData.tokensNum }}</span>
					</div>
				</div>
				<div class="flex-center census-card">
					<el-avatar shape="square" style="background: rgb(254, 237, 236)">
						<el-icon size="24" color="rgb(245, 74, 69)"><Star /></el-icon>
					</el-avatar>
					<div class="info-item">
						<div class="info-title">用户满意度</div>

						<div class="flex-center">
							<div class="flex-center">
								<span class="iconfont icon-zan icon-style" style="font-size: 14px"></span>
								<span style="margin-left: 5px">{{ censusData.likeNum }}</span>
							</div>
							<div class="flex-center" style="margin-left: 10px">
								<span class="iconfont icon-cai icon-style" style="font-size: 14px"></span>
								<span style="margin-left: 5px">{{ censusData.dislikeNum }}</span>
							</div>
						</div>
					</div>
				</div>
			</div>

			<div class="flex-center" style="justify-content: space-between;flex-wrap: wrap;">
				<div class="census-data-card">
					<scEcharts height="320px" :option="userOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="questionOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="tokenOption"></scEcharts>
				</div>
				<div class="census-data-card">
					<scEcharts height="320px" :option="appraiseOption"></scEcharts>
				</div>
			</div>
		</div>
	</div>

	<deploy-dialog v-if="deployVisible" ref="deployDialog" @closed="deployVisible=false" :close-on-click-modal="false"></deploy-dialog>
</template>

<script>
import {Avatar, ChatLineRound, CopyDocument, Key, Star} from "@element-plus/icons-vue";
import scEcharts from "@/components/scEcharts/index.vue";
import deployDialog from '@/views/index/dialog/deploy.vue'
import saveDialog from "@/views/dataset/save.vue";

export default {
	components: {saveDialog, Star, Key, ChatLineRound, Avatar, CopyDocument, scEcharts, deployDialog},
	data() {
		return {
			open: 1,
			deployVisible: false,
			dayRange: [],
			baseOption: {
				title: {
					text: ''
				},
				legend: {
					data: []
				},
				xAxis: {
					type: 'category',
					data: []
				},
				yAxis: {
					type: 'value'
				},
				series: [],
				tooltip: {
					trigger: 'axis',
					axisPointer: {
						type: 'cross',
						label: {
							backgroundColor: '#6a7985'
						}
					}
				}
			},
			userOption: {},
			questionOption: {},
			tokenOption: {},
			appraiseOption: {},
			appId: "",
			appInfo: {},
			domain: window.location.origin,
			censusData: {
				userNum: 0,
				questionNum: 0,
				tokensNum: 0,
				likeNum: 0,
				dislikeNum: 0
			},
			searchForm: {
				days: 1,
				startTime: "",
				endTime: "",
			},
			accessToken: ""
		}
	},
	watch: {
		dayRange(newVal, oldVal) {
			this.census()
		}
	},
	mounted() {
		this.appId = this.$route.query.appId;
		this.getAppInfo()
		this.census()
	},
	methods: {
		// 获取应用信息
		async getAppInfo() {
			let res = await this.$API.application.info.get({appId: this.appId})
			this.appInfo = res.data
			this.accessToken = res.data.accessToken
		},
		// 前往聊天
		goChat() {
			this.$router.push('/chat/' + this.accessToken + '?debug=true')
		},
		// 复制
		copy() {
			let text = this.domain + '/chat/' + this.appId
			navigator.clipboard.writeText(text).then(() => {
				this.$message.success('复制成功')
			}).catch(error => {
				console.log('错误', error)
				this.$message.error('复制错误')
			});
		},
		// 选择了日期
		dayChange() {
			if (this.searchForm.days !== 5) {
				this.census()
			}
		},
		// 统计数据
		async census() {
			if (this.dayRange.length > 0) {
				this.searchForm.startTime = this.dayRange[0]
				this.searchForm.endTime = this.dayRange[1]
			}

			let res = await this.$API.application.census.get(this.searchForm)
			this.censusData.userNum = res.data.userNum
			this.censusData.questionNum = res.data.questionNum
			this.censusData.tokensNum = res.data.tokensNum
			this.censusData.likeNum = res.data.likeNum
			this.censusData.dislikeNum = res.data.dislikeNum

			this.baseOption.xAxis.data = res.data.timeLine
			let baseData = this.baseOption
			this.userOption = JSON.parse(JSON.stringify(baseData))
			this.questionOption = JSON.parse(JSON.stringify(baseData))
			this.tokenOption = JSON.parse(JSON.stringify(baseData))
			this.appraiseOption = JSON.parse(JSON.stringify(baseData))

			this.userOption.title.text = "用户总数"
			this.userOption.series = res.data.userSeries

			this.questionOption.title.text = "提问次数"
			this.questionOption.series = res.data.questionSeries

			this.tokenOption.title.text = "tokens总数"
			this.tokenOption.series = res.data.tokensSeries

			this.appraiseOption.title.text = "用户满意度"
			this.appraiseOption.legend.data = ['答的不错', '还不够好']
			res.data.likeSeries.name = '答的不错'
			res.data.dislikeSeries.name = '还不够好'
			this.appraiseOption.series = [res.data.likeSeries, res.data.dislikeSeries]
		},
		// 显示三方部署
		showDeploy() {

			let location = window.location
			this.deployVisible = true
			this.$nextTick(() => {
				this.$refs.deployDialog.open({
					url1: this.domain + '/#/chat/' + this.accessToken,
					url2: this.domain + '/script.js?protocol=' + location.protocol +
						'&host=' + location.host + '&token=' + this.accessToken
				})
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.application-info {
	width: 100%;
}
.application-title {
	font-size: 16px;
	padding-left: 10px;
	border-left: 5px solid var(--el-color-theme);
	font-weight: bold;
}
.base-div {
	background: #fff;
	border-radius: 10px;
	padding: 20px;
	height: calc(100vh - 110px);
	overflow-y: scroll;
}

.app-desc {
	height: 200px;
	margin-top: 20px;
	display: flex;
	align-items: center;
	width: 100%;
	border: 1px solid #e4e7ed;
	padding: 20px;
	margin-bottom: 20px;
	border-radius: 5px;
}

.app-title-box {
	height: 32px;
	display: flex;
	align-items: center;

	.app-title {
		margin-left: 5px;
		font-size: 14px;
		font-weight: bold;
	}
}
.base-info-item {
	width: 50%;
	height: 100%;
}
.title-label {
	width: 32px;
	height: 32px;
	border-radius: 5px;
	background: var(--el-color-theme);
	line-height: 32px;
	text-align: center;
	color: #fff;
}
.time-select {
	margin-top: 10px;
}
.census-list {
	width: 100%;
	margin-top: 20px;
	justify-content: space-between;
}
.census-card {
	width: 24%;
	border: 1px solid #e4e7ed;
	height: 85px;
	border-radius: 5px;
	padding: 10px 20px;
}
.info-item {
	margin-left: 20px;
	span {
		font-size: 20px;
		font-weight: 500;
	}
}
.info-title {
	color: #646a73;
	font-weight: 400;
	margin-bottom: 5px;
}
.census-data-card {
	width: 49%;
	border: 1px solid #e4e7ed;
	height: 365px;
	margin-top: 20px;
	padding: 10px;
}
</style>
