package site.easy.to.build.crm.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.api.*;
import site.easy.to.build.crm.api.dto.DashboardDTO;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseApiController {

    private final ExpenseService expenseService;
    private final BudgetService budgetService;
//    private final BudgetAlertConfigService budgetAlertConfigService;

    @GetMapping("/dashboard")
    @JsonView({POV.Dashboard.class})
    public DashboardDTO findDashboardData() {
        return new DashboardDTO(
                budgetService.findTotalCustomerBudget(),
                expenseService.findTotalLeadExpense(),
                expenseService.findTotalTicketExpense(),
                expenseService.findAll(),
                expenseService.findAllLeadsExpenses(),
                expenseService.findAllTicketsExpenses()
        );
    }

    @GetMapping
    @JsonView({POV.Expense.class})
    public List<Expense> findAll() {
        return expenseService.findAll();
    }

    @GetMapping("/tickets")
    @JsonView({POV.Expense.class})
    public List<Expense> findAllTickets() {
        return expenseService.findAllTicketsExpenses();
    }

    @GetMapping("/leads")
    @JsonView({POV.Expense.class})
    public List<Expense> findAllLeads() {
        return expenseService.findAllLeadsExpenses();
    }

    @GetMapping("/{id}")
    @JsonView({POV.Expense.class})
    public Expense findById(@PathVariable int id) throws ApiServerException {
        return expenseService.findById(id);
    }

    @GetMapping("/by-client")
    @JsonView({POV.Expense.class})
    public List<Expense> findByClient(@RequestParam int clientId) {
        return expenseService.findByCustomerId(clientId);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<?>> update(@PathVariable(name = "id") int expenseId, @RequestParam double newAmount) {
//        ApiResponse<?> response;
//        try {
//            HashMap<String, Object> map = expenseService.updateById(expenseId, newAmount);
//            Budget budget = (Budget) map.get("budget");
//            double newBudgetRemain = budget.getAmountRemain();
//
//            BudgetAlertConfig bac = budgetAlertConfigService.findCurrent();
//            double alerte = budget.getAmountLimit() * (bac.getRate() / 100);
//
//            List<String> messages = new ArrayList<>();
//            messages.add("Modification du budget '" + budget.getName() + "' par la mise a jour de l'expense '" + expenseId + "'");
//            if (alerte <= newBudgetRemain) {
//                messages.add("Seuil d'alerte de depense atteint pour le budget! seuil: " + alerte + " | reste: " + newBudgetRemain);
//            }
//            if (newBudgetRemain < 0) {
//                messages.add("Depassement de budget! reste: " + newBudgetRemain);
//            }
//
//            response = new ApiOkResponse<>("Data mis a jour", messages);
//            return ResponseEntity.ok(response);
//        } catch (ApiServerException e) {
//            response = new ApiBadResponse<>(e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable(name = "id") int expenseId) {
        ApiResponse<?> response;
        expenseService.deleteById(expenseId);
        response = new ApiOkResponse<>("Data efface", expenseId);
        return ResponseEntity.ok(response);
    }
}
