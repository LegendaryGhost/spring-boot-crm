package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Expense;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    @Query("SELECT e FROM Expense e WHERE e.budget.customer.customerId = :customerId")
    List<Expense> findByCustomerId(@Param("customerId") int cutomerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0 ) FROM Expense e WHERE e.budget.customer.customerId = :customerId")
    double findSumAmountByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.budget.id = :budgetId")
    double findSumAmountByBudgetId(@Param("budgetId") int budgetId);

    @Query("SELECT e FROM Expense e WHERE e.ticket IS NOT NULL")
    List<Expense> findAllTicketsExpenses();

    @Query("SELECT e FROM Expense e WHERE e.lead IS NOT NULL")
    List<Expense> findAllLeadsExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.lead IS NOT NULL")
    double findSumAmountLead();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.ticket IS NOT NULL")
    double findSumAmountTicket();

    @Modifying
    @Query("DELETE FROM Expense e WHERE e.budget.id = :budgetId")
    void deleteByBudgetId(@Param("budgetId") int budgetId);
}
