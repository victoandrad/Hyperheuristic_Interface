package com.victoandrad.hyperheuristic.heuristics.hyperheuristics;

import com.victoandrad.hyperheuristic.heuristics.Heuristic;
import com.victoandrad.hyperheuristic.heuristics.metaheuristics.Metaheuristic;
import com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction.HeuristicAndPerformance;

import java.util.List;

public interface Hyperheuristic extends Heuristic {
    Metaheuristic selectMetaheuristic();
    void updateScores(List<HeuristicAndPerformance> history);
}
