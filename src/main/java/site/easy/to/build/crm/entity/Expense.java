package site.easy.to.build.crm.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import site.easy.to.build.crm.api.POV;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private Integer id;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Min amount is 0")
    @Column(name = "amount")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private Double amount;

    @Column(name = "description")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class})
    private String description;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    @JsonView({POV.LeadExpense.class, POV.Expense.class})
    private Lead lead;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonView({POV.TicketExpense.class, POV.Expense.class})
    private Ticket ticket;

    @ManyToOne(optional = false)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    // Validation personnalisée pour garantir lead OU ticket
    @AssertTrue
    private boolean isValid() {
        return (lead != null) ^ (ticket != null);
    }

}
