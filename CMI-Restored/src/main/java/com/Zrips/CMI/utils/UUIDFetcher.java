package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDFetcher {
  @Deprecated
  public static UUID getUUID(String string) {
    return getUUIDAsync(string).join();
  }

  public static CompletableFuture<UUID> getUUIDAsync(String string) {
    return CompletableFuture.supplyAsync(
        () -> {
          UUID uuid = CMI.getInstance().preFetchUUIDS().get(string);
          if (uuid != null) {
            return uuid;
          }

          String urlText = "https://api.mojang.com/users/profiles/minecraft/" + string;
          StringBuilder stringBuilder = new StringBuilder();
          HttpURLConnection httpURLConnection = null;
          InputStream inputStream = null;
          BufferedReader bufferedReader = null;

          try {
            URL url = new URL(urlText);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() != 200) {
              return null;
            }

            inputStream = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
              stringBuilder.append(line);
              stringBuilder.append('\r').append('\n');
            }
          } catch (Exception exception) {
          } finally {
            try {
              if (bufferedReader != null) {
                bufferedReader.close();
              }
            } catch (IOException ioException) {
            }

            try {
              if (inputStream != null) {
                inputStream.close();
              }
            } catch (IOException ioException2) {
            }

            if (httpURLConnection != null) {
              httpURLConnection.disconnect();
            }
          }

          try {
            Pattern pattern = Pattern.compile("(\")([a-zA-Z0-9]{32})(\")");
            Matcher matcher = pattern.matcher(stringBuilder.toString());
            if (matcher.find()) {
              return UUID.fromString(
                  matcher
                      .group(2)
                      .replaceFirst(
                          "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
                          "$1-$2-$3-$4-$5"));
            }
          } catch (Exception exception2) {
          }

          try {
            String uuidText =
                stringBuilder
                    .toString()
                    .split("\",\"")[0]
                    .split(":")[1]
                    .replace("\"", "")
                    .replaceFirst(
                        "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)",
                        "$1-$2-$3-$4-$5");
            return UUID.fromString(uuidText);
          } catch (Exception exception3) {
            return null;
          }
        },
        CMIThread.getExecutor());
  }
}
