/* Copyright (c) IBM Corporation 2016. All Rights Reserved.
 * Project name: Object Generator
 * This project is licensed under the Apache License 2.0, see LICENSE.
 */

package com.cleversafe.og.json;

public class ObjectConfig {
  public String prefix;
  public SelectionType selection;
  public long minSuffix;
  public long maxSuffix;
  public SelectionConfig<Long> partSize;

  public ObjectConfig() {
    this.prefix = "";
    // FIXME distinguish between default (uuid for writes, object manager for reads/deletes) and
    // custom implementations using a generic type argument; this should be done once guice
    // refactoring occurs to allow dynamic component implementations
    // null selection means use default implementation
    this.selection = null;
    this.minSuffix = 0;
    this.maxSuffix = Long.MAX_VALUE;
    this.partSize = null;
  }
}
