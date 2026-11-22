package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.Util;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class iteminfo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("nameMat", " {gcp}Name: {gcs}[name] {gcp}Material: {gcs}[material]");
    configReader.get("mojangHover", "{gcp}Mojang Name: {gcs}[name]");
    configReader.get("display", " {gcp}Display Name: {gcs}[display]");
    configReader.get("lore", " {gcp}Lore:");
    configReader.get("loreLine", "   {gcs}[loreLine]");
    configReader.get("enchants", " {gcp}Enchants: {gcs}[enchants]");
    configReader.get("entityType", " {gcp}Entity Type: {gcs}[type]");
    configReader.get("customModel", " {gcp}Custom Model Data: {gcs}[data]");
    configReader.get("durability", " {gcp}Durability: {gcs}[current]{gcp}/{gcs}[max]");
    configReader.get("unbreakable", " {gcs}(Unbreakable)");
    configReader.get("stackSize", " {gcp}Max stack size: {gcs}[size]");
  }

  @CAnnotation(
      priority = 47,
      info = "Show item information",
      args = "(playerName)",
      tab = "playername",
      explanation = {},
      regVar = {0, 1, 2},
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

    ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
    CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
    CMIMaterial cmiMaterial = cmiItemStack.getCMIType();
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(
        CMILC.getIM(
            this,
            "nameMat",
            "[name]",
            cmiItemStack.getRealName(),
            "[material]",
            cmiMaterial.getBukkitName()));
    rawMessage.addSuggestion(cmiMaterial.getBukkitName());
    rawMessage.addHover(CMILC.getIM(this, "mojangHover", "[name]", cmiMaterial.getMojangName()));
    rawMessage.show(commandSender);
    if (itemStack.hasItemMeta()) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta.hasDisplayName()) {
        new RawMessage()
            .addText(CMILC.getIM(this, "display", "[display]", itemMeta.getDisplayName()))
            .addHover(itemMeta.getDisplayName())
            .addSuggestion(itemMeta.getDisplayName())
            .show(commandSender);
      }

      if (itemMeta.hasLore()) {
        rawMessage = new RawMessage().addText(CMILC.getIM(this, "lore"));

        for (String loreLine : itemMeta.getLore()) {
          rawMessage
              .addText("\n")
              .addText(CMILC.getIM(this, "loreLine", "[loreLine]", loreLine))
              .addHover(loreLine)
              .addSuggestion(loreLine);
        }

        rawMessage.show(commandSender);
      }

      StringBuilder stringBuilder = new StringBuilder();
      if (itemMeta instanceof EnchantmentStorageMeta) {
        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemMeta;

        for (var entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          stringBuilder.append(
              CMIEnchantment.getTranslatedName((Enchantment) entry.getKey())
                  + " -> "
                  + entry.getValue());
        }
      }

      if (!stringBuilder.toString().isEmpty()) {
        CMILC.info(this, commandSender, "enchants", "[enchants]", stringBuilder);
      }

      try {
        if (cmiMaterial.isPotion() || cmiMaterial.equals(CMIMaterial.TIPPED_ARROW)) {
          PotionMeta potionMeta = (PotionMeta) itemMeta;

          try {
            if (potionMeta.getBasePotionType() != null) {
              PotionType potionType = potionMeta.getBasePotionType();
              stringBuilder = new StringBuilder();
            }
          } catch (Throwable throwable) {
          }

          if (!stringBuilder.toString().isEmpty()) {
            CMILC.info(this, commandSender, "enchants", "[enchants]", stringBuilder);
          }
        }
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    }

    if (!itemStack.getEnchantments().isEmpty()) {
      StringBuilder enchantBuilder = new StringBuilder();

      for (var enchantEntry : itemStack.getEnchantments().entrySet()) {
        if (!enchantBuilder.toString().isEmpty()) {
          enchantBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
        }

        enchantBuilder.append(
            CMIEnchantment.getTranslatedName((Enchantment) enchantEntry.getKey())
                + " -> "
                + enchantEntry.getValue());
      }

      if (!enchantBuilder.toString().isEmpty()) {
        CMILC.info(this, commandSender, "enchants", "[enchants]", enchantBuilder);
      }
    }

    if (cmiMaterial.isMonsterEgg() || cmiMaterial == CMIMaterial.SPAWNER) {
      CMIEntityType cmiEntityType = CMIEntityType.getByType(cmiItemStack.getEntityType());
      if (cmiEntityType != null) {
        CMILC.info(this, commandSender, "entityType", "[type]", cmiEntityType.getName());
      }
    }

    int durability = CMIItemStack.getDurability(itemStack);
    if (durability > 0) {
      int maxDurability = CMIItemStack.getMaxDurability(itemStack);
      CMILC.sendMessage(
          commandSender,
          CMILC.getIM(
                  this,
                  "durability",
                  "[current]",
                  maxDurability - durability,
                  "[max]",
                  maxDurability)
              + (Util.isUnbreakable(itemStack) ? CMILC.getIM(this, "unbreakable") : ""));
    }

    Integer integer = Util.getCustomModelData(itemStack);
    if (integer != null) {
      CMILC.info(this, commandSender, "customModel", "[data]", integer);
    }

    CMILC.info(this, commandSender, "stackSize", "[size]", itemStack.getMaxStackSize());
    LC.info_Spliter.sendMessage(commandSender, new Object[0]);
    return true;
  }
}
