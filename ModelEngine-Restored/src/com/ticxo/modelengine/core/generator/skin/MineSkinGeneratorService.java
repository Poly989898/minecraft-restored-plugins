package com.ticxo.modelengine.core.generator.skin;

import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.io.ByteArrayInputStream;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import lombok.Getter;
import org.mineskin.JsoupRequestHandler;
import org.mineskin.MineSkinClient;
import org.mineskin.data.Visibility;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.options.GenerateQueueOptions;
import org.mineskin.request.GenerateRequest;
import org.mineskin.response.MineSkinResponse;

public class MineSkinGeneratorService implements SkinGeneratorService {
   @Getter
   private boolean enabled;
   private MineSkinClient client;

   @Override
   public void onReload() {
      String key = ConfigProperty.MINESKIN_API_KEY.getString();
      if (key != null && !key.isBlank()) {
         this.client = MineSkinClient.builder()
            .requestHandler(JsoupRequestHandler::new)
            .userAgent("ModelEngine/R4.0.9")
            .apiKey(key)
            .generateQueueOptions(GenerateQueueOptions.createAuto())
            .build();
         quietenClientLogger();
         this.enabled = true;
      } else {
         TLogger.warn("Unable to activate MineSkin skin generation: Empty API Key");
         this.enabled = false;
      }
   }

   private static void quietenClientLogger() {
      Logger.getLogger("org.mineskin.MineSkinClient").setFilter(record -> {
         Throwable thrown = record.getThrown();
         if (thrown == null) {
            return true;
         }

         TLogger.warn("Failed to communicate with MineSkin: " + summarize(thrown));
         return false;
      });
   }

   private static String summarize(Throwable thrown) {
      for (Throwable cause = thrown; cause != null; cause = cause.getCause()) {
         if (cause instanceof MineSkinRequestException requestException) {
            MineSkinResponse<?> response = requestException.getResponse();
            return response != null ? "HTTP " + response.getStatus() + " - " + requestException.getMessage() : requestException.getMessage();
         }

         if (cause.getCause() == cause) {
            break;
         }
      }

      Throwable root = thrown;

      while (root.getCause() != null && root.getCause() != root) {
         root = root.getCause();
      }

      String message = root.getMessage();
      return root.getClass().getSimpleName() + (message != null ? ": " + firstLine(message) : "");
   }

   private static String firstLine(String message) {
      int newline = message.indexOf(10);
      String line = newline < 0 ? message : message.substring(0, newline);
      return line.length() > 120 ? line.substring(0, 120) + "..." : line;
   }

   @Override
   public CompletableFuture<String> generate(byte[] rawImage) {
      GenerateRequest request = GenerateRequest.upload(new ByteArrayInputStream(rawImage)).visibility(Visibility.UNLISTED);
      return this.client
         .queue()
         .submit(request)
         .thenCompose(queueResponse -> queueResponse.getJob().waitForCompletion(this.client))
         .thenCompose(jobResponse -> jobResponse.getOrLoadSkin(this.client))
         .thenApply(skinInfo -> skinInfo.texture().url().skin());
   }
}
