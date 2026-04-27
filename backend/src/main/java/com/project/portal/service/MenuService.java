package com.project.portal.service;

import com.project.portal.domain.Menu;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final FileUploadService fileUploadService;

    @Transactional
    public String uploadImage(Long menuId, MultipartFile file) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
        String imagePath = fileUploadService.saveImage(file);
        menu.setImageUrl(imagePath);
        return imagePath;
    }

    @Transactional(readOnly = true)
    public byte[] getImage(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(ErrorCode.MENU_NOT_FOUND));
        if (menu.getImageUrl() == null || menu.getImageUrl().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "?깅줉???대?吏媛 ?놁뒿?덈떎.");
        }
        return fileUploadService.load(menu.getImageUrl());
    }
}
