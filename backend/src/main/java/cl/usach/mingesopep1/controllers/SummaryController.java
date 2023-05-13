package cl.usach.mingesopep1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cl.usach.mingesopep1.services.SummaryService;
import cl.usach.mingesopep1.entities.SummaryEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequestMapping
public class SummaryController {
    @Autowired
    private SummaryService summaryService;

    @GetMapping("/summary")
    public String getSummary(Model model) {
        summaryService.makeSummary();
        List<SummaryEntity> summary = summaryService.getSummaries();
        model.addAttribute("summary", summary);
        return "summary";
    }
}
