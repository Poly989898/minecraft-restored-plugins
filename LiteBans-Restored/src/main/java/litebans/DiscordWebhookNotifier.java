package litebans;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DiscordWebhookNotifier {
   private DiscordWebhookNotifier() {
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull PunishmentRequest punishmentRequest, @NotNull String text) {
      if (text.length() != 0) {
         PunishmentRequest punishmentRequest2 = punishmentRequest;
         DiscordWebhook discordWebhook = new DiscordWebhook(text);
         Punishment punishment = punishmentRequest2.getPunishment();
         Intrinsics.checkNotNull(punishment);
         String text2 = Message.computeText("WEB_" + punishmentRequest2.getType() + "_TITLE");
         if (text2 == null) {
            text2 = "";
         }

         String text3 = text2;
         String text4 = Message.computeText("WEB_" + punishmentRequest2.getType() + "_CONTENT");
         int value = DiscordWebhook.KOTLIN_DATA.computeIntValue(databaseSession, Message.computeText("WEB_" + punishmentRequest2.getType() + "_COLOR"));
         String text5 = Message.computeText("WEB_" + punishmentRequest2.getType() + "_THUMB");
         String text6;
         if ((text5 != null ? !StringsKt.isBlank(text5) : true) && (text5 != null ? StringsKt.checkText(text5, "https://", false, 2, null) : true)) {
            label51: {
               if (text5 != null) {
                  text2 = (String) databaseSession.replacePlaceholders(text5, punishmentRequest2.getExecutorName(), punishmentRequest2.getExecutorUuid(), false);
                  if (text2 != null) {
                     text2 = (String) databaseSession.replacePlaceholders(text2, punishment, punishmentRequest2.getTargetRecord(), false);
                     if (text2 != null) {
                        text2 = text2.toString();
                        break label51;
                     }
                  }
               }

               text2 = null;
            }

            text6 = text2;
         } else {
            text6 = null;
         }

         text2 = Message.computeText("WEB_" + punishmentRequest2.getType() + "_FIELDS");
         if (text2 == null) {
            text2 = "";
         }

         String text7 = text2;
         Pair[] pairs = DiscordWebhook.KOTLIN_DATA.computePairArray(databaseSession, text7, punishmentRequest2);
         String string = databaseSession.replacePlaceholders(databaseSession.replacePlaceholders(text3, punishmentRequest2.getExecutorName(), punishmentRequest2.getExecutorUuid(), false), punishment, punishmentRequest2.getTargetRecord(), false).toString();
         Intrinsics.checkNotNull(text4);
         String string2 = databaseSession.replacePlaceholders(databaseSession.replacePlaceholders(text4, punishmentRequest2.getExecutorName(), punishmentRequest2.getExecutorUuid(), false), punishment, punishmentRequest2.getTargetRecord(), false).toString();
         DiscordWebhook.KOTLIN_DATA.sendWebhook(databaseSession, discordWebhook, string, string2, value, text6, pairs, !StringsKt.containsWithDefaults((CharSequence)text7, (CharSequence)"\n\n", false, 2, null));
      }
   }

   private final Pair[] computePairArray(DatabaseSession databaseSession, String text, PunishmentRequest punishmentRequest) {
      if (StringsKt.isBlank(text)) {
         return null;
      }

      DiscordWebhookNotifier kotlinData = DiscordWebhook.KOTLIN_DATA;
      CharSequence replacePlaceholders2 = databaseSession.replacePlaceholders(text, punishmentRequest.getPunishment(), punishmentRequest.getTargetRecord(), false);
      ArrayList arrayList = new ArrayList();
      String text2 = kotlinData.computeText(replacePlaceholders2);
      Intrinsics.checkNotNull(text2);
      List list;
      if (StringsKt.containsWithDefaults((CharSequence)text2, (CharSequence)"\n\n", false, 2, null)) {
         CharSequence message = text2;
         String[] texts = new String[]{"\n\n"};
         list = StringsKt.splitWithDefaults(message, texts, false, 0, 6, null);
      } else {
         CharSequence message2 = text2;
         char[] chars = new char[]{'\n'};
         list = StringsKt.splitWithDefaults(message2, chars, false, 0, 6, null);
      }

      Iterable iterable = list;

      for (Object obj : iterable) {
         String string = (String)obj;
         if (string.length() != 0) {
            CharSequence message3 = string;
            char[] chars2 = new char[]{':'};
            List split = StringsKt.splitWithDefaults(message3, chars2, false, 0, 6, null);
            if (split.size() <= 1) {
               if (arrayList.isEmpty()) {
                  arrayList.add(TuplesKt.computePair(string, string));
               } else {
                  Pair last2 = (Pair)CollectionsKt.last(arrayList);
                  arrayList.set(CollectionsKt.getLastIndex(arrayList), TuplesKt.computePair(last2.getFirst(), (String)last2.getSecond() + '\n' + string));
               }
            } else {
               arrayList.add(TuplesKt.computePair(split.get(0), split.get(1)));
            }
         }
      }

      Pair[] pairs;
      if (!arrayList.isEmpty()) {
         Collection collection = arrayList;
         pairs = (Pair[]) collection.toArray(new Pair[0]);
      } else {
         pairs = null;
      }

      return pairs;
   }

   public final int computeIntValue(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message) {
      int value = 16777215;

      try {
         value = Integer.valueOf(StringsKt.substringAfterWithDefaults(message.toString(), '#', null, 2, null), 16).intValue();
      } catch (NumberFormatException e) {
         databaseSession.getPlatform().getLogger().warning("Invalid color format in embed configuration, ignoring.");
      }

      return value;
   }

   public final String computeText(@NotNull CharSequence message) {
      return LiteColor.stripColor(JsonMessageModule.KOTLIN_DATA.computeText(message.toString()));
   }

   public final void sendWebhook(
      @NotNull DatabaseSession databaseSession,
   @NotNull DiscordWebhook discordWebhook,
   @NotNull CharSequence message,
   @NotNull CharSequence message2,
   int value,
   @Nullable String text,
   @Nullable Pair[] pairs,
   boolean flag
) {
      if (message2.length() != 0 || pairs != null) {
         try {
            DiscordEmbed kotlinHelper23 = new DiscordEmbed(this.computeText(message), this.computeText(message2), null, value, null, text, null, null, null, 468, null);
            if (pairs != null) {
               Pair[] pairs2 = pairs;
               int value2 = 0;

               for (int i = pairs2.length; value2 < i; value2++) {
                  Pair pair = pairs2[value2];
                  String string = (String)pair.component1();
                  String string2 = (String)pair.component2();
                  kotlinHelper23.computeKotlinHelper(string, string2, flag);
               }
            }

            DiscordEmbed[] kotlinHelpers = new DiscordEmbed[]{kotlinHelper23};
            discordWebhook.setList3(CollectionsKt.arrayListOf(kotlinHelpers));
            discordWebhook.perform();
         } catch (Exception e) {
            if (!(e instanceof IllegalArgumentException) && !(e instanceof MalformedURLException)) {
               databaseSession.getPlatform().getLogger().warning("Attempted to post Discord message but encountered an exception!\nJSON data:" + discordWebhook.getJSONObject());
               databaseSession.getDatabaseModule().logConnectionError(e);
            } else {
               databaseSession.getPlatform().getLogger().warning("Unable to post Discord message to invalid URL: '" + discordWebhook.getText6() + "': " + e.getLocalizedMessage());
            }
         }
      }
   }

   public DiscordWebhookNotifier(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
