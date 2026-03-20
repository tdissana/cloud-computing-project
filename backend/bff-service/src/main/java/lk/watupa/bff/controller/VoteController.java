package lk.watupa.bff.controller;

import lk.watupa.bff.config.ServiceProperties;
import lk.watupa.bff.dto.ApiResponse;
import lk.watupa.bff.exception.DownstreamException;
import lk.watupa.bff.service.AuthTokenService;
import lk.watupa.bff.service.ProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * BFF Vote Controller  — PROTECTED (JWT required)
 *
 * POST   /bff/api/votes      → vote-service  (upvote / downvote)
 * POST   /bff/api/reports    → vote-service  (report a submission)
 *
 * The BFF:
 *  1. Validates the Authorization header exists.
 *  2. Calls identity-service /api/auth/validate to get the userId.
 *  3. Injects X-User-Id header before forwarding to vote-service.
 *  4. The raw JWT is NEVER forwarded to downstream services.
 */
@Slf4j
@RestController
@RequestMapping("/bff/api")
@RequiredArgsConstructor
public class VoteController {

    private final ProxyService      proxyService;
    private final AuthTokenService  authTokenService;
    private final ServiceProperties serviceProperties;

    // ── Vote (upvote / downvote) ──────────────────────────────────────────────

    /**
     * POST /bff/api/votes
     * Headers: Authorization: Bearer <token>
     * Body: { submissionId: "uuid", voteType: "UP" | "DOWN" }
     */
    @PostMapping("/votes")
    public ResponseEntity<ApiResponse<Map>> vote(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> body
    ) {
        String userId = requireAuth(authorization);
        log.info("Vote request from userId={} body={}", userId, body);

        String targetUrl = serviceProperties.getVote().getUrl() + "/api/votes";

        ResponseEntity<Map> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                body,
                Map.of("X-User-Id", userId),   // inject userId, not the raw JWT
                Map.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    // ── Report a submission ───────────────────────────────────────────────────

    /**
     * POST /bff/api/reports
     * Headers: Authorization: Bearer <token>
     * Body: { submissionId: "uuid", reason: "..." }
     */
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<Map>> report(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Object> body
    ) {
        String userId = requireAuth(authorization);
        log.info("Report request from userId={}", userId);

        String targetUrl = serviceProperties.getVote().getUrl() + "/api/reports";

        ResponseEntity<Map> downstream = proxyService.forward(
                targetUrl,
                HttpMethod.POST,
                body,
                Map.of("X-User-Id", userId),
                Map.class
        );

        return ResponseEntity
                .status(downstream.getStatusCode())
                .body(ApiResponse.ok(downstream.getBody()));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Guards a protected route.
     * Returns the userId if the token is valid, throws 401 otherwise.
     */
    private String requireAuth(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new DownstreamException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required. Please log in to perform this action."
            );
        }
        return authTokenService.validateAndExtractUserId(authorization);
    }
}
