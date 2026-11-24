package me.TechsCode.UltraPermissions.base.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.update.customRequests.HttpGetWithBody;
import me.TechsCode.UltraPermissions.base.update.responses.CreateUpdateRequestResponse;
import me.TechsCode.UltraPermissions.base.update.responses.GetPluginsResponse;
import me.TechsCode.UltraPermissions.base.update.responses.GetUpdateRequestResponse;
import me.TechsCode.UltraPermissions.base.update.responses.IsAvailableResponse;
import me.TechsCode.UltraPermissions.base.update.responses.IsUpToDateResponse;
import me.TechsCode.UltraPermissions.base.update.responses.UpdateUpdateRequestResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class UpdateServer {
   private final String updateServerBaseUrl = "https://update.techscode.com";
   private final String updateServerApiToken = "tqhcgRUCzBYjDuDEmP3MDYF6PtVGsc3RjTTvnsvT";
   private final CloseableHttpClient httpClient = HttpClients.createDefault();
   private boolean hasJarDownloadFailed = false;

   public UpdateServer() {
   }

   private void close() throws IOException {
      this.httpClient.close();
   }

   public JsonObject getRequest(String text, JsonObject jsonObject) {
      try {
         HttpGetWithBody httpGetWithBody = new HttpGetWithBody("https://update.techscode.com" + text);
         httpGetWithBody.addHeader("Content-Type", "application/json");
         httpGetWithBody.addHeader("Authorization", "tqhcgRUCzBYjDuDEmP3MDYF6PtVGsc3RjTTvnsvT");
         StringEntity stringEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
         httpGetWithBody.setEntity(stringEntity);
         CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpGetWithBody);

         JsonObject result;
         try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            JsonObject parsed = null;
            if (entity != null) {
               Gson gson = new Gson();
               String body = EntityUtils.toString(entity);
               parsed = (JsonObject)gson.fromJson(body, JsonObject.class);
            }

            result = parsed;
         } catch (Throwable exception) {
            if (closeableHttpResponse != null) {
               try {
                  closeableHttpResponse.close();
               } catch (Throwable closeException) {
                  exception.addSuppressed(closeException);
               }
            }

            throw exception;
         }

         if (closeableHttpResponse != null) {
            closeableHttpResponse.close();
         }

         return result;
      } catch (Exception exception) {
         return null;
      }
   }

   public JsonObject postRequest(String text, JsonObject jsonObject) {
      try {
         HttpPost httpPost = new HttpPost("https://update.techscode.com" + text);
         httpPost.addHeader("Content-Type", "application/json");
         httpPost.addHeader("Authorization", "tqhcgRUCzBYjDuDEmP3MDYF6PtVGsc3RjTTvnsvT");
         StringEntity stringEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
         httpPost.setEntity(stringEntity);
         CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpPost);

         JsonObject result;
         try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            JsonObject parsed = null;
            if (entity != null) {
               Gson gson = new Gson();
               String body = EntityUtils.toString(entity);
               parsed = (JsonObject)gson.fromJson(body, JsonObject.class);
            }

            result = parsed;
         } catch (Throwable exception) {
            if (closeableHttpResponse != null) {
               try {
                  closeableHttpResponse.close();
               } catch (Throwable closeException) {
                  exception.addSuppressed(closeException);
               }
            }

            throw exception;
         }

         if (closeableHttpResponse != null) {
            closeableHttpResponse.close();
         }

         return result;
      } catch (Exception exception) {
         return null;
      }
   }

   public JsonObject patchRequest(String text, JsonObject jsonObject) {
      try {
         HttpPatch httpPatch = new HttpPatch("https://update.techscode.com" + text);
         httpPatch.addHeader("Content-Type", "application/json");
         httpPatch.addHeader("Authorization", "tqhcgRUCzBYjDuDEmP3MDYF6PtVGsc3RjTTvnsvT");
         StringEntity stringEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
         httpPatch.setEntity(stringEntity);
         CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpPatch);

         JsonObject result;
         try {
            HttpEntity entity = closeableHttpResponse.getEntity();
            JsonObject parsed = null;
            if (entity != null) {
               Gson gson = new Gson();
               String body = EntityUtils.toString(entity);
               parsed = (JsonObject)gson.fromJson(body, JsonObject.class);
            }

            result = parsed;
         } catch (Throwable exception) {
            if (closeableHttpResponse != null) {
               try {
                  closeableHttpResponse.close();
               } catch (Throwable closeException) {
                  exception.addSuppressed(closeException);
               }
            }

            throw exception;
         }

         if (closeableHttpResponse != null) {
            closeableHttpResponse.close();
         }

         return result;
      } catch (Exception exception) {
         return null;
      }
   }

   public IsAvailableResponse isAvailable() {
      JsonObject jsonObject = new JsonObject();
      JsonObject request = this.getRequest("/api/status", jsonObject);
      if (request == null) {
         return new IsAvailableResponse(500);
      }

      int count = request.has("code") ? request.get("code").getAsInt() : 500;
      return new IsAvailableResponse(count);
   }

   public IsUpToDateResponse isUpToDate(TechPlugin<?> techPlugin) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("plugin_name", techPlugin.getName().toLowerCase());
      jsonObject.addProperty("current_version", techPlugin.getVersion());
      JsonObject request = this.getRequest("/api/plugin/version-check", jsonObject);
      if (request == null) {
         return new IsUpToDateResponse(500, false, null, "Invalid Response");
      }

      int count = request.has("code") ? request.get("code").getAsInt() : 500;
      boolean flag = request.has("is_up_to_date") && request.get("is_up_to_date").getAsBoolean();
      String text = request.has("latest_version") ? request.get("latest_version").getAsString() : null;
      String message = request.has("message") ? request.get("message").getAsString() : null;
      return new IsUpToDateResponse(count, flag, text, message);
   }

   public GetPluginsResponse getPlugins(String text) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("update_token", text);
      JsonObject request = this.getRequest("/api/get-plugins", jsonObject);
      if (request == null) {
         return new GetPluginsResponse(500, null, "Invalid Response");
      }

      int count = request.has("code") ? request.get("code").getAsInt() : 500;
      JsonObject data = request.has("data") ? request.get("data").getAsJsonObject() : null;
      String message = request.has("message") ? request.get("message").getAsString() : null;
      return new GetPluginsResponse(count, data, message);
   }

   public CreateUpdateRequestResponse createUpdateRequest(TechPlugin<?> techPlugin) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("plugin_name", techPlugin.getName().toLowerCase());
      jsonObject.addProperty("current_version", techPlugin.getVersion());
      JsonObject response = this.postRequest("/api/plugin/request-update", jsonObject);
      if (response == null) {
         return new CreateUpdateRequestResponse(500, null, "Invalid Response");
      }

      int count = response.has("code") ? response.get("code").getAsInt() : 500;
      String text = response.has("update_token") ? response.get("update_token").getAsString() : null;
      String message = response.has("message") ? response.get("message").getAsString() : null;
      return new CreateUpdateRequestResponse(count, text, message);
   }

   public GetUpdateRequestResponse getUpdateRequest(String text) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("update_token", text);
      JsonObject request = this.getRequest("/api/plugin/request-update", jsonObject);
      if (request == null) {
         return new GetUpdateRequestResponse(500, null, "Invalid Response");
      }

      int count = request.has("code") ? request.get("code").getAsInt() : 500;
      JsonObject updateRequestJson = request.has("update_request") ? request.get("update_request").getAsJsonObject() : null;
      String message = request.has("message") ? request.get("message").getAsString() : null;
      UpdateRequest updateRequest = null;
      if (updateRequestJson != null) {
         updateRequest = new UpdateRequest(updateRequestJson);
      }

      return new GetUpdateRequestResponse(count, updateRequest, message);
   }

   public UpdateUpdateRequestResponse updateUpdateRequest(String text, TechPlugin<?> techPlugin, String updateTo) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("update_token", text);
      jsonObject.addProperty("plugin_name", techPlugin.getName().toLowerCase());
      jsonObject.addProperty("current_version", techPlugin.getVersion());
      jsonObject.addProperty("update_to", updateTo);
      JsonObject response = this.patchRequest("/api/plugin/request-update", jsonObject);
      if (response == null) {
         return new UpdateUpdateRequestResponse(500, null, "Invalid Response");
      }

      int count = response.has("code") ? response.get("code").getAsInt() : 500;
      JsonObject updateRequestJson = response.has("update_request") ? response.get("update_request").getAsJsonObject() : null;
      String message = response.has("message") ? response.get("message").getAsString() : null;
      UpdateRequest updateRequest = null;
      if (updateRequestJson != null) {
         updateRequest = new UpdateRequest(updateRequestJson);
      }

      return new UpdateUpdateRequestResponse(count, updateRequest, message);
   }

   public Thread downloadJar(TechPlugin<?> techPlugin, String text) {
      Thread thread = new Thread(() -> {
         String name = techPlugin.getName();
         GetUpdateRequestResponse updateRequest = this.getUpdateRequest(text);
         if (updateRequest.getStatusCode() != 200) {
            techPlugin.log("Error downloading plugin " + name);
            techPlugin.log("Response code: " + updateRequest.getStatusCode());
            this.hasJarDownloadFailed = true;
         } else {
            UpdateRequest innerRequest = updateRequest.getUpdateRequest();
            if (innerRequest == null) {
               techPlugin.log("Error downloading plugin " + name);
               techPlugin.log("Response was empty");
               this.hasJarDownloadFailed = true;
            } else {
               try {
                  HttpGetWithBody httpGetWithBody = new HttpGetWithBody("https://update.techscode.com/api/plugin/download-jar");
                  httpGetWithBody.addHeader("Content-Type", "application/json");
                  httpGetWithBody.addHeader("Accept", "application/zip");
                  httpGetWithBody.addHeader("Authorization", "tqhcgRUCzBYjDuDEmP3MDYF6PtVGsc3RjTTvnsvT");
                  JsonObject jsonObject = new JsonObject();
                  jsonObject.addProperty("update_token", text);
                  StringEntity stringEntity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
                  httpGetWithBody.setEntity(stringEntity);
                  CloseableHttpResponse closeableHttpResponse = this.httpClient.execute(httpGetWithBody);

                  label85: {
                     label92: {
                        try {
                           int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
                           HttpEntity entity = closeableHttpResponse.getEntity();
                           if (statusCode != 200) {
                              techPlugin.log("Error downloading plugin " + name);
                              techPlugin.log("Response code: " + statusCode);
                              String body = EntityUtils.toString(entity, "UTF-8");
                              techPlugin.log("");
                              techPlugin.log("----------------------------------------");
                              techPlugin.log(body);
                              techPlugin.log("----------------------------------------");
                              this.hasJarDownloadFailed = true;
                              break label92;
                           }

                           techPlugin.log("Downloading new plugin (" + name + ")");

                           try {
                              File file = new File(techPlugin.getPluginFolder().getParent());
                              File updateDir = new File(file, "update");
                              File jarFile = new File(updateDir, techPlugin.getName() + "-" + innerRequest.getUpdateToVersionName() + ".jar");
                              if (!updateDir.exists()) {
                                 boolean flag = updateDir.mkdirs();
                                 if (!flag) {
                                    techPlugin.log("Error creating update folder");
                                    this.hasJarDownloadFailed = true;
                                    break label85;
                                 }

                                 techPlugin.log("Update folder created");
                              }

                              if (entity != null) {
                                 InputStream content = entity.getContent();
                                 Files.copy(content, Paths.get(jarFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                                 techPlugin.log("Download completed (" + name + ")");
                                 techPlugin.log("Please restart the server to complete the update.");
                                 this.hasJarDownloadFailed = false;
                              } else {
                                 techPlugin.log("Error downloading plugin " + name);
                                 techPlugin.log("Response was empty");
                                 this.hasJarDownloadFailed = true;
                              }
                           } catch (Exception exception) {
                              techPlugin.log("Error downloading plugin " + name);
                              techPlugin.log(exception.getMessage());
                              this.hasJarDownloadFailed = true;
                           }
                        } catch (Throwable exception) {
                           if (closeableHttpResponse != null) {
                              try {
                                 closeableHttpResponse.close();
                              } catch (Throwable closeException) {
                                 exception.addSuppressed(closeException);
                              }
                           }

                           throw exception;
                        }

                        if (closeableHttpResponse != null) {
                           closeableHttpResponse.close();
                        }

                        return;
                     }

                     if (closeableHttpResponse != null) {
                        closeableHttpResponse.close();
                     }

                     return;
                  }

                  if (closeableHttpResponse != null) {
                     closeableHttpResponse.close();
                  }
               } catch (Exception exception) {
                  techPlugin.log("Error downloading plugin " + name);
                  techPlugin.log(exception.getMessage());
                  this.hasJarDownloadFailed = true;
               }
            }
         }
      });
      thread.start();
      return thread;
   }

   public boolean hasJarDownloadFailed() {
      return this.hasJarDownloadFailed;
   }

   public String getAuthorizeUrl(String text) {
      return "https://update.techscode.com/authorize/" + text;
   }

   public void destroy() {
      try {
         this.close();
      } catch (IOException exception) {
      }
   }
}
