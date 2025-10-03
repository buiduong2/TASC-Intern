import { generatedId, runWithEffectAndDelete } from './utils.js'

export type Todo = ReturnType<typeof TodoItem>
const idGenerator = generatedId()

const itemHTML = `
    <div  class="pb-2">
        <form class="relative rounded overflow-hidden group shadow ">
            <div class="relative">
               <span 
                    class="drag-item-handle cursor-grab active:cursor-grabbing
                            absolute left-0 top-1/2 -translate-y-1/2
                            flex items-center justify-center
                            w-8 h-8 text-gray-500 hover:text-gray-700"
                >
                        <i class="fas fa-grip-lines"></i>
                </span>
                <div type="text" class="w-full bg-slate-200 font-medium item-input-btn break-all pl-8"></div>
            </div>
    
            <button class="shadow item-delete-btn  absolute text-white bg-red-400 p-2 w-10 h-full flex items-center justify-center right-0 top-0
                hover:bg-red-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded"
                type="button"
                >
                <i class="fa-solid fa-trash"></i>
            </button>
        </form>
    </div>
`

export function TodoItem(value: string, onRemove: (id: number) => void) {
	const id = idGenerator.getNext()
	let text = value
	let wrapper: HTMLElement
	let deleteBtnEle: HTMLButtonElement
	let contentEle: HTMLDivElement

	//Init
	wrapper = document.createElement('div')
	wrapper.innerHTML = itemHTML
	deleteBtnEle = wrapper.querySelector('.item-delete-btn')!
	contentEle = wrapper.querySelector('.item-input-btn')!
	contentEle.textContent = value

	// Add eventListener
	deleteBtnEle.onclick = e => {
		remove()
	}

	// Function
	function remove() {
		runWithEffectAndDelete(wrapper, () => {
			wrapper.remove()
			onRemove(id)
		})
	}

	function getContent() {
		return text
	}

	return {
		id,
		getContent,
		remove,
		ele: wrapper
	}
}
