import { type TodoListSelector, TodoList } from './TodoList.js'
import {
	generatedId,
	runWithEffectAndAdd,
	runWithEffectAndDelete
} from './utils.js'




type TodoData = {
	id: number
	content: string
}
type TodoAddSelectors = {
	inputSelector: string
	btnSelector: string
}

type Selectors = {
	add: TodoAddSelectors
	list: TodoListSelector
}

App({
	add: {
		inputSelector: '.todo-add-input',
		btnSelector: '.todo-add-btn'
	},
	list: {
		clearBtnSelector: '.todo-clear-btn',
		countSelector: '.todo-count',
		listSelector: '.todo-list'
	}
})

function App(selectors: Selectors): void {
	TodoAdd({ ...selectors.add }, onSubmitAddTodo)
	const todoList = TodoList({ ...selectors.list })

	function onSubmitAddTodo(newContent: string): void {
		todoList.addTodo(newContent)
	}

	function onSubmitSearchTodo(search: string): void {
		todoList.filterTodo(search)
	}
}

function TodoAdd(
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
