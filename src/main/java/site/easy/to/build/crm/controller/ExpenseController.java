package site.easy.to.build.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Numbers;
import site.easy.to.build.crm.dto.LeadExpenseForm;
import site.easy.to.build.crm.dto.TicketExpenseForm;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.lead.LeadServiceImpl;
import site.easy.to.build.crm.service.ticket.TicketServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final TicketServiceImpl ticketServiceImpl;
    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final CustomerServiceImpl customerServiceImpl;
    private final LeadServiceImpl leadServiceImpl;
    private final Numbers numbers = new Numbers(Locale.FRANCE);

    @GetMapping("/ticket/{ticketId}/create")
    public String showTicketExpenseForm(@PathVariable("ticketId") int ticketId, Model model) {
        Ticket ticket = ticketServiceImpl.findByTicketId(ticketId);
        if (ticket == null) {
            return "error/not-found";
        }

        model.addAttribute("expense", new TicketExpenseForm(ticketId));

        return "expense/create-ticket-expense";
    }

    @PostMapping("/ticket/save")
    public String saveTicketExpense(@ModelAttribute("expense") @Valid TicketExpenseForm ticketExpenseForm, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketServiceImpl.findByTicketId(ticketExpenseForm.getTicketId());
        if (ticket == null) {
            return "error/not-found";
        }

        if (bindingResult.hasErrors()) {
            return "expense/create-ticket-expense";
        }

        int customerId = ticket.getCustomer().getCustomerId();
        if (ticketExpenseForm.getConfirm() == null && budgetService.isBudgetExceeded(customerId, ticketExpenseForm.getAmount())) {
            double totalCustomerBudget = budgetService.findTotalBudgetByCustomerId(customerId);
            model.addAttribute("expense", ticketExpenseForm);
            model.addAttribute("confirm",
                    "The budget limit " + numbers.formatDecimal(totalCustomerBudget, 1, "COMMA", 2, "POINT") + " will be exceeded if you confirm this expense."
            );
            return "expense/create-ticket-expense";
        }

        // Create an expense from the form
        Expense expense = new Expense(
                ticketExpenseForm.getAmount(),
                ticketExpenseForm.getDescription(),
                ticket
        );
        expenseService.save(expense);

        budgetService.warnIfThresholdReached(redirectAttributes, customerId);

        return "redirect:/expenses/customer/" + ticket.getCustomer().getCustomerId();
    }

    @GetMapping("/lead/{leadId}/create")
    public String showLeadExpenseForm(@PathVariable("leadId") int leadId, Model model) {
        Lead lead = leadServiceImpl.findByLeadId(leadId);
        if (lead == null) {
            return "error/not-found";
        }

        model.addAttribute("expense", new LeadExpenseForm(leadId));

        return "expense/create-lead-expense";
    }

    @PostMapping("/lead/save")
    public String saveLeadExpense(@ModelAttribute("expense") @Valid LeadExpenseForm leadExpenseForm, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Lead lead = leadServiceImpl.findByLeadId(leadExpenseForm.getLeadId());
        if (lead == null) {
            return "error/not-found";
        }

        if (bindingResult.hasErrors()) {
            return "expense/create-lead-expense";
        }

        int customerId = lead.getCustomer().getCustomerId();
        if (leadExpenseForm.getConfirm() == null && budgetService.isBudgetExceeded(customerId, leadExpenseForm.getAmount())) {
            double totalCustomerBudget = budgetService.findTotalBudgetByCustomerId(customerId);
            model.addAttribute("expense", leadExpenseForm);
            model.addAttribute("confirm",
                    "The budget limit " + numbers.formatDecimal(totalCustomerBudget, 1, "COMMA", 2, "POINT") + " will be exceeded if you confirm this expense."
            );
            return "expense/create-lead-expense";
        }

        // Create an expense from the form
        Expense expense = new Expense(
                leadExpenseForm.getAmount(),
                leadExpenseForm.getDescription(),
                lead
        );
        expenseService.save(expense);

        budgetService.warnIfThresholdReached(redirectAttributes, customerId);

        return "redirect:/expenses/customer/" + lead.getCustomer().getCustomerId();
    }

    @GetMapping("/customer/{customerId}")
    public String showCustomerExpenses(@PathVariable("customerId") int customerId, Model model) {
        Customer customer = customerServiceImpl.findByCustomerId(customerId);
        if (customer == null) {
            return "error/not-found";
        }

        double customerTotalExpense = expenseService.findTotalExpenseByCustomerId(customerId);
        List<Expense> expenses = expenseService.findByCustomerId(customerId);

        model.addAttribute("customerTotalExpense", customerTotalExpense);
        model.addAttribute("expenses", expenses);
        model.addAttribute("customer", customer);

        return "expense/customer-expenses";
    }

}
