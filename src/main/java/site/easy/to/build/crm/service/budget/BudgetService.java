package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.repository.BudgetRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

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
}
