package com.Zrips.CMI.Modules.Elytra;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Speed;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIArmorChangeEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Items.ArmorTypes;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ElytraBoostListener implements Listener {
  private CMI plugin;
  private Effect eff = null;
  HashMap<UUID, Speed> speedMap = new HashMap<>();

  public ElytraBoostListener(CMI cmi) {
    this.plugin = cmi;
    Effect[] effects;
    int value = (effects = Effect.values()).length;

    for (int index = 0; index < value; index++) {
      Effect effect = effects[index];
      if (effect.name().equalsIgnoreCase("ENDERDRAGON_SHOOT")) {
        this.eff = effect;
      }
    }
  }

  @EventHandler
  public void onPlayerSpeed(PlayerMoveEvent playerMoveEvent) {
    Player player = playerMoveEvent.getPlayer();
    Speed speed = this.speedMap.get(player.getUniqueId());
    if (!player.isGliding()) {
      if (speed != null) {
        this.speedMap.remove(player.getUniqueId());
      }
    } else {
      if (speed != null) {
        if (speed.isBoosting() && this.plugin.getElytraManager().isBoostShowParticles()) {
          if (!speed.isSuperBoosting()) {
            showParticle(player, Particle.CLOUD);
          } else {
            showParticle(player, Particle.FLAME);
          }
        }

        if (!speed.isSpeedometer() || !speed.isGoodTime()) {
          return;
        }

        String string = "";
        if (speed.isBoosting()) {
          string = CMILC.Elytra_SpeedBoost.getLocale();
        }

        double number = speed.getSpeed();
        String speedText = String.valueOf(number);

        try {
          DecimalFormat decimalFormat = new DecimalFormat("#000.00");
          if (!this.plugin.getElytraManager().isBoostSpeedDecimals()) {
            decimalFormat = new DecimalFormat("#000");
          }

          speedText = decimalFormat.format(speed.getSpeed());
        } catch (Exception exception) {
        }

        boolean flag = false;
        if (speed.isShowDurability() && this.plugin.getConfigManager().isArmorDurabilityLossUse()) {
          CMIItemStack cmiItemStack =
              CMILib.getInstance().getItemManager().getItem(player.getInventory().getChestplate());
          if (cmiItemStack != null && cmiItemStack.getCMIType().equals(CMIMaterial.ELYTRA)) {
            int value = cmiItemStack.getDurability();
            int maxDurability = cmiItemStack.getMaxDurability();
            double durabilityPercent = 100.0 - value * 100.0 / maxDurability;
            if (durabilityPercent
                <= this.plugin.getConfigManager().getArmorDurabilityLossPercentage()) {
              flag = true;
            }
          }
        }

        if (flag) {
          String speedMessage = CMILC.Elytra_Speed.getLocale("[speed]", speedText);
          speedMessage = "&c" + CMIChatColor.stripColor(speedMessage);
          CMIActionBar.send(player, string + speedMessage + string);
        } else {
          CMIActionBar.send(
              player, string + CMILC.Elytra_Speed.getLocale("[speed]", speedText) + string);
        }

        speed.setTime(System.currentTimeMillis());
        speed.setLoc(player.getLocation());
      } else {
        this.speedMap.put(player.getUniqueId(), new Speed(player));
      }
    }
  }

  @EventHandler
  public void onInventoryClick(CMIArmorChangeEvent cmiArmorChangeEvent) {
    if (cmiArmorChangeEvent.getType() == ArmorTypes.ELYTRA
        && !PermissionsManager.CMIPerm.elytra.hasPermission(cmiArmorChangeEvent.getPlayer())) {
      cmiArmorChangeEvent.setCancelled(true);
      CMILC.Elytra_CanUse.sendMessage(cmiArmorChangeEvent.getPlayer());
    }
  }

  public static double xPosYaw(double number, double y, double z) {
    return Math.sin(number) * y * Math.cos((Math.PI / 180.0) * z);
  }

  public static double yPosYaw(double number, double z) {
    return Math.cos(number) * z;
  }

  public static double zPosYaw(double number, double x, double y) {
    return Math.sin(number) * x * Math.sin((Math.PI / 180.0) * y);
  }

  public double zPosPitch2(double number, double x, double y) {
    return Math.sin(number) * x * Math.sin((Math.PI / 180.0) * y);
  }

  public void spawnCircle(Player player) {
    spawnCircle(player.getLocation());
  }

  public static void spawnCircle(Location location) {
    Location copy = location.clone();
    double number = copy.getYaw() / 180.0 * Math.PI;
    double pitchRad = copy.getPitch() / 180.0 * Math.PI;
    double scale = 1.0;
    copy = copy.add(copy.getDirection());
    draw(copy, scale, number, pitchRad, true);
    scale = 0.5;
    copy = copy.subtract(copy.getDirection());
    draw(copy, scale, number, pitchRad, true);
  }

  private static void draw(Location location, double number, double y, double z, boolean flag) {
    Random random = new Random(System.currentTimeMillis());

    for (double angle = 0.0; angle < 360.0; angle += 15.0) {
      double radius = number;
      if (flag) {
        radius = random.nextInt((int) (number * 10.0)) / 10.0;
      }

      double x = xPosYaw(Math.toRadians(angle), radius, y);
      double yPos = yPosYaw(Math.toRadians(angle), radius);
      double zPos = zPosYaw(Math.toRadians(angle), radius, y);
      Vector vector = new Vector(x, yPos, zPos);
      vector = rotateX(vector, z);
      vector = rotateY(vector, -y);
      Location ring =
          new Location(
              location.getWorld(),
              vector.getX() + location.getX(),
              vector.getY() + location.getY(),
              vector.getZ() + location.getZ());
      location.getWorld().spawnParticle(Particle.FLAME, ring, 1, 0.0, 0.0, 0.0, 0.0);
    }
  }

  private static Vector rotateX(Vector vector, double number) {
    double rotY = Math.cos(number) * vector.getY() - Math.sin(number) * vector.getZ();
    double rotZ = Math.sin(number) * vector.getY() + Math.cos(number) * vector.getZ();
    return vector.setY(rotY).setZ(rotZ);
  }

  private static Vector rotateY(Vector vector, double number) {
    double rotX = Math.cos(number) * vector.getX() + Math.sin(number) * vector.getZ();
    double rotZFinal = -Math.sin(number) * vector.getX() + Math.cos(number) * vector.getZ();
    return vector.setX(rotX).setZ(rotZFinal);
  }

  @EventHandler
  public void EntityToggleGlideEvent(EntityToggleGlideEvent entityToggleGlideEvent) {
    if (entityToggleGlideEvent.getEntity() instanceof Player) {
      Player player = (Player) entityToggleGlideEvent.getEntity();
      if (entityToggleGlideEvent.isGliding()) {
        this.speedMap.put(player.getUniqueId(), new Speed(player));
      } else {
        this.speedMap.remove(player.getUniqueId());
      }
    }
  }

  @EventHandler
  public void onElytraFlight(PlayerInteractEvent playerInteractEvent) {
    Player player = playerInteractEvent.getPlayer();
    if (player.isGliding()) {
      Action action = playerInteractEvent.getAction();
      if (action != Action.PHYSICAL) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (this.plugin.getElytraManager().isPreventRocketUsage()
            && (CMIMaterial.get(cmiUser.getItemInHand()) == CMIMaterial.FIREWORK_ROCKET
                || CMIMaterial.get(cmiUser.getItemInOffHand()) == CMIMaterial.FIREWORK_ROCKET)) {
          playerInteractEvent.setCancelled(true);
        }

        if (this.plugin.getElytraManager().getBoostItemStack() == null
            || action == Action.RIGHT_CLICK_AIR
            || action == Action.LEFT_CLICK_AIR) {
          int value = this.plugin.getElytraManager().getBoostAmount();
          double number = this.plugin.getElytraManager().getBoostMultyplier();
          Speed speed = this.speedMap.get(player.getUniqueId());
          if (speed == null || speed.isAllowBoost()) {
            if (speed == null
                || speed.getSpeed() < this.plugin.getElytraManager().getBoostSpeedLimit()) {
              if (player.isSneaking()) {
                if (speed == null || !speed.isSuperBoosting() && speed.isAllowSuperBoost()) {
                  this.spawnCircle(player);
                  if (this.eff != null) {
                    player.getWorld().playEffect(player.getLocation(), this.eff, 0);
                  }

                  number = this.plugin.getElytraManager().getBoostSuperMultyplier();
                  value = this.plugin.getElytraManager().getBoostSuperAmount();
                }

                if (speed != null && speed.isAllowSuperBoost()) {
                  speed.setSuperBoost(System.currentTimeMillis());
                }
              }

              if (speed != null) {
                speed.setBoost(System.currentTimeMillis());
              }

              if (this.plugin.getElytraManager().isBoostRequiresItem()) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (!this.plugin.getElytraManager().isBoostItemStack(itemStack)) {
                  return;
                }
              }

              if (speed != null && !speed.isFree()) {
                if (this.plugin.getElytraManager().isBoostUseItems()) {
                  ItemStack mainHand = player.getInventory().getItemInMainHand();
                  if (!this.plugin.getElytraManager().isBoostItemStackConsume(mainHand)) {
                    return;
                  }

                  cmiUser
                      .getInventory()
                      .removeItemByCriteria(
                          this.plugin.getElytraManager().getBoostItemStackConsume(),
                          CMIPlayerInventory.itemCheckType.material,
                          CMIPlayerInventory.itemCheckType.custommodel);
                  player.updateInventory();
                } else {
                  long longValue = cmiUser.getTotalExp();
                  if (longValue < value) {
                    number = (double) longValue / value;
                  }

                  if (longValue > value) {
                    cmiUser.takeExp(value);
                  }
                }
              }

              setVel(player, number);
            }
          }
        }
      }
    }
  }

  private static void setVel(Player player, double number) {
    Vector vector = player.getVelocity().add(player.getLocation().getDirection().multiply(number));
    double maxSpeed = 350.0;
    double speedPerTick = maxSpeed / 3.6 / 20.0;
    if (vector.length() > speedPerTick) {
      vector = vector.normalize().multiply(speedPerTick);
    }

    player.setVelocity(vector);
  }

  private static void showParticle(Player player, Particle particle) {
    player.getWorld().spawnParticle(particle, player.getLocation(), 1, 0.0, 0.0, 0.0, 0.0);
  }
}
