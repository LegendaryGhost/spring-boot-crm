package site.easy.to.build.crm.service.budget;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.api.dto.CustomerExpenseDTO;
import site.easy.to.build.crm.api.dto.ExpenseTypeDTO;
import site.easy.to.build.crm.csv.CsvValidationException;
import site.easy.to.build.crm.csv.GenericCsvService;
import site.easy.to.build.crm.csv.dto.CsvErrorWrapper;
import site.easy.to.build.crm.csv.dto.ExpenseCsvDto;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CustomerRepository customerRepository;
    private final LeadRepository leadRepository;
    private final TicketRepository ticketRepository;

    private final GenericCsvService<ExpenseCsvDto, Expense> genericCsvService;
    private final JdbcTemplate jdbcTemplate;
    private final Set<String> ticketStatus = Set.of(
            "open",
            "assigned",
            "on-hold",
            "in-progress",
            "resolved",
            "closed",
            "reopened",
            "pending-customer-response",
            "escalated",
            "archived"
    );
    private final Set<String> leadStatus = Set.of(
            "meeting-to-schedule",
            "scheduled",
            "archived",
            "success",
            "assign-to-sales"
    );

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> findByCustomerId(int customerId) {
        return expenseRepository.findByCustomerId(customerId);
    }

    public double findTotalExpenseByCustomerId(int customerId) {
        return expenseRepository.findSumAmountByCustomerId(customerId);
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public List<Expense> findAllLeadsExpenses() {
        return expenseRepository.findAllLeadsExpenses();
    }

    public List<Expense> findAllTicketsExpenses() {
        return expenseRepository.findAllTicketsExpenses();
    }

    public Expense findById(int id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    public void deleteById(int expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    public double findTotalLeadExpense() {
        return expenseRepository.findSumAmountLead();
    }

    public double findTotalTicketExpense() {
        return expenseRepository.findSumAmountTicket();
    }

    public Expense updateAmountById(int expenseId, double newAmount) {
        Expense expense = findById(expenseId);
        expense.setAmount(newAmount);
        return expenseRepository.save(expense);
    }

    public List<ExpenseTypeDTO> findExpensesPerType() {
        List<ExpenseTypeDTO> types = new ArrayList<>();
        types.add(
                new ExpenseTypeDTO(
                        "Tickets",
                        findTotalTicketExpense()
                )
        );
        types.add(
                new ExpenseTypeDTO(
                        "Leads",
                        findTotalLeadExpense()
                )
        );
        return types;
    }

    public List<CustomerExpenseDTO> findExpensesPerCustomer() {
        return expenseRepository.findExpensesByCustomer()
                .stream()
                .map(expense -> new CustomerExpenseDTO(
                        (String) expense[0],
                        ((BigDecimal) expense[1]).doubleValue(),
                        ((BigDecimal) expense[2]).doubleValue()
                ))
                .toList();
    }

    public Expense findByTicketId(int ticketId) {
        return expenseRepository.findByTicketId(ticketId);
    }

    public Expense findByLeadId(int leadId) {
        return expenseRepository.findByLeadId(leadId);
    }

    // batch
    @Transactional
    public void saveBatch(List<Expense> expenses, int batchSize) {
        String sql = "INSERT INTO expenses (created_at, amount, lead_id, ticket_id) VALUES (?, ?, ?, ?)";
        Timestamp t = Timestamp.valueOf(LocalDateTime.now());

        int listSize = expenses.size();
        batchSize = Math.min(batchSize, listSize);

        jdbcTemplate.batchUpdate(sql, expenses, batchSize,
                (PreparedStatement ps, Expense expense) -> {
                    ps.setTimestamp(1, t);
                    ps.setDouble(2, expense.getAmount());

                    Integer leadId = (expense.getLead() != null) ? expense.getLead().getLeadId() : null;
                    Integer ticketId = (expense.getTicket() != null) ? expense.getTicket().getTicketId() : null;

                    ps.setObject(3, leadId, java.sql.Types.INTEGER);
                    ps.setObject(4, ticketId, java.sql.Types.INTEGER);
                });
    }

    // csv
    @Transactional
    public List<Expense> importCsv(MultipartFile file, User user) throws IOException, CsvValidationException {
        List<Expense> entities = new ArrayList<>();
        List<ExpenseCsvDto> dtos = new ArrayList<>();
        List<CsvErrorWrapper> errors = new ArrayList<>();

        String filename = file.getOriginalFilename();
        try {
            dtos = genericCsvService.getDtosFromCsv(file, ExpenseCsvDto.class, filename);
        } catch (CsvValidationException e) {
            errors.addAll(e.getErrors());
        }

        for (int i = 0; i < dtos.size(); i++) {
            entities.add(convertToEntity(dtos.get(i), user, errors, i + 1, filename));
        }

        if (!errors.isEmpty()) {
            throw new CsvValidationException("csv->entity", errors);
        }

        return entities;
    }

    @Transactional
    public Expense convertToEntity(
            ExpenseCsvDto csvDto,
            User user,
            List<CsvErrorWrapper> errors,
            int rowIndex,
            String filename
    ) {
        Expense expense = new Expense();
        expense.setAmount(csvDto.getExpense());

        String email = csvDto.getCustomer_email();
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            String msg = "Customer '" + email + "' not found!";
            errors.add(new CsvErrorWrapper(filename, rowIndex, msg, csvDto.toString()));
            return null;
        }

//        try {
//            decreaseBudget(customer.getCustomerId(), csvDto.getExpense(), email);
//        } catch (CsvValidationException ve) {
//            errors.add(new CsvErrorWrapper(filename, rowIndex, ve.getMessage(), csvDto.toString()));
//            return null;
//        }

        if (csvDto.getType().equals("ticket")) {
            String status = csvDto.getStatus();
            if (!this.ticketStatus.contains(status)) {
//                String message = "Status '" + status + "' incompatible for type ticket!";
//                errors.add(new CsvErrorWrapper(filename, rowIndex, message, csvDto.toString()));
//                return expense;
                // TODO: remove to enable status check
                csvDto.setStatus("open");
            }

            Ticket ticket = new Ticket();
            ticket.setCustomer(customer);
            ticket.setManager(user);
            ticket.setEmployee(user);
            ticket.setSubject(csvDto.getSubject_or_name());
            ticket.setStatus(csvDto.getStatus());
            ticket.setPriority("low");

            expense.setTicket(ticket);
            ticketRepository.save(ticket);

        } else if (csvDto.getType().equals("lead")) {
            String status = csvDto.getStatus();
            if (!this.leadStatus.contains(status)) {
//                String message = "Status '" + status + "' incompatible for type lead!";
//                errors.add(new CsvErrorWrapper(filename, rowIndex, message, csvDto.toString()));
//                return expense;
                // TODO: remove to enable status check
                csvDto.setStatus("meeting-to-schedule");
            }

            Lead lead = new Lead();
            lead.setCustomer(customer);
            lead.setManager(user);
            lead.setEmployee(user);
            lead.setName(csvDto.getSubject_or_name());
            lead.setStatus(csvDto.getStatus());

            expense.setLead(lead);
            leadRepository.save(lead);

        } else {
            String msg = "Unknown expense type : '" + csvDto.getType() + "'! Please refer to existing type";
            errors.add(new CsvErrorWrapper(filename, rowIndex, msg, csvDto.toString()));
        }

        return expense;
    }

//    private void decreaseBudget(int customerId, double amountExpense, String email) throws CsvValidationException {
//        BudgetTotal budgetTotal = budgetTotalRepository.findByCustomerId(customerId).orElse(null);
//        if (budgetTotal == null) {
//            throw new CsvValidationException("No budget found for customer '" + email + "'!", null);
//        }
//
//        double amountRemain = budgetTotal.getAmountRemain();
//        if (amountRemain < amountExpense) {
//            String msg = "Budget of customer '" + customerId + "' exceeded expense amount! Remain: " + amountRemain + " | expense: " + amountExpense;
//            throw new CsvValidationException(msg, null);
//        }
//
//        budgetTotal.setAmountRemain(amountRemain - amountExpense);
//        budgetTotalRepository.save(budgetTotal);
//    }

}
