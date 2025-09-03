package com;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;

public class Main {
    /**
     * 
     * DELIMITER $$
     * 
     * CREATE PROCEDURE calc_max(
     * IN p1 INT,
     * IN p2 INT,
     * OUT p3 INT
     * )
     * BEGIN
     * IF p1 > p2 THEN
     * SET p3 = p1;
     * ELSE
     * SET p3 = p2;
     * END IF;
     * END $$
     * 
     * DELIMITER ;
     * 
     */
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/demo_itern";
        String user = "root";
        String password = "my-secret-pw";
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 1. Chuẩn bị câu lệnh SQL
        String sql = "{call calc_max(?, ?, ?)}";

        // 2. mở ra một Conoection đến DB
        // 3. Tạo ra một đối tượng tạo câu lẹn SQL và thực thi
        try (Connection conn = DriverManager.getConnection(url, user, password);
                CallableStatement cs = conn.prepareCall(sql)) {

            // 4. Khai báo các tham số
            cs.setInt(1, 1);
            cs.setInt(2, 2);

            // 5. Đăng kí OUT parameter
            cs.registerOutParameter(3, Types.INTEGER);

            // 6. Thực thi
            cs.execute();

            // 7. Lấy ra đối tượng trả về
            Integer resultValue = cs.getInt(3);
            System.out.println("OK!: " + resultValue); // OK!: 2
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}