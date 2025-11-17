package com.bai_04;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

public class ReadFileExcel {

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_CREATED_AT = 2;
    public static final int COLUMN_INDEX_IS_ACTIVE = 3;

    public static void main(String[] args) {
        final List<Student> students = new ArrayList<>();
        final String filePath = "output/bai4.xlsx";

        try (InputStream is = new FileInputStream(new File(filePath));
                Workbook wb = WorkbookFactory.create(is)) {

            Sheet sheet = wb.getSheetAt(0);
            Iterator<Row> rowIte = sheet.rowIterator();
            rowIte.next();// ingore title
            while (rowIte.hasNext()) {
                Row row = rowIte.next();
                if (row == null) {
                    continue;
                }

                Cell[] cells = {
                        row.getCell(COLUMN_INDEX_ID, MissingCellPolicy.RETURN_BLANK_AS_NULL),
                        row.getCell(COLUMN_INDEX_NAME, MissingCellPolicy.RETURN_BLANK_AS_NULL),
                        row.getCell(COLUMN_INDEX_CREATED_AT, MissingCellPolicy.RETURN_BLANK_AS_NULL),
                        row.getCell(COLUMN_INDEX_IS_ACTIVE, MissingCellPolicy.RETURN_BLANK_AS_NULL)
                };

                Student student = StudentMapper.toStudent(cells);
                students.add(student);

            }

            students.forEach(System.out::println);

        } catch (IOException e) {
            return;
        }

    }
}
