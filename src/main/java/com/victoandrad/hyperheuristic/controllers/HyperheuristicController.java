package com.victoandrad.hyperheuristic.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.victoandrad.hyperheuristic.services.HyperheuristicService;
import com.victoandrad.hyperheuristic.services.MetaheuristicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hyperheuristic")
public class HyperheuristicController {

    private final HyperheuristicService hyperHeuristicService;
    private final MetaheuristicService metaHeuristicService;

    @Autowired
    public HyperheuristicController(HyperheuristicService hyperHeuristicService,
                                    MetaheuristicService metaHeuristicService) {
        this.hyperHeuristicService = hyperHeuristicService;
        this.metaHeuristicService = metaHeuristicService;
    }

    @PostMapping
    public ResponseEntity<String> solve() {
        String jobId = hyperHeuristicService.start();
        return ResponseEntity.ok().body(jobId);
    }

    @GetMapping(value = "/{jobId}")
    public ResponseEntity<JsonNode> getJob(@PathVariable String jobId) {
        JsonNode problem = hyperHeuristicService.getJob(jobId);
        return ResponseEntity.ok().body(problem);
    }

    @GetMapping
    public ResponseEntity<List<String>> getJobs() {
        List<String> jobs = hyperHeuristicService.getJobs();
        return ResponseEntity.ok().body(jobs);
    }

    @PutMapping(value = "/analyze")
    public ResponseEntity<JsonNode> analyze(@RequestBody JsonNode timetable) {
        JsonNode result = metaHeuristicService.analyze(timetable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/{jobId}/status")
    public ResponseEntity<JsonNode> getStatus(@PathVariable String jobId) {
        JsonNode result = hyperHeuristicService.getStatus(jobId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping(value = "/{jobId}")
    public ResponseEntity<JsonNode> terminateSolving(@PathVariable String jobId) {
        JsonNode result = metaHeuristicService.terminateSolving(jobId);
        return ResponseEntity.ok().body(result);
    }
}
