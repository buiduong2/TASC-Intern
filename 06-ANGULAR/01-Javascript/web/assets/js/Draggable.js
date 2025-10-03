import { swap } from './utils.js';
const DRAG_ITEM_CLASS = 'drag-item';
export function Draggable(selector) {
    let containerEle = document.querySelector(selector.container.wrapperSelector);
    const dragItemEles = Array.from(document.querySelectorAll(selector.item.wrapperSelector));
    const handleItemEles = dragItemEles.map(item => item.querySelector(selector.item.handleSelector));
    containerEle.style.position = 'relative';
    let dragItems = new Array(dragItemEles.length)
        .fill(null)
        .map((_, i) => {
        const ele = dragItemEles[i];
        const rect = ele.getBoundingClientRect();
        return {
            ele: ele,
            handle: handleItemEles[i],
            translateY: 0,
            relativeTop: rect.top,
            relativeBottom: rect.top + ele.offsetHeight,
            idx: i
        };
    });
    const topBound = Math.min(...dragItems.map(i => i.relativeTop));
    const bottomBound = Math.max(...dragItems.map(it => it.relativeBottom));
    const maxIdx = dragItems.length - 1;
    let moving = false;
    dragItems.forEach(item => {
        const { ele, handle } = item;
        ele.classList.add(DRAG_ITEM_CLASS);
        ele.style.transform = 'translateY(0px)';
        ele.style.transition = 'all 0.3s ease';
        item.translateY = 0;
        handle.onmousedown = e => {
            startDragging(ele);
        };
        handle.onmouseup = e => {
            stopDragging(ele);
        };
        ele.ondragend = e => {
            stopDragging(ele);
            // setTimeout(() => {
            // 	commitOrder()
            // }, 300)
        };
        ele.ondrag = e => {
            if (moving) {
                return;
            }
            if (e.clientY === 0)
                return;
            const cursor = e.clientY;
            if (cursor >= item.relativeTop && cursor <= item.relativeBottom) {
                return;
            }
            if (dragItems.length == 1) {
                return;
            }
            if (topBound > cursor && item.idx == 0) {
                return;
            }
            if (bottomBound < cursor && item.idx == maxIdx) {
                return;
            }
            const prevItem = dragItems[item.idx - 1];
            const nextItem = dragItems[item.idx + 1];
            const prevMid = prevItem
                ? (prevItem.relativeTop + prevItem.relativeBottom) / 2
                : -Infinity;
            const nextMid = nextItem
                ? (nextItem.relativeTop + nextItem.relativeBottom) / 2
                : Infinity;
            if (cursor < prevMid) {
                swap(dragItems, item.idx, item.idx - 1);
            }
            else if (cursor > nextMid) {
                swap(dragItems, item.idx, item.idx + 1);
            }
            moving = true;
            setTimeout(() => {
                moving = false;
            }, 200);
            const newItems = reCalculateBound();
            reTranslateY(newItems);
            translateY(newItems);
            dragItems.forEach((it, idx) => {
                const newItem = newItems[idx];
                it.idx = idx;
                it.relativeTop = newItem.relativeTop;
                it.relativeBottom = newItem.relativeBottom;
                it.translateY = newItem.translateY;
            });
        };
        ele.ondragover = e => {
            e.preventDefault();
        };
    });
    function reCalculateBound() {
        let prevBottom = topBound;
        const newItem = dragItems.map(i => (Object.assign({}, i)));
        for (let i = 0; i < newItem.length; i++) {
            const item = newItem[i];
            item.idx = i;
            item.relativeTop = prevBottom;
            item.relativeBottom = item.relativeTop + item.ele.offsetHeight;
            prevBottom = item.relativeBottom;
        }
        return newItem;
    }
    function reTranslateY(newItems) {
        const oldTopByEle = new Map();
        dragItems.forEach(it => oldTopByEle.set(it.ele, it));
        newItems.forEach(it => {
            const oldEle = oldTopByEle.get(it.ele);
            it.translateY =
                oldEle.translateY + it.relativeTop - oldEle.relativeTop;
        });
    }
    function translateY(newItems) {
        newItems.forEach(({ ele, translateY }) => {
            ele.style.transform = `translateY(${translateY}px)`;
        });
    }
    function commitOrder() {
        dragItems.forEach(item => {
            containerEle.appendChild(item.ele);
        });
        dragItems.forEach(item => {
            item.ele.style.transform = 'translateY(0px)';
            item.translateY = 0;
        });
        for (let i = 0; i < dragItems.length; i++) {
            const item = dragItems[i];
            const rect = item.ele.getBoundingClientRect();
            item.idx = i;
            item.relativeTop = rect.top;
            item.relativeBottom = rect.top + item.ele.offsetHeight;
        }
    }
    function startDragging(ele) {
        ele.draggable = true;
        ele.classList.add('dragging');
    }
    function stopDragging(ele) {
        ele.draggable = false;
        ele.classList.remove('dragging');
    }
}
