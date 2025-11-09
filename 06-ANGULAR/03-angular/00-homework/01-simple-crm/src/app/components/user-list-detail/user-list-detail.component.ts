import { Component, Input } from '@angular/core';
import {
  ChevronDown,
  DiscIcon,
  HouseIcon,
  LucideAngularModule,
  MailIcon,
  PenIcon,
  PhoneIcon,
} from 'lucide-angular';
import { UIAccordionContentComponent } from '../app-ui/ui-accordion/ui-accordion-content.component';
import { UIAccordionTriggerDirective } from '../app-ui/ui-accordion/ui-accordion-trigger.component';
import { UiAccordionComponent } from '../app-ui/ui-accordion/ui-accordion.component';
import { Employee } from '../../pages/user-list-page/user-list-page.component';
import { getStatusDotClasses, getStatusTextClasses } from '../../utils/utils';

@Component({
  selector: 'app-user-list-detail',
  templateUrl: './user-list-detail.component.html',
  imports: [
    LucideAngularModule,
    UiAccordionComponent,
    UIAccordionContentComponent,
    UIAccordionTriggerDirective,
  ],
  styles: `
    :host {
      display: contents;
    }
  `,
})
export class UserListDetailComponent {
  readonly PhoneIcon = PhoneIcon;
  readonly MailIcon = MailIcon;
  readonly HouseIcon = HouseIcon;
  readonly DiscIcon = DiscIcon;
  readonly PenIcon = PenIcon;
  readonly ChevronDown = ChevronDown;

  readonly getStatusDotClasses = getStatusDotClasses;
  readonly getStatusTextClasses = getStatusTextClasses;

  @Input({ required: true }) employee!: Employee;

  constructor() {}
}
