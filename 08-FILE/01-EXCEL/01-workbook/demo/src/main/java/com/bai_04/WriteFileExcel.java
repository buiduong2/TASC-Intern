package com.bai_04;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteFileExcel {

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_CREATED_AT = 2;
    public static final int COLUMN_INDEX_IS_ACTIVE = 3;

    public static void main(String[] args) {
        final List<Student> students = getStudents();
        final String outputPath = "output/bai4.xlsx";

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("students");
            int rowIndex = 0;
            writerHeader(sheet, rowIndex);

            for (Student student : students) {
                rowIndex++;
                Row row = sheet.createRow(rowIndex);
                writeStudent(student, row);
            }

            File file = new File(outputPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            OutputStream os = new FileOutputStream(outputPath);
            wb.write(os);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writerHeader(Sheet sheet, int rowIndex) {
        Row row = sheet.createRow(rowIndex);

        row.createCell(COLUMN_INDEX_ID).setCellValue("ID");
        row.createCell(COLUMN_INDEX_NAME).setCellValue("Name");
        row.createCell(COLUMN_INDEX_CREATED_AT).setCellValue("Created At");
        row.createCell(COLUMN_INDEX_IS_ACTIVE).setCellValue("Is Active");

    }

    private static void writeStudent(Student student, Row row) {
        row.createCell(COLUMN_INDEX_ID).setCellValue(student.getId());
        row.createCell(COLUMN_INDEX_NAME).setCellValue(student.getName());
        row.createCell(COLUMN_INDEX_CREATED_AT)
                .setCellValue(Date.from(student.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        row.createCell(COLUMN_INDEX_IS_ACTIVE).setCellValue(student.isActive());

    }

    public static List<Student> getStudents() {
        return List.of(
                new Student(1, "Duong", LocalDateTime.now().minusMinutes(1), false),
                new Student(2, "Bui", LocalDateTime.now().minusMinutes(2), true),
                new Student(3, "Duc", LocalDateTime.now().minusMinutes(3), false),
                new Student(4, "Dung", LocalDateTime.now().minusMinutes(4), true));
    }
}
