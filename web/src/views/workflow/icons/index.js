const icons = import.meta.glob('./**.vue', { eager: true })
export function iconComponent(name) {
	const url = `./${name}.vue`
	return icons[url]?.default || null
}
