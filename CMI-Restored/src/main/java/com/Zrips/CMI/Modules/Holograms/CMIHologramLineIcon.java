package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.utils.Util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Enchants.CMIEnchantEnum;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CMIHologramLineIcon extends CMIHologramLineBase {
  private static final String regex = "(%)(?i)(CustomModelData:)(\\d+)(%)";
  private static final Pattern pattern = Pattern.compile("(%)(?i)(CustomModelData:)(\\d+)(%)", 8);
  private ItemStack item = null;
  protected boolean enchanted = false;
  protected boolean glowing = false;
  protected boolean itemInHand = false;
  @Deprecated protected int modelData = 0;

  public CMIHologramLineIcon clone() {
    return new CMIHologramLineIcon(this.getOriginalText());
  }

  public CMIHologramLineIcon(String string) {
    super(string);
    this.setType(CMIHoloLineType.icon);
    if (string.contains("%glowing%")) {
      this.glowing = true;
      string = string.replace("%glowing%", "");
    }

    if (string.contains("%enchanted%")) {
      this.enchanted = true;
      string = string.replace("%enchanted%", "");
    }

    if (string.equalsIgnoreCase("%itemInHand%")) {
      this.itemInHand = true;
      this.setDynamic(true);
    }

    Matcher matcher = pattern.matcher(string);

    while (matcher.find()) {
      try {
        this.modelData = Integer.parseInt(matcher.group(3));
      } catch (Throwable throwable) {
        continue;
      }

      string = string.replace("%" + matcher.group(2) + this.modelData + "%", "");
    }

    this.originalText = string;
  }

  public ItemStack updateItem(Player player, CMIAsyncHead cmiAsyncHead) {
    if (this.itemInHand) {
      if (player != null && player.isOnline()) {
        ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
        return itemStack != null && !CMIMaterial.isAir(itemStack.getType()) ? itemStack : null;
      } else {
        return null;
      }
    } else {
      CMIItemStack cmiItemStack =
          CMIItemStack.deserialize(
              CMI.getInstance()
                  .getPlaceholderAPIManager()
                  .updatePlaceHolders(player, this.getIconText()),
              cmiAsyncHead);
      if (cmiItemStack == null || !cmiItemStack.getCMIType().isAir()) {
        if (cmiItemStack != null && this.enchanted) {
          cmiItemStack.addEnchant(CMIEnchantEnum.POWER.getEnchantment(), 1);
        }

        if (cmiItemStack != null
            && cmiItemStack.getItemStack() != null
            && !cmiItemStack.getCMIType().isAir()) {
          ItemStack iconItem = cmiItemStack.getItemStack();
          if (this.modelData != 0) {
            iconItem = Util.setCustomModelData(iconItem, this.modelData);
          }

          cmiItemStack.setItemStack(iconItem);
        }
      }

      if (cmiItemStack == null || cmiItemStack.getItemStack() == null) {
        cmiItemStack = CMIMaterial.AIR.newCMIItemStack();
      }

      return cmiItemStack.getItemStack();
    }
  }

  public CMIHologramLineIcon setIcon(ItemStack itemStack) {
    this.item = itemStack;
    return this;
  }

  public ItemStack getIcon(Player player) {
    return this.getIcon(player, null);
  }

  public ItemStack getIcon(Player player, CMIAsyncHead cmiAsyncHead) {
    if (!this.isDynamic()) {
      if (this.item == null) {
        this.item = this.updateItem(player, cmiAsyncHead);
      }

      return this.item;
    } else {
      return this.updateItem(player, cmiAsyncHead);
    }
  }

  public String getIconText() {
    return this.getOriginalText();
  }
}
