package site.easy.to.build.crm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "configuration")
public class Configuration {

    @Id
    @Column(name = "configuration_key")
    private String key;

    @Column(name = "configuration_value")
    private String value;

}
