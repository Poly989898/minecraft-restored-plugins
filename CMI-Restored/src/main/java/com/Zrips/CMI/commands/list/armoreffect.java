package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class armoreffect implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @CAnnotation(
      info = "Modifies armor effects",
      args = "[potionEffect]",
      multiTab = "[potioneffect]",
      explanation = {},
      regVar = -666,
      consoleVar = 666,
      alias = true,
      modules = "armorEffects",
      others = true)
  @Override
  public Boolean perform(final CMI cmi, CommandSender commandSender, String[] strings) {
    String string = null;
    HashSet<PotionEffect> hashSet = new HashSet();
    boolean flag = false;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.equalsIgnoreCase("-s")) {
        flag = true;
      } else {
        if (string == null) {
          CMIUser cmiUser = cmi.getPlayerManager().getUserByAproxName(arg);
          if (cmiUser != null) {
            string = cmiUser.getName();
            continue;
          }
        }

        if (arg.contains(";;")) {
          String[] effectParts;
          int partCount = (effectParts = arg.split(";;")).length;

          for (int partIndex = 0; partIndex < partCount; partIndex++) {
            String part = effectParts[partIndex];
            PotionEffect potionEffect = cmi.getUtilManager().deserializePotionEffect(part, 15);
            if (potionEffect != null) {
              hashSet.add(potionEffect);
            }
          }
        } else {
          PotionEffect effect = cmi.getUtilManager().deserializePotionEffect(arg, 15);
          if (effect != null) {
            hashSet.add(effect);
          }
        }
      }
    }

    final Player player = cmi.getTarget(commandSender, string, this);
    if (player != null && player.isOnline()) {
      ItemStack itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
      if (itemStack == null || itemStack.getType() == Material.AIR) {
        LC.info_nothingInHand.sendMessage(commandSender, new Object[0]);
        return null;
      }

      if (strings.length == 0) {
        var list = new CMINBT(itemStack).getList("CMIArmorEffects");
        if (list == null) {
          list = new ArrayList();
        }

        boolean removeMode = false;

        for (int loreIndex = 0; loreIndex < list.size(); loreIndex++) {
          String loreText = CMIEncoder.decode((String) list.get(loreIndex));
          if (loreText == null) {
            loreText = (String) list.get(loreIndex);
            removeMode = true;
          }

          list.set(loreIndex, loreText);
        }

        if (removeMode) {
          List encodedLore = CMIEncoder.encode(new ArrayList<>(list));
          if (!encodedLore.isEmpty()) {
            CMILib.getInstance()
                .getReflectionManager()
                .setItemInMainHand(
                    player, cmi.getNMS().setNBTList(itemStack, "CMIArmorEffects", encodedLore));
          }

          player.updateInventory();
          itemStack = CMILib.getInstance().getReflectionManager().getItemInMainHand(player);
        }

        final ItemStack itemCopy = itemStack;
        final ArrayList<String> arrayList = new ArrayList<>(list);

        for (int effectIndex = 0; effectIndex < arrayList.size(); effectIndex++) {
          PotionEffect effectCopy =
              cmi.getUtilManager().deserializePotionEffect((String) arrayList.get(effectIndex));
          if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
            arrayList.set(
                effectIndex,
                CMIText.everyFirstToUpperCase(effectCopy.getType().getKey().getKey(), false)
                    + ":"
                    + (effectCopy.getAmplifier() + 1)
                    + (effectCopy.hasParticles() ? ":v" : ""));
          } else {
            arrayList.set(
                effectIndex,
                CMIText.everyFirstToUpperCase(effectCopy.getType().getName(), false)
                    + ":"
                    + (effectCopy.getAmplifier() + 1)
                    + (effectCopy.hasParticles() ? ":v" : ""));
          }
        }

        PageInfo pageInfo = new PageInfo(10, arrayList.size(), 1);
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        ChatMessageListEdit chatMessageListEdit =
            new ChatMessageListEdit(player, arrayList, ChatEditType.String) {
              public void onUpdate() {
                ArrayList lore = new ArrayList();

                for (String line : arrayList) {
                  lore.addAll(Arrays.asList(line.split(";;")));
                }

                arrayList.clear();
                arrayList.addAll(lore);

                for (int loreCount = 0; loreCount < lore.size(); loreCount++) {
                  lore.set(
                      loreCount,
                      CMIText.firstToUpperCase(((String) lore.get(loreCount)).split(":")[0])
                          + ":15"
                          + (((String) lore.get(loreCount)).contains(":")
                              ? ":" + ((String) lore.get(loreCount)).split(":", 2)[1]
                              : ""));
                }

                List encodedEffects = CMIEncoder.encode(lore);
                if (!encodedEffects.isEmpty()) {
                  CMILib.getInstance()
                      .getReflectionManager()
                      .setItemInMainHand(
                          player,
                          cmi.getNMS().setNBTList(itemCopy, "CMIArmorEffects", encodedEffects));
                } else {
                  CMINBT cminbt = new CMINBT(itemCopy);
                  ItemStack removedItem = (ItemStack) cminbt.remove("CMIArmorEffects");
                  CMILib.getInstance()
                      .getReflectionManager()
                      .setItemInMainHand(player, removedItem);
                }

                player.updateInventory();
              }
            };
        chatMessageListEdit.print();
        pageInfo.autoPagination(
            commandSender, CommandsHandler.getLabel() + " " + this.getClass().getSimpleName());
        return true;
      } else {
        ArrayList<String> serializedEffects = new ArrayList();

        for (PotionEffect activeEffect : hashSet) {
          String serialized = cmi.getUtilManager().serializePotionEffect(activeEffect);
          if (serialized != null) {
            serializedEffects.add(serialized);
          }
        }

        List encodedSerialized = CMIEncoder.encode(serializedEffects);
        if (!encodedSerialized.isEmpty()) {
          CMILib.getInstance()
              .getReflectionManager()
              .setItemInMainHand(
                  player, cmi.getNMS().setNBTList(itemStack, "CMIArmorEffects", encodedSerialized));
        }

        return true;
      }
    } else {
      LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      return null;
    }
  }
}
