package com.sportshop.sportshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AdminCommentRow {
    private Long id;
    private String accountName;
    private Integer rate;
    private String messages;
    private String mediaUrl;
    private Date date;
    private String adminReply;
}
