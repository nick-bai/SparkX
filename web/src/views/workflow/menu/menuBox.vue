<template>
	<div class="add-menu-box">
		<div class="menu-item flex-center" @click="$emit('addNode', 'answer')">
			<div class="menu-icon" style="background: #06ae4d;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-pinglun3-copy" style="font-size: 18px !important;"></span>
			</div>
			<div class="menu-title">回复</div>
		</div>
		<div class="menu-item flex-center" @click="$emit('addNode', 'llm')">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 2px;border-radius: 5px;">
				<span class="iconfont icon-a-zhuliudeLLM"></span>
			</div>
			<div class="menu-title">LLM</div>
		</div>
		<div class="menu-item flex-center" @click="$emit('addNode', 'agent')">
			<div class="menu-icon" style="background: #17b26a;color: #fff;padding: 2px;border-radius: 5px;">
				<span class="iconfont icon-a-agent1"></span>
			</div>
			<div class="menu-title">Agent</div>
		</div>
		<div class="menu-item flex-center" @click="$emit('addNode', 'purpose')">
			<div class="menu-icon" style="background: #f79009;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-fenlei" style="font-size: 18px !important;"></span>
			</div>
			<div class="menu-title">意图分类</div>
		</div>
		<div class="menu-item flex-center" @click="$emit('addNode', 'dataset')">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-zhishiku" style="font-size: 18px !important;"></span>
			</div>
			<div class="menu-title">知识检索</div>
		</div>
		<div class="menu-item flex-center" @click="$emit('addNode', 'switch')">
			<div class="menu-icon" style="background: #6172f3;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-fenzhi" style="font-size: 18px !important;"></span>
			</div>
			<div class="menu-title">条件分支</div>
		</div>
		<!--<div class="menu-item flex-center">
			<div class="menu-icon" style="background: #f79009;color: #fff;padding: 3px;border-radius: 5px;">
				<span class="iconfont icon-qizhi" style="font-size: 18px !important;"></span>
			</div>
			<div class="menu-title">结束</div>
		</div>-->
	</div>
</template>

<script>
export default {
	data() {
		return {

		}
	}
}
</script>

<style scoped>
.add-menu-box {
	width: 150px;
	height: 270px;
	background: #ffffff;
	display: flex;
	flex-direction: column;
	justify-content: left;
	padding: 10px 20px;
	border-radius: 5px;
}
.menu-item {
	height: 100px;
	cursor: pointer;
}
.menu-title {
	margin-left: 10px;
	font-size: 14px;
}
.menu-icon span {
	font-size: 20px;
}
</style>
