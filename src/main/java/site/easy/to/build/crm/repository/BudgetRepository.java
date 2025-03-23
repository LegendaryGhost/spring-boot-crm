package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.easy.to.build.crm.entity.Budget;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    @Query("SELECT COALESCE(SUM(b.amount), 0.0) FROM  Budget b WHERE b.customer.customerId = :customerId")
    double findSumAmountByCustomerId(@Param("customerId") int customerId);

    @Query("SELECT b FROM Budget b WHERE b.customer.customerId = :customerId")
    List<Budget> findByCustomerId(@Param("customerId") int customerId);
}
