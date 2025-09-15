package com.backend.product.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.model.Audit;
import com.backend.product.dto.req.TagFilter;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.model.Tag;
import com.backend.user.model.User;
import com.backend.user.security.CustomUserDetail;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcTagRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Tag> findById(long id) {
        String sql = "SELECT * FROM tag WHERE id= ?";
        List<Tag> tags = jdbcTemplate.query(sql, (rs, i) -> toTag(rs), id);
        return tags.stream().findFirst();
    }

    public int deleteById(long id) {
        String sqlManyToMany = "DELETE FROM product_tags WHERE tags_id = ?";
        String sql = "DELETE FROM tag WHERE id = ?";
        jdbcTemplate.update(sqlManyToMany, id);
        return jdbcTemplate.update(sql, id);
    }

    @Transactional
    public Tag save(Tag entity) {
        if (entity.getAudit() == null) {
            entity.setAudit(new Audit());
        }

        return entity.getId() == null ? create(entity) : update(entity);
    }

    private Tag create(Tag entity) {

        LocalDateTime now = LocalDateTime.now();
        Long id = jdbcTemplate.queryForObject("select nextval('tag_seq')", Long.class);
        User user = buildAuthUser();
        if (user != null) {
            entity.getAudit().setCreatedBy(user);
        }

        entity.getAudit().setCreatedAt(now);
        entity.setId(id);

        String sql = "INSERT INTO tag(id, name, description, created_at, created_by_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, entity.getName(), entity.getDescription(),
                entity.getAudit().getCreatedAt(), user == null ? null : user.getId());

        return entity;
    }

    private Tag update(Tag entity) {

        User user = buildAuthUser();
        if (user != null) {
            entity.getAudit().setUpdatedBy(user);
        }

        entity.getAudit().setUpdatedAt(LocalDateTime.now());
        String sql = "UPDATE tag SET name = ?, description = ?, updated_at = ?, updated_by_id = ? WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql,
                entity.getName(),
                entity.getDescription(),
                entity.getAudit().getUpdatedAt(),
                user == null ? null : user.getId(),
                entity.getId());

        if (rowAffected != 1) {
            throw new ResourceNotFoundException("tag id =" + entity.getId() + " is not found");
        }
        return entity;
    }

    private final Map<String, String> orderColMap = Map.of(
            "id", "t.id",
            "name", "t.name",
            "productCount", "product_count",
            "createdAt", "t.created_at",
            "updatedAt", "t.updated_at");

    public Page<TagAdminDTO> findAllAdmin(TagFilter filter, Pageable pageable) {

        List<Object> params = new ArrayList<>();

        String where = buildAdminWhereClause(filter, params);
        String having = buildAdminHavingClause(filter, params);
        String order = buildOrderClause(pageable);

        StringBuilder mainQuery = new StringBuilder(
                "SELECT t.id, t.name, t.created_at, t.updated_at, COUNT(p.products_id) AS product_count "
                        + "FROM tag AS t "
                        + "LEFT JOIN product_tags AS p ON t.id = p.tags_id ");
        if (StringUtils.hasText(where)) {
            mainQuery.append("WHERE ").append(where).append(" ");
        }

        mainQuery.append("GROUP BY t.id, t.name, t.created_at, t.updated_at ");
        if (StringUtils.hasText(having)) {
            mainQuery.append("HAVING ").append(having).append(" ");
        }
        if (StringUtils.hasText(order)) {
            mainQuery.append("ORDER BY ").append(order).append(" ");
        }
        mainQuery.append("LIMIT ? OFFSET ? ");

        List<Object> countParams = new ArrayList<>(params);
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        String countQuery = buildAdminCountQuery(where, having);

        Long count = jdbcTemplate.queryForObject(countQuery, Long.class, countParams.toArray());

        List<TagAdminDTO> tagAdminDTOs = jdbcTemplate.query(mainQuery.toString(), (rs, i) -> toTagAdminDTO(rs),
                params.toArray());

        return new PageImpl<>(tagAdminDTOs, pageable, count == null ? 0L : count);
    }

    private String buildAdminCountQuery(String whereClauses, String havingClauses) {
        StringBuilder sb = new StringBuilder();
        if (havingClauses.isEmpty()) {
            sb.append("SELECT COUNT(*) FROM tag t ");
            if (!whereClauses.isEmpty()) {
                sb.append("WHERE ").append(whereClauses);
            }
        } else {
            sb.append("WITH main_query AS ( ");
            sb.append("SELECT t.id  FROM tag t LEFT JOIN product_tags p ON p.tags_id = t.id ");
            if (!whereClauses.isEmpty()) {
                sb.append("WHERE ").append(whereClauses).append(" ");
            }
            sb.append("GROUP BY t.id ");
            sb.append("HAVING ").append(havingClauses).append(" ");
            sb.append(") SELECT COUNT(*) FROM main_query");
        }
        return sb.toString();
    }

    private String buildOrderClause(Pageable pageable) {
        List<String> clauses = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            String col = order.getProperty();
            if (orderColMap.containsKey(col)) {
                clauses.add(orderColMap.get(col) + " " + order.getDirection().name());
            }
        });
        return String.join(", ", clauses);
    }

    private String buildAdminHavingClause(TagFilter filter, List<Object> params) {
        List<String> clauses = new ArrayList<>();
        if (filter.getMinProductCount() != null) {
            clauses.add("COUNT(p.products_id) >= ?");
            params.add(filter.getMinProductCount());
        }
        if (filter.getMaxProductCount() != null) {
            clauses.add("COUNT(p.products_id) <= ?");
            params.add(filter.getMaxProductCount());
        }
        return String.join(" AND ", clauses);
    }

    private String buildAdminWhereClause(TagFilter filter, List<Object> params) {
        List<String> clauses = new ArrayList<>();
        if (filter.getCreatedFrom() != null) {
            clauses.add("t.created_at >= ?");
            params.add(filter.getCreatedFrom());
        }
        if (filter.getUpdatedFrom() != null) {
            clauses.add("t.updated_at >= ?");
            params.add(filter.getUpdatedFrom());
        }
        if (StringUtils.hasText(filter.getName())) {
            clauses.add("t.name LIKE ?");
            params.add("%" + filter.getName() + "%");
        }
        return String.join(" AND ", clauses);
    }

    private User buildAuthUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
            User user = new User();
            user.setId(userDetail.getUserId());
            return user;
        }
        return null;
    }

    private TagAdminDTO toTagAdminDTO(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("name");
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        long productCount = rs.getLong("product_count");

        LocalDateTime createdAt = createdAtTs == null ? null : createdAtTs.toLocalDateTime();
        LocalDateTime updatedAt = updatedAtTs == null ? null : updatedAtTs.toLocalDateTime();

        return new TagAdminDTO(id, name, createdAt, updatedAt, productCount);
    }

    private Tag toTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        Audit audit = new Audit();
        tag.setAudit(audit);

        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        tag.setDescription(rs.getString("description"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            audit.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            audit.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        Long createdById = rs.getLong("created_by_id");
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(createdById);
            audit.setCreatedBy(user);
        }

        Long updatedById = rs.getLong("updated_by_id");
        if (!rs.wasNull()) {
            User user = new User();
            user.setId(updatedById);
            audit.setUpdatedBy(user);
        }

        return tag;
    }

}
