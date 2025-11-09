import { Component, inject, OnInit, signal } from '@angular/core';
import {
  CalendarCheckIcon,
  ChartColumnBigIcon,
  ChevronDownIcon,
  ChevronUpIcon,
  LucideAngularModule,
  LucideIconData,
  UserIcon,
} from 'lucide-angular';
import { AppLayoutShowOnCompact } from '../../layous/app-layout/app-layout-show-on-compact.component';
import { ApplayoutShowOnExpanded } from '../../layous/app-layout/app-layout-show-on-expanded.component';
import { AppLayoutStateService } from '../../layous/app-layout/app-layout-state.service';

type NavItem = {
  id: number;
  title: string;
  icon: LucideIconData;
  link: string;
  items: { id: number; label: string; link: string }[];
};

@Component({
  selector: 'app-sidebar',
  templateUrl: './app-sidebar.component.html',
  styles: `
    :host {
      display: block;
    }
  `,
  imports: [LucideAngularModule, ApplayoutShowOnExpanded, AppLayoutShowOnCompact],
})
export class AppSidebarComponent implements OnInit {
  readonly UserIcon = UserIcon;
  readonly ChevronDownIcon = ChevronDownIcon;
  readonly ChevronUpIcon = ChevronUpIcon;
  navItems: NavItem[] = [];
  openGroup = signal(new Set<NavItem['id']>());
  layoutService = inject(AppLayoutStateService);

  constructor() {
    this.navItems = [
      {
        id: 1,
        icon: UserIcon,
        link: '#',
        title: 'CRM',
        items: [
          {
            id: 1,
            label: 'Contact List',
            link: '#vip',
          },
          {
            id: 2,
            label: 'Contact Details',
            link: '#',
          },
        ],
      },
      {
        id: 2,
        icon: CalendarCheckIcon,
        link: '#',
        title: 'Plainning',
        items: [
          {
            id: 1,
            label: 'Task List',
            link: '#',
          },
          {
            id: 2,
            label: 'Task Details',
            link: '#',
          },
        ],
      },
      {
        id: 3,
        icon: ChartColumnBigIcon,
        link: '#',
        title: 'Analytics',
        items: [
          {
            id: 1,
            label: 'Dardboard',
            link: '#',
          },
          {
            id: 2,
            label: 'Sales Report',
            link: '#',
          },
          {
            id: 3,
            label: 'Geography',
            link: '#',
          },
        ],
      },
    ];
  }

  toggleGroup(id: number): void {
    this.openGroup.update((g) => {
      const newGroup = new Set(g);
      if (newGroup.has(id)) {
        newGroup.delete(id);
      } else {
        newGroup.add(id);
      }
      return newGroup;
    });
  }

  toggleGroupAndExpandLayout(id: number) {
    this.layoutService.setExpanded();
    this.openGroup.update((g) => {
      const newGroup = new Set(g);
      newGroup.add(id);
      return newGroup;
    });
  }

  ngOnInit() {}
}
