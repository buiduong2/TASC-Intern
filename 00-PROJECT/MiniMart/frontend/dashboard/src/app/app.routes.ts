import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './core/layout/admin-layout/admin-layout.component';
import { AuthLayoutComponent } from './core/layout/auth-layout/auth-layout.component';
import { ErrorLayoutComponent } from './core/layout/error-layout/error-layout.component';
import { NotFound } from './features/not-found/not-found';

export const routes: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashobard',
        pathMatch: 'full',
      },
      {
        path: 'dashobard',
        loadComponent: () => import('./features/overview/overview').then((m) => m.Overview),
      },
      {
        path: 'products',
        loadComponent: () =>
          import('./features/products/pages/product-list/product-list').then((m) => m.ProductList),
      },
      {
        path: 'products/:id/edit',
        loadComponent: () =>
          import('./features/products/pages/product-edit/product-edit').then((m) => m.ProductEdit),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./features/category-list/category-list').then((m) => m.CategoryList),
      },
      { path: '**', component: NotFound },
    ],
  },
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [],
  },
  {
    path: '**',
    component: ErrorLayoutComponent,
    children: [],
  },
];
