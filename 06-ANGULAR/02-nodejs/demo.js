"use strict";
class Book {
    constructor(id, name, price, author) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.author = author;
    }
    toString() {
        return JSON.stringify(this);
    }
}
