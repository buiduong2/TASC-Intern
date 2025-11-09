import { Component, effect, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UIAccordionStateService } from './ui-accordtion-state.service';

export interface AccordionStateEvent {
  id: number;
  isOpen: boolean;
}

@Component({
  selector: 'app-ui-accordion',
  template: ` <ng-content></ng-content> `,
  providers: [UIAccordionStateService],
  styles: `
    :host {
      display: contents;
    }
  `,
})
export class UiAccordionComponent implements OnInit {
  constructor(private stateService: UIAccordionStateService) {
    effect(() => {
      this.openChange.emit({
        id: this.id,
        isOpen: this.stateService.isOpen(),
      });
    });
  }

  @Input({ required: true }) id!: number;
  @Input() defaultOpen: boolean = false;

  @Output() openChange = new EventEmitter<AccordionStateEvent>();

  ngOnInit(): void {
    if (this.defaultOpen) {
      this.stateService.setOpen(true);
    }
  }

  @Input() set open(value: boolean) {
    this.stateService.setOpen(value);
  }
}
