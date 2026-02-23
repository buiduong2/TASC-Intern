import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChip } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { Router } from '@angular/router';
import { AvatarComponent } from '@shared/ui/avatar/avatar.component';
import { ConfirmDialog } from '@shared/ui/dialog/confirm-dialog/confirm-dialog';
import { CustomPaginatorIntl } from '@shared/ui/paginator/custom-paginator-intl.service';
import { Observable } from 'rxjs';
import { AuthService, User } from 'src/app/core/auth/auth.service';

@Component({
  selector: 'app-user-menu',
  templateUrl: './user-menu.component.html',
  styleUrls: ['./user-menu.component.css'],
  imports: [
    MatMenuModule,
    AvatarComponent,
    MatButtonModule,
    AsyncPipe,
    MatChip,
    MatDivider,
    MatIcon,
  ],
  providers: [{ provide: MatPaginatorIntl, useClass: CustomPaginatorIntl }],
})
export class UserMenuComponent {
  user$: Observable<User>;

  constructor(
    readonly authService: AuthService,
    readonly router: Router,
    private dialog: MatDialog,
  ) {
    this.user$ = authService.user$;
  }

  logout() {
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Thoát tài khoản',
        description: 'Hành động này không thể hoàn tác.',
        confirmText: 'Thoát',
        cancelText: 'Hủy',
      },
    });

    ref.afterClosed().subscribe((result) => {
      if (result === true) {
        console.log('DELETE TODO');
      }
    });
  }

  openChangePassword() {
    this.router.navigate(['auth', 'change-password']);
  }
  openProfile() {
    this.router.navigate(['auth', 'me']);
  }
}
