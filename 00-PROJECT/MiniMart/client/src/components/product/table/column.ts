import DataTableColumnHeader from '@/components/datatable/DataTableColumnHeader.vue'
import type { Product } from '@/types/product'
import type { ColumnDef } from '@tanstack/vue-table'
import { Checkbox } from 'reka-ui/namespaced'
import { h } from 'vue'

export const columns: ColumnDef<Product>[] = [
  {
    id: 'select',
    header: ({ table }) =>
      h(Checkbox, {
        modelValue: table.getIsAllPageRowsSelected(),
        'onUpdate:modelValue': (value: boolean) => table.toggleAllPageRowsSelected(!!value),
        ariaLabel: 'Select all',
      }),
    cell: ({ row }) =>
      h(Checkbox, {
        modelValue: row.getIsSelected(),
        'onUpdate:modelValue': (value: boolean) => row.toggleSelected(!!value),
        ariaLabel: 'Select row',
      }),
    enableSorting: false,
    enableHiding: false,
  },
  {
    id: 'imageUrl',
    header: ({ column }) =>
      h(DataTableColumnHeader<Product>, { column: column, title: 'Hình ảnh' }),
    cell: ({ row }) => h('img', { class: 'size-20 rounded-lg', src: row.original.imageUrl }),
  },

  {
    id: 'salePrice',
    header: ({ column }) =>
      h(DataTableColumnHeader<Product>, { column: column, title: 'Giá khuyến mại' }),
    cell: ({ row }) =>
      h('div', { class: '', src: row.original.imageUrl }, row.original.salePrice || '-'),
  },

  {
    id: 'compareAtPrice',
    header: ({ column }) => h(DataTableColumnHeader<Product>, { column: column, title: 'Giá Gốc' }),
    cell: ({ row }) =>
      h('div', { class: '', src: row.original.imageUrl }, row.original.compareAtPrice || '-'),
  },

  {
    id: 'stock',
    header: ({ column }) => h(DataTableColumnHeader<Product>, { column: column, title: 'Còn' }),
    cell: ({ row }) =>
      h('div', { class: '', src: row.original.imageUrl }, row.original.stock || '-'),
  },
]
