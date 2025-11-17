package com.bai_03;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Main {
    public static void main(String[] args) {

        final String fileUrl = "output/result.xlsx";

        final String sheetName = "students";
        final int beginRowNumber = 1;
        final int[] pickedCellIndexes = new int[] { 0, 1, 2 };

        List<Student> students = new ArrayList<>();

        try (Workbook wb = WorkbookFactory.create(new File(fileUrl))) {

            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found");
            }

            Iterator<Row> rowsIte = sheet.rowIterator();

            for (int i = 0; i < beginRowNumber; i++) {
                rowsIte.next();
            }

            while (rowsIte.hasNext()) {
                Row row = rowsIte.next();

                if (row == null) {
                    continue;
                }

                Cell[] pickedCells = Arrays.stream(pickedCellIndexes)
                        .mapToObj(cellIndex -> row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
                        .toArray(Cell[]::new);

                Student student = StudentMapper.toStudent(pickedCells);

                students.add(student);
            }

            System.out.println("Students imported: ");

            students.forEach(System.out::println);

        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
