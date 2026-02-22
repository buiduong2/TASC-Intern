import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, Observable, startWith } from 'rxjs';

export interface Breadcrumb {
  path: string;
  label: string;
}
@Injectable({ providedIn: 'root' })
export class BreadcrumbService {
  readonly breadcrumbs$: Observable<Breadcrumb[]>;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.breadcrumbs$ = this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      startWith(null), // 👈 trigger lần đầu
      map(() => this.buildBreadcrumb(this.route.root)),
    );
  }

  private buildBreadcrumb(root: ActivatedRoute): Breadcrumb[] {
    const breadcrumbs: Breadcrumb[] = [];
    let url = '';

    let current = root.firstChild;

    while (current) {
      const segment = current.snapshot.url.map((s) => s.path).join('/');

      if (segment) {
        url += `/${segment}`;
      }

      const config = current.routeConfig?.data?.['breadcrumb'];

      if (config) {
        const label = typeof config === 'function' ? config(current.snapshot.data) : config;

        breadcrumbs.push({
          label,
          path: url,
        });
      }

      current = current.firstChild!;
    }

    return breadcrumbs;
  }
}
