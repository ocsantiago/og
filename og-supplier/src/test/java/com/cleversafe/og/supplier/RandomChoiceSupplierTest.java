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
// Date: Jun 28, 2014
// ---------------------

package com.cleversafe.og.supplier;

import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.cleversafe.og.supplier.Suppliers;
import com.cleversafe.og.supplier.RandomChoiceSupplier;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

public class RandomChoiceSupplierTest
{
   @Test(expected = IllegalArgumentException.class)
   public void testNoChoice()
   {
      new RandomChoiceSupplier.Builder<Integer>().build();
   }

   @Test(expected = NullPointerException.class)
   public void testNullChoice()
   {
      new RandomChoiceSupplier.Builder<Integer>().withChoice(null).build();
   }

   @Test(expected = NullPointerException.class)
   public void testNullRandom()
   {
      new RandomChoiceSupplier.Builder<Integer>().withChoice(1).withRandom(null).build();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNegativeWeight()
   {
      new RandomChoiceSupplier.Builder<Integer>().withChoice(1, -1.0).build();
   }

   @Test(expected = IllegalArgumentException.class)
   public void testZeroWeight()
   {
      new RandomChoiceSupplier.Builder<Integer>().withChoice(1, 0.0).build();
   }

   @Test
   public void testOneChoice()
   {
      final Supplier<Integer> p = new RandomChoiceSupplier.Builder<Integer>().withChoice(1).build();
      for (int i = 0; i < 10; i++)
      {
         Assert.assertEquals(Integer.valueOf(1), p.get());
      }
   }

   @Test
   public void testNChoices()
   {
      final RandomChoiceSupplier.Builder<Integer> b = new RandomChoiceSupplier.Builder<Integer>();
      b.withChoice(1, 33);
      b.withChoice(2, Suppliers.of(33.5));
      b.withChoice(3, Suppliers.of(33));
      b.withRandom(new Random());
      final Supplier<Integer> p = b.build();

      final Map<Integer, Integer> counts = Maps.newHashMap();
      counts.put(1, 0);
      counts.put(2, 0);
      counts.put(3, 0);

      for (int i = 0; i < 100; i++)
      {
         final Integer nextInt = p.get();
         counts.put(nextInt, counts.get(nextInt) + 1);
      }

      for (final int count : counts.values())
      {
         Assert.assertTrue(count > 0);
      }
   }
}