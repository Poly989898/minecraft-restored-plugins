package me.TechsCode.UltraPermissions.base.messaging;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Message {
   private final String key;
   private final JsonObject data;

   public Message(String text, JsonObject jsonObject) {
      this.key = text;
      this.data = jsonObject;
   }

   public String getKey() {
      return this.key;
   }

   public JsonObject getData() {
      return this.data;
   }

   public String encode() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("key", this.key);
      jsonObject.add("data", this.data);
      return Base64.encodeBase64String(jsonObject.toString().replace("'", "\\'").getBytes(StandardCharsets.UTF_8));
   }

   public static Message decode(String text) {
      String decoded = new String(Base64.decodeBase64(text), StandardCharsets.UTF_8);
      JsonObject asJsonObject = new JsonParser().parse(decoded).getAsJsonObject();
      String asString = asJsonObject.get("key").getAsString();
      JsonObject dataObject = asJsonObject.get("data").getAsJsonObject();
      return new Message(asString, dataObject);
   }
}
