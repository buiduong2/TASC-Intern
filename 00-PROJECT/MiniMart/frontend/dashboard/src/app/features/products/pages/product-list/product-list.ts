import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginator, MatPaginatorIntl } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { appendSort, parseSortParams, removeSort } from '../../services/product-sort.util';
import { ProductService } from '../../services/product.service';
import { Page, Pageable, SortConfig } from '../../../../shared/models/page.model';
import { ProductQuery } from '../../models/product-query.model';
import { SortIndicator } from '../../../../shared/ui/sort-indicator/sort-indicator';
import { NgFor, NgIf } from '@angular/common';
import { CustomPaginatorIntl } from '../../../../shared/ui/paginator/custom-paginator-intl.service';
import { MenuGroup } from '../../../../shared/models/table.model';
import { ConfirmDialog } from '../../../../shared/ui/dialog/confirm-dialog/confirm-dialog';

const statusConfig: Record<Product['status'], { label: string; color: 'primary' | 'warn' }> = {
  ACTIVE: {
    label: 'Hoạt động',
    color: 'primary',
  },
  INACTIVE: {
    label: 'Không hoạt động',
    color: 'warn',
  },
};

const ROW_ACTION = ['EDIT', 'DELETE'] as const;
const HEADER_ACTION = ['SORT_ASC', 'SORT_DESC', 'CLEAR_SORT', 'HIDE'] as const;

@Component({
  selector: 'app-product-list',
  imports: [
    MatTableModule,
    MatCheckbox,
    MatChipsModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatPaginator,
    SortIndicator,
    NgFor,
    NgIf,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: CustomPaginatorIntl }],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements AfterViewInit, OnInit {
  products!: Product[];
  displayedColumns: string[];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  initialSelection: string[];
  selection: SelectionModel<string>;
  statusConfig = statusConfig;
  activeColId!: keyof Product;
  activeRowId!: Product['id'];
  readonly SORT_MAPPING = {
    id: 'id',
    name: 'name',
    price: 'price',
    status: 'status',
  } as const;

  rowActionGroups: MenuGroup<(typeof ROW_ACTION)[number]>[] = [
    {
      items: [
        {
          action: 'EDIT',
          label: 'Chỉnh sửa',
          icon: 'edit_note',
        },
      ],
    },
    {
      items: [
        {
          action: 'DELETE',
          label: 'Xóa',
          icon: 'delete',
          danger: true,
        },
      ],
    },
  ];

  colActionGroups: MenuGroup<(typeof HEADER_ACTION)[number]>[] = [
    {
      items: [
        {
          action: 'SORT_ASC',
          label: 'Sắp xếp tăng dần',
          icon: 'arrow_upward',
        },
        {
          action: 'SORT_DESC',
          label: 'Sắp xếp giảm dần',
          icon: 'arrow_downward',
        },
        {
          action: 'CLEAR_SORT',
          label: 'Bỏ sắp xếp',
          icon: 'close',
        },
      ],
    },
    {
      items: [
        {
          action: 'HIDE',
          label: 'Ẩn cột',
          icon: 'visibility_off',
        },
      ],
    },
  ];

  page!: Page<void>;
  queryParam!: ProductQuery;
  pageable!: Pageable<Product>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    private productService: ProductService,
  ) {
    this.displayedColumns = ['select', 'id', 'name', 'price', 'status', 'action'];
    this.initialSelection = [];
    const allowMultiSelect = true;
    this.selection = new SelectionModel(allowMultiSelect, this.initialSelection)!;
  }

  ngAfterViewInit(): void {
    this.paginator.length = this.page.totalElements;
    this.paginator.pageIndex = this.page.number;
    this.paginator.pageSize = this.page.size;

    this.paginator.page.subscribe((event) => {
      this.router.navigate([], {
        queryParams: {
          page: event.pageIndex,
          size: event.pageSize,
        },
        queryParamsHandling: 'merge',
      });
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.pageable = this.mapPageable(params);
      this.queryParam = this.mapQueryParams(params);
      this.loadData();
    });
  }

  getStatusLabel(status: Product['status']): string {
    return statusConfig[status].label;
  }
  // DATA
  loadData() {
    this.productService.findAll(this.queryParam, this.pageable).subscribe((page) => {
      this.products = page.content;

      this.pageable = {
        page: page.number,
        size: page.size,
        sort: this.pageable.sort,
      };

      this.page = {
        ...page,
        content: [],
      };
    });
  }

  private mapQueryParams(params: Params): ProductQuery {
    return {
      search: params['search'] ?? undefined,
      status:
        params['status'] === 'ACTIVE' || params['status'] === 'INACTIVE'
          ? params['status']
          : undefined,
    };
  }

  private mapPageable(params: Params): Pageable<Product> {
    let sort: string[];

    if (!params['sort']) {
      sort = [];
    } else {
      if (typeof params['sort'] === 'string') {
        sort = [params['sort']];
      } else {
        sort = params['sort'];
      }
    }

    const sortParam: SortConfig<Product>[] = sort
      .map((s) => s.split(','))
      .map(
        ([field, order]) =>
          ({
            field,
            order,
          }) as SortConfig<Product>,
      );

    return {
      page: Number(params['page'] ?? 0),
      size: Number(params['size'] ?? 5),
      sort: sortParam,
    };
  }

  // Selection BEGIN
  get allSelected(): boolean {
    return this.products.every((p) => this.selection.isSelected(p.id));
  }

  toggleAllRows() {
    if (this.allSelected) {
      this.products.forEach((p) => this.selection.deselect(p.id));
    } else {
      this.products.forEach((p) => this.selection.select(p.id));
    }
  }

  // Selection END
  // Sort
  isSorted(column: keyof typeof this.SORT_MAPPING): boolean {
    const sortColumn = this.SORT_MAPPING[column];
    return this.pageable.sort.some((s) => s.field === sortColumn);
  }
  getSortIndex(column: keyof typeof this.SORT_MAPPING): number {
    const sortColumn = this.SORT_MAPPING[column];
    return this.pageable.sort.findIndex((s) => s.field === sortColumn) + 1;
  }

  getSortDirection(column: keyof typeof this.SORT_MAPPING): 'asc' | 'desc' | undefined {
    const sortColumn = this.SORT_MAPPING[column];

    return this.pageable.sort.find((s) => s.field === sortColumn)?.order;
  }

  // HEADER MENU ACTION
  onHeaderAction(action: (typeof HEADER_ACTION)[number], columnId: keyof Product) {
    switch (action) {
      case 'SORT_ASC':
        this.onSort('asc', columnId);
        break;
      case 'SORT_DESC':
        this.onSort('desc', columnId);
        break;
      case 'CLEAR_SORT':
        this.onClearSort(columnId);
        break;
      case 'HIDE':
        this.onColHide(columnId);
        break;
    }
  }

  onSort(dir: SortConfig<Product>['order'], col: keyof Product) {
    const currentSort = parseSortParams(this.route.snapshot.queryParams['sort']);
    const nextSort = appendSort(currentSort, col as string, dir);
    this.router.navigate(['/products'], {
      queryParams: {
        ...this.route.snapshot.queryParams,
        sort: nextSort,
      },
    });
  }

  onClearSort(col: keyof Product) {
    const currentSort = parseSortParams(this.route.snapshot.queryParams['sort']);
    const nextSort = removeSort(currentSort, col);
    this.router.navigate(['/products'], {
      queryParams: {
        ...this.route.snapshot.queryParams,
        sort: nextSort,
      },
    });
  }

  onColHide(col: keyof Product) {
    console.log('Hide', col);
  }

  // ACTIVE MENU STATE
  openRowMenu(id: typeof this.activeRowId) {
    this.activeRowId = id;
  }

  openColMenu(id: typeof this.activeColId) {
    this.activeColId = id;
  }

  // ROW ACTION MENU
  onRowAction(action: (typeof ROW_ACTION)[number], id: string) {
    switch (action) {
      case 'EDIT':
        this.onEdit(id);
        break;
      case 'DELETE':
        this.onDelete(id);
        break;
    }
  }

  onEdit(id: string) {
    this.router.navigate(['/products', id, 'edit']);
  }

  onDelete(id: string) {
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Xóa sản phẩm',
        description: 'Hành động này không thể hoàn tác.',
        confirmText: 'Xóa',
        cancelText: 'Hủy',
      },
    });

    ref.afterClosed().subscribe((result) => {
      if (result === true) {
        console.log('DELETE TODO', id);
      }
    });
  }
}
