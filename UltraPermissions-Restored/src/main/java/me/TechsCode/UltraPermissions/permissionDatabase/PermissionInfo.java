package me.TechsCode.UltraPermissions.permissionDatabase;

import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.apache.commons.lang3.StringUtils;

public class PermissionInfo {
   private final String permission;
   private final String plugin;
   private final String description;
   private final String[] commands;
   private final String source;
   private String[] placeholders;
   private static final Phrase COMMANDS_TITLE = Phrase.create("permissionInfo.commands.title", "Commands:", Colors.GRAY);
   private static final Phrase COMMANDS_ENTRY = Phrase.create("permissionInfo.commands.entry", "- **/%command%**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase DESCRIPTION_TITLE = Phrase.create("permissionInfo.description.title", "Description:", Colors.GRAY);

   public PermissionInfo(String text, String plugin, String description, String[] texts, String source) {
      this.permission = text;
      this.plugin = plugin;
      this.description = description;
      this.commands = texts;
      this.source = source;
      this.placeholders = StringUtils.substringsBetween(text, "[", "]");
      this.placeholders = this.placeholders == null ? new String[0] : this.placeholders;
   }

   public String getPermission() {
      return this.permission;
   }

   public String getPlugin() {
      return this.plugin;
   }

   public String getDescription() {
      return this.description;
   }

   public String[] getCommands() {
      return this.commands;
   }

   public String getSource() {
      return this.source;
   }

   public boolean hasPlaceholders() {
      return this.placeholders.length != 0;
   }

   public String[] getPlaceholders() {
      return this.placeholders;
   }

   public List<String> asLore() {
      ArrayList list = new ArrayList();
      if (this.getCommands().length != 0) {
         list.add(COMMANDS_TITLE.get());

         for (String command : this.getCommands()) {
            command = command.replace("/", "");
            list.add(COMMANDS_ENTRY.get().replace("%command%", command));
         }

         list.add("");
      }

      list.add(DESCRIPTION_TITLE.get());

      for (String text : Tools.lineSplitter(this.getDescription(), 60)) {
         list.add("   " + Colors.WHITE + text);
      }

      return list;
   }

   public boolean isThisPermission(String text) {
      if (text.equalsIgnoreCase(this.permission)) {
         return true;
      }

      if (!this.hasPlaceholders()) {
         return false;
      }

      String permission = this.permission;

      for (String placeholder : this.placeholders) {
         permission = permission.replace("[" + placeholder + "]", "$$$");
      }

      for (String part : permission.split("[$$$]")) {
         if (!text.contains(part)) {
            return false;
         }
      }

      return true;
   }
}
