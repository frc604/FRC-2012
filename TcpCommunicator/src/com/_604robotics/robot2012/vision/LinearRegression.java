package com._604robotics.robot2012.vision;

/**
 * Accepts a sequence of pairs of real numbers and computes the best fit (least squares) line y = ax + b through the set
 * of points. Also computes the correlation coefficient and the standard error of the regression coefficients.
 * 
 * @author Robert Sedgewick <http://www.cs.princeton.edu/~rs>, Kevin Wayne <http://www.cs.princeton.edu/~wayne> </br>
 *         ({@link #getRegression(double[], double[])}) is adapted from <a
 *         href="http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html">
 *         introcs.cs.princeton.edu/java/97data/LinearRegression.java.html</a>)
 * @author Kevin Parker <kevin.m.parker@gmail.com>
 * 
 */
public class LinearRegression {
	
	/**
	 * A regression result that, instead of having y as a function of x has x as a function of y.
	 * 
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 * @see RegressionResult
	 */
	public static class BackwardsRegressionResult extends RegressionResult {
		
		public BackwardsRegressionResult(double m, double b, double r2) {
			super(m, b, r2);
		}
		
	}
	
	/**
	 *  A regression result that indicates the line that best matches a given set of data.
	 * 
	 * @author Kevin Parker <kevin.m.parker@gmail.com>
	 */
	public static class RegressionResult {
		
		/**
		 * The slope of the regression line
		 */
		double m;
		
		/**
		 * The y-intercept of the regression line
		 */
		double b;
		
		/**
		 * A number indicating how good of a fit this line is
		 */
		double R2;
		
		/**
		 * @param m	The slope of the regression line
		 * @param b	The y-intercept of the regression line
		 * @param r2	A number indicating how good of a fit this line is
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
	
	/**
	 * This returns a regression result that, instead of having y as a function of x has x as a function of y.
	 * 
	 * @param y	the list of Y values
	 * @param x	the list of X values
	 * @return
	 */
	public static BackwardsRegressionResult getBackwardsRegression(double[] y, double[] x) {
		RegressionResult backwards = getRegression(x, y);
		return new BackwardsRegressionResult(backwards.m, backwards.b, backwards.R2);
	}
	
	/**
	 * <p>
	 * This function computes the linear regression of a set of x and y values.
	 * </p>
	 * <p>
	 * It is largely taken from: <a
	 * href="http://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html">http://
	 * introcs.cs.princeton.edu/java/97data/LinearRegression.java.html</a>
	 * </p>
	 * 
	 * @param x	An array of X values
	 * @param y	An array of Y values
	 * @author Robert Sedgewick <http://www.cs.princeton.edu/~rs>
	 * @author Kevin Wayne <http://www.cs.princeton.edu/~wayne>
	 * @return
	 */
	public static RegressionResult getRegression(double[] x, double[] y) {
		
		int n = 0;
		int actualN = 0;
		


		// first pass: read in data, compute xbar and ybar
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		while (n < x.length) {
			if (x[n] == x[n] && y[n] == y[n]) {
				sumx += x[n];
				sumx2 += x[n] * x[n];
				sumy += y[n];
				actualN++;
			}
			n++;
		}
		double xbar = sumx / actualN;
		double ybar = sumy / actualN;
		
		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			if (x[i] == x[i] && y[i] == y[i]) {
				xxbar += (x[i] - xbar) * (x[i] - xbar);
				yybar += (y[i] - ybar) * (y[i] - ybar);
				xybar += (x[i] - xbar) * (y[i] - ybar);
			}
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
		
		// print results
		////System.out.println("y   = " + beta1 + " * x + " + beta0);
		
		// analyze results
		double ssr = 0.0; // regression sum of squares
		for (int i = 0; i < n; i++) {
			if (x[i] == x[i] && x[i] == x[i]) {
				double fit = beta1 * x[i] + beta0;
				ssr += (fit - ybar) * (fit - ybar);
			}
		}
		double R2 = ssr / yybar;
		
		return new RegressionResult(beta1, beta0, R2);
	}
	
	/**
	 * Computes the intersection of two RegressionResults
	 * 
	 * @param a	A RegressionResult
	 * @param b	A RegressionResult
	 * @return The intersection
	 */
	public static Point2d solve(RegressionResult a, RegressionResult b) {
		boolean bA = a instanceof BackwardsRegressionResult;
		boolean bB = b instanceof BackwardsRegressionResult;
		
		if (bA && bB) {	// if both are backwards
			double y = -(a.b - b.b) / (a.m - b.m);
			return new Point2d(a.m * y + a.b, y);
		} else if (!(bA || bB)) {	// if both are normal regression results
			double x = -(a.b - b.b) / (a.m - b.m);
			return new Point2d(x, a.m * x + a.b);
		} else if (bA)	// if A is backwards
			return new Point2d(-(b.b * a.m + a.b) / (b.m * a.m - 1), -(a.b * b.m + b.b) / (b.m * a.m - 1));
		else	// if B is backwards
			return new Point2d(-(a.b * b.m + b.b) / (a.m * b.m - 1), -(b.b * a.m + a.b) / (a.m * b.m - 1));
	}
}
