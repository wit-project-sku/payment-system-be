/* 
 * Copyright (c) WIT Global 
 */
package com.wit.payment.global.tl3800.transport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FileReplayTransport implements TLTransport {

  private final InputStream in;
  private final FileOutputStream out;

  public FileReplayTransport(String inputBinPath, String outputCapturePath) throws Exception {
    this.in = new FileInputStream(inputBinPath);
    this.out = new FileOutputStream(outputCapturePath, true);
  }

  @Override
  public void open() {}

  @Override
  public void close() {
    try {
      in.close();
      out.close();
    } catch (Exception ignored) {
    }
  }

  @Override
  public void write(byte[] bytes) throws Exception {
    out.write(bytes);
    out.flush();
  }

  @Override
  public int readFully(byte[] buf, int len, int timeoutMs) throws Exception {
    int off = 0;
    while (off < len) {
      int r = in.read(buf, off, len - off);
      if (r < 0) {
        break;
      }
      off += r;
    }
    return off;
  }

  @Override
  public int readByte(int timeoutMs) throws Exception {
    return in.read();
  }
}
