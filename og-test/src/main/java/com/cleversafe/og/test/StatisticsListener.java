//
// Copyright (C) 2005-2011 Cleversafe, Inc. All rights reserved.
//
// Contact Information:
// Cleversafe, Inc.
// 222 South Riverside Plaza
// Suite 1700
// Chicago, IL 60606, USA
//
// licensing@cleversafe.com
//
// END-OF-HEADER
//
// -----------------------
// @author: rveitch
//
// Date: Jun 22, 2014
// ---------------------

package com.cleversafe.og.test;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cleversafe.og.statistic.Counter;
import com.cleversafe.og.statistic.Statistics;
import com.cleversafe.og.util.Operation;
import com.google.common.eventbus.Subscribe;

public class StatisticsListener
{
   private static Logger _logger = LoggerFactory.getLogger(StatisticsListener.class);
   private final LoadTest test;
   private final Operation operation;
   private final Counter counter;
   private final long thresholdValue;

   public StatisticsListener(
         final LoadTest test,
         final Operation operation,
         final Counter counter,
         final long thresholdValue)
   {
      this.test = checkNotNull(test);
      this.operation = checkNotNull(operation);
      this.counter = checkNotNull(counter);
      checkArgument(thresholdValue > 0, "thresholdValue must be > 0 [%s]", thresholdValue);
      this.thresholdValue = thresholdValue;
   }

   @Subscribe
   public void handleStatisticEvent(final Statistics stats)
   {
      final long currentValue = stats.get(this.operation, this.counter);
      if (currentValue >= this.thresholdValue)
         this.test.stopTest();
   }
}
