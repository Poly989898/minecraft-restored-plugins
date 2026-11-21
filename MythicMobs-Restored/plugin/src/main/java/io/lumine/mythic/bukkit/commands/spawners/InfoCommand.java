package io.lumine.mythic.bukkit.commands.spawners;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class InfoCommand extends Command<MythicBukkit> {
   public InfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      if (args.length < 1) {
         sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "You must enter the name of a mob spawner!");
         return true;
      } else {
         String name = args[0];
         MythicSpawner ms = MythicBukkit.inst().getSpawnerManager().getSpawnerByName(name);
         if (ms == null) {
            sender.sendMessage(ChatColor.GOLD + "[MythicMobs] " + ChatColor.RED + "The spawner entered does not exist!");
            return true;
         } else {
            MythicMob mm = MythicBukkit.inst().getMobManager().getMythicMob(ms.getTypeName()).orElseGet(() -> null);
            Text.sendMessage(sender, "<gold><bold>Spawner <aqua>" + ms.getName() + "<gray>:");
            Text.sendMessage(
               sender,
               "<gray>\u25ba <yellow>Location<gray>: <white>"
                  + ms.getLocation().getBlockX()
                  + ","
                  + ms.getLocation().getBlockY()
                  + ","
                  + ms.getLocation().getBlockZ()
                  + "@"
                  + ms.getLocation().getWorld().getName()
            );
            Text.sendMessage(sender, "<gray>\u25ba <yellow>Mob(s)<gray>: <white>" + ms.getTypeName());
            if (ms.getGroup() != null && !ms.getGroup().isEmpty()) {
               Text.sendMessage(sender, "<gray>\u25ba <yellow>Group<gray>: <white>" + ms.getGroup());
            }

            Text.sendMessage(sender, "<gray>\u25ba <yellow>Cooldown<gray>: <white>" + ms.getCooldownSeconds());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>Warmup<gray>: <white>" + ms.getWarmupSeconds());
            Text.sendMessage(
               sender,
               "<gray>\u25ba <yellow>MaxMobs<gray>: <white>" + ms.getMaxMobs().serialize() + " <gray>(Currently <white>" + ms.getMaxMobs().get(ms) + "<gray>)"
            );
            Text.sendMessage(sender, "<gray>\u25ba <yellow>MobLevel<gray>: <white>" + ms.getMobLevel());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>MobsPerSpawn<gray>: <white>" + ms.getMobsPerSpawn());
            Text.sendMessage(
               sender, "<gray>\u25ba <yellow>SpawnRadius<gray>: <white>" + ms.getSpawnRadius() + " Horizontal " + ms.getSpawnRadiusY() + " Vertical"
            );
            Text.sendMessage(sender, "<gray>\u25ba <yellow>ActivationRange<gray>: <white>" + ms.getActivationRange());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>LeashRange<gray>: <white>" + ms.getLeashRange());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>HealOnLeash<gray>: <white>" + ms.isHealOnLeash());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>Breakable<gray>: <white>" + ms.isBreakable());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>Chance<gray>: <white>" + ms.getChance());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>RandomRotation<gray>: <white>" + ms.isRandomRotation());
            Text.sendMessage(sender, "<gray>\u25ba <yellow>UseWorldScaling<gray>: <white>" + ms.isUseWorldScaling());
            if (!ms.getConditions().isEmpty()) {
               Text.sendMessage(sender, "<gold>Activation Conditions<gray>:");

               for (SkillCondition condition : ms.getConditions()) {
                  Text.sendMessage(sender, "<gray>- <white>" + condition.getLine());
               }
            }

            if (!ms.getConditionsTarget().isEmpty()) {
               Text.sendMessage(sender, "<gold>Spawn Conditions<gray>:");

               for (SkillCondition condition : ms.getConditionsTarget()) {
                  Text.sendMessage(sender, "<gray>- <white>" + condition.getLine());
               }
            }

            Text.sendMessage(
               sender, "<red>Current # Mobs Spawned<gray>: <white>" + ms.getNumberOfMobs() + "<gray>(Cached: <white>" + ms.getNumberOfCachedMobs() + "<gray>)"
            );
            sender.sendMessage(
               ChatColor.RED
                  + "Spawner On Cooldown: "
                  + ChatColor.WHITE
                  + ms.isOnCooldown()
                  + " ("
                  + ChatColor.GRAY
                  + ms.getRemainingCooldownSeconds()
                  + " seconds)"
            );
            sender.sendMessage(
               ChatColor.RED + "Spawner On Warmup: " + ChatColor.WHITE + ms.isOnWarmup() + " (" + ChatColor.GRAY + ms.getRemainingWarmupSeconds() + " seconds)"
            );
            sender.sendMessage(ChatColor.RED + "Spawner Internal Cooldown: " + ChatColor.GRAY + ms.getInternalCooldown() + " ticks");
            Text.sendMessage(sender, "<gold>Mobs Associated with Spawner</gold><white>:");

            for (UUID uuid : ms.getAssociatedMobs()) {
               Entity entity = Bukkit.getEntity(uuid);
               if (entity == null) {
                  Text.sendMessage(sender, "<white>- <red>" + uuid + " <gray>(Dead)");
               } else {
                  String x = String.valueOf(Numbers.round(entity.getLocation().getX(), 2));
                  String y = String.valueOf(Numbers.round(entity.getLocation().getY(), 2));
                  String z = String.valueOf(Numbers.round(entity.getLocation().getZ(), 2));
                  double d = Numbers.round(entity.getLocation().distance(((Player)sender).getLocation()), 2);
                  String entityName = entity.getType().toString();
                  if (MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).isPresent()) {
                     entityName = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).get().getType().getInternalName();
                  }

                  TextComponent baseMessage = new TextComponent(ColorString.get("&6+-- &f" + entityName + " &7- &f" + d + " away "));
                  TextComponent tpMessage = new TextComponent(ColorString.get("&7[&aTP&7]"));
                  TextComponent infoMessage = new TextComponent(ColorString.get("&7[&bInfo&7]"));
                  TextComponent deleteMessage = new TextComponent(ColorString.get("&7[&cD&7]"));
                  TextComponent tpHereMessage = new TextComponent(ColorString.get("&7[&dTPHERE&7]"));
                  tpMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/minecraft:tp " + x + " " + y + " " + z));
                  infoMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mythicmobs:mythicmobs u activeinfo " + entity.getUniqueId()));
                  deleteMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/mythicmobs:mythicmobs u activeremove " + entity.getUniqueId()));
                  tpHereMessage.setClickEvent(
                     new ClickEvent(Action.RUN_COMMAND, "/execute as " + entity.getUniqueId() + " run tp " + ((Player)sender).getName())
                  );
                  baseMessage.addExtra(tpMessage);
                  baseMessage.addExtra(" ");
                  baseMessage.addExtra(infoMessage);
                  baseMessage.addExtra(" ");
                  baseMessage.addExtra(deleteMessage);
                  baseMessage.addExtra(" ");
                  baseMessage.addExtra(tpHereMessage);
                  sender.sendMessage(baseMessage);
               }
            }

            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return args.length == 1
         ? CollectionUtils.filterMatches(args[0], this.getPlugin().getSpawnerManager().getSpawners().stream().map(s -> s.getInternalName()).toList(), 2)
         : Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.spawners.info";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "info";
   }
}
