import { Routes } from '@angular/router';
import { CategoryPage } from '../pages/category-page/category-page';
import { HomePage } from '../pages/home-page/home-page';
import { ProductDetailPage } from '../pages/product-detail-page/product-detail-page';
import { ProductListPage } from '../pages/product-list-page/product-list-page';

export const routes: Routes = [
  { path: '', title: 'App Home Page', component: HomePage },
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
    path: 'login',
    title: 'Login',
    loadComponent: () => import('../pages/login-page/login-page').then((m) => m.LoginPage),
  },
  {
    path: 'register',
    title: 'Register',
    loadComponent: () => import('../pages/register-page/register-page').then((m) => m.RegisterPage),
  },
  {
    path: 'about',
    title: 'About',
    loadComponent: () => import('../pages/about-page/about-page').then((m) => m.AboutPage),
  },
];
