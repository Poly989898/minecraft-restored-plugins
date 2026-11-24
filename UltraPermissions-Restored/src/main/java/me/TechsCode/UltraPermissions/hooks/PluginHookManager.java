package me.TechsCode.UltraPermissions.hooks;

import java.util.HashMap;
import java.util.HashSet;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.hooks.pluginHooks.MvdwPlaceholderAPIHook;
import me.TechsCode.UltraPermissions.hooks.pluginHooks.NametagEditHook;
import me.TechsCode.UltraPermissions.hooks.pluginHooks.PlaceholderAPIHook;
import me.TechsCode.UltraPermissions.hooks.pluginHooks.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginHookManager implements Listener {
   private final UltraPermissions plugin;
   private final HashMap<String, Runnable> hooks;
   private PlaceholderAPIHook placeholderAPIHook;
   private MvdwPlaceholderAPIHook mvdwPlaceholderAPIHook;
   private VaultHook vaultHook;
   private NametagEditHook nametagEditHook;

   public PluginHookManager(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      this.hooks = new HashMap<>();
      this.hooks.put("NametagEdit", () -> this.nametagEditHook = new NametagEditHook(ultraPermissions));
      this.hooks.put("Vault", () -> this.vaultHook = new VaultHook(ultraPermissions));
      this.hooks.put("PlaceholderAPI", () -> this.placeholderAPIHook = new PlaceholderAPIHook(ultraPermissions));
      this.hooks.put("MVdWPlaceholderAPI", () -> this.mvdwPlaceholderAPIHook = new MvdwPlaceholderAPIHook(ultraPermissions));
      new HashSet<>(this.hooks.keySet()).forEach(text -> this.tryHook(false, text));
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
   }

   public void tryHook(boolean flag, String text) {
      if (this.hooks.containsKey(text)) {
         if (flag || Bukkit.getPluginManager().isPluginEnabled(text)) {
            this.hooks.get(text).run();
            this.hooks.remove(text);
            this.plugin.log("Hooked into " + text);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onLoad(PluginEnableEvent event) {
      this.tryHook(true, event.getPlugin().getName());
   }

   public String replacePlaceholders(Player player, String text) {
      if (this.placeholderAPIHook != null) {
         text = this.placeholderAPIHook.replace(player, text);
      }

      if (this.mvdwPlaceholderAPIHook != null) {
         text = this.mvdwPlaceholderAPIHook.replace(player, text);
      }

      return text;
   }

   public void clearCaches() {
      if (this.vaultHook != null) {
         this.vaultHook.clearCache();
      }
   }
}
