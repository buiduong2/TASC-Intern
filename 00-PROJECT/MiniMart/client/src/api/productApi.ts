import type { Product } from '@/types/product'

const URL = 'http://localhost:8081/api/products'

export async function fetchProducts(page = 0): Promise<Product[]> {
  const res = await fetch(URL + '?page=' + page)

  if (res.ok) {
    const result: Product[] = await res.json()
    return result
  }

  throw new Error('error when fetch')
}
