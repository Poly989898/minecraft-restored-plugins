package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FindCommand extends Command<MythicBukkit> {
   public FindCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      AbstractLocation pLoc = BukkitAdapter.adapt(p.getLocation());
      double radius = 32.0;
      if (args.length > 0 && (args[0].matches("(\\d+\\.\\d+)") || args[0].matches("(\\d+)"))) {
         radius = Double.parseDouble(args[0]);
      }

      CommandHelper.sendHeader(sender);
      sender.sendMessage("\u00a76Spawners within " + radius + " blocks of you:");

      for (MythicSpawner ms : this.getPlugin().getSpawnerManager().getSpawners()) {
         try {
            if (ms.getLocation().getWorld() != null && ms.getLocation().getWorld().getUniqueId().equals(pLoc.getWorld().getUniqueId())) {
               double distance = ms.distanceTo(pLoc);
               if (distance <= radius) {
                  TextComponent message = new TextComponent("\u00a76- \u00a7a" + ms.getInternalName() + " \u00a7b(" + distance + " blocks away");
                  message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mythicmobs:mythicmobs spawners ui " + ms.getInternalName()));
                  p.spigot().sendMessage(message);
               }
            }
         } catch (Exception e) {
         }
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.find";
   }

   @Override
   public boolean isConsoleFriendly() {
      return false;
   }

   @Override
   public String getName() {
      return "find";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"listnear", "ln"};
   }
}
