import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FileSymlinkIcon,
  LucideAngularModule,
  PlusIcon,
  RefreshCwIcon,
  SearchIcon,
  TableIcon,
} from 'lucide-angular';
import { Employee } from '../../../pages/user-list-page/user-list-page.component';
import { getStatusTextClasses, getStatusDotClasses } from '../../../utils/utils';
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

  readonly getStatusTextClasses = getStatusTextClasses;
  readonly getStatusDotClasses = getStatusDotClasses;

  @Input({ required: true }) employees!: Employee[];

  @Output() onClickRecord = new EventEmitter<number>();
  constructor() {}

  clickRow(id: number) {
    this.onClickRecord.emit(id);
  }
}
