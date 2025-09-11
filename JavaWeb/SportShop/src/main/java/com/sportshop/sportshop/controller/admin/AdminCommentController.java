package com.sportshop.sportshop.controller.admin;;

import com.sportshop.sportshop.dto.response.AdminCommentRow;
import com.sportshop.sportshop.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/comment")

public class AdminCommentController {

    private final CommentService service;

    public AdminCommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        List<AdminCommentRow> rows = service.listForAdmin();
        model.addAttribute("comments", rows);
        return "admin/comment/comment"; // -> templates/admin/comment.html
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/comment?deleted=" + id;
    }

    @PostMapping("/{id}/reply")
    public String reply(@PathVariable Long id, @RequestParam("replyText") String replyText) {
        service.reply(id, replyText);
        return "redirect:/admin/comment?replied=" + id;
    }
}
