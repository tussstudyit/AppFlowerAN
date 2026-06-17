package com.appshopbanhang.admin.service;

import com.appshopbanhang.admin.dto.ReviewRequest;
import com.appshopbanhang.admin.dto.ReviewResponse;
import com.appshopbanhang.admin.entity.DanhGia;
import com.appshopbanhang.admin.repository.DanhGiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {
    private final DanhGiaRepository reviewRepository;

    public ReviewService(DanhGiaRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<DanhGia> getReviewEntities() {
        return reviewRepository.findAllByOrderByIdDesc();
    }

    public List<ReviewResponse> getReviews(Integer productId) {
        List<DanhGia> reviews = productId == null
                ? reviewRepository.findAllByOrderByIdDesc()
                : reviewRepository.findByProductIdOrderByIdDesc(productId);
        return reviews.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        DanhGia review = new DanhGia();
        review.setProductId(request.productId());
        review.setOrderDetailId(request.orderDetailId());
        review.setUsername(request.username());
        review.setContent(request.content());
        review.setStar1(request.star1());
        review.setStar2(request.star2());
        review.setStar3(request.star3());
        review.setStar4(request.star4());
        review.setStar5(request.star5());
        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Integer id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponse toResponse(DanhGia review) {
        return new ReviewResponse(
                review.getId(),
                review.getProductId(),
                review.getOrderDetailId(),
                review.getUsername(),
                review.getContent(),
                review.getStar1(),
                review.getStar2(),
                review.getStar3(),
                review.getStar4(),
                review.getStar5()
        );
    }
}
