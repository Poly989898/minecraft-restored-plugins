package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ListAllEntitiesCommand extends Command<MythicBukkit> {
   public ListAllEntitiesCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player player = (Player)sender;
      double radius = sender instanceof Player ? Double.MAX_VALUE : -1.0;
      if (args.length > 0) {
         radius = sender instanceof Player ? Double.valueOf(args[0]) : -1.0;
      }

      List<Entity> found = new ArrayList<>();

      for (Entity entity : player.getWorld().getEntities()) {
         if (radius > 0.0) {
            double distanceSq = entity.getLocation().distanceSquared(player.getLocation());
            if (distanceSq > Math.pow(radius, 2.0)) {
               continue;
            }
         }

         found.add(entity);
      }

      Collections.sort(found, (e1, e2) -> {
         double d1 = e1.getLocation().distanceSquared(((Player)sender).getLocation());
         double d2 = e2.getLocation().distanceSquared(((Player)sender).getLocation());
         return (int)(d1 - d2);
      });
      int size = found.size();
      CommandHelper.sendSuccess(sender, "Entities Found &7(" + size + " total &7)");

      for (Entity entity : found) {
         String x = String.valueOf(Numbers.round(entity.getLocation().getX(), 2));
         String y = String.valueOf(Numbers.round(entity.getLocation().getY(), 2));
         String z = String.valueOf(Numbers.round(entity.getLocation().getZ(), 2));
         double d = Numbers.round(entity.getLocation().distance(((Player)sender).getLocation()), 2);
         String name = entity.getType().toString();
         if (MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).isPresent()) {
            name = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).get().getType().getInternalName();
         }

         TextComponent baseMessage = new TextComponent(ColorString.get("&6+-- &f" + name + " &7- &f" + d + " away "));
         TextComponent tpMessage = new TextComponent(ColorString.get("&7[&aTP&7]"));
         TextComponent infoMessage = new TextComponent(ColorString.get("&7[&bInfo&7]"));
         TextComponent deleteMessage = new TextComponent(ColorString.get("&7[&cD&7]"));
         TextComponent tpHereMessage = new TextComponent(ColorString.get("&7[&dTPHERE&7]"));
         tpMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/minecraft:tp " + x + " " + y + " " + z));
         infoMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mythicmobs:mythicmobs u activeinfo " + entity.getUniqueId()));
         deleteMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mythicmobs:mythicmobs u activeremove " + entity.getUniqueId()));
         tpHereMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/execute as " + entity.getUniqueId() + " run tp " + ((Player)sender).getName()));
         baseMessage.addExtra(tpMessage);
         baseMessage.addExtra(" ");
         baseMessage.addExtra(infoMessage);
         baseMessage.addExtra(" ");
         baseMessage.addExtra(deleteMessage);
         baseMessage.addExtra(" ");
         baseMessage.addExtra(tpHereMessage);
         sender.sendMessage(baseMessage);
      }

      return true;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? (List)StringUtil.copyPartialMatches(
            args[0],
            this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(),
            new ArrayList()
         )
         : null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.listactive";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "listallentities";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"listall", "la"};
   }
}
