package com.bai_05;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_CREATED_AT = 2;
    public static final int COLUMN_INDEX_IS_ACTIVE = 3;

    public static void main(String[] args) {
        final List<Student> students = getStudents();
        final String outputPath = "output/bai5.xlsx";

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("students");
            CellStyle headerStyle = styleHeaderCell(wb);
            CellStyle dataStyle = styleDataCell(wb);
            CellStyle dataDateStyle = styleDataDateCell(dataStyle, wb);

            int rowIndex = 0;
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

    private static CellStyle styleDataCell(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        style.setAlignment(HorizontalAlignment.LEFT);

        return style;
    }

    private static CellStyle styleDataDateCell(CellStyle dataStyle, Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(dataStyle);
        style.setDataFormat(
                workbook.getCreationHelper().createDataFormat().getFormat("yyyy-MM-dd"));

        return style;
    }

    private static CellStyle styleHeaderCell(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);

        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        font.setColor(IndexedColors.WHITE.getIndex());

        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFont(font);

        return style;
    }

    private static void writerHeader(Sheet sheet, int rowIndex, CellStyle style) {
        Row row = sheet.createRow(rowIndex);

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
