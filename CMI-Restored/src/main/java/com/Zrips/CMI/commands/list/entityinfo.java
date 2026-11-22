package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIVillagerProfession;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.Arrays;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Entities.CMIEntitySubType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;

public class entityinfo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("type", "{gcp}Entity Type: {gcs}[type]");
    configReader.get("customName", "{gcp}Custom name: {gcs}[name]");
    configReader.get("health", "{gcp}Health: {gcs}[current]{gcp}/{gcs}[max]");
    configReader.get("lastDamage", "{gcp}Last damage cause: {gcs}[cause]");
    configReader.get("tamed", "{gcp}Tamed: {gcs}[state]");
    configReader.get("id", "{gcp}ID: {gcs}[id]");
    configReader.get("uuid", "{gcs}[uuid]");
    configReader.get("owner", "{gcp}Owner: {gcs}[playerDisplayName]");
    configReader.get("grows", "{gcp}Grows up in: {gcs}[time]");
    configReader.get(
        "horse", "{gcp}Horse: jump {gcs}[jump] {gcp}blocks, speed {gcs}[speed] {gcp}blocks/sec");
    configReader.get(
        "villager",
        "{gcp}Profession: {gcs}[profession] {gcp}Lvl: {gcs}[lvl] {gcp}Type: {gcs}[type]");
    configReader.get("subTypes", "{gcp}Sub Types: {gcs}[list]");
  }

  @CAnnotation(
      info = "Check entity information",
      args = "(playername)",
      multiTab = "[playername]",
      regVar = {0, 1},
      consoleVar = 1,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = args[argIndex];
      if (string == null) {
        string = arg;
      }
    }

    Player player = cmi.getTarget(commandSender, string, this);
    if (player == null) {
      return null;
    }

    Entity entity = cmi.getUtilManager().getTargetEntity(player);
    if (entity == null) {
      LC.info_lookAtEntity.sendMessage(commandSender, new Object[0]);
      return null;
    }

    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM(this, "type", "[type]", entity.getType().name()));
    rawMessage.addInsertion(entity.getType().name());
    rawMessage.addSuggestion(entity.getUniqueId().toString());
    int entityId = 0;

    try {
      entityId = entity.getEntityId();
    } catch (Throwable throwable) {
    }

    rawMessage.addHover(
        Arrays.asList(
            CMILC.getIM(this, "id", "[id]", entityId),
            CMILC.getIM(this, "uuid", "[uuid]", entity.getUniqueId())));
    rawMessage.show(commandSender);
    if (entity instanceof Damageable) {
      Damageable damageable = (Damageable) entity;
      CMILC.info(
          this,
          commandSender,
          "health",
          "[current]",
          damageable.getHealth(),
          "[max]",
          damageable.getMaxHealth());
    }

    if (entity instanceof Tameable) {
      Tameable tameable = (Tameable) entity;
      CMILC.info(this, commandSender, "tamed", "[state]", tameable.isTamed());
      if (tameable.getOwner() != null) {
        try {
          UUID uuid = tameable.getOwner().getUniqueId();
          CMIUser cmiUser = cmi.getPlayerManager().getUser(uuid);
          if (cmiUser != null && cmiUser.getName() != null) {
            CMILC.info(this, commandSender, "owner", cmiUser);
          }
        } catch (Exception exception) {
          CMIUser ownerUser = cmi.getPlayerManager().getUser(tameable.getOwner().getName());
          if (ownerUser != null && ownerUser.getName() != null) {
            CMILC.info(this, commandSender, "owner", ownerUser);
          }
        }
      }
    }

    try {
      if (entity instanceof Horse) {
        Horse horse = (Horse) entity;
        double number = horse.getJumpStrength();
        double horseJumpHeight = 0.0;
        double horseSpeed = 0.0;
        if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)) {
          horseSpeed = (int) (CMIAttribute.MOVEMENT_SPEED.getValue(horse) * 43.17 * 100.0) / 100.0;
          horseJumpHeight = Math.pow(number, 1.7) * 5.293;
        } else {
          horseSpeed =
              (int) (CMIAttribute.MOVEMENT_SPEED.getValue(horse) * 42.157787584 * 100.0) / 100.0;
          horseJumpHeight =
              -0.1817584952 * number * number * number
                  + 3.689713992 * number * number
                  + 2.128599134 * number
                  - 0.343930367;
        }

        CMILC.info(
            this,
            commandSender,
            "horse",
            "[jump]",
            (int) (horseJumpHeight * 100.0) / 100.0,
            "[speed]",
            horseSpeed);
      }
    } catch (Throwable throwable2) {
    }

    if (entity instanceof Ageable) {
      Ageable ageable = (Ageable) entity;
      if (!ageable.isAdult()) {
        CMILC.info(
            this,
            commandSender,
            "grows",
            "[time]",
            CMITimeManager.to24hourShort(-(ageable.getAge() / 20 * 1000L)));
      }
    }

    try {
      if (entity instanceof Villager) {
        Villager villager = (Villager) entity;
        CMILC.info(
            this,
            commandSender,
            "villager",
            "[profession]",
            CMIText.firstToUpperCase(CMIVillagerProfession.getName(villager.getProfession())),
            "[lvl]",
            villager.getVillagerLevel(),
            "[type]",
            CMIText.firstToUpperCase(villager.getVillagerType().toString()));
      }
    } catch (Throwable throwable3) {
    }

    if (entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() != null) {
      CMILC.info(
          this,
          commandSender,
          "lastDamage",
          "[cause]",
          entity.getLastDamageCause().getCause().name().toLowerCase());
    }

    var list = CMIEntitySubType.getSubTypes(entity);
    if (list != null) {
      StringBuilder stringBuilder = new StringBuilder();

      for (CMIEntitySubType cmiEntitySubType : list) {
        if (!stringBuilder.toString().isEmpty()) {
          stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
        }

        stringBuilder.append(
            CMIText.firstToUpperCase(cmiEntitySubType.toString()).replace(" ", "_"));
      }

      if (!stringBuilder.toString().isEmpty()) {
        CMILC.info(this, commandSender, "subTypes", "[list]", stringBuilder.toString());
      }
    }

    return true;
  }
}
