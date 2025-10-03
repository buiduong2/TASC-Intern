export function generatedId() {
    let id = 1;
    return {
        getNext: function () {
            return id++;
        }
    };
}
export function runWithEffectAndAdd(ele) {
    ele.classList.add('transition-opacity', 'opacity-0', 'duration-300', 'ease-out');
    setTimeout(() => {
        ele.classList.add('opacity-1');
        ele.classList.remove('opacity-0');
    }, 100);
    setTimeout(() => {
        ele.classList.remove('transition-opacity', 'opacity-0', 'duration-300', 'ease-out');
    }, 400);
}
let elesToDelete = [];
export function runWithEffectAndDelete(ele, deleteFun) {
    if (elesToDelete.some(e => e == ele)) {
        return;
    }
    elesToDelete.push(ele);
    ele.classList.add('transition-opacity', 'opacity-1', 'duration-300', 'ease-out');
    setTimeout(() => {
        ele.classList.remove('opacity-1');
        ele.classList.add('opacity-0');
    }, 100);
    setTimeout(() => {
        elesToDelete = elesToDelete.filter(e => e != ele);
        deleteFun();
    }, 400);
}
export function swap(arr, i, j) {
    if (i === j)
        return;
    const tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
}
