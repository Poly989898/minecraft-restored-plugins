package io.lumine.mythic.bukkit.utils.permissions;

import io.lumine.mythic.bukkit.utils.Schedulers;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

public final class Permissions {
   public static final LuckPerms getLuckPerms() {
      return LuckPermsProvider.get();
   }

   public static final boolean has(Player player, String permission) {
      if (!player.isOnline()) {
         return false;
      } else {
         boolean op = player.isOp();

         boolean flag;
         try {
            if (op) {
               player.setOp(false);
            }

            flag = player.hasPermission(permission);
         } finally {
            if (op) {
               player.setOp(true);
            }
         }

         return flag;
      }
   }

   public void addTransient(Player player, String permission) {
      this.addTransient(player, PermissionNode.builder(permission).build());
   }

   public void addTransient(Player player, Node node) {
      Schedulers.async().run(() -> {
         User user = getLuckPerms().getUserManager().getUser(player.getUniqueId());
         user.transientData().add(node);
      });
   }

   public void removeTransient(Player player, String permission) {
      this.removeTransient(player, PermissionNode.builder(permission).build());
   }

   public void removeTransient(Player player, Node node) {
      LuckPerms lp = LuckPermsProvider.get();
      Schedulers.async().run(() -> {
         User user = lp.getUserManager().getUser(player.getUniqueId());
         user.transientData().remove(node);
      });
   }

   private Permissions() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
