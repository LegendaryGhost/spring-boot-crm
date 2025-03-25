package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Expense;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    @Query("""
        SELECT e
        FROM Expense e
        WHERE e.ticket.customer.customerId = :customerId
        UNION
        SELECT e
        FROM Expense e
        WHERE e.lead.customer.customerId = :customerId
    """)
    List<Expense> findByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT COALESCE(SUM(e.amount), 0 ) FROM Expense e WHERE e.ticket.customer.customerId = :customerId OR e.lead.customer.customerId = :customerId")
    double findSumAmountByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT e FROM Expense e WHERE e.ticket IS NOT NULL")
    List<Expense> findAllTicketsExpenses();

    @Query("SELECT e FROM Expense e WHERE e.lead IS NOT NULL")
    List<Expense> findAllLeadsExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.lead IS NOT NULL")
    double findSumAmountLead();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.ticket IS NOT NULL")
    double findSumAmountTicket();

    @Query(value = """
        SELECT c.name AS customerName,
               COALESCE(SUM(b.amount), 0) AS budget,
               COALESCE(SUM(te.amount), 0) + COALESCE(SUM(le.amount), 0) AS expense
        FROM customer c
        LEFT JOIN budgets b ON c.customer_id = b.customer_id
        LEFT JOIN trigger_ticket t ON c.customer_id = t.customer_id
        LEFT JOIN trigger_lead l ON c.customer_id = l.customer_id
        LEFT JOIN expenses te ON t.ticket_id = te.ticket_id
        LEFT JOIN expenses le ON l.lead_id = le.lead_id
        GROUP BY c.name
    """, nativeQuery = true)
    List<Object[]> findExpensesByCustomer();

}
