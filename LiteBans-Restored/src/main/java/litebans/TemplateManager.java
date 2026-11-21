package litebans;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TemplateManager {
   private final Platform platform;
   private final File folder;
   private final boolean flag2;
   private final ArrayList templates;
   private List groups;
   private ConfigFile configFile;

   public TemplateManager(@NotNull Platform platform, @NotNull File file, boolean flag) {
      this.platform = platform;
      this.folder = file;
      this.flag2 = flag;
      this.templates = new ArrayList();
      this.groups = new ArrayList();
   }

   public TemplateManager(Platform platform, File file, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         File dataFolder = platform.getDataFolder();
         String text = "templates.yml";
         file = new File(dataFolder, text);
      }

      if ((value & 4) != 0) {
         flag = false;
      }

      this(platform, file, flag);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final File getFolder() {
      return this.folder;
   }

   public final ArrayList getTemplates() {
      return this.templates;
   }

   public final List getGroups() {
      return this.groups;
   }

   public final void setGroups(@NotNull List list) {
      this.groups = list;
   }

   public final ConfigFile getConfig() {
      return this.configFile;
   }

   public final void setConfig(@Nullable ConfigFile configFile) {
      this.configFile = configFile;
   }

   public final Template computeTemplate(@NotNull PunishmentType punishmentType, @NotNull String text) {
      Iterable iterable = this.templates;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         Template template = (Template)obj;
         if (template.getType() == punishmentType.obtainPunishmentType()) {
            arrayList.add(obj);
         }
      }

      Iterator iterator2 = arrayList.iterator();

      Object obj2;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj3 = iterator2.next();
            Template template2 = (Template)obj3;
            if (!StringsKt.checkText(template2.getName(), text, true)) {
               continue;
            }

            obj2 = obj3;
            break;
         }

         obj2 = null;
         break;
      }

      return (Template)obj2;
   }

   public final Template findTemplate(@NotNull Punishment punishment) {
      return this.computeTemplate(punishment.getType(), punishment.getTemplate() & 255);
   }

   public final Template computeTemplate(@NotNull PunishmentType punishmentType, byte b) {
      Iterable iterable = this.templates;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         Template template = (Template)obj;
         if (template.getType() == punishmentType.obtainPunishmentType()) {
            arrayList.add(obj);
         }
      }

      Iterator iterator2 = arrayList.iterator();

      Object obj2;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj3 = iterator2.next();
            Template template2 = (Template)obj3;
            if (template2.getId() != b) {
               continue;
            }

            obj2 = obj3;
            break;
         }

         obj2 = null;
         break;
      }

      return (Template)obj2;
   }

   public final Template computeTemplate(@NotNull PunishmentType punishmentType, int value) {
      Iterable iterable = this.templates;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         Template template = (Template)obj;
         if (template.getType() == punishmentType.obtainPunishmentType()) {
            arrayList.add(obj);
         }
      }

      Iterator iterator2 = arrayList.iterator();

      Object obj2;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj3 = iterator2.next();
            Template template2 = (Template)obj3;
            if ((template2.getId() & 255) != value) {
               continue;
            }

            obj2 = obj3;
            break;
         }

         obj2 = null;
         break;
      }

      return (Template)obj2;
   }

   public final TemplateGroup computeTemplateGroup(@NotNull PunishmentType punishmentType, @NotNull Template template) {
      Iterable iterable = this.groups;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         TemplateGroup templateGroup = (TemplateGroup)obj;
         if (templateGroup.getPunishmentType() == punishmentType.obtainPunishmentType()) {
            arrayList.add(obj);
         }
      }

      Iterator iterator2 = arrayList.iterator();

      Object obj2;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj3 = iterator2.next();
            TemplateGroup templateGroup2 = (TemplateGroup)obj3;
            if (!templateGroup2.getMap3().containsKey(template)) {
               continue;
            }

            obj2 = obj3;
            break;
         }

         obj2 = null;
         break;
      }

      return (TemplateGroup)obj2;
   }

   public final List getTemplatesFor(@NotNull PunishmentType punishmentType) {
      Iterable iterable = this.templates;
      var arrayList = new ArrayList();

      for (Object obj : iterable) {
         Template template = (Template)obj;
         if (template.getType() == punishmentType.obtainPunishmentType()) {
            arrayList.add(obj);
         }
      }

      Iterable iterable2 = arrayList;
      var arrayList2 = new ArrayList();

      for (Object obj2 : iterable2) {
         Template template2 = (Template)obj2;
         var list = template2.getDefaultInfo().getFlags();
         boolean flag;
         if (list instanceof Collection && ((Collection)list).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator2 = list.iterator();

            while (true) {
               if (!iterator2.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj3 = iterator2.next();
               String string = (String)obj3;
               if (StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"--hide", false, 2, null)) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag) {
            arrayList2.add(obj2);
         }
      }

      Iterable iterable3 = arrayList2;
      var arrayList3 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList2, 10));

      for (Object obj4 : iterable3) {
         Template template3 = (Template)obj4;
         arrayList3.add(UByte.box(template3.getId()));
      }

      return arrayList3;
   }

   public final List computeList(@NotNull ConfigKeyReader configKeyReader, @Nullable String text) {
      List stringList = configKeyReader.getStringList(text);
      if (stringList == null) {
         String string = configKeyReader.getString(text);
         if (string != null) {
            CharSequence message = string;
            char[] chars = new char[]{' '};
            stringList = StringsKt.splitWithDefaults(message, chars, false, 0, 6, null);
         } else {
            stringList = null;
         }

         if (stringList == null) {
            stringList = CollectionsKt.emptyList();
         }
      }

      return stringList;
   }

   public final void handleException(@NotNull Exception exception) {
      try {
         if (this.flag2) {
            throw exception;
         }

         ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
         module.logLoadError("templates", exception);
         if (module.isDebugAtLeast(1)) {
            module.handleConfigError(exception);
         }
      } catch (Exception e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
