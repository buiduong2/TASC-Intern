import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatToolbar } from '@angular/material/toolbar';
import { BreadcrumbComponent } from '../breadcrumb/breadcrumb.component';
import { UserMenuComponent } from '../user-menu/user-menu.component';
import { AdminLayoutService } from '../../services/admin-layout.service';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
  imports: [MatToolbar, MatIconButton, MatIcon, UserMenuComponent, BreadcrumbComponent],
})
export class TopBarComponent {
  constructor(readonly layoutSerice: AdminLayoutService) {}
}
