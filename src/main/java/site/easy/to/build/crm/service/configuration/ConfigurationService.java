package site.easy.to.build.crm.service.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Configuration;
import site.easy.to.build.crm.repository.ConfigurationRepository;

@RequiredArgsConstructor
@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private Configuration expenseThreshold;

    public double getExpenseThreshold() {
        if (expenseThreshold == null) {
            expenseThreshold = configurationRepository.findById("EXPENSE_THRESHOLD_PERCENTAGE")
                    .orElseThrow(() -> new RuntimeException("Expense threshold not found"));
        }
        return Double.parseDouble(expenseThreshold.getValue());
    }

    public void updateExpenseThreshold(double threshold) {
        if (expenseThreshold == null) {
            expenseThreshold = configurationRepository.findById("EXPENSE_THRESHOLD_PERCENTAGE")
                    .orElseThrow(() -> new RuntimeException("Expense threshold not found"));
        }
        expenseThreshold.setValue(String.valueOf(threshold));
        configurationRepository.save(expenseThreshold);
    }
}
