import { Draggable } from './Draggable.js';
import { TodoAdd } from './TodoAdd.js';
import { TodoList } from './TodoList.js';
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
    TodoAdd(Object.assign({}, selectors.add), onSubmitAddTodo);
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
}
