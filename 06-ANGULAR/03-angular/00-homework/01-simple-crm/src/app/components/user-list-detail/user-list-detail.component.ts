import { Component, OnInit } from '@angular/core';
import {
  LucideAngularModule,
  PinIcon,
  XIcon,
  PhoneIcon,
  MailIcon,
  HouseIcon,
  DiscIcon,
  PenIcon,
  ChevronDown,
} from 'lucide-angular';
import { UiAccordionComponent } from '../app-ui/ui-accordion/ui-accordion.component';
import { UIAccordionContentComponent } from '../app-ui/ui-accordion/ui-accordion-content.component';
import { UIAccordionTriggerDirective } from '../app-ui/ui-accordion/ui-accordion-trigger.component';

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
export class UserListDetailComponent implements OnInit {
  readonly PinIcon = PinIcon;
  readonly XIcon = XIcon;
  readonly PhoneIcon = PhoneIcon;
  readonly MailIcon = MailIcon;
  readonly HouseIcon = HouseIcon;
  readonly DiscIcon = DiscIcon;
  readonly PenIcon = PenIcon;
  readonly ChevronDown = ChevronDown;

  constructor() {}

  ngOnInit() {}
}
