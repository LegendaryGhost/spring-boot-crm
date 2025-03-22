package site.easy.to.build.crm.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import site.easy.to.build.crm.dto.CsvImportForm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CSVUtils {

    public void readCSVFile(CsvImportForm csvImportForm) {
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
            List<String> headers = csvParser.getHeaderNames();

            // Iterate through the CSV records
            for (CSVRecord record : csvParser) {
                for (String header : headers) {
                    System.out.print(header + "=" + record.get(header) + " ");
                }
                System.out.println();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
