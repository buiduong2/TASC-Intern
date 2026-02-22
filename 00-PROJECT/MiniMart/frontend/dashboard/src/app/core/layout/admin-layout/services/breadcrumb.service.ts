import { Injectable } from '@angular/core';

export interface Breadcrumb {
  path: string;
  label: string;
}

@Injectable({
  providedIn: 'root',
})
export class BreadcrumbService {
  private _breadcrumbs: Breadcrumb[];
  constructor() {
    this._breadcrumbs = [
      {
        label: 'Trang Chủ',
        path: '/',
      },
      {
        label: 'Sản Phẩm',
        path: '/products',
      },
    ];
  }

  get breadcrumbs(): Breadcrumb[] {
    return this._breadcrumbs;
  }
}
