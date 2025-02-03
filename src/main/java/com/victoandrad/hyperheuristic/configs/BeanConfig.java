package com.victoandrad.hyperheuristic.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction.ChoiceFunction;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.Hyperheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.SimulatedAnnealing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanConfig {

    // Configuration of heuristics

    @Bean
    public List<Metaheuristic> metaHeuristics() {
        List<Metaheuristic> metaHeuristics = new ArrayList<>();
        metaHeuristics.add(new SimulatedAnnealing());
        return metaHeuristics;
    }

    @Bean
    public Hyperheuristic hyperHeuristic(List<Metaheuristic> metaHeuristics) {
        return new ChoiceFunction(metaHeuristics);
    }

    // Other dependencies

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
