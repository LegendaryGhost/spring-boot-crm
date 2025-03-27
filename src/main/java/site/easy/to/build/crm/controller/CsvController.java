package site.easy.to.build.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import site.easy.to.build.crm.csv.CsvValidationException;
import site.easy.to.build.crm.csv.dto.CsvErrorWrapper;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.budget.ExpenseService;
import site.easy.to.build.crm.service.customer.CustomerServiceImpl;
import site.easy.to.build.crm.service.user.UserServiceImpl;
import site.easy.to.build.crm.util.AuthenticationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/import/csv")
@RequiredArgsConstructor
public class CsvController {

    private final ExpenseService expenseService;
    private final UserServiceImpl userService;
    private final CustomerServiceImpl customerService;
    private final AuthenticationUtils authenticationUtils;
    private final BudgetService budgetService;
    private final PlatformTransactionManager transactionManager;

    @GetMapping("/all")
    public String showAll() {
        return "/data-management/csv";
    }

    @PostMapping("/all")
    public String all(
            @RequestParam("fileCustomer") MultipartFile fileCustomer,
            @RequestParam("fileBudget") MultipartFile fileBudget,
            @RequestParam("fileExpense") MultipartFile fileExpense,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            @RequestParam(required = false, defaultValue = "false") boolean controlSumExpenseVsSumBudget,
            Model model
    ) {
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User loggedInUser = userService.findById(userId);
        if (loggedInUser.isInactiveUser()) {
            return "error/account-inactive";
        }

        HashMap<String, List<CsvErrorWrapper>> errorsPerFile = new HashMap<>();
        List<Customer> customers = new ArrayList<>();
        List<Budget> budgets = new ArrayList<>();
        List<Expense> expenses = new ArrayList<>();

        // Init transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("myTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            // csv -> dto -> entity
            try {
                customers = customerService.importCsv(fileCustomer, loggedInUser);
            } catch (CsvValidationException e) {
                errorsPerFile.put("customers", e.getErrors());
            }
            try {
                budgets = budgetService.importCsv(fileBudget);
            } catch (CsvValidationException e) {
                errorsPerFile.put("budgets", e.getErrors());
            }
            try {
                expenses = expenseService.importCsv(fileExpense, loggedInUser);
            } catch (CsvValidationException e) {
                errorsPerFile.put("expenses", e.getErrors());
            }

            if (errorsPerFile.isEmpty()) {
                this.saveBatches(customers, budgets, expenses);
                setSuccessAttributes(redirectAttributes, customers, budgets, expenses);

                transactionManager.commit(status);
            } else {
                transactionManager.rollback(status);

                setErrorAttributes(model, errorsPerFile);
                return "data-management/csv-errors";
            }

        } catch (Exception ex) {
            transactionManager.rollback(status);

            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("errorImp", ex.getMessage());
        }
        return "redirect:/import/csv/all";
    }

    // methods
    private void validateSumExpenseVsSumBudget(List<Expense> expenses, List<Budget> budgets,
                                               HashMap<String, List<CsvErrorWrapper>> errorsPerFile) {
        double sumExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double sumBudget = budgets.stream().mapToDouble(Budget::getAmount).sum();

        if (sumExpense > sumBudget) {
            CsvErrorWrapper cew = new CsvErrorWrapper("budget+expense", 0, "sumExpense > sumBudget", null);
            errorsPerFile.put("sumExpense > sumBudget", List.of(cew));
        }
    }

    private void setSuccessAttributes(RedirectAttributes redirectAttributes, List<Customer> customers, List<Budget> budgets, List<Expense> expenses) {
        redirectAttributes.addFlashAttribute("messageImp", "All data have been imported (" + customers.size() + " customers, " + budgets.size() + " budgets and " + expenses.size() + " expenses inserted)");
        redirectAttributes.addFlashAttribute("customersOk", customers.size());
        redirectAttributes.addFlashAttribute("budgetsOk", budgets.size());
        redirectAttributes.addFlashAttribute("expensesOk", expenses.size());
    }

    private void setErrorAttributes(Model model, HashMap<String, List<CsvErrorWrapper>> errorsPerFile) {
        model.addAttribute("customersErrors", errorsPerFile.get("customers"));
        model.addAttribute("budgetsErrors", errorsPerFile.get("budgets"));
        model.addAttribute("expensesErrors", errorsPerFile.get("expenses"));
    }

    private void saveBatches(List<Customer> customers, List<Budget> budgets, List<Expense> expenses) {
        int BATCH_SIZE = 100;
        // Already inserted
//        customerService.saveBatch(customers, BATCH_SIZE);
        budgetService.saveBatch(budgets, BATCH_SIZE);
        expenseService.saveBatch(expenses, BATCH_SIZE);
    }

}
