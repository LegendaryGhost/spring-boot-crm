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

    public List<Expense> findByCutomerId(int cutomerId) {
        return expenseRepository.findByCustomerId(cutomerId);
    }

    public double findTotalExpenseByCustomerId(int customerId) {
        return expenseRepository.findSumAmountByCustomerId(customerId);
    }

    public double findTotalExpenseByBudgetId(int budgetId) {
        return expenseRepository.findSumAmountByBudgetId(budgetId);
    }

}
