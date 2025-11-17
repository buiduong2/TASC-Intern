package com.bai_03;

import org.apache.poi.ss.usermodel.Cell;

public class StudentMapper {
    public static Student toStudent(Cell[] cells) {
        long id = (long) cells[0].getNumericCellValue();
        String name = cells[1].getStringCellValue();
        int age = (int) cells[2].getNumericCellValue();

        return new Student(id, name, age);
    }

}
