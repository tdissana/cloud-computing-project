    package lk.watupa.salary_submission.model;

    import jakarta.persistence.*;
    import lk.watupa.salary_submission.enums.ExperienceLevel;
    import lk.watupa.salary_submission.enums.Status;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;

    import java.time.LocalDateTime;
    import java.util.UUID;

    @Entity
    @Table(schema = "salary", name = "submissions")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Submission {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @Column(length = 255)
        private String company;

        @Column(length = 255)
        private String role;

        @Enumerated(EnumType.STRING)
        @Column(name = "experience_level", length = 50)
        private ExperienceLevel experienceLevel;

        @Column(length = 100)
        @Builder.Default
        private String country = "Sri Lanka";

        @Column(name = "total_compensation")
        private double totalCompensation;

        @Column(name = "base_salary")
        private double baseSalary;

        @Column(length = 10)
        @Builder.Default
        private String currency = "LKR";

        @Column
        @Builder.Default
        private Boolean anonymize = true;

        @Enumerated(EnumType.STRING)
        @Column(length = 20)
        @Builder.Default
        private Status status = Status.PENDING;

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;
    }
