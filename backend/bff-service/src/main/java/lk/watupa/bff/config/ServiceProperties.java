package lk.watupa.bff.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the 'services' block in application.yml.
 * Each inner class maps to one downstream microservice.
 */
@Data
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {

    private ServiceUrl identity          = new ServiceUrl();
    private ServiceUrl salarySubmission  = new ServiceUrl();
    private ServiceUrl search            = new ServiceUrl();
    private ServiceUrl stats             = new ServiceUrl();
    private ServiceUrl vote              = new ServiceUrl();

    @Data
    public static class ServiceUrl {
        private String url;
    }
}
