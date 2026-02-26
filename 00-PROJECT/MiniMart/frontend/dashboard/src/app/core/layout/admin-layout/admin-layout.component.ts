import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { TopBarComponent } from './components/top-bar/top-bar.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { AdminLayoutService } from './services/admin-layout.service';

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css'],
  imports: [RouterOutlet, MatToolbarModule, MatSidenavModule, TopBarComponent, SideBarComponent],
})
export class AdminLayoutComponent implements OnInit {
  constructor(readonly layoutService: AdminLayoutService) {}

  get collapsed(): boolean {
    return this.layoutService.isCollapse();
  }

  toggleCollapse() {
    this.layoutService.toggleCollapse();
  }

  ngOnInit() {}
}
