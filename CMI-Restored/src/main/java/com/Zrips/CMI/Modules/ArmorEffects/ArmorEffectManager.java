package com.Zrips.CMI.Modules.ArmorEffects;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.utils.CMIEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorEffectManager {
  private CMI plugin;
  public static final String NBTName = "CMIArmorEffects";
  private Set<UUID> players = new HashSet<>();
  private CMITask saveId = null;

  public ArmorEffectManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void removePlayer(UUID uuid) {
    this.players.remove(uuid);
    if (this.players.isEmpty()) {
      this.onDisable();
    }
  }

  public void addPlayer(UUID uuid) {
    this.players.add(uuid);
  }

  public void onDisable() {
    if (this.saveId != null) {
      this.saveId.cancel();
      this.saveId = null;
    }
  }

  public void tasker() {
    if (this.saveId == null) {
      this.saveId =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (this.players.isEmpty()) {
                  this.onDisable();
                } else {
                  var iterator = this.players.iterator();

                  while (iterator.hasNext()) {
                    UUID uuid = (UUID) iterator.next();
                    Player player = CMIUser.getOnlinePlayer(uuid);
                    if (player == null || !player.isOnline()) {
                      iterator.remove();
                    } else if (!this.checkPlayerArmor(player)) {
                      iterator.remove();
                    }
                  }
                }
              },
              280L,
              280L);
    }
  }

  public boolean checkPlayerArmor(Player player) {
    HashMap<PotionEffectType, PotionEffect> hashMap = new HashMap();
    ItemStack[] itemStacks;
    int value = (itemStacks = player.getInventory().getArmorContents()).length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = itemStacks[index];
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

                  PotionEffect existingEffect = (PotionEffect) hashMap.get(potionEffect.getType());
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

    for (PotionEffect activeEffect : hashMap.values()) {
      if (activeEffect != null) {
        PotionEffect currentEffect = getPotionEffect(player, activeEffect.getType());
        if (currentEffect != null) {
          if (currentEffect.getType().equals(PotionEffectType.NIGHT_VISION)
                  && currentEffect.getDuration() - 20 <= activeEffect.getDuration()
              || currentEffect.getDuration() - 12 <= activeEffect.getDuration()) {
            if (currentEffect.getType().equals(PotionEffectType.HEALTH_BOOST)) {
              if (Version.isCurrentLower(Version.v1_20_R1)) {
                double number = player.getHealth();
                player.removePotionEffect(activeEffect.getType());
                player.addPotionEffect(activeEffect);
                player.setHealth(CMINumber.clamp(number, 1.0, player.getMaxHealth()));
              } else {
                player.addPotionEffect(activeEffect);
              }
            } else {
              if (Version.isCurrentLower(Version.v1_20_R1)) {
                player.removePotionEffect(activeEffect.getType());
              }

              player.addPotionEffect(activeEffect);
              if (player.isFlying() && activeEffect.getType().getName().equals("LEVITATION")) {
                player.setFlying(false);
              }
            }
          }
        } else {
          player.addPotionEffect(activeEffect);
          if (player.isFlying() && activeEffect.getType().getName().equals("LEVITATION")) {
            player.setFlying(false);
          }
        }
      }
    }

    return !hashMap.isEmpty();
  }

  public static PotionEffect getPotionEffect(Player player, PotionEffectType potionEffectType) {
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      return player.getPotionEffect(potionEffectType);
    }

    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
      if (potionEffect.getType() == potionEffectType) {
        return potionEffect;
      }
    }

    return null;
  }
}
