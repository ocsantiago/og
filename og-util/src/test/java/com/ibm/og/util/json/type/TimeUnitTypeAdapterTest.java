/* Copyright (c) IBM Corporation 2016. All Rights Reserved.
 * Project name: Object Generator
 * This project is licensed under the Apache License 2.0, see LICENSE.
 */

package com.ibm.og.util.json.type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class TimeUnitTypeAdapterTest {
  private TimeUnitTypeAdapter typeAdapter;

  @Before
  public void before() {
    this.typeAdapter = new TimeUnitTypeAdapter();
  }

  @Test
  public void write() throws IOException {
    final JsonWriter writer = mock(JsonWriter.class);
    this.typeAdapter.write(writer, TimeUnit.SECONDS);
    verify(writer).value("seconds");
  }

  @Test
  public void read() throws IOException {
    final JsonReader reader = mock(JsonReader.class);
    when(reader.nextString()).thenReturn("s");
    assertThat(this.typeAdapter.read(reader), is(TimeUnit.SECONDS));
  }
}
