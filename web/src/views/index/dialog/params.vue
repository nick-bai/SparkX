<template>
	<el-dialog title="设置参数" v-model="visible" :width="600" destroy-on-close @closed="$emit('closed')">
		<el-form :model="form" label-width="10px" label-position="top" style="margin-left:30px">
			<el-form-item label="">
				<el-radio-group v-model="form.searchMode" class="too-radio-list">
					<el-radio border label="embedding" class="radio-item">
						<div class="radio-title">向量检索</div>
						<div class="radio-desc">通过向量距离计算与用户问题最相似的文本分段</div>
					</el-radio>
					<el-radio border label="text" class="radio-item">
						<div class="radio-title">全文检索</div>
						<div class="radio-desc">通过关键词检索，返回包含关键词最多的文本分段</div>
					</el-radio>
					<el-radio border label="mix" class="radio-item">
						<div class="radio-title">混合检索</div>
						<div class="radio-desc">同时执行全文检索和向量检索，再进行重排序，从两类查询结果中选择匹配用户问题的最佳结果</div>
					</el-radio>
				</el-radio-group>
			</el-form-item>
			<el-form-item label="">
				<div class="score-list">
					<div class="score-item">
						<div class="item-title">置信度高于</div>
						<div class="item-input"><el-input-number v-model="form.similarity" :min="0" :precision="3"></el-input-number></div>
					</div>
					<div class="score-item">
						<div class="item-title">召回数量</div>
						<div class="item-input"><el-input-number v-model="form.topRank" :min="1"></el-input-number></div>
					</div>
				</div>
			</el-form-item>
			<!--<el-form-item label="无召回片段">
				<el-switch
					:active-value="1"
					:inactive-value="2"
					active-text="AI回复"
					inactive-text="指定回复"
					v-model="form.emptyReply">
				</el-switch>
			</el-form-item>
			<el-form-item label="回复内容" v-if="form.emptyReply == 2" style="width: calc(100% - 40px);">
				<el-input type="textarea" v-model="form.replyContent" rows="3" maxlength="255" show-word-limit></el-input>
			</el-form-item>
			<el-form-item>
				<template #label>
					问题优化
					<el-tooltip effect="dark" content="开启优化，系统将上下文提交给AI获得更精确的用户提问消息消除歧义" placement="top-start">
						<el-icon size="16"><InfoFilled /></el-icon>
					</el-tooltip>
				</template>
				<el-switch
					:active-value="1"
					:inactive-value="2"
					v-model="form.compressingQuery">
				</el-switch>
			</el-form-item>-->
		</el-form>
		<template #footer>
			<div class="dialog-footer">
				<el-button @click="visible = false">取 消</el-button>
				<el-button type="primary" @click="optSubmit">确 定</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
import {InfoFilled} from "@element-plus/icons-vue";

export default {
	components: {InfoFilled},
	emits: ['success', 'closed'],
	data() {
		return {
			visible: false,
			form: {}
		}
	},
	methods: {
		//显示
		open(mode = 'add') {
			this.mode = mode;
			this.visible = true;
			return this
		},
		// 表单提交方法
		optSubmit() {
			this.$emit("success", this.form)
		},
		setData(row) {
			this.form = row
			console.log('xx', this.form)
		}
	}
}
</script>

<style scoped>
.hit-list {
	height: calc(100vh - 210px);
	width: 100%;
	background: #fff;
	padding: 10px;
	overflow-y: scroll;
}
.hit-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.hit-list .content {
	background: #f4f4f4;
	width: 100%;
	padding: 20px 10px;
	display: grid;
	box-sizing: border-box;
	margin-top: 16px;
	gap: 16px;
	grid-template-columns: repeat(auto-fill, minmax(24%, 1fr));
}
.paragraph-item {
	background: #fff;
	height: 250px;
	border-radius: 5px;
	cursor: pointer;
	padding: 15px 12px 15px 12px;
	margin-right: 5px;
	flex-direction: column;
	align-items: flex-start;
}
.paragraph-title {
	width: 100%;
	height: 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.title-left {
	width: 320px;
}
.paragraph-doc {
	width: 100%;
	height: calc(100% - 81px);
	color: #606266;
	margin-top: 10px;
}
.paragraph-id {
	color: #5c5f66;
	border-bottom: 1px solid #e2e2e2;
	font-size: 13px;
	padding-bottom: 5px;
}
.paragraph-bottom {
	width: 100%;
	height: 30px;
	margin-top: 10px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-bottom: 10px;
	color: #5c5f66;
}
.too-radio-list {
	width: 100%;
	display: flex;
}
.radio-item {
	margin-bottom: 10px;
	width: calc(100% - 40px);
	height: 85px;
	font-size: 13px;
}
.radio-desc {
	margin-top: 3px;
	margin-left: 10px;
	color: #8f959e;
	text-wrap: wrap;
	line-height: 17px;
}
.radio-title {
	margin-left: 10px;
}
.score-list {
	display: flex;
	width: 100%;
}
.score-item {
	width: 50%;
	display: flex;
	flex-direction: column;
}
</style>
