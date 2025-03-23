package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.dto.TicketExpenseForm;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final TicketServiceImpl ticketServiceImpl;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final CustomerServiceImpl customerServiceImpl;

    @GetMapping("/ticket/{ticketId}/create")
    public String showExpenseForm(@PathVariable("ticketId") int ticketId, Model model) {
        Ticket ticket = ticketServiceImpl.findByTicketId(ticketId);

        if (ticket == null) {
            return "error/not-found";
        }

        List<Budget> budgets = budgetService.findByCustomerId(ticket.getCustomer().getCustomerId());

        model.addAttribute("budgets", budgets);
        model.addAttribute("expense", new TicketExpenseForm(ticketId));

        return "expense/create-expense";
    }

    @PostMapping("/ticket/save")
    public String saveTicketExpense(@ModelAttribute("expense") @Valid TicketExpenseForm ticketExpenseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "expense/create-expense";
        }

        Ticket ticket = ticketServiceImpl.findByTicketId(ticketExpenseForm.getTicketId());
        if (ticket == null) {
            return "error/not-found";
        }

        Budget budget = budgetService.findById(ticketExpenseForm.getBudgetId());
        if (budget == null) {
            return "error/not-found";
        }

        // Create an expense from the form
        Expense expense = new Expense();
        expense.setTicket(ticket);
        expense.setBudget(budget);
        expense.setAmount(ticketExpenseForm.getAmount());
        expense.setDescription(ticketExpenseForm.getDescription());
        expense.setExpenseDate(ticket.getCreatedAt().toLocalDate());
        expenseService.save(expense);
        return "redirect:/expenses/customer/" + ticket.getCustomer().getCustomerId();
    }

    @GetMapping("/customer/{customerId}")
    public String showCustomerExpenses(@PathVariable("customerId") int customerId, Model model) {
        Customer customer = customerServiceImpl.findByCustomerId(customerId);
        if (customer == null) {
            return "error/not-found";
        }

        double customerTotalExpense = expenseService.findTotalExpenseByCustomerId(customerId);
        List<Expense> expenses = expenseService.findByCutomerId(customerId);

        model.addAttribute("customerTotalExpense", customerTotalExpense);
        model.addAttribute("expenses", expenses);
        model.addAttribute("customer", customer);

        return "expense/customer-expenses";
    }

}
