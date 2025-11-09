import { Component } from '@angular/core';
import {
  LucideAngularModule,
  PlusIcon,
  RefreshCwIcon,
  FileSymlinkIcon,
  TableIcon,
  SearchIcon,
} from 'lucide-angular';

@Component({
  selector: 'app-ui-table',
  templateUrl: './ui-table.component.html',
  imports: [LucideAngularModule],
  styles: `
    :host {
      display: contents;
    }
  `,
})
export class UiTableComponent {
  readonly PlusIcon = PlusIcon;
  readonly RefreshCwIcon = RefreshCwIcon;
  readonly FileSymlinkIcon = FileSymlinkIcon;
  readonly TableIcon = TableIcon;
  readonly SearchIcon = SearchIcon;

  data = Array(100)
    .fill(null)
    .map((_, i) => i);

  constructor() {}
}
