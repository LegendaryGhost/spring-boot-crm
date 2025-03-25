package site.easy.to.build.crm.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.easy.to.build.crm.api.POV;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private Integer id;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 0, message = "Min amount is 0")
    @Column(name = "amount")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private Double amount;

    @Column(name = "description")
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class})
    private String description;

    @Column(name = "created_at", insertable = false, updatable = false)
    @JsonView({POV.TicketExpense.class, POV.LeadExpense.class, POV.Expense.class, POV.Dashboard.class})
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "lead_id")
    @JsonView({POV.LeadExpense.class, POV.Expense.class})
    private Lead lead;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    @JsonView({POV.TicketExpense.class, POV.Expense.class})
    private Ticket ticket;

    // Validation personnalisée pour garantir lead OU ticket
    @AssertTrue
    private boolean isValid() {
        return (lead != null) ^ (ticket != null);
    }

    public Expense(Double amount, String description, Ticket ticket) {
        this.amount = amount;
        this.description = description;
        this.ticket = ticket;
    }

    public Expense(Double amount, String description, Lead lead) {
        this.amount = amount;
        this.description = description;
        this.lead = lead;
    }
}
