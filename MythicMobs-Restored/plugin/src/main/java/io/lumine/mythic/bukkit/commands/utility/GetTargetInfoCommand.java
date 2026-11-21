package io.lumine.mythic.bukkit.commands.utility;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.chat.ColorString;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.stats.StatRegistry;
import io.lumine.mythic.core.skills.stats.StatType;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class GetTargetInfoCommand extends Command<MythicBukkit> {
   public GetTargetInfoCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Player p = (Player)sender;
      LivingEntity l = MythicUtil.getTargetedEntity(p);
      if (l == null) {
         CommandHelper.sendError(sender, "You must target a valid entity!");
         return true;
      } else {
         AbstractEntity aEntity = BukkitAdapter.adapt(l);
         CommandHelper.sendHeader(sender);
         Text.sendMessage(sender, "<gold><bold>Information about Targeted Mob:</bold> <gray>");
         Text.sendMessage(sender, "<gray>- <gold>EntityID: <white>" + l.getEntityId());
         Text.sendMessage(sender, "<gray>- <gold>UUID: <white>" + l.getUniqueId());
         if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
            Text.sendMessage(sender, "<gray>- <gold>Is MythicMob: <white>TRUE");
         } else {
            Text.sendMessage(sender, "<gray>- <gold>Is MythicMob: <white>FALSE");
         }

         if (this.getPlugin().getMobManager().isIgnoredEntity(l.getUniqueId())) {
            Text.sendMessage(sender, "<gray>- <gold>in Void List: <white>TRUE");
         } else {
            Text.sendMessage(sender, "<gray>- <gold>in Void List: <white>FALSE");
         }

         if (this.getPlugin().getMobManager().isActiveMob(l.getUniqueId())) {
            ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(l);
            am.remountSpawner();
            Text.sendMessage(sender, "<gray>- <yellow>EntityType<gray>: <white>" + am.getType().getEntityType());
            Text.sendMessage(sender, "<gray>- <yellow>MythicType<gray>: <white>" + am.getType().getInternalName());
            Text.sendMessage(sender, "<gray>- <yellow>Location<gray>: <white>" + am.getLocation().toString());
            Text.sendMessage(sender, "<gray>- <yellow>Level<gray>: <white>" + am.getLevel());
            Text.sendMessage(sender, "<gray>- <yellow>Health<gray>: <white>" + am.getEntity().getHealth());
            Text.sendMessage(
               sender,
               "<gray>- <yellow>TypeHealth<gray>: <white>"
                  + am.getType().getHealth(am)
                  + " <gray>(+<white>"
                  + am.getType().getPerLevelHealth()
                  + " <gray>per level)"
            );
            Text.sendMessage(sender, "<gray>- <yellow>Damage<gray>: <white>" + am.getEntity().getDamage());
            if (am.getType().getDamage() != null) {
               Text.sendMessage(sender, "<gray>- <yellow>TypeDamage<gray>: <white>" + am.getType().getDamage().toString());
            }

            Text.sendMessage(
               sender, "<gray>- <yellow>Power<gray>: <white>" + am.getPower() + "<gray>(<yellow>" + am.getType().getPerLevelPower() + " <gray>per level"
            );
            Text.sendMessage(
               sender,
               "<gray>- <yellow>Despawn<gray>: <white>"
                  + am.getType().getDespawnMode().toString()
                  + "<gray> (p: <aqua>"
                  + am.getEntity().getBukkitEntity().isPersistent()
                  + "<gray> d: <aqua>"
                  + ((LivingEntity)am.getEntity().getBukkitEntity()).getRemoveWhenFarAway()
                  + "<gray>)"
            );
            Text.sendMessage(sender, "<gray>- <yellow>Stance<gray>: <white>" + am.getStance());
            if (am.getSpawner() != null) {
               Text.sendMessage(sender, "<gray>- <yellow>Spawner<gray>: <white>" + am.getSpawner().getInternalName());
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

            Text.sendMessage(sender, "<gray>- <yellow>Has AI<gray>: <white>" + (l.hasAI() ? "TRUE" : "FALSE"));
            Text.sendMessage(sender, "<gray>- <yellow>Gravity<gray>: <white>" + (l.hasGravity() ? "TRUE" : "FALSE"));
            Text.sendMessage(sender, "<gray>- <yellow>Leashed<gray>: <white>" + (l.isLeashed() ? "TRUE" : "FALSE"));
            if (!am.getStatRegistry().isEmpty()) {
               Text.sendMessage(sender, "<red><strikethrough><bold>----</bold><strikethrough></red> <white>Stats");
               StatRegistry statRegistry = am.getStatRegistry();

               for (StatType stat : statRegistry.getApplicableStats()) {
                  statRegistry.getStatData(stat)
                     .ifPresent(
                        statMap -> Text.sendMessage(
                           sender,
                           "<green>* <gold>" + stat.getKey() + " <gray>- <yellow>" + statMap.get() + " <gray>(base <aqua>" + statMap.getBaseValue() + "<gray>)"
                        )
                     );
               }
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
            sender.sendMessage(ColorString.get("&6Health&7: &f" + l.getHealth()));
            sender.sendMessage(ColorString.get("&6MaxHealth&7: &f" + l.getMaxHealth()));
            sender.sendMessage(ColorString.get("&6Damage Base&7: &f" + aEntity.getDamageBase()));
            sender.sendMessage(ColorString.get("&6Damage&7: &f" + aEntity.getDamage()));
            if (l instanceof Player targetPlayer) {
               Optional<String> maybeFaction = this.getPlugin().getPlayerManager().getFactionProvider().getFaction(targetPlayer.getUniqueId());
               sender.sendMessage(ColorString.get("&6Food&7: &f" + targetPlayer.getFoodLevel()));
               sender.sendMessage(ColorString.get("&6Saturation&7: &f" + targetPlayer.getSaturation()));
               sender.sendMessage(ColorString.get("&6Faction&7: &f" + maybeFaction.orElse("[Not Set]")));
               sender.sendMessage(ColorString.get("&6Is Flying&7: &f" + (targetPlayer.isFlying() ? "TRUE" : "FALSE")));
               sender.sendMessage(ColorString.get("&6Is Gliding&7: &f" + (targetPlayer.isGliding() ? "TRUE" : "FALSE")));
            }

            sender.sendMessage(ColorString.get("&6Has AI&7: &f" + (l.hasAI() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Has Gravity&7: &f" + (l.hasGravity() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Is Leashed&7: &f" + (l.isLeashed() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Marked as Dead&7: &f" + (l.isDead() ? "TRUE" : "FALSE")));
            sender.sendMessage(ColorString.get("&6Marked as Valid&7: &f" + (l.isValid() ? "TRUE" : "FALSE")));
         }

         CommandHelper.sendFooter(sender);
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
      return false;
   }

   @Override
   public String getName() {
      return "gettargetinfo";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"gti"};
   }
}
