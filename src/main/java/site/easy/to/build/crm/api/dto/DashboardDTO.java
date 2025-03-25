package site.easy.to.build.crm.api.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import site.easy.to.build.crm.api.POV;
import site.easy.to.build.crm.entity.Expense;

import java.util.List;

@Data
@AllArgsConstructor
@JsonView(POV.Dashboard.class)
public class DashboardDTO {

    private double totalClientBudget;
    private double totalLeadExpense;
    private double totalTicketExpense;
    private List<ExpenseTypeDTO> expenseTypes;
    private List<CustomerExpenseDTO> customerExpenses;
    private List<Expense> all;
    private List<Expense> lead;
    private List<Expense> ticket;

}
