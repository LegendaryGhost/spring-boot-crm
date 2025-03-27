package site.easy.to.build.crm.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class TicketExpenseForm {

    private int ticketId;

    @Min(value = 0, message = "The amount should be superior to 0")
    private double amount;

    private String description;

    private String confirm;

    public TicketExpenseForm(int ticketId) {
        this.ticketId = ticketId;
    }

}
