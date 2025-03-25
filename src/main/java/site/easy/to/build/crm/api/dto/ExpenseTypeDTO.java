package site.easy.to.build.crm.api.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import site.easy.to.build.crm.api.POV;

@Data
@AllArgsConstructor
@JsonView(POV.Dashboard.class)
public class ExpenseTypeDTO {

    private String type;
    private double amount;

}
