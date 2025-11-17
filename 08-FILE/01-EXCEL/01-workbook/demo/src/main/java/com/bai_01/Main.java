package com.bai_01;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ✔ Tạo một Workbook mới (bằng XSSFWorkbook)
 * ✔ Tạo một Sheet tên “Students”
 * ✔ Tạo một Row đầu tiên
 * ✔ Tạo 3 Cell trong Row đó
 * ✔ Gán giá trị text cho mỗi cell (VD: "ID", "Name", "Age")
 * ❗ Chưa cần ghi ra file (đó là Bài 2)
 * 
 */
public class Main {
    public static void main(String[] args) {
        // B1. Tạo ra một work book mới
        try (Workbook workbook = new XSSFWorkbook();) {
            // B2. ✔ Tạo một Sheet tên “Students”
            Sheet sheet = workbook.createSheet("Students");

            // B3: Tạo Row
            Row row = sheet.createRow(0);

            // B4: Tạo ra 3 cell trong row
            Cell[] cells = new Cell[3];
            for (int i = 0; i < cells.length; i++) {
                Cell cell = row.createCell(i);
                cells[i] = cell;
            }

            // B5: Gán giá trị cho mỗi cell

            cells[0].setCellValue("ID");
            cells[1].setCellValue("Name");
            cells[2].setCellValue("Age");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Done !");

    }
}
