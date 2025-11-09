import { Employee } from '../pages/user-list-page/user-list-page.component';

export function getStatusTextClasses(status: Employee['status']): string[] {
  switch (status) {
    case 'Commission':
      return ['text-blue-500'];
    case 'Salaried':
      return ['text-green-500'];
    case 'Terminated':
      return ['text-orange-500'];
    default:
      break;
  }
  return [];
}

export function getStatusDotClasses(status: Employee['status'], base: string): string[] {
  const baseArr = base.split(' ');
  switch (status) {
    case 'Commission':
      return ['bg-blue-500', ...baseArr];
    case 'Salaried':
      return ['bg-green-500', ...baseArr];
    case 'Terminated':
      return ['bg-orange-500', ...baseArr];
    default:
      break;
  }
  return [];
}
