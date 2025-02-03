package com.victoandrad.hyperheuristic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.Hyperheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction.HeuristicAndPerformance;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;
import com.victoandrad.hyperheuristic.utils.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class HyperheuristicService {

    // To create HTTP requests
    private final RestTemplate restTemplate;

    // To convert objects to JSON
    private final ObjectMapper objectMapper;

    // To store jobs
    private final ConcurrentMap<String, Job> jobs = new ConcurrentHashMap<>();

    // To control multithreaded processing
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // To store the hyper-heuristic
    private final Hyperheuristic hyperHeuristic;

    // To store the meta-heuristic service
    private final MetaheuristicService metaHeuristicService;

    @Autowired
    public HyperheuristicService(RestTemplate restTemplate,
                                 ObjectMapper objectMapper,
                                 Hyperheuristic hyperHeuristic,
                                 MetaheuristicService metaHeuristicService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.hyperHeuristic = hyperHeuristic;
        this.metaHeuristicService = metaHeuristicService;
    }

    public String start() {
        LocalDateTime startExecution = LocalDateTime.now();

        String jobId = UUID.randomUUID().toString();
        JsonNode problem = getProblem();
        jobs.put(jobId, new Job(jobId, problem));

        executor.submit(() -> {

            System.out.println("================");
            System.out.println("Job started: " + jobId);

            List<HeuristicAndPerformance> historic = new ArrayList<>();

            while (Duration.between(startExecution, LocalDateTime.now()).toHours() < 3) {

                System.out.println("================");
                Metaheuristic selectedHeuristic = hyperHeuristic.selectMetaheuristic();
                LocalDateTime start = LocalDateTime.now();
                Job execution = metaHeuristicService.applyMetaHeuristic(selectedHeuristic, jobs.get(jobId).getProblem());
                LocalDateTime end = LocalDateTime.now();

                selectedHeuristic.setLastApplication(LocalDateTime.now());
                long duration = Duration.between(start, end).getSeconds();
                System.out.println("Execution time: " + duration + " seconds");

                // Update heuristic performance
                Performance performance = Performance.of(execution.getProblem().get("score").asText());

                selectedHeuristic.updatePerformance(performance);

                // Add heuristic name and its performance to history
                historic.add(new HeuristicAndPerformance(selectedHeuristic.getName(), performance));

                // Update heuristics score before execution
                hyperHeuristic.updateScores(historic);

                // Update selected heuristics usage count
                selectedHeuristic.incrementUsageCount();

                // Update job
                updateJob(jobId, execution);

                System.out.println(Duration.between(startExecution, LocalDateTime.now()).toHours() < 3);
                System.out.println("TEST");
            }

            System.out.println("Hyper-heuristic finished: " + jobId);
        });

        return jobId;
    }

    public JsonNode getProblem() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        JsonNode problem = null;
        try {
            problem = objectMapper.readTree(restTemplate.exchange(
                    "http://localhost:8080/demo-data/B3",
                    HttpMethod.GET,
                    request,
                    String.class).getBody());
        } catch (JsonProcessingException e) {
            System.out.println("Error converting problem to JSON: " + e.getMessage());
        }
        return problem;
    }

    public Job getJob(String jobId) {
        return jobs.get(jobId);
    }

    public void updateJob(String jobId, Job job) {
        jobs.put(jobId, job);
    }

    public List<String> getJobs() {
        return List.copyOf(jobs.keySet());
    }
}
