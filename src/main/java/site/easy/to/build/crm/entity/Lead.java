package site.easy.to.build.crm.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import site.easy.to.build.crm.api.POV;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "trigger_lead")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lead_id")
    @JsonView(POV.LeadExpense.class)
    private int leadId;

    @Column(name = "name")
    @NotBlank(message = "Name is required")
    @JsonView(POV.LeadExpense.class)
    private String name;

    @Column(name = "status")
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(meeting-to-schedule|scheduled|archived|success|assign-to-sales)$", message = "Invalid status")
    @JsonView(POV.LeadExpense.class)
    private String status;

    @Column(name = "phone")
    private String phone;

    @Column(name = "meeting_id")
    private String meetingId;

    @Column(name = "google_drive")
    private Boolean googleDrive;

    @Column(name = "google_drive_folder_id")
    private String googleDriveFolderId;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<LeadAction> leadActions;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<File> files;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<GoogleDriveFile> googleDriveFiles;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User manager;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonView(POV.LeadExpense.class)
    private Customer customer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Lead() {
    }

    public Lead(String name, String status, String phone, String meetingId, Boolean googleDrive, String googleDriveFolderId,
                List<LeadAction> leadActions, List<File> files, List<GoogleDriveFile> googleDriveFiles, User manager, User employee,
                Customer customer, LocalDateTime createdAt) {
        this.name = name;
        this.status = status;
        this.phone = phone;
        this.meetingId = meetingId;
        this.googleDrive = googleDrive;
        this.googleDriveFolderId = googleDriveFolderId;
        this.leadActions = leadActions;
        this.files = files;
        this.googleDriveFiles = googleDriveFiles;
        this.manager = manager;
        this.employee = employee;
        this.customer = customer;
        this.createdAt = createdAt;
    }

    public void addLeadAction(LeadAction leadAction) {
        this.leadActions.add(leadAction);
    }

    public void removeLeadAction(LeadAction leadAction) {
        this.leadActions.remove(leadAction);
    }

    public void addFile(File file) {
        this.files.add(file);
    }

    public void removeFile(File file) {
        this.files.remove(file);
    }


    public void addGoogleDriveFile(GoogleDriveFile googleDriveFile) {
        this.googleDriveFiles.add(googleDriveFile);
    }

    public void removeGoogleDriveFile(GoogleDriveFile googleDriveFile) {
        this.googleDriveFiles.remove(googleDriveFile);
    }

}


