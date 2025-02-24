package com.victoandrad.hyperheuristic.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.Hyperheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction.HeuristicAndPerformance;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // To store jobs
    private final ConcurrentMap<String, JsonNode> jobs = new ConcurrentHashMap<>();

    // To control multithreaded processing
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // To store the hyper-heuristic
    private final Hyperheuristic hyperHeuristic;

    // To store the meta-heuristic service
    private final MetaheuristicService metaheuristicService;

    @Autowired
    public HyperheuristicService(Hyperheuristic hyperHeuristic,
                                 MetaheuristicService metaheuristicService) {
        this.hyperHeuristic = hyperHeuristic;
        this.metaheuristicService = metaheuristicService;
    }

    public String start() {
        LocalDateTime startExecution = LocalDateTime.now();

        String jobId = UUID.randomUUID().toString();
        JsonNode problem = metaheuristicService.getProblem();
        jobs.put(jobId, problem);

        executor.submit(() -> {

            System.out.println("================");
            System.out.println("Job started: " + jobId);

            List<HeuristicAndPerformance> historic = new ArrayList<>();

            while (Duration.between(startExecution, LocalDateTime.now()).toHours() < 3) {

                System.out.println("================");
                Metaheuristic selectedHeuristic = hyperHeuristic.selectMetaheuristic();
                LocalDateTime start = LocalDateTime.now();
                JsonNode execution = metaheuristicService.applyMetaHeuristic(selectedHeuristic, jobs.get(jobId));
                LocalDateTime end = LocalDateTime.now();

                selectedHeuristic.setLastApplication(LocalDateTime.now());
                long duration = Duration.between(start, end).getSeconds();
                System.out.println("Execution time: " + duration + " seconds");

                // Update heuristic performance
                Performance performance = Performance.of(execution.get("score").asText());

                selectedHeuristic.updatePerformance(performance);

                // Add heuristic name and its performance to history
                historic.add(new HeuristicAndPerformance(selectedHeuristic.getName(), performance));

                // Update heuristics score before execution
                hyperHeuristic.updateScores(historic);

                // Update selected heuristics usage count
                selectedHeuristic.incrementUsageCount();

                // Update job
                updateSolverStatus(execution, "SOLVING_ACTIVE");
                updateJob(jobId, execution);
            }

            updateSolverStatus(jobs.get(jobId), "NOT_SOLVING");
            System.out.println("Hyperheuristic finished: " + jobId);
        });

        return jobId;
    }

    public JsonNode getStatus(String jobId) {
        JsonNode job = jobs.get(jobId);
        return metaheuristicService.stringToJson(
                "{ \"name\": \"" + job.get("name").asText() +
                        "\", \"score\": \"" + job.get("score").asText() +
                        "\", \"solverStatus\": \"" + job.get("solverStatus").asText() +
                        "\" }");
    }

    public JsonNode getJob(String jobId) {
        return jobs.get(jobId);
    }

    public void updateJob(String jobId, JsonNode problem) {
        jobs.put(jobId, problem);
    }

    public List<String> getJobs() {
        return List.copyOf(jobs.keySet());
    }

    private void updateSolverStatus(JsonNode problem, String solverStatus) {
        if (problem.isObject()) {
            ObjectNode objectNode = (ObjectNode) problem;
            objectNode.put("solverStatus", solverStatus);
        }
    }
}
