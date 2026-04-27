package com.project.kiosk.controller;

import com.project.kiosk.service.MenuService;
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
@Tag(name = "Menu", description = "메뉴 이미지 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 이미지 업로드", description = "관리자가 메뉴 이미지를 업로드합니다.")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        String path = menuService.uploadImage(id, file);
        return ResponseEntity.ok(path);
    }

    @Operation(summary = "메뉴 이미지 조회", description = "메뉴 이미지 파일을 조회합니다.")
    @GetMapping(value = "/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        byte[] image = menuService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}
