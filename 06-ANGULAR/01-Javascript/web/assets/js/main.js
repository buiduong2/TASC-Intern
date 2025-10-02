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
function App(selectors) {
    TodoAdd(Object.assign({}, selectors.add), onSubmitAddTodo);
    const todoList = TodoList(Object.assign({}, selectors.list));
    function onSubmitAddTodo(newContent) {
        todoList.addTodo(newContent);
    }
    function onSubmitSearchTodo(search) {
        todoList.filterTodo(search);
    }
}
function TodoAdd(selectors, onSubmit) {
    let btnEle;
    let inputEle;
    //init
    btnEle = document.querySelector(selectors.btnSelector);
    inputEle = document.querySelector(selectors.inputSelector);
    // add Event
    inputEle.addEventListener('keydown', e => {
        if (e.key == 'Enter') {
            e.preventDefault();
            submit();
        }
    });
    btnEle.onclick = e => {
        e.preventDefault();
        submit();
    };
    // Function
    function submit() {
        const content = inputEle.value.trim();
        if (content) {
            onSubmit(content);
            inputEle.value = '';
        }
    }
}
