import { Component, inject, signal } from '@angular/core';
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
import { UIAccordionContentComponent } from '../app-ui/ui-accordion/ui-accordion-content.component';
import { UIAccordionTriggerDirective } from '../app-ui/ui-accordion/ui-accordion-trigger.component';
import {
  AccordionStateEvent,
  UiAccordionComponent,
} from '../app-ui/ui-accordion/ui-accordion.component';
import NAV_DATA  from '../../../data/sidebar-navItems.json';
type NavItem = {
  id: number;
  title: string;
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
  imports: [
    LucideAngularModule,
    ApplayoutShowOnExpanded,
    AppLayoutShowOnCompact,
    UiAccordionComponent,
    UIAccordionContentComponent,
    UIAccordionTriggerDirective,
  ],
})
export class AppSidebarComponent {
  readonly UserIcon = UserIcon;
  readonly ChevronDownIcon = ChevronDownIcon;
  readonly ChevronUpIcon = ChevronUpIcon;
  navItems: NavItem[] = [];
  navIcons: LucideIconData[] = [];
  openGroup = signal<Record<number, boolean>>({});
  layoutService = inject(AppLayoutStateService);

  constructor() {
    this.navIcons = [UserIcon, CalendarCheckIcon, ChartColumnBigIcon];
    this.navItems = (NAV_DATA as any);
  }

  toggleGroupAndExpandLayout(id: number) {
    this.layoutService.setExpanded();
  }

  handleOpenChange({ id, isOpen }: AccordionStateEvent) {
    this.openGroup.update((v) => ({ ...v, [id]: isOpen }));
  }
}
