package me.TechsCode.UltraPermissions.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.CachedPlayerPermissionProvider;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import me.TechsCode.UltraPermissions.internal.lookup.PermissionLookup;
import me.TechsCode.UltraPermissions.internal.lookup.checks.AttachedPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.BlankCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefaultPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.DefinedPermissionCheck;
import me.TechsCode.UltraPermissions.internal.lookup.checks.OperatorLookupCheck;
import me.TechsCode.UltraPermissions.permissionlogger.PermissionLogger;
import me.TechsCode.UltraPermissions.storage.collection.PermissionList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class ModifiedPermissible extends PermissibleBase implements Listener {
   private final Player p;
   private final UltraPermissions plugin;
   private final PermissionLogger permissionLogger;
   private final boolean defaultPerms;
   private final List<PermissionAttachment> attachments;
   private final CachedPlayerPermissionProvider permissionProvider;

   public ModifiedPermissible(Player player, UltraPermissions ultraPermissions, PermissionLogger permissionLogger) {
      super(player);
      this.p = player;
      this.plugin = ultraPermissions;
      this.permissionLogger = permissionLogger;
      this.defaultPerms = ultraPermissions.isDefaultPermissionsEnabled();
      this.attachments = new ArrayList<>();
      this.permissionProvider = new CachedPlayerPermissionProvider(ultraPermissions);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
   }

   public void clearCache() {
      this.permissionProvider.clearCache();
   }

   public boolean isPermissionSet(String text) {
      return this.hasPermission(text);
   }

   public boolean isPermissionSet(Permission permission) {
      if (this.isPermissionSet(permission.getName())) {
         return true;
      } else {
         return this.defaultPerms ? permission.getDefault().getValue(this.isOp()) : Permission.DEFAULT_PERMISSION.getValue(this.isOp());
      }
   }

   public boolean hasPermission(Permission permission) {
      return this.hasPermission(permission.getName());
   }

   public boolean hasPermission(String permission) {
      if (permission == null) {
         return false;
      }

      PermissionLookup permissionLookup = new PermissionLookup(permission) {
         @Override
         public LookupCheck[] getChecks() {
            return new LookupCheck[]{
               new OperatorLookupCheck(ModifiedPermissible.this.plugin, ModifiedPermissible.this.p),
               ModifiedPermissible.this.defaultPerms ? new DefaultPermissionCheck(ModifiedPermissible.this.p) : new BlankCheck(),
               new AttachedPermissionCheck() {
                  @Override
                  public List<PermissionAttachment> getAttachments() {
                     return ModifiedPermissible.this.attachments;
                  }
               },
               new DefinedPermissionCheck() {
                  @Override
                  public List<me.TechsCode.UltraPermissions.storage.objects.Permission> getDefinedPermissions() {
                     return ModifiedPermissible.this.permissionProvider
                        .retrieve(ModifiedPermissible.this.p.getUniqueId(), ModifiedPermissible.this.p.getWorld().getName());
                  }
               }
            };
         }
      };
      LookupOutcome lookupOutcome = permissionLookup.perform();
      if (!this.plugin.getToggledPluginsRegistry().isEmpty()) {
         for (String pluginName : this.plugin.getToggledPluginsRegistry()) {
            if (!permission.contains(pluginName)) {
               this.permissionLogger.log(this.p, permission, lookupOutcome.getResult(), lookupOutcome.getSource());
            }
         }

         return lookupOutcome.getResult();
      } else {
         this.permissionLogger.log(this.p, permission, lookupOutcome.getResult(), lookupOutcome.getSource());
         return lookupOutcome.getResult();
      }
   }

   public PermissionAttachment addAttachment(Plugin plugin) {
      PermissionAttachment permissionAttachment = new PermissionAttachment(plugin, this);
      this.attachments.add(permissionAttachment);
      return permissionAttachment;
   }

   public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean flag) {
      PermissionAttachment permissionAttachment = this.addAttachment(plugin);
      permissionAttachment.setPermission(permission, flag);
      return permissionAttachment;
   }

   public PermissionAttachment addAttachment(Plugin plugin, int count) {
      PermissionAttachment permissionAttachment = this.addAttachment(plugin);
      Bukkit.getScheduler().runTaskLater(plugin, permissionAttachment::remove, count);
      return permissionAttachment;
   }

   public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean flag, int count) {
      PermissionAttachment permissionAttachment = this.addAttachment(plugin, count);
      permissionAttachment.setPermission(permission, flag);
      return permissionAttachment;
   }

   public void removeAttachment(PermissionAttachment permissionAttachment) {
      this.attachments.remove(permissionAttachment);
   }

   public void recalculatePermissions() {
   }

   public synchronized void clearPermissions() {
      this.attachments.clear();
   }

   public Set<PermissionAttachmentInfo> getEffectivePermissions() {
      PermissionList list = this.permissionProvider.retrieve(this.p.getUniqueId(), this.p.getWorld().getName());
      return list.stream().map(permission -> new PermissionAttachmentInfo(this, permission.getName(), null, permission.isPositive())).collect(Collectors.toSet());
   }
}
