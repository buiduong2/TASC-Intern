import { Component, OnInit } from '@angular/core';
import { MatAnchor, MatIconButton } from '@angular/material/button';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { UserMenuComponent } from '../user-menu/user-menu.component';
import { BreadcrumbComponent } from '../breadcrumb/breadcrumb.component';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
  imports: [MatToolbar, MatIconButton, MatIcon, UserMenuComponent, BreadcrumbComponent],
})
export class TopBarComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
