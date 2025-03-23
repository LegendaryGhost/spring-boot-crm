package site.easy.to.build.crm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.repository.ExpenseRepository;

@AllArgsConstructor
@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

}
