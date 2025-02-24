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

@Service
public class MetaheuristicService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "http://localhost:8080";

    @Autowired
    public MetaheuristicService(RestTemplate restTemplate,
                                ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode applyMetaHeuristic(Metaheuristic metaHeuristic, JsonNode problem) {
        System.out.println("Starting " + metaHeuristic.getClass().getSimpleName() + " meta-heuristic");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JsonNode> request = new HttpEntity<>(problem, headers);

        String jobId = restTemplate.exchange(
                API_URL + "/timetables/" + metaHeuristic.getName(),
                HttpMethod.POST,
                request,
                String.class).getBody();

        System.out.println("Job: " + jobId);

        JsonNode job = null;

        boolean isSolving = true;
        while (isSolving) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Error while sleep: " + e.getMessage());
            }

            job = getJob(jobId);
            String solverStatus = job.get("solverStatus").asText();
            if (solverStatus.equals("NOT_SOLVING")) {
                isSolving = false;
            }
        }

        return job;
    }

    public JsonNode getJob(String jobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                API_URL + "/timetables/" + jobId,
                HttpMethod.GET,
                request,
                String.class).getBody();

        return stringToJson(response);
    }

    // retorna o score e uma lista das constraints ocorridas
    public JsonNode analyze(JsonNode timetable) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<JsonNode> request = new HttpEntity<>(timetable, headers);

        String response = restTemplate.exchange(
                API_URL + "/timetables/analyze",
                HttpMethod.PUT,
                request,
                String.class).getBody();

        return stringToJson(response);
    }

    public JsonNode getStatus(String jobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<JsonNode> request = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                API_URL + "/timetables/" + jobId + "/status}",
                HttpMethod.GET,
                request,
                String.class).getBody();

        return stringToJson(response);
    }

    public JsonNode terminateSolving(String jobId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<JsonNode> request = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                API_URL + "/timetables/" + jobId,
                HttpMethod.DELETE,
                request,
                String.class).getBody();

        return stringToJson(response);
    }

    // MÉTODOS AUXILIARES

    public JsonNode getProblem() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String response = restTemplate.exchange(
                    "http://localhost:8080/demo-data/B3",
                    HttpMethod.GET,
                    request,
                    String.class).getBody();

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
