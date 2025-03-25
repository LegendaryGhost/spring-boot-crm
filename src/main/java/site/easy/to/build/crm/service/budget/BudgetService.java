package site.easy.to.build.crm.service.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Numbers;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.service.configuration.ConfigurationService;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseService expenseService;
    private final ConfigurationService configurationService;
    private final Numbers numbers = new Numbers(Locale.FRANCE);

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public void save(Budget budget) {
        budgetRepository.save(budget);
    }

    public double findTotalBudgetByCustomerId(int customerId) {
        return budgetRepository.findSumAmountByCustomerId(customerId);
    }

    public List<Budget> findByCustomerId(int customerId) {
        return budgetRepository.findByCustomerId(customerId);
    }

    public Budget findById(int budgetId) {
        return budgetRepository.findById(budgetId).orElse(null);
    }

    /**
     // Show warning if the budget expense threshold was reached
     * @param redirectAttributes redirect attributes
     * @param customerId customer ID
     */
    public void warnIfThresholdReached(RedirectAttributes redirectAttributes, int customerId) {
        double totalCustomerExpense = expenseService.findTotalExpenseByCustomerId(customerId);
        double totalCustomerBudget = findTotalBudgetByCustomerId(customerId);
        double threshold = configurationService.getExpenseThreshold();
        double thresholdAmount = totalCustomerBudget * threshold / 100;
        if (totalCustomerExpense >= thresholdAmount) {
            redirectAttributes.addFlashAttribute("warning",
                    "The " + threshold + "% (" +
                    numbers.formatDecimal(thresholdAmount, 1, "COMMA", 2, "POINT") + ") threshold was reached for customer " + customerId
            );
        }
    }

    public boolean isBudgetExceeded(int customerId, double newExpense) {
        double totalCustomerBudget = budgetRepository.findSumAmountByCustomerId(customerId);
        double totalCustomerId = expenseService.findTotalExpenseByCustomerId(customerId);
        return totalCustomerId + newExpense > totalCustomerBudget;
    }

    public double findTotalCustomerBudget() {
        return budgetRepository.findSumAmount();
    }

    public void deleteById(int budgetId) {
        budgetRepository.deleteById(budgetId);
    }
}
