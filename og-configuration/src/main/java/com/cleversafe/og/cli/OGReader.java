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
// Date: Feb 13, 2014
// ---------------------

package com.cleversafe.og.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.common.io.BaseEncoding;
import com.martiansoftware.jsap.JSAPResult;

public class OGReader extends AbstractCLI
{
   private static final String JSAP_RESOURCE_NAME = "ogreader.jsap";
   // TODO place this constant in util somewhere?
   private static int ID_LENGTH = 18;

   public static void main(final String[] args)
   {
      final JSAPResult jsapResult = processArgs(JSAP_RESOURCE_NAME, args);

      try
      {
         readIdFile(jsapResult.getFile("object_file"));
      }
      catch (final IOException e)
      {
         _consoleLogger.error("Exception reading object file", e);
         System.exit(UNKNOWN_ERROR);
      }
   }

   public static void readIdFile(final File filename) throws IOException
   {
      FileInputStream in = null;
      try
      {
         in = new FileInputStream(filename);
         final byte[] objectID = new byte[ID_LENGTH];
         int numRecords = 0;
         while (in.read(objectID, 0, ID_LENGTH) == ID_LENGTH)
         {
            numRecords++;
            final String id = BaseEncoding.base16().lowerCase().encode(objectID);
            _consoleLogger.info(id);
         }
         _consoleLogger.info("{} records", numRecords);
      }
      finally
      {
         if (in != null)
            in.close();
      }
   }
}