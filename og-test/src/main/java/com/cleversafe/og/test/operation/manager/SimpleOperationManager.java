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
// Date: Apr 7, 2014
// ---------------------

package com.cleversafe.og.test.operation.manager;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.cleversafe.og.operation.Request;
import com.cleversafe.og.operation.Response;
import com.cleversafe.og.operation.manager.OperationManager;
import com.cleversafe.og.operation.manager.OperationManagerException;
import com.cleversafe.og.scheduling.Scheduler;
import com.cleversafe.og.statistic.Statistics;
import com.cleversafe.og.util.consumer.Consumer;
import com.cleversafe.og.util.producer.Producer;
import com.cleversafe.og.util.producer.ProducerException;

public class SimpleOperationManager implements OperationManager
{
   private final Producer<Producer<Request>> requestMix;
   private final List<Consumer<Response>> consumers;
   private final Scheduler scheduler;
   private final Map<Long, Request> pendingRequests;
   private final Statistics stats;

   public SimpleOperationManager(
         final Producer<Producer<Request>> requestMix,
         final List<Consumer<Response>> consumers,
         final Scheduler scheduler,
         final Map<Long, Request> pendingRequests,
         final Statistics stats)
   {
      this.requestMix = checkNotNull(requestMix);
      this.consumers = checkNotNull(consumers);
      this.scheduler = checkNotNull(scheduler);
      this.pendingRequests = checkNotNull(pendingRequests);
      this.stats = checkNotNull(stats);
   }

   @Override
   public Request next() throws OperationManagerException
   {
      this.scheduler.waitForNext();

      final Producer<Request> producer = this.requestMix.produce();
      try
      {
         final Request request = producer.produce();
         this.pendingRequests.put(request.getId(), request);
         return request;
      }
      catch (final ProducerException e)
      {
         throw new OperationManagerException(e);
      }
   }

   @Override
   public void complete(final Response response)
   {
      for (final Consumer<Response> consumer : this.consumers)
      {
         consumer.consume(response);
      }
      this.scheduler.complete(response);
      final Request request = this.pendingRequests.remove(response.getRequestId());
      this.stats.update(request, response);
   }
}