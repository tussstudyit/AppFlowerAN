package com.appshopbanhang.admin.dto;

public record ReviewResponse(
        Integer id,
        Integer productId,
        Integer orderDetailId,
        String username,
        String content,
        String star1,
        String star2,
        String star3,
        String star4,
        String star5
) {
}
