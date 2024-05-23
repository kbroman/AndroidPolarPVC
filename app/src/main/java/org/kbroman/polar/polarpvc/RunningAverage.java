package org.kbroman.polar.polarpvc;

import java.util.LinkedList;

public class RunningAverage {
    public LinkedList<Double> list;
    public int maxItems;
    public double sum;

    public RunningAverage(int maxItems) {
        list = new LinkedList<>();
        this.maxItems = maxItems;
        sum = 0;
    }

    public void add(double val) {
        if (list.size() == maxItems) {
            sum -= list.getFirst();
            list.removeFirst();
        }
        list.add(val);
        sum += val;
    }

    public double average() {
        return (list.size() == 0) ? 0 : sum / list.size();
    }

    public int size() {
        return list.size();
    }
}
