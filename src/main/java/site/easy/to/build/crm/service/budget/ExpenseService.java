package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.api.dto.CustomerExpenseDTO;
import site.easy.to.build.crm.api.dto.ExpenseTypeDTO;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> findByCustomerId(int customerId) {
        return expenseRepository.findByCustomerId(customerId);
    }

    public double findTotalExpenseByCustomerId(int customerId) {
        return expenseRepository.findSumAmountByCustomerId(customerId);
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> findAllLeadsExpenses() {
        return expenseRepository.findAllLeadsExpenses();
    }

    public List<Expense> findAllTicketsExpenses() {
        return expenseRepository.findAllTicketsExpenses();
    }

    public Expense findById(int id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public void deleteById(int expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public double findTotalLeadExpense() {
        return expenseRepository.findSumAmountLead();
    }

    public double findTotalTicketExpense() {
        return expenseRepository.findSumAmountTicket();
    }

    public Expense updateAmountById(int expenseId, double newAmount) {
        Expense expense = findById(expenseId);
        expense.setAmount(newAmount);
        return expenseRepository.save(expense);
    }

    public List<ExpenseTypeDTO> findExpensesPerType() {
        List<ExpenseTypeDTO> types = new ArrayList<>();
        types.add(
                new ExpenseTypeDTO(
                        "Tickets",
                        findTotalTicketExpense()
                )
        );
        types.add(
                new ExpenseTypeDTO(
                        "Leads",
                        findTotalLeadExpense()
                )
        );
        return types;
    }

    public List<CustomerExpenseDTO> findExpensesPerCustomer() {
        return expenseRepository.findExpensesByCustomer()
                .stream()
                .map(expense -> new CustomerExpenseDTO(
                        (String) expense[0],
                        ((BigDecimal) expense[1]).doubleValue(),
                        ((BigDecimal) expense[2]).doubleValue()
                ))
                .toList();
    }

    public Expense findByTicketId(int ticketId) {
        return expenseRepository.findByTicketId(ticketId);
    }
}
