package litebans;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public final class VanillaImport extends ImportSource {
   private final File file3 = new File("banned-players.json");
   private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

   public VanillaImport(@NotNull Platform platform) {
      super(platform, "vanilla", null, 0, 0L, 28, null);
   }

   @Override
   public boolean obtainFlag5() {
      return false;
   }

   @Override
   public boolean isSource(@NotNull String text) {
      return super.isSource(text)
         || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"banned-players", false, 2, null)
         || StringsKt.containsWithDefaults((CharSequence)text, (CharSequence)"essentials", false, 2, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      try {
         File file = this.file3;
         Charset charset = KotlinCharsets.CHARSET;
         short value = 8192;
         Reader inputStreamReader = new InputStreamReader(new FileInputStream(file), charset);
         BufferedReader bufferedReader = inputStreamReader instanceof BufferedReader ? (BufferedReader)inputStreamReader : new BufferedReader(inputStreamReader, value);
         Throwable error = null;

         try {
            String joinToString = CollectionsKt.joinToStringWithDefaults(KotlinUtil17.computeList(bufferedReader), null, null, null, 0, null, null, 63, null);
            Object obj = JSONValue.computeObj(joinToString);
            Intrinsics.checkNotNullExpressionValue(obj, "");
            JSONArray jsonArray = (JSONArray)obj;
            this.handleJsonArray(jsonArray, databaseSession);
         } catch (Throwable e2) {
            error = e2;
            throw SneakyThrow.<RuntimeException>rethrow(e2);
         } finally {
            CloseableKt.handleCloseable(bufferedReader, error);
         }
      } catch (FileNotFoundException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleJsonArray(JSONArray jsonArray, DatabaseSession databaseSession) {
      JSONObject jsonObject = null;
      String text = "";

      for (Object obj : jsonArray) {
         try {
            Intrinsics.checkNotNullExpressionValue(obj, "");
            jsonObject = (JSONObject)obj;
            String string = (String)jsonObject.get("name");
            String string2 = (String)jsonObject.get("uuid");
            Date parse2 = this.dateFormat.parse((String)jsonObject.get("created"));
            long time = parse2.getTime();
            String string3 = (String)jsonObject.get("source");
            if (string3 != null) {
               String text2 = string3;
               text = LiteColor.stripColor(text2);
               String string4 = (String)jsonObject.get("expires");
               long value = Intrinsics.areEqual(string4, "forever") ? 0L : this.dateFormat.parse(string4).getTime();
               string3 = (String)jsonObject.get("reason");
               if (string3 == null) {
                  string3 = "";
               }

               String text3 = string3;
               Punishment punishment = new Punishment(PunishmentType.BAN, string2, null, text3, this.computeText(text), text, "*", null, time, value, (byte)0, false, false, false, 0L, 31744, null);
               if (PunishmentQueries.findPunishmentWithDefaults(databaseSession, string2, null, (PunishmentType)null, false, false, 30, null) == null) {
                  databaseSession.insertPunishment(punishment);
                  this.obtainAtomicLong().incrementAndGet();
               } else {
                  this.getLogger().warning("Ignoring duplicate ban for " + punishment.getUuid());
               }

               if (string != null && string2 != null) {
                  new QueueCommandTask(this.getPlatform(), string, string2, null).run();
               }
            }
         } catch (Exception e) {
            this.handleText(this.getSource(), text, jsonObject, e);
         }
      }
   }

   private final void handleText(String text, String text2, JSONObject jsonObject, Exception exception) {
      this.getPlatform().getLogger().warning("Failed to import ban for " + text + " (created by " + text2 + "). JSON data:");
      this.getPlatform().getLogger().warning(jsonObject != null ? jsonObject.obtainText() : null);
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).logConnectionError(exception);
   }
}
