package com.spring_sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;

@SpringBootApplication
public class SpringSqlApplication {

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
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringSqlApplication.class, args);

        // Cách 1 Sử dụng EntityManager
        EntityManager em = context.getBean(EntityManager.class);

        // Cách 2: Sử dụng JpaRepository
        MyRepository myRepository = context.getBean(MyRepository.class);

        callableStatementWithEntityManager(em);

        callableStatementWithJpaRepository(myRepository);

    }

    private static void callableStatementWithEntityManager(EntityManager em) {
        StoredProcedureQuery query = em.createStoredProcedureQuery("calc_max");

        query.registerStoredProcedureParameter(1, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);

        query.setParameter(1, 1);
        query.setParameter(2, 2);

        // query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.OUT);

        query.execute();
        Integer result = (Integer) query.getOutputParameterValue(3);
        System.out.println("Result : " + result);
        // Hibernate: {call calc_max(?,?,?)}
        // Result : 2
    }

    private static void callableStatementWithJpaRepository(MyRepository repository) {
        Integer result2 = repository.max(1, 2);
        System.out.println("Result2: " + result2);
        // Hibernate: {call calc_max(?,?,?)}
        // Result2: 2
    }
}
