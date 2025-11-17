package com.bai_07;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Main {

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_CREATED_AT = 2;
    public static final int COLUMN_INDEX_IS_ACTIVE = 3;

    public static void main(String[] args) {

        final List<Student> students = getStudents();
        final String path = "template/student_template.xlsx";
        final String output = "output/bai7.xlsx";
        final int patternRowIndex = 2;
        final int startRowIndex = patternRowIndex + 1;

        try (Workbook wb = WorkbookFactory.create(new File(path))) {

            Sheet sheet = wb.getSheet("students");
            Row patternRow = sheet.getRow(patternRowIndex);

            Map<Integer, CellStyle> mapStyle = Map.of(
                    COLUMN_INDEX_ID, patternRow.getCell(COLUMN_INDEX_ID).getCellStyle(),
                    COLUMN_INDEX_NAME, patternRow.getCell(COLUMN_INDEX_NAME).getCellStyle(),
                    COLUMN_INDEX_CREATED_AT, patternRow.getCell(COLUMN_INDEX_CREATED_AT).getCellStyle(),
                    COLUMN_INDEX_IS_ACTIVE, patternRow.getCell(COLUMN_INDEX_IS_ACTIVE).getCellStyle());

            int rowIndex = startRowIndex;

            for (Student student : students) {
                Row row = sheet.createRow(rowIndex);
                writeStudent(student, row, mapStyle);
                rowIndex++;
            }

            try (OutputStream os = new FileOutputStream(output)) {
                wb.write(os);
            }

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeStudent(Student student, Row row, Map<Integer, CellStyle> mapStyle) {

        Cell idCell = row.createCell(COLUMN_INDEX_ID);
        idCell.setCellValue(student.getId());
        idCell.setCellStyle(mapStyle.get(COLUMN_INDEX_ID));

        Cell nameCell = row.createCell(COLUMN_INDEX_NAME);
        nameCell.setCellValue(student.getName());
        nameCell.setCellStyle(mapStyle.get(COLUMN_INDEX_NAME));

        Cell createdAtCell = row.createCell(COLUMN_INDEX_CREATED_AT);
        createdAtCell.setCellValue(Date.from(student.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        createdAtCell.setCellStyle(mapStyle.get(COLUMN_INDEX_CREATED_AT));

        Cell activeCell = row.createCell(COLUMN_INDEX_IS_ACTIVE);
        activeCell.setCellValue(student.isActive());
        activeCell.setCellStyle(mapStyle.get(COLUMN_INDEX_IS_ACTIVE));

    }

    public static List<Student> getStudents() {
        return List.of(
                new Student(1, "Duong", LocalDateTime.now().minusMinutes(1), false),
                new Student(2, "Bui", LocalDateTime.now().minusMinutes(2), true),
                new Student(3, "Duc", LocalDateTime.now().minusMinutes(3), false),
                new Student(4, "Dung", LocalDateTime.now().minusMinutes(4), true));
    }
}
