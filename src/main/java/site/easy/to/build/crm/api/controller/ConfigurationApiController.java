package site.easy.to.build.crm.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.easy.to.build.crm.api.ApiOkResponse;
import site.easy.to.build.crm.api.ApiResponse;
import site.easy.to.build.crm.service.configuration.ConfigurationService;

@RestController
@RequestMapping("/api/configurations")
@RequiredArgsConstructor
public class ConfigurationApiController {

    private final ConfigurationService configurationService;

    @GetMapping("/expense-threshold")
    public ResponseEntity<Double> getExpenseThreshold() {
        return ResponseEntity.ok(configurationService.getExpenseThreshold());
    }

    @PutMapping("/expense-threshold")
    public ResponseEntity<ApiResponse<?>> create(@RequestParam("threshold") double threshold) {
        configurationService.updateExpenseThreshold(threshold);
        ApiResponse<?> response = new ApiOkResponse<>("Expense threshold updated", threshold);
        return ResponseEntity.ok(response);
    }
}
