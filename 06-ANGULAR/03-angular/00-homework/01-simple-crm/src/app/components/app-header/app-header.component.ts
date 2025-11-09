import { Component, OnInit } from '@angular/core';
import { LucideAngularModule, MenuIcon } from 'lucide-angular';
import { AppLayoutToggleButton } from '../../layous/app-layout/app-layout-toggle-button.component';
import { AppHeaderAuthComponent } from '../app-header-auth/app-header-auth.component';
import { AppHeaderSearchComponent } from '../app-header-search/app-header-search.component';

@Component({
  selector: 'app-header',
  templateUrl: './app-header.component.html',
  imports: [
    LucideAngularModule,
    AppHeaderSearchComponent,
    AppHeaderAuthComponent,
    AppLayoutToggleButton,
  ],
})
export class AppHeaderComponent implements OnInit {
  readonly MenuIcon = MenuIcon;

  constructor() {}

  ngOnInit() {}
}
