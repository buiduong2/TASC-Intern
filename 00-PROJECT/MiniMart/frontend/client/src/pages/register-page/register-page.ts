import { Component, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatError, MatFormField, MatInputModule, MatLabel } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatAnchor } from '@angular/material/button';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { ValidationErrorResponse } from '../../types/res.type';
import { UserSummary } from '../../types/user.type';
import {
  applyServerErrors,
  passwordMatchValidator,
} from '../../utils/validators/password-match.validator';
import { getErrorMessage } from '../../utils/validators/validation-messages';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.html',

  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAnchor,
    MatError,
  ],
})
export class RegisterPage {
  readonly getErrorMessage = getErrorMessage;
  readonly applyServerErrors = applyServerErrors;

  isSubmitting = signal(false);

  public form = new FormGroup(
    {
      username: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(100),
      ]),
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(100),
      ]),
      confirmPassword: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(100),
      ]),
      email: new FormControl('', [
        Validators.required,
        Validators.email,
        Validators.minLength(8),
        Validators.maxLength(100),
      ]),
      firstName: new FormControl('', [Validators.required, Validators.minLength(6)]),
      lastName: new FormControl('', [Validators.required, Validators.minLength(6)]),
    },
    {
      validators: passwordMatchValidator,
    },
  );
  constructor(
    private http: HttpClient,
    private toast: ToastrService,
    private router: Router,
  ) {}

  submitRegister() {
    if (this.form.invalid || this.isSubmitting()) {
      return;
    }

    this.isSubmitting.set(true);
    this.http
      .post<UserSummary>('/v1/auth/register', this.form.value)
      .pipe(
        finalize(() => {
          this.isSubmitting.set(false);
        }),
      )
      .subscribe({
        next: () => {
          this.toast.success('Đăng kí thành công');
          this.router.navigate(['/login']);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 400 && err.error?.errors) {
            const validateErr: ValidationErrorResponse = err.error;
            this.applyServerErrors(this.form, validateErr.errors);
          }
        },
      });
  }
}
