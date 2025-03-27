package site.easy.to.build.crm.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.api.*;
import site.easy.to.build.crm.api.dto.DashboardDTO;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseApiController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final TicketServiceImpl ticketServiceImpl;
    private final LeadServiceImpl leadServiceImpl;

    @GetMapping("/dashboard")
    @JsonView({POV.Dashboard.class})
    public DashboardDTO findDashboardData() {
        return new DashboardDTO(
                budgetService.findTotalCustomerBudget(),
                expenseService.findTotalLeadExpense(),
                expenseService.findTotalTicketExpense(),
                expenseService.findExpensesPerType(),
                expenseService.findExpensesPerCustomer(),
                expenseService.findAll(),
                expenseService.findAllLeadsExpenses(),
                expenseService.findAllTicketsExpenses()
        );
    }

    @GetMapping
    @JsonView({POV.Expense.class})
    public List<Expense> findAll() {
        return expenseService.findAll();
    }

    @GetMapping("/tickets")
    @JsonView({POV.TicketExpense.class})
    public List<Expense> findAllTickets() {
        return expenseService.findAllTicketsExpenses();
    }

    @GetMapping("/leads")
    @JsonView({POV.LeadExpense.class})
    public List<Expense> findAllLeads() {
        return expenseService.findAllLeadsExpenses();
    }

    @GetMapping("/{id}")
    @JsonView({POV.Expense.class})
    public Expense findById(@PathVariable int id) throws ApiServerException {
        return expenseService.findById(id);
    }

    @GetMapping("/by-client")
    @JsonView({POV.Expense.class})
    public List<Expense> findByClient(@RequestParam int clientId) {
        return expenseService.findByCustomerId(clientId);
    }

    @PutMapping("/{id}")
    @JsonView({POV.Expense.class})
    public ResponseEntity<ApiResponse<?>> update(@PathVariable(name = "id") int expenseId, @RequestParam(name = "newAmount") double newAmount) {
        ApiResponse<?> response;
        Expense expense = expenseService.updateAmountById(expenseId, newAmount);
        response = new ApiOkResponse<>("Expense amount updated", expense);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable(name = "id") int expenseId) {
        ApiResponse<?> response;
        Integer ticketId = ticketServiceImpl.getTicketIdByExpenseId(expenseId);
        Integer leadId = leadServiceImpl.getLeadIdByExpenseId(expenseId);
        expenseService.deleteById(expenseId);
        if (ticketId != null) {
            ticketServiceImpl.deleteById(ticketId);
        }
        if (leadId != null) {
            leadServiceImpl.deleteBydId(leadId);
        }
        response = new ApiOkResponse<>("Expense deleted", expenseId);
        return ResponseEntity.ok(response);
    }

}
