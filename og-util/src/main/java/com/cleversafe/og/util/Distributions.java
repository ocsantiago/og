/* Copyright (c) IBM Corporation 2016. All Rights Reserved.
 * Project name: Object Generator
 * This project is licensed under the Apache License 2.0, see LICENSE.
 */

package com.cleversafe.og.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import com.google.common.math.DoubleMath;

/**
 * A utility class for creating distribution instances
 * 
 * @since 1.0
 */
public class Distributions {
  private static final double ERR = Math.pow(0.1, 6);

  private Distributions() {}

  /**
   * Creates a uniform distribution with a range of [average - spread, average + spread].
   * 
   * @param average the average value of this distribution
   * @param spread distance from the average
   * @return a uniform distribution instance
   * @throws IllegalArgumentException if average or spread are negative, or if average - spread is
   *         negative
   */
  public static Distribution uniform(final double average, final double spread) {
    checkArgument(average >= 0.0, "average must be >= 0.0 [%s]", average);
    checkArgument(spread >= 0.0, "spread must be >= 0.0 [%s]", spread);

    if (DoubleMath.fuzzyEquals(spread, 0.0, Distributions.ERR)) {
      return constant(average);
    }

    final double lower = average - spread;
    final double upper = average + spread;
    checkArgument(lower >= 0.0, "average - spread must be >= 0.0 [%s]", lower);
    final String s = String.format("UniformDistribution [average=%s, spread=%s]", average, spread);
    return new RealDistributionAdapter(new UniformRealDistribution(lower, upper), s);
  }

  /**
   * Creates a normal distribution with a rangge of [average - 3*spread, average + 3*spread].
   * 
   * @param average the center of this distribution
   * @param spread distance of one standard deviation
   * @return a normal distribution instance
   * @throws IllegalArgumentException if average or spread are negative, or if average - 3*spread is
   *         negative
   */
  public static Distribution normal(final double average, final double spread) {
    checkArgument(average >= 0.0, "average must be >= 0.0 [%s]", average);
    checkArgument(spread >= 0.0, "spread must be >= 0.0 [%s]", spread);

    if (DoubleMath.fuzzyEquals(spread, 0.0, Distributions.ERR)) {
      return constant(average);
    }

    final double min = average - (3 * spread);
    checkArgument(min >= 0.0, "three standard deviations must be >= 0.0 [%s]", min);
    final String s = String.format("NormalDistribution [average=%s, spread=%s]", average, spread);
    return new RealDistributionAdapter(new NormalDistribution(average, spread), s);
  }

  /**
   * Creates a lognormal distribution.
   * 
   * @param average the average
   * @param spread the spread
   * @return a lognormal distribution instance
   * @throws IllegalArgumentException if average or spread are negative, or if average - 3*spread is
   *         negative
   */
  public static Distribution lognormal(final double average, final double spread) {
    // FIXME configure lognormal distribution range correctly
    checkArgument(average >= 0.0, "average must be >= 0.0 [%s]", average);
    checkArgument(spread >= 0.0, "spread must be >= 0.0 [%s]", spread);

    if (DoubleMath.fuzzyEquals(spread, 0.0, Distributions.ERR)) {
      return constant(average);
    }

    final double min = average - (3 * spread);
    checkArgument(min >= 0.0, "three standard deviations must be >= 0.0 [%s]", min);
    final String s =
        String.format("LogNormalDistribution [average=%s, spread=%s]", average, spread);
    return new RealDistributionAdapter(new LogNormalDistribution(average, spread), s);
  }

  /**
   * Creates a poisson distribution.
   * 
   * @param average the average value generated by this distribution
   * @return a poisson distribution instance
   * @throws IllegalArgumentException if average is negative
   */
  public static Distribution poisson(final double average) {
    checkArgument(average >= 0.0, "average must be >= 0.0 [%s]", average);
    final String s = String.format("PoissonDistribution [average=%s]", average);
    return new IntegerDistributionAdapter(new PoissonDistribution(average), s);
  }

  private static Distribution constant(final double average) {
    checkArgument(average >= 0.0, "average must be >= 0.0 [%s]", average);
    final String s = String.format("ConstantDistribution [average=%s]", average);
    return new RealDistributionAdapter(new ConstantRealDistribution(average), s);
  }

  // adapt apache's RealDistribution interface to og's Distribution interface
  private static class RealDistributionAdapter implements Distribution {
    private final RealDistribution d;
    private final String stringRepresentation;

    public RealDistributionAdapter(final RealDistribution d, final String stringRepresentation) {
      this.d = checkNotNull(d);
      this.stringRepresentation = checkNotNull(stringRepresentation);
    }

    @Override
    public double getAverage() {
      return this.d.getNumericalMean();
    }

    @Override
    public double nextSample() {
      return this.d.sample();
    }

    @Override
    public String toString() {
      return this.stringRepresentation;
    }
  }

  // adapt apache's IntegerDistribution interface to og's Distribution interface
  private static class IntegerDistributionAdapter implements Distribution {
    private final IntegerDistribution d;
    private final String stringRepresentation;

    public IntegerDistributionAdapter(final IntegerDistribution d,
        final String stringRepresentation) {
      this.d = checkNotNull(d);
      this.stringRepresentation = checkNotNull(stringRepresentation);
    }

    @Override
    public double getAverage() {
      return this.d.getNumericalMean();
    }

    @Override
    public double nextSample() {
      return this.d.sample();
    }

    @Override
    public String toString() {
      return this.stringRepresentation;
    }
  }
}
