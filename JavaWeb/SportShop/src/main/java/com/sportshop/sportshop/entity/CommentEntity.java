package com.sportshop.sportshop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "comment")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // Số sao (1..5)
    @Column(name = "rate", nullable = false)
    Integer rate;

    // Nội dung đánh giá
    @Column(name = "messages", nullable = false, length = 2000)
    String messages;

    // Thời điểm tạo (cột DB là 'date')
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    Date createDate;

    // Đường dẫn ảnh/video do khách gửi (png/jpg/jpeg/gif/mp4/webm/ogg...)
    @Column(name = "media_url", length = 255)
    String mediaUrl;

    // Phản hồi của admin (tùy chọn)
    @Column(name = "admin_reply", length = 2000)
    String adminReply;

    // Thời điểm admin trả lời (tùy chọn)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reply_date")
    Date replyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @PrePersist
    void prePersist() {
        if (createDate == null) {
            createDate = new Date();
        }
    }
}
