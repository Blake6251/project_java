package com.project.portal.controller;

import com.project.portal.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "Menu image API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "Upload menu image", description = "Admin uploads a menu image file")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        String path = menuService.uploadImage(id, file);
        return ResponseEntity.ok(path);
    }

    @Operation(summary = "Get menu image", description = "Retrieve menu image file bytes")
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = menuService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}
