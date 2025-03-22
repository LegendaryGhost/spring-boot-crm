package site.easy.to.build.crm.util;

import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import site.easy.to.build.crm.dto.UserDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class BatchUtils {

    private final JdbcTemplate jdbcTemplate;

    public void batchSaveUsers(List<UserDTO> users) {
        String sql = "INSERT INTO users(email, hire_date, username) VALUES(?,?,?)";
        jdbcTemplate.batchUpdate(sql, users, 500, (ps, user) -> {
            ps.setString(1, user.getEmail());
            ps.setTimestamp(2, Timestamp.valueOf(user.getHireDate()));
            ps.setString(3, user.getUsername());
        });
    }

    public void batchSave(String tableName, CSVParser parser) {
        String sql = "INSERT INTO " + tableName + "(";

        List<String> headers = parser.getHeaderNames();

        sql += String.join(", ", headers);
        sql += ") VALUES (";
        sql += Stream.generate(() -> "?")
                .limit(headers.size())
                .collect(Collectors.joining(", "));
        sql += ")";

        jdbcTemplate.batchUpdate(sql, parser.getRecords(), 500, (ps, record) -> {
            for (int i = 1; i <= headers.size(); i++) {
                ps.setString(i, record.get(headers.get(i - 1)));
            }
        });
    }

}
