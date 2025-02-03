package com.victoandrad.hyperheuristic.heuristics.metaheuristics;

import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing implements Metaheuristic {

    private final String heuristicName = "simulatedAnnealing";
    private final List<Performance> performances = new ArrayList<>(List.of(new Performance(0, 0)));
    private Integer usageCount = 0;
    private LocalDateTime lastApplication;

    @Override
    public String getName() {
        return heuristicName;
    }

    @Override
    public Performance getPerformance() {
        return performances.getLast();
    }

    @Override
    public void updatePerformance(Performance performance) {
        this.performances.add(performance);
    }

    @Override
    public int getUsageCount() {
        return usageCount;
    }

    @Override
    public void incrementUsageCount() {
        this.usageCount++;
    }

    @Override
    public void setLastApplication(LocalDateTime lastApplication) {
        this.lastApplication = lastApplication;
    }

    @Override
    public LocalDateTime getLastApplication() {
        return lastApplication;
    }
}
