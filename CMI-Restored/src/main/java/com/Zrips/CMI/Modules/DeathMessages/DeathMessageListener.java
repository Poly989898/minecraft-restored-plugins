package com.Zrips.CMI.Modules.DeathMessages;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;

public class DeathMessageListener implements Listener {
  private CMI plugin;
  private static CMIDeathMsgInfo temp = null;
  Pattern pattern = Pattern.compile("(\\[)([^\"^\\[^\\]^ ]+)(\\])");

  public DeathMessageListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void playerDamageEvent(EntityDamageEvent entityDamageEvent) {}

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerCombatEvent(EntityDamageEvent entityDamageEvent) {
    if (this.plugin.getDeathMessageManager().isEnabled() && !entityDamageEvent.isCancelled()) {
      this.processEvent(entityDamageEvent);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onQuit(PlayerDeathEvent playerDeathEvent) {
    if (this.plugin.getDeathMessageManager().getAutoHideFrom() >= 0
        && this.plugin.getDeathMessageManager().getAutoHideFrom()
            < this.plugin.getUtilManager().getOnlinePlayerCount()) {
      playerDeathEvent.setDeathMessage("");
    }
  }

  private void processEvent(EntityDamageEvent entityDamageEvent) {
    if (this.plugin.getDeathMessageManager().isEnabled()
        && entityDamageEvent != null
        && entityDamageEvent.getEntity() instanceof Player) {
      Player player = (Player) entityDamageEvent.getEntity();
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        if (!(entityDamageEvent.getFinalDamage() <= 0.0)) {
          damageInformation damageInformation = new damageInformation();
          DamageCause damageCause = entityDamageEvent.getCause();
          if (entityDamageEvent.getFinalDamage() == 32767.0) {
            damageCause = DamageCause.SUICIDE;
          }

          damageInformation.setCause(damageCause);
          damageInformation.setTime();
          damageInformation.setDamage(entityDamageEvent.getFinalDamage());
          if (!(entityDamageEvent instanceof EntityDamageByEntityEvent)) {
            damageInformation.setEnt(null);
            damageInformation.setItem(null);
            if (entityDamageEvent instanceof EntityDamageByBlockEvent) {
              EntityDamageByBlockEvent entityDamageByBlockEvent =
                  (EntityDamageByBlockEvent) entityDamageEvent;
              damageInformation.setBlock(entityDamageByBlockEvent.getDamager());
            }

            cmiUser.addLastDamage(damageInformation);
          } else {
            damageInformation.setBlock(null);
            EntityDamageByEntityEvent entityDamageByEntityEvent =
                (EntityDamageByEntityEvent) entityDamageEvent;
            Entity entity = entityDamageByEntityEvent.getDamager();
            boolean flag = false;
            if (!(entity instanceof LivingEntity)) {
              if (entity instanceof TNTPrimed) {
                TNTPrimed tntPrimed = (TNTPrimed) entity;
                if (tntPrimed.getSource() != null) {
                  entity = tntPrimed.getSource();
                  flag = true;
                }
              }

              if (entity instanceof Projectile) {
                Projectile projectile = (Projectile) entity;
                if (projectile.getShooter() instanceof Entity) {
                  entity = (Entity) projectile.getShooter();
                }

                if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
                    && projectile instanceof Trident) {
                  flag = true;
                  damageInformation.setItem(((Trident) projectile).getItem());
                }
              }

              if (!(entity instanceof LivingEntity)) {
                cmiUser.addLastDamage(damageInformation);
                damageInformation.setEnt(entity);
                return;
              }
            }

            damageInformation.setEnt(entity);
            LivingEntity livingEntity = (LivingEntity) entity;
            if (!flag) {
              ItemStack itemStack = livingEntity.getEquipment().getItemInHand();
              if (Version.isCurrentEqualOrHigher(Version.v1_13_R1) && itemStack == null
                  || itemStack.getType().equals(Material.AIR)) {
                itemStack = livingEntity.getEquipment().getItemInOffHand();
              }

              damageInformation.setItem(itemStack);
            }

            cmiUser.addLastDamage(damageInformation);
          }
        }
      }
    }
  }

