import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Breadcrumb, BreadcrumbService } from '../../services/breadcrumb.service';
import { RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { Observable } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.css'],
  imports: [RouterLink, MatIcon, AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BreadcrumbComponent {
  readonly breadcrumbs$!: Observable<Breadcrumb[]>;

  constructor(private breadcrumbService: BreadcrumbService) {
    this.breadcrumbs$ = this.breadcrumbService.breadcrumbs$;
  }
}
