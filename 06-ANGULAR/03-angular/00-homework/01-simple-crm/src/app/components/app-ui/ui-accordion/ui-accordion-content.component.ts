import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common'; // Cần cho *ngIf
import { UIAccordionStateService } from './ui-accordtion-state.service';

@Component({
  selector: 'app-ui-accordion-content',
  standalone: true,
  imports: [CommonModule],
  // Template chứa logic ẩn/hiện và slot nội dung
  template: `
    <div class="accordion-content-container" [class.open]="stateService.isOpen()">
      <div class="content">
        <ng-content></ng-content>
      </div>
    </div>
  `,
  styles: `
    .accordion-content-container {
      display: grid;
      grid-template-rows: 0fr;
      overflow: hidden;
      transition: grid-template-rows 500ms;
    }
    .accordion-content-container.open {
      grid-template-rows: 1fr;
    }
    .accordion-content-container .content {
      min-height: 0;
      transition: visibility 1s;
      visibility: hidden;
      overflow: hidden;
    }
    .accordion-content-container.open .content {
      visibility: visible;
    }
  `,
})
export class UIAccordionContentComponent {
  constructor(public stateService: UIAccordionStateService) {}
}
