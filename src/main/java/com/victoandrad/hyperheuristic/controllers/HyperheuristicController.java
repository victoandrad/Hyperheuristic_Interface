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

    private final HyperheuristicService hyperheuristicService;
    private final MetaheuristicService metaheuristicService;

    @Autowired
    public HyperheuristicController(HyperheuristicService hyperheuristicService,
                                    MetaheuristicService metaheuristicService) {
        this.hyperheuristicService = hyperheuristicService;
        this.metaheuristicService = metaheuristicService;
    }

    @PostMapping
    public ResponseEntity<String> solve() {
        String jobId = hyperheuristicService.start();
        return ResponseEntity.ok().body(jobId);
    }

    @GetMapping(value = "/{jobId}")
    public ResponseEntity<JsonNode> getJob(@PathVariable String jobId) {
        JsonNode problem = hyperheuristicService.getJob(jobId);
        return ResponseEntity.ok().body(problem);
    }

    @GetMapping
    public ResponseEntity<List<String>> getJobs() {
        List<String> jobs = hyperheuristicService.getJobs();
        return ResponseEntity.ok().body(jobs);
    }

    @PutMapping(value = "/analyze")
    public ResponseEntity<JsonNode> analyze(@RequestBody JsonNode timetable) {
        JsonNode result = metaheuristicService.analyze(timetable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/{jobId}/status")
    public ResponseEntity<JsonNode> getStatus(@PathVariable String jobId) {
        JsonNode result = hyperheuristicService.getStatus(jobId);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping(value = "/{jobId}")
    public ResponseEntity<JsonNode> terminateSolving(@PathVariable String jobId) {
        JsonNode result = metaheuristicService.terminateSolving(jobId);
        return ResponseEntity.ok().body(result);
    }
}
