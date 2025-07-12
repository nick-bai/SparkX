/**
 * @description 自动import导入所有 api 模块
 */
const files = import.meta.glob('./model/*.js', {eager: true})
const modules = {}
Object.keys(files).forEach((key) => {
	modules[key.replace(/(\.\/|\.js)/g, '').replace('model/', '')] = files[key].default
})

export default modules
