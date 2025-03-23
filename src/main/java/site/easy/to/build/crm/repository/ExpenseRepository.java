package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Expense;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.budget.customer.customerId = :customerId")
    List<Expense> findByCustomerId(@Param("customerId") int cutomerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0 ) FROM Expense e WHERE e.budget.customer.customerId = :customerId")
    double findSumAmountByCustomerId(@Param("customerId") int customerId);
}
