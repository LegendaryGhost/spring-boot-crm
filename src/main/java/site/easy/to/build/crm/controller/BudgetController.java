package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.easy.to.build.crm.service.budget.BudgetService;

@AllArgsConstructor
@Controller
@RequestMapping("/manager/budget")
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public String showBudgetList(Model model) {
        model.addAttribute("budgets", budgetService.findAll());
        return "budget/all-budgets";
    }

}
