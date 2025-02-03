package com.victoandrad.hyperheuristic.heuristics.metaheuristics.dependents;

import lombok.Data;

@Data
public class Performance {

    private Integer hard;
    private Integer soft;

    public Performance(Integer hard, Integer soft) {
        this.hard = hard;
        this.soft = soft;
    }

    public static Performance of(String performance) {
        String[] parts = performance.split("/");
        int hard = Integer.parseInt(parts[0].replace("hard", ""));
        int soft = Integer.parseInt(parts[1].replace("soft", ""));
        return new Performance(hard, soft);
    }
}
