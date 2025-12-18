interface Product {
	id: number
	name: string
	price: number
}

class Book implements Product {
	id: number
	name: string
	price: number
	author: string

	constructor(id: number, name: string, price: number, author: string) {
		this.id = id
		this.name = name
		this.price = price
		this.author = author
	}

	toString(): string {
		return JSON.stringify(this)
	}

	print(): void {
		console.log(this.toString())
	}
}

const book = new Book(1, 'book', 1000, 'Duong')
book.print()
