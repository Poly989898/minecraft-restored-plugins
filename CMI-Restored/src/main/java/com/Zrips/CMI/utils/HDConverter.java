package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.commands.list.importfrom;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import net.Zrips.CMILib.Container.CMILocation;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class HDConverter {
  public static void convert(CommandSender commandSender) {
    File file =
        new File(
            "plugins" + File.separator + "HolographicDisplays" + File.separator + "database.yml");
    if (!file.exists()) {
      CMILC.info(importfrom.class, commandSender, "HDfile");
    } else {
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      int value = 0;

      for (String string : yamlConfiguration.getKeys(false)) {
        try {
          ConfigurationSection configurationSection =
              yamlConfiguration.getConfigurationSection(string);
          CMILocation cmiLocation = null;
          if (configurationSection.contains("location")) {
            String text = configurationSection.getString("location");
            String[] strings = text.replace(" ", "").split(",");
            World world = Bukkit.getWorld(strings[0]);
            if (world == null) {
              continue;
            }

            double number = Double.parseDouble(strings[1]);
            double amount = Double.parseDouble(strings[2]);
            double amountSecond = Double.parseDouble(strings[3]);
            cmiLocation = new CMILocation(world, number, amount, amountSecond);
          } else {
            if (!configurationSection.contains("position")) {
              continue;
            }

            World worldSecond = Bukkit.getWorld(configurationSection.getString("position.world"));
            if (worldSecond == null) {
              continue;
            }

            double amountThird = configurationSection.getDouble("position.x");
            double amountFourth = configurationSection.getDouble("position.y");
            double amountFifth = configurationSection.getDouble("position.z");
            cmiLocation = new CMILocation(worldSecond, amountThird, amountFourth, amountFifth);
          }

          CMIHologram cmiHologram = new CMIHologram(string, cmiLocation);
          cmiHologram.makePersistent();
          if (configurationSection.contains("lines")) {
            if (configurationSection.isList("lines")) {
              cmiHologram.setLines(configurationSection.getStringList("lines"));
            }

            if (configurationSection.isString("lines")) {
              cmiHologram.setLines(
                  new ArrayList<>(Arrays.asList(configurationSection.getString("lines"))));
            }

            var list = cmiHologram.getLines();

            for (int count = 0; count < list.size(); count++) {
              String textSecond = (String) list.get(count);
              textSecond = textSecond.replace("[x]", "█");
              textSecond = textSecond.replace("[/]", "▌");
              textSecond = textSecond.replace("[.]", "░");
              textSecond = textSecond.replace("[..]", "▒");
              textSecond = textSecond.replace("[...]", "▓");
              textSecond = textSecond.replace("[p]", "•");
              textSecond = textSecond.replace("[|]", "⎹");
              if (list.size() - 1 >= count) {
                list.set(count, textSecond);
              } else {
                list.add(textSecond);
              }
            }
          }

          if (CMI.getInstance().getHologramManager().getByName(string) != null) {
            CMILC.info(importfrom.class, commandSender, "HDexist", "[name]", string);
          } else {
            CMI.getInstance().getHologramManager().addHologram(cmiHologram);
            value++;
          }
        } catch (Exception exception) {
          CMILC.info(importfrom.class, commandSender, "HDfailed", "[name]", string);
        }
      }

      CMILC.info(importfrom.class, commandSender, "HDimported", "[amount]", value);
      CMI.getInstance().getHologramManager().save();
    }
  }
}
