package lk.watupa.search.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VerificationStatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVerificationStatus {
    String message() default "verificationStatus must be VERIFIED or UNVERIFIED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
