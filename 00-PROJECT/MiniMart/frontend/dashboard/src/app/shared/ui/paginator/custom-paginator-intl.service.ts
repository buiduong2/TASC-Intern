import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { Subject } from 'rxjs';

@Injectable()
export class CustomPaginatorIntl implements MatPaginatorIntl {
  changes = new Subject<void>();

  // For internationalization, the `$localize` function from
  // the `@angular/localize` package can be used.
  firstPageLabel = `Trang đầu`;
  itemsPerPageLabel = `Số hàng trong 1 trang:`;
  lastPageLabel = `Trang cuối`;

  // You can set labels to an arbitrary string too, or dynamically compute
  // it through other third-party internationalization libraries.
  nextPageLabel = 'Trang tiếp theo';
  previousPageLabel = 'Trang trước';

  getRangeLabel(page: number, pageSize: number, length: number): string {
    if (!length || !pageSize) {
      return 'Page 0 of 0';
    }
    const totalPages = Math.ceil(length / pageSize);
    return `Page ${page + 1} of ${totalPages}`;
  }
}
