package com.backend.mock.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import com.backend.product.model.Category;
import com.backend.product.model.CategoryImage;
import com.backend.product.model.Status;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MockCategoryData {

    @Autowired
    private EntityManager em;

    // @Test
    @Commit
    public void mockCategory() {
        saveBeautiAndCare();
        saveBreakFast();
        saveCookingEssentials();

    }

    private void saveBeautiAndCare() {
        Category category = new Category();
        category.setName("Beauty & Healths");
        category.setStatus(Status.ACTIVE);
        CategoryImage image = new CategoryImage();
        image.setSrc("https://res.cloudinary.com/ahossain/image/upload/v1658340706/category%20icon/beauty_vfbmzc.png");

        category.setImage(image);

        em.persist(image);
        em.persist(category);

    }

    private void saveCookingEssentials() {
        Category category = new Category();
        category.setName("Cooking Essentials");
        category.setStatus(Status.ACTIVE);
        CategoryImage image = new CategoryImage();
        image.setSrc(
                "https://res.cloudinary.com/ahossain/image/upload/v1658340704/category%20icon/frying-pan_vglm5c.png");

        category.setImage(image);

        em.persist(image);
        em.persist(category);
    }

    private void saveBreakFast() {
        Category category = new Category();
        category.setName("Break Fast");
        category.setStatus(Status.ACTIVE);
        CategoryImage image = new CategoryImage();
        image.setSrc(
                "https://res.cloudinary.com/ahossain/image/upload/v1658340705/category%20icon/bagel_mt3fod.png");

        category.setImage(image);

        em.persist(image);
        em.persist(category);
    }

}
