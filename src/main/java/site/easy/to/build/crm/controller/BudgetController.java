package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;

@AllArgsConstructor
@Controller
@RequestMapping("/manager/budget")
public class BudgetController {

    private final BudgetService budgetService;
    private final CustomerServiceImpl customerServiceImpl;

    @GetMapping
    public String showBudgetList(Model model) {
        model.addAttribute("budgets", budgetService.findAll());
        return "budget/all-budgets";
    }

    @GetMapping("/create")
    public String showBudgetForm(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("customers", customerServiceImpl.findAll());
        return "budget/create-budget";
    }

    @PostMapping("/save")
    public String saveBudget(@ModelAttribute("budget") Budget budget) {
        budgetService.save(budget);
        return "redirect:/manager/budget";
    }

}
