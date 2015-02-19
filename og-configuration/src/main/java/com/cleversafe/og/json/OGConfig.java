/*
 * Copyright (C) 2005-2015 Cleversafe, Inc. All rights reserved.
 * 
 * Contact Information: Cleversafe, Inc. 222 South Riverside Plaza Suite 1700 Chicago, IL 60606, USA
 * 
 * licensing@cleversafe.com
 */

package com.cleversafe.og.json;

import java.util.List;
import java.util.Map;

import com.cleversafe.og.api.Data;
import com.cleversafe.og.http.Api;
import com.cleversafe.og.http.Scheme;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OGConfig {
  Scheme scheme;
  SelectionType hostSelection;
  List<HostConfig> host;
  Integer port;
  Api api;
  String uriRoot;
  String container;
  Map<String, String> headers;
  OperationConfig write;
  OperationConfig read;
  OperationConfig delete;
  SelectionType filesizeSelection;
  List<FilesizeConfig> filesize;
  Data data;
  ConcurrencyConfig concurrency;
  AuthenticationConfig authentication;
  ClientConfig client;
  StoppingConditionsConfig stoppingConditions;
  ObjectManagerConfig objectManager;

  public OGConfig() {
    this.scheme = Scheme.HTTP;
    this.hostSelection = SelectionType.ROUNDROBIN;
    this.host = Lists.newArrayList();
    this.port = null;
    this.api = Api.SOH;
    this.uriRoot = null;
    this.container = null;
    this.headers = Maps.newLinkedHashMap();
    this.write = new OperationConfig();
    this.read = new OperationConfig();
    this.delete = new OperationConfig();
    this.filesizeSelection = SelectionType.RANDOM;
    this.filesize = Lists.newArrayList();
    this.filesize.add(new FilesizeConfig());
    this.data = Data.RANDOM;
    this.concurrency = new ConcurrencyConfig();
    this.authentication = new AuthenticationConfig();
    this.client = new ClientConfig();
    this.stoppingConditions = new StoppingConditionsConfig();
    this.objectManager = new ObjectManagerConfig();
  }

  public Scheme getScheme() {
    return this.scheme;
  }

  public SelectionType getHostSelection() {
    return this.hostSelection;
  }

  public List<HostConfig> getHost() {
    return this.host;
  }

  public Integer getPort() {
    return this.port;
  }

  public Api getApi() {
    return this.api;
  }

  public String getUriRoot() {
    return this.uriRoot;
  }

  public String getContainer() {
    return this.container;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public OperationConfig getWrite() {
    return this.write;
  }

  public OperationConfig getRead() {
    return this.read;
  }

  public OperationConfig getDelete() {
    return this.delete;
  }

  public SelectionType getFilesizeSelection() {
    return this.filesizeSelection;
  }

  public List<FilesizeConfig> getFilesize() {
    return this.filesize;
  }

  public Data getData() {
    return this.data;
  }

  public ConcurrencyConfig getConcurrency() {
    return this.concurrency;
  }

  public AuthenticationConfig getAuthentication() {
    return this.authentication;
  }

  public ClientConfig getClient() {
    return client;
  }

  public StoppingConditionsConfig getStoppingConditions() {
    return this.stoppingConditions;
  }

  public ObjectManagerConfig getObjectManager() {
    return this.objectManager;
  }
}