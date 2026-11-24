package me.TechsCode.UltraPermissions.base.storage.implementations;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.debug.Debugger;
import me.TechsCode.UltraPermissions.base.debug.TechException;
import me.TechsCode.UltraPermissions.base.storage.ReadCallback;
import me.TechsCode.UltraPermissions.base.storage.StorageImplementation;
import me.TechsCode.UltraPermissions.base.storage.WriteCallback;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class MySQL extends StorageImplementation {
   private static final Pattern REGEX_PATTERN = Pattern.compile("^\\p{XDigit}+$");
   private String tableName;

   public MySQL(TechPlugin plugin, String text) {
      super(plugin, text, true);
      this.tableName = plugin.getName() + "_" + text;

      try {
         Connection connection = plugin.getMySQLManager().getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(
            "CREATE TABLE IF NOT EXISTS `" + this.tableName + "` ( `key` VARCHAR(64) NOT NULL UNIQUE, `value` MEDIUMBLOB);"
         );
         preparedStatement.execute();
         preparedStatement.close();

         try {
            PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE `" + this.tableName + "` ADD UNIQUE (`key`);");
            alterStatement.execute();
            alterStatement.close();
         } catch (SQLException ex) {
            String lowerCase = ex.getMessage().toLowerCase();
            if (!lowerCase.contains("duplicate") && !lowerCase.contains("already exists")) {
               ex.printStackTrace();
            }
         }

         connection.close();
      } catch (SQLException tableException) {
         tableException.printStackTrace();
      }
   }

   @Override
   public void destroy(String text, WriteCallback writeCallback) {
      Debugger.debug("MySQL", "Destroying " + text + " in " + this.tableName);
      this.update("DELETE FROM " + this.tableName + " WHERE `key`='" + text + "';", writeCallback);
   }

   @Override
   public void update(String text, JsonObject jsonObject, WriteCallback writeCallback) {
      Debugger.debug("MySQL", "Updating " + text + " in " + this.tableName);
      this.update("UPDATE " + this.tableName + " SET `value` = '" + this.jsonToString(jsonObject) + "' WHERE `key`='" + text + "';", writeCallback);
   }

   @Override
   public void create(String text, JsonObject jsonObject, WriteCallback writeCallback) {
      Debugger.debug("MySQL", "Creating " + text + " in " + this.tableName);
      this.destroy(text, new WriteCallback.EmptyWriteCallback());
      this.update("INSERT INTO " + this.tableName + " (`key`, `value`) VALUES ('" + text + "', '" + this.jsonToString(jsonObject) + "');", writeCallback);
   }

   private String jsonToString(JsonObject jsonObject) {
      return Base64.encodeBase64String(jsonObject.toString().replace("'", "\\'").getBytes(StandardCharsets.UTF_8));
   }

   @Override
   public void read(String text, ReadCallback readCallback) {
      Debugger.debug("MySQL", "Reading " + text + " from " + this.tableName);
      JsonParser jsonParser = new JsonParser();

      try {
         while (!this.plugin.getMySQLManager().connectionAvailable()) {
            new Thread(() -> {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException ex) {
                  throw new RuntimeException(ex);
               }

               if (!this.plugin.getMySQLManager().connectionAvailable()) {
                  readCallback.onFailure(new TechException(new SQLException(), "Could not process MySQL Update"));
               }
            }).start();
         }

         HashMap map = new HashMap();
         Connection connection = this.plugin.getMySQLManager().getConnection();
         String sql = "SELECT * FROM " + this.tableName + " WHERE " + (text.equals("*") ? "1" : "`key`='" + text + "';");
         PreparedStatement preparedStatement = connection.prepareStatement(sql);
         ResultSet resultSet = preparedStatement.executeQuery();

         while (resultSet.next()) {
            String string = resultSet.getString("key");
            Blob blob = resultSet.getBlob("value");
            String content = new String(blob.getBytes(1L, (int)blob.length()));
            if (Base64.isBase64(content)) {
               content = new String(Base64.decodeBase64(content), StandardCharsets.UTF_8);
            }

            if (REGEX_PATTERN.matcher(content).matches()) {
               try {
                  content = new String(Hex.decodeHex(content.toCharArray()), StandardCharsets.UTF_8);
               } catch (DecoderException hexException) {
                  hexException.printStackTrace();
               }
            }

            try {
               JsonObject jsonObject = (JsonObject)jsonParser.parse(content);
               map.put(string, jsonObject);
            } catch (JsonSyntaxException jsonException) {
               jsonException.printStackTrace();
            }
         }

         resultSet.close();
         connection.close();
         readCallback.onSuccess(map);
      } catch (SQLException queryException) {
         readCallback.onFailure(queryException);
      }

      Debugger.debug("MySQL", "Finished reading " + text + " from " + this.tableName);
   }

   private void update(String text, WriteCallback writeCallback) {
      while (!this.plugin.getMySQLManager().connectionAvailable()) {
         Debugger.debug("MySQL", "Waiting for MySQL connection to be available");
      }

      try (Connection connection = this.plugin.getMySQLManager().getConnection()) {
         Debugger.debug("MySQLDetailed", "Connection: " + connection);
         if (connection == null) {
            throw new TechException(new SQLException(), "No available MySQL connection");
         }

         Debugger.debug("MySQL", "Executing SQL: " + text);
         connection.prepareStatement(text).execute();
         connection.close();
         Debugger.debug("MySQL", "Connection found and SQL executed");
         Debugger.debug("MySQLDetailed", "SQL: " + text);
         writeCallback.onSuccess();
      } catch (SQLException ex) {
         writeCallback.onFailure(ex);
         throw new TechException(ex, "Could not process MySQL Update");
      }
   }
}
