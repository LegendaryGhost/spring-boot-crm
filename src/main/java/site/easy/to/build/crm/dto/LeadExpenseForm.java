package site.easy.to.build.crm.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class LeadExpenseForm {

    private int leadId;

    @Min(value = 0, message = "The amount should be superior to 0")
    private double amount;

    private String description;

    private int budgetId;

    private String confirm;

    public LeadExpenseForm(int leadId) {
        this.leadId = leadId;
    }

}
