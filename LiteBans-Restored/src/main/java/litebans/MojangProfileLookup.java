package litebans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import org.jetbrains.annotations.NotNull;

public final class MojangProfileLookup {
   private MojangProfileLookup() {
   }

   public final String computeText(@NotNull String text) {
      if (!this.checkText(text) && TargetParser.isUuid(text) && !Intrinsics.areEqual(text, KotlinUtil11.obtainText())) {
         String text2 = this.computeText2(text);

         try {
            URLConnection connection = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + text2).openConnection();
            Intrinsics.checkNotNullExpressionValue(connection, "");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)connection;
            short value = 10000;
            httpsURLConnection.setConnectTimeout(value);
            httpsURLConnection.setReadTimeout(value);
            InputStream inputStream = httpsURLConnection.getInputStream();
            Charset charset = KotlinCharsets.CHARSET;
            Reader inputStreamReader = new InputStreamReader(inputStream, charset);
            short value2 = 512;
            var bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader)inputStreamReader : new BufferedReader(inputStreamReader, value2);
            Throwable error = null;

            try {
               Object obj = UsernameValidator.obtainJsonParser().computeObj(bufferedReader);
               Intrinsics.checkNotNullExpressionValue(obj, "");
               JSONObject jsonObject = (JSONObject)obj;
               Object obj2 = jsonObject.get("name");
               String text3 = obj2 instanceof String ? (String)obj2 : null;
               if (text3 != null) {
                  UsernameValidator.KOTLIN_DATA.handleJsonObject(jsonObject);
                  return text3;
               }

            } catch (Throwable e) {
               error = e;
               throw SneakyThrow.<RuntimeException>rethrow(e);
            } finally {
               CloseableKt.handleCloseable(bufferedReader, error);
            }
         } catch (Exception e2) {
            ConfigModule module = (ConfigModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(ConfigModule.class);
            module.debug(1, e2);
         }

         return null;
      } else {
         return null;
      }
   }

   public final Profile computeProfile(@NotNull String text) {
      Profile profile = this.computeProfile2(text);
      return !profile.obtainFlag() ? profile : null;
   }

   public final Profile computeProfile2(@NotNull String text) {
      ConfigModule module = (ConfigModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(ConfigModule.class);
      CharSequence lowerCase = text.toLowerCase(Locale.ROOT);
      if (!UsernameValidator.obtainRegex().matches(lowerCase)) {
         module.debug(1, "Username \"" + text + "\" invalid");
         return KotlinUtil11.obtainProfile();
      }

      try {
         URLConnection connection = new URL("https://api.mojang.com/users/profiles/minecraft/" + text).openConnection();
         Intrinsics.checkNotNullExpressionValue(connection, "");
         HttpsURLConnection httpsURLConnection = (HttpsURLConnection)connection;
         short value = 10000;
         httpsURLConnection.setConnectTimeout(value);
         httpsURLConnection.setReadTimeout(value);
         InputStream inputStream = httpsURLConnection.getInputStream();
         Charset charset = KotlinCharsets.CHARSET;
         Reader inputStreamReader = new InputStreamReader(inputStream, charset);
         short value2 = 512;
         var bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader)inputStreamReader : new BufferedReader(inputStreamReader, value2);
         Throwable error = null;

         try {
            Object obj = UsernameValidator.obtainJsonParser().computeObj(bufferedReader);
            Intrinsics.checkNotNullExpressionValue(obj, "");
            JSONObject jsonObject = (JSONObject)obj;
            Object obj2 = jsonObject.get("name");
            String text2 = obj2 instanceof String ? (String)obj2 : null;
            Object obj3 = jsonObject.get("id");
            String text3 = obj3 instanceof String ? (String)obj3 : null;
            if (text2 != null && text3 != null) {
               UsernameValidator.KOTLIN_DATA.handleJsonObject(jsonObject);
               return new Profile(text2, UUID.fromString(UsernameValidator.KOTLIN_DATA.computeText3(text3)));
            }

         } catch (Throwable e) {
            error = e;
            throw SneakyThrow.<RuntimeException>rethrow(e);
         } finally {
            CloseableKt.handleCloseable(bufferedReader, error);
         }
      } catch (Exception e2) {
         module.debug(1, e2);
      }

      return KotlinUtil11.obtainProfile();
   }

   public final boolean checkText(@NotNull String text) {
      String text2 = this.computeText3(text);
      return text2.length() == 36 && text2.charAt(14) == '3';
   }

   public final String computeText(@NotNull UUID uuid) {
      return this.computeText2(uuid.toString());
   }

   public final String computeText2(@NotNull String text) {
      return text.length() != 36 ? text : CacheModule.CACHE_MODULE_COMPANION.computeText(text, "-", "");
   }

   public final String computeText3(@NotNull String text) {
      if (text.length() != 32) {
         return text;
      }

      StringBuilder stringBuilder = new StringBuilder(36);
      int value = 0;
      int value2 = 0;
      int value3 = 0;
      byte b = 8;
      int value4 = 0;

      for (int i = text.length(); value4 < i; value4++) {
         char charAt2 = text.charAt(value4);
         value++;
         stringBuilder.append(charAt2);
         if (++value2 >= b) {
            value2 = 0;
            if (value < 32) {
               stringBuilder.append('-');
            }

            if (++value3 == 1) {
               b = 4;
            } else if (value3 >= 4) {
               b = 12;
            }
         }
      }

      return stringBuilder.toString();
   }

   private final void handleJsonObject(JSONObject jsonObject) {
      Object obj = jsonObject.get("cause");
      String text = obj instanceof String ? (String)obj : null;
      Object obj2 = jsonObject.get("errorMessage");
      String text2 = obj2 instanceof String ? (String)obj2 : null;
      if (text != null ? text.length() != 0 : false) {
         throw new RuntimeException(text2);
      }
   }

   public MojangProfileLookup(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
