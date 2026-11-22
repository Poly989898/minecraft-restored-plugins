package com.Zrips.CMI.Modules.Mirror;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMIPlayerInventory;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.commands.list.mirror;
import com.Zrips.CMI.utils.Util;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.BlockStateType;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMIBlock.FlipDirection;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.Vector;

public class CMIMirrorManager {
  HashMap<UUID, CMIBlockMirroring> mirrorMap = new HashMap<>();
  private int maxRange = 50;
  private int maxBlocks = 50;
  private boolean breakDisabled = false;
  private final String CMIMirrorBlock = "CMIMirrorBlock";
  private CMI plugin;
  private static Method setDataMethod = null;
  private static Set<Block> ignorePlaced = Collections.synchronizedSet(new HashSet<>());
  private static Set<Block> ignoreBroken = Collections.synchronizedSet(new HashSet<>());

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Mirror.MaxRange",
        new String[] {
          "Defines how far in blocks from mirror center you can build",
          "This is mainly to protect from forgeting to turn off mirror and starting to build on"
              + " different side of map"
        });
    this.maxRange = configReader.get("Mirror.MaxRange", 50);
    configReader.addComment(
        "Mirror.MaxBlocks",
        new String[] {
          "Max amount of blocks to be modified in one go",
          "Keep this realitvely low to avoid unesecerry load on server due to each block requiring"
              + " separte checks"
        });
    this.maxBlocks = configReader.get("Mirror.MaxBlocks", 16);
    configReader.addComment(
        "Mirror.BreakDisabled",
        new String[] {
          "When disabled block will not be broken in mirror mode",
          "This is mainly to prevent this feature being used for mining purposes and not for"
              + " building"
        });
    this.breakDisabled = configReader.get("Mirror.BreakDisabled", false);
  }

  public CMIBlockMirroring getMirror(CMIUser cmiUser) {
    return !cmiUser.isOnline() ? null : this.getMirror(cmiUser.getPlayer());
  }

  public CMIBlockMirroring getMirror(Player player) {
    return this.mirrorMap.computeIfAbsent(
        player.getUniqueId(), uuid -> new CMIBlockMirroring(player, player.getLocation()));
  }

  @Nullable
  public CMIBlockMirroring getMirror(UUID uuid) {
    return this.mirrorMap.get(uuid);
  }

  public void setMirror(UUID uuid, CMIBlockMirroring cmiBlockMirroring) {
    this.mirrorMap.put(uuid, cmiBlockMirroring);
  }

  public CMIMirrorManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean isMirroring(Player player) {
    return this.mirrorMap.containsKey(player.getUniqueId());
  }

  public boolean addMirroring(Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    return cmiUser != null
        ? this.mirrorMap.computeIfAbsent(
                player.getUniqueId(), uuid -> new CMIBlockMirroring(player, player.getLocation()))
            != null
        : false;
  }

  public void removeMirroring(Player player) {
    this.removeMirroring(player.getUniqueId());
  }

  public void removeMirroring(UUID uuid) {
    this.mirrorMap.remove(uuid);
  }

  public void placeBlock(Player player, Block block, ItemStack itemStack) {
    if (this.isMirroring(player)) {
      CMIUser cmiUser = CMIUser.getUser(player);
      CMIBlockMirroring cmiBlockMirroring = this.getMirror(cmiUser);
      if (ignorePlaced.contains(block)) {
        ignorePlaced.remove(block);
      } else if (!cmiBlockMirroring.isPaused() && cmiBlockMirroring.getCenter() != null) {
        if (!cmiBlockMirroring.inRange(block.getLocation())) {
          CMILC.info(mirror.class, player, "tooFar", "[blocks]", this.getMaxRange());
          cmiBlockMirroring.setPaused(true);
        } else if (itemStack != null) {
          World world = block.getWorld();
          boolean flag = CMIMaterial.isTool(itemStack.getType());
          ItemStack copy = itemStack.clone();
          Material material = block.getType();
          CMIMaterial cmiMaterial = CMIMaterial.get(material);
          if (copy.getType() == Material.AIR) {
            copy = cmiMaterial.newItemStack();
          }

          if (copy.getAmount() == 0) {
            copy.setAmount(1);
          }

          ItemStack mirrorItem = copy;
          CMIBlock cmiBlock = new CMIBlock(block);
          int value =
              this.plugin
                  .getPlayerManager()
                  .getUser(player)
                  .getInventory()
                  .getTotalAmount(
                      mirrorItem,
                      CMIPlayerInventory.itemCheckType.material,
                      CMIPlayerInventory.itemCheckType.nbt);
          boolean notCreative = player.getGameMode() != GameMode.CREATIVE;
          var completableFuture =
              cmiBlockMirroring.getPointsWithFlipAsync(block.getLocation().toVector());
          completableFuture.whenComplete(
              (object, error) ->
                  CMIScheduler.runAtLocation(
                      this.plugin,
                      block.getLocation(),
                      () -> {
                        if (error != null) {
                          error.printStackTrace();
                        }

                        if (object != null && !object.isEmpty()) {
                          int count = 0;
                          boolean changed = false;
                          if (CMIHookType.NoCheatPlus.isEnabled()) {
                            if (NCPExemptionManager.isExempted(player, CheckType.BLOCKPLACE)) {
                              changed = true;
                            }

                            if (!changed) {
                              NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKPLACE);
                            }
                          }

                          try {
                            int slotIndex = 0;

                            for (Entry entry : object.entrySet()) {
                              if (++slotIndex >= this.getMaxBlocks()) {
                                break;
                              }

                              Vector vector = (Vector) entry.getKey();
                              Block targetBlock =
                                  world.getBlockAt(
                                      vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                              Material targetMaterial = targetBlock.getType();
                              if (targetMaterial != Material.BEDROCK
                                  || player.getGameMode() == GameMode.CREATIVE) {
                                boolean valid = false;
                                if (cmiMaterial.isSlab()) {
                                  var hashMap = CMIBlock.getBlockStates(block);
                                  valid =
                                      hashMap.containsKey(BlockStateType.type)
                                          && ((String) hashMap.get(BlockStateType.type))
                                              .equalsIgnoreCase("double");
                                }

                                if (valid || cmiMaterial != CMIMaterial.get(targetMaterial)) {
                                  if (notCreative && count >= value) {
                                    CMILC.info(mirror.class, player, "noBlocks");
                                    break;
                                  }

                                  ignorePlaced.add(targetBlock);
                                  BlockPlaceEvent blockPlaceEvent =
                                      CMIEvent.placeBlockEvent(targetBlock, player);
                                  Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
                                  if (!blockPlaceEvent.isCancelled()) {
                                    count++;
                                    targetBlock.setType(material);
                                    ((CMIMirrorFlipInformation) entry.getValue()).get().isEmpty();
                                    if (!CMIMaterial.isFence(material)
                                        && !CMIMaterial.isGlassPane(material)
                                        && !CMIMaterial.IRON_BARS.equals(material)
                                        && !CMIMaterial.isRail(material)
                                        && !CMIMaterial.isWall(material)) {
                                      if (Version.isCurrentEqualOrLower(Version.v1_12_R1)) {
                                        try {
                                          setDataMethod.invoke(
                                              targetBlock, (Byte) cmiBlock.getData());
                                          CMIBlock targetCmiBlock = new CMIBlock(targetBlock);
                                          if (setDataMethod == null) {
                                            setDataMethod =
                                                Block.class.getMethod("setData", byte.class);
                                          }

                                          for (FlipDirection flipDirection :
                                              ((CMIMirrorFlipInformation) entry.getValue()).get()) {
                                            targetCmiBlock =
                                                targetCmiBlock.flip(flipDirection, false);
                                          }

                                          setDataMethod.invoke(
                                              targetBlock, (Byte) targetCmiBlock.getData());
                                        } catch (Throwable throwable) {
                                          throwable.printStackTrace();
                                        }
                                      } else {
                                        targetBlock.setBlockData(
                                            ((BlockData) cmiBlock.getData()).clone());
                                        CMIBlock cmiBlockCopy = new CMIBlock(targetBlock);

                                        for (FlipDirection flip :
                                            ((CMIMirrorFlipInformation) entry.getValue()).get()) {
                                          cmiBlockCopy = cmiBlockCopy.flip(flip, false);
                                        }

                                        if (cmiBlockCopy.getData() != null) {
                                          targetBlock.setBlockData(
                                              ((BlockData) cmiBlockCopy.getData()).clone());
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          } catch (Throwable throwable2) {
                            throwable2.printStackTrace();
                          }

                          if (CMIHookType.NoCheatPlus.isEnabled() && !changed) {
                            NCPExemptionManager.unexempt(player, CheckType.BLOCKPLACE);
                          }

                          if (notCreative && !flag) {
                            ItemStack itemCopy = mirrorItem.clone();
                            itemCopy.setAmount(count);
                            var mirrorMap =
                                this.plugin
                                    .getPlayerManager()
                                    .getUser(player)
                                    .getInventory()
                                    .removeItemByCriteria(
                                        itemCopy,
                                        CMIPlayerInventory.itemCheckType.material,
                                        CMIPlayerInventory.itemCheckType.nbt);

                            for (Entry mirrorEntry : mirrorMap.entrySet()) {
                              ItemStack offHandItem = CMIItemStack.getItemInOffHand(player);
                              if (offHandItem != null && offHandItem.getType().equals(material)) {
                                int mirrorIndex =
                                    offHandItem.getAmount()
                                        - ((ItemStack) mirrorEntry.getValue()).getAmount();
                                if (mirrorIndex < 0) {
                                  CMIItemStack.setItemInOffHand(player, null);
                                } else {
                                  offHandItem.setAmount(mirrorIndex);
                                }
                              }
                            }

                            CMILC.info(mirror.class, player, "used", "[blocks]", ++count);
                          }
                        }
                      }));
        }
      }
    }
  }

  public void breakBlock(Player player, Block block) {
    if (this.isMirroring(player) && !this.plugin.getConfigManager().isMirrorBreakDisabled()) {
      CMIUser cmiUser = CMIUser.getUser(player);
      CMIBlockMirroring cmiBlockMirroring = this.getMirror(cmiUser);
      if (!ignoreBroken.remove(block)) {
        if (!cmiBlockMirroring.isPaused()) {
          if (cmiBlockMirroring.getCenter() != null) {
            if (cmiBlockMirroring.getLastAction() == 0L
                || cmiBlockMirroring.getLastAction() + 30L <= System.currentTimeMillis()) {
              if (!cmiBlockMirroring.inRange(block.getLocation())) {
                CMILC.info(mirror.class, player, "tooFar", "[blocks]", this.getMaxRange());
                cmiBlockMirroring.setPaused(true);
              }

              cmiBlockMirroring.setLastAction(System.currentTimeMillis());
              var completableFuture =
                  cmiBlockMirroring.getPointsAsync(block.getLocation().toVector());
              World world = block.getWorld();
              completableFuture.whenComplete(
                  (object, error) ->
                      CMIScheduler.runAtLocation(
                          this.plugin,
                          block.getLocation(),
                          () -> {
                            boolean flag = false;
                            if (CMIHookType.NoCheatPlus.isEnabled()) {
                              if (NCPExemptionManager.isExempted(player, CheckType.BLOCKBREAK)) {
                                flag = true;
                              }

                              if (!flag) {
                                NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);
                              }
                            }

                            int value = 0;
                            int count = 0;

                            for (Vector vector : object) {
                              if (++count >= this.getMaxBlocks()) {
                                break;
                              }

                              if (vector != null) {
                                Block targetBlock =
                                    world.getBlockAt(
                                        vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                                if (targetBlock.getType() != Material.BEDROCK
                                    || player.getGameMode() == GameMode.CREATIVE) {
                                  BlockState blockState = block.getState();
                                  ignoreBroken.add(targetBlock);
                                  BlockBreakEvent blockBreakEvent =
                                      new BlockBreakEvent(targetBlock, player);
                                  Bukkit.getServer().getPluginManager().callEvent(blockBreakEvent);
                                  if (!blockBreakEvent.isCancelled()) {
                                    if (player.getGameMode() != GameMode.CREATIVE) {
                                      targetBlock.breakNaturally();
                                    } else {
                                      targetBlock.setType(Material.AIR);
                                      if (Version.isCurrentEqualOrLower(Version.v1_14_R1)) {
                                        blockState.getData().setData((byte) 0);
                                      }
                                    }

                                    value++;
                                  }
                                }
                              }
                            }

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                              ItemStack itemStack = CMIItemStack.getItemInMainHand(player);
                              if (itemStack != null
                                  && itemStack.getItemMeta() instanceof Damageable
                                  && itemStack.getType().getMaxDurability() > 15
                                  && !Util.isUnbreakable(itemStack)) {
                                Damageable damageable = (Damageable) itemStack.getItemMeta();
                                int damage = damageable.getDamage() + value;
                                damage =
                                    CMINumber.clamp(
                                        damage, 0, itemStack.getType().getMaxDurability());
                                damageable.setDamage(damage);
                                itemStack.setItemMeta(damageable);
                                CMIItemStack.setItemInMainHand(player, itemStack);
                              }
                            }

                            if (CMIHookType.NoCheatPlus.isEnabled() && !flag) {
                              NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
                            }
                          }));
            }
          }
        }
      }
    }
  }

  public void openGui(Player player) {
    this.openGui(CMIUser.getUser(player));
  }

  public void openGui(final CMIUser cmiUser) {
    if (cmiUser.isOnline()) {
      final CMIBlockMirroring cmiBlockMirroring = this.getMirror(cmiUser);
      if (cmiBlockMirroring.getCenter() == null) {
        cmiBlockMirroring.setCenter(cmiUser.getLocation());
      }

      CMIGui cmiGui = new CMIGui(cmiUser.getPlayer());
      cmiGui.setTitle(CMILC.getIM(mirror.class, "title"));
      cmiGui.setInvSize(GUIRows.r6);
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(
              8,
              cmiBlockMirroring.getCenter() != null
                  ? CMIMaterial.COMPASS
                  : CMIMaterial.RED_STAINED_GLASS) {
            public void click(GUIClickType guiClickType) {
              if (cmiUser.isOnline()) {
                CMIBlockMirroring mirror = CMIMirrorManager.this.getMirror(cmiUser);
                mirror.setCenter(cmiUser.getPlayer().getLocation());
                this.updateLooks();
                this.update(this.getGui());
              }
            }

            public void updateLooks() {
              this.setItem(
                  cmiBlockMirroring.getCenter() != null
                      ? CMIMaterial.COMPASS.newItemStack()
                      : CMIMaterial.RED_STAINED_GLASS.newItemStack());
              this.setName(CMILC.getIM(mirror.class, "location"));
              this.clearLore();
              this.addLore(
                  LC.Location_Full.getLocale(new Object[] {cmiBlockMirroring.getCenter()}));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(17, CMIMaterial.LADDER) {
            public void click(GUIClickType clickType) {
              if (cmiUser.isOnline()) {
                CMIBlockMirroring mirror = CMIMirrorManager.this.getMirror(cmiUser);
                mirror.setEdge(mirror.getNextEdge());
                this.updateLooks();
                this.update(this.getGui());
              }
            }

            public void updateLooks() {
              this.setItem(CMIMaterial.LADDER.newItemStack());
              this.setName(CMILC.getIM(mirror.class, "offset"));
              this.clearLore();
              this.addLore(
                  CMILC.getIM(
                      mirror.class,
                      "offsetPos",
                      "[x]",
                      cmiBlockMirroring.getEdge().getX(),
                      "[y]",
                      cmiBlockMirroring.getEdge().getY(),
                      "[z]",
                      cmiBlockMirroring.getEdge().getZ(),
                      "[id]",
                      cmiBlockMirroring.edgeToNumber() + 1));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(35, CMIMaterial.BLUE_WOOL) {
            public void click(GUIClickType clickType) {
              if (cmiUser.isOnline()) {
                CMIBlockMirroring mirror = CMIMirrorManager.this.getMirror(cmiUser);
                mirror.setPaused(!mirror.isPaused());
                this.updateLooks();
                this.update(this.getGui());
              }
            }

            public void updateLooks() {
              this.setItem(
                  cmiBlockMirroring.isPaused()
                      ? CMIMaterial.BLUE_WOOL.newItemStack()
                      : CMIMaterial.GREEN_WOOL.newItemStack());
              this.setName(
                  cmiBlockMirroring.isPaused()
                      ? LC.modify_paused.getLocale(new Object[0])
                      : LC.modify_running.getLocale(new Object[0]));
              this.clearLore();
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(53, CMIMaterial.ACACIA_FENCE) {
            public void click(GUIClickType clickType) {
              if (cmiUser.isOnline()) {
                CMIBlockMirroring flipMirror = CMIMirrorManager.this.getMirror(cmiUser);
                flipMirror.setFlipType(
                    flipMirror.getFlipType() == CMIMIrrorFlipType.Axis
                        ? CMIMIrrorFlipType.Angle
                        : CMIMIrrorFlipType.Axis);
                this.updateLooks();
                this.update(this.getGui());
              }
            }

            public void updateLooks() {
              this.setItem(
                  cmiBlockMirroring.getFlipType() == CMIMIrrorFlipType.Axis
                      ? CMIMaterial.ACACIA_FENCE.newItemStack()
                      : CMIMaterial.STONE_BRICK_WALL.newItemStack());
              this.setName(
                  CMILC.getIM(
                      mirror.class,
                      "flipType",
                      "[type]",
                      CMIText.firstToUpperCase(cmiBlockMirroring.getFlipType().toString())));
              this.clearLore();
            }
          };
      cmiGui.addButton(cmiGuiButton);
      CMIMirrorType[] cmiMirrorTypes;
      int value = (cmiMirrorTypes = CMIMirrorType.values()).length;

      for (int typeIndex = 0; typeIndex < value; typeIndex++) {
        final CMIMirrorType cmiMirrorType = cmiMirrorTypes[typeIndex];
        cmiGuiButton =
            new CMIGuiButton(
                cmiMirrorType.getSlot(),
                cmiBlockMirroring.is(cmiMirrorType)
                    ? cmiMirrorType.getIconEnabled()
                    : cmiMirrorType.getIconDisabled()) {
              public void click(GUIClickType clickType) {
                if (cmiUser.isOnline()) {
                  CMIBlockMirroring modeMirror = CMIMirrorManager.this.getMirror(cmiUser);
                  CMIMirrorMode cmiMirrorMode = modeMirror.get(cmiMirrorType);
                  switch (clickType) {
                    case Left:
                    case Q:
                      cmiMirrorMode.setEnabled(!cmiMirrorMode.isEnabled());
                      break;
                    case LeftShift:
                      if (cmiMirrorType.isSections()) {
                        cmiMirrorMode.setSections(cmiMirrorMode.getSections() + 1);
                      } else {
                        cmiMirrorMode.setEnabled(!cmiMirrorMode.isEnabled());
                      }
                    case Right:
                    case MiddleMouse:
                    case QControl:
                    default:
                      break;
                    case RightShift:
                      if (cmiMirrorType.isSections()) {
                        cmiMirrorMode.setSections(cmiMirrorMode.getSections() - 1);
                      } else {
                        cmiMirrorMode.setEnabled(!cmiMirrorMode.isEnabled());
                      }
                  }

                  this.updateLooks();
                  this.update(this.getGui());
                }
              }

              public void updateLooks() {
                this.setItem(
                    cmiBlockMirroring.is(cmiMirrorType)
                        ? cmiMirrorType.getIconEnabled().newItemStack()
                        : cmiMirrorType.getIconDisabled().newItemStack());
                this.setName(
                    CMIChatColor.GRAY
                        + CMILC.getIM(
                            mirror.class, "Action." + cmiMirrorType.toString() + ".Name"));
                this.clearLore();
                ArrayList arrayList = new ArrayList();
                if (cmiMirrorType.isSections()) {
                  arrayList.add(
                      CMILC.getIM(
                          mirror.class,
                          "sections",
                          "[sections]",
                          CMIMirrorManager.this
                              .getMirror(cmiUser)
                              .get(cmiMirrorType)
                              .getSections()));
                  arrayList.add(CMILC.getIM(mirror.class, "sectionsAdjust"));
                }

                arrayList.add(
                    cmiBlockMirroring.is(cmiMirrorType)
                        ? LC.modify_enabled.getLocale(new Object[0])
                        : LC.modify_disabled.getLocale(new Object[0]));
                arrayList.add(
                    CMIChatColor.GRAY
                        + CMILC.getIM(
                            mirror.class, "Action." + cmiMirrorType.toString() + ".Desc"));
                this.addLore(arrayList);
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }

      cmiGui.fillEmptyButtons();
      cmiGui.open();
    }
  }

  public int getMaxRange() {
    return this.maxRange;
  }

  public int getMaxBlocks() {
    return this.maxBlocks;
  }

  public boolean isBreakDisabled() {
    return this.breakDisabled;
  }
}
