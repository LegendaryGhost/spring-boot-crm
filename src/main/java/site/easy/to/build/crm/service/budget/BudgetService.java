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

    public void warnIfThresholdReached(RedirectAttributes redirectAttributes, Budget budget) {
        // Show warning if the budget expense threshold was reached
        double totalBudgetExpenses = expenseService.findTotalExpenseByBudgetId(budget.getId());
        double threshold = configurationService.getExpenseThreshold();
        double thresholdAmount = budget.getAmount() * threshold / 100;
        if (totalBudgetExpenses >= thresholdAmount) {
            redirectAttributes.addFlashAttribute("warning",
                    "The " + threshold + "% (" +
                    numbers.formatDecimal(thresholdAmount, 1, "COMMA", 2, "POINT") + ") threshold was reached on the budget " + budget.getName()
            );
        }
    }
}
