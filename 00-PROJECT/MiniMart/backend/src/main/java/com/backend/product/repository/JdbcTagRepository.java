package com.backend.product.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.backend.common.utils.Utils;
import com.backend.product.dto.req.TagFilter;
import com.backend.product.dto.res.TagAdminDTO;
import com.backend.product.model.Tag;
import com.backend.user.model.User;
import com.backend.user.security.CustomUserDetail;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JdbcTagRepository {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Tag> findById(long id) {
        String sql = "SELECT * FROM tag WHERE id= ?";
        List<Tag> tags = jdbcTemplate.query(sql, (rs, i) -> toTag(rs), id);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags.get(0));
    }

    public int deleteById(long id) {
        String sqlManyToMany = "DELETE FROM product_tags WHERE tags_id = ?";
        String sql = "DELETE FROM tag WHERE id = ?";
        jdbcTemplate.update(sqlManyToMany, id);
        return jdbcTemplate.update(sql, id);
    }

    public Tag save(Tag entity) {
        return entity.getId() == null ? create(entity) : update(entity);
    }

    private Tag create(Tag entity) {
        if (entity.getAudit() == null) {
            entity.setAudit(new Audit());
        }
        LocalDateTime now = LocalDateTime.now();
        entity.getAudit().setCreatedAt(now);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long createdById = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
            createdById = userDetail.getUserId();
            User user = new User();
            user.setId(createdById);
            entity.getAudit().setCreatedBy(user);
        }
        Long id = jdbcTemplate.queryForObject("select nextval('tag_seq')", Long.class);
        if (id == null) {
            throw new IllegalStateException("Cannot generate id from tag_seq");
        }
        entity.setId(id);

        String sql = "INSERT INTO tag(id, name, description, created_at, created_by_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, id, entity.getName(), entity.getDescription(),
                Timestamp.valueOf(entity.getAudit().getCreatedAt()), createdById);

        return entity;
    }

    private Tag update(Tag entity) {
        if (entity.getAudit() == null) {
            entity.setAudit(new Audit());
        }
        entity.getAudit().setUpdatedAt(LocalDateTime.now());

        Long updatedById = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetail userDetail) {
            updatedById = userDetail.getUserId();
            User user = new User();
            user.setId(updatedById);
            entity.getAudit().setUpdatedBy(user);
        }

        String sql = "UPDATE tag SET name = ?, description = ?, updated_at = ?, updated_by_id = ? WHERE id = ?";

        int rowAffected = jdbcTemplate.update(sql,
                entity.getName(),
                entity.getDescription(),
                Timestamp.valueOf(entity.getAudit().getUpdatedAt()),
                updatedById,
                entity.getId());

        if (rowAffected != 1) {
            throw new ResourceNotFoundException("tag id =" + entity.getId() + " is not found");
        }
        return entity;
    }

    public Page<TagAdminDTO> findAllAdmin(TagFilter filter, Pageable pageable) {

        List<Object> params = new ArrayList<>();

        String whereClauses = buildAdminWhereClause(filter, params);
        String havingClauses = buildAdminHavingClause(filter, params);
        String orderClauses = buildOrderClause(pageable);

        StringBuilder mainQuery = new StringBuilder(
                "SELECT t.id, t.name, t.created_at, t.updated_at, COALESCE(COUNT(p.id), 0) AS product_count "
                        + "FROM tag AS t "
                        + "LEFT JOIN product_tags AS p ON t.id = p.tags_id ");
        if (!whereClauses.isEmpty()) {
            mainQuery.append("WHERE ").append(whereClauses).append(" ");
        }

        mainQuery.append("GROUP BY t.id, t.name, t.created_at, t.updated_at ");
        if (!havingClauses.isEmpty()) {
            mainQuery.append("HAVING ").append(havingClauses).append(" ");
        }
        if (!orderClauses.isEmpty()) {
            mainQuery.append("ORDER BY ").append(orderClauses).append(" ");
        }
        mainQuery.append("LIMIT ? OFFSET ? ");
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<Object> countParams = new ArrayList<>(params.subList(0, params.size() - 2));

        String countQuery = buildAdminCountQuery(whereClauses, havingClauses);

        Long count = jdbcTemplate.queryForObject(countQuery, Long.class, countParams.toArray());

        List<TagAdminDTO> tagAdminDTOs = jdbcTemplate.query(mainQuery.toString(), (rs, i) -> toTagAdminDTO(rs),
                params.toArray());

        return new PageImpl<>(tagAdminDTOs, pageable, count == null ? 0L : count);
    }

    private String buildAdminCountQuery(String whereClauses, String havingClauses) {
        StringBuilder countQuery = new StringBuilder();
        if (havingClauses.isEmpty()) {
            countQuery.append("SELECT COUNT(*) FROM tag t ");
            if (!whereClauses.isEmpty()) {
                countQuery.append("WHERE ").append(whereClauses);
            }
        } else {
            countQuery.append("WITH main_query AS ( ");
            countQuery.append("SELECT t.id  FROM tag t LEFT JOIN product_tags p ON p.tags_id = t.id ");
            if (!whereClauses.isEmpty()) {
                countQuery.append("WHERE ").append(whereClauses).append(" ");
            }
            countQuery.append("GROUP BY t.id ");
            countQuery.append("HAVING ").append(havingClauses).append(" ");
            countQuery.append(") SELECT COUNT(*) FROM main_query");
        }
        return countQuery.toString();
    }

    private String buildOrderClause(Pageable pageable) {
        List<String> orderClauses = new ArrayList<>();
        pageable.getSort().forEach(
                order -> orderClauses.add(Utils.camelToSnake(order.getProperty()) + " " + order.getDirection().name()));
        return String.join(", ", orderClauses);
    }

    private String buildAdminHavingClause(TagFilter filter, List<Object> params) {
        List<String> havingClauses = new ArrayList<>();
        if (filter.getMinProductCount() != null) {
            havingClauses.add("COUNT(p.id) >= ?");
            params.add(filter.getMinProductCount());
        }
        if (filter.getMaxProductCount() != null) {
            havingClauses.add("COUNT(p.id) <= ?");
            params.add(filter.getMaxProductCount());
        }
        return String.join(" AND ", havingClauses);
    }

    private String buildAdminWhereClause(TagFilter filter, List<Object> params) {
        List<String> whereClauses = new ArrayList<>();
        if (filter.getCreatedFrom() != null) {
            whereClauses.add("t.created_at >= ?");
            params.add(filter.getCreatedFrom());
        }
        if (filter.getUpdatedFrom() != null) {
            whereClauses.add("t.updated_at >= ?");
            params.add(filter.getUpdatedFrom());
        }
        if (StringUtils.hasText(filter.getName())) {
            whereClauses.add("t.name LIKE ?");
            params.add("%" + filter.getName() + "%");
        }
        return String.join(" AND ", whereClauses);
    }

    private TagAdminDTO toTagAdminDTO(ResultSet rs) throws SQLException {
        long id = rs.getLong(1);
        String name = rs.getString(2);
        Timestamp createdAtTs = rs.getTimestamp(3);
        Timestamp updatedAtTs = rs.getTimestamp(4);
        long productCount = rs.getLong(5);

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
