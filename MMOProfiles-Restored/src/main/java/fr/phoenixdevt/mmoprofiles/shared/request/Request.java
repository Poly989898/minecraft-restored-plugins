package fr.phoenixdevt.mmoprofiles.shared.request;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class Request {
   private static final Gson GSON = new GsonBuilder().create();
   public static final String CHANNEL_NAME = "mmoprofiles:main";

   @NotNull
   public abstract RequestType getType();

   protected abstract void fillJsonObject(@NotNull JsonObject var1);

   @NotNull
   public JsonObject toJson() {
      JsonObject obj = new JsonObject();
      obj.addProperty("RequestType", this.getType().name());
      this.fillJsonObject(obj);
      return obj;
   }

   @NotNull
   public static Request fromJson(@NotNull JsonObject requestObject) {
      Validate.isTrue(requestObject.has("RequestType"), "JsonObject is not a request object");
      RequestType type = RequestType.valueOf(requestObject.get("RequestType").getAsString());
      return type.readRequest(requestObject);
   }

   public static Request fromByteArray(@NotNull byte[] data) {
      return fromJson(GSON.fromJson(ByteStreams.newDataInput(data).readUTF(), JsonObject.class));
   }
}
