package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillAllCommand extends Command<MythicBukkit> {
   public KillAllCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      int radius = 0;
      boolean literally = false;
      List<String> argList = new ArrayList<>(Arrays.asList(args));
      Iterator<String> iterator = argList.iterator();

      while (iterator.hasNext()) {
         String arg = iterator.next();
         if (arg.startsWith("-") && arg.contains("p")) {
            literally = true;
            iterator.remove();
         }
      }

      args = argList.toArray(new String[0]);
      if (args.length > 0 && sender instanceof Player) {
         radius = Integer.parseInt(args[0]);
      }

      if (args.length > 1 && args[1].equals("LITERALLY")) {
         literally = true;
      }

      int amount = 0;
      if (radius > 0) {
         AbstractPlayer player = BukkitAdapter.adapt((Player)sender);

         for (ActiveMob am : MythicBukkit.inst().getMobManager().getActiveMobs()) {
            if ((am.getType().getDespawnMode().getObeysKillCommand() || literally)
               && am.getLocation().getWorld().equals(player.getWorld())
               && am.getLocation().distanceSquared(player.getLocation()) <= Math.pow(radius, 2.0)
               && am.getEntity().isLoaded()) {
               am.setDespawned();
               MythicBukkit.inst().getMobManager().unregisterActiveMob(am);
               am.getEntity().remove();
               amount++;
            }
         }
      } else if (literally) {
         amount = MythicBukkit.inst().getMobManager().removeAllAllMobs();
      } else {
         amount = MythicBukkit.inst().getMobManager().removeAllMobs();
      }

      sender.sendMessage(ChatColor.GREEN + "Removed " + amount + " Mythic Mobs!");
      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.killall";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "killall";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"ka"};
   }
}
