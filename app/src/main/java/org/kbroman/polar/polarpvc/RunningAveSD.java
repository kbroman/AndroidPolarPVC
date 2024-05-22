package org.kbroman.polar.polarpvc;

import java.util.LinkedList;

/* this should inherit from RunningAverage, but I couldn't figure out how to do so */
public class RunningAveSD {

    private LinkedList<Double> list;
    private int maxItems;
    private double sum, sumsq;

    public RunningAveSD(int maxItems) {
        list = new LinkedList<Double>();
        this.maxItems = maxItems;
        sum = 0.0;
        sumsq = 0.0;
    }

    public void add(double val) {
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

    public double average() {
            return (list.size() == 0) ? 0 : sum / list.size();
        }

    public double sum() { return sum; }

    public int size() {
            return list.size();
        }

    public double sd() {
        int n=list.size();
        return (n < 2) ? 0 : Math.sqrt((sumsq -  (sum*sum)/(double)n)/(n-1));
    }

    public double sumsq() { return sumsq; }

}
