package com.sportshop.sportshop.service;

import com.sportshop.sportshop.dto.response.AdminCommentRow;
import com.sportshop.sportshop.entity.CommentEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface CommentService {
    // USER: Lấy đánh giá theo sản phẩm
    List<CommentEntity> findByProductId(Long productId);

    // USER: Thêm đánh giá (newComment cần có user được set từ Controller)
    CommentEntity addComment(CommentEntity newComment, Long productId);

    // ADMIN: Danh sách đánh giá cho trang quản trị (kèm tên tài khoản)
    List<AdminCommentRow> listForAdmin();

    // ADMIN: Trả lời đánh giá
    void reply(Long commentId, String replyText);

    // ADMIN: Xóa đánh giá
    void delete(Long commentId);

    void createUserComment(Long userId, Long productId, String messages, Integer rate, List<MultipartFile> media);
}
