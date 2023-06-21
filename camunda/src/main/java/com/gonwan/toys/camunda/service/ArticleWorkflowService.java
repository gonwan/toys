package com.gonwan.toys.camunda.service;

import com.gonwan.toys.camunda.domain.Approval;
import com.gonwan.toys.camunda.domain.Article;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ArticleWorkflowService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Transactional
    public void startProcess(Article article) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("author", article.getAuthor());
        variables.put("url", article.getUrl());
        runtimeService.startProcessInstanceByKey("articleReview", variables);
    }

    @Transactional
    public List<Article> getTasks(String assignee) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup(assignee)
                .list();
        List<Article> articles = tasks.stream()
                .map(task -> {
                    Map<String, Object> variables = taskService.getVariables(task.getId());
                    return new Article(
                            task.getId(), (String) variables.get("author"), (String) variables.get("url"));
                })
                .collect(Collectors.toList());
        return articles;
    }

    @Transactional
    public void submitReview(Approval approval) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approval.isStatus());
        taskService.complete(approval.getId(), variables);
    }

    @Transactional
    public List<HistoricProcessInstance> historyList() {
        return historyService.createHistoricProcessInstanceQuery()
                .completed()
                .list();
    }

    @Transactional
    public List<HistoricActivityInstance> historyGet(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .list();
    }

    public List<String> definitionGet(String processDefinitionId) {
        BpmnModelInstance instance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<FlowElement> flowElements = instance.getModelElementsByType(Process.class).stream().findFirst().get().getFlowElements();
        List<String> elements = flowElements.stream()
                .map(x -> String.format("id=%s name=%s type=%s", x.getId(), x.getName(), x.getElementType().getTypeName()))
                .collect(Collectors.toList());
        return elements;
    }

}
