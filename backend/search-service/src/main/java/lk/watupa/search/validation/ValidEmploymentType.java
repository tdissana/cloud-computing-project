//package lk.watupa.search.validation;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.*;
//
///**
// * Validates that employmentType is one of the allowed types (case-insensitive).
// */
//@Documented
//@Constraint(validatedBy = EmploymentTypeValidator.class)
//@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ValidEmploymentType {
//    String message() default "employmentType must be one of: Full-time, Part-time, Contract, Freelance (case-insensitive)";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}
