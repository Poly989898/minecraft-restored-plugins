package com.nexomc.nexo.commands;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import kotlin.Metadata;
import kotlin.io.CloseableKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0000¢\u0006\u0002\b\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/commands/LogDumpCommand;", "", "<init>", "()V", "postToPasteBin", "", "text", "postToPasteBin$com_nexomc_core", "com.nexomc:core"}
)
public final class LogDumpCommand {
   @NotNull
   public static final LogDumpCommand INSTANCE = new LogDumpCommand();

   private LogDumpCommand() {
   }

   @Nullable
   public final String postToPasteBin$com_nexomc_core(@NotNull String text) throws Exception {
      byte[] postData = StringsKt.encodeToByteArray(text);
      int postDataLength = postData.length;
      String requestURL = "https://hastebin.com/documents";
      URL url = URI.create(requestURL).toURL();
      URLConnection openConnection = url.openConnection();
      HttpsURLConnection conn = (HttpsURLConnection)openConnection;
      conn.setDoOutput(true);
      conn.setInstanceFollowRedirects(false);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("User-Agent", "Hastebin Java Api");
      conn.setRequestProperty("Content-Length", String.valueOf(postDataLength));
      conn.setUseCaches(false);
      Closeable inputStream = (Closeable)conn.getInputStream();
      Throwable throwable = null;

      String str;
      try {
         InputStream it = (InputStream)inputStream;
         int value = 0;
         Reader reader = (Reader)(new InputStreamReader(it, Charsets.UTF_8));
         short value2 = 8192;
         str = (reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, value2)).readLine();
      } catch (Throwable e) {
         throwable = e;
         throw e;
      } finally {
         CloseableKt.closeFinally(inputStream, throwable);
      }

      return str;
   }
}
