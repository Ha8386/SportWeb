package com.sportshop.sportshop.controller.admin;

import com.sportshop.sportshop.service.RevenueService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/revenue")
public class AdminRevenueController {

    private final RevenueService revenueService;
    public AdminRevenueController(RevenueService revenueService) { this.revenueService = revenueService; }

    // ===== Helpers =====
    private static LocalDate parseFlexible(String s) {
        if (s == null || s.isBlank()) return null;
        DateTimeFormatter[] fmts = new DateTimeFormatter[]{
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd/MM/uuuu"),
                DateTimeFormatter.ofPattern("MM/dd/uuuu")
        };
        for (var f : fmts) { try { return LocalDate.parse(s, f); } catch (Exception ignore) {} }
        return null;
    }
    private static LocalDate[] ensureRange(String from, String to) {
        LocalDate f = parseFlexible(from);
        LocalDate t = parseFlexible(to);
        if (f == null || t == null) {
            t = LocalDate.now();
            f = t.minusDays(29);
        }
        return new LocalDate[]{f, t};
    }

    // ===== Page =====
    @GetMapping
    public String page(@RequestParam(required = false) String from,
                       @RequestParam(required = false) String to,
                       Model model) {
        var r = ensureRange(from, to);
        var summary = revenueService.getSummary(r[0], r[1]);
        model.addAllAttributes(summary);
        model.addAttribute("from", r[0]);
        model.addAttribute("to", r[1]);
        return "admin/revenue/revenue";
    }

    // ===== JSON APIs =====
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> summary(@RequestParam(required = false) String from,
                                       @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getSummary(r[0], r[1]);
    }

    @GetMapping(value = "/chart/day", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> chartByDay(@RequestParam(required = false) String from,
                                                @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getRevenueByDay(r[0], r[1]);
    }

    @GetMapping(value = "/chart/month", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> chartByMonth(@RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getRevenueByMonth(r[0], r[1]);
    }

    @GetMapping(value = "/chart/year", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> chartByYear(@RequestParam(required = false) String from,
                                                 @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getRevenueByYear(r[0], r[1]);
    }

    @GetMapping(value = "/chart/product", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> chartByProduct(@RequestParam(required = false) String from,
                                                    @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getRevenueByProduct(r[0], r[1]);
    }

    @GetMapping(value = "/chart/category", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> chartByCategory(@RequestParam(required = false) String from,
                                                     @RequestParam(required = false) String to) {
        var r = ensureRange(from, to);
        return revenueService.getRevenueByCategory(r[0], r[1]);
    }
}
