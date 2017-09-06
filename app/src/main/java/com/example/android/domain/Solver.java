package com.example.android.domain;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class Solver
{
    public static int count = 0;
    public static double rndUniformContinuous(double a, double b) {
        if (Double.isNaN(a)) return Double.NaN;
        if (Double.isNaN(b)) return Double.NaN;
        if (b < a) return Double.NaN;
        if (a == b) return a;
        double r = a + Math.random() * (b - a);
        return r;

    }

    public static double round(double value, int places) {
        if (Double.isNaN(value)) return Double.NaN;
        if (places < 0) return Double.NaN;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double solve(String f, double a, double b) throws Exception {
        MathParser parser = new MathParser();
        double  fa, fb, fc, fs, c, c0, c1, c2;
        double tmp, d, s;
        boolean mflag;
        int iter;
        double eps = 0.0000001;
        double maxSteps = 1000;
        /*
         * If b lower than b then swap
         */
        if (b < a) {
            tmp = a;
            a = b;
            b = tmp;
        }
        fa = parser.Parse(f, a);
        fb = parser.Parse(f, b);
        /*
         * If already root then no need to solve
         */
        if (Math.abs(fa) <= eps) return a;
        if (Math.abs(fb) <= eps) return b;
        if (b == a) return Double.NaN;
        /*
         * If root not bracketed the perform random search
         */
        if (fa * fb > 0) {
            boolean rndflag = false;
            double ap, bp;
            for (int i = 0; i < maxSteps; i++) {
                count++;
                ap = rndUniformContinuous(a, b);
                bp = rndUniformContinuous(a, b);
                if (bp < ap) {
                    tmp = ap;
                    ap = bp;
                    bp = tmp;
                }
                fa = parser.Parse(f, ap);
                fb = parser.Parse(f, bp);
                if (Math.abs(fa) <= eps) return ap;
                if (Math.abs(fb) <= eps) return bp;
                if (fa * fb < 0) {
                    rndflag = true;
                    a = ap;
                    b = bp;
                    break;
                }
            }
            if (rndflag == false) return Double.NaN;
        }
        c = a;
        d = c;
        fc = parser.Parse(f, c);
        if (Math.abs(fa) < Math.abs(fb)) {
            tmp = a;
            a = b;
            b = tmp;
            tmp = fa;
            fa = fb;
            fb = tmp;
        }
        mflag = true;
        iter = 0;
        /*
         * Perform actual Brent algorithm
         */
        while ((Math.abs(fb) > eps) && ( Math.abs(b-a) > eps) && (iter < maxSteps)) {
            if ( (fa != fc) && (fb != fc) ) {
                c0 = (a * fb * fc) / ((fa - fb) * (fa - fc));
                c1 = (b * fa * fc) / ((fb - fa) * (fb - fc));
                c2 = (c * fa * fb) / ((fc - fa) * (fc - fb));
                s = c0 + c1 + c2;
            } else
                s = b - (fb * (b - a)) / (fb - fa);
            if (    ( s < (3 * (a + b) / 4) || s > b) ||
                    ( (mflag == true) && Math.abs(s-b) >= (Math.abs(b-c)/2) ) ||
                    ( (mflag == false) && Math.abs(s-b) >= (Math.abs(c-d)/2) )  ) {
                s = (a+b)/2;
                mflag = true;
            } else
                mflag = true;
            fs = parser.Parse(f, s);
            d = c;
            c = b;
            fc = fb;
            if ((fa * fs) < 0)
                b = s;
            else
                a = s;
            if (Math.abs(fa) < Math.abs(fb)) {
                tmp = a;
                a = b;
                b = tmp;
                tmp = fa;
                fa = fb;
                fb = tmp;
            }
            iter++;

        }
        return round(b, 6);
    }
}

