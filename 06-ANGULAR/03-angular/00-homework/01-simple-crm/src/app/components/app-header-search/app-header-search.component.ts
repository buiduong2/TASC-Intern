import { Component, OnInit } from '@angular/core';
import { LucideAngularModule, SearchIcon } from 'lucide-angular';

@Component({
  selector: 'app-header-search',
  templateUrl: './app-header-search.component.html',
  imports: [LucideAngularModule],
})
export class AppHeaderSearchComponent implements OnInit {
  readonly SearchIcon = SearchIcon;

  constructor() {}

  ngOnInit() {}
}
