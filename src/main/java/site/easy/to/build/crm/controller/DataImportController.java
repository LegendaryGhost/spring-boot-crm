package site.easy.to.build.crm.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.easy.to.build.crm.dto.CsvImportForm;
import site.easy.to.build.crm.util.csv.CSVUtils;

@AllArgsConstructor
@Controller
@RequestMapping("/manager/import")
public class DataImportController {

    private final CSVUtils csvUtils;

    @GetMapping("/users")
    public String showImportUsersForm() {
        return "import/import-users";
    }

    @PostMapping("/users")
    public String importUsers(
            @ModelAttribute("csvImport") CsvImportForm csvImportForm,
            BindingResult bindingResult
    ) {
        MultipartFile file = csvImportForm.getFile();
        if (file.isEmpty()) {
            bindingResult.rejectValue("file", "file.empty", "The file should not be empty");
            return "import/import-users";
        }

        if (!"text/csv".equals(file.getContentType())) {
            bindingResult.rejectValue("file", "file.contentType", "The file should be text csv");
            return "import/import-users";
        }

        if (bindingResult.hasErrors()) {
            return "import/import-users";
        }

        csvUtils.saveUsersFromCSV(csvImportForm);

        return "redirect:/manager/all-users";
    }

}
