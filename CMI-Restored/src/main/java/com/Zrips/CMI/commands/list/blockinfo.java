package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.BlockStateType;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.ChiseledBookshelf;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.util.Vector;

public class blockinfo implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("Id", " {gcp}Id: {gcs}[id][data]");
    configReader.get("Material", " {gcp}Material: {gcs}[name][ndata]");
    configReader.get("RealName", " {gcp}Real name: {gcs}[name]");
    configReader.get(
        "TempLight",
        " {gcp}Temperature: {gcs}[temp]{gcp}, Light - block: {gcs}[blevel]{gcp}, sky:"
            + " {gcs}[slevel]{gcp}, overral: {gcs}[level]");
    configReader.get(
        "Resistance", " {gcp}Resistance - Mining: {gcs}[mining]{gcp}, Explosion: {gcs}[explosion]");
    configReader.get(
        "noteBlock",
        " {gcp}Tone: {gcs}[tone] {gcp}Octave: {gcs}[octave] {gcp}Instrument: {gcs}[instrument]"
            + " {gcp}Pitch: {gcs}[pitch]");
    configReader.get("BlockState", " {gcp}[state]: {gcs}[value]");
    configReader.get(
        "BlockHitBox",
        " {gcp}Height: {gcs}[height] {gcp}Length: {gcs}[length] {gcp}Width: {gcs}[width]");
    configReader.get(
        "BlockHitBoxHover", "{gcp}From Top: {gcs}[top] {gcp}From Bottom: {gcs}[bottom]");
    configReader.get("Books", " {gcp}Books: {gcs}[list]");
    configReader.get("Enchant", "{gcs}[name]:{gcp}[level] ");
  }

  @CAnnotation(info = "Check block information", regVar = 0, consoleVar = 666, others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    Player player = (Player) commandSender;
    var arrayList = cmi.getUtilManager().getLastTwoTargetBlock(player, 50);
    Block block = arrayList.isEmpty() ? null : (Block) arrayList.get(arrayList.size() - 1);
    if (block != null
        && block.getState().getType() != Material.AIR
        && block.getState().getType() != Material.AIR) {
      CMIMaterial cmiMaterial = CMIMaterial.get(block);
      String string = cmiMaterial.getData() == 0 ? "" : ":" + cmiMaterial.getData();
      if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
        string = block.getData() == 0 ? "" : ":" + block.getData();
      }

      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      CMIMessages.sendMessage(
          commandSender,
          Version.isCurrentEqualOrLower(Version.v1_12_R1)
              ? CMILC.getIM(this, "Id", "[id]", cmiMaterial.getId(), "[data]", string)
              : CMILC.getIM(
                  this, "Material", "[name]", block.getType().toString(), "[ndata]", string));
      CMILC.info(this, commandSender, "RealName", "[name]", cmiMaterial.getName());
      if (arrayList.size() == 2) {
        Block blockSecond = (Block) arrayList.get(0);
        if (blockSecond == null) {
          blockSecond = (Block) arrayList.get(1);
        }

        if (blockSecond != null) {
          CMILC.info(
              this,
              commandSender,
              "TempLight",
              "[temp]",
              (int) (blockSecond.getTemperature() * 100.0) / 100.0,
              "[level]",
              blockSecond.getLightLevel(),
              "[slevel]",
              blockSecond.getLightFromSky(),
              "[blevel]",
              blockSecond.getLightFromBlocks());
        }
      }

      if (Version.isCurrentLower(Version.v1_16_R1)) {
        CMILC.info(
            this,
            commandSender,
            "Resistance",
            "[mining]",
            CMILib.getInstance()
                .getReflectionManager()
                .getItemInfo(cmiMaterial.getId(), "strength"),
            "[explosion]",
            CMILib.getInstance()
                .getReflectionManager()
                .getItemInfo(cmiMaterial.getId(), "durability"));
      } else if (cmiMaterial.getMaterial() != null) {
        CMILC.info(
            this,
            commandSender,
            "Resistance",
            "[mining]",
            cmiMaterial.getMaterial().getHardness(),
            "[explosion]",
            cmiMaterial.getMaterial().getBlastResistance());
      }

      try {
        if (block.getBlockData() instanceof NoteBlock) {
          NoteBlock noteBlock = (NoteBlock) block.getBlockData();
          Tone tone = noteBlock.getNote().getTone();
          int value = noteBlock.getNote().getOctave();
          Instrument instrument = noteBlock.getInstrument();
          float floatValue = (float) Math.pow(2.0, (noteBlock.getNote().getId() - 12) / 12.0);
          CMILC.info(
              this,
              commandSender,
              "noteBlock",
              "[tone]",
              tone.toString(),
              "[octave]",
              value,
              "[instrument]",
              instrument.toString(),
              "[pitch]",
              floatValue);
        }
      } catch (Exception | NoSuchMethodError value2) {
      }

      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
        try {
          if (block.getBlockData() instanceof ChiseledBookshelf) {
            org.bukkit.block.ChiseledBookshelf chiseledBookshelf =
                (org.bukkit.block.ChiseledBookshelf) block.getState();
            ItemStack[] itemStacks = chiseledBookshelf.getInventory().getStorageContents();
            StringBuilder stringBuilder = new StringBuilder();
            ItemStack[] itemStacksValue = itemStacks;
            int count = itemStacks.length;

            for (int countSecond = 0; countSecond < count; countSecond++) {
              ItemStack itemStack = itemStacksValue[countSecond];
              if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                CMIMaterial cmiMaterialValue = CMIMaterial.get(itemStack);
                if (cmiMaterialValue.equals(CMIMaterial.ENCHANTED_BOOK)) {
                  EnchantmentStorageMeta enchantmentStorageMeta =
                      (EnchantmentStorageMeta) itemStack.getItemMeta();

                  for (var entry : enchantmentStorageMeta.getStoredEnchants().entrySet()) {
                    stringBuilder.append(
                        CMILC.getIM(
                            this,
                            "Enchant",
                            "[name]",
                            CMIEnchantment.getTranslatedName((Enchantment) entry.getKey()),
                            "[level]",
                            entry.getValue()));
                  }
                } else {
                  stringBuilder.append(CMIMaterial.get(itemStack).getTranslatedName());
                }
              }
            }

            CMILC.info(this, commandSender, "Books", "[list]", stringBuilder.toString());
          }
        } catch (Exception | NoSuchMethodError value5) {
        }
      }

      var hashMap = getBlockStates(block);
      if (!hashMap.isEmpty()) {
        RawMessage rawMessage = new RawMessage();

        for (Entry entrySecond : hashMap.entrySet()) {
          rawMessage.addText(
              CMILC.getIM(
                  this,
                  "BlockState",
                  "[state]",
                  CMIText.firstToUpperCase(((BlockStateType) entrySecond.getKey()).toString()),
                  "[value]",
                  entrySecond.getValue()));
        }

        rawMessage.show(commandSender);
      }

      CMIHitBox cmiHitBox = new CMIHitBox(block);

      try {
        RawMessage rawMessageSecond = new RawMessage();
        Vector vector = cmiHitBox.getMin();
        Vector vectorSecond = cmiHitBox.getMax();
        rawMessageSecond.addText(
            CMILC.getIM(
                this,
                "BlockHitBox",
                "[height]",
                vectorSecond.getY() - vector.getY(),
                "[length]",
                vectorSecond.getX() - vector.getX(),
                "[width]",
                vectorSecond.getZ() - vector.getZ()));
        rawMessageSecond.addHover(
            CMILC.getIM(
                this,
                "BlockHitBoxHover",
                "[top]",
                1.0 - vectorSecond.getY(),
                "[bottom]",
                vector.getY()));
        rawMessageSecond.show(commandSender);
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      LC.info_Spliter.sendMessage(commandSender, new Object[0]);
      return true;
    } else {
      LC.info_lookAtBlock.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }

  private static HashMap<BlockStateType, String> getBlockStates(Block block) {
    HashMap<BlockStateType, String> hashMap = new HashMap();
    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return hashMap;
    }

    try {
      String string = block.getBlockData().getAsString();
      if (string.contains("[")) {
        String text = string.split("\\[", 2)[1];
        text = text.substring(0, text.length() - 1);
        String[] strings;
        int value = (strings = text.split(",")).length;

        for (int count = 0; count < value; count++) {
          String textSecond = strings[count];
          String[] parts = textSecond.split("=", 2);
          BlockStateType blockStateType = BlockStateType.getByName(parts[0]);
          if (blockStateType != null) {
            hashMap.put(blockStateType, parts[1]);
          }
        }
      }
    } catch (Throwable throwable) {
    }

    return hashMap;
  }
}
