package com.backend.product.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.model.Audit;
import com.backend.product.dto.req.CategoryFilter;
import com.backend.product.dto.res.CategoryAdminDTO;
import com.backend.product.model.Category;
import com.backend.product.model.CategoryImage;
import com.backend.product.model.ProductStatus;
import com.backend.user.model.User;
import com.backend.user.security.CustomUserDetail;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcCategoryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<Category> findById(long id) {
        String sql = "SELECT * FROM category WHERE id = :id";
        List<Category> categories = jdbcTemplate.query(sql,
                new MapSqlParameterSource("id", id),
                (rs, i) -> toCategory(rs));

        return categories.stream().findFirst();

    }

    public int deleteById(long id) {
        String sqlManyToOne = "UPDATE product SET category_id = NULL WHERE category_id = :id";
        String sql = "DELETE FROM category WHERE id = :id";

        return jdbcTemplate.update(sqlManyToOne, new MapSqlParameterSource("id", id))
                + jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    @Transactional
    public Category save(Category category) {
        if (category.getAudit() == null) {
            category.setAudit(new Audit());
        }

        return category.getId() == null ? create(category) : update(category);
    }

    private Category create(Category entity) {
        LocalDateTime now = LocalDateTime.now();

        Long id = jdbcTemplate.queryForObject("SELECT nextval('category_seq')", Collections.emptyMap(), Long.class);

        User user = buildAuthUser();
        if (user != null) {
            entity.getAudit().setCreatedBy(user);
        }
        entity.getAudit().setCreatedAt(now);
        entity.setId(id);
        String sql = """
                    INSERT INTO category(id, name, description, status, image_id, created_at, created_by_id)
                    VALUES (:id, :name, :description, :status, :image_id, :created_at, :created_by_id)
                """;

        MapSqlParameterSource params = buildParams(entity);

        int rowAffected = jdbcTemplate.update(sql, params);
        if (rowAffected != 1) {
            throw new RuntimeException("Cannot create Category");
        }

        return entity;
    }

    private Category update(Category entity) {
        User user = buildAuthUser();
        if (user != null) {
            entity.getAudit().setUpdatedBy(user);
        }
        entity.getAudit().setUpdatedAt(LocalDateTime.now());

        String sql = "UPDATE category SET name = :name, description = :description, status = :status, image_id = :image_id, updated_at =:updated_at, updated_by_id = :updated_by_id WHERE id = :id";
        MapSqlParameterSource params = buildParams(entity);

        int rowAffected = jdbcTemplate.update(sql, params);

        if (rowAffected != 1) {
            throw new ResourceNotFoundException("Category id =" + entity.getId() + " is not found");
        }
        return entity;
    }

    private User buildAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof CustomUserDetail customUserDetail) {
            long authId = customUserDetail.getUserId();
            User user = new User();
            user.setId(authId);
            return user;
        }

        return null;
    }

    private MapSqlParameterSource buildParams(Category entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("name", entity.getName());
        params.addValue("description", entity.getDescription());
        params.addValue("status", entity.getStatus() != null ? entity.getStatus().name() : null);
        params.addValue("image_id", entity.getImage() == null ? null : entity.getImage().getId());
        params.addValue("created_at", entity.getAudit().getCreatedAt());
        params.addValue("updated_at", entity.getAudit().getUpdatedAt());
        User createdBy = entity.getAudit().getCreatedBy();
        User updatedBy = entity.getAudit().getUpdatedBy();
        params.addValue("created_by_id", createdBy == null ? null : createdBy.getId());
        params.addValue("updated_by_id", updatedBy == null ? null : updatedBy.getId());

        return params;
    }

    private Category toCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        String status = rs.getString("status");
        if (status != null) {
            category.setStatus(ProductStatus.valueOf(status));
        }

        Audit audit = new Audit();
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        category.setAudit(audit);
        if (createdAt != null) {
            audit.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            audit.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        Long createdById = rs.getObject("created_by_id", Long.class);
        Long updatedById = rs.getObject("updated_by_id", Long.class);
        if (createdById != null) {
            User user = new User();
            user.setId(createdById);
            audit.setCreatedBy(user);
        }

        if (updatedById != null) {
            User user = new User();
            user.setId(updatedById);
            audit.setUpdatedBy(user);
        }

        Long imageId = rs.getObject("image_id", Long.class);
        if (imageId != null) {
            CategoryImage image = new CategoryImage();
            image.setId(imageId);
            category.setImage(image);
        }

        return category;
    }

    private final Map<String, String> orderColMap = Map.of(
            "id", "c.id",
            "imageUrl", "ci.url",
            "name", "c.name",
            "status", "c.status",
            "productCount", "product_count",
            "createdAt", "c.created_at",
            "updatedAt", "c.updated_at");

    public Page<CategoryAdminDTO> findAllAdmin(CategoryFilter filter, Pageable pageable) {
        StringBuilder mainSql = new StringBuilder("""
                SELECT
                    c.id,
                    ci.url AS image_url,
                    c.name,
                    c.status,
                    c.created_at,
                    c.updated_at,
                    COUNT(p.id) AS product_count
                FROM category AS c
                LEFT JOIN product AS p ON p.category_id = c.id
                LEFT JOIN category_image AS ci ON ci.id = c.image_id
                """);

        if (StringUtils.hasText(filter.getName())) {
            filter.setName("%" + filter.getName() + "%");
        }
        MapSqlParameterSource params = buildAdminParams(filter, pageable);

        String where = buildAdminWhereClause(filter);
        String having = buildAdminHavingClauses(filter);
        String order = buildAdminOrderClauses(pageable);

        String countQuery = buildAdminCountQuery(where, having);

        Long count = jdbcTemplate.queryForObject(countQuery, params, Long.class);

        if (StringUtils.hasText(where)) {
            mainSql.append("WHERE ").append(where).append(" ");
        }

        mainSql.append("GROUP BY c.id, ci.url, c.name, c.status, c.created_at, c.updated_at ");
        if (StringUtils.hasText(having)) {
            mainSql.append("HAVING ").append(having).append(" ");
        }

        if (StringUtils.hasText(order)) {
            mainSql.append("ORDER BY ").append(order).append(" ");
        }

        mainSql.append("LIMIT :limit OFFSET :offset");

        List<CategoryAdminDTO> categoryAdminDTOs = jdbcTemplate.query(mainSql.toString(),
                params, (rs, i) -> toCategoryAdminDTO(rs));

        return new PageImpl<>(categoryAdminDTOs, pageable, count == null ? 0L : count);
    }

    private MapSqlParameterSource buildAdminParams(CategoryFilter filter, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", filter.getName());
        params.addValue("status", filter.getStatus());
        params.addValue("createdFrom", filter.getCreatedFrom());
        params.addValue("updatedFrom", filter.getUpdatedFrom());
        params.addValue("minProductCount", filter.getMinProductCount());
        params.addValue("maxProductCount", filter.getMaxProductCount());

        params.addValue("limit", pageable.getPageSize());
        params.addValue("offset", pageable.getOffset());
        return params;
    }

    private String buildAdminCountQuery(String where, String having) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(having)) {
            sb.append("WITH main_query AS ( ");
            sb.append("SELECT c.id  FROM category c LEFT JOIN product AS p ON p.category_id = c.id ");
            if (StringUtils.hasText(where)) {
                sb.append("WHERE ").append(where).append(" ");
            }
            sb.append("GROUP BY c.id ");
            sb.append("HAVING ").append(having).append(" ");
            sb.append(") SELECT COUNT(*) FROM main_query");
        } else {
            sb.append("SELECT COUNT(*) FROM category AS c");
            if (StringUtils.hasText(where)) {
                sb.append("WHERE ").append(where);
            }
        }

        return sb.toString();
    }

    private String buildAdminOrderClauses(Pageable pageable) {
        List<String> clauses = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String col = order.getProperty();
            if (orderColMap.containsKey(col)) {
                clauses.add(orderColMap.get(col) + " " + order.getDirection().name());
            }
        });
        return String.join(", ", clauses);
    }

    private String buildAdminHavingClauses(CategoryFilter filter) {
        List<String> clauses = new ArrayList<>();
        if (filter.getMaxProductCount() != null) {
            clauses.add("COUNT(p.id) <= :maxProductCount");
        }
        if (filter.getMinProductCount() != null) {
            clauses.add("COUNT(p.id) >= :minProductCount");
        }

        return String.join(" AND ", clauses);
    }

    private String buildAdminWhereClause(CategoryFilter filter) {
        List<String> clauses = new ArrayList<>();
        if (StringUtils.hasText(filter.getName())) {
            clauses.add("c.name LIKE :name");
        }

        if (StringUtils.hasText(filter.getStatus())) {
            clauses.add("c.status = :status");
        }

        if (filter.getCreatedFrom() != null) {
            clauses.add("c.created_at >= :createdFrom");
        }
        if (filter.getUpdatedFrom() != null) {
            clauses.add("c.updated_at >= :updatedFrom");
        }

        return String.join(" AND ", clauses);
    }

    private CategoryAdminDTO toCategoryAdminDTO(ResultSet rs) throws SQLException {
        CategoryAdminDTO categoryAdminDTO = new CategoryAdminDTO();

        categoryAdminDTO.setId(rs.getLong("id"));
        categoryAdminDTO.setImageUrl(rs.getString("image_url"));
        categoryAdminDTO.setName(rs.getString("name"));
        categoryAdminDTO.setProductCount(rs.getLong("product_count"));

        String status = rs.getString("status");
        categoryAdminDTO.setStatus(status == null ? null : ProductStatus.valueOf(status));

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        LocalDateTime createdAt = createdAtTs == null ? null : createdAtTs.toLocalDateTime();
        LocalDateTime updatedAt = updatedAtTs == null ? null : updatedAtTs.toLocalDateTime();
        categoryAdminDTO.setCreatedAt(createdAt);
        categoryAdminDTO.setUpdatedAt(updatedAt);
        return categoryAdminDTO;
    }
}
