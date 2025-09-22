export type Purchase = {
  id: number
  createAt: string
  supplier: string
  totalQuantity: number
  totalCostPrice: number
}

export type PurchaseCreate = {
  supplier: string
  items: PurchaseItemCreate[]
}

export type PurchaseItemCreate = {
  productId: number
  quantity: number
  costPrice: number
}
