package site.easy.to.build.crm.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import site.easy.to.build.crm.api.POV;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    @JsonView(POV.Budget.class)
    private Integer id;

    @Column(name = "budget_name")
    @JsonView(POV.Budget.class)
    private String name;

    @Column(name = "amount")
    @JsonView(POV.Budget.class)
    private Double amount;

    @Column(name = "start_date")
    @JsonView(POV.Budget.class)
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonView(POV.Budget.class)
    private LocalDate endDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonView(POV.Budget.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    @JsonView(POV.Budget.class)
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonView(POV.Budget.class)
    private Customer customer;

}
