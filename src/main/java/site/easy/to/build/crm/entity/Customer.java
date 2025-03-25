package site.easy.to.build.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.Data;
import site.easy.to.build.crm.api.POV;
import site.easy.to.build.crm.customValidations.customer.UniqueEmail;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "customer")
public class Customer {

    public interface CustomerUpdateValidationGroupInclusion {}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    @JsonView(POV.Budget.class)
    private Integer customerId;

    @Column(name = "name")
    @NotBlank(message = "Name is required", groups = {Default.class, CustomerUpdateValidationGroupInclusion.class})
    @JsonView(POV.Budget.class)
    private String name;

    @Column(name = "email")
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email format")
    @UniqueEmail
    @JsonView(POV.Budget.class)
    private String email;

    @Column(name = "position")
    private String position;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    @NotBlank(message = "Country is required", groups = {Default.class, CustomerUpdateValidationGroupInclusion.class})
    private String country;

    @Column(name = "description")
    private String description;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "youtube")
    private String youtube;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable=false)
    @JsonIgnoreProperties("customer")
    private User user;

    @OneToOne
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private CustomerLoginInfo customerLoginInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Customer() {
    }

    public Customer(String name, String email, String position, String phone, String address, String city, String state, String country,
                    String description, String twitter, String facebook, String youtube, User user, CustomerLoginInfo customerLoginInfo,
                    LocalDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.position = position;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.description = description;
        this.twitter = twitter;
        this.facebook = facebook;
        this.youtube = youtube;
        this.user = user;
        this.customerLoginInfo = customerLoginInfo;
        this.createdAt = createdAt;
    }

}
