import { type Todo, TodoItem } from './TodoItem.js'
import { runWithEffectAndAdd, runWithEffectAndDelete } from './utils.js'

export type TodoListSelector = {
	listSelector: string
	countSelector: string
	clearBtnSelector: string
}

export function TodoList(selectors: TodoListSelector, onDelete: Function) {
	let count = 0
	let countEle: HTMLElement
	let ele: HTMLElement
	let clearBtnEle: HTMLButtonElement
	let todos: Todo[] = []

	// init
	countEle = document.querySelector(selectors.countSelector)!
	ele = document.querySelector(selectors.listSelector)!
	clearBtnEle = document.querySelector(selectors.clearBtnSelector)!
	setCount(0)

	// Add event

	clearBtnEle.onclick = e => clearTodo()
	//function

	function addTodo(text: string) {
		const item = TodoItem(text, onItemRemove)
		todos.push(item)
		ele.appendChild(item.ele)
		updateCountByDelta(1)
		runWithEffectAndAdd(item.ele)
	}

	function filterTodo(text: string) {
		const matchesItems = todos.filter(todo =>
			todo.getContent().includes(text)
		)

		setCount(matchesItems.length)
		ele.innerHTML = ''

		for (const item of matchesItems) {
			ele.appendChild(item.ele)
		}
	}

	function clearTodo() {
		for (const todo of todos) {
			runWithEffectAndDelete(todo.ele, todo.remove)
		}
	}

	// function
	function onItemRemove(id: number) {
		updateCountByDelta(-1)
		todos = todos.filter(todo => todo.id != id)
		onDelete()
	}

	function updateCountByDelta(delta: number) {
		count += delta
		countEle.textContent = String(count)
	}

	function setCount(num: number) {
		count = num
		countEle.textContent = String(count)
	}

	return {
		addTodo,
		filterTodo
	}
}
