package org.kbroman.polar.polarpvc;

import com.polar.sdk.api.model.PolarEcgData;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.util.Log;


public class QRSDetection implements IConstants, IQRSConstants {

    private final ECGActivity mActivity;

    private final FixedSizeList<Integer> mPeakIndices =
            new FixedSizeList<>(DATA_WINDOW);

    private int mPeakIndex = -1;

    private double mStartTime = Double.NaN;

    private final List<Double> ecgVals = new ArrayList<>();

    private final RunningAveSD movingAveSDecg = new RunningAveSD(MOVING_AVESD_WINDOW);

    public QRSDetection(ECGActivity activity) {
        mActivity = activity;
    }

    public void process(PolarEcgData polarEcgData) {
        // Update the ECG plot
        ecgPlotter().addValues(polarEcgData);

        int start, end, n;

        // grab a batch of data
        if(ecgVals.isEmpty()) start = 0;
        else start = ecgVals.size();
        for(Integer val : polarEcgData.samples) {
            ecgVals.add(MICRO_TO_MILLI_VOLT * val);

            qrsPlotter().addValues(MICRO_TO_MILLI_VOLT * val, 0.0, 0.0);
        }
        n = ecgVals.size() - start;

        if(ecgVals.size() < 500) return; // wait to start looking for peaks

        // look for peaks in first half
        end = start + n/2;
        start = Math.max(0, start-5);
        find_peaks(start, end);

        // look for peaks in second half
        start = Math.max(0, end-5);
        find_peaks(start, ecgVals.size());
    }

    // array to hold smoothed squared differences
    private final ArrayList<Double> smsqdiff = new ArrayList<>();

    int lastPeak=-1;
    double last_smsqdiff = -Double.MAX_VALUE;

    public void find_peaks(int start, int end) {
        // Record the start time as now.
        if (Double.isNaN(mStartTime)) mStartTime = new Date().getTime();

        smsqdiff.clear();
        // get squared differences
        for(int i=start; i<end-1; i++) {
            double diff = (ecgVals.get(i) - ecgVals.get(i+1));
            smsqdiff.add(diff * diff);
        }
        smsqdiff.add(0.0);
        smsqdiff.add(0.0);

        // smooth in groups of three
        for(int i=0; i<smsqdiff.size()-2; i++) {
            smsqdiff.set(i, (smsqdiff.get(i)+smsqdiff.get(i+1)+smsqdiff.get(i+2))/3.0);

            movingAveSDecg.add(smsqdiff.get(i)); // get running mean and SD
        }

        // find maximum
        int max_index = which_max(smsqdiff);
        double this_smsqdiff = smsqdiff.get(max_index);
        mPeakIndex = max_index + start;

        double this_ecg = ecgVals.get(mPeakIndex);

        Boolean peakFound=false;

        if((this_smsqdiff - movingAveSDecg.average())/movingAveSDecg.sd() >= MIN_PEAK_ECG_VALUE) { // peak only if large
            if(mPeakIndices.size() == 0 || mPeakIndex - lastPeak >= HR_200_INTERVAL) { // new peak
                peakFound = true;
                last_smsqdiff = this_smsqdiff;
                lastPeak = mPeakIndex;
                mPeakIndices.add(mPeakIndex);
                qrsPlotter().addPeakValue(mPeakIndex, this_ecg);
            } else { // too close to previous peak
                if(this_smsqdiff > last_smsqdiff) {
                    last_smsqdiff = this_smsqdiff;
                    lastPeak = mPeakIndex;
                    mPeakIndices.setLast(mPeakIndex);
                    qrsPlotter().replaceLastPeakValue(mPeakIndex, this_ecg);
                }
            }
        }

        // now look at whether penultimate peak was a PVC
        if(peakFound && mPeakIndices.size() > INITIAL_PEAKS_TO_SKIP) {
            int lastPeakIndex = mPeakIndices.get(mPeakIndices.size()-2);
            int thisPeakIndex = mPeakIndices.get(mPeakIndices.size()-1);

            ArrayList<Double> temp_ecg = new ArrayList<>();

            int endSearch = (int)Math.min((double)(thisPeakIndex - lastPeakIndex)/2.0, 20.0);

            for(int i=1; i<endSearch; i++) temp_ecg.add( ecgVals.get(lastPeakIndex+i) );
            int minPeakIndex = which_min(temp_ecg);

            if(minPeakIndex >= PVC_RS_DIST) { // looks like a PVC
                qrsPlotter().addPVCValue(lastPeakIndex, ecgVals.get(lastPeakIndex));
                mActivity.PVCdata.add(1.0);
            } else {                          // not a PVC
                mActivity.PVCdata.add(0.0);
            }
        }

    }


    public ECGPlotter ecgPlotter() {
        return mActivity.mECGPlotter;
    }

    public QRSPlotter qrsPlotter() {
        return mActivity.mQRSPlotter;
    }

    public HRPlotter hrPlotter() {
        return mActivity.mHRPlotter;
    }


    int which_max(ArrayList<Double> v) {
        if(v.isEmpty()) return(-1);

        int n = v.size();
        if(n==1) return(0);

        double max = v.get(0);
        int max_index = 0;

        for(int i=1; i<n; i++) {
            if(v.get(i) > max) {
                max_index = i;
                max = v.get(i);
            }
        }

        return(max_index);
    }

    int which_min(ArrayList<Double> v) {
        if(v.isEmpty()) return(-1);

        int n = v.size();
        if(n==1) return(0);

        double min = v.get(0);
        int min_index = 0;

        for(int i=1; i<n; i++) {
            if(v.get(i) < min) {
                min_index = i;
                min = v.get(i);
            }
        }

        return(min_index);
    }

}
