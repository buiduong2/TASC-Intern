package com.bai_04;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

public class StudentMapper {

    public static Student toStudent(Cell[] cells) {
        if (cells.length < 4) {
            throw new IllegalArgumentException("Cells length not valid. Required: 4 but found: " + cells.length);
        }

        Long id = readNumber(cells[0], d -> d.longValue());
        if (id == null) {
            return null;
        }
        String name = readString(cells[1]);
        LocalDateTime createdAt = LocalDateTime.ofInstant(readDate(cells[2]).toInstant(), ZoneId.systemDefault());
        boolean isActive = readBoolean(cells[3]);

        return new Student(id, name, createdAt, isActive);
    }

    public static String readString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> throw new IllegalArgumentException("Unsupported cell type");
        };
    }

    private static Date parseDateString(String str) {
        return null;
    }

    public static Date readDate(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return DateUtil.getJavaDate(cell.getNumericCellValue());
        }

        if (cell.getCellType() == CellType.STRING) {
            return parseDateString(cell.getStringCellValue());
        }

        throw new IllegalArgumentException("Not a date cell");

    }

    public static Boolean readBoolean(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case BOOLEAN -> cell.getBooleanCellValue();
            case NUMERIC -> cell.getNumericCellValue() != 0;
            default -> null;
        };
    }

    public static <T extends Number> T readNumber(Cell cell, Function<Double, T> function) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> function.apply(cell.getNumericCellValue());
            case STRING -> function.apply(Double.parseDouble(cell.getStringCellValue().trim()));
            case FORMULA -> function.apply(cell.getNumericCellValue());
            default -> throw new IllegalArgumentException("Not numeric");
        };

    }
}
