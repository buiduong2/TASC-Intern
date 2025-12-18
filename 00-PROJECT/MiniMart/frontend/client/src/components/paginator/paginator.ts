import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.html',
})
export class PaginatorComponent implements OnInit {
  @Input() pageNumber!: number;
  @Input() totalPages!: number;
  @Output() pageChange = new EventEmitter<number>();

  constructor() {}

  ngOnInit() {}

  get pages() {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }

  change(p: number) {
    this.pageChange.emit(p);
  }
}
