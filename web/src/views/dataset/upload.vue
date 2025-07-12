<template>
	<el-container style="padding: 20px">
		<div class="go-back"  @click="goBack" style="cursor: pointer">
			<el-icon style="font-size: 18px">
				<Back />
			</el-icon>
			<span style="margin-left: 10px;font-size: 16px;">返回</span>
		</div>

		<el-card shadow="never" class="custom-card">
			<el-steps :active="active" finish-status="success" simple>
				<el-step title="选择文档"></el-step>
				<el-step title="处理数据"></el-step>
				<el-step title="上传数据"></el-step>
			</el-steps>
			<div class="upload-box" v-if="active === 0">
				<h4 class="upload-title">上传文档</h4>
				<el-radio-group v-model="fileType" class="btn-group" @change="typeChange">
					<el-radio-button label="txt">文本文件</el-radio-button>
					<el-radio-button label="excel">Excel表格</el-radio-button>
					<el-radio-button label="qa">QA 问答对</el-radio-button>
				</el-radio-group>
				<div class="notice-box" v-if="fileType === 'txt'">
					<p>1、文件上传前，建议规范文件的分段标识</p>
					<p>2、每次最多上传 50 个文件，每个文件不超过 100MB</p>
				</div>
				<div class="notice-box" v-if="fileType === 'excel'">
					<p>1、下载系统提供的模板进行操作：<a :href="excelTpl" style="color: var(--el-color-primary)" download>下载模板</a></p>
					<p>2、数据表须采用标准结构化格式，第一行须有业务语义，系统将表中的每一条记录结合表头作为一个段落处理</p>
					<p>3、如果您设置了多个sheet，则系统将会把这些sheet当做多个文档来处理，每个文档的标题即sheet的标题</p>
					<p>4、每次最多上传 50 个文件，每个文件不超过 100MB</p>
				</div>
				<div class="notice-box" v-if="fileType === 'qa'">
					<p>1、下载系统提供的模板进行操作：<a :href="qaTpl" style="color: var(--el-color-primary)" download>下载模板</a></p>
					<p>2、如果您设置了多个sheet，则系统将会把这些sheet当做多个文档来处理，每个文档的标题即sheet的标题</p>
					<p>3、每次最多上传 50 个文件，每个文件不超过 100MB</p>
				</div>

				<el-upload
					v-model:file-list="fileList"
					ref="upload"
					style="margin-top: 20px"
					class="upload-demo"
					:limit="50"
					:acceptConfig="acceptConfig"
					:action="uploadUrl"
					:auto-upload="false"
					:show-file-list="false"
					:on-change="onChange"
					drag
					multiple
				>
					<el-icon size="48">
						<UploadFilled />
					</el-icon>
					<div class="el-upload__text">
						拖拽文件至此上传或 <em>选择文件</em>
						<p style="margin-top: 5px;font-size: 12px" v-if="fileType === 'txt'">支持格式：TXT、Markdown、PDF、DOCX、HTML、XLS、XLSX、CSV </p>
						<p style="margin-top: 5px;font-size: 12px" v-else>支持格式：XLSX </p>
					</div>
				</el-upload>

				<div class="file-list">
					<div class="file-item" v-for="(item, index) in fileList" :key="index">
						<div class="file-info">
							<img :src="`/src/assets/files_icon/` + getExtByName(item.name) + `.png`" style="width: 30px;">
							<div class="file-data">
								<div class="file-data-title line1">{{ item.name }}</div>
								<div class="file-data-size">{{ $TOOL.formatBytes(item.size) }}</div>
							</div>
						</div>
						<el-icon size="16" style="cursor: pointer" @click="delFile(index)">
							<Delete />
						</el-icon>
					</div>
				</div>
			</div>

			<div class="document-box" v-if="active === 1">
				<div class="document-tool">
					<div class="title">分段设置</div>

					<div class="tool-list">
						<el-radio-group v-model="diyForm.splitType" class="tool-radio-list">
							<el-radio :label="1" border class="radio-item">
								<div class="radio-title">默认分段</div>
								<div class="radio-desc">系统会根据换行符以512个字符为一块，自动拆分文本</div>
							</el-radio>
							<el-radio :label="2" border class="radio-item" :class="{'active-radio': diyForm.splitType === 2}">
								<div class="radio-title">自定义分段</div>
								<div class="radio-desc">根据用户自定义的规则拆分文本</div>
								<div class="tool-radio-box" v-if="diyForm.splitType === 2">
									<el-form label-position="top" label-width="80px" :model="diyForm" style="width: 450px;">
										<el-form-item>
											<template #label>
												<span>自定义分隔符</span>
												<el-tooltip class="item" effect="dark" content="填写你的文档的特殊的分段标识，来提高分段的精确性。" placement="bottom">
													<el-icon>
														<InfoFilled />
													</el-icon>
												</el-tooltip>
											</template>
											<el-input v-model="diyForm.pattern" placeholder="==SPLIT=="></el-input>
										</el-form-item>
										<el-form-item label="分段长度">
											<el-slider
												v-model="diyForm.splitLen"
												:min="100"
												:max="8000"
												show-input>
											</el-slider>
										</el-form-item>
										<el-form-item label="自动清洗">
											<el-switch
												:active-value="1"
												:inactive-value="2"
												v-model="diyForm.autoClean">
											</el-switch>
										</el-form-item>
										<p style="color:#8f9593;margin-top: -20px;font-size: 12px">去掉重复多余符号空格、空行、制表符</p>
									</el-form>
								</div>
							</el-radio>
						</el-radio-group>

						<!--<el-checkbox v-model="diyForm.addTitle" style="margin-left: 13px;margin-top: 20px"> 导入时添加分段标题为关联问题（适用于标题为问题的问答对） </el-checkbox>-->
						<div style="width: 560px;height: 40px;text-align: right;padding-top: 20px;">
							<el-button class="preview-btn" @click="preview">重新预览</el-button>
						</div>
					</div>
				</div>
				<div class="document-preview">
					<div class="title">分段预览</div>
					<div class="file-title-list scrollbar-flex-content">
						<div class="file-title-item" :class="{active: index === nowFileIndex}"
							 v-for="(item, index) in segmentTitle" :key="index"
							 @click="
							 nowFileIndex = index;
							 nowSegmentData = segmentData[nowFileIndex]">
							<el-icon size="16">
								<Document />
							</el-icon>
							<span style="margin-left: 5px">{{ item }}</span>
						</div>
					</div>

					<div class="preview-list">
						<div class="item-count">共 {{ nowSegmentData.length }} 个片段</div>

						<div class="preview-item" v-for="(item, index) in nowSegmentData" :key="index">
							<div class="too-bar">
								<div class="item-no">#{{ index + 1 }}</div>
								<div class="tool-box">
									<el-icon size="16" @click="editSegment(index)">
										<Edit />
									</el-icon>
									<el-icon size="16" style="margin-left: 10px" @click="delSegment(index)">
										<Delete />
									</el-icon>
								</div>
							</div>
							<div class="item-title">
								<div class="label">分段标题</div>
								<div class="title-body">{{ item.title === '' ? '-' : item.title }}</div>
							</div>
							<div class="item-content" style="margin-top: 10px">
								<div class="label">分段内容</div>
								<div class="content-body">
									{{ item.content }}
								</div>
								<div class="label-num">{{ (item.content).length }} 字符</div>
							</div>
						</div>

					</div>
				</div>
			</div>
		</el-card>

		<div class="tool-bar">
			<el-button>取消</el-button>
			<el-button type="primary" @click="preStep" v-if="active > 0">上一步</el-button>
			<el-button type="primary" @click="nextStep" :loading="loading">下一步</el-button>
		</div>
	</el-container>

	<el-dialog title="编辑片段" v-model="editorVisible" width="1000px" ref="saveDialog" :close-on-click-modal="false">
		<el-form :model="editForm" label-width="10px">
			<el-form-item>
				<el-input v-model="editForm.title" placeholder="标题" maxlength="255" show-word-limit></el-input>
			</el-form-item>
			<el-form-item>
				<el-input type="textarea" :rows="8" placeholder="内容" v-model="editForm.content" style="width: 100%" maxlength="8000" show-word-limit></el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="editorVisible = false">取消</el-button>
				<el-button type="primary" @click="changeDocument">确认修改</el-button>
			</el-form-item>
		</el-form>
	</el-dialog>
