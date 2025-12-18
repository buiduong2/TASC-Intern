import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from '../app';

@Component({
  selector: 'app-user-item',
  imports: [CommonModule],
  templateUrl: './user-item.html',
  styleUrl: './user-item.css',
})
export class UserItem {
  // ✅ Nhận dữ liệu từ component cha
  @Input() user!: User;

  @Output() deleteUser = new EventEmitter<number>();

  onDelete() {
    this.deleteUser.emit(this.user.id);
  }
}
