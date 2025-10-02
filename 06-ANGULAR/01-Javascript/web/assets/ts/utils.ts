export function generatedId() {
	let id = 1

	return {
		getNext: function () {
			return id++
		}
	}
}

export function runWithEffectAndAdd(ele: HTMLElement) {
	ele.classList.add(
		'transition-opacity',
		'opacity-0',
		'duration-300',
		'ease-out'
	)

	setTimeout(() => {
		ele.classList.add('opacity-1')
		ele.classList.remove('opacity-0')
	}, 100)

	setTimeout(() => {
		ele.classList.remove(
			'transition-opacity',
			'opacity-0',
			'duration-300',
			'ease-out'
		)
	}, 400)
}

let elesToDelete: HTMLElement[] = []
export function runWithEffectAndDelete(ele: HTMLElement, deleteFun: Function) {
	if (elesToDelete.some(e => e == ele)) {
		return
	}
	elesToDelete.push(ele)
	ele.classList.add(
		'transition-opacity',
		'opacity-1',
		'duration-300',
		'ease-out'
	)

	setTimeout(() => {
		ele.classList.remove('opacity-1')
		ele.classList.add('opacity-0')
	}, 100)

	setTimeout(() => {
		elesToDelete = elesToDelete.filter(e => e != ele)
		deleteFun()
	}, 400)
}