  private CMIDeathMsgInfo getKillerInfo(CMIUser cmiUser) {
    damageInformation damageInformation = cmiUser.getLastDamage(null);
    CMIDeathMsgInfo cmiDeathMsgInfo =
        new CMIDeathMsgInfo(
            cmiUser.getUniqueId(), this.getKillerInfo(cmiUser.getPlayer(false), damageInformation));
    damageInformation damageInformationValue = null;
    if (damageInformation != null) {
      damageInformationValue = cmiUser.getLastDamage(damageInformation);
    }

    cmiDeathMsgInfo.setPreviousDamageInfo(
        this.getKillerInfo(cmiUser.getPlayer(false), damageInformationValue));
    calculateBasePath(cmiDeathMsgInfo);
    return cmiDeathMsgInfo;
  }

  private CMIKillerInfo getKillerInfo(Player player, damageInformation damageInformation) {
    ArrayList<CMIDeathType> arrayList = new ArrayList();
    CMIKillerInfo cmiKillerInfo = new CMIKillerInfo(damageInformation);
    boolean flag = false;
    if (damageInformation == null) {
      return cmiKillerInfo;
    }

    Entity entity = damageInformation.getEnt();
    if (entity != null) {
      if (entity instanceof Projectile) {
        Projectile projectile = (Projectile) entity;
        if (projectile.getShooter() instanceof Entity) {
          entity = (Entity) projectile.getShooter();
        } else if (projectile.getShooter() instanceof BlockProjectileSource) {
          BlockProjectileSource blockProjectileSource =
              (BlockProjectileSource) projectile.getShooter();
          cmiKillerInfo.setType(CMIDeathType.Block);
          cmiKillerInfo.setKillerBlock(blockProjectileSource.getBlock());
          arrayList.add(CMIDeathType.Block);
          if (entity.getType().toString().contains("SPLASH")) {
            arrayList.add(CMIDeathType.SplashPotion);
          } else {
            arrayList.add(CMIDeathType.Projectile);
          }

          cmiKillerInfo.setKiller(entity);
          flag = true;
        }
      }

      if (entity instanceof LightningStrike) {
        arrayList.add(CMIDeathType.Custom);
      }

      if (entity != null && arrayList.isEmpty()) {
        if (entity instanceof Player) {
          if (player != null && entity.getUniqueId().equals(player.getUniqueId())) {
            damageInformation.setItem(null);
            damageInformation.setCause(DamageCause.SUICIDE);
          } else {
            cmiKillerInfo.setKiller(entity);
            cmiKillerInfo.setType(CMIDeathType.Player);
            arrayList.add(CMIDeathType.Player);
          }
        } else if (entity instanceof FallingBlock) {
          cmiKillerInfo.setKiller(entity);
          cmiKillerInfo.setType(CMIDeathType.FallingBlocks);
          arrayList.add(CMIDeathType.FallingBlocks);
        } else if (entity instanceof Projectile) {
          cmiKillerInfo.setKiller(entity);
          if (entity.getType().toString().contains("FIREBALL")) {
            cmiKillerInfo.setType(CMIDeathType.FireBall);
            arrayList.add(CMIDeathType.FireBall);
          } else {
            cmiKillerInfo.setType(CMIDeathType.Projectile);
            arrayList.add(CMIDeathType.Projectile);
          }
        } else if (entity instanceof EnderCrystal) {
          cmiKillerInfo.setKiller(entity);
          cmiKillerInfo.setType(CMIDeathType.EndCrystal);
          arrayList.add(CMIDeathType.Custom);
          arrayList.add(CMIDeathType.EndCrystal);
        } else {
          cmiKillerInfo.setKiller(entity);
          cmiKillerInfo.setType(CMIDeathType.Mob);
          arrayList.add(CMIDeathType.Mob);
        }
      }
    }

    Block block = damageInformation.getBlock();
    if (block != null || damageInformation.getCause() == DamageCause.LAVA) {
      cmiKillerInfo.setType(CMIDeathType.Block);
      cmiKillerInfo.setKillerBlock(damageInformation.getBlock());
      arrayList.add(CMIDeathType.Block);
      flag = true;
    }

    ItemStack itemStack = damageInformation.getItem();
    if (itemStack != null && itemStack.getType() != Material.AIR) {
      cmiKillerInfo.setKillerItem(new CMIItemStack(itemStack));
    } else if (entity != null) {
      boolean flagSecond = entity instanceof Projectile;
    }

    DamageCause damageCause = damageInformation.getCause();
    if (damageCause != null && !flag) {
      if (arrayList.isEmpty()) {
        arrayList.add(CMIDeathType.Custom);
      }

      if (damageCause == DamageCause.SUICIDE) {
        arrayList.add(CMIDeathType.Suicide);
      } else if (damageCause == DamageCause.VOID) {
        arrayList.add(CMIDeathType.Void);
      } else if (damageCause == DamageCause.BLOCK_EXPLOSION && damageInformation.getEnt() != null) {
        arrayList.add(CMIDeathType.Tnt);
      }
    }

    cmiKillerInfo.setPath(arrayList);
    return cmiKillerInfo;
  }

