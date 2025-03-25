package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.repository.ExpenseRepository;

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

    public double findTotalExpenseByBudgetId(int budgetId) {
        return expenseRepository.findSumAmountByBudgetId(budgetId);
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
}
