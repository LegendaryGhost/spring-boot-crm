package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.dto.LeadExpenseForm;
import site.easy.to.build.crm.dto.TicketExpenseForm;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.configuration.ConfigurationService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
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
    private final LeadServiceImpl leadServiceImpl;
    private final ConfigurationService configurationService;

    @GetMapping("/ticket/{ticketId}/create")
    public String showTicketExpenseForm(@PathVariable("ticketId") int ticketId, Model model) {
        Ticket ticket = ticketServiceImpl.findByTicketId(ticketId);

        if (ticket == null) {
            return "error/not-found";
        }

        List<Budget> budgets = budgetService.findByCustomerId(ticket.getCustomer().getCustomerId());

        model.addAttribute("budgets", budgets);
        model.addAttribute("expense", new TicketExpenseForm(ticketId));

        return "expense/create-ticket-expense";
    }

    @PostMapping("/ticket/save")
    public String saveTicketExpense(@ModelAttribute("expense") @Valid TicketExpenseForm ticketExpenseForm, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Ticket ticket = ticketServiceImpl.findByTicketId(ticketExpenseForm.getTicketId());
            List<Budget> budgets = budgetService.findByCustomerId(ticket.getCustomer().getCustomerId());
            model.addAttribute("budgets", budgets);
            return "expense/create-ticket-expense";
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

        budgetService.warnIfThresholdReached(redirectAttributes, budget);

        return "redirect:/expenses/customer/" + ticket.getCustomer().getCustomerId();
    }

    @GetMapping("/lead/{leadId}/create")
    public String showLeadExpenseForm(@PathVariable("leadId") int leadId, Model model) {
        Lead lead = leadServiceImpl.findByLeadId(leadId);

        if (lead == null) {
            return "error/not-found";
        }

        List<Budget> budgets = budgetService.findByCustomerId(lead.getCustomer().getCustomerId());

        model.addAttribute("budgets", budgets);
        model.addAttribute("expense", new LeadExpenseForm(leadId));

        return "expense/create-lead-expense";
    }

    @PostMapping("/lead/save")
    public String saveLeadExpense(@ModelAttribute("expense") @Valid LeadExpenseForm leadExpenseForm, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Lead lead = leadServiceImpl.findByLeadId(leadExpenseForm.getLeadId());
            List<Budget> budgets = budgetService.findByCustomerId(lead.getCustomer().getCustomerId());
            model.addAttribute("budgets", budgets);
            return "expense/create-lead-expense";
        }

        Lead lead = leadServiceImpl.findByLeadId(leadExpenseForm.getLeadId());
        if (lead == null) {
            return "error/not-found";
        }

        Budget budget = budgetService.findById(leadExpenseForm.getBudgetId());
        if (budget == null) {
            return "error/not-found";
        }

        // Create an expense from the form
        Expense expense = new Expense();
        expense.setLead(lead);
        expense.setBudget(budget);
        expense.setAmount(leadExpenseForm.getAmount());
        expense.setDescription(leadExpenseForm.getDescription());
        expense.setExpenseDate(lead.getCreatedAt().toLocalDate());
        expenseService.save(expense);

        budgetService.warnIfThresholdReached(redirectAttributes, budget);

        return "redirect:/expenses/customer/" + lead.getCustomer().getCustomerId();
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