  private static void calculateBasePath(CMIDeathMsgInfo cmiDeathMsgInfo) {
    CMIKillerInfo cmiKillerInfo = cmiDeathMsgInfo.getKillerInfo();
    if (!cmiKillerInfo.getPath().isEmpty()) {
      CMIDeathType cmiDeathType = cmiKillerInfo.getPath().get(0);
      String string = cmiDeathType.toString();
      damageInformation damageInformation = cmiKillerInfo.getDamage();
      switch (cmiDeathType) {
        case FallingBlocks:
          if (damageInformation != null) {
            Entity entity = damageInformation.getEnt();
            if (entity instanceof FallingBlock) {
              FallingBlock fallingBlock = (FallingBlock) entity;
              string =
                  string
                      + "."
                      + CMIText.firstToUpperCase(
                              fallingBlock.getBlockData().getMaterial().toString())
                          .replace(" ", "_");
            }
          }
        case Player:
        case Projectile:
        default:
          break;
        case Block:
          if (damageInformation != null) {
            Entity entitySecond = damageInformation.getEnt();
            if (entitySecond instanceof Projectile) {
              Projectile projectile = (Projectile) entitySecond;
              if (projectile.getShooter() instanceof BlockProjectileSource) {
                BlockProjectileSource blockProjectileSource =
                    (BlockProjectileSource) projectile.getShooter();
                string =
                    string
                        + "."
                        + CMIText.firstToUpperCase(
                                blockProjectileSource.getBlock().getType().toString())
                            .replace(" ", "_");
              }
            }

            Block block = damageInformation.getBlock();
            if (block != null) {
              string =
                  string
                      + "."
                      + CMIText.firstToUpperCase(block.getType().toString()).replace(" ", "_");
            } else if (damageInformation.getCause() == DamageCause.LAVA) {
              string = string + ".Lava";
            }
          }
          break;
        case Mob:
          if (damageInformation != null) {
            Entity entityThird = damageInformation.getEnt();
            if (entityThird != null) {
              if (entityThird instanceof Projectile) {
                Projectile projectileValue = (Projectile) entityThird;
                if (projectileValue.getShooter() instanceof Entity) {
                  entityThird = (Entity) projectileValue.getShooter();
                }
              }

              if (entityThird != null) {
                string =
                    string
                        + "."
                        + CMIText.firstToUpperCase(CMIEntityType.get(entityThird).toString())
                            .replace(" ", "_");
              } else {
                string = string + ".General";
              }
            }
          }
          break;
        case Custom:
          if (cmiKillerInfo.getPath().size() > 1) {
            string = string + "." + cmiKillerInfo.getPath().get(1).toString();
          } else if (damageInformation != null) {
            string =
                string
                    + "."
                    + CMIText.firstToUpperCase(damageInformation.getCause().toString())
                        .replace(" ", "_");
          }
      }

      cmiDeathMsgInfo.setBasePath(string);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    if (this.plugin.getDeathMessageManager().isEnabled()
        && playerDeathEvent.getDeathMessage() != null
        && !playerDeathEvent.getDeathMessage().isEmpty()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerDeathEvent.getEntity());
      if (cmiUser != null) {
        this.processEvent(playerDeathEvent.getEntity().getLastDamageCause());
        CMIDeathMsgInfo cmiDeathMsgInfo = this.getKillerInfo(cmiUser);
        if (cmiDeathMsgInfo.getKillerInfo() != null) {
          if (cmiDeathMsgInfo.getKillerInfo().getType() == CMIDeathType.Projectile) {
            cmiDeathMsgInfo.setPrefixPath(
                cmiDeathMsgInfo.getBasePath()
                    + "."
                    + CMIText.firstToUpperCase(
                            cmiDeathMsgInfo.getKillerInfo().getKiller().getType().toString())
                        .replace(" ", "_"));
          } else if (cmiDeathMsgInfo.getKillerInfo().getKillerItem() != null) {
            cmiDeathMsgInfo.setPrefixPath(cmiDeathMsgInfo.getBasePath() + ".Item");
          } else if (cmiDeathMsgInfo.getKillerInfo().getDamage() != null
              && cmiDeathMsgInfo.getKillerInfo().getDamage().getCause() == DamageCause.FALL) {
            if (playerDeathEvent.getEntity().getFallDistance() > 5.0F) {
              cmiDeathMsgInfo.setPrefixPath(cmiDeathMsgInfo.getBasePath() + ".High.General");
            } else {
              cmiDeathMsgInfo.setPrefixPath(cmiDeathMsgInfo.getBasePath() + ".Low.General");
            }
          } else if (cmiDeathMsgInfo.getKillerInfo() != null
              && cmiDeathMsgInfo.getKillerInfo().getDamage() != null
              && cmiDeathMsgInfo.getKillerInfo().getDamage().getCause()
                  == DamageCause.BLOCK_EXPLOSION
              && cmiDeathMsgInfo.getKillerInfo().getType() == CMIDeathType.Player) {
            cmiDeathMsgInfo.setPrefixPath(cmiDeathMsgInfo.getBasePath() + "." + CMIDeathType.Tnt);
          } else {
            cmiDeathMsgInfo.setPrefixPath(cmiDeathMsgInfo.getBasePath() + ".General");
          }
        }

        if (cmiDeathMsgInfo.getPreviousDamageInfo() != null) {
          CMIKillerInfo cmiKillerInfo = cmiDeathMsgInfo.getPreviousDamageInfo();
          if (cmiKillerInfo.getKiller() != null
              && (cmiKillerInfo.getKiller() instanceof Player
                      && cmiDeathMsgInfo.getKillerInfo().getKiller() != cmiKillerInfo.getKiller()
                  || !(cmiKillerInfo.getKiller() instanceof Player)
                      && cmiDeathMsgInfo.getKillerInfo().getKiller() != null
                      && cmiDeathMsgInfo.getKillerInfo().getKiller().getType()
                          != cmiKillerInfo.getKiller().getType()
                  || cmiDeathMsgInfo.getKillerInfo().getKiller() == null)) {
            switch (cmiKillerInfo.getType()) {
              case Player:
                cmiDeathMsgInfo.setSuffixPath("Escaping");
                break;
              case Block:
              case Mob:
                cmiDeathMsgInfo.setSuffixPath("RunningAway");
              case Tnt:
            }
          }
        }

        String string =
            this.plugin.getDeathMessageManager().getDeathMessage(cmiDeathMsgInfo.getPrefixPath());
        if (string != null) {
          String text = "";
          if (cmiDeathMsgInfo.getSuffixPath() != null
              && !cmiDeathMsgInfo.getPrefixPath().equals(cmiDeathMsgInfo.getSuffixPath())) {
            text =
                this.plugin
                    .getDeathMessageManager()
                    .getDeathMessage(cmiDeathMsgInfo.getSuffixPath());
            text = text.replace("[mobName]", "[prevMobName]");
          }

          cmiDeathMsgInfo.setMessage(
              this.plugin.getDeathMessageManager().getPrefix() + string + text);
          if (string.isEmpty()) {
            playerDeathEvent.setDeathMessage("");
          } else {
            playerDeathEvent.setDeathMessage(cmiDeathMsgInfo.getMessage());
          }

          temp = cmiDeathMsgInfo;
          cmiUser.resetLastDamage();
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerDeathEventHighest(PlayerDeathEvent playerDeathEvent) {
    if (this.plugin.getDeathMessageManager().isEnabled()
        && playerDeathEvent.getDeathMessage() != null
        && !playerDeathEvent.getDeathMessage().isEmpty()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(playerDeathEvent.getEntity());
      if (cmiUser != null) {
        if (temp != null && temp.getUniqueId() == cmiUser.getUniqueId()) {
          if (!playerDeathEvent.getDeathMessage().equals(temp.getMessage())) {
            temp.setMessage(playerDeathEvent.getDeathMessage());
          }

          String string = temp.getMessage();
          Matcher matcher = this.pattern.matcher(string);
          ArrayList<String> arrayList = new ArrayList();
          String text = string;
          RawMessage rawMessage = new RawMessage();

          while (matcher.find()) {
            try {
              String textSecond = matcher.group();
              String[] strings = text.split(Pattern.quote(textSecond), 2);
              if (!strings[0].isEmpty()) {
                arrayList.add(strings[0]);
                rawMessage.addText(
                    this.plugin
                        .getPlaceholderAPIManager()
                        .updatePlaceHolders(playerDeathEvent.getEntity(), strings[0]));
              }

              breakThird:
              {
                breakFourth:
                {
                  breakSecond:
                  {
                    arrayList.add(textSecond);
                    String textThird;
                    switch ((textThird = textSecond.toLowerCase()).hashCode()) {
                      case -1937898099:
                        if (textThird.equals("[mobname]")) {
                          if (temp.getKillerInfo().getType() == CMIDeathType.Mob) {
                            rawMessage.addText(
                                textSecond.replace(
                                    "[mobName]",
                                    CMIEntityType.get(temp.getKillerInfo().getKiller())
                                        .getTranslatedName()));
                          }
                          break breakThird;
                        }
                        break breakFourth;
                      case -1589188561:
                        if (textThird.equals("[item]")) {
                          if (temp.getKillerInfo().getKillerItem() != null) {
                            rawMessage.addText(
                                temp.getKillerInfo()
                                    .getKillerItem()
                                    .getDisplayName()
                                    .replace("&", "＆")
                                    .replace("{#", "{＆#"));
                            rawMessage.addItem(temp.getKillerInfo().getKillerItem().getItemStack());
                          }
                          break breakThird;
                        }
                        break breakFourth;
                      case -1209724740:
                        if (!textThird.equals("[sourcename]")) {
                          break breakFourth;
                        }
                        break breakSecond;
                      case -940377940:
                        if (!textThird.equals("[playerdisplayname]")) {
                          break breakFourth;
                        }
                        break;
                      case -567957066:
                        if (!textThird.equals("[playername]")) {
                          break breakFourth;
                        }
                        break;
                      case 80912192:
                        if (textThird.equals("[prevmobname]")) {
                          if (temp.getPreviousDamageInfo() != null
                              && temp.getPreviousDamageInfo().getType() == CMIDeathType.Mob) {
                            rawMessage.addText(
                                textSecond.replace(
                                    "[prevMobName]",
                                    CMIEntityType.get(temp.getPreviousDamageInfo().getKiller())
                                        .getTranslatedName()));
                          }
                          break breakThird;
                        }
                        break breakFourth;
                      case 1370013414:
                        if (!textThird.equals("[sourcedisplayname]")) {
                          break breakFourth;
                        }
                        break breakSecond;
                      case 1464239620:
                        if (textThird.equals("[itemname]")) {
                          if (temp.getKillerInfo().getKillerItem() != null) {
                            rawMessage.addText(
                                temp.getKillerInfo()
                                    .getKillerItem()
                                    .getRealName()
                                    .replace("&", "＆")
                                    .replace("{#", "{＆#"));
                            rawMessage.addItem(temp.getKillerInfo().getKillerItem().getItemStack());
                          }
                          break breakThird;
                        }
                      default:
                        break breakFourth;
                    }

                    Snd snd = new Snd();
                    snd.setTarget(playerDeathEvent.getEntity());
                    textSecond = this.plugin.getLM().updateSnd(snd, textSecond);
                    rawMessage.addText(textSecond);
                    var list = this.plugin.getDeathMessageManager().getPlayerHover();
                    if (!list.isEmpty()) {
                      this.plugin
                          .getPlaceholderAPIManager()
                          .updatePlaceHolders(playerDeathEvent.getEntity(), list);
                      rawMessage.addHover(
                          this.plugin
                              .getPlaceholderAPIManager()
                              .updatePlaceHolders(playerDeathEvent.getEntity(), list));
                    }
                    break breakThird;
                  }

                  Snd sndSecond = new Snd();
                  if (temp.getKillerInfo().getType() == CMIDeathType.Player
                      && temp.getKillerInfo().getKiller() instanceof Player) {
                    sndSecond.setSource((Player) temp.getKillerInfo().getKiller());
                  }

                  textSecond = this.plugin.getLM().updateSnd(sndSecond, textSecond);
                  if (temp.getPreviousDamageInfo() != null
                      && temp.getPreviousDamageInfo().getType() == CMIDeathType.Player) {
                    sndSecond.setSource((Player) temp.getPreviousDamageInfo().getKiller());
                  }

                  textSecond = this.plugin.getLM().updateSnd(sndSecond, textSecond);
                  if (temp.getKillerInfo().getType() == CMIDeathType.Mob) {
                    textSecond =
                        textSecond.replace(
                            "[sourceDisplayName]",
                            temp.getKillerInfo().getKiller().getCustomName() == null
                                ? CMIEntityType.get(temp.getKillerInfo().getKiller())
                                    .getTranslatedName()
                                : temp.getKillerInfo().getKiller().getCustomName());
                  }

                  rawMessage.addText(textSecond);
                  var varSecond = this.plugin.getDeathMessageManager().getKillerHover();
                  if (!varSecond.isEmpty()) {
                    if (temp.getKillerInfo().getKiller() instanceof Player) {
                      rawMessage.addHover(
                          this.plugin
                              .getPlaceholderAPIManager()
                              .updatePlaceHolders(
                                  (Player) temp.getKillerInfo().getKiller(), varSecond));
                    } else {
                      rawMessage.addHover(varSecond);
                    }
                  }
                  break breakThird;
                }

                rawMessage.addText(textSecond);
              }

              if (strings.length > 1) {
                text = strings[1];
              }
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          }

          if (!text.isEmpty()) {
            arrayList.add(text);
            rawMessage.addText(
                this.plugin
                    .getPlaceholderAPIManager()
                    .updatePlaceHolders(playerDeathEvent.getEntity(), text));
          }

          this.plugin
              .getDeathMessageManager()
              .broadcastDeathMessage(playerDeathEvent.getEntity(), rawMessage);
          this.plugin
              .getDiscordSRVManager()
              .sendDiscordDeathMessage(playerDeathEvent.getEntity(), rawMessage.getTextOnly());
          playerDeathEvent.setDeathMessage("");
          temp = null;
        } else {
          temp = null;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerDeathEventMonitor(PlayerDeathEvent playerDeathEvent) {}
}
