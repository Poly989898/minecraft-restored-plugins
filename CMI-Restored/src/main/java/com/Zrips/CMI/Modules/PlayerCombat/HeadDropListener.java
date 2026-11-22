package com.Zrips.CMI.Modules.PlayerCombat;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Totems.ResurectionListener;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadDropListener implements Listener {
  private CMI plugin;
  String HEX_PATTERN = "([a-zA-Z0-9+/]{32,}+={0,2})";
  Pattern pattern = Pattern.compile(this.HEX_PATTERN);

  public HeadDropListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void PlayerCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (!entityDamageEvent.isCancelled() && entityDamageEvent.getEntity() instanceof Player) {
      final Player player = (Player) entityDamageEvent.getEntity();
      if (this.plugin.getPlayerCombatManager().getPlayerWorlds().isEmpty()
          || this.plugin
              .getPlayerCombatManager()
              .getPlayerWorlds()
              .contains(player.getWorld().getName().toLowerCase())) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (cmiUser != null) {
          if (!(entityDamageEvent.getFinalDamage() <= 0.0)) {
            Player damagerPlayer = null;
            if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
              EntityDamageByEntityEvent entityDamageByEntityEvent =
                  (EntityDamageByEntityEvent) entityDamageEvent;
              Entity entity = entityDamageByEntityEvent.getDamager();
              if (entity instanceof Player) {
                damagerPlayer = (Player) entity;
              } else if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectile.getShooter() instanceof Player) {
                  damagerPlayer = (Player) projectile.getShooter();
                }
              }

              if (damagerPlayer != null && !player.equals(damagerPlayer)) {
                if (!(player.getHealth() > entityDamageEvent.getFinalDamage())) {
                  CMIUser damagerUser = this.plugin.getPlayerManager().getUser(damagerPlayer);
                  if (damagerUser != null) {
                    if (this.plugin.getPlayerCombatManager().isDropPlayerHead()) {
                      if ((this.plugin.getTotemManager().isOnCd(player)
                              || !this.plugin.getTotemManager().isTotemRemoveFromInventory()
                              || !ResurectionListener.haveTotem(player))
                          && (cmiUser.getItemInOffHand() == null
                              || CMIMaterial.get(cmiUser.getItemInOffHand())
                                  != CMIMaterial.TOTEM_OF_UNDYING)
                          && (cmiUser.getItemInHand() == null
                              || CMIMaterial.get(cmiUser.getItemInHand())
                                  != CMIMaterial.TOTEM_OF_UNDYING)) {
                        double number = damagerUser.getHeadDropChance(cmiUser.getUniqueId(), true);
                        Random random = new Random(System.currentTimeMillis());
                        double roll = random.nextInt(100000) / 1000.0;
                        if (!(roll >= number)) {
                          damagerUser.addHeadDropCount(cmiUser.getUniqueId());

                          try {
                            var list = this.plugin.getPlayerCombatManager().getPlayerHeadLore();
                            if (!list.isEmpty()) {
                              Snd snd = new Snd();
                              snd.setSource(cmiUser);
                              snd.setTarget(damagerUser);
                              list = this.plugin.getLM().updateSnd(snd, list);
                              list =
                                  this.plugin
                                      .getPlaceholderAPIManager()
                                      .updatePlaceHolders(player, list);

                              for (int value = 0; value < list.size(); value++) {
                                list.set(value, CMIChatColor.translate((String) list.get(value)));
                              }
                            }

                            final List dropList = list;
                            CMIAsyncHead cmiAsyncHead =
                                new CMIAsyncHead() {
                                  public void afterAsyncUpdate(ItemStack itemStack) {
                                    CMIScheduler.runTask(
                                        HeadDropListener.this.plugin,
                                        () -> HeadDropListener.drop(player, itemStack, dropList));
                                  }
                                };
                            cmiAsyncHead.setForce(true);
                            String string = CMIUser.getUser(player).getSkin();
                            string = string == null ? player.getName() : string;
                            CMIItemStack cmiItemStack =
                                CMILib.getInstance()
                                    .getItemManager()
                                    .getItem("head:" + string, cmiAsyncHead);
                            if (!cmiAsyncHead.isAsyncHead()) {
                              drop(player, cmiItemStack.getItemStack(), list);
                            }
                          } catch (Exception | Error value2) {
                            value2.printStackTrace();
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private static void drop(Player player, ItemStack itemStack, List<String> list) {
    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
    if (!list.isEmpty()) {
      skullMeta.setLore(list);
    }

    skullMeta.setOwningPlayer(player);
    itemStack.setItemMeta(skullMeta);
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        player.getLocation(),
        () -> {
          Item item = player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
          if (item != null) {
            item.setPickupDelay(20);
          }
        });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void BlockBreakEvent(BlockBreakEvent blockBreakEvent) {}

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
    if (this.plugin.getPlayerCombatManager().isDropEntityHead()
        && !(entityDeathEvent.getEntity() instanceof Player)) {
      LivingEntity livingEntity = entityDeathEvent.getEntity();
      if (livingEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
        if (this.plugin.getPlayerCombatManager().getMobWorlds().isEmpty()
            || this.plugin
                .getPlayerCombatManager()
                .getMobWorlds()
                .contains(livingEntity.getWorld().getName().toLowerCase())) {
          Player player = null;
          EntityDamageByEntityEvent entityDamageByEntityEvent =
              (EntityDamageByEntityEvent) livingEntity.getLastDamageCause();
          Entity entity = entityDamageByEntityEvent.getDamager();
          if (entity instanceof Player) {
            player = (Player) entity;
          } else if (entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            if (projectile.getShooter() instanceof Player) {
              player = (Player) projectile.getShooter();
            }
          }

          if (player != null) {
            CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
            if (cmiUser != null) {
              EntityType entityType = livingEntity.getType();
              cmiUser.addEntityKill(entityType);
              double number = cmiUser.getEntityHeadDropChance(entityType, true);
              Random random = new Random(System.currentTimeMillis());
              double roll = random.nextInt(100000) / 1000.0;
              if (!(roll >= number)) {
                ItemStack itemStack =
                    this.plugin.getPlayerCombatManager().tryToGetEntityHead(player, livingEntity);
                if (itemStack != null) {
                  cmiUser.getEntityKills().get(entityType).addHeadDropCount();

                  try {
                    livingEntity
                        .getWorld()
                        .dropItemNaturally(livingEntity.getLocation(), itemStack);
                  } catch (Exception | Error value) {
                    value.printStackTrace();
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerDeath(EntityDeathEvent entityDeathEvent) {}
}
