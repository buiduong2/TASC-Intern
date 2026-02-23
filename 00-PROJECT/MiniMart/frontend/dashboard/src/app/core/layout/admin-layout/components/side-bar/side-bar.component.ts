import { ConnectedPosition, OverlayModule } from '@angular/cdk/overlay';
import { Component, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter, startWith } from 'rxjs';
import { AdminLayoutService } from '../../services/admin-layout.service';

export const SIDEBAR_ITEMS: SideBarItem[] = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/dashboard',
  },

  // ================= CATALOG =================
  {
    id: 'catalog',
    label: 'Catalog',
    icon: 'inventory_2',
    children: [
      {
        id: 'catalog-products',
        label: 'Products',
        icon: 'shopping_bag',
        route: '/products',
      },
      {
        id: 'catalog-categories',
        label: 'Categories',
        icon: 'category',
        route: '/categories',
      },
      {
        id: 'catalog-tags',
        label: 'Tags',
        icon: 'label',
        route: '/tags',
      },
    ],
  },

  // ================= OPERATIONS =================
  {
    id: 'operations',
    label: 'Operations',
    icon: 'settings',
    children: [
      {
        id: 'operations-orders',
        label: 'Orders',
        icon: 'receipt_long',
        route: '/orders',
      },
      {
        id: 'operations-inventory',
        label: 'Inventory',
        icon: 'warehouse',
        route: '/inventory',
      },
      {
        id: 'operations-payments',
        label: 'Payments',
        icon: 'payments',
        route: '/payments',
      },
      {
        id: 'operations-shipments',
        label: 'Shipments',
        icon: 'local_shipping',
        route: '/shipments',
      },
    ],
  },

  // ================= ACCESS CONTROL =================
  {
    id: 'access-control',
    label: 'Access Control',
    icon: 'admin_panel_settings',
    children: [
      {
        id: 'access-users',
        label: 'Users',
        icon: 'person',
        route: '/users',
      },
      {
        id: 'access-roles',
        label: 'Roles',
        icon: 'security',
        route: '/roles',
      },
      {
        id: 'access-permissions',
        label: 'Permissions',
        icon: 'lock',
        route: '/permissions',
      },
    ],
  },

  // ================= ANALYTICS =================
  {
    id: 'analytics',
    label: 'Analytics',
    icon: 'insights',
    children: [
      {
        id: 'analytics-revenue',
        label: 'Revenue Report',
        icon: 'bar_chart',
        route: '/reporting/revenue',
      },
      {
        id: 'analytics-orders',
        label: 'Orders Report',
        icon: 'query_stats',
        route: '/reporting/orders',
      },
      {
        id: 'analytics-inventory',
        label: 'Inventory Turnover',
        icon: 'stacked_line_chart',
        route: '/reporting/inventory',
      },
    ],
  },

  // ================= SYSTEM =================
  {
    id: 'system',
    label: 'System',
    icon: 'settings_applications',
    children: [
      {
        id: 'system-saga-monitor',
        label: 'Saga Monitor',
        icon: 'bug_report',
        route: '/saga-monitor',
      },
      {
        id: 'system-logs',
        label: 'Event Logs',
        icon: 'history',
        route: '/logs',
      },
      {
        id: 'system-audit',
        label: 'Audit Trail',
        icon: 'fact_check',
        route: '/audit',
      },
    ],
  },

  {
    id: 'settings',
    label: 'Settings',
    icon: 'settings',
    route: '/settings',
  },
];

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
  imports: [MatListModule, MatIcon, RouterLink, RouterLinkActive, MatMenuModule, OverlayModule],
})
export class SideBarComponent implements OnInit, OnChanges {
  readonly sidebarItems = SIDEBAR_ITEMS;
  readonly openGroups = new Set<string>();
  activeGroupId: string | null = null;

  constructor(
    readonly router: Router,
    readonly layoutService: AdminLayoutService,
  ) {}

  get collapsed() {
    return this.layoutService.isCollapse();
  }

  ngOnInit() {
    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        startWith(null),
      )
      .subscribe(() => {
        const url = this.router.url;

        // update activeGroupId

        this.sidebarItems.forEach((item) => {
          if (item.route) {
            if (url.startsWith(item.route)) {
              this.activeGroupId = item.id;
            }
          } else {
            if (item.children?.some((child) => url.startsWith(child.route!))) {
              this.openGroups.add(item.id);
              this.activeGroupId = item.id;
            }
          }
        });

        console.log(this.activeGroupId);
      });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.collapsed) {
      this.openGroups.clear();
    }
  }

  isOpen(groupId: string) {
    return this.openGroups.has(groupId);
  }

  toggleGroup(groupId: string) {
    if (this.collapsed) {
      return;
    }

    if (this.isOpen(groupId)) {
      this.openGroups.delete(groupId);
    } else {
      this.openGroups.add(groupId);
    }
  }

  hoverId: string | null = null;

  positions: ConnectedPosition[] = [
    {
      originX: 'end',
      originY: 'top',
      overlayX: 'start',
      overlayY: 'top',
      offsetX: 0,
    },
  ];

  onGroupClick(sidebar: SideBarItem) {
    if (!this.collapsed) {
      this.toggleGroup(sidebar.id);
    }
  }

  onHover(sidebar: SideBarItem) {
    if (this.collapsed) {
      this.hoverId = sidebar.id;
    }
  }

  onLeave() {
    this.hoverId = null;
  }
}
