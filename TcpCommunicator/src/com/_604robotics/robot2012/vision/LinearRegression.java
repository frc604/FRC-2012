package com._604robotics.robot2012.vision;

public class LinearRegression { 

	public static void main(String[] args) {
		double[] x = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		double[] y = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1};

		System.out.println(getRegression(x, y));
	}
	
	public static BackwardsRegressionResult getBackwardsRegression(double[] x, double[] y) {
		RegressionResult backwards = getRegression(y, x);
		return new BackwardsRegressionResult(backwards.m, backwards.b, backwards.R2);
	}

	/**
	 * <p>
	 * This function computes the linear regression of a set of x and y values.
	 * </p>
	 * <p>
	 * It is largely taken from:
	 * <a href="http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html">http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html</a>
	 * </p>
	 * @param x
	 * @param y
	 * @return
	 */
	public static RegressionResult getRegression(double[] x, double[] y) {

		int n = 0;
		int actualN = 0;




		// first pass: read in data, compute xbar and ybar
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		while(n < x.length) {
			if(x[n]==x[n] && y[n]==y[n]) {
				sumx  += x[n];
				sumx2 += x[n] * x[n];
				sumy  += y[n];
				actualN++;
			}
			n++;
		}
		double xbar = sumx / actualN;
		double ybar = sumy / actualN;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			if(x[i] == x[i] && y[i] == y[i]) {
				xxbar += (x[i] - xbar) * (x[i] - xbar);
				yybar += (y[i] - ybar) * (y[i] - ybar);
				xybar += (x[i] - xbar) * (y[i] - ybar);
			}
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;

		// print results
		///System.out.println("y   = " + beta1 + " * x + " + beta0);

		// analyze results
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < n; i++) {
			if(x[i] == x[i] && x[i] == x[i]) {
				double fit = beta1*x[i] + beta0;
				ssr += (fit - ybar) * (fit - ybar);
			}
		}
		double R2    = ssr / yybar;

		return new RegressionResult(beta1, beta0, R2);
	}

	public static Point2d solve(RegressionResult a, RegressionResult b) {
		boolean bA = a instanceof BackwardsRegressionResult;
		boolean bB = b instanceof BackwardsRegressionResult;
		if(bA && bB) {
			double y = -(a.b-b.b)/(a.m-b.m);
			return new Point2d(a.m*y + a.b, y);
		} else if(!(bA || bB)) {
			double x = -(a.b-b.b)/(a.m-b.m);
			return new Point2d(x, a.m*x + a.b);
		} else if(bA) {
			return new Point2d(
			-(b.b*a.m+a.b)/(b.m*a.m-1),
			-(a.b*b.m+b.b)/(b.m*a.m-1));
		} else {	// bB
			return new Point2d(
					-(a.b*b.m+b.b)/(a.m*b.m-1),
					-(b.b*a.m+a.b)/(a.m*b.m-1));
		}
	}

	public static class BackwardsRegressionResult extends RegressionResult {
		public BackwardsRegressionResult(double m, double b, double r2) {
			super(m, b, r2);
		}
		
	}

	public static class RegressionResult {
		double m, b, R2;

		/**
		 * @param m
		 * @param b
		 * @param r2
		 */
		public RegressionResult(double m, double b, double r2) {
			this.m = m;
			this.b = b;
			R2 = r2;
		}

		public String toString() {
			return "RegressionResult [m=" + m + ", b=" + b + ", R2=" + R2 + "]";
		}


	}
}