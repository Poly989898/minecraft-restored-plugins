package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Locale.CMILC;
import java.util.HashMap;
import java.util.Map.Entry;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class DecoratedPotListener implements Listener {
  private CMI plugin;

  public DecoratedPotListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler
  public void onInteractBeeHive(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
      if (!Version.isCurrentLower(Version.v1_20_R3)) {
        if (Config.ShowDecoratedPot) {
          if (playerInteractEvent.getHand() == EquipmentSlot.HAND) {
            Block block = playerInteractEvent.getClickedBlock();
            CMIScheduler.runAtLocation(
                this.plugin,
                block.getLocation(),
                () -> {
                  CMIMaterial cmiMaterial = CMIMaterial.get(block);
                  if (cmiMaterial == CMIMaterial.DECORATED_POT) {
                    DecoratedPot decoratedPot = (DecoratedPot) block.getState();
                    ItemStack[] itemStacks = decoratedPot.getInventory().getContents();
                    HashMap<Material, Integer> hashMap = new HashMap();
                    ItemStack[] stackArray = itemStacks;
                    int value = itemStacks.length;

                    for (int index = 0; index < value; index++) {
                      ItemStack itemStack = stackArray[index];
                      if (itemStack != null && itemStack.getType() != Material.AIR) {
                        hashMap.computeIfAbsent(itemStack.getType(), object -> 0);
                        hashMap.put(
                            itemStack.getType(),
                            (Integer) hashMap.get(itemStack.getType()) + itemStack.getAmount());
                      }
                    }

                    StringBuilder stringBuilder = new StringBuilder();

                    for (Entry entry : hashMap.entrySet()) {
                      if (!stringBuilder.toString().isEmpty()) {
                        stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
                      }

                      stringBuilder
                          .append(CMIMaterial.get((Material) entry.getKey()).getTranslatedName())
                          .append(" x ")
                          .append(entry.getValue());
                    }

                    String string = stringBuilder.toString();
                    CMILC.info_decoratedpotinfo.sendMessage(
                        playerInteractEvent.getPlayer(),
                        "[items]",
                        string.isEmpty() ? "-" : string);
                  }
                });
          }
        }
      }
    }
  }
}
