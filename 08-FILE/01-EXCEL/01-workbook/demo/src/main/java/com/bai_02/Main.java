package com.bai_02;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * TIẾP THEO: BÀI 2 – Write File → Export thành công
 * 
 * Bạn đã tạo được file trong RAM, giờ chúng ta sẽ:
 * 
 * ghi Workbook xuống file .xlsx
 * 
 * đặt đường dẫn
 * 
 * tạo file Excel thật → mở bằng Excel được
 */
public class Main {
    public static void main(String[] args) {
        // B1: dữ liệu để import
        final List<Student> students = List.of(
                new Student(1, "Duong", 18),
                new Student(2, "Bui", 20),
                new Student(3, "Duc", 22)

        );

        final String outputFile = "output/result.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {

            // 2. Sheet
            Sheet sheet = workbook.createSheet("students");

            // 3. Header Row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");
            headerRow.createCell(2).setCellValue("Age");

            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getAge());
            }

            File file = new File(outputFile);

            OutputStream os = new FileOutputStream(file);

            workbook.write(os);
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
