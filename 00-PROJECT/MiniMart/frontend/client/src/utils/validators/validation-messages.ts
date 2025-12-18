import { FormGroup } from '@angular/forms';

export const VALIDATION_MESSAGES: Record<string, any> = {
  required: () => 'This field is required',
  minlength: (e: any) => `Minimum length is ${e.requiredLength}`,
  maxlength: (e: any) => `Maximum length is ${e.requiredLength}`,
  email: () => 'Invalid email address',
  passwordMismatch: () => 'Passwords do not match',
  emailExists: () => 'Email already exists',
  serverError: (msg: string) => msg,
};

export function getErrorMessage(form: FormGroup, controlName: string): string | null {
  const control = form.get(controlName);
  if (!control || !control.errors) return null;

  const firstKey = Object.keys(control.errors)[0];
  const errorValue = control.errors[firstKey];

  const messageGenerator = VALIDATION_MESSAGES[firstKey];

  return messageGenerator ? messageGenerator(errorValue) : null;
}
