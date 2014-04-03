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
// Date: Mar 27, 2014
// ---------------------

package com.cleversafe.oom.guice;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.cleversafe.oom.api.Producer;
import com.cleversafe.oom.cli.json.Concurrency;
import com.cleversafe.oom.cli.json.FileSize;
import com.cleversafe.oom.cli.json.JSONConfiguration;
import com.cleversafe.oom.distribution.Distribution;
import com.cleversafe.oom.distribution.LogNormalDistribution;
import com.cleversafe.oom.distribution.NormalDistribution;
import com.cleversafe.oom.distribution.UniformDistribution;
import com.cleversafe.oom.guice.annotation.DefaultContainer;
import com.cleversafe.oom.guice.annotation.DefaultEntity;
import com.cleversafe.oom.guice.annotation.DefaultHeaders;
import com.cleversafe.oom.guice.annotation.DefaultHost;
import com.cleversafe.oom.guice.annotation.DefaultId;
import com.cleversafe.oom.guice.annotation.DefaultMetaData;
import com.cleversafe.oom.guice.annotation.DefaultPort;
import com.cleversafe.oom.guice.annotation.DefaultQueryParams;
import com.cleversafe.oom.guice.annotation.DefaultScheme;
import com.cleversafe.oom.http.Scheme;
import com.cleversafe.oom.operation.Entity;
import com.cleversafe.oom.operation.EntityType;
import com.cleversafe.oom.operation.OperationTypeMix;
import com.cleversafe.oom.operation.RequestContext;
import com.cleversafe.oom.scheduling.RequestRateScheduler;
import com.cleversafe.oom.scheduling.Scheduler;
import com.cleversafe.oom.util.Entities;
import com.cleversafe.oom.util.WeightedRandomChoice;
import com.cleversafe.oom.util.producer.Producers;
import com.google.common.math.DoubleMath;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class JsonModule extends AbstractModule
{
   private final JSONConfiguration config;

   public JsonModule(final JSONConfiguration config)
   {
      this.config = checkNotNull(config, "config must not be null");
   }

   @Override
   protected void configure()
   {}

   @Provides
   JSONConfiguration provideJSONConfiguration()
   {
      return this.config;
   }

   @Provides
   @Singleton
   @DefaultId
   Producer<Long> provideDefaultIdProducer()
   {
      return new Producer<Long>()
      {
         private final AtomicLong id = new AtomicLong();

         @Override
         public Long produce(final RequestContext context)
         {
            return this.id.getAndIncrement();
         }
      };
   }

   @Provides
   @Singleton
   @DefaultScheme
   Producer<Scheme> provideDefaultScheme()
   {
      return Producers.of(this.config.getScheme());
   }

   @Provides
   @Singleton
   @DefaultHost
   Producer<String> provideDefaultHost()
   {
      final WeightedRandomChoice<String> wrc = new WeightedRandomChoice<String>();
      for (final String host : this.config.getHosts())
      {
         wrc.addChoice(host);
      }
      return Producers.of(wrc);
   }

   @Provides
   @Singleton
   @DefaultPort
   Producer<Integer> provideDefaultPort()
   {
      return Producers.of(this.config.getPort());
   }

   @Provides
   @Singleton
   @DefaultContainer
   Producer<String> provideDefaultContainer()
   {
      return Producers.of(this.config.getContainer());
   }

   @Provides
   @Singleton
   @DefaultQueryParams
   Producer<Map<String, String>> provideDefaultQueryParams()
   {
      final Map<String, String> queryParams = new HashMap<String, String>();
      return Producers.of(queryParams);
   }

   @Provides
   @Singleton
   @DefaultHeaders
   Producer<Map<String, String>> provideDefaultHeaders()
   {
      final Map<String, String> headers = new HashMap<String, String>();
      return Producers.of(headers);
   }

   @Provides
   @Singleton
   @DefaultEntity
   Producer<Entity> provideDefaultEntity()
   {
      final WeightedRandomChoice<Distribution> wrc = new WeightedRandomChoice<Distribution>();
      for (final FileSize f : this.config.getFilesizes())
      {
         wrc.addChoice(createSizeDistribution(f), f.getWeight());
      }

      return new Producer<Entity>()
      {
         private final WeightedRandomChoice<Distribution> sizes = wrc;

         @Override
         public Entity produce(final RequestContext context)
         {
            return Entities.of(EntityType.RANDOM, (long) this.sizes.nextChoice().nextSample());
         }
      };
   }

   private static Distribution createSizeDistribution(final FileSize filesize)
   {
      // TODO standardize terminology; mean or average
      final double mean = filesize.getAverage() * filesize.getAverageUnit().toBytes(1);
      final double spread = filesize.getSpread() * filesize.getSpreadUnit().toBytes(1);
      switch (filesize.getDistribution())
      {
      // TODO determine how to expose these in json configuration in a way that makes sense
      // mean/average/min/max?
         case NORMAL :
            return new NormalDistribution(mean, spread);
         case LOGNORMAL :
            return new LogNormalDistribution(mean, spread);
         default :
            return new UniformDistribution(mean, spread);
      }
   }

   @Provides
   @Singleton
   @DefaultMetaData
   Producer<Map<String, String>> provideDefaultMetaData()
   {
      final Map<String, String> metadata = new HashMap<String, String>();
      return Producers.of(metadata);
   }

   @Provides
   @Singleton
   OperationTypeMix provideOperationTypeMix()
   {
      double write = this.config.getWrite();
      final double read = this.config.getRead();
      final double delete = this.config.getDelete();
      if (allEqual(0.0, write, read, delete))
         write = 100.0;

      final long floorBytes = (long) (this.config.getFloor() / 100.0 * this.config.getCapacity());
      final long ceilBytes = (long) (this.config.getCeiling() / 100.0 * this.config.getCapacity());
      return new OperationTypeMix(write, read, delete, floorBytes, ceilBytes);
   }

   private boolean allEqual(final double compare, final double... values)
   {
      final double err = Math.pow(0.1, 6);
      for (final double v : values)
      {
         if (!DoubleMath.fuzzyEquals(v, compare, err))
            return false;
      }
      return true;
   }

   @Provides
   @Singleton
   Scheduler provideScheduler()
   {
      final Concurrency concurrency = this.config.getConcurrency();
      final Distribution count = new UniformDistribution(concurrency.getCount(), 0.0);
      return new RequestRateScheduler(count, concurrency.getUnit());
   }
}
