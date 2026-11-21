package it.ytnoos.lpx.utils;

import it.ytnoos.lpx.EmbedImage;
import it.ytnoos.lpx.EmbedThumbnail;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;

public class DiscordWebhook {
   private final String url;
   private final List<DiscordWebhook.EmbedObject> embeds = new ArrayList<>();
   private String content;
   private String username;
   private String avatarUrl;
   private boolean tts;
   private String h;

   public DiscordWebhook(String string) {
      this.url = string;
   }

   public void setContent(String string) {
      this.content = string;
   }

   public void setUsername(String string) {
      this.username = string;
   }

   public void setAvatarUrl(String string) {
      this.avatarUrl = string;
   }

   public void setTts(boolean flag) {
      this.tts = flag;
   }

   public void addEmbed(Object object) {
      this.embeds.add((DiscordWebhook.EmbedObject)object);
   }

   public void execute() throws IOException {
      if (this.content == null && this.embeds.isEmpty()) {
         throw new IllegalArgumentException("Set content or add at least one EmbedObject");
      }

      DiscordWebhook.JSONObject jsonObject = new DiscordWebhook.JSONObject();
      jsonObject.put("content", this.content);
      jsonObject.put("username", this.username);
      jsonObject.put("avatar_url", this.avatarUrl);
      jsonObject.put("tts", this.tts);
      if (!this.embeds.isEmpty()) {
         ArrayList arrayList = new ArrayList();

         for (DiscordWebhook.EmbedObject embedObject : this.embeds) {
            DiscordWebhook.JSONObject jsonObject2 = new DiscordWebhook.JSONObject();
            jsonObject2.put("title", embedObject.getTitle());
            jsonObject2.put("description", embedObject.getDescription());
            jsonObject2.put("url", embedObject.getUrl());
            jsonObject2.put("timestamp", embedObject.getTimestamp());
            if (embedObject.getColor() != null) {
               Color color = embedObject.getColor();
               int value = color.getRed();
               value = (value << 8) + color.getGreen();
               value = (value << 8) + color.getBlue();
               jsonObject2.put("color", value);
            }

            DiscordWebhook.EmbedObject.Footer footer = embedObject.getFooter();
            EmbedImage qn = embedObject.getImage();
            EmbedThumbnail qq = embedObject.getThumbnail();
            DiscordWebhook.EmbedObject.Author author = embedObject.getAuthor();
            List list = embedObject.getFields();
            if (footer != null) {
               DiscordWebhook.JSONObject jsonObject3 = new DiscordWebhook.JSONObject();
               jsonObject3.put("text", footer.getText());
               jsonObject3.put("icon_url", footer.getIconUrl());
               jsonObject2.put("footer", jsonObject3);
            }

            if (qn != null) {
               DiscordWebhook.JSONObject jsonObject4 = new DiscordWebhook.JSONObject();
               jsonObject4.put("url", qn.getUrl());
               jsonObject2.put("image", jsonObject4);
            }

            if (qq != null) {
               DiscordWebhook.JSONObject jsonObject5 = new DiscordWebhook.JSONObject();
               jsonObject5.put("url", qq.getUrl());
               jsonObject2.put("thumbnail", jsonObject5);
            }

            if (author != null) {
               DiscordWebhook.JSONObject jsonObject6 = new DiscordWebhook.JSONObject();
               jsonObject6.put("name", author.getName());
               jsonObject6.put("url", author.getUrl());
               jsonObject6.put("icon_url", author.getIconUrl());
               jsonObject2.put("author", jsonObject6);
            }

            ArrayList arrayList2 = new ArrayList();

            for (Object var18_o : list) { DiscordWebhook.EmbedObject.Field field = (DiscordWebhook.EmbedObject.Field) var18_o;
               DiscordWebhook.JSONObject jsonObject7 = new DiscordWebhook.JSONObject();
               jsonObject7.put("name", field.getName());
               jsonObject7.put("value", field.getValue());
               jsonObject7.put("inline", field.isInline());
               arrayList2.add(jsonObject7);
            }

            jsonObject2.put("fields", arrayList2.toArray());
            arrayList.add(jsonObject2);
         }

         jsonObject.put("embeds", arrayList.toArray());
      }

      URL url = new URL(this.url);
      HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
      httpsURLConnection.addRequestProperty("Content-Type", "application/json");
      httpsURLConnection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
      httpsURLConnection.setDoOutput(true);
      httpsURLConnection.setRequestMethod("POST");
      OutputStream outputStream = httpsURLConnection.getOutputStream();
      this.h = jsonObject.toString();
      outputStream.write(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
      outputStream.flush();
      outputStream.close();
      httpsURLConnection.getInputStream().close();
      httpsURLConnection.disconnect();
   }

   public String getJson() {
      return this.h;
   }

   public static class EmbedObject {
      private final List<DiscordWebhook.EmbedObject.Field> fields = new ArrayList<>();
      private String title;
      private String description;
      private String url;
      private Color color;
      private DiscordWebhook.EmbedObject.Footer footer;
      private String timestamp;
      private EmbedThumbnail thumbnail;
      private EmbedImage image;
      private DiscordWebhook.EmbedObject.Author author;

      public EmbedObject() {
      }

      public String getTitle() {
         return this.title;
      }

      public DiscordWebhook.EmbedObject setTitle(String string) {
         this.title = string;
         return this;
      }

      public String getDescription() {
         return this.description;
      }

      public DiscordWebhook.EmbedObject setDescription(String string) {
         this.description = string.replace("\n", "\\n").replace("\\_", "_");
         return this;
      }

      public String getUrl() {
         return this.url;
      }

      public DiscordWebhook.EmbedObject setUrl(String string) {
         this.url = string;
         return this;
      }

      public Color getColor() {
         return this.color;
      }

      public DiscordWebhook.EmbedObject setColor(Color color2) {
         this.color = color2;
         return this;
      }

      public DiscordWebhook.EmbedObject.Footer getFooter() {
         return this.footer;
      }

      public String getTimestamp() {
         return this.timestamp;
      }

      public DiscordWebhook.EmbedObject setTimestamp(TemporalAccessor temporalAccessor) {
         this.timestamp = temporalAccessor.toString();
         return this;
      }

      public EmbedThumbnail getThumbnail() {
         return this.thumbnail;
      }

      public DiscordWebhook.EmbedObject setThumbnail(String string) {
         this.thumbnail = new EmbedThumbnail(string);
         return this;
      }

      public EmbedImage getImage() {
         return this.image;
      }

      public DiscordWebhook.EmbedObject setImage(String string) {
         this.image = new EmbedImage(string);
         return this;
      }

      public DiscordWebhook.EmbedObject.Author getAuthor() {
         return this.author;
      }

      public List getFields() {
         return this.fields;
      }

      public DiscordWebhook.EmbedObject setFooter(String string, String string2) {
         this.footer = new DiscordWebhook.EmbedObject.Footer(string, string2);
         return this;
      }

      public DiscordWebhook.EmbedObject setAuthor(String string, String string2, String string3) {
         this.author = new DiscordWebhook.EmbedObject.Author(string, string2, string3);
         return this;
      }

      public DiscordWebhook.EmbedObject addField(String string, String string2, boolean flag) {
         this.fields.add(new DiscordWebhook.EmbedObject.Field(string, string2, flag));
         return this;
      }

      static class Author {
         private final String name;
         private final String url;
         private final String iconUrl;

         private Author(String string, String string2, String string3) {
            this.name = string;
            this.url = string2;
            this.iconUrl = string3;
         }

         private String getName() {
            return this.name;
         }

         private String getUrl() {
            return this.url;
         }

         private String getIconUrl() {
            return this.iconUrl;
         }
      }

      static class Field {
         private final String name;
         private final String value;
         private final boolean inline;

         private Field(String string, String string2, boolean flag) {
            this.name = string;
            this.value = string2;
            this.inline = flag;
         }

         private String getName() {
            return this.name;
         }

         private String getValue() {
            return this.value;
         }

         private boolean isInline() {
            return this.inline;
         }
      }

      static class Footer {
         private final String text;
         private final String iconUrl;

         private Footer(String string, String string2) {
            this.text = string;
            this.iconUrl = string2;
         }

         private String getText() {
            return this.text;
         }

         private String getIconUrl() {
            return this.iconUrl;
         }
      }
   }

   static class JSONObject {
      private final HashMap<String, Object> map = new HashMap<>();

      private JSONObject() {
      }

      void put(Object object, Object object2) {
         if (object2 != null) {
            this.map.put((String)object, object2);
         }
      }

      @Override
      public String toString() {
         StringBuilder stringBuilder = new StringBuilder();
         Set set = this.map.entrySet();
         stringBuilder.append("{");
         int value = 0;

         for (Object var8_o : set) { Entry entry = (Entry) var8_o;
            Object object = entry.getValue();
            stringBuilder.append(this.quote((String)entry.getKey())).append(":");
            if (object instanceof String) {
               stringBuilder.append(this.quote(String.valueOf(object)));
            } else if (object instanceof Integer) {
               stringBuilder.append(Integer.valueOf(String.valueOf(object)));
            } else if (object instanceof Boolean) {
               stringBuilder.append(object);
            } else if (object instanceof DiscordWebhook.JSONObject) {
               stringBuilder.append(object);
            } else if (object.getClass().isArray()) {
               stringBuilder.append("[");
               int value2 = Array.getLength(object);

               for (int value3 = 0; value3 < value2; value3++) {
                  stringBuilder.append(Array.get(object, value3).toString()).append(value3 != value2 - 1 ? "," : "");
               }

               stringBuilder.append("]");
            }

            value++;
            stringBuilder.append(value == set.size() ? "}" : ",");
         }

         return stringBuilder.toString();
      }

      private String quote(String string) {
         return "\"" + string + "\"";
      }

   }
}
