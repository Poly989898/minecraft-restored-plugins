package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class CMIFileAppender {
  private FileWriter writer;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[HH:mm:ss] ");

  public CMIFileAppender(String string) {
    try {
      this.writer =
          new FileWriter(CMI.getInstance().getDataFolder() + File.separator + string, true);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }

  public void appendData(Object object) {
    CompletableFuture.runAsync(
        () -> {
          synchronized (this.writer) {
            try {
              this.writer.write(
                  LocalTime.now().format(formatter) + object + System.lineSeparator());
              this.writer.flush();
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          }
        });
  }

  public void close() {
    try {
      this.writer.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }
  }
}
