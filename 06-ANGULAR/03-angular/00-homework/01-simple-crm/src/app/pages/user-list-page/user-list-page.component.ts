import { Component, computed, OnInit, signal } from '@angular/core';
import { UiTableComponent } from '../../components/app-ui/ui-table/ui-table.component';
import { UserListDetailComponent } from '../../components/user-list-detail/user-list-detail.component';
import { LucideAngularModule, PinIcon, XIcon } from 'lucide-angular';

import EMPLOYEES from '../../../data/employee.json';
export interface Employee {
  id: number;
  name: string;
  role: string;
  company: string;
  status: 'Salaried' | 'Terminated' | 'Commission';
  assignedTo: string;
  phone: string;
  email: string;
  avatar: string; // Tên file ảnh (ví dụ: 'ren.avif')
}
@Component({
  selector: 'user-list-page',
  templateUrl: './user-list-page.component.html',
  imports: [LucideAngularModule, UiTableComponent, UserListDetailComponent],
})
export class UserListPageComponent {
  readonly PinIcon = PinIcon;
  readonly XIcon = XIcon;
  isPinned = signal(false);

  employees = signal<Employee[]>([]);

  selectedEmployeeId = signal<number | undefined>(undefined);

  selectedEmployee = computed<Employee | undefined>(() => {
    const id = this.selectedEmployeeId();
    if (id) {
      return this.employees().find((e) => e.id === id);
    }
    return undefined;
  });

  isOpenDetail = computed<boolean>(() => !!this.selectedEmployee());

  constructor() {
    this.employees.set(EMPLOYEES as Employee[]);
  }

  viewDetail(id: number) {
    this.selectedEmployeeId.set(id);
  }

  closeDetail() {
    this.selectedEmployeeId.set(undefined);
  }

  togglePin = () => {
    this.isPinned.update((p) => !p);
  };
}
