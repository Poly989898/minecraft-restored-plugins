package com.Zrips.CMI.Modules.ArmorEffects;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.HashMap;
import net.Zrips.CMILib.Items.ArmorTypes;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorEffectListener implements Listener {
  private CMI plugin;

  public ArmorEffectListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onBlockInteract(CMIArmorChangeEvent cmiArmorChangeEvent) {
    if (!cmiArmorChangeEvent.isCancelled()) {
      Player player = cmiArmorChangeEvent.getPlayer();
      this.modifyOldEffect(player, cmiArmorChangeEvent.getOldArmorPiece());
      CMIScheduler.runTask(
          this.plugin, () -> this.applyNewEffect(player, cmiArmorChangeEvent.getType()));
    }
  }

  private static ItemStack[] getArmorContents(Player player) {
    return Version.isCurrentEqualOrHigher(Version.v1_9_R1)
        ? player.getInventory().getArmorContents()
        : new ItemStack[] {
          player.getInventory().getHelmet(),
          player.getInventory().getChestplate(),
          player.getInventory().getLeggings(),
          player.getInventory().getBoots()
        };
  }

  private void modifyOldEffect(Player player, ItemStack itemStack) {
    if (itemStack != null) {
      CMINBT cminbt = new CMINBT(itemStack);
      if (cminbt.hasNBT("CMIArmorEffects")) {
        var list = cminbt.getList("CMIArmorEffects");
        if (list != null) {
          list = CMIEncoder.decodeList(list);
          if (list != null) {
            HashMap<PotionEffectType, PotionEffect> hashMap = new HashMap();
            ItemStack[] itemStacks;
            int value = (itemStacks = getArmorContents(player)).length;

            for (int index = 0; index < value; index++) {
              ItemStack armorStack = itemStacks[index];
              if (armorStack != null && !armorStack.equals(itemStack)) {
                CMINBT armorNbt = new CMINBT(armorStack);
                if (armorNbt.hasNBT("CMIArmorEffects")) {
                  var effectList = armorNbt.getList("CMIArmorEffects");
                  if (effectList != null) {
                    effectList = CMIEncoder.decodeList(effectList);
                    if (effectList != null) {
                      for (String string : effectList) {
                        PotionEffect potionEffect =
                            this.plugin.getUtilManager().deserializePotionEffect(string);
                        if (potionEffect != null) {
                          if (potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
                            potionEffect =
                                this.plugin.getUtilManager().deserializePotionEffect(string, 30);
                            if (potionEffect == null) {
                              continue;
                            }
                          }

                          PotionEffect existingEffect =
                              (PotionEffect) hashMap.get(potionEffect.getType());
                          if (existingEffect == null
                              || existingEffect.getAmplifier() <= potionEffect.getAmplifier()) {
                            hashMap.put(potionEffect.getType(), potionEffect);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }

            for (String effectText : list) {
              PotionEffect effectToApply =
                  this.plugin.getUtilManager().deserializePotionEffect(effectText);
              if (effectToApply != null) {
                if (effectToApply.getType().equals(PotionEffectType.NIGHT_VISION)) {
                  effectToApply =
                      this.plugin.getUtilManager().deserializePotionEffect(effectText, 30);
                  if (effectToApply == null) {
                    continue;
                  }
                }

                PotionEffect currentEffect =
                    ArmorEffectManager.getPotionEffect(player, effectToApply.getType());
                if (currentEffect != null) {
                  if (currentEffect.getDuration() <= effectToApply.getDuration() * 2) {
                    if (hashMap.containsKey(effectToApply.getType())) {
                      PotionEffect storedEffect =
                          (PotionEffect) hashMap.get(effectToApply.getType());
                      if (storedEffect.getAmplifier() <= effectToApply.getAmplifier()) {
                        player.removePotionEffect(effectToApply.getType());
                        player.addPotionEffect(storedEffect);
                      }
                    } else {
                      player.removePotionEffect(effectToApply.getType());
                    }
                  } else {
                    PotionEffect mergedEffect =
                        new PotionEffect(
                            currentEffect.getType(),
                            currentEffect.getDuration() - effectToApply.getDuration(),
                            effectToApply.getAmplifier(),
                            true,
                            effectToApply.hasParticles());
                    player.removePotionEffect(effectToApply.getType());
                    player.addPotionEffect(mergedEffect);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void applyNewEffect(Player player, ArmorTypes armorTypes) {
    ItemStack itemStack = player.getInventory().getItem(44 - armorTypes.getSlot());
    if (itemStack != null) {
      CMINBT cminbt = new CMINBT(itemStack);
      if (cminbt.hasNBT("CMIArmorEffects")) {
        var list = cminbt.getList("CMIArmorEffects");
        if (list != null) {
          list = CMIEncoder.decodeList(list);
          if (list != null) {
            for (String string : list) {
              PotionEffect potionEffect =
                  this.plugin.getUtilManager().deserializePotionEffect(string);
              if (potionEffect != null) {
                if (potionEffect.getType().equals(PotionEffectType.NIGHT_VISION)) {
                  potionEffect = this.plugin.getUtilManager().deserializePotionEffect(string, 30);
                  if (potionEffect == null) {
                    continue;
                  }
                }

                PotionEffect existingEffect =
                    ArmorEffectManager.getPotionEffect(player, potionEffect.getType());
                if (existingEffect != null
                    && existingEffect.getAmplifier() < potionEffect.getAmplifier()) {
                  player.removePotionEffect(potionEffect.getType());
                }

                player.addPotionEffect(potionEffect);
              }
            }

            this.plugin.getArmorEffectManager().addPlayer(player.getUniqueId());
            this.plugin.getArmorEffectManager().tasker();
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLoginNameCheck(PlayerJoinEvent playerJoinEvent) {
    CMIScheduler.runTaskLater(
        this.plugin,
        () -> {
          if (playerJoinEvent.getPlayer().isOnline()
              && this.plugin
                  .getArmorEffectManager()
                  .checkPlayerArmor(playerJoinEvent.getPlayer())) {
            this.plugin
                .getArmorEffectManager()
                .addPlayer(playerJoinEvent.getPlayer().getUniqueId());
          }
        },
        20L);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
    this.plugin.getArmorEffectManager().removePlayer(playerQuitEvent.getPlayer().getUniqueId());
  }
}
