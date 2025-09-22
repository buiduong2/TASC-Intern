import type { Purchase } from '@/types/purchase'
const URL = 'http://localhost:8081/api/purchases'

export async function fetchPurchases(page = 0): Promise<Purchase[]> {
  const res = await fetch(URL + '?page=' + page)

  if (res.ok) {
    const result: Purchase[] = await res.json()
    return result
  }

  throw new Error('error when fetch')
}
