package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PlatformMarker(obtainByteValue = 0)
public final class BukkitCommandManagerImpl extends BukkitLiteCommandManager {
   public BukkitCommandManagerImpl(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void load() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.enable();
      this.platform.setCommandManager(this);
      this.perform7();
      this.perform8();
      super.load();
   }

   @Override
   public void enable() {
   }

   @Override
   public void reloadConfig() {
      this.perform8();
   }

   private final void perform7() {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      PlatformBridge bridge = this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      Plugin plugin = BukkitBridge.asPluginWithDefaults((BukkitBridge)bridge, null, 1, null);
      Map commands = plugin.getDescription().getCommands();

      for (Entry entry : (Iterable<Entry>) commands.entrySet()) {
         String key = (String)entry.getKey();
         PluginCommand pluginCommand = plugin.getServer().getPluginCommand(key);
         if (pluginCommand != null) {
            if (pluginCommand.getTabCompleter() != null && !Intrinsics.areEqual(pluginCommand.getPlugin(), this.platform)) {
               module.debug(1, "Command /" + key + " is tab-completed by " + pluginCommand.getTabCompleter() + '!');
            }

            pluginCommand.setTabCompleter((TabCompleter)plugin);
            if (!Intrinsics.areEqual(pluginCommand.getPlugin(), this.platform)) {
               module.debug(1, "Command /" + key + " is owned by " + pluginCommand.getPlugin().getDescription().getName() + '!');
            }
         }
      }
   }

   private final void perform8() {
      PlatformBridge bridge = this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      Plugin plugin = BukkitBridge.asPluginWithDefaults((BukkitBridge)bridge, null, 1, null);
      Map commands = plugin.getDescription().getCommands();

      for (Entry entry : (Iterable<Entry>) commands.entrySet()) {
         String key = (String)entry.getKey();
         PluginCommand pluginCommand = plugin.getServer().getPluginCommand(key);
         if (pluginCommand != null && Intrinsics.areEqual(pluginCommand.getPlugin(), this.platform)) {
            pluginCommand.setPermissionMessage(JsonMessageModule.KOTLIN_DATA.computeText(Message.PERMISSION_ERROR.toString()));
         }
      }
   }

   @Override
   public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String text, @NotNull String[] texts) {
      return this.checkCommandSender(commandSender, command, text, texts);
   }

   public final boolean checkCommandSender(@NotNull CommandSender commandSender, @Nullable Command command, @NotNull String text, @NotNull String[] texts) {
      String text2 = null;
      String[] texts2 = texts;
      int value = 0;
      int length2 = texts2.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = true;
            break;
         }

         String text3 = texts2[value];
         if (StringsKt.isBlank(text3)) {
            flag = false;
            break;
         }

         value++;
      }

      String[] texts3;
      if (flag) {
         texts3 = texts;
      } else {
         String[] texts4 = texts;
         var arrayList = new ArrayList();
         int value2 = 0;

         for (int i = texts4.length; value2 < i; value2++) {
            String text4 = texts4[value2];
            if (!StringsKt.isBlank(text4)) {
               arrayList.add(text4);
            }
         }

         Collection collection = arrayList;
         texts3 = (String[]) collection.toArray(new String[0]);
      }

      String[] texts5;
      LiteSender liteSender;
      label55: {
         texts5 = texts3;
         liteSender = this.platform.wrapSender(commandSender);
         if (command != null) {
            text2 = command.getName();
            if (text2 != null) {
               break label55;
            }
         }

         text2 = text;
      }

      String text5 = text2;
      String text6 = text5;
      if (StringsKt.checkText(text5, "litebans:", false, 2, null)) {
         text6 = text6.substring(9);
      }

      AbstractCommand abstractCommand = (AbstractCommand)this.computeLiteCommand(text5);
      if (abstractCommand != null) {
         String permission = abstractCommand.getPermission();
         if (permission != null) {
            Intrinsics.checkNotNull(liteSender);
            LiteSender liteSender2 = liteSender;
            if (!liteSender2.hasPermission(permission)) {
               LiteSender liteSender3 = liteSender;
               CharSequence message = Message.PERMISSION_ERROR;
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender3, message, null, 4, null);
               return true;
            }
         }

         Intrinsics.checkNotNull(liteSender);
         this.handleLiteSender(liteSender, texts5, abstractCommand, text6);
         return true;
      } else {
         String[] texts6 = AltAccountPunisher.computeTexts(AbstractCommand.MUTED_COMMAND_FILTER, text5, texts5);
         AbstractCommand abstractCommand2 = (AbstractCommand)this.computeLiteCommand("ban");
         if (abstractCommand2 == null) {
            Intrinsics.checkNotNull(liteSender);
            LiteSender liteSender4 = liteSender;
            CharSequence message2 = Message.INTERNAL_ERROR;
            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender4, message2, null, 4, null);
            return true;
         } else {
            Intrinsics.checkNotNull(liteSender);
            this.handleLiteSender(liteSender, texts6, abstractCommand2, text6);
            return true;
         }
      }
   }

   @Override
   public List computeList(@Nullable LiteCommand liteCommand, @NotNull String text, @NotNull LiteSender liteSender, @NotNull String[] texts) {
      if (texts.length != 0) {
         LiteSender liteSender2 = liteSender;
         String text2 = "litebans.tabcomplete";
         if (liteSender2.hasPermission(text2)) {
            return AbstractCommand.MUTED_COMMAND_FILTER.computeList(liteCommand, text, liteSender, this.platform, texts);
         }
      }

      return CollectionsKt.emptyList();
   }

   private final void handleLiteSender(LiteSender liteSender, String[] texts, AbstractCommand abstractCommand, String text) {
      if (abstractCommand.obtainFlag7()) {
         this.platform.execute(new LiteRunnableImpl2(liteSender, texts, abstractCommand, text));
      } else {
         abstractCommand.handleLiteSender(liteSender, texts, text);
      }
   }
}
