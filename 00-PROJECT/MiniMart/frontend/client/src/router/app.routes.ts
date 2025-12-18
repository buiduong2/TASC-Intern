import { Routes } from '@angular/router';
import { CategoryPage } from '../pages/category-page/category-page';
import { HomePage } from '../pages/home-page/home-page';
import { ProductDetailPage } from '../pages/product-detail-page/product-detail-page';
import { ProductListPage } from '../pages/product-list-page/product-list-page';
import { authGuard } from './auth.guard';

export const routes: Routes = [
  {
    path: '',
    title: 'App Home Page',
    component: HomePage,
  },
  {
    path: 'category',
    title: 'Category',
    component: CategoryPage,
  },
  {
    path: 'category/:id',
    title: 'Product',
    component: ProductListPage,
  },
  {
    path: 'product/:id',
    title: 'Product Detail',
    component: ProductDetailPage,
  },
  {
    path: 'account',
    title: 'Account',
    loadComponent: () => import('../pages/account-page/account-page').then((m) => m.AccountPage),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('../pages/account-profile-page/account-profile-page').then(
            (m) => m.AccountProfilePage,
          ),
      },
      {
        path: 'address',
        loadComponent: () =>
          import('../pages/account-address-page/account-address-page').then(
            (m) => m.AccountAddressPage,
          ),
      },
      {
        path: 'password',
        loadComponent: () =>
          import('../pages/account-password-page/account-password-page').then(
            (m) => m.AccountPasswordPage,
          ),
      },
    ],
  },
  {
    path: 'login',
    title: 'Login',
    loadComponent: () => import('../pages/login-page/login-page').then((m) => m.LoginPage),
    canActivate: [authGuard(false)],
  },
  {
    path: 'register',
    title: 'Register',
    loadComponent: () => import('../pages/register-page/register-page').then((m) => m.RegisterPage),
    canActivate: [authGuard(false)],
  },
  {
    path: 'about',
    title: 'About',
    loadComponent: () => import('../pages/about-page/about-page').then((m) => m.AboutPage),
  },

  {
    path: 'callback',
    title: 'Callback',
    loadComponent: () => import('../pages/callback-page/callback-page').then((m) => m.CallbackPage),
  },
];
