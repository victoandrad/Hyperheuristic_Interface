package com.victoandrad.hyperheuristic.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MetaheuristicService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String HOST = "http://localhost:8080";
    private static final String END_POINT = HOST + "/hyperheuristic";

    @Autowired
    public MetaheuristicService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode applyMetaHeuristic(String hyperheuristicJobId, Metaheuristic metaheuristic, JsonNode problem) {
        System.out.println("Starting " + metaheuristic.getName() + " metaheuristic");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonNode> request = new HttpEntity<>(problem, headers);

        Map<String, String> params = new HashMap<>();
        params.put("metaheuristic", metaheuristic.getName());
        params.put("hyperheuristicJobId", hyperheuristicJobId);

        String metaheuristicJobId = restTemplate.exchange(
                END_POINT,
                HttpMethod.POST,
                request,
                String.class,
                params
        ).getBody();

        System.out.println("Job: " + metaheuristicJobId);

        JsonNode job = null;
        boolean isSolving = true;
        while (isSolving) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error while sleep: " + e.getMessage());
            }

            job = getJob(hyperheuristicJobId, metaheuristicJobId);
            String solverStatus = job.get("solverStatus").asText();
            if (solverStatus.equals("NOT_SOLVING")) {
                isSolving = false;
            }
        }
        return job;
    }

    public JsonNode getJob(String hyperheuristicJobId, String metaheuristicJobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("hyperheuristicJobId", hyperheuristicJobId);

        String response = restTemplate.exchange(
                END_POINT + "/" + metaheuristicJobId,
                HttpMethod.GET,
                request,
                String.class,
                params
        ).getBody();

        return stringToJson(response);
    }

    public JsonNode analyze(String hyperheuristicJobId, JsonNode timetable) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<JsonNode> request = new HttpEntity<>(timetable, headers);

        Map<String, String> params = new HashMap<>();
        params.put("hyperheuristicJobId", hyperheuristicJobId);

        String response = restTemplate.exchange(
                END_POINT + "/analyze",
                HttpMethod.PUT,
                request,
                String.class,
                params
        ).getBody();

        return stringToJson(response);
    }

    public JsonNode terminateSolving(String hyperheuristicJobId, String metaheuristicJobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<JsonNode> request = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("hyperheuristicJobId", hyperheuristicJobId);

        String response = restTemplate.exchange(
                END_POINT + "/" + metaheuristicJobId,
                HttpMethod.DELETE,
                request,
                String.class,
                params
        ).getBody();

        return stringToJson(response);
    }

    // MÉTODOS AUXILIARES

    public JsonNode getProblem() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                HOST + "/demo-data/B3",
                HttpMethod.GET,
                request,
                String.class
        ).getBody();

        return stringToJson(response);
    }

    public JsonNode stringToJson(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            System.out.println("Error converting content to JSON: " + e.getMessage());
        }
        return jsonNode;
    }
}
