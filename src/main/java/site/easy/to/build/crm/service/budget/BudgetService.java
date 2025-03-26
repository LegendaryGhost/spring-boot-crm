package site.easy.to.build.crm.service.budget;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.expression.Numbers;
import site.easy.to.build.crm.csv.CsvValidationException;
import site.easy.to.build.crm.csv.GenericCsvService;
import site.easy.to.build.crm.csv.dto.BudgetCsvDto;
import site.easy.to.build.crm.csv.dto.CsvErrorWrapper;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.service.configuration.ConfigurationService;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseService expenseService;
    private final ConfigurationService configurationService;
    private final GenericCsvService<BudgetCsvDto, Budget> genericCsvService;
    private final CustomerRepository customerRepository;

    private final JdbcTemplate jdbcTemplate;

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

    @Transactional
    public void saveBatch(List<Budget> budgets, Integer batchSize) {
        String sql = "INSERT INTO budgets (created_at, amount, customer_id) VALUES (?, ?, ?)";
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());

        int listSize = budgets.size();
        batchSize = (batchSize == null || batchSize > listSize) ? listSize : batchSize;

        jdbcTemplate.batchUpdate(sql, budgets, batchSize,
                (PreparedStatement ps, Budget budget) -> {
                    ps.setTimestamp(1, t);
                    ps.setDouble(2, budget.getAmount());
                    ps.setInt(3, budget.getCustomer().getCustomerId());
                });
    }

    // csv
    @Transactional
    public List<Budget> importCsv(MultipartFile file) throws IOException, CsvValidationException {
        List<Budget> entities = new ArrayList<>();
        List<BudgetCsvDto> dtos = new ArrayList<>();
        List<CsvErrorWrapper> errors = new ArrayList<>();

        String filename = file.getOriginalFilename();
        try {
            dtos = genericCsvService.getDtosFromCsv(file, BudgetCsvDto.class, filename);
        } catch (CsvValidationException e) {
            errors.addAll(e.getErrors());
        }

        for (int i = 0; i < dtos.size(); i++) {
            entities.add(convertToEntity(dtos.get(i), errors, i + 1, filename));
        }

        if (!errors.isEmpty()) {
            throw new CsvValidationException("csv conversion error", errors);
        }

        return entities;
    }

    @Transactional
    public Budget convertToEntity(
            BudgetCsvDto csvDto,
            List<CsvErrorWrapper> errors,
            int rowIndex,
            String filename
    ) {
        Budget budget = new Budget();
        budget.setAmount(csvDto.getBudget());

        double amount = csvDto.getBudget();

        Customer customer = customerRepository.findByEmail(csvDto.getCustomer_email());
        if (customer == null) {
            String msg = "Customer '" + csvDto.getCustomer_email() + "' not found!";
            errors.add(new CsvErrorWrapper(filename, rowIndex, msg, csvDto.toString()));
            return null;
        }
        budget.setCustomer(customer);

//        BudgetTotal bt = budgetTotalRepository.findByCustomerId(customer.getCustomerId()).orElse(null);
//        if (bt == null) {
//            bt = new BudgetTotal();
//            bt.setCustomer(customer);
//            bt.setAmountTotal(amount);
//            bt.setAmountRemain(amount);
//        } else {
//            double oldRemain = bt.getAmountRemain(),
//                    oldTotal = bt.getAmountTotal();
//            bt.setAmountTotal(oldTotal + amount);
//            bt.setAmountRemain(oldRemain + amount);
//        }
//        budgetTotalRepository.save(bt);

        return budget;
    }

}
