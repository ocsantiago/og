/*
 * Copyright (C) 2005-2015 Cleversafe, Inc. All rights reserved.
 * 
 * Contact Information: Cleversafe, Inc. 222 South Riverside Plaza Suite 1700 Chicago, IL 60606, USA
 * 
 * licensing@cleversafe.com
 */

package com.cleversafe.og.s3.v4;

import java.util.Map;
import java.util.Map.Entry;

import com.cleversafe.og.http.HttpAuth;
import com.google.common.collect.Maps;

public abstract class AWSAuthV4Base implements HttpAuth {
  protected final String regionName;
  protected final String serviceName;
  protected final Long forcedDate;

  /**
   * @param regionName region name to use in requests. Can really be any string and doesn't matter
   *        much for a dsnet.
   * @param serviceName service name to use in requests. Should be usually be "s3".
   * @param forcedDate optional timestamp to use for all requests. If null, the current time will be
   *        used.
   */
  public AWSAuthV4Base(String regionName, String serviceName, Long forcedDate) {
    this.regionName = regionName;
    this.serviceName = serviceName;
    this.forcedDate = forcedDate;
  }

  protected Map<String, String> filterOutOgHeaders(Map<String, String> headers) {
    final Map<String, String> filtered = Maps.newHashMap();
    for (Entry<String, String> header : headers.entrySet()) {
      if (!header.getKey().startsWith("x-og")) {
        filtered.put(header.getKey(), header.getValue());
      }
    }
    return filtered;
  }
}
