export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

export interface SortConfig<T> {
  field: keyof T;
  order: 'asc' | 'desc';
}

export interface Pageable<T> {
  page: number;
  size: number;
  sort: SortConfig<T>[];
}
