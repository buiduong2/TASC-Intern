import { Component, signal } from '@angular/core';
import { AppHeaderComponent } from './components/app-header/app-header.component';
import { AppSidebarComponent } from './components/app-sidebar/app-sidebar.component';
import { AppLayoutComponent } from './layous/app-layout/app-layout.component';
import { UserListPageComponent } from './pages/user-list-page/user-list-page.component';

@Component({
  selector: 'app-root',
  imports: [AppHeaderComponent, AppSidebarComponent, AppLayoutComponent, UserListPageComponent],
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('01-simple-crm');
}
