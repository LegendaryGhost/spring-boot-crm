package site.easy.to.build.crm.csv.dto;

import com.opencsv.bean.CsvBindByName;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BudgetCsvDto {
    @NotBlank(message = "'customer_email' cannot be blank")
    @CsvBindByName(column = "customer_email")
    @Size(max = 255, message = "'customer_email' must be less than 255 characters")
    private String customer_email;

    @NotBlank(message = "'budgetStr' cannot be blank")
    @Pattern(regexp = "^[+-]?(?:[0-9]+(?:[.,][0-9]+)?|[.,][0-9]+)$", message = "Invalid number format")
    @CsvBindByName(column = "Budget")
    private String budgetStr;

    @DecimalMin(value = "0.0", inclusive = false, message = "'budget' must be strictly sup to 0.0")
    public Double budget;

    // Custom setter for budget to handle comma replacement
    public void setBudgetStr(String budget) {
        try {
            if (budget.contains(",")) {
                this.budget = Double.parseDouble(budget.replace(",", "."));
            } else {
                this.budget = Double.parseDouble(budget);
            }
        } catch (NumberFormatException ignored) {
        }
        this.budgetStr = budget;
    }
}
