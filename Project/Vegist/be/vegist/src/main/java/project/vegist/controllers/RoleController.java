package project.vegist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.vegist.dtos.RoleDTO;
import project.vegist.exceptions.ResourceExistException;
import project.vegist.exceptions.ResourceNotFoundException;
import project.vegist.responses.BaseResponse;
import project.vegist.responses.ErrorResponse;
import project.vegist.responses.SuccessResponse;
import project.vegist.services.RoleService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/private")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<List<RoleDTO>>> getAllRoles() {
        try {
            List<RoleDTO> roles = roleService.findAll();
            return ResponseEntity.ok(new SuccessResponse<>(roles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse<>("An error occurred while fetching roles"));
        }
    }

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<RoleDTO>> getRoleById(@PathVariable Long id) {
        try {
            Optional<RoleDTO> roleOptional = roleService.findById(id);

            if (roleOptional.isPresent()) {
                return ResponseEntity.ok(new SuccessResponse<>(roleOptional.get()));
            } else {
                throw new ResourceNotFoundException("Role", id, HttpStatus.NOT_FOUND);
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(e.getStatus()).body(new ErrorResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse<>("An error occurred while fetching the role"));
        }
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<RoleDTO>> createRole(@RequestBody RoleDTO role) {
        try {
            RoleDTO createdRole = roleService.create(role);
            return new ResponseEntity<>(new SuccessResponse<>(createdRole), HttpStatus.CREATED);
        } catch (ResourceExistException e) {
            return ResponseEntity.status(e.getStatus()).body(new ErrorResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse<>("An error occurred while creating the role"));
        }
    }

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<RoleDTO>> updateRole(@PathVariable Long id, @RequestBody RoleDTO updatedRole) {
        try {
            Optional<RoleDTO> existingRoleOptional = roleService.update(id, updatedRole);

            if (existingRoleOptional.isPresent()) {
                return ResponseEntity.ok(new SuccessResponse<>(existingRoleOptional.get()));
            } else {
                throw new ResourceNotFoundException("Role", id, HttpStatus.NOT_FOUND);
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(e.getStatus()).body(new ErrorResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse<>("An error occurred while updating the role"));
        }
    }

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteRole(@PathVariable Long id) {
        try {
            boolean deleted = roleService.delete(id);
            return deleted
                    ? new ResponseEntity<>(new SuccessResponse<>(null), HttpStatus.NO_CONTENT)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse<>("Role not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse<>("An error occurred while deleting the role"));
        }
    }
}
