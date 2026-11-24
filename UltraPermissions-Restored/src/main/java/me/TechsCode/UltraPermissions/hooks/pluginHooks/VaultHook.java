package me.TechsCode.UltraPermissions.hooks.pluginHooks;

import me.TechsCode.UltraPermissions.UltraPermissions;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public class VaultHook {
   private final UltraPermissions plugin;
   private final VaultPermissionHook permissionHook;
   private final VaultChatHook vaultChatHook;

   public VaultHook(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      this.permissionHook = new VaultPermissionHook(ultraPermissions);
      this.vaultChatHook = new VaultChatHook(this.permissionHook, ultraPermissions);
      this.hook();
   }

   public void hook() {
      ServicesManager servicesManager = this.plugin.getBootstrap().getServer().getServicesManager();
      servicesManager.register(Permission.class, this.permissionHook, (Plugin)this.plugin.getBootstrap(), ServicePriority.High);
      servicesManager.register(Chat.class, this.vaultChatHook, (Plugin)this.plugin.getBootstrap(), ServicePriority.High);
   }

   public void unhook() {
      ServicesManager servicesManager = this.plugin.getBootstrap().getServer().getServicesManager();
      servicesManager.unregister(Permission.class, this.permissionHook);
   }

   public void clearCache() {
      this.permissionHook.clearCache();
      this.vaultChatHook.clearCache();
   }
}
