package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.dto.ReviewRequest;
import com.appshopbanhang.admin.dto.ReviewResponse;
import com.appshopbanhang.admin.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {
    private final ReviewService reviewService;

    public ReviewApiController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> reviews(@RequestParam(required = false) Integer productId) {
        return reviewService.getReviews(productId);
    }

    @PostMapping
    public ReviewResponse create(@Valid @RequestBody ReviewRequest request) {
        return reviewService.createReview(request);
    }
}
