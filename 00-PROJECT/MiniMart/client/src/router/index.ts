import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

export const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomeView,
  },
  {
    path: '/product',
    name: 'ProductTable',
    component: () => import('../views/ProductTableView.vue'),
  },

  {
    path: '/pruchase',
    name: 'PurchaseTable',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/PurchaseTableView.vue'),
  },
] as const

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
})

export type RouterName = (typeof routes)[number]['name']

export default router
