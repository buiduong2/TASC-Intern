import { Component } from '@angular/core';
import { BellIcon, LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-header-auth',
  templateUrl: './app-header-auth.component.html',
  imports: [LucideAngularModule],
})
export class AppHeaderAuthComponent {
  readonly BellIcon = BellIcon;

  constructor() {}
}
