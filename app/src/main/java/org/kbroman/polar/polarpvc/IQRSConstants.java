package org.kbroman.polar.polarpvc;

interface IQRSConstants {
    /**
     * Sampling rate. These algorithms are based on this particular sampling
     * rate.
     */
    double FS = 130.169282548569;
    /**
     * Number of small boxes in a large box.
     */
    int N_SMALL_BOXES_PER_LARGE_BOX = 5;
    /**
     * The number of samples in a large box on an ECG plot.
     */
    int N_LARGE = (int) (FS / N_SMALL_BOXES_PER_LARGE_BOX); // =26
    /**
     * The total number of points to keep.
     */
    int N_TOTAL_POINTS = (int) (30 * FS);  // =3900 -> 30 sec
    /**
     * The number of points to show in an ECG plot.
     */
    int N_ECG_PLOT_POINTS = 4 * N_SMALL_BOXES_PER_LARGE_BOX * N_LARGE;
    // =520 points -> 4 sec
    /**
     * Number of large boxes visible on the x axis.
     */
    int N_DOMAIN_LARGE_BOXES = N_ECG_PLOT_POINTS / N_LARGE; // = 20
    /**
     * Ratio of mV to mm for a box.
     */
    int RATIO_MM_MV = 100;

    /**
     * Data window size.  Must be large enough for maximum number of
     * coefficients.
     */
    int DATA_WINDOW = 20;

    /***
     * The heart rate interval. The algorithm is based on there being only one
     * heart beat in this interval. Assumes maximum heart rate is 200.
     */
    int HR_200_INTERVAL = (int) (60.0 / 200.0 * FS); // 39
    /**
     * How many seconds the domain interval is for the HRPlotter.
     */
    long HR_PLOT_DOMAIN_INTERVAL = 1 * 60000;  // 1 min

    /**
     * Convert μV to mV.
     */
    double MICRO_TO_MILLI_VOLT = .001;

    /**
     * minimum value for a peak (don't call a peak if it's below this)
     * this is in terms of number of SDs above the mean,
     * where mean and sd are from running window,
     * and after taking smoothed squared differences
     */
    double MIN_PEAK_VALUE = 1.5;
    /**
     * size of window for the running mean and sd
     */
    int MOVING_AVESD_WINDOW = 500;

    /**
     * conclude PVC if if number of samples between R and S peaks is >= this
     */
    int PVC_RS_DIST = 5;

    /**
     * number of peaks to skip before starting to call PVCs
     */
    int INITIAL_PEAKS_TO_SKIP = 2;

}
