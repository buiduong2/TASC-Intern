import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'students',
    loadComponent: () => import('./pages/students/students').then((m) => m.StudentPage),
  },

  {
    path: 'students/:id/edit',
    loadComponent: () => import('./pages/students/student-detail').then((m) => m.StudentDetail),
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin/admin').then((m) => m.AdminPage),
    canActivate: [
      (route, state) => {
        const isLoggedIn = JSON.parse(localStorage.getItem('isLogged') || 'null'); // giả lập chưa đăng nhập

        if (!isLoggedIn) {
          window.confirm('Bạn cần đăng nhập để vào trang này!');
          return false;
        }
        return true;
      },
    ],
  },
  {
    path: 'not-found',
    loadComponent: () => import('./notfound').then((m) => m.NotFound),
  },

  {
    path: '**',
    loadComponent: () => import('./notfound').then((m) => m.NotFound),
  },
];
