package site.easy.to.build.crm.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.api.ApiOkResponse;
import site.easy.to.build.crm.api.ApiResponse;
import site.easy.to.build.crm.api.POV;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.service.budget.BudgetService;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetApiController {

    private final BudgetService budgetService;

    @GetMapping
    @JsonView(POV.Budget.class)
    public List<Budget> budgets() {
        return budgetService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable(name = "id") int budgetId) {
        budgetService.deleteBudgetAndExpensesByBudgetId(budgetId);
        ApiResponse<?> response = new ApiOkResponse<>("Budget and related expenses deleted", budgetId);
        return ResponseEntity.ok(response);
    }

}
