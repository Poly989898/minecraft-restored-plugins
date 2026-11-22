package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.EssentialsConverter;
import com.Zrips.CMI.utils.HDConverter;
import com.Zrips.CMI.utils.importSettings;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;

public class importfrom implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("converted", "{gcp}Converted: {gcs}[done]{gcp}/{gcs}[total] {gcp}users");
    configReader.get("convertedWarps", "{gcp}Converted {gcs}[amount] {gcp}warps");
    configReader.get(
        "noInfo", "{gcd}You forgot to provide information what you want to import exactly");
    configReader.get(
        "savingInProgress",
        "{gcp}Data will be saved into data base in next couple minutes depending on how many"
            + " entries we need to save");
    configReader.get(
        "HDfile", "{gcd}Could not find HolographicDisplays plugin database file. Aborting.");
    configReader.get("HDexist", "{gcd}Hologram by {gcs}[name] {gcd}name already exists. Skipping.");
    configReader.get("HDfailed", "{gcd}Could not load hologram ({gcs}[name]{gcd})");
    configReader.get("HDimported", "{gcp}Imported {gcs}[amount] {gcp}holograms");
  }

  @CAnnotation(
      info = "Import data from other plugins",
      args = "[essentials/hd] [home/warp/nick/logoutlocation/money/mail]",
      tab = {
        "essentials%%hd",
        "home%%warp%%nick%%logoutlocation%%money%%mail%%",
        "home%%",
        "warp%%",
        "nick%%",
        "logoutlocation%%",
        "money%%",
        "mail%%"
      },
      explanation = {},
      regVar = {1, 2, 3, 4, 5, 6, 7},
      consoleVar = {1, 2, 3, 4, 5, 6, 7})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    importSettings importSettings = new importSettings();
    String[] args = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = args[argIndex];
      if (string.equalsIgnoreCase("home")) {
        importSettings.set(EssentialsConverter.importType.homes, true);
      }

      if (string.equalsIgnoreCase("warp")) {
        importSettings.set(EssentialsConverter.importType.warps, true);
      }

      if (string.equalsIgnoreCase("nick")) {
        importSettings.set(EssentialsConverter.importType.nickname, true);
      }

      if (string.equalsIgnoreCase("logoutlocation")) {
        importSettings.set(EssentialsConverter.importType.logoutlocation, true);
      }

      if (string.equalsIgnoreCase("money")) {
        importSettings.set(EssentialsConverter.importType.money, true);
      }

      if (string.equalsIgnoreCase("mail")) {
        importSettings.set(EssentialsConverter.importType.mail, true);
      }
    }

    String importSource;
    switch ((importSource = strings[0].toLowerCase()).hashCode()) {
      case 3324:
        if (importSource.equals("hd")) {
          HDConverter.convert(commandSender);
          return true;
        }
        break;
      case 1190983397:
        if (importSource.equals("essentials")) {
          if (importSettings.getMap().isEmpty()) {
            CMILC.info(this, commandSender, "noInfo");
            return true;
          }

          EssentialsConverter.convert(commandSender, importSettings);
          return true;
        }
    }

    return true;
  }
}
