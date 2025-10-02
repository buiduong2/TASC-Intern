import { generatedId, runWithEffectAndDelete } from './utils.js'

export type Todo = ReturnType<typeof TodoItem>
const idGenerator = generatedId()

const itemHTML = `
    <form class="relative rounded overflow-hidden group shadow">
        <div type="text" class="w-full bg-slate-200 font-medium item-input-btn break-all" ></div>

        <button class="shadow item-delete-btn  absolute text-white bg-red-400 p-2 w-10 h-full flex items-center justify-center right-0 top-0
            hover:bg-red-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded"
            type="button"
            >
            <i class="fa-solid fa-trash"></i>
        </button>
    </form>
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
