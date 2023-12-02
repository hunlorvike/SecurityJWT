package project.vegist.controllers;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import project.vegist.entities.User;
import project.vegist.services.AuthenticationService;
import project.vegist.services.JwtService;

@RestController
@Tag(name = "Test")
public class TestController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

    @Operation(description = "Xem danh sách User", responses = {
            @ApiResponse(content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class))), responseCode = "200")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "403", description = "Truy cập bị cấm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy")
    })
    @GetMapping("/test/ok")
    public String testOk() {
        return "test Ok ";
    }

    @GetMapping("/api/v1/private/test")
    public String testPrivate() {
        return "testPrivate";
    }

    @GetMapping("/private/test-private-admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String testPrivateAdmin() {
        return "test Private Admin";
    }

    @GetMapping("/test/get-user-id")
    public Long getUserId(@RequestHeader("Authorization") String authorizationHeader) {
        return jwtService.getUserIdFromToken(authorizationHeader);
    }

    @GetMapping("/test/get-claims")
    public Claims getClaims(@RequestHeader("Authorization") String authorizationHeader) {
        return jwtService.getClaims(authorizationHeader);
    }

    @GetMapping("test/role-user")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public String testRoleUser() {
        return "test Role User";
    }

    @GetMapping("test/role-admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String testRoleAdmin() {
        return "test Role Admin";
    }

    @GetMapping("test/role-admin-read")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_READ')")
    public String testRoleAdminREAD() {
        return "test Role Admin READ";
    }
}
