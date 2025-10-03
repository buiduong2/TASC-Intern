export type TodoAddSelectors = {
	inputSelector: string
	btnSelector: string
	msgSelector: string
}

export function TodoAdd(
	selectors: TodoAddSelectors,
	onSubmit: (value: string) => void,
	validate: (value: string) => string | null
) {
	let btnEle: HTMLButtonElement
	let inputEle: HTMLInputElement
	let messageEle: HTMLDivElement

	let valid = true

	//init
	btnEle = document.querySelector(selectors.btnSelector)!
	inputEle = document.querySelector(selectors.inputSelector)!
	messageEle = document.querySelector(selectors.msgSelector)!

	messageEle.style.display = 'none'

	// add Event
	inputEle.addEventListener('keydown', e => {
		if (e.key == 'Enter') {
			e.preventDefault()
			submit()
			return
		}
		if (!valid) {
			valid = true
			messageEle.style.display = 'none'
		}
	})

	btnEle.onclick = e => {
		e.preventDefault()
		submit()
	}

	// Function
	function submit() {
		const content = inputEle.value.trim()
		const validateMsg = validate(content)

		if (validateMsg) {
			valid = false
			messageEle.style.display = 'block'
			messageEle.textContent = validateMsg
		}

		if (validateMsg === null) {
			onSubmit(content)
			inputEle.value = ''
		}
	}
}
