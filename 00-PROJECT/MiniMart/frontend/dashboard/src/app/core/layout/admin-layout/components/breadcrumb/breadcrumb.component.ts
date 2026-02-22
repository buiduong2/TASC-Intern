import { Component, OnInit } from '@angular/core';
import { BreadcrumbService } from '../../services/breadcrumb.service';
import { RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css'],
  imports: [RouterLink, MatIcon],
})
export class BreadcrumbComponent implements OnInit {
  constructor(readonly breadcrumbService: BreadcrumbService) {}

  get breadcrumbs() {
    return this.breadcrumbService.breadcrumbs;
  }

  isLastBreadcrumb(index: number) {
    return this.breadcrumbs.length - 1 === index;
  }

  ngOnInit() {}
}
