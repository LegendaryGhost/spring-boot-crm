package site.easy.to.build.crm.service.data;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@AllArgsConstructor
@Service
public class DuplicationService {


    public Resource generateCustomerCsvFile(Customer customer, List<Budget> budgets, List<Ticket> tickets, List<Lead> leads) throws IOException {
        String headers = "username,email,country,phone,type,subject_name,amount,status,description_phone";
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

        Path filePath = Paths.get("customer.csv");
        if (!filePath.toFile().exists()) {
            Files.createFile(filePath);
        }
        Files.write(filePath, content.toString().getBytes());
        return new UrlResource(filePath.toUri());
    }

    private String getTicketCsvString(Ticket ticket) {
        return "\"ticket\",\"" + ticket.getSubject() + "\"," + ticket.getExpense().getAmount() + ",\"" + ticket.getStatus() + "\"," + ticket.getDescription();
    }

    private String getLeadCsvString(Lead lead) {
        return "\"lead\",\"" + lead.getName() + "\"," + lead.getExpense().getAmount() + ",\"" + lead.getStatus() + "\"," + lead.getPhone();
    }

    private String getBudgetCsvString(Budget budget) {
        return "\"budget\",\"" + budget.getName() + "\"," + budget.getAmount() + ",\"none\"";
    }

    private String getCustomerCsvString(Customer customer) {
        return "\"" + customer.getName() + " copy\",\"copy_" + customer.getEmail() + "\"," + "\"" + customer.getCountry() + "\",\"" + customer.getPhone() + "\"";
    }
}
