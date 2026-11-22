package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class FileDownloader {
  static final List<String> VALID_TYPES =
      new ArrayList<>(Arrays.asList("dat", "yml", "txt", "jar"));
  static final List<String> VALID_SOURCES =
      new ArrayList<>(
          Arrays.asList(
              "https://raw.githubusercontent.com/Zrips/CMI/master/Settings/DeathMessages/",
              "https://raw.githubusercontent.com/Zrips/CMI/master/Translations/",
              "https://www.zrips.net/wp-content/uploads/2019/02/",
              "https://www.zrips.net/CMILib/"));

  public void downloadUsingStream(String string, String filePath) {
    try {
      if (!VALID_TYPES.contains(
          filePath.split("\\.")[filePath.split("\\.").length - 1].toLowerCase())) {
        return;
      }
    } catch (Exception exception) {
      return;
    }

    try {
      if (VALID_SOURCES.stream().noneMatch(string::startsWith)) {
        return;
      }
    } catch (Exception exception2) {
      return;
    }

    CMIScheduler.runTaskAsynchronously(
        CMI.getInstance(),
        () -> {
          try {
            BufferedInputStream bufferedInputStream =
                new BufferedInputStream(new URL(string).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            byte[] bs = new byte[1024];
            int value = 0;

            while ((value = bufferedInputStream.read(bs, 0, 1024)) != -1) {
              fileOutputStream.write(bs, 0, value);
            }

            fileOutputStream.close();
            bufferedInputStream.close();
            CMIScheduler.runTask(CMI.getInstance(), this::afterDownload);
          } catch (Throwable throwable) {
            File file = new File(filePath);
            CMI.getInstance()
                .consoleMessage(
                    "Failed to download "
                        + string
                        + " file into "
                        + file.getParent()
                        + File.separator
                        + " folder ");
            CMI.getInstance()
                .consoleMessage("You can do it manually or try again later or simply ignore it");
            this.failedDownload();
          }
        });
  }

  public void afterDownload() {}

  public void failedDownload() {}
}
