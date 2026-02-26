export interface ColumnConfig<T> {
  key: keyof T;
  label: string;
  visible: boolean;

  sortable?: boolean;
  hideable?: boolean;
  align?: 'left' | 'center' | 'right';
}

export interface MenuItem<T> {
  action: T;
  label: string;
  icon?: string;
  danger?: boolean;
}
export interface MenuGroup<T> {
  items: MenuItem<T>[];
}
