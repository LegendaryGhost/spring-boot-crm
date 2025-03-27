package site.easy.to.build.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CsvImportForm {

    private MultipartFile file;

    @NotBlank(message = "The separator cannot be empty")
    private String separator;

}
