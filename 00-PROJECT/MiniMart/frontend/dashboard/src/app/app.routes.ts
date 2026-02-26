import { Data, Route } from '@angular/router';
import { AdminLayoutComponent } from './core/layout/admin-layout/admin-layout.component';
import { AuthLayoutComponent } from './core/layout/auth-layout/auth-layout.component';
import { ErrorLayoutComponent } from './core/layout/error-layout/error-layout.component';
import { NotFound } from './features/not-found/not-found';
import { productDetailResolver } from '@products/resolvers/product-detail.resolver';
type BreadcrumbFn = (data: any) => string;

interface AppRoute extends Route {
  data?: Data & {
    breadcrumb?: string | BreadcrumbFn;
  };

  children?: AppRoute[];
}

export const routes: AppRoute[] = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/overview/overview').then((m) => m.Overview),
        data: {
          breadcrumb: 'Bảng điều khiển',
        },
      },
      {
        path: 'products',
        data: {
          breadcrumb: 'Sản phẩm',
        },
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/products/pages/product-list/product-list').then(
                (m) => m.ProductList,
              ),
          },
          {
            path: ':id',
            resolve: {
              product: productDetailResolver,
            },
            children: [
              {
                path: 'edit',
                loadComponent: () =>
                  import('./features/products/pages/product-edit/product-edit').then(
                    (m) => m.ProductEdit,
                  ),
                data: {
                  breadcrumb: (data: any) => `Chỉnh sửa ${data.product.id}`,
                },
              },
              {
                path: 'detail',
                loadComponent: () =>
                  import('./features/products/pages/product-edit/product-edit').then(
                    (m) => m.ProductEdit, // TODO: new Page Component
                  ),
                data: {
                  breadcrumb: (data: any) => `Chi tiết ${data.product.id}`,
                },
              },
            ],
          },
        ],
      },
      {
        path: 'categories',
        data: {
          breadcrumb: 'Danh Mục',
        },
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/category-list/category-list').then((m) => m.CategoryList),
          },
        ],
      },

      {
        path: 'auth',
        data: {
          breadcrumb: 'Tài khoản',
        },
        children: [
          {
            path: '',
            redirectTo: 'me',
            pathMatch: 'full',
          },
          {
            path: 'me',
            loadComponent: () =>
              import('./features/auth/pages/auth-profile/auth-profile.component').then(
                (m) => m.AuthProfileComponent,
              ),
            data: {
              breadcrumb: 'Hồ sơ',
            },
          },
          {
            path: 'change-password',
            loadComponent: () =>
              import('./features/auth/pages/auth-change-password/auth-change-password.component').then(
                (m) => m.AuthChangePasswordComponent,
              ),
            data: {
              breadcrumb: 'Đổi mật khẩu',
            },
          },
        ],
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
