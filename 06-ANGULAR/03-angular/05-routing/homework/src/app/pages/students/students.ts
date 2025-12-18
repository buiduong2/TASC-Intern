import { Component, signal } from '@angular/core';
import { Student } from '../../app';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'StudentsPage',
  template: `
    <h1>page Students</h1>
    @if(students.length == 0) {
    <i>Chưa có student nào</i>
    } @else { @for(student of students; track student.id) {
    <li>
      {{ student.name }} <a [routerLink]="['/students', student.id, 'edit']">Edit</a>
    </li>
    } }
  `,
  standalone: true,
  imports: [RouterLink],
})
export class StudentPage {
  students: Student[];

  constructor() {
    const storedStudents =
      JSON.parse(localStorage.getItem('students') || 'null') || ([] as Student[]);

    if (storedStudents.length == 0) {
      this.students = [
        { id: 1, name: 'Duong', desc: ' hello world from duong' },
        { id: 2, name: 'Bui', desc: ' hello world from Bui' },
      ];
      localStorage.setItem('students', JSON.stringify(this.students));
    } else {
      this.students = storedStudents;
    }
  }
}
