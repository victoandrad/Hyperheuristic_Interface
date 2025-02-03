package com.victoandrad.hyperheuristic.heuristics.hyperheuristics.choicefunction;

import com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents.Performance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeuristicAndPerformance {
    private String heuristicName;
    private Performance performance;
}
