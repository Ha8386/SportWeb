package com.sportshop.sportshop.repository;

import com.sportshop.sportshop.dto.response.AdminCommentRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sportshop.sportshop.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByProductId(Long productId);
    @Query("""
           select new com.sportshop.sportshop.dto.response.AdminCommentRow(
               c.id,
               u.username,
               c.rate,
               c.messages,
               c.mediaUrl,
               c.createDate,
               c.adminReply
           )
           from CommentEntity c
           join c.user u
           order by c.createDate desc
           """)
    List<AdminCommentRow> findAllAdminRows();

    Optional<CommentEntity> findByUserIdAndProductId(Long userId, Long productId);
}
