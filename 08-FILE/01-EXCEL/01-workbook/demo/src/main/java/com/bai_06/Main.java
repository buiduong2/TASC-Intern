package com.bai_06;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_CREATED_AT = 2;
    public static final int COLUMN_INDEX_IS_ACTIVE = 3;

    public static void main(String[] args) {
        final List<Student> students = getStudents();
        final String outputPath = "output/bai6.xlsx";

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("students");
            CellStyle titleStyle = CellStyles.createTitleCelLStyle(wb);
            CellStyle headerStyle = CellStyles.createHeaderCellStyle(wb);
            CellStyle dataStyle = CellStyles.createBodyCellStyle(wb);
            CellStyle dataDateStyle = CellStyles.createBodyDateCellStyle(dataStyle, wb);

            int rowIndex = 0;

            writeTitle(sheet, rowIndex, titleStyle);

            rowIndex++;
            writerHeader(sheet, rowIndex, headerStyle);

            for (Student student : students) {
                rowIndex++;
                Row row = sheet.createRow(rowIndex);

                writeStudent(student, row, dataStyle, dataDateStyle);
            }

            for (int i = 0; i <= COLUMN_INDEX_IS_ACTIVE; i++) {
                sheet.autoSizeColumn(i);
            }

            try (OutputStream os = new FileOutputStream(outputPath)) {
                wb.write(os);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeTitle(Sheet sheet, int rowIndex, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        Cell first = row.createCell(0);
        Cell second = row.createCell(1);
        Cell third = row.createCell(2);
        Cell fourth = row.createCell(3);

        first.setCellStyle(style);
        second.setCellStyle(style);
        third.setCellStyle(style);
        fourth.setCellStyle(style);

        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, COLUMN_INDEX_ID, COLUMN_INDEX_IS_ACTIVE));

        first.setCellValue("STUDENT REPORT");
        row.setHeightInPoints(30);

    }

    private static void writerHeader(Sheet sheet, int rowIndex, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(20);

        Cell idCell = row.createCell(COLUMN_INDEX_ID);
        idCell.setCellValue("ID");
        idCell.setCellStyle(style);

        Cell nameCell = row.createCell(COLUMN_INDEX_NAME);
        nameCell.setCellValue("Name");
        nameCell.setCellStyle(style);

        Cell createdAtCell = row.createCell(COLUMN_INDEX_CREATED_AT);
        createdAtCell.setCellValue("Created At");
        createdAtCell.setCellStyle(style);

        Cell isActiveCell = row.createCell(COLUMN_INDEX_IS_ACTIVE);
        isActiveCell.setCellValue("Is Active");
        isActiveCell.setCellStyle(style);
    }

    private static void writeStudent(Student student, Row row, CellStyle cellStyle, CellStyle dateStyle) {

        Cell idCell = row.createCell(COLUMN_INDEX_ID);
        idCell.setCellValue(student.getId());
        idCell.setCellStyle(cellStyle);

        Cell nameCell = row.createCell(COLUMN_INDEX_NAME);
        nameCell.setCellValue(student.getName());
        nameCell.setCellStyle(cellStyle);

        Cell createdAtCell = row.createCell(COLUMN_INDEX_CREATED_AT);
        createdAtCell.setCellValue(Date.from(student.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        createdAtCell.setCellStyle(dateStyle);

        Cell activeCell = row.createCell(COLUMN_INDEX_IS_ACTIVE);
        activeCell.setCellValue(student.isActive());
        activeCell.setCellStyle(cellStyle);

    }

    public static List<Student> getStudents() {
        return List.of(
                new Student(1, "Duong", LocalDateTime.now().minusMinutes(1), false),
                new Student(2, "Bui", LocalDateTime.now().minusMinutes(2), true),
                new Student(3, "Duc", LocalDateTime.now().minusMinutes(3), false),
                new Student(4, "Dung", LocalDateTime.now().minusMinutes(4), true));
    }
}
