package site.easy.to.build.crm.service.data;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;

import java.util.List;

@AllArgsConstructor
@Service
public class DuplicationService {


    public void generateCustomerCsvFile(Customer customer, List<Budget> budgets, List<Ticket> tickets, List<Lead> leads) {
        String headers = "username,email,type,subject,amount,status";
        String customerCsvString = getCustomerCsvString(customer);
        StringBuilder content = new StringBuilder(headers + "\n");

        for (Ticket ticket : tickets) {
            String ticketCsvString = getTicketCsvString(ticket);
            content.append(customerCsvString).append(",").append(ticketCsvString).append("\n");
        }

        for (Lead lead : leads) {
            String leadCsvString = getLeadCsvString(lead);
            content.append(customerCsvString).append(",").append(leadCsvString).append("\n");
        }

        for (Budget budget : budgets) {
            String budgetCsvString = getBudgetCsvString(budget);
            content.append(customerCsvString).append(",").append(budgetCsvString).append("\n");
        }

        System.out.println(content);
    }

    private String getTicketCsvString(Ticket ticket) {
        return "\"ticket\",\"" + ticket.getSubject() + "\"," + ticket.getExpense().getAmount() + ",\"" + ticket.getStatus() + "\"";
    }

    private String getLeadCsvString(Lead lead) {
        return "\"lead\",\"" + lead.getName() + "\"," + lead.getExpense().getAmount() + ",\"" + lead.getStatus() + "\"";
    }

    private String getBudgetCsvString(Budget budget) {
        return "\"budget\",\"" + budget.getName() + "\"," + budget.getAmount() + ",\"none\"";
    }

    private String getCustomerCsvString(Customer customer) {
        return "\"" + customer.getName() + " copy\",\"copy_" + customer.getEmail() + "\"";
    }
}
