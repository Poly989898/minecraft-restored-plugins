package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ActiveInfoCommand extends Command<MythicBukkit> {
   public ActiveInfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Entity l = Bukkit.getEntity(UUID.fromString(args[0]));
      if (l == null) {
         CommandHelper.sendError(sender, "You must target a valid entity!");
         return true;
      } else {
         sender.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "Information About Entity: " + l.getUniqueId());
         sender.sendMessage(ChatColor.GOLD + "EntityID: " + ChatColor.GRAY + l.getEntityId());
         sender.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.GRAY + l.getUniqueId());
         if (MythicBukkit.inst().getMobManager().isActiveMob(l.getUniqueId())) {
            sender.sendMessage(ChatColor.GOLD + "Is Active MythicMob: " + ChatColor.GRAY + "TRUE");
         } else {
            sender.sendMessage(ChatColor.GOLD + "Is Active MythicMob: " + ChatColor.GRAY + "FALSE");
         }

         if (MythicBukkit.inst().getMobManager().isIgnoredEntity(l.getUniqueId())) {
            sender.sendMessage(ChatColor.GOLD + "Is In Void List: " + ChatColor.GRAY + "TRUE");
         } else {
            sender.sendMessage(ChatColor.GOLD + "Is In Void List: " + ChatColor.GRAY + "FALSE");
         }

         if (MythicBukkit.inst().getMobManager().isActiveMob(l.getUniqueId())) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(l);
            am.remountSpawner();
            sender.sendMessage(ChatColor.GOLD + "EntityType: " + ChatColor.GRAY + am.getType().getEntityType());
            sender.sendMessage(ChatColor.GOLD + "MythicType: " + ChatColor.GRAY + am.getType().getInternalName());
            sender.sendMessage(ChatColor.GOLD + "Location: " + ChatColor.GRAY + am.getLocation().toString());
            sender.sendMessage(ChatColor.GOLD + "Level: " + ChatColor.GRAY + am.getLevel());
            sender.sendMessage(ChatColor.GOLD + "TypeHealth: " + ChatColor.GRAY + am.getType().getHealth(am));
            sender.sendMessage(ChatColor.GOLD + "+LevelHealth: " + ChatColor.GRAY + am.getType().getPerLevelHealth());
            sender.sendMessage(ChatColor.GOLD + "TargetHealth: " + ChatColor.GRAY + am.getEntity().getHealth());
            if (am.getType().getDamage() != null) {
               sender.sendMessage(ChatColor.GOLD + "TypeDamage: " + ChatColor.GRAY + am.getType().getDamage().toString());
            }

            sender.sendMessage(ChatColor.GOLD + "TargetDamage: " + ChatColor.GRAY + am.getDamage());
            sender.sendMessage(ChatColor.GOLD + "+LevelPower: " + ChatColor.GRAY + am.getType().getPerLevelPower());
            sender.sendMessage(ChatColor.GOLD + "TargetPower: " + ChatColor.GRAY + am.getPower());
            sender.sendMessage(ChatColor.GOLD + "Current Stance: " + ChatColor.GRAY + am.getStance());
            if (am.getSpawner() != null) {
               sender.sendMessage(ChatColor.GOLD + "FromSpawner: " + ChatColor.AQUA + am.getSpawner().getInternalName());
            }

            if (am.getOwnerUUID().isPresent()) {
               Player player = Bukkit.getPlayer(am.getOwnerUUID().get());
               if (player == null) {
                  sender.sendMessage(ColorString.get("&6Owner: &f" + am.getOwnerUUID().get().toString()));
               } else {
                  sender.sendMessage(ColorString.get("&6Owner: &f" + player.getName()));
               }
            }

            if (am.getParentUUID().isPresent()) {
               if (am.getParent().isPresent()) {
                  AbstractEntity ent = am.getParent().get();
                  if (ent.isPlayer()) {
                     sender.sendMessage(ColorString.get("&6Parent: &f" + ((Player)ent.getBukkitEntity()).getName()));
                  } else {
                     sender.sendMessage(ColorString.get("&6Parent: &a" + ent.getUniqueId()));
                  }
               } else {
                  sender.sendMessage(ColorString.get("&6Parent: &e" + am.getParentUUID().get()));
               }
            }

            if (am.getFaction() != null) {
               sender.sendMessage(ColorString.get("&6Faction&7: " + am.getFaction()));
            }

            sender.sendMessage(ColorString.get("&6Has Gravity&7: &f" + (l.hasGravity() ? "TRUE" : "FALSE")));
            if (l instanceof LivingEntity) {
               sender.sendMessage(ColorString.get("&6Has AI&7: &f" + (((LivingEntity)l).hasAI() ? "TRUE" : "FALSE")));
               sender.sendMessage(ColorString.get("&6Is Leashed&7: &f" + (((LivingEntity)l).isLeashed() ? "TRUE" : "FALSE")));
            }

            if (am.isDead()) {
               sender.sendMessage(ChatColor.GOLD + "Marked as Dead: " + ChatColor.GRAY + "TRUE");
            } else {
               sender.sendMessage(ChatColor.GOLD + "Marked as Dead: " + ChatColor.GRAY + "FALSE");
            }

            if (am.getEntity().isValid()) {
               sender.sendMessage(ChatColor.GOLD + "Marked as Valid: " + ChatColor.GRAY + "TRUE");
            } else {
               sender.sendMessage(ChatColor.GOLD + "Marked as Valid: " + ChatColor.GRAY + "FALSE");
            }

            if (am.hasThreatTable()) {
               sender.sendMessage(ChatColor.GOLD + "Using ThreatTable: " + ChatColor.GRAY + "TRUE");
               if (am.getThreatTable().inCombat()) {
                  sender.sendMessage(
                     ChatColor.GOLD
                        + "Top Threat Target: "
                        + ChatColor.GRAY
                        + am.getThreatTable().getTopThreatHolder().getName()
                        + " ("
                        + am.getThreatTable().getTopTargetThreat()
                        + ")"
                  );
                  sender.sendMessage(ChatColor.GOLD + "Total Threat: " + ChatColor.GRAY + am.getThreatTable().getTotalThreat());
                  sender.sendMessage(ChatColor.GOLD + "-- Threat Targets: ");

                  for (AbstractEntity le : am.getThreatTable().getAllThreatTargets()) {
                     sender.sendMessage(ChatColor.GOLD + "* " + le.getName() + " (" + am.getThreatTable().getThreat(le) + ")");
                  }
               } else {
                  sender.sendMessage(ChatColor.GOLD + "-- Mob Not In Combat");
               }
            } else {
               sender.sendMessage(ChatColor.GOLD + "Using ThreatTable: " + ChatColor.GRAY + "FALSE");
            }
         } else {
            sender.sendMessage(ColorString.get("&6EntityType&7: &f" + l.getType().toString()));
            sender.sendMessage(ColorString.get("&6Location&7: &f" + l.getLocation().toString()));
            if (l instanceof LivingEntity) {
               sender.sendMessage(ColorString.get("&6Health&7: &f" + ((LivingEntity)l).getHealth()));
               sender.sendMessage(ColorString.get("&6MaxHealth&7: &f" + ((LivingEntity)l).getMaxHealth()));
            }

            if (l instanceof Player lp) {
               sender.sendMessage(ColorString.get("&6Food&7: &f" + lp.getFoodLevel()));
               sender.sendMessage(ColorString.get("&6Saturation&7: &f" + lp.getSaturation()));
               sender.sendMessage(ColorString.get("&6Is Flying&7: &f" + (lp.isFlying() ? "TRUE" : "FALSE")));
               sender.sendMessage(ColorString.get("&6Is Gliding&7: &f" + (lp.isGliding() ? "TRUE" : "FALSE")));
            }

            if (l instanceof LivingEntity) {
               sender.sendMessage(ColorString.get("&6Has AI&7: &f" + (((LivingEntity)l).hasAI() ? "TRUE" : "FALSE")));
               sender.sendMessage(ColorString.get("&6Is Leashed&7: &f" + (((LivingEntity)l).isLeashed() ? "TRUE" : "FALSE")));
            }

            sender.sendMessage(ColorString.get("&6Has Gravity&7: &f" + (l.hasGravity() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Marked as Dead&7: &f" + (l.isDead() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Marked as Valid&7: &f" + (l.isValid() ? "TRUE" : "FALSE")));
         }

         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.utilities.gettargetinfo";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "activeinfo";
   }
}
