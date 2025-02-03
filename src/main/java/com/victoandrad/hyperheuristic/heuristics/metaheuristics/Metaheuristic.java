package com.victoandrad.hyperheuristic.heuristics.metaheuristics;

import com.victoandrad.hyperheuristic.heuristics.Heuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;

import java.time.LocalDateTime;

public interface Metaheuristic extends Heuristic {
    Performance getPerformance();
    void updatePerformance(Performance performance);
    int getUsageCount();
    void incrementUsageCount();
    void setLastApplication(LocalDateTime lastApplication);
    LocalDateTime getLastApplication();
    String getName();
}
