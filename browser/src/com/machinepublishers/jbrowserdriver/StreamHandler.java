/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2015 Machine Publishers, LLC
 * ops@machinepublishers.com | screenslicer.com | machinepublishers.com
 * Cincinnati, Ohio, USA
 *
 * You can redistribute this program and/or modify it under the terms of the GNU Affero General Public
 * License version 3 as published by the Free Software Foundation.
 *
 * "ScreenSlicer", "jBrowserDriver", "Machine Publishers", and "automatic, zero-config web scraping"
 * are trademarks of Machine Publishers, LLC.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License version 3 for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License version 3 along with this
 * program. If not, see http://www.gnu.org/licenses/
 * 
 * For general details about how to investigate and report license violations, please see
 * https://www.gnu.org/licenses/gpl-violation.html and email the author, ops@machinepublishers.com
 */
package com.machinepublishers.jbrowserdriver;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class StreamHandler implements URLStreamHandlerFactory {
  private static final StreamConnectionHandler handler = new StreamConnectionHandler();

  StreamHandler() {}

  static class StreamConnectionHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
      StackTraceElement[] trace = new Throwable().getStackTrace();
      if (trace.length > 2
          && "com.sun.webkit.network.URLLoader".equals(trace[2].getClassName())) {
        return new StreamConnection(url);
      }
      if ("http".equals(url.getProtocol())) {
        return HttpDefault.open(url);
      }
      if ("https".equals(url.getProtocol())) {
        return HttpsDefault.open(url);
      }
      throw new IllegalStateException();
    }
  }

  static class HttpDefault extends sun.net.www.protocol.http.Handler {
    private static HttpDefault instance = new HttpDefault();

    public static URLConnection open(URL url) throws IOException {
      return instance.openConnection(url);
    }
  }

  static class HttpsDefault extends sun.net.www.protocol.https.Handler {
    private static HttpsDefault instance = new HttpsDefault();

    public static URLConnection open(URL url) throws IOException {
      return instance.openConnection(url);
    }
  }

  @Override
  public URLStreamHandler createURLStreamHandler(String protocol) {
    if ("http".equals(protocol) || "https".equals(protocol)) {
      return handler;
    }
    if ("about".equals(protocol)) {
      return new com.sun.webkit.network.about.Handler();
    }
    if ("data".equals(protocol)) {
      return new com.sun.webkit.network.data.Handler();
    }
    if ("file".equals(protocol)) {
      return new sun.net.www.protocol.file.Handler();
    }
    if ("ftp".equals(protocol)) {
      return new sun.net.www.protocol.ftp.Handler();
    }
    if ("jar".equals(protocol)) {
      return new sun.net.www.protocol.jar.Handler();
    }
    if ("mailto".equals(protocol)) {
      return new sun.net.www.protocol.mailto.Handler();
    }
    if ("netdoc".equals(protocol)) {
      return new sun.net.www.protocol.netdoc.Handler();
    }
    throw new InternalError();
  }

}
