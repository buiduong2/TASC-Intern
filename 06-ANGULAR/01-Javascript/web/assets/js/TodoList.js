import { TodoItem } from './TodoItem.js';
import { runWithEffectAndAdd, runWithEffectAndDelete } from './utils.js';
export function TodoList(selectors, onDelete) {
    let count = 0;
    let countEle;
    let ele;
    let clearBtnEle;
    let todos = [];
    // init
    countEle = document.querySelector(selectors.countSelector);
    ele = document.querySelector(selectors.listSelector);
    clearBtnEle = document.querySelector(selectors.clearBtnSelector);
    setCount(0);
    // Add event
    clearBtnEle.onclick = e => clearTodo();
    //function
    function addTodo(text) {
        const item = TodoItem(text, onItemRemove);
        todos.push(item);
        ele.appendChild(item.ele);
        updateCountByDelta(1);
        runWithEffectAndAdd(item.ele);
    }
    function filterTodo(text) {
        const matchesItems = todos.filter(todo => todo.getContent().includes(text));
        setCount(matchesItems.length);
        ele.innerHTML = '';
        for (const item of matchesItems) {
            ele.appendChild(item.ele);
        }
    }
    function clearTodo() {
        for (const todo of todos) {
            runWithEffectAndDelete(todo.ele, todo.remove);
        }
    }
    // function
    function onItemRemove(id) {
        updateCountByDelta(-1);
        todos = todos.filter(todo => todo.id != id);
        onDelete();
    }
    function updateCountByDelta(delta) {
        count += delta;
        countEle.textContent = String(count);
    }
    function setCount(num) {
        count = num;
        countEle.textContent = String(count);
    }
    return {
        addTodo,
        filterTodo,
        getTodos: () => todos
    };
}
