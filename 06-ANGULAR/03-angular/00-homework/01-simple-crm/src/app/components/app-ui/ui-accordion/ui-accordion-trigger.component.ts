// accordion-trigger.directive.ts (Component Con)

import { Directive, HostListener } from '@angular/core';
import { UIAccordionStateService } from './ui-accordtion-state.service';

@Directive({
  selector: '[accordionTrigger]',
  standalone: true,
})
export class UIAccordionTriggerDirective {
  constructor(private stateService: UIAccordionStateService) {}

  @HostListener('click')
  onClick() {
    this.stateService.toggle();
  }
}