</template>

<script>
import config from "@/config"
import {Back, Delete, InfoFilled, UploadFilled, Document, Edit} from "@element-plus/icons-vue";

export default {
	components: {InfoFilled, Delete, UploadFilled, Back, Document, Edit},
	data() {
		return {
			active: 0,
			fileList: [],
			uploadUrl: config.API_URL + '/document/preview',
			isUpload: false,
			diyForm: {
				splitType: 1,
				pattern: "",
				splitLen: 512,
				autoClean: 1,
				addTitle: false
			},
			checked: false,
			nowFileIndex: 0,
			segmentTitle: [],
			segmentData: [],
			documentList: [],
			datasetId: '',
			nowSegmentData: [],
			fileType: 'txt',
			acceptConfig: '.txt,.md,.pdf,.docx,.html,.xls,.xlsx,.csv',
			excelTpl: '/tpl/excel表格模版.xlsx',
			qaTpl: '/tpl/qa模板.xlsx',
			loading: false,
			editorVisible: false,
			editForm: {
				title: '',
				content: '',
			},
			nowSegementIndex: 0
		}
	},
	mounted() {
		this.datasetId = this.$route.query.datasetId;
	},
	methods: {
		goBack() {
			this.$router.go(-1)
		},
		onChange(file) {
			const fileSize = file.size / 1024 / 1024
			if (fileSize > 100) {
				this.$message.error('上传的文件不得超过100M')
				return false
			}
		},
		getExtByName(name) {
			return name.split('.')[1]
		},
		delFile(index) {
			this.fileList.splice(index, 1)
		},
		// 预览
		async preview() {
			let formData = new FormData()
			// 将上传的文件放到数据对象中
			this.fileList.forEach(file => {
				formData.append('files', file.raw)
			})
			formData.append('pattern', this.diyForm.pattern)
			formData.append('splitLen', this.diyForm.splitLen)
			//formData.append('addTitle', this.diyForm.addTitle)
			formData.append('splitType', this.diyForm.splitType)
			formData.append('autoClean', this.diyForm.autoClean)
			formData.append('fileType', this.fileType) // 上传的文件类型

			let res;
			this.loading = true
			if (this.fileType === 'txt') {
				res = await this.$API.document.preview.post(formData)
				this.loading = false
				this.documentList = res.data
				this.nowFileIndex = 0
				this.segmentTitle = []
				this.segmentData = []

				this.documentList.forEach(doc => {
					this.segmentTitle.push(doc.name)
					this.segmentData.push(doc.content)
				})

				this.nowSegmentData = this.segmentData[this.nowFileIndex]
				this.active = 1
			} else {
				formData.append('datasetId', this.datasetId) // 上传的文件类型
				res = await this.$API.document.uploadFile.post(formData)
				this.loading = false
				if (res.code === 0) {
					this.$message.success('操作成功')
					setTimeout(() => {
						this.$router.push('/dataset/detail?datasetId=' + this.datasetId)
					}, 800)
				} else {
					this.$message.error(res.msg)
				}
			}
		},
		// 上一步
		preStep() {
			this.active -= 1
		},
		// 下一步
		nextStep() {
			// 预览文件
			if (this.active === 0) {
				if (this.fileList.length === 0) {
					this.$message.error('请上传文件')
					return
				}

				this.preview()
			} else if (this.active === 1) { // 上传文件
				this.uploadDocument()
			}
		},
		// 上传文件
		async uploadDocument() {
			this.loading = true
			let res = await this.$API.document.save.post({documentList: this.documentList, datasetId: this.datasetId})
			this.loading = false
			if (res.code === 0) {
				this.$message.success('上传成功')
				this.$router.push('/dataset/detail?datasetId=' + this.datasetId)
			} else {
				this.$message.error(res.msg)
			}
		},
		// 上传类型改变
		typeChange(fileType) {
			if (fileType === 'txt') {
				this.acceptConfig = '.txt,.md,.pdf,.docx,.html,.xls,.xlsx,.csv'
			} else {
				this.acceptConfig = '.xlsx'
			}
		},
		// 编辑分段
		editSegment(index) {
			this.editForm.title = this.segmentData[this.nowFileIndex][index].title
			this.editForm.content = this.segmentData[this.nowFileIndex][index].content
			this.nowSegementIndex = index

			this.editorVisible = true
		},
		// 删除分段
		delSegment(index) {
			this.segmentData[this.nowFileIndex].splice(index, 1)
		},
		// 确认修改文档
		changeDocument() {
			this.segmentData[this.nowFileIndex][this.nowSegementIndex] = this.editForm
			this.editorVisible = false
		}
	}
}
</script>
<style>
.btn-group {
	margin-top: 20px;
	border: 1px solid #bbbfc4;
	border-radius: 4px;
}
.btn-group .el-radio-button {
	padding: 3px;
}
.btn-group .el-radio-button__inner {
	border: none !important;
	border-radius: 4px !important;
	padding: 5px 8px;
	font-weight: 400;
	font-size: 13px !important;
}
.btn-group .el-radio-button__original-radio:checked + .el-radio-button__inner {
	color: var(--el-color-theme) !important;
	background: #EEE7FD !important;
	border: none !important;
	box-shadow: none !important;
	font-weight: 500;
}
</style>
<style scoped>
.go-back {
	width: 200px;
	height: 30px;
	display: flex;
	align-items: center;
	margin-bottom: 10px;
}
.upload-box {
	width: 70%;
	margin: 0 auto;
	margin-top: 20px;
}
.upload-title {
	border-left: 5px solid var(--el-color-theme);
	padding-left: 10px;
	font-size: 16px;
}
.notice-box {
	background: #eee7fd;
	border-radius: 4px;
	width: 100%;
	margin-top: 20px;
	display: flex;
	justify-content: center;
	flex-direction: column;
	padding: 10px 20px;
}
.notice-box p {
	font-weight: 400;
	margin-top: 5px;
}
.tool-bar {
	width: calc(100% - 100px);
	height: 50px;
	background: #fff;
	position: absolute;
	bottom: 0;
	display: flex;
	align-items: center;
	text-align: right;
	border-radius: 10px;
	padding-left: 80%;
}
.file-item {
	width: 49%;
	height: 58px;
	border: 1px solid var(--el-card-border-color);
	border-radius: 5px;
	margin-bottom: 10px;
	padding: 10px 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.file-item .file-info {
	display: flex;
	align-items: center;
	width: calc(100% - 150px);
}
.file-data {
	margin-left: 8px;
	width: 100%;
}
.file-data-title {
	width: 100%;
}
.file-data-size {
	color: #8f959e;
	margin-top: 5px;
}
.file-list {
	display: flex;
	flex-wrap: wrap;
	justify-content: space-between;
	overflow-y: scroll;
	margin-top: 10px;
}

.file-list  {
	scrollbar-width: none; /* Firefox */
	-ms-overflow-style: none;  /* Internet Explorer 10+ */
}

.file-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.document-box {
	width: 100%;
	height: calc(100vh - 240px);
	display: flex;
	margin-top: 20px;
}
.document-tool {
	width: 600px;
	height: 100%;
	border-right: 1px solid var(--el-card-border-color);
}
.document-preview {
	width: calc(100% - 600px);
	height: 100%;
	overflow-y: scroll;
	padding-left: 20px;
}
.document-preview::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.tool-list {
	width: 100%;
}
.tool-radio-list {
	width: 100%;
	display: flex;
	padding-left: 10px;
}
.radio-item {
	margin-top: 20px;
	width: calc(100% - 40px);
	padding: 30px 10px !important;
	font-size: 13px;
}
.radio-desc {
	margin-top: 5px;
	margin-left: 10px;
}
.radio-title {
	margin-left: 10px;
}
.tool-radio-box {
	background: #f5f6f7;
	height: 270px;
	margin-top: 10px;
	width: 500px;
	border-radius: 5px;
	padding: 20px;
}
.active-radio {
	height: 340px;
}
.preview-btn {
	top: 40px;
	left: 42px;
}
.file-title-list {
	width: 100%;
	height: 57px;
	display: flex;
	align-items: center;
	margin-top: 10px;
	overflow-x: scroll;
	padding: 12px 0;
}
.file-title-item {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 5px;
	border-radius: 5px;
	border: 1px solid #909399;
	margin-right: 10px;
	cursor: pointer;
}
.file-title-item:hover {
	border: 1px solid var(--el-color-theme);
	color: var(--el-color-theme);
}
.file-title-list .active {
	border: 1px solid var(--el-color-theme);
	background: var(--el-color-theme);
	color: #fff;
}
.preview-list {
	display: flex;
	flex-direction: column;
	width: 100%;
	overflow-y: scroll;
}
.preview-list::-webkit-scrollbar { /* WebKit */
	width: 0 !important;
}
.preview-item {
	background: #f5f6f7;;
	margin-top: 10px;
	width: 100%;
	min-height: 200px;
	border-radius: 5px;
	color: #303133;
	padding: 10px 20px;
	cursor: pointer;
}
.preview-item .label {
	font-size: 13px;
	font-weight: bold;
	color: var(--el-color-theme);
	margin-bottom: 5px;
}
.label-num {
	margin-top: 10px;
	color: #909399;
}
.too-bar {
	width: 100%;
	margin-right: 10px;
	height: 20px;
	display: flex;
	justify-content: space-between;
}
.item-count {
	margin-top: 5px;
}
.item-no {
	font-weight: bold;
	font-size: 13px;
}
.content-body {
	font-size: 14px;
	font-weight: 400;
	line-height: 23px;
}
</style>
