import { Draggable } from './Draggable.js'
import { TodoAdd as TodoForm, type TodoAddSelectors } from './TodoForm.js'
import { type TodoListSelector, TodoList } from './TodoList.js'

type Selectors = {
	add: TodoAddSelectors
	list: TodoListSelector
}

App({
	add: {
		inputSelector: '.todo-add-input',
		btnSelector: '.todo-add-btn',
		msgSelector: '.todo-add-msg'
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
	TodoForm({ ...selectors.add }, onSubmitAddTodo, validateAddTodo)
	TodoForm(
		{
			btnSelector: '.todo-search-btn',
			inputSelector: '.todo-search-input',
			msgSelector: '.todo-search-msg'
		},
		onSubmitSearchTodo,
		() => null
	)
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

	function validateAddTodo(text: string): string | null {
		if (todoList.getTodos().some(todo => todo.getContent() === text)) {
			return 'todo exsitsed'
		}
		return null
	}
}
