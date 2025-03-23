package site.easy.to.build.crm.controller.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.service.budget.BudgetService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/budgets")
public class BudgetAPIController {

    private final BudgetService budgetService;

    @GetMapping("/customer/{customerId}")
    public List<Budget> findBudgetByCustomerId(@PathVariable("customerId") int customerId) {
        return budgetService.findByCustomerId(customerId);
    }

}
