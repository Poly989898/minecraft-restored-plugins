package com.ticxo.modelengine.core.generator.skin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ticxo.modelengine.api.generator.skin.SkinGeneratorService;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.utils.logger.TLogger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Generates skins through the MineSkin v2 queue API, using plain HTTP.
 * Protocol (from the MineSkin Java client): POST /v2/queue as multipart with the image in the
 * "file" field, the job id in "job.id", then GET /v2/queue/{id} until the response contains the
 * skin; the texture URL is at "skin.texture.url.skin".
 */
public class MineSkinGeneratorService implements SkinGeneratorService {
   private static final String BASE_URL = "https://api.mineskin.org";
   private static final int MAX_POLLS = 60;

   private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
   private volatile String apiKey;

   @Override
   public boolean isEnabled() {
      return this.apiKey != null;
   }

   @Override
   public void onReload() {
      String key = ConfigProperty.MINESKIN_API_KEY.getString();
      if (key != null && !key.isBlank()) {
         this.apiKey = key.trim();
      } else {
         this.apiKey = null;
         TLogger.warn("Unable to activate MineSkin skin generation: Empty API Key");
      }
   }

   @Override
   public CompletableFuture<String> generate(byte[] rawImage) {
      String key = this.apiKey;
      if (key == null) {
         return CompletableFuture.failedFuture(new IllegalStateException("MineSkin is disabled: no API key"));
      }
      return CompletableFuture.supplyAsync(() -> {
         try {
            return this.generateBlocking(key, rawImage);
         } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
               Thread.currentThread().interrupt();
            }
            throw new CompletionException(e);
         }
      });
   }

   private String generateBlocking(String key, byte[] rawImage) throws IOException, InterruptedException {
      String boundary = "ModelEngine" + UUID.randomUUID().toString().replace("-", "");
      HttpRequest submit = HttpRequest.newBuilder(URI.create(BASE_URL + "/v2/queue"))
         .timeout(Duration.ofSeconds(30))
         .header("Authorization", "Bearer " + key)
         .header("Content-Type", "multipart/form-data; boundary=" + boundary)
         .header("User-Agent", "ModelEngine")
         .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody(boundary, rawImage)))
         .build();

      JsonObject root = send(submit);
      String jobId = jobId(root);
      for (int poll = 0; poll < MAX_POLLS; poll++) {
         String texture = textureUrl(root);
         if (texture != null) {
            return texture;
         }
         if ("failed".equalsIgnoreCase(jobStatus(root))) {
            throw new IOException("MineSkin job " + jobId + " failed");
         }
         Thread.sleep(1000L); // MineSkin asks for at most one status check per second
         root = send(HttpRequest.newBuilder(URI.create(BASE_URL + "/v2/queue/" + jobId))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", "Bearer " + key)
            .header("User-Agent", "ModelEngine")
            .GET()
            .build());
      }
      throw new IOException("MineSkin job " + jobId + " did not finish in " + MAX_POLLS + " seconds");
   }

   private JsonObject send(HttpRequest request) throws IOException, InterruptedException {
      HttpResponse<String> response = this.http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      String body = response.body();
      int code = response.statusCode();
      if (code < 200 || code > 299) {
         throw new IOException("MineSkin HTTP " + code + ": " + (body.length() > 200 ? body.substring(0, 200) : body));
      }
      JsonElement parsed = new JsonParser().parse(body);
      if (!parsed.isJsonObject()) {
         throw new IOException("MineSkin returned an unexpected response");
      }
      return parsed.getAsJsonObject();
   }

   private static String jobId(JsonObject root) throws IOException {
      JsonObject job = object(root, "job");
      if (job == null || !job.has("id")) {
         throw new IOException("MineSkin response has no job id");
      }
      return job.get("id").getAsString();
   }

   private static String jobStatus(JsonObject root) {
      JsonObject job = object(root, "job");
      return job != null && job.has("status") && !job.get("status").isJsonNull() ? job.get("status").getAsString() : null;
   }

   private static String textureUrl(JsonObject root) {
      JsonObject skin = object(root, "skin");
      JsonObject texture = skin == null ? null : object(skin, "texture");
      JsonObject urls = texture == null ? null : object(texture, "url");
      if (urls != null && urls.has("skin") && !urls.get("skin").isJsonNull()) {
         return urls.get("skin").getAsString();
      }
      return null;
   }

   private static JsonObject object(JsonObject parent, String key) {
      JsonElement element = parent.get(key);
      return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
   }

   private static byte[] multipartBody(String boundary, byte[] image) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      String head = "--" + boundary + "\r\n"
         + "Content-Disposition: form-data; name=\"file\"; filename=\"skin.png\"\r\n"
         + "Content-Type: image/png\r\n\r\n";
      out.writeBytes(head.getBytes(StandardCharsets.UTF_8));
      out.writeBytes(image);
      out.writeBytes("\r\n".getBytes(StandardCharsets.UTF_8));
      String visibility = "--" + boundary + "\r\n"
         + "Content-Disposition: form-data; name=\"visibility\"\r\n\r\n"
         + "unlisted\r\n"
         + "--" + boundary + "--\r\n";
      out.writeBytes(visibility.getBytes(StandardCharsets.UTF_8));
      return out.toByteArray();
   }
}
