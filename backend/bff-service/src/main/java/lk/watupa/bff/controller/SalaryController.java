package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.dto.ApiResponse;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bff/api/submissions")
@RequiredArgsConstructor
public class SalaryController {

    private final ProxyService proxyService;
    private final ServiceProperties serviceProperties;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Map>> submitSalary(
            @RequestBody Map<String, Object> body
    ) {
        log.info("Salary submission request received");

        String targetUrl = serviceProperties.getSalarySubmission().getUrl() + "/api/submissions/submit";

        ResponseEntity<Map> downstream = proxyService.forward(
                targetUrl, HttpMethod.POST, body, Map.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }
}
