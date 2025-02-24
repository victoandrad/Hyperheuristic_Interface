package com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction;

import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.Hyperheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChoiceFunction implements Hyperheuristic {

    private final String heuristicName = "choiceFunction";

    private static final double pesoF1 = 0.9;
    private static final double pesoF2 = 0.1;
    private static final double pesoF3 = 1.5;

    @Getter
    private final List<Metaheuristic> heuristics;

    @Getter
    private final double[] heuristicsScores;

    public ChoiceFunction(List<Metaheuristic> metaHeuristics) {
        this.heuristics = metaHeuristics;
        this.heuristicsScores = new double[metaHeuristics.size()];
    }

    public Metaheuristic selectMetaheuristic() {
        List<HeuristicAndPerformance> emptyList = new ArrayList<>();
        updateScores(emptyList);

        int bestHeuristicIndex = 0;
        for (int i = 1; i < heuristicsScores.length; i++) {
            if (heuristicsScores[i] > heuristicsScores[bestHeuristicIndex]) {
                bestHeuristicIndex = i;
            }
        }
        Metaheuristic selectedHeuristic = heuristics.get(bestHeuristicIndex);
        selectedHeuristic.incrementUsageCount();
        return selectedHeuristic;
    }

    public void updateScores(List<HeuristicAndPerformance> historic) {
        // Update score based on performance
        for (int i = 0; i < heuristics.size(); i++) {
            Metaheuristic heuristic = heuristics.get(i);
            double score = calculateScoreByPerformance(heuristic.getPerformance());
            heuristicsScores[i] = score / pesoF1;
        }

        // Update score based on heuristics relations
        if (historic.size() > 1) {
            calculateScoreByRelation(historic);
        }

        // Update score based on time since a heuristic was last selected
        if (getWithUsageCountZero().isEmpty()) {
            List<Long> minutesWithoutApplyPerHeuristic = getMinutesWithoutApplyPerHeuristic();
            for (int i = 0; i < heuristics.size(); i++) {
                heuristicsScores[i] += pesoF3 * minutesWithoutApplyPerHeuristic.get(i) / 10.0;
            }
        }
    }

    public double calculateScoreByPerformance(Performance performance) {
        double normalizer = 1000.0;
        return performance.getHard() + (performance.getSoft() / normalizer);
    }

    public void calculateScoreByRelation(List<HeuristicAndPerformance> historic) {
        double normalizer = 1000.0;

        // Previous execution
        HeuristicAndPerformance previousExecution = historic.get(historic.size() - 2);
        Performance previousPerformance = previousExecution.getPerformance();

        // Current execution
        HeuristicAndPerformance currentExecution = historic.getLast();
        Performance currentPerformance = currentExecution.getPerformance();

        // How much improved from the previous execution to the current execution
        int hardDifference = Math.abs(previousPerformance.getHard() - currentPerformance.getHard());
        int softDifference = Math.abs(previousPerformance.getSoft() - currentPerformance.getSoft());

        double pointsBonusByRelation = hardDifference + (softDifference / normalizer);

        for (int i = 0; i < heuristics.size(); i++) {
            if (heuristics.get(i).getName().equals(previousExecution.getHeuristicName())) {
                heuristicsScores[i] += pesoF2 * pointsBonusByRelation;
            }
        }
    }

    public List<Long> getMinutesWithoutApplyPerHeuristic() {
        LocalDateTime now = LocalDateTime.now();
        return heuristics
                .stream()
                .filter(heuristic -> heuristic.getLastApplication() != null) // Evita NullPointerException
                .map(heuristic -> Duration.between(heuristic.getLastApplication(), now).toMinutes())
                .toList();
    }

    public List<Metaheuristic> getWithUsageCountZero() {
        return heuristics
                .stream()
                .filter(heuristic -> heuristic.getUsageCount() == 0)
                .toList();
    }
}
