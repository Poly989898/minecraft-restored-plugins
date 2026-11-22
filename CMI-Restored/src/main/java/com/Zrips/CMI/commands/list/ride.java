package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ride implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("riding", "{gcp}You are riding {gcs}[entityName]");
    configReader.get("cantRide", "{gcw}You cant ride ({gcs}[entityName]{gcq}) entity");
    configReader.get("someOneElse", "&cSome one is already riding this entity");
    configReader.get(
        "ridingYou",
        "{gcs}[playerDisplayName] {gcp}is riding you, do {gcs}/shakeitoff {gcp}to get them off");
    configReader.get("canRide", "{gcp}Can ride: {gcs}[list]");
  }

  @CAnnotation(
      priority = 47,
      info = "Ride target entity",
      args = "list",
      multiTab = "list",
      explanation = " cmi.command.ride.[entityType] - to have access in riding entity",
      regVar = {0, 1},
      consoleVar = 666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    Entity entity = cmi.getUtilManager().getTargetEntity(player, false, false);
    if (strings.length > 0 && strings[0].equalsIgnoreCase("list")) {
      int value = 0;
      StringBuilder stringBuilder = new StringBuilder();
      if (PermissionsManager.CMIPerm.command_ride_$1.hasPermission(commandSender, "*")) {
        stringBuilder.append(CMILC.info_Any.getLocale());
      } else {
        EntityType[] entityTypes;
        int entityTypeCount = (entityTypes = EntityType.values()).length;

        for (int index = 0; index < entityTypeCount; index++) {
          EntityType entityType = entityTypes[index];
          if (PermissionsManager.CMIPerm.command_ride_$1.hasPermission(
              commandSender, entityType.name())) {
            if (!stringBuilder.toString().isEmpty()) {
              stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
            } else {
              stringBuilder.append("&7");
            }

            stringBuilder.append(CMIEntityType.get(entityType).getTranslatedName());
            if (value % 2 == 0) {
              stringBuilder = stringBuilder.append("&f");
            } else {
              stringBuilder = stringBuilder.append("&7");
            }

            value++;
          }
        }
      }

      CMILC.info(this, commandSender, "canRide", "[list]", stringBuilder.toString());
      return true;
    } else {
      if (!(entity instanceof LivingEntity)) {
        LC.info_lookAtEntity.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (!PermissionsManager.CMIPerm.command_ride_$1.hasPermission(
              commandSender, entity.getType().name())
          && !PermissionsManager.CMIPerm.command_ride_$1.hasPermission(commandSender, "*")) {
        CMILC.info(
            this, commandSender, "cantRide", "[entityName]", entity.getType().name().toLowerCase());
        return null;
      }

      if (cmi.getTeleportHandler()
              .isEmpty(
                  CMIMaterial.get(entity.getLocation().clone().add(0.0, 2.0, 0.0).getBlock()), true)
          && cmi.getTeleportHandler()
              .isEmpty(
                  CMIMaterial.get(entity.getLocation().clone().add(0.0, 3.0, 0.0).getBlock()),
                  true)) {
        PlayerTeleportEvent playerTeleportEvent =
            new PlayerTeleportEvent(
                player, player.getLocation(), entity.getLocation(), TeleportCause.COMMAND);
        Bukkit.getServer().getPluginManager().callEvent(playerTeleportEvent);
        if (playerTeleportEvent.isCancelled()) {
          return false;
        }

        if (entity.getPassenger() instanceof Slime
            && (entity.hasMetadata("CMIFakeSlime")
                || entity.getPassenger().getCustomName() != null
                    && entity.getPassenger().getCustomName().equalsIgnoreCase("CMIFakeSlime"))) {
          Entity passenger = entity.getPassenger();
          entity.removePassenger(passenger);
          passenger.remove();
        }

        if (entity.getPassenger() != null) {
          CMILC.info(
              this,
              commandSender,
              "someOneElse",
              "[entityName]",
              entity.getType().name().toLowerCase());
          return null;
        }

        if (entity instanceof Player && Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
          CMIUser cmiUser = CMIUser.getUser((Player) entity);
          if (cmiUser == null || !cmiUser.getOptionState(PlayerOption.rideMe)) {
            CMILC.info(
                this,
                commandSender,
                "cantRide",
                "[entityName]",
                entity.getType().name().toLowerCase());
            return null;
          }

          try {
            Slime slime =
                (Slime)
                    entity
                        .getWorld()
                        .spawn(entity.getLocation().clone().add(0.0, -20.0, 0.0), Slime.class);
            slime.setAI(false);
            slime.setSize(1);
            slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1));
            if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
              slime.setInvisible(true);
            }

            slime.setCustomName("CMIFakeSlime");
            slime.setInvulnerable(true);
            slime.setRemoveWhenFarAway(true);

            try {
              slime.setMetadata("CMIFakeSlime", new FixedMetadataValue(cmi, true));
            } catch (Throwable throwable) {
            }

            slime.setNoDamageTicks(1728000);
            if (slime.isDead()) {
              if (Version.isCurrentLower(Version.v1_11_R1)) {
                entity.setPassenger(player);
              } else {
                entity.addPassenger(player);
              }
            } else if (Version.isCurrentLower(Version.v1_11_R1)) {
              entity.setPassenger(slime);
              slime.setPassenger(player);
            } else {
              entity.addPassenger(slime);
              slime.addPassenger(player);
              player.setSwimming(true);
            }
          } catch (Throwable throwable2) {
            throwable2.printStackTrace();
            if (Version.isCurrentLower(Version.v1_11_R1)) {
              entity.setPassenger(player);
            } else {
              entity.addPassenger(player);
            }
          }
        } else if (Version.isCurrentLower(Version.v1_11_R1)) {
          entity.setPassenger(player);
        } else {
          entity.addPassenger(player);
        }

        cmi.getAnimationManager().addRiding(player, entity);
        String string = entity.getCustomName();
        if (string == null) {
          string = entity.getName();
        }

        if (!(entity instanceof Player)) {
          string = string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
        }

        string = string.replace("_", " ");
        CMILC.info(this, commandSender, "riding", "[entityName]", string);
        if (entity instanceof Player) {
          Snd snd = new Snd();
          snd.setTarget(commandSender);
          snd.setSender(commandSender);
          snd.setSource((Player) entity);
          CMILC.info(this, (Player) entity, "ridingYou", snd);
        }

        return true;
      } else {
        LC.info_noSpace.sendMessage(commandSender, new Object[0]);
        return null;
      }
    }
  }
}
