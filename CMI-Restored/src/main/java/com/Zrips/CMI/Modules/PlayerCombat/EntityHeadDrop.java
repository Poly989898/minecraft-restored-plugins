package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Entities.CMIEntitySubType;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Entities.MobHeadInfo;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EntityHeadDrop {
  private CMIEntityType type;
  private double baseDropChance = 0.0;
  private double baseLowerAmount = 0.0;
  private HashMap<String, Double> heads = new HashMap<>();
  private Set<String> ls = new HashSet<>();

  public EntityHeadDrop(CMIEntityType cmiEntityType) {
    this.type = cmiEntityType;
  }

  public HashMap<String, Double> getHeads() {
    return this.heads;
  }

  public void setHeads(HashMap<String, Double> hashMap) {
    this.heads = hashMap;
    this.ls.clear();

    for (var entry : hashMap.entrySet()) {
      this.ls.add((String) entry.getKey());
    }
  }

  public void addHead(String string, Double amount) {
    this.heads.put(string, amount);
    this.ls.add(string);
  }

  public ItemStack getHead() {
    if (this.ls.isEmpty()) {
      return null;
    }

    ArrayList arrayList = new ArrayList();
    arrayList.addAll(this.ls);
    Collections.shuffle(arrayList);
    Double amount = new Random(System.currentTimeMillis()).nextInt(100000) / 1000.0;

    for (String string : ((Iterable<String>) arrayList)) {
      Double amountSecond = this.heads.get(string);
      if (amountSecond != null && !(amountSecond < amount)) {
        return this.type.getHead(string);
      }
    }

    return null;
  }

  public ItemStack getHead(Entity entity) {
    return this.getHead(null, entity);
  }

  public ItemStack getHead(Player player, Entity entity) {
    if (this.ls.isEmpty()) {
      return null;
    }

    ArrayList arrayList = new ArrayList();
    arrayList.addAll(this.ls);
    Collections.shuffle(arrayList);
    Double amount = new Random(System.currentTimeMillis()).nextInt(100000) / 1000.0;
    CMIEntityType cmiEntityType = CMIEntityType.getByType(entity.getType());
    CMIUser cmiUser = CMI.getInstance().getPlayerManager().getUser(player);
    var list = CMIEntitySubType.getSubTypes(entity);
    String string = null;

    for (String text : ((Iterable<String>) arrayList)) {
      Double amountSecond = this.heads.get(text);
      if (amountSecond != null && !(amountSecond < amount)) {
        MobHeadInfo mobHeadInfo = (MobHeadInfo) cmiEntityType.getHeadTexturesMap().get(text);
        boolean flag =
            mobHeadInfo.getCriterias().isEmpty()
                ? false
                : list.containsAll(mobHeadInfo.getCriterias());
        if (flag) {
          string = text;
          break;
        }
      }
    }

    if (string == null) {
      for (String textSecond : ((Iterable<String>) arrayList)) {
        Double amountThird = this.heads.get(textSecond);
        if (amountThird != null && !(amountThird < amount)) {
          MobHeadInfo mobHeadInfoValue =
              (MobHeadInfo) cmiEntityType.getHeadTexturesMap().get(textSecond);
          boolean flagSecond =
              mobHeadInfoValue.getCriterias().isEmpty()
                  ? true
                  : list.containsAll(mobHeadInfoValue.getCriterias());
          if (flagSecond) {
            string = textSecond;
          }
        }
      }
    }

    if (string == null) {
      return null;
    }

    MobHeadInfo mobHeadInfoSecond = (MobHeadInfo) cmiEntityType.getHeadTexturesMap().get(string);
    ItemStack itemStack = this.type.getHead(string);
    if (!mobHeadInfoSecond.getLore().isEmpty()) {
      ArrayList<String> items = new ArrayList();

      for (String textThird : mobHeadInfoSecond.getLore()) {
        textThird =
            CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, textThird);
        if (cmiUser != null) {
          textThird =
              textThird.replace(
                  "[chance]",
                  String.valueOf(
                      (int) (cmiUser.getEntityHeadDropChance(entity.getType(), true) * 100.0)
                          / 100.0));
        } else {
          textThird = textThird.replace("[chance]", String.valueOf(0));
        }

        textThird =
            CMI.getInstance()
                .getLM()
                .updateSnd(new Snd().setSender(player).setTarget(player), textThird);
        items.add(CMIChatColor.translate(textThird));
      }

      CMIItemStack cmiItemStack = new CMIItemStack(itemStack);
      cmiItemStack.setLore(items);
      itemStack = cmiItemStack.getItemStack();
    }

    return itemStack;
  }

  public double getBaseDropChance() {
    return this.baseDropChance;
  }

  public void setBaseDropChance(double number) {
    this.baseDropChance = number;
  }

  public double getBaseLowerAmount() {
    return this.baseLowerAmount;
  }

  public void setBaseLowerAmount(double number) {
    this.baseLowerAmount = number;
  }

  public CMIEntityType getType() {
    return this.type;
  }
}
