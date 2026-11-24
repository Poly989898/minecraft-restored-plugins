package me.TechsCode.UltraPermissions.base.update.responses;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DownloadErrorDeserializer implements JsonDeserializer<DownloadErrorResponse> {
   public DownloadErrorDeserializer() {
   }

   public DownloadErrorResponse deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
      JsonObject asJsonObject = jsonElement.getAsJsonObject();
      return (DownloadErrorResponse)jsonDeserializationContext.deserialize(asJsonObject, DownloadErrorResponse.class);
   }
}
