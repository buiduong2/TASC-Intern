import { Routes } from '@angular/router';
import { NotFound } from './pages/not-found/not-found';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/overview/overview').then((m) => m.Overview),
  },
  {
    path: 'products',
    loadComponent: () => import('./pages/product-list/product-list').then((m) => m.ProductList),
  },
  {
    path: 'products/:id/edit',
    loadComponent: () => import('./pages/product-edit/product-edit').then((m) => m.ProductEdit),
  },
  {
    path: 'categories',
    loadComponent: () => import('./pages/category-list/category-list').then((m) => m.CategoryList),
  },
  {
    path: '**',
    component: NotFound,
  },
];
