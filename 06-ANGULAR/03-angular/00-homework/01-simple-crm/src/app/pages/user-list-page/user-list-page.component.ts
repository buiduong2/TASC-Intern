import { Component, OnInit } from '@angular/core';
import { UiTableComponent } from '../../components/app-ui/ui-table/ui-table.component';
import { UserListDetailComponent } from '../../components/user-list-detail/user-list-detail.component';

@Component({
  selector: 'user-list-page',
  templateUrl: './user-list-page.component.html',
  imports: [UiTableComponent, UserListDetailComponent],
})
export class UserListPageComponent implements OnInit {
  constructor() {}

  ngOnInit() {}
}
