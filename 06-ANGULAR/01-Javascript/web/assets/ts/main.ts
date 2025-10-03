import { Draggable } from './Draggable.js'
import { TodoAdd, type TodoAddSelectors } from './TodoAdd.js'
import { type TodoListSelector, TodoList } from './TodoList.js'

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

const draggableSelector: Parameters<typeof Draggable>[0] = {
	container: {
		wrapperSelector: '.drag-wrapper'
	},

	item: {
		handleSelector: '.drag-item-handle',
		wrapperSelector: '.drag-wrapper > *'
	}
}

function App(selectors: Selectors): void {
	TodoAdd({ ...selectors.add }, onSubmitAddTodo)
	const todoList = TodoList({ ...selectors.list }, onDeleteTodo)

	function onSubmitAddTodo(newContent: string): void {
		todoList.addTodo(newContent)
		Draggable(draggableSelector)
	}

	function onSubmitSearchTodo(search: string): void {
		todoList.filterTodo(search)
		Draggable(draggableSelector)
	}

	function onDeleteTodo() {
		Draggable(draggableSelector)
	}
}
