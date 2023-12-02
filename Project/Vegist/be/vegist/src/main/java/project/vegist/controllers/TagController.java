package project.vegist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import project.vegist.dtos.TagDTO;
import project.vegist.exceptions.ResourceExistException;
import project.vegist.exceptions.ResourceNotFoundException;
import project.vegist.exceptions.UnauthorizedException;
import project.vegist.responses.BaseResponse;
import project.vegist.responses.ErrorResponse;
import project.vegist.responses.SuccessResponse;
import project.vegist.services.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<List<TagDTO>>> getAllTags() {
        List<TagDTO> tags = tagService.findAll();
        return ResponseEntity.ok(new SuccessResponse<>(tags));
    }

    @GetMapping("/tags/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<TagDTO>> getTagById(@PathVariable Long id) {
        try {
            TagDTO tag = tagService.findById(id);
            return ResponseEntity.ok(new SuccessResponse<>(tag));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/tags")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<TagDTO>> createTag(@RequestBody TagDTO tag) {
        try {
            tagService.validateTagName(tag.getName());
            TagDTO createdTag = tagService.create(tag);
            return new ResponseEntity<>(new SuccessResponse<>(createdTag), HttpStatus.CREATED);
        } catch (ResourceExistException e) {
            return ResponseEntity.status(e.getStatus()).body(new ErrorResponse<>(e.getMessage()));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PutMapping("/tags/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<TagDTO>> updateTag(@PathVariable Long id, @RequestBody TagDTO updatedTag) {
        try {
            TagDTO tag = tagService.update(id, updatedTag);
            return ResponseEntity.ok(new SuccessResponse<>(tag));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(e.getStatus()).body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @DeleteMapping("/tags/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteTag(@PathVariable Long id) {
        try {
            boolean deleted = tagService.delete(id);
            return deleted
                    ? new ResponseEntity<>(new SuccessResponse<>(null), HttpStatus.NO_CONTENT)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse<>("Tag not found"));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(new ErrorResponse<>(e.getMessage()));
        }
    }
}
