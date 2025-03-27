package site.easy.to.build.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDTO {

    public String email;
    public LocalDateTime hireDate;
    public String username;

}
