package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ListActiveCommand extends Command<MythicBukkit> {
   public ListActiveCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicMob filter = null;
      double radius = sender instanceof Player ? Double.MAX_VALUE : -1.0;
      if (args.length > 0 && !args[0].equals("*")) {
         if (args[0].matches("[0-9]*")) {
            radius = sender instanceof Player ? Double.valueOf(args[0]) : -1.0;
         } else {
            filter = this.getPlugin().getMobManager().getMythicMob(args[0]).orElseGet(() -> null);
         }
      }

      if (args.length > 1) {
         radius = sender instanceof Player ? Double.valueOf(args[1]) : -1.0;
      }

      Map<MythicMob, List<ActiveMob>> found = new HashMap<>();

      for (ActiveMob am : MythicBukkit.inst().getMobManager().getActiveMobs()) {
         try {
            if (filter == null || am.getType().getInternalName().equals(filter.getInternalName())) {
               if (radius > 0.0) {
                  double distanceSq = am.getEntity().getBukkitEntity().getLocation().distanceSquared(((Player)sender).getLocation());
                  if (distanceSq > Math.pow(radius, 2.0)) {
                     continue;
                  }
               }

               if (!found.containsKey(am.getType())) {
                  found.put(am.getType(), new ArrayList<>());
               }

               found.get(am.getType()).add(am);
            }
         } catch (Error | Exception e) {
            sender.sendMessage(Text.colorize("&cFailed to get information on " + am.getMobType() + "." + am.getUniqueId()));
            if (am.getEntity() == null) {
               sender.sendMessage(Text.colorize("&7- &eEntity is null"));
            }

            if (am.getType() == null) {
               sender.sendMessage(Text.colorize("&7- &eType is null"));
            }
         }
      }

      if (filter != null) {
         List<ActiveMob> filterFound = found.get(filter);
         CommandHelper.sendSuccess(
            sender, "ActiveMobs of Type &f" + filter.getInternalName() + " &7(" + (filterFound != null ? filterFound.size() : 0) + " total&7)"
         );
      } else {
         int size = found.values().stream().mapToInt(List::size).sum();
         CommandHelper.sendSuccess(sender, "ActiveMobs Found &7(" + size + " total&7)");
      }

      for (Entry<MythicMob, List<ActiveMob>> entry : found.entrySet()) {
         sender.sendMessage(ColorString.get("&7- &f" + entry.getKey().getInternalName() + " &7- &a" + entry.getValue().size()));
         if (radius > 0.0) {
            Collections.sort(entry.getValue(), (am1, am2) -> {
               double d1 = am1.getEntity().getBukkitEntity().getLocation().distanceSquared(((Player)sender).getLocation());
               double d2 = am2.getEntity().getBukkitEntity().getLocation().distanceSquared(((Player)sender).getLocation());
               return (int)(d1 - d2);
            });
         }

         for (ActiveMob am : entry.getValue()) {
            String x = String.valueOf(Numbers.round(am.getLocation().getX(), 2));
            String y = String.valueOf(Numbers.round(am.getLocation().getY(), 2));
            String z = String.valueOf(Numbers.round(am.getLocation().getZ(), 2));
            String j = String.valueOf(Numbers.round(am.getLocation().getX(), 0));
            String k = String.valueOf(Numbers.round(am.getLocation().getY(), 0));
            String i = String.valueOf(Numbers.round(am.getLocation().getZ(), 0));
            double d = 0.0;
            if (sender instanceof Player) {
               d = Numbers.round(am.getEntity().getBukkitEntity().getLocation().distance(((Player)sender).getLocation()), 2);
            }

            Component message = (((((((((Component.empty()
                                          .append(Text.parse("&6+-- &f(" + j + "," + k + "," + i + ") &7- &f" + d + " away ")))
                                       .append(Text.parse("&7[&aTP&7]").clickEvent(ClickEvent.runCommand("/minecraft:tp " + x + " " + y + " " + z))))
                                    .append(Component.space()))
                                 .append(
                                    Text.parse("&7[&dTPHere&7]")
                                       .clickEvent(ClickEvent.runCommand("/minecraft:execute as " + am.getUniqueId() + " run minecraft:tp " + sender.getName()))
                                 ))
                              .append(Component.space()))
                           .append(Text.parse("&7[&cD&7]").clickEvent(ClickEvent.runCommand("/mythicmobs:mythicmobs u activeremove " + am.getUniqueId()))))
                        .append(Component.space()))
                     .append(Text.parse("&7[&cK&7]").clickEvent(ClickEvent.runCommand("/minecraft:execute as " + am.getUniqueId() + " run minecraft:kill @s"))))
                  .append(Component.space()))
               .append(Text.parse("&7[&bInfo&7]").clickEvent(ClickEvent.runCommand("/mythicmobs:mythicmobs u activeinfo " + am.getUniqueId())));
            Text.sendMessage(sender, message);
         }
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
      return "listactive";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"la"};
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
      Map<K, V> result = new LinkedHashMap<>();
      Stream<Entry<K, V>> st = map.entrySet().stream();
      st.sorted(Comparator.comparing(e -> e.getValue())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
      return result;
   }
}
