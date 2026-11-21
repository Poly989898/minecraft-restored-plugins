package litebans;

import java.net.ProtocolException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DiscordWebhook {
   public static final DiscordWebhookNotifier KOTLIN_DATA = new DiscordWebhookNotifier(null);
   private final String url;
   private String content;
   private String username;
   private String avatarUrl;
   private boolean tts;
   private List embeds;
   private final JSONObject jSONObject;
   private static final Character[] value2;

   public DiscordWebhook(@NotNull String text) {
      this.url = text;
      this.embeds = CollectionsKt.emptyList();
      this.jSONObject = new JSONObject();
   }

   public final String getText6() {
      return this.url;
   }

   public final void setList3(@NotNull List list) {
      this.embeds = list;
   }

   public final JSONObject getJSONObject() {
      return this.jSONObject;
   }

   public final String computeText(@Nullable String text, boolean flag) {
      if (text == null) {
         return null;
      }

      String text2 = text;
      if (!flag) {
         Character[] characters = value2;
         int value = 0;

         for (int i = characters.length; value < i; value++) {
            char c = characters[value];
            if (StringsKt.containsWithDefaults(text2, c, false, 2, null)) {
               text2 = StringsKt.computeText(text2, String.valueOf(c), "" + '\\' + c, false, 4, null);
            }
         }
      }

      return text2;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void perform() {
      try {
         this.jSONObject.put("content", this.computeText(this.content, false));
         this.jSONObject.put("username", this.username);
         this.jSONObject.put("avatar_url", this.avatarUrl);
         this.jSONObject.put("tts", this.tts);
         Map jSONObject2 = this.jSONObject;
         String text = "embeds";
         Iterable iterable = this.embeds;
         var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

         for (Object obj : iterable) {
            DiscordEmbed kotlinHelper23 = (DiscordEmbed)obj;
            arrayList.add(this.computeJsonObject(kotlinHelper23));
         }

         List list = arrayList;

         Object obj2;
         try {
            obj2 = (HttpsURLConnection)new JSONParser().computeObj(JSONArray.computeText(list));
         } catch (Throwable e2) {
            System.err.println(JSONArray.computeText(list));
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         }

         Object obj3 = obj2;
         jSONObject2.put(text, obj3);
         JSONObject jSONObject3 = this.jSONObject;
         obj2 = URI.create(this.url).toURL().openConnection();
         Intrinsics.checkNotNullExpressionValue(obj2, "");
         HttpsURLConnection httpsURLConnection = (javax.net.ssl.HttpsURLConnection) obj2;
         httpsURLConnection.addRequestProperty("Content-Type", "application/json");
         httpsURLConnection.addRequestProperty("User-Agent", "Java (LiteBans " + BuildInfoUtil.obtainText2() + ')');
         httpsURLConnection.setConnectTimeout(80000);
         httpsURLConnection.setReadTimeout(80000);
         httpsURLConnection.setDoOutput(true);
         httpsURLConnection.setRequestMethod("POST");
         HttpURLConnection httpURLConnection = httpsURLConnection;
         boolean flag = false /* VF: Semaphore variable */;

         try {
            flag = true;
            var outputStream = httpURLConnection.getOutputStream();
            Throwable error = null;

            try {
               Intrinsics.checkNotNull(outputStream);
               outputStream.write(jSONObject3.toString().getBytes(KotlinCharsets.CHARSET));
            } catch (Throwable e3) {
               error = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               CloseableKt.handleCloseable(outputStream, error);
            }

            flag = false;
         } finally {
            if (flag) {
               try {
                  httpURLConnection.getInputStream().close();
                  httpURLConnection.disconnect();
               } catch (Throwable e4) {
                  throw new RuntimeException(e4);
               }
            }
         }

         httpURLConnection.getInputStream().close();
         httpURLConnection.disconnect();
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final JSONObject computeJsonObject(DiscordEmbed kotlinHelper) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("title", this.computeText(kotlinHelper.getTitle(), true));
      jsonObject.put("description", this.computeText(kotlinHelper.getDescription(), true));
      Map map = jsonObject;
      String text = "url";
      String text2 = kotlinHelper.getUrl();
      if (text2 == null) {
         text2 = "";
      }

      String text3 = text2;
      map.put(text, text3);
      EmbedFooter namePair = kotlinHelper.getFooter();
      EmbedImage kotlinHelper2 = kotlinHelper.getImage();
      String text4 = kotlinHelper.getThumbnailUrl();
      EmbedAuthor kotlinHelper17 = kotlinHelper.getAuthor();
      List list = kotlinHelper.getFields();
      int value = kotlinHelper.getColor();
      if (namePair != null) {
         JSONObject jsonObject2 = new JSONObject();
         jsonObject2.put("text", namePair.getText());
         jsonObject2.put("icon_url", namePair.getIconUrl());
         jsonObject.put("footer", jsonObject2);
      }

      if (kotlinHelper2 != null) {
         JSONObject jsonObject3 = new JSONObject();
         jsonObject3.put("url", kotlinHelper2.getUrl());
         jsonObject.put("image", jsonObject3);
      }

      if (kotlinHelper17 != null) {
         JSONObject jsonObject4 = new JSONObject();
         jsonObject4.put("name", kotlinHelper17.getName());
         jsonObject4.put("url", kotlinHelper17.getUrl());
         jsonObject4.put("icon_url", kotlinHelper17.getIconUrl());
         jsonObject.put("author", jsonObject4);
      }

      if (text4 != null) {
         JSONObject jsonObject5 = new JSONObject();
         jsonObject5.put("url", text4);
         jsonObject.put("thumbnail", jsonObject5);
      }

      Integer boxed = value;
      jsonObject.put("color", boxed);
      Map map2 = jsonObject;
      String text5 = "fields";
      Iterable iterable = list;
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         EmbedField kotlinHelper5 = (EmbedField)obj;
         JSONObject jsonObject6 = new JSONObject();
         jsonObject6.put("name", kotlinHelper5.getName());
         jsonObject6.put("value", kotlinHelper5.getValue());
         jsonObject6.put("inline", kotlinHelper5.isInline());
         arrayList.add(jsonObject6);
      }

      List list2 = arrayList;

      try {
         text2 = (String)new JSONParser().computeObj(JSONArray.computeText(list2));
      } catch (Throwable e) {
         System.err.println(JSONArray.computeText(list2));
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }

      Object obj2 = text2;
      map2.put(text5, obj2);
      return jsonObject;
   }

   static {
      Character[] characters = new Character[]{'_', '*', '`', '~', '#', '-'};
      value2 = characters;
   }
}
