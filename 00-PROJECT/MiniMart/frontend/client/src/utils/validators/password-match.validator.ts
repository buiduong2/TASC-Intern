import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { FieldError } from '../../types/res.type';

export const passwordMatchValidator: ValidatorFn = (group: AbstractControl) => {
  const password = group.get('password');
  const confirmPassword = group.get('confirmPassword');

  if (!password || !confirmPassword) return null;

  const pass = password.value;
  const confirm = confirmPassword.value;

  // Lấy lỗi hiện tại
  const currentErrors = confirmPassword.errors || {};

  // Nếu trùng nhau → XÓA lỗi passwordMismatch, giữ lại lỗi khác
  if (pass === confirm) {
    if (currentErrors['passwordMismatch']) {
      delete currentErrors['passwordMismatch'];
    }

    // Nếu sau khi xoá mà KHÔNG còn lỗi nào → set null
    if (Object.keys(currentErrors).length === 0) {
      confirmPassword.setErrors(null);
    } else {
      confirmPassword.setErrors(currentErrors);
    }

    return null;
  }

  // Nếu KHÔNG trùng nhau → THÊM lỗi passwordMismatch (KHÔNG xoá lỗi cũ)
  confirmPassword.setErrors({
    ...currentErrors,
    passwordMismatch: true,
  });

  return null;
};

export const applyServerErrors = (form: FormGroup, errors: FieldError[]) => {
  errors.forEach((err) => {
    const control = form.get(err.field);

    if (control) {
      const currentErrors = control.errors || {};

      control.setErrors({
        ...currentErrors,
        serverError: err.message,
      });
    }
  });

  form.updateValueAndValidity();
};
