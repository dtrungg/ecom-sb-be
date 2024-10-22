package com.project.shopapp.controllers;

import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.product.IProductService;
import com.project.shopapp.services.product.image.IProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/product_images")
@RequiredArgsConstructor
public class ProductImageController {

    private final IProductImageService productImageService;
    private final IProductService productService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(id);
        if (productImage != null) {
            productService.deleteFile(productImage.getImageUrl());
        }
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Delete product image successfully")
                .data(productImage)
                .status(HttpStatus.OK)
                .build());
    }
}
