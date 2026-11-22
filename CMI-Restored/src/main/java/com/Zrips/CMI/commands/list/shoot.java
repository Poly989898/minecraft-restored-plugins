/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Entities.CMIEntityType
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.Bee
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Damageable
 *  org.bukkit.entity.Dolphin
 *  org.bukkit.entity.Egg
 *  org.bukkit.entity.EnderPearl
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.ExperienceOrb
 *  org.bukkit.entity.LargeFireball
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Monster
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.ShulkerBullet
 *  org.bukkit.entity.SmallFireball
 *  org.bukkit.entity.Snowball
 *  org.bukkit.entity.SpectralArrow
 *  org.bukkit.entity.ThrownExpBottle
 *  org.bukkit.entity.TippedArrow
 *  org.bukkit.entity.WitherSkull
 *  org.bukkit.metadata.FixedMetadataValue
 *  org.bukkit.metadata.MetadataValue
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.projectiles.ProjectileSource
 *  org.bukkit.util.Vector
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public class shoot implements Cmd {
  public static final String shootident = "CMIShoot";

  @Override
  public void getExtra(ConfigReader configReader) {}

  @Override
  @CAnnotation(
      priority = 120,
      info = "Shoot projectile",
      args = "(playerName) (-t:targetPlayer) (type) (speed)",
      tab = {
        "playername%%projectiletype%%cleanEntityType",
        "-t:%%projectiletype%%cleanEntityType",
        "projectiletype%%cleanEntityType"
      },
      explanation = {},
      regVar = {0, 1, 2, 3, 4},
      consoleVar = {1, 2, 3, 4},
      others = true)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] location) {
    String string;
    Object object;
    Class clazz = null;
    Double d = 2.0;
    Object firstArg = null;
    String targetName = null;
    String mobOption = null;
    boolean bl = false;
    String[] args = location;
    int n = args.length;
    int argIndex = 0;
    while (argIndex < n) {
      object = args[argIndex];
      switch (((String) object).toLowerCase()) {
        case "smallfireball":
          {
            clazz = SmallFireball.class;
            break;
          }
        case "largefirreball":
        case "largefireball":
          {
            clazz = LargeFireball.class;
            break;
          }
        case "thrownexpbottle":
          {
            clazz = ThrownExpBottle.class;
            break;
          }
        case "snowball":
          {
            clazz = Snowball.class;
            break;
          }
        case "egg":
          {
            clazz = Egg.class;
            break;
          }
        case "enderpearl":
          {
            clazz = EnderPearl.class;
            break;
          }
        case "witherskull":
          {
            clazz = WitherSkull.class;
            break;
          }
        case "arrow":
          {
            clazz = Arrow.class;
            break;
          }
        case "tippedarrow":
          {
            clazz = TippedArrow.class;
            break;
          }
        case "spectralarrow":
          {
            clazz = SpectralArrow.class;
            break;
          }
        case "shulkerbullet":
        case "bullet":
        case "shulker":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.ShulkerBullet");
              bl = true;
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "trident":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.Trident");
              bl = true;
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "firework":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.Firework");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "splashpotion":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.SplashPotion");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "dragonfireball":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.DragonFireball");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "breezewindcharge":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.BreezeWindCharge");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "windcharge":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.WindCharge");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        case "spit":
        case "llama":
        case "llamaspit":
          {
            try {
              clazz = Class.forName("org.bukkit.entity.LlamaSpit");
            } catch (ClassNotFoundException classNotFoundException) {
              classNotFoundException.printStackTrace();
            }
            break;
          }
        default:
          {
            try {
              if (clazz == null) {
                if (((String) object).contains(":")) {
                  mobOption = ((String) object).split(":", 2)[1];
                  object = ((String) object).split(":")[0];
                }
                if (((String) object).contains(";")) {
                  mobOption = ((String) object).split(";", 2)[1];
                  object = ((String) object).split(";")[0];
                }
                CMIEntityType cmiEntityType = CMIEntityType.get((String) object);
                if (cmiEntityType != null) {
                  clazz = cmiEntityType.getType().getEntityClass();
                }
                if (clazz != null) {
                  break;
                }
              }
            } catch (Throwable throwable) {
              // empty catch block
            }
            try {
              d = Double.parseDouble((String) object);
              break;
            } catch (Exception exception) {
              if (firstArg == null && !((String) object).startsWith("-t:")) {
                firstArg = object;
                break;
              }
              if (!((String) object).startsWith("-t:")) break;
              targetName = ((String) object).substring("-t:".length());
            }
          }
      }
      ++argIndex;
    }
    if (clazz == null) {
      return false;
    }
    if (d < 0.0) {
      d = 0.0;
    }
    if (d > 5.0) {
      d = 5.0;
    }
    if ((object = cMI.getUser(commandSender, (String) firstArg, this)) == null) {
      return null;
    }
    if (!((CMIUser) object).isOnline()) {
      return false;
    }
    if (!PermissionsManager.CMIPerm.command_shoot_$1.hasPermission(
        commandSender, true, clazz.getSimpleName())) {
      return false;
    }
    Player player = null;
    if (targetName != null) {
      player = CMIUser.getOnlinePlayer(targetName, true);
    }
    Player shooter = ((CMIUser) object).getPlayer();
    Location eyeLocation = shooter.getEyeLocation().clone();
    Vector direction = shooter.getLocation().getDirection();
    if (player != null && shooter.getWorld().equals(player.getWorld())) {
      direction = cMI.getUtilManager().getVector(eyeLocation, player.getLocation());
    }
    direction = direction.normalize().multiply(d.doubleValue());
    Entity entity = null;
    try {
      entity =
          ((CMIUser) object)
              .getWorld()
              .spawn(eyeLocation.add(direction.getX(), direction.getY(), direction.getZ()), clazz);
    } catch (Throwable throwable) {
      return false;
    }
    if (entity == null) {
      return false;
    }
    if (!(entity instanceof Projectile)) {
      spawnmob.proccessMob(commandSender, entity, mobOption);
      if (entity instanceof Damageable) {
        ((Damageable) entity).setFallDistance(-150.0f);
      }
      if (player != null) {
        if (entity instanceof Monster) {
          ((Monster) entity).setTarget((LivingEntity) player);
        } else if (entity instanceof Creature) {
          ((Creature) entity).setTarget((LivingEntity) player);
        }
        if (Version.isCurrentEqualOrHigher((Version) Version.v1_15_R1) && entity instanceof Bee) {
          ((Bee) entity).setAnger(1200);
          ((Bee) entity).setTarget((LivingEntity) player);
        }
        if (Version.isCurrentEqualOrHigher((Version) Version.v1_14_R1)
            && entity instanceof Dolphin) {
          ((Dolphin) entity).setTarget((LivingEntity) player);
        }
      }
      entity.setVelocity(direction);
      return true;
    }
    Projectile projectile = (Projectile) entity;
    if (bl) {
      try {
        ShulkerBullet shulkerBullet = (ShulkerBullet) projectile;
        if (player == null) {
          Block block = cMI.getUtilManager().getTargetBlock(shooter, 300);
          ExperienceOrb experienceOrb =
              (ExperienceOrb)
                  ((CMIUser) object)
                      .getWorld()
                      .spawn(block.getLocation().clone(), ExperienceOrb.class);
          shulkerBullet.setTarget((Entity) experienceOrb);
          experienceOrb.remove();
        } else if (shooter.getWorld().equals(player.getWorld())) {
          shulkerBullet.setTarget((Entity) player);
        }
      } catch (Exception exception) {
        // empty catch block
      }
    }
    projectile.setVelocity(direction);
    projectile.setShooter((ProjectileSource) shooter);
    entity.setMetadata(
        shootident,
        (MetadataValue) new FixedMetadataValue((Plugin) CMI.getInstance(), (Object) true));
    return true;
  }
}
