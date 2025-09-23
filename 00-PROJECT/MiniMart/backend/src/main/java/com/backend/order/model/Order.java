package com.backend.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.common.model.Audit;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.user.model.Customer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = "Order.EntityUpdate", attributeNodes = {
        @NamedAttributeNode(value = "customer"),
})

@SqlResultSetMapping(name = "OrderAdminMapping", //
        classes = @ConstructorResult(targetClass = OrderAdminDTO.class, columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "paymentMethod", type = String.class),
                @ColumnResult(name = "shippingMethod", type = String.class),
                @ColumnResult(name = "totalPrice", type = BigDecimal.class),
                @ColumnResult(name = "totalCost", type = BigDecimal.class),
                @ColumnResult(name = "profit", type = BigDecimal.class),
                @ColumnResult(name = "customerId", type = Long.class),
                @ColumnResult(name = "createdAt", type = LocalDateTime.class),
                @ColumnResult(name = "updatedAt", type = LocalDateTime.class)
        }))
@NamedStoredProcedureQuery(name = Order.NamedProcedure_PAGE_ADMIN, //
        procedureName = "get_admin_orders_page", //
        resultSetMappings = "OrderAdminMapping", //
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "ref_orders", type = void.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "page_total", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_order_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_status", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_payment_method_name", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_shipping_method_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_customer_id", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_created_from", type = java.sql.Timestamp.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_created_to", type = java.sql.Timestamp.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_min_total_price", type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_max_total_price", type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_min_total_cost", type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_max_total_cost", type = BigDecimal.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sorts", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_page_size", type = Integer.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_page_offset", type = Integer.class)
        }

)

@Entity
@Getter
@Setter
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    public static final String NamedProcedure_PAGE_ADMIN = "get_admin_orders_page";

    @Id
    @GeneratedValue
    private Long id;

    // Snapshot
    @Column(precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    private String message;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    private ShippingMethod shippingMethod;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    @Embedded
    private Audit audit = new Audit();

    @ManyToOne
    private Customer customer;

    @Version
    private long version;

}
