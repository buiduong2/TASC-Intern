export type TodoAddSelectors = {
	inputSelector: string
	btnSelector: string
}

export function TodoAdd(
	selectors: TodoAddSelectors,
	onSubmit: (value: string) => void
) {
	let btnEle: HTMLButtonElement
	let inputEle: HTMLInputElement

	//init
	btnEle = document.querySelector(selectors.btnSelector)!
	inputEle = document.querySelector(selectors.inputSelector)!

	// add Event
	inputEle.addEventListener('keydown', e => {
		if (e.key == 'Enter') {
			e.preventDefault()
			submit()
		}
	})

	btnEle.onclick = e => {
		e.preventDefault()
		submit()
	}

	// Function
	function submit() {
		const content = inputEle.value.trim()

		if (content) {
			onSubmit(content)
			inputEle.value = ''
		}
	}
}
