export default {
	beforeMount(el, binding) {
		// 定义点击事件处理函数
		el.clickOutsideEvent = event => {
			// 检查点击是否发生在元素外部
			if (!(el === event.target || el.contains(event.target))) {
				// 调用绑定的方法
				binding.value(event);
			}
		};

		// 添加事件监听（使用捕获阶段确保先执行）
		document.addEventListener('click', el.clickOutsideEvent, true);
	},
	unmounted(el) {
		// 组件卸载时移除事件监听
		document.removeEventListener('click', el.clickOutsideEvent, true);
	}
};
