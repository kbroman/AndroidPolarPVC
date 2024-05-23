package org.kbroman.polar.polarpvc;

import java.util.LinkedList;

public class RunningAveSD extends RunningAverage {

    public double sumsq;

    public RunningAveSD(int maxItems) {
        super(maxItems);
        sumsq = 0.0;
    }

    @Override public void add(double val) {
        if (list.size() == maxItems) {
            double first = list.getFirst();
            sum -= first;
            sumsq -= (first*first);
            list.removeFirst();
        }
        list.add(val);
        sum += val;
        sumsq += (val*val);
    }

    public double sd() {
        int n=list.size();
        return (n < 2) ? 0 : Math.sqrt((sumsq -  (sum*sum)/(double)n)/(n-1));
    }

}
