import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Student } from '../../app';

@Component({
  selector: 'About',
  template: `
    <h1>Thông tin chi tiết về student</h1>

    <p>ID: {{ student.id }}</p>
    <p>name: {{ student.name }}</p>
    <p>desc: {{ student.desc }}</p>
  `,
  standalone: true,
})
export class StudentDetail {
  id: number;
  student: Student;
  constructor(private route: ActivatedRoute, private router: Router) {
    this.id = Number(route.snapshot.paramMap.get('id'));
    this.student = JSON.parse(localStorage.getItem('students') || '[]').find(
      (s: Student) => s.id === this.id
    );

    if (!this.student) {
      router.navigate(['not-found']);
    }
  }
}
