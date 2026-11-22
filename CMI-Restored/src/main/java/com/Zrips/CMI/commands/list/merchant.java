package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIVillagerProfession;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Merchant;

public class merchant implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("incorrect", "{gcw}Incorrect profession.");
    configReader.get(
        "feedback",
        "{gcp}You have opened {gcs}[type] {gcp}trade for {gcs}[playerDisplayName]{gcp}.");
    configReader.get(
        "targetfeedback", "{gcs}[senderDisplayName]{gcp} opened {gcs}[type] {gcp}trade for you.");

    try {
      for (Profession profession : getVillagerProfessions()) {
        String string = CMIVillagerProfession.getName(profession);
        if (!string.equals("NONE")) {
          configReader.get("professions." + string.toLowerCase(), CMIText.firstToUpperCase(string));
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public static List<Profession> getVillagerProfessions() {
    ArrayList<org.bukkit.entity.Villager.Profession> arrayList = new ArrayList();

    try {
      var villagerProfessionClass = Class.forName("org.bukkit.entity.Villager$Profession");
      Method method = villagerProfessionClass.getMethod("values");
      Object[] objects = (Object[]) method.invoke(null);
      Object[] professionValues = objects;
      int value = objects.length;

      for (int index = 0; index < value; index++) {
        Object object = professionValues[index];
        arrayList.add((Profession) object);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return arrayList;
  }

  @CAnnotation(
      info = "Open merchant trade window",
      args = "[type] (playerName) (level)",
      tab = {"merchants", "playername", "1%%2%%3%%4%%5"},
      regVar = {1, 2, 3},
      consoleVar = {1, 2, 3},
      alias = true,
      others = true)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_9_R1)) {
      commandSender.sendMessage("Temporaly disabled. Works only on 1.9+ versions");
      return true;
    }

    Profession profession = null;
    String string = "";
    String playerName = null;
    int value = 0;
    String[] args = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = args[argIndex];
      if (profession == null) {
        for (Profession candidateProfession : getVillagerProfessions()) {
          if (candidateProfession != null) {
            String professionName = CMIVillagerProfession.getName(candidateProfession);
            if (professionName.equalsIgnoreCase(arg)) {
              profession = candidateProfession;
              string = professionName;
            }
          }
        }
      } else if (playerName == null && CMIUser.getOnlinePlayer(commandSender, arg, true) != null) {
        playerName = arg;
      } else if (value == 0) {
        try {
          value = Integer.parseInt(arg);
        } catch (Throwable throwable) {
        }
      }
    }

    value = Math.max(value, 1);
    value = Math.min(value, 5);
    Player player = cmi.getTarget(commandSender, playerName, this);
    if (player == null) {
      return null;
    }

    if (profession != null
        && (string.equalsIgnoreCase("NORMAL") || string.equalsIgnoreCase("HUSK"))) {
      profession = null;
    }

    if (profession == null) {
      CMILC.info(this, commandSender, "incorrect");
      return null;
    }

    Snd snd = new Snd().setSender(commandSender).setTarget(player);
    CMILC.info(this, commandSender, "feedback", snd, "[type]", string.toLowerCase());
    if (!player.getName().equalsIgnoreCase(commandSender.getName())) {
      CMILC.info(this, player, "targetfeedback", snd, "[type]", string.toLowerCase());
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      ArrayList arrayList = new ArrayList();
      Villager villager =
          (Villager)
              player
                  .getWorld()
                  .spawnEntity(player.getLocation().add(0.0, -10.0, 0.0), EntityType.VILLAGER);

      for (int level = 1; level <= value; level++) {
        villager.setProfession(Profession.NONE);
        villager.setProfession(profession);
        villager.setVillagerLevel(level);
        arrayList.addAll(villager.getRecipes());
      }

      villager.remove();
      String professionMessage = CMILC.getIM(merchant.class, "professions." + string.toLowerCase());
      Merchant merchant = Bukkit.createMerchant(professionMessage);
      merchant.setRecipes(arrayList);
      player.openMerchant(merchant, true);
    } else {
      Villager merchantVillager =
          (Villager)
              player
                  .getWorld()
                  .spawnEntity(player.getLocation().add(0.0, -10.0, 0.0), EntityType.VILLAGER);
      merchantVillager.setProfession(profession);
      player.openMerchant(merchantVillager, true);
      merchantVillager.remove();
    }

    return true;
  }
}
