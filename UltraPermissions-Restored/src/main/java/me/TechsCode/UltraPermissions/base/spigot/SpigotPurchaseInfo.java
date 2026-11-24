package me.TechsCode.UltraPermissions.base.spigot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpigotPurchaseInfo {
   private static String USER_ID = "29620";
   private static String USERNAME = null;

   public SpigotPurchaseInfo() {
   }

   public static Optional<String> getUserId() {
      return USER_ID.contains("__USER__") ? Optional.empty() : Optional.of(USER_ID);
   }

   public static Optional<String> getUsername() {
      if (USERNAME != null) {
         return Optional.of(USERNAME);
      } else {
         Optional v0 = getUserId();
         if (v0.isPresent()) {
            USERNAME = fetchUsername((String)v0.get());
            return Optional.of(USERNAME);
         } else {
            return Optional.empty();
         }
      }
   }

   private static String fetchUsername(String userId) {
      try {
         URLConnection connection = new URL("https://api.spigotmc.org/simple/0.1/index.php?action=getAuthor&id=" + userId).openConnection();
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         StringBuilder body = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            body.append(line);
         }

         reader.close();
         JsonParser parser = new JsonParser();
         JsonObject json = (JsonObject)parser.parse(body.toString());
         return !json.has("username") ? "Unknown" : json.get("username").getAsString();
      } catch (Exception exception) {
         return "Unknown";
      }
   }
}
