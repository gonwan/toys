package com.gonwan.toys.camunda.controller;

import com.gonwan.toys.camunda.domain.Approval;
import com.gonwan.toys.camunda.domain.Article;
import com.gonwan.toys.camunda.service.ArticleWorkflowService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleWorkflowController {

    @Autowired
    private ArticleWorkflowService service;

    @PostMapping("/submit")
    public void submit(@RequestBody Article article) {
        service.startProcess(article);
    }

    @GetMapping("/tasks")
    public List<Article> getTasks(@RequestParam String assignee) {
        return service.getTasks(assignee);
    }

    @PostMapping("/review")
    public void review(@RequestBody Approval approval) {
        service.submitReview(approval);
    }

    @GetMapping("/history/list")
    public List<HistoricProcessInstance> historyList() {
        return service.historyList();
    }

    @GetMapping("/history/get")
    public List<HistoricActivityInstance> historyGet(String id) {
        return service.historyDetail(id);
    }

}
