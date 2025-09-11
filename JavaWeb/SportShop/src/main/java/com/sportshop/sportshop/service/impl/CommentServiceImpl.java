package com.sportshop.sportshop.service.impl;

import com.sportshop.sportshop.dto.response.AdminCommentRow;
import com.sportshop.sportshop.entity.CommentEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.enums.StatusEnum;
import com.sportshop.sportshop.repository.CommentRepository;
import com.sportshop.sportshop.repository.ProductRepository;
import com.sportshop.sportshop.repository.UserRepository;
import com.sportshop.sportshop.service.CloudinaryService;
import com.sportshop.sportshop.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired private CommentRepository commentRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CloudinaryService cloudinaryService; // dùng Cloudinary

    @Override
    public List<CommentEntity> findByProductId(Long productId) {
        return commentRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public CommentEntity addComment(CommentEntity newComment, Long productId) {
        if (newComment == null) throw new IllegalArgumentException("Dữ liệu đánh giá trống.");
        if (newComment.getUser() == null) throw new IllegalArgumentException("Thiếu thông tin người dùng khi gửi đánh giá.");
        if (newComment.getRate() == null || newComment.getRate() < 1 || newComment.getRate() > 5)
            throw new IllegalArgumentException("Số sao không hợp lệ (1..5).");
        if (!StringUtils.hasText(newComment.getMessages()))
            throw new IllegalArgumentException("Nội dung đánh giá không được để trống.");

        ProductEntity product = productRepository.findByIdAndStatus(productId, StatusEnum.Active);
        if (product == null) throw new IllegalArgumentException("Sản phẩm không tồn tại hoặc không hoạt động.");
        newComment.setProduct(product);

        if (newComment.getCreateDate() == null) newComment.setCreateDate(new Date());
        return commentRepository.save(newComment);
    }

    @Override
    public List<AdminCommentRow> listForAdmin() {
        return commentRepository.findAllAdminRows();
    }

    @Override
    @Transactional
    public void reply(Long commentId, String replyText) {
        CommentEntity c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đánh giá #" + commentId));
        c.setAdminReply(replyText);
        c.setReplyDate(new Date());
        commentRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("Không tìm thấy đánh giá #" + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void createUserComment(Long userId, Long productId, String messages, Integer rate, List<MultipartFile> media) {
        if (rate == null || rate < 1 || rate > 5) throw new IllegalArgumentException("Rate must be 1..5");
        if (!StringUtils.hasText(messages)) throw new IllegalArgumentException("Nội dung đánh giá trống");

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm: " + productId));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user: " + userId));

        String mediaUrl = null;
        if (media != null && !media.isEmpty()) {
            try {
                List<String> urls = cloudinaryService.uploadFiles(media, "comments");
                if (!urls.isEmpty()) mediaUrl = String.join(",", urls); // LƯU secure_url
            } catch (IOException e) {
                throw new RuntimeException("Upload Cloudinary thất bại", e);
            }
        }

        CommentEntity c = new CommentEntity();
        c.setCreateDate(new Date());
        c.setMessages(messages);
        c.setProduct(product);
        c.setUser(user);
        c.setMediaUrl(mediaUrl);
        c.setAdminReply(null);
        c.setReplyDate(null);
        c.setRate(rate);

        commentRepository.save(c);
    }
}
