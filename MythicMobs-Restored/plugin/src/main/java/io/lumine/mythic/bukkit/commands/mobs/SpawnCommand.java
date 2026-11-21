package io.lumine.mythic.bukkit.commands.mobs;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntityType;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import io.lumine.mythic.bukkit.utils.CollectionUtils;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.adapters.VirtualEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.WorldScaling;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.SkillTriggers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnCommand extends Command<MythicBukkit> {
   public SpawnCommand(Command<MythicBukkit> parent) {
      super(parent);
   }

   public SpawnCommand(MythicBukkit plugin) {
      super(plugin);
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      Location loc = null;
      int amount = 1;
      boolean optAtTarget = false;
      boolean optAtPlayer = false;
      boolean optSilent = false;
      boolean optNatural = false;
      if (args.length == 0) {
         CommandHelper.sendError(sender, "You must specify a mob to spawn.");
         return true;
      } else {
         if (args != null && args.length > 1 && args[0].startsWith("-")) {
            if (args[0].contains("s")) {
               optSilent = true;
            }

            if (args[0].contains("t")) {
               optAtTarget = true;
            }

            if (args[0].contains("p")) {
               optAtPlayer = true;
            }

            if (args[0].contains("n")) {
               optNatural = true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);
         }

         if (optAtPlayer) {
            String name = args[0];
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
               if (!optSilent) {
                  CommandHelper.sendError(sender, "Player " + name + " not found.");
               }

               return true;
            }

            if (optAtTarget) {
               loc = player.getTargetBlock(MythicBukkit.inst().getConfiguration().getTransparentBlocks(), 200).getRelative(BlockFace.UP).getLocation();
            } else {
               loc = player.getLocation();
            }

            args = Arrays.copyOfRange(args, 1, args.length);
         }

         double level = 1.0;
         if (args.length > 1) {
            try {
               amount = Integer.parseInt(args[1]);
            } catch (Exception e) {
               amount = 1;
            }
         }

         if (args.length > 2) {
            String input = args[2];
            if (input.startsWith("@")) {
               try {
                  SkillTargeter targeter = this.getPlugin().getSkillManager().getTargeter(input);
                  if (targeter == null) {
                     CommandHelper.sendError(sender, "Invalid location targeter specified for spawning a mob.");
                     return true;
                  }

                  SkillCaster caster;
                  if (sender instanceof Player playerx) {
                     caster = this.getPlugin().getSkillManager().getCaster(BukkitAdapter.adapt(playerx));
                  } else {
                     caster = this.getPlugin().getSkillManager().getCaster(new VirtualEntity());
                  }

                  SkillMetadata metadata = this.getPlugin()
                     .getSkillManager()
                     .getEventBus()
                     .buildSkillMetadata(SkillTriggers.API, caster, caster.getEntity(), caster.getEntity().getLocation(), true);
                  loc = BukkitAdapter.adapt(this.getPlugin().getSkillManager().getLocationTarget(targeter, metadata));
               } catch (Exception ex) {
                  CommandHelper.sendError(sender, "Invalid location targeter specified for spawning a mob.");
                  return true;
               }
            } else {
               try {
                  String[] part = args[2].split(",");
                  World w = Bukkit.getWorld(part[0]);
                  float x = Float.parseFloat(part[1]);
                  float y = Float.parseFloat(part[2]);
                  float z = Float.parseFloat(part[3]);
                  float pitch = 0.0F;
                  float yaw = 0.0F;
                  if (part.length > 4) {
                     yaw = Float.parseFloat(part[4]);
                  }

                  if (part.length > 5) {
                     pitch = Float.parseFloat(part[5]);
                  }

                  if (w != null) {
                     loc = new Location(w, x, y, z, yaw, pitch);
                  }
               } catch (Exception ex2) {
                  CommandHelper.sendError(sender, "Invalid location specified for spawning a mob: location must be in format world,x,y,z,yaw,pitch");
                  return true;
               }
            }
         } else if (!optAtPlayer && sender instanceof Player) {
            if (optAtTarget) {
               loc = ((Player)sender).getTargetBlock(this.getPlugin().getConfiguration().getTransparentBlocks(), 200).getRelative(BlockFace.UP).getLocation();
            } else {
               loc = ((Player)sender).getLocation();
            }
         }

         if (loc == null) {
            if (!optSilent) {
               CommandHelper.sendError(sender, "Invalid location specified for spawning a mob: world does not exist.");
            }

            return true;
         } else {
            AbstractLocation aLocation = BukkitAdapter.adapt(loc);
            String mob;
            if (args[0].contains(":")) {
               String[] split = args[0].split(":");
               mob = split[0];

               try {
                  level = Double.parseDouble(split[1]);
               } catch (Error | Exception ex3) {
                  if (!optSilent) {
                     CommandHelper.sendError(sender, "Invalid mob level supplied: must be a number.");
                  }

                  return true;
               }
            } else {
               mob = args[0];
               level = this.getPlugin()
                  .getMobManager()
                  .getMythicMob(mob)
                  .filter(MythicMob::isLevelConfigured)
                  .map(mmx -> (double)mmx.getDefaultLevel().get())
                  .orElseGet(() -> WorldScaling.getLevelBonus(aLocation));
            }

            if (this.getPlugin().getMobManager().getMythicMob(mob).isPresent()) {
               ActiveMob spawnedMob = null;

               for (int i = 0; i < amount; i++) {
                  if (optNatural) {
                     spawnedMob = this.getPlugin().getMobManager().spawnMob(mob, aLocation, SpawnReason.NATURAL, level);
                  } else {
                     spawnedMob = this.getPlugin().getMobManager().spawnMob(mob, aLocation, SpawnReason.COMMAND, level);
                  }
               }

               if (spawnedMob != null) {
                  if (!optSilent) {
                     CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
                  }
               } else if (!optSilent) {
                  CommandHelper.sendError(
                     sender, "Failed to spawn mob. See console for more details. If no error then you are in a mob spawning denied region."
                  );
               }
            } else if (MythicEntityType.get(mob) != null) {
               for (int ix = 0; ix < amount; ix++) {
                  Entity entity;
                  if (optNatural) {
                     entity = BukkitEntityType.getMythicEntity(mob).spawn(loc, SpawnReason.NATURAL);
                  } else {
                     entity = BukkitEntityType.getMythicEntity(mob).spawn(loc, SpawnReason.COMMAND);
                  }

                  MythicEntityType mythicType = this.getPlugin().getBootstrap().getMythicEntityType(entity);
                  Optional<MythicMob> maybeMob = this.getPlugin().getMobManager().getVanillaType(mythicType);
                  if (maybeMob.isPresent()) {
                     MythicMob mm = maybeMob.get();
                     ActiveMob am = this.getPlugin().getMobManager().registerActiveMob(BukkitAdapter.adapt(entity), mm, level);
                     mm.getMythicEntity().applySpawnOptions(entity);
                     mm.applyMobOptions(am, am.getLevel());
                     mm.applyMobVolatileOptions(am);
                     mm.applySpawnModifiers(am);
                  }
               }

               if (!optSilent) {
                  CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
               }
            } else {
               try {
                  EntityType type = EntityType.valueOf(mob);

                  for (int ix = 0; ix < amount; ix++) {
                     loc.getWorld().spawnEntity(loc, type);
                  }

                  if (!optSilent) {
                     CommandHelper.sendSuccess(sender, "Spawned " + amount + "x " + mob + "&a!");
                  }

                  return true;
               } catch (Exception ex4) {
                  if (!optSilent) {
                     CommandHelper.sendError(sender, "No mob type found with the name " + mob + ".");
                  }
               }
            }

            return true;
         }
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      if (args != null && args.length > 1 && args[0].startsWith("-")) {
         args = Arrays.copyOfRange(args, 1, args.length);
      }

      return args.length == 1
         ? CollectionUtils.filterMatches(
            args[0], this.getPlugin().getMobManager().getMobTypes().stream().filter(mobType -> !mobType.isHidden()).map(MythicMob::getInternalName).toList(), 2
         )
         : null;
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.mobs.spawn";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "spawn";
   }

   @Override
   public String[] getAliases() {
      return new String[]{"s"};
   }
}
