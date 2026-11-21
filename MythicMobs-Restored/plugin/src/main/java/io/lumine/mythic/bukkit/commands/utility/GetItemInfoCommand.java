package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class GetItemInfoCommand extends Command<MythicBukkit> {
   public GetItemInfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      ItemStack item = p.getInventory().getItemInMainHand();
      if (item != null && !item.getType().equals(Material.AIR)) {
         sender.sendMessage(ColorString.get("&6&lInformation About Held Item&f:"));
         sender.sendMessage(ColorString.get("&6Material&f: &7" + item.getType().toString()));
         if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
               sender.sendMessage(ColorString.get("&6Item Display&f: &7" + meta.getDisplayName()));
            }

            try {
               if (meta instanceof Damageable) {
                  sender.sendMessage(ColorString.get("&6Durability&f: &7" + ((Damageable)meta).getDamage() + " / " + ((Damageable)meta).getMaxDamage()));
               }
            } catch (Exception e) {
            } catch (Error ex) {
            }
         }

         if (ServerVersion.isAfter(MinecraftVersions.v1_20_3)) {
            CommandHelper.send(sender, "<gray><bold>>> <gold><bold>Component Data");
            CommandHelper.send(sender, "<white>" + this.getPlugin().getVolatileCodeHandler().getItemHandler().dumpComponentData(item));
            CommandHelper.send(sender, "<gray>");
         }

         try {
            CommandHelper.send(sender, "<gray><bold>>> <gold><bold>NBT Data");
            CommandHelper.send(sender, "<white>" + this.getPlugin().getVolatileCodeHandler().getItemHandler().dumpNBTData(item));
            CommandHelper.send(sender, "<gray>");
         } catch (Exception ex2) {
         }

         return true;
      } else {
         CommandHelper.sendError(sender, "You must be holding a valid item!");
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.getiteminfo";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "getiteminfo";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"gii"};
   }
}
