package com.victoandrad.hyperheuristic.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.victoandrad.hyperheuristic.services.HyperheuristicService;
import com.victoandrad.hyperheuristic.services.MetaheuristicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/hyperheuristic")
public class HyperheuristicController {

    private final HyperheuristicService hyperheuristicService;
    private final MetaheuristicService metaheuristicService;

    @Autowired
    public HyperheuristicController(HyperheuristicService hyperheuristicService, MetaheuristicService metaheuristicService) {
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
    public ResponseEntity<Collection<String>> getJobs() {
        Collection<String> jobs = hyperheuristicService.getJobs();
        return ResponseEntity.ok().body(jobs);
    }

    @PutMapping(value = "/analyze")
    public ResponseEntity<JsonNode> analyze(@RequestBody JsonNode timetable, @RequestParam("hyperheuristicJobId") String hyperheuristicJobId) {
        JsonNode result = metaheuristicService.analyze(hyperheuristicJobId, timetable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/{jobId}/status")
    public ResponseEntity<JsonNode> getStatus(@PathVariable String jobId) {
        JsonNode result = hyperheuristicService.getStatus(jobId);
        return ResponseEntity.ok().body(result);
    }

    // TODO: RESOLVER O MÉTODO TERMINATESOLVING

//    @DeleteMapping(value = "/{jobId}")
//    public ResponseEntity<JsonNode> terminateSolving(@PathVariable String jobId, @RequestParam("hyperheuristicJobId") String hyperheuristicJobId) {
//        JsonNode result = metaheuristicService.terminateSolving(jobId, hyperheuristicJobId);
//        return ResponseEntity.ok().body(result);
//    }
}
