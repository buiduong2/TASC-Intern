import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AvatarComponent implements OnInit {
  @Input() name?: string | null = null;
  @Input() src?: string | null = null;

  protected imageError = false;

  get initial(): string {
    if (!this.name) return '?';
    return this.name.trim().charAt(0).toUpperCase();
  }

  constructor() {}

  ngOnInit() {}

  onError() {
    this.imageError = true;
  }

  get backgroundColor(): string {
    const colors = ['#1abc9c', '#3498db', '#9b59b6', '#e67e22', '#e74c3c'];

    if (!this.name) return colors[0];

    const hash = this.name.charCodeAt(0);
    return colors[hash % colors.length];
  }
}
