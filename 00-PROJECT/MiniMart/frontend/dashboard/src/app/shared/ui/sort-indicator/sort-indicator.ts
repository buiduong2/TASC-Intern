import { Component, Input } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-sort-indicator',
  imports: [MatIcon],
  template: `
    @if (sorted) {
      <span class="sort-container">
        <mat-icon class="sort-icon">
          {{ direction === 'asc' ? 'arrow_upward' : 'arrow_downward' }}
        </mat-icon>

        @if (index) {
          <span class="sort-index">{{ index }}</span>
        }
      </span>
    }
  `,
  styles: `
    .sort-container {
      display: flex;
      justify-content: center;
      align-items: center;
    }
  `,
})
export class SortIndicator {
  @Input() sorted: boolean = false;
  @Input() direction?: 'asc' | 'desc';
  @Input() index?: number;

  constructor() {}
}
