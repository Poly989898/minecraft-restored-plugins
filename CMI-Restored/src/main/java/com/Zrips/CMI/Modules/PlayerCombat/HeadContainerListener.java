package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Enchants.CMIEnchantment;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Entities.MobHeadInfo;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.PersistentData.CMIBlockPersistentDataContainer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HeadContainerListener implements Listener {
  private CMI plugin;
  static final Pattern pattern = Pattern.compile("([a-zA-Z0-9+/]{32,}+={0,2})");

  public HeadContainerListener(CMI cmi) {
    this.plugin = cmi;
  }

  private boolean processPersistentContainer(BlockBreakEvent blockBreakEvent) {
    if (!this.plugin.getPlayerCombatManager().isMaintainName()
        && !this.plugin.getPlayerCombatManager().isMaintainLore()
        && !this.plugin.getPlayerCombatManager().isMaintainEnchants()) {
      return false;
    }

    Block block = blockBreakEvent.getBlock();
    CMIBlockPersistentDataContainer cmiBlockPersistentDataContainer =
        new CMIBlockPersistentDataContainer(block);
    String string =
        this.plugin.getPlayerCombatManager().isMaintainName()
            ? cmiBlockPersistentDataContainer.getString("Skull.Name")
            : null;
    String loreText =
        this.plugin.getPlayerCombatManager().isMaintainLore()
            ? cmiBlockPersistentDataContainer.getString("Skull.Lore")
            : null;
    String enchantText =
        this.plugin.getPlayerCombatManager().isMaintainEnchants()
            ? cmiBlockPersistentDataContainer.getString("Skull.Enchants")
            : null;
    if (string == null && loreText == null && enchantText == null) {
      return false;
    }

    cmiBlockPersistentDataContainer.remove("Skull.Name");
    cmiBlockPersistentDataContainer.remove("Skull.Lore");
    cmiBlockPersistentDataContainer.remove("Skull.Enchants");
    cmiBlockPersistentDataContainer.save();
    if (block.getDrops().isEmpty()) {
      return false;
    }

    ItemStack itemStack = ((ItemStack) block.getDrops().iterator().next()).clone();
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (string != null) {
      itemMeta.setDisplayName(string);
    }

    if (loreText != null) {
      itemMeta.setLore(CMIList.stringToList(loreText, "\n"));
    }

    itemStack.setItemMeta(itemMeta);
    if (enchantText != null) {
      HashMap<Enchantment, Integer> hashMap = new HashMap();
      String[] strings;
      int value = (strings = enchantText.split(";")).length;

      for (int index = 0; index < value; index++) {
        String enchantEntry = strings[index];

        try {
          String[] enchantParts = enchantEntry.split(":");
          Enchantment enchantment = CMIEnchantment.get(enchantParts[0]);
          int enchantLevel = Integer.parseInt(enchantParts[1]);
          if (enchantment != null && enchantLevel > 0) {
            hashMap.put(enchantment, enchantLevel);
          }
        } catch (Exception exception) {
        }
      }

      if (!hashMap.isEmpty()) {
        itemStack.addUnsafeEnchantments(hashMap);
      }
    }

    blockBreakEvent.setDropItems(false);
    blockBreakEvent
        .getBlock()
        .getWorld()
        .dropItemNaturally(blockBreakEvent.getBlock().getLocation(), itemStack);
    return true;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
    if (!blockBreakEvent.isCancelled() && blockBreakEvent.isDropItems()) {
      Block block = blockBreakEvent.getBlock();
      if (block.getState() instanceof Skull) {
        try {
          if (this.processPersistentContainer(blockBreakEvent)) {
            return;
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        Object object = CMINBT.getNbt(block);
        if (object != null) {
          Matcher matcher = pattern.matcher(object.toString());
          if (matcher.find()) {
            CMINBT cminbt = new CMINBT(block);
            if (cminbt.hasNBT("PublicBukkitValues.slimefun:slimefun_block")) {
              String string = cminbt.getString("SkullOwner.Name");
              if (string != null && string.startsWith("CS-")) {
                return;
              }
            }

            String headTexture = matcher.group(0);
            if (headTexture != null) {
              CMIEntityType[] cmiEntityTypes;
              int value = (cmiEntityTypes = CMIEntityType.values()).length;

              for (int index = 0; index < value; index++) {
                CMIEntityType cmiEntityType = cmiEntityTypes[index];
                MobHeadInfo mobHeadInfo =
                    (MobHeadInfo) cmiEntityType.getHeadTexturesMap().get(headTexture);
                if (mobHeadInfo != null) {
                  ItemStack itemStack = cmiEntityType.getHead(headTexture);
                  if (itemStack != null) {
                    blockBreakEvent.setDropItems(false);
                    blockBreakEvent
                        .getBlock()
                        .getWorld()
                        .dropItemNaturally(blockBreakEvent.getBlock().getLocation(), itemStack);
                  }
                  break;
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlockPlaceEvent(BlockPlaceEvent blockPlaceEvent) {
    if (!blockPlaceEvent.isCancelled()) {
      Block block = blockPlaceEvent.getBlock();
      if (block.getState() instanceof Skull) {
        ItemStack itemStack = blockPlaceEvent.getItemInHand();
        CMIBlockPersistentDataContainer cmiBlockPersistentDataContainer =
            new CMIBlockPersistentDataContainer(block);
        cmiBlockPersistentDataContainer.remove("Skull.Name");
        cmiBlockPersistentDataContainer.remove("Skull.Lore");
        cmiBlockPersistentDataContainer.remove("Skull.Enchants");
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
          if (itemMeta.hasDisplayName()) {
            cmiBlockPersistentDataContainer.set("Skull.Name", itemMeta.getDisplayName());
          }

          if (itemMeta.hasLore()) {
            cmiBlockPersistentDataContainer.set(
                "Skull.Lore", CMIList.listToString(itemMeta.getLore(), "\n"));
          }

          if (!itemMeta.getEnchants().isEmpty()) {
            String string =
                itemMeta.getEnchants().entrySet().stream()
                    .map(
                        entry ->
                            CMIEnchantment.getName((Enchantment) entry.getKey())
                                + ":"
                                + entry.getValue())
                    .collect(Collectors.joining(";"));
            if (!string.isEmpty()) {
              cmiBlockPersistentDataContainer.set("Skull.Enchants", string);
            }
          }
        }

        try {
          cmiBlockPersistentDataContainer.save();
        } catch (Throwable throwable) {
        }
      }
    }
  }
}
