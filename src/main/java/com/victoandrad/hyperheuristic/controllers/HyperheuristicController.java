package com.victoandrad.hyperheuristic.controllers;

import com.victoandrad.hyperheuristic.services.HyperheuristicService;
import com.victoandrad.hyperheuristic.utils.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hyperheuristic")
public class HyperheuristicController {

    private final HyperheuristicService service;

    @Autowired
    public HyperheuristicController(HyperheuristicService hyperHeuristicService) {
        this.service = hyperHeuristicService;
    }

    @PostMapping(value = "/start")
    public ResponseEntity<String> start() {
        String jobId = service.start();
        return ResponseEntity.ok().body(jobId);
    }

    @GetMapping(value = "/{jobId}")
    public ResponseEntity<Job> getJob(@PathVariable String jobId) {
        Job job = service.getJob(jobId);
        return ResponseEntity.ok().body(job);
    }

    @GetMapping
    public ResponseEntity<List<String>> getJobs() {
        List<String> jobs = service.getJobs();
        return ResponseEntity.ok().body(jobs);
    }
}
