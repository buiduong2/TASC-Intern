import { Draggable } from './Draggable.js';
import { TodoAdd as TodoForm } from './TodoForm.js';
import { TodoList } from './TodoList.js';
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
});
const draggableSelector = {
    container: {
        wrapperSelector: '.drag-wrapper'
    },
    item: {
        handleSelector: '.drag-item-handle',
        wrapperSelector: '.drag-wrapper > *'
    }
};
function App(selectors) {
    TodoForm(Object.assign({}, selectors.add), onSubmitAddTodo, validateAddTodo);
    TodoForm({
        btnSelector: '.todo-search-btn',
        inputSelector: '.todo-search-input',
        msgSelector: '.todo-search-msg'
    }, onSubmitSearchTodo, () => null);
    const todoList = TodoList(Object.assign({}, selectors.list), onDeleteTodo);
    function onSubmitAddTodo(newContent) {
        todoList.addTodo(newContent);
        Draggable(draggableSelector);
    }
    function onSubmitSearchTodo(search) {
        todoList.filterTodo(search);
        Draggable(draggableSelector);
    }
    function onDeleteTodo() {
        Draggable(draggableSelector);
    }
    function validateAddTodo(text) {
        if (!text || !text.trim()) {
            return 'todo must not be null nor empty';
        }
        if (todoList.getTodos().some(todo => todo.getContent() === text)) {
            return 'todo exsitsed';
        }
        return null;
    }
}
