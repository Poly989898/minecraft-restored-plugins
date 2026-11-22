package net.Zrips.CMILib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.jetbrains.annotations.NotNull;

public class FileDownloader {
  @NotNull
  static final List<String> VALID_TYPES = new ArrayList<>(Arrays.asList("yml", "txt", "jar"));

  static final List<String> VALID_SOURCES =
      new ArrayList<>(
          Arrays.asList(
              "https://raw.githubusercontent.com/Zrips/CMILib/master/Translations/",
              "https://www.zrips.net/CMILib/"));

  public void downloadUsingStream(
      @NotNull String urlString, @NotNull String fileName, boolean inform) {
    try {
      if (!VALID_TYPES.contains(
          fileName.split("\\.")[fileName.split("\\.").length - 1].toLowerCase())) {
        return;
      }
    } catch (Exception e) {
      return;
    }

    try {
      if (VALID_SOURCES.stream().noneMatch(urlString::startsWith)) {
        return;
      }
    } catch (Exception e) {
      return;
    }

    CMIScheduler.get()
        .runTaskAsynchronously(
            () -> {
              BufferedInputStream bufferedInputStream = null;
              FileOutputStream fileOutputStream = null;

              try {
                URL url = new URL(urlString);
                bufferedInputStream = new BufferedInputStream(url.openStream());
                fileOutputStream = new FileOutputStream(fileName);
                byte[] buffer = new byte[1024];

                int count;
                while ((count = bufferedInputStream.read(buffer, 0, 1024)) != -1) {
                  fileOutputStream.write(buffer, 0, count);
                }

                fileOutputStream.close();
                bufferedInputStream.close();
                CMIScheduler.runTask(this::afterDownload);
              } catch (Throwable e) {
                File file = new File(fileName);
                if (inform) {
                  CMIMessages.consoleMessage(
                      "Failed to download file '"
                          + urlString
                          + "' to folder '"
                          + file.getParent()
                          + File.separator
                          + "'.");
                  CMIMessages.consoleMessage(
                      "You can do it manually, try again later or simply ignore it.");
                }

                this.failedDownload();
              } finally {
                if (fileOutputStream != null) {
                  try {
                    fileOutputStream.close();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }

                if (bufferedInputStream != null) {
                  try {
                    bufferedInputStream.close();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              }
            });
  }

  public void afterDownload() {}

  public void failedDownload() {}
}
