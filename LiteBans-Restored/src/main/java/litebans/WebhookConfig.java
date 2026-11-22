package litebans;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class WebhookConfig {
   private final Platform platform;
   private final File file;
   private final HashMap hashMap;
   private String text3;
   private final ConfigModule configModule;

   public WebhookConfig(@NotNull Platform platform, @NotNull File file) {
      this.platform = platform;
      this.file = file;
      this.hashMap = new HashMap();
      this.text3 = "";
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   public WebhookConfig(Platform platform, File file, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         File dataFolder = platform.getDataFolder();
         String text = "webhooks.yml";
         file = new File(dataFolder, text);
      }

      this(platform, file);
   }

   public final String computeText(@NotNull String text) {
      String string = this.hashMap.getOrDefault(text, "").toString();
      ConfigModule configModule = this.configModule;
      byte b = 1;
      if (configModule.isDebugAtLeast(b)) {
         configModule.logDebug("Fetch webhook: " + text + " => '" + string + '\'');
      }

      return !Intrinsics.areEqual(string, "on") && !Intrinsics.areEqual(string, "true") && string.length() != 0 ? (!Intrinsics.areEqual(string, "off") && !Intrinsics.areEqual(string, "false") ? string : "") : this.text3;
   }

   public final WebhookConfig obtainKotlinData() {

      try {
         this.platform.saveResource("webhooks.yml");
         ConfigFile throwIfFailed2 = this.platform.loadConfigFile(this.file).throwIfFailed();
         throwIfFailed2.computeConfigFile(ConfigFile.computeText("webhooks.yml", null));
         String string = throwIfFailed2.getString("default_webhook", "");
         CharSequence message = string;
         this.text3 = message == null || message.length() == 0 ? this.configModule.getSettings().getNotifyNotifyDiscord() : string;
         this.hashMap.clear();
         PunishmentTypeCompanion punishmentTypeCompanion = PunishmentType.PUNISHMENT_TYPE_COMPANION;
         Iterable iterable = punishmentTypeCompanion.obtainList();
         var arrayList = new ArrayList();

         for (Object obj : iterable) {
            PunishmentType punishmentType = (PunishmentType)obj;
            List list;
            switch (PunishmentTypeSwitchMap2.value[punishmentType.ordinal()]) {
               case 1:
                  list = CollectionsKt.computeList(punishmentType.obtainPunishmentType());
                  break;
               case 2:
                  Object[] objs = new Object[]{punishmentType.obtainPunishmentType(), "un" + punishmentType.obtainPunishmentType()};
               list = CollectionsKt.listOf(objs);
               break;
               default:
                  Object[] objs2 = new Object[]{punishmentType.obtainPunishmentType(), "temp_" + punishmentType.obtainPunishmentType(), "temp_ip" + punishmentType.obtainPunishmentType(), "ip" + punishmentType.obtainPunishmentType(), "un" + punishmentType.obtainPunishmentType()};
               list = CollectionsKt.listOf(objs2);
            }

            Iterable iterable2 = list;
            var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable2, 10));

            for (Object obj2 : iterable2) {
               arrayList2.add(obj2.toString());
            }

            KotlinUtil37.checkCollection(arrayList, arrayList2);
         }

         Iterable iterable3 = punishmentTypeCompanion.obtainList();
         Collection collection = arrayList;
         var arrayList3 = new ArrayList();

         for (Object obj3 : iterable3) {
            PunishmentType punishmentType2 = (PunishmentType)obj3;
            List list2;
            switch (PunishmentTypeSwitchMap2.value[punishmentType2.ordinal()]) {
               case 1:
                  list2 = CollectionsKt.computeList(punishmentType2.obtainPunishmentType());
                  break;
               case 2:
                  Object[] objs3 = new Object[]{punishmentType2.obtainPunishmentType(), "un" + punishmentType2.obtainPunishmentType()};
               list2 = CollectionsKt.listOf(objs3);
               break;
               default:
                  Object[] objs4 = new Object[]{punishmentType2.obtainPunishmentType(), "temp_" + punishmentType2.obtainPunishmentType(), "temp_ip" + punishmentType2.obtainPunishmentType(), "ip" + punishmentType2.obtainPunishmentType(), "un" + punishmentType2.obtainPunishmentType()};
               list2 = CollectionsKt.listOf(objs4);
            }

            Iterable iterable4 = list2;
            var arrayList4 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable4, 10));

            for (Object obj4 : iterable4) {
               arrayList4.add(obj4.toString());
            }

            Iterable iterable5 = arrayList4;
            var arrayList5 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList4, 10));

            for (Object obj5 : iterable5) {
               String string2 = (String)obj5;
               arrayList5.add("silent_" + string2);
            }

            KotlinUtil37.checkCollection(arrayList3, arrayList5);
         }

         for (String text : (Iterable<String>) CollectionsKt.plus(collection, arrayList3)) {
            this.hashMap.put(text, this.computeText(throwIfFailed2, "webhooks." + text, ""));
         }

         this.hashMap.put("alts", this.computeText(throwIfFailed2, "webhooks.alts", "off"));
      } catch (Exception e) {
         this.configModule.logLoadError("webhooks", e);
      }

      return this;
   }

   private final String computeText(ConfigFile configFile, String text, String text2) {
      Object obj = configFile.get(text, text2);
      if (obj instanceof Boolean) {
         return (Boolean)obj ? "on" : "off";
      }

      if (obj instanceof String) {
         return (String)obj;
      }

      StandardKt.notImplemented();
      throw new CommandAbortException();
   }
}
