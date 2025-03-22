package site.easy.to.build.crm.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;
import site.easy.to.build.crm.dto.CsvImportForm;
import site.easy.to.build.crm.util.BatchUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class CSVUtils {

    private final BatchUtils batchUtils;

    public CSVUtils(BatchUtils batchUtils) {
        this.batchUtils = batchUtils;
    }

    public void saveUsersFromCSV(CsvImportForm csvImportForm) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(csvImportForm.getFile().getInputStream(), StandardCharsets.UTF_8)
                );
                CSVParser csvParser = new CSVParser(
                        reader,
                        CSVFormat.Builder.create()
                                .setDelimiter(csvImportForm.getSeparator())
                                .setHeader()
                                .setTrim(true)
                                .build()
                )
        ) {
            batchUtils.batchSave("temp_users", csvParser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Buggy constraint can't handle YYYY-MM-DD format
//    public void saveUsersFromCSVFile(CsvImportForm csvImportForm) {
//        CSVFile<UserDTO> file = new CSVFile<>(csvImportForm.getFile(), csvImportForm.getSeparator());
//        file.addConstraint("hire_date", ConstraintCSV.LOCALDATE_TIME);
//        file.readAndTransform(line -> new UserDTO(
//                (String) line.get("email"),
//                (LocalDateTime) line.get("hire_date"),
//                (String) line.get("username")
//        ));
//
//        for (String error : file.errors) {
//            System.out.println(error);
//        }
//
//        batchUtils.batchSaveUsers(file.data);
//    }

}
