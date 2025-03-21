package site.easy.to.build.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager/import")
public class DataImportController {

    @GetMapping("/users")
    public String showImportUsersForm() {
        return "import/import-users";
    }

}
