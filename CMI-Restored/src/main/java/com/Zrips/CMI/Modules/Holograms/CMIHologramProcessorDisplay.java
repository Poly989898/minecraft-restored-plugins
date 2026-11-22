package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Components.CMIComponentText;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Display.CMIBlockDisplay;
import com.Zrips.CMI.Modules.Display.CMIDisplay;
import com.Zrips.CMI.Modules.Display.CMIItemDisplay;
import com.Zrips.CMI.Modules.Display.CMITextDisplay;
import com.Zrips.CMI.Modules.Holograms.Animations.CMIHologramAnimationType;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramBoardSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CMIHologramProcessorDisplay extends CMIHologramProcessor {
  public CMIHologramProcessorDisplay(CMIHologram cmiHologram) {
    super(cmiHologram);
  }

  @Override
  public CompletableFuture<Void> updateBillboard(UUID uuid, Set<CMIHologramUpdateType> set) {
    if (this.isHiding(set)) {
      CompletableFuture completableFuture = new CompletableFuture();
      CMIHologramUserData cmiHologramUserData = this.getHologramBase().getData(uuid);
      if (cmiHologramUserData != null) {
        if (!set.contains(CMIHologramUpdateType.InstantHide)
            && this.getHologramBase().getAnimationSettings().isFadeOutAnimation()) {
          cmiHologramUserData.initializeFadeOutAnimation(1.0);
          cmiHologramUserData
              .startFadeAnimation(
                  this.getHologramBase(),
                  uuid,
                  cmiHologramAnimationCycle -> {
                    switch (cmiHologramAnimationCycle) {
                      case Finished:
                        if (this.getHologramBase().getType().equals(CMIHologramType.TextDisplay)) {
                          this.getHologramBase().removeFromCache(uuid, false);
                        }

                        completableFuture.complete(null);
                        cmiHologramUserData.setHidden(true);
                        CMIHologramBatch cmiHologramBatch = cmiHologramUserData.getHologramBatch();
                        if (cmiHologramBatch != null) {
                          cmiHologramBatch.clear();
                        }
                        break;
                      case Canceled:
                        completableFuture.complete(null);
                        this.getHologramBase().removeFromCache(uuid, false);
                        break;
                      case Running:
                        this.updateBatch(cmiHologramUserData, Bukkit.getPlayer(uuid));
                    }
                  })
              .setSteps(this.getHologramBase().getAnimationSettings().getFadeOutAnimationSpeed());
        } else {
          this.getHologramBase().removeFromCache(uuid, false);
          completableFuture.complete(null);
          cmiHologramUserData.setHidden(true);
          CMIHologramBatch batch = cmiHologramUserData.getHologramBatch();
          if (batch != null) {
            batch.clear();
          }
        }

        CMIHologramHoverData.getData(uuid).setHoveringLine(-1);
      } else {
        completableFuture.complete(null);
      }

      return completableFuture;
    } else {
      if (this.getHologramBase().getLocation().getWorld() == null) {
        return CompletableFuture.completedFuture(null);
      }

      Player player = Bukkit.getPlayer(uuid);
      if (player != null && player.isOnline()) {
        CMIHologramUserData userData = this.getHologramBase().getData(uuid);
        var list = this.getHologramBase().getLinesByPlayerPage(uuid);
        int value = userData.getPage();
        ArrayList<CMIHologramLine> arrayList = new ArrayList();

        for (int index = 0; index < list.size(); index++) {
          CMIHologramLine cmiHologramLine =
              list.size() - 1 < index || index < 0
                  ? new CMIHologramLine(this.getHologramBase().getTextSettings(), "")
                  : (list.get(index) == null
                      ? new CMIHologramLine(this.getHologramBase().getTextSettings(), "")
                      : (CMIHologramLine) list.get(index));
          arrayList.add(cmiHologramLine);
        }

        if (this.getHologramBase().getPageSettings().isAutoPagination()) {
          arrayList.add(this.getHologramBase().getAutoPageLine());
        }

        if (this.getHologramBase().getPlayersFromVisibilityRange().isEmpty()
            && this.getHologramBase().getPlayersFromUpdateRange().isEmpty()) {
          return CompletableFuture.completedFuture(null);
        }

        CMIHologramPage cmiHologramPage = this.getHologramBase().getPages().getPage(value);
        if (cmiHologramPage == null) {
          return CompletableFuture.completedFuture(null);
        }

        ArrayList displays = new ArrayList();
        CMIHologramBatch batchCopy = userData.getHologramBatch();
        if (!(batchCopy instanceof CMIHologramBatchDisplay)) {
          if (batchCopy != null) {
            batchCopy.destroy(player);
          }

          batchCopy = new CMIHologramBatchDisplay();
          userData.setHologramBatch(batchCopy);
        }

        CMIHologramBatchDisplay cmiHologramBatchDisplay = (CMIHologramBatchDisplay) batchCopy;
        cmiHologramPage.setLineCount(arrayList.size());
        if (!this.getHologramBase().getPlayersFromVisibilityRange().contains(uuid)) {
          this.getHologramBase().hide(uuid);
          return CompletableFuture.completedFuture(null);
        }

        int addedCount = 0;
        int removedCount = 0;
        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();
        HashSet<CMIHologramUpdateType> hashSet = new HashSet();
        boolean flag = false;

        for (CMIHologramLine line : arrayList) {
          removedCount++;
          switch (line.getType()) {
            case plainText:
              addedCount++;
              displays.add(this.processTextLine(player, line));
              CMIHologramBatchGroup cmiHologramBatchGroup =
                  cmiHologramBatchDisplay.getIcon(removedCount);
              if (cmiHologramBatchGroup != null) {
                cmiHologramBatchGroup.destroy(player);
                cmiHologramBatchGroup.setEnabled(false);
              }
              break;
            case icon:
            case staticIcon:
              displays.add(addedCount == 0 ? " " : "");
              addedCount++;
              CMIHologramLineIcon cmiHologramLineIcon =
                  (CMIHologramLineIcon) line.getLinePortions().get(0).getBaseLineData();
              if (!(cmiHologramBatchDisplay.getIcon(removedCount)
                  instanceof CMIHologramBatchGroupDisplay)) {
                CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay =
                    new CMIHologramBatchGroupDisplay(null, cmiHologramLineIcon);
                cmiHologramBatchDisplay.addIcon(removedCount, cmiHologramBatchGroupDisplay);
                CMIVector3D cmiVector3D =
                    this.getIconPositionOffset(removedCount, cmiHologramPage.getLineCount());
                cmiHologramBatchGroupDisplay.setOffset(cmiVector3D.clone());
                var iconFuture = this.createIcon(player, cmiHologramBatchGroupDisplay);
                futures.add(iconFuture);
                hashSet.add(CMIHologramUpdateType.Icon);
                int iconIndex = removedCount;
                iconFuture.thenAccept(
                    object -> {
                      cmiHologramBatchGroupDisplay.setDisplay(object);
                      cmiHologramBatchDisplay.addIcon(iconIndex, cmiHologramBatchGroupDisplay);
                      this.updateItemDisplay(cmiHologramBatchGroupDisplay, player, set);
                    });
              } else {
                CMIVector3D offset =
                    this.getIconPositionOffset(removedCount, cmiHologramPage.getLineCount());
                CMIHologramBatchGroupDisplay groupDisplay =
                    (CMIHologramBatchGroupDisplay) cmiHologramBatchDisplay.getIcon(removedCount);
                CMIVector3D groupOffset = groupDisplay.getOffset();
                if (offset.getX() != groupOffset.getX()
                    || offset.getY() != groupOffset.getY()
                    || offset.getZ() != groupOffset.getZ()) {
                  groupDisplay.setOffset(offset.clone());
                  set.add(CMIHologramUpdateType.Icon);
                }

                if (groupDisplay.getLine().isDynamic()) {
                  this.updateItemDisplay(groupDisplay, player, set);
                }

                if (!groupDisplay.isEnabled()) {
                  groupDisplay.show(player);
                  groupDisplay.setEnabled(true);
                }
              }
              break;
            case image:
              CMIHologramLineImage cmiHologramLineImage =
                  (CMIHologramLineImage) line.getLinePortions().get(0).getBaseLineData();
              var imageDisplays = cmiHologramLineImage.getImage();
              if (imageDisplays != null) {
                displays.addAll(imageDisplays);
                addedCount += imageDisplays.size();
              }
          }
        }

        for (var entry : cmiHologramBatchDisplay.getIcons().entrySet()) {
          if ((Integer) entry.getKey() > cmiHologramPage.getLineCount()) {
            ((CMIHologramBatchGroup) entry.getValue()).destroy(player);
            ((CMIHologramBatchGroup) entry.getValue()).setEnabled(false);
          }
        }

        if (!displays.isEmpty()
            && (this.getHologramBase()
                    .getPlayersFromVisibilityRange()
                    .contains(player.getUniqueId())
                || this.getHologramBase()
                    .getPlayersFromUpdateRange()
                    .contains(player.getUniqueId()))) {
          if (!this.isEmptyLines(displays)
              && this.isShowing(set)
              && (cmiHologramBatchDisplay.getFrontDisplay() == null
                  || cmiHologramBatchDisplay.getFrontDisplay().getDisplay() == null)) {
            userData.setHidden(false);
            flag = true;
            cmiHologramBatchDisplay.addFrontDisplay(new CMITextDisplay(null));
            if (this.getHologramBase().getTextSettings().isDoubleSided()
                && this.getHologramBase().getSettings().getBillboard().isCanHaveBackSide()) {
              cmiHologramBatchDisplay.addBackDisplay(new CMITextDisplay(null));
            }

            hashSet.add(CMIHologramUpdateType.Text);
            futures.add(
                CMIScheduler.runAtLocation(
                    CMI.getInstance(),
                    this.getHologramBase().getLocation(),
                    () -> {
                      try {
                        if (!this.getHologramBase()
                            .getHologramType(uuid)
                            .equals(CMIHologramType.TextDisplay)) {
                          return;
                        }

                        cmiHologramBatchDisplay.addFrontDisplay(
                            this.createDisplay(userData, displays, true));
                        if (this.getHologramBase().getTextSettings().isDoubleSided()
                            && this.getHologramBase()
                                .getSettings()
                                .getBillboard()
                                .isCanHaveBackSide()) {
                          cmiHologramBatchDisplay.addBackDisplay(
                              this.createDisplay(userData, displays, false));
                        }
                      } catch (Throwable throwable) {
                        throwable.printStackTrace();
                      }
                    }));
          }

          if (this.getHologramBase().getBoardSettings().isEnabled()
              && (cmiHologramBatchDisplay.getBoard() == null
                  || cmiHologramBatchDisplay.getBoard().getBlockData() != null
                      && !this.getHologramBase()
                          .getBoardSettings()
                          .getMaterial()
                          .equals(
                              cmiHologramBatchDisplay.getBoard().getBlockData().getMaterial()))) {
            if (cmiHologramBatchDisplay.getBoard() != null
                && cmiHologramBatchDisplay.getBoard().getBlockData() != null
                && !this.getHologramBase()
                    .getBoardSettings()
                    .getMaterial()
                    .equals(cmiHologramBatchDisplay.getBoard().getBlockData().getMaterial())) {
              cmiHologramBatchDisplay.getBoard().destroy(player);
            }

            cmiHologramBatchDisplay.setBoard(new CMIBlockDisplay(null));
            hashSet.add(CMIHologramUpdateType.Billboard);
            futures.add(
                CMIScheduler.runAtLocation(
                    CMI.getInstance(),
                    this.getHologramBase().getLocation(),
                    () ->
                        cmiHologramBatchDisplay.setBoard(
                            this.createBoard(userData, cmiHologramPage))));
          }

          cmiHologramPage.setHeight(
              cmiHologramPage.getLineCount() * this.getHologramBase().getLineHeight());
          if (!futures.isEmpty()) {
            CompletableFuture allFutures =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
            if (flag
                && !set.contains(CMIHologramUpdateType.InstantShow)
                && this.getHologramBase().getAnimationSettings().isFadeInAnimation()) {
              userData.initializeFadeInAnimation(1.0);
              userData
                  .startFadeAnimation(this.getHologramBase(), uuid, cycle -> {})
                  .setSteps(
                      this.getHologramBase().getAnimationSettings().getFadeInAnimationSpeed());
            } else {
              userData.resetFadeInAnimation();
            }

            return allFutures.thenRun(
                () -> {
                  int displayIndex = 0;

                  for (CMIDisplay cmiDisplay : cmiHologramBatchDisplay.getDisplays().values()) {
                    displayIndex++;

                    try {
                      this.updateDisplaySettings(
                          userData, (CMITextDisplay) cmiDisplay, displayIndex == 1);
                      if (hashSet.contains(CMIHologramUpdateType.Text)) {
                        cmiDisplay.show(player);
                      } else {
                        cmiDisplay.update(player);
                      }
                    } catch (Throwable throwable2) {
                      throwable2.printStackTrace();
                    }
                  }

                  for (CMIHologramBatchGroup iconGroup :
                      cmiHologramBatchDisplay.getIcons().values()) {
                    this.updateIconScaleDirectionOffset(
                        userData, (CMIHologramBatchGroupDisplay) iconGroup);
                    if (hashSet.contains(CMIHologramUpdateType.Icon)) {
                      iconGroup.show(player);
                    } else {
                      iconGroup.update(player);
                    }
                  }

                  if (cmiHologramBatchDisplay.getBoard() != null) {
                    this.setBoardCenter(
                        userData, cmiHologramBatchDisplay.getBoard(), cmiHologramPage);
                    if (hashSet.contains(CMIHologramUpdateType.Billboard)) {
                      cmiHologramBatchDisplay.getBoard().show(player);
                    } else {
                      cmiHologramBatchDisplay.getBoard().update(player);
                    }
                  }
                });
          } else {
            if (this.getHologramBase().getAnimationSettings().getAutoRotate() != 0) {
              set.add(CMIHologramUpdateType.AutoRotate);
            }

            if (set.contains(CMIHologramUpdateType.Location)
                || set.contains(CMIHologramUpdateType.AutoRotate)) {
              CMILocation cmiLocation = this.getHologramBase().getLocationWithPitchYaw().clone();
              if (set.contains(CMIHologramUpdateType.AutoRotate)) {
                cmiLocation.setYaw(
                    (float)
                            (cmiLocation.getYaw()
                                + userData.getCurrentAutoRotateExtraAngle(
                                    this.getHologramBase().getAnimationSettings().getAutoRotate()))
                        % 360.0F);
              }

              for (CMIDisplay batchedDisplay : cmiHologramBatchDisplay.getDisplays().values()) {
                batchedDisplay.setLocation(cmiLocation);
              }

              for (CMIHologramBatchGroup iconGroup : cmiHologramBatchDisplay.getIcons().values()) {
                iconGroup.setLocation(cmiLocation);
              }

              if (cmiHologramBatchDisplay.getBoard() != null) {
                cmiHologramBatchDisplay.getBoard().setLocation(cmiLocation);
              }
            }

            this.updateCycle(
                player, cmiHologramBatchDisplay, displays, userData, cmiHologramPage, set);
            return CompletableFuture.completedFuture(null);
          }
        } else {
          return CompletableFuture.completedFuture(null);
        }
      } else {
        this.getHologramBase().removeData(uuid);
        return CompletableFuture.completedFuture(null);
      }
    }
  }

  private void updateCycle(
      Player player,
      CMIHologramBatchDisplay cmiHologramBatchDisplay,
      List<String> list,
      CMIHologramUserData cmiHologramUserData,
      CMIHologramPage cmiHologramPage,
      Set<CMIHologramUpdateType> set) {
    int value = 0;

    for (CMIDisplay cmiDisplay : cmiHologramBatchDisplay.getDisplays().values()) {
      if (cmiDisplay instanceof CMITextDisplay) {
        value++;
        if (CMIComponentBase.isValidVersion()) {
          ((CMITextDisplay) cmiDisplay).setText(createComponent(list));
        } else {
          ((CMITextDisplay) cmiDisplay).setText(list);
        }

        if (value == 2
            && !this.getHologramBase().getSettings().getBillboard().isCanHaveBackSide()) {
          cmiDisplay.destroy(player);
        } else {
          if (set.contains(CMIHologramUpdateType.Billboard)) {
            this.updateDisplaySettings(
                cmiHologramUserData, (CMITextDisplay) cmiDisplay, value == 1);
          }

          cmiDisplay.update(player);
        }
      }
    }

    if (set.contains(CMIHologramUpdateType.Billboard)
        || set.contains(CMIHologramUpdateType.AutoRotate)) {
      for (CMIHologramBatchGroup cmiHologramBatchGroup :
          cmiHologramBatchDisplay.getIcons().values()) {
        this.updateIconScaleDirectionOffset(
            cmiHologramUserData, (CMIHologramBatchGroupDisplay) cmiHologramBatchGroup);
        cmiHologramBatchGroup.update(player);
      }
    }

    if (cmiHologramBatchDisplay.getBoard() != null
        && (set.contains(CMIHologramUpdateType.Billboard)
            || set.contains(CMIHologramUpdateType.AutoRotate))) {
      this.setBoardCenter(cmiHologramUserData, cmiHologramBatchDisplay.getBoard(), cmiHologramPage);
      cmiHologramBatchDisplay.getBoard().update(player);
    }
  }

  private void updateBatch(CMIHologramUserData cmiHologramUserData, Player player) {
    int value = 0;
    if (cmiHologramUserData.getHologramBatch() instanceof CMIHologramBatchDisplay) {
      CMIHologramBatchDisplay cmiHologramBatchDisplay =
          (CMIHologramBatchDisplay) cmiHologramUserData.getHologramBatch();

      for (CMIDisplay cmiDisplay : cmiHologramBatchDisplay.getDisplays().values()) {
        if (cmiDisplay instanceof CMITextDisplay) {
          value++;
          this.updateDisplaySettings(cmiHologramUserData, (CMITextDisplay) cmiDisplay, value == 1);
          cmiDisplay.update(player);
        }
      }

      for (CMIHologramBatchGroup cmiHologramBatchGroup :
          cmiHologramBatchDisplay.getIcons().values()) {
        this.updateIconScaleDirectionOffset(
            cmiHologramUserData, (CMIHologramBatchGroupDisplay) cmiHologramBatchGroup);
        cmiHologramBatchGroup.update(player);
      }

      CMIBlockDisplay cmiBlockDisplay = cmiHologramBatchDisplay.getBoard();
      if (cmiBlockDisplay != null) {
        this.setBoardCenter(
            cmiHologramUserData, cmiBlockDisplay, this.getHologramBase().getPlayerPage(player));
        cmiBlockDisplay.update(player);
      }
    }
  }

  private CompletableFuture<CMIItemDisplay> createIcon(
      Player player, CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay) {
    CompletableFuture completableFuture = new CompletableFuture();
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        this.getHologramBase().getLocation(),
        () -> {
          try {
            CMILocation cmiLocation = this.getHologramBase().getLocationWithPitchYaw().clone();
            CMIItemDisplay cmiItemDisplay = new CMIItemDisplay(cmiLocation);
            cmiItemDisplay.setOffset(cmiHologramBatchGroupDisplay.getOffset());
            if (!this.getHologramBase()
                .getPlayersFromVisibilityRange()
                .contains(player.getUniqueId())) {
              return;
            }

            completableFuture.complete(cmiItemDisplay);
          } catch (Exception exception) {
            completableFuture.completeExceptionally(exception);
          }
        });
    return completableFuture;
  }

  private void updateItemDisplay(
      CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay,
      final Player player,
      Set<CMIHologramUpdateType> set) {
    final CMIHologramLineIcon cmiHologramLineIcon =
        (CMIHologramLineIcon) cmiHologramBatchGroupDisplay.getLine();
    final CMIItemDisplay cmiItemDisplay =
        (CMIItemDisplay) cmiHologramBatchGroupDisplay.getDisplay();
    if (cmiItemDisplay != null) {
      CMIAsyncHead cmiAsyncHead =
          new CMIAsyncHead() {
            public void afterAsyncUpdate(ItemStack itemStack) {
              cmiHologramLineIcon.setIcon(itemStack);
              cmiItemDisplay.setItemStack(itemStack);
              cmiItemDisplay.update(player);
            }
          };
      cmiAsyncHead.setForce(true);
      ItemStack newIcon = cmiHologramLineIcon.getIcon(player, cmiAsyncHead);
      if (newIcon != null) {
        ItemStack previousIcon = cmiItemDisplay.getItemStack();
        if (cmiItemDisplay.getItemStack() != null && previousIcon.equals(newIcon)) {
          if (set.contains(CMIHologramUpdateType.Icon)) {
            this.modifyIcon(cmiHologramBatchGroupDisplay, this.getHologramBase().getData(player));
            cmiItemDisplay.update(player);
          }
        } else {
          cmiItemDisplay.setItemStack(newIcon);
          this.modifyIcon(cmiHologramBatchGroupDisplay, this.getHologramBase().getData(player));
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              cmiItemDisplay.getLocation(),
              () -> {
                if (previousIcon == null
                    || previousIcon.getType().isAir()
                    || previousIcon != null && !previousIcon.equals(newIcon)) {
                  if (!this.getHologramBase()
                      .getPlayersFromVisibilityRange()
                      .contains(player.getUniqueId())) {
                    return;
                  }

                  cmiItemDisplay.show(player);
                } else {
                  cmiItemDisplay.update(player);
                }
              });
        }
      } else {
        Object object = null;
        cmiItemDisplay.setItemStack((ItemStack) object);
        CMIScheduler.runAtLocation(
            CMI.getInstance(),
            cmiItemDisplay.getLocation(),
            () -> {
              if (this.getHologramBase()
                  .getPlayersFromVisibilityRange()
                  .contains(player.getUniqueId())) {
                cmiItemDisplay.show(player);
              }
            });
      }
    }
  }

  private void modifyIcon(
      CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay,
      CMIHologramUserData cmiHologramUserData) {
    CMIItemDisplay cmiItemDisplay = (CMIItemDisplay) cmiHologramBatchGroupDisplay.getDisplay();
    CMIBillboard cmiBillboard = this.getHologramBase().getIconSettings().getBillboard();
    if (cmiBillboard == null) {
      cmiBillboard = this.getHologramBase().getSettings().getBillboard();
    }

    cmiItemDisplay.setBillboard(cmiBillboard.getBillboard());
    cmiItemDisplay.setItemDisplayTransform(ItemDisplayTransform.FIXED);
    cmiItemDisplay.setRange(this.getHologramBase().getSettings().getVisibilityRange());
    this.updateIconScaleDirectionOffset(cmiHologramUserData, cmiHologramBatchGroupDisplay);
  }

  private boolean isEmptyLines(List<String> list) {
    return list.size() == 1 && ((String) list.get(0)).equals(" ");
  }

  private CMITextDisplay createDisplay(
      CMIHologramUserData cmiHologramUserData, List<String> list, boolean flag) {
    if (this.isEmptyLines(list)) {
      return new CMITextDisplay(null);
    }

    CMITextDisplay cmiTextDisplay =
        new CMITextDisplay(this.getHologramBase().getLocationWithPitchYaw().clone());
    if (CMIComponentBase.isValidVersion()) {
      cmiTextDisplay.setText(createComponent(list));
    } else {
      cmiTextDisplay.setText(list);
    }

    this.updateDisplaySettings(cmiHologramUserData, cmiTextDisplay, flag);
    return cmiTextDisplay;
  }

  private void updateDisplaySettings(
      CMIHologramUserData cmiHologramUserData, CMITextDisplay cmiTextDisplay, boolean flag) {
    if (cmiTextDisplay != null && cmiTextDisplay.getDisplay() != null) {
      CMIHologram cmiHologram = this.getHologramBase();
      CMIHologramTextSettings cmiHologramTextSettings = cmiHologram.getTextSettings();
      CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
      cmiTextDisplay.setShadowed(cmiHologramTextSettings.isShadowed());
      cmiTextDisplay.setLineWidth(cmiHologramTextSettings.getLineWidth());
      cmiTextDisplay.setBillboard(cmiHologramSettings.getBillboard().getBillboard());
      cmiTextDisplay.setAligment(cmiHologramTextSettings.getTextAlignment().getTextAlignment());
      cmiTextDisplay.setRange(cmiHologramSettings.getVisibilityRange());
      if (cmiHologramSettings.getSkyLevel() > -1 || cmiHologramSettings.getBlockLevel() > -1) {
        cmiTextDisplay.setBrightness(
            cmiHologramSettings.getSkyLevel(), cmiHologramSettings.getBlockLevel());
      }

      cmiTextDisplay.setSeeThrough(cmiHologramTextSettings.isSeeThrough());
      float floatValue = (float) cmiHologramSettings.getYaw();
      if (this.getHologramBase().getAnimationSettings().getAutoRotate() != 0) {
        floatValue =
            (float)
                (floatValue
                    + this.getHologramBase().getAnimationSettings().getAutoRotate()
                        / 100.0
                        * cmiHologramUserData.getTick());
      }

      cmiTextDisplay.setFacing((float) cmiHologramSettings.getPitch(), floatValue);
      CMIVector3D cmiVector3D = cmiHologramSettings.getOffset().clone();
      Transformation transformation = cmiTextDisplay.getTransformation();
      float zOffset = 0.001F;
      if (this.getHologramBase().getBoardSettings().isEnabled()) {
        zOffset =
            (float)
                (this.getHologramBase().getBoardSettings().getExtraDimensions().getZ() / 2.0
                    + 0.001F);
      }

      if (this.getHologramBase().getAnimationSettings().getAutoRotate() > 0) {
        zOffset += 0.01F;
      }

      if (cmiHologramSettings.getBillboard().isCanHaveBackSide() && !flag) {
        transformation
            .getLeftRotation()
            .set(new AxisAngle4f((float) Math.toRadians(180.0), 0.0F, 1.0F, 0.0F));
        cmiVector3D = cmiVector3D.plus(0.0, 0.0, -zOffset);
      } else {
        cmiVector3D = cmiVector3D.plus(0.0, 0.0, zOffset);
      }

      if (cmiHologramUserData.isFadeAnimationActive()) {
        cmiVector3D =
            cmiVector3D.multiply(
                (float)
                    cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base));
      }

      transformation
          .getTranslation()
          .set((float) cmiVector3D.getX(), (float) cmiVector3D.getY(), (float) cmiVector3D.getZ());
      cmiTextDisplay.setTransformation(transformation);
      this.updateTextAnimation(cmiHologramUserData, cmiTextDisplay);
    }
  }

  private CMIBlockDisplay createBoard(
      CMIHologramUserData cmiHologramUserData, CMIHologramPage cmiHologramPage) {
    if (cmiHologramPage == null) {
      return null;
    }

    CMILocation cmiLocation = this.getHologramBase().getLocationWithPitchYaw().clone();
    cmiLocation.add(0.0, new Random().nextFloat() / 1000.0F, 0.0);
    CMIBlockDisplay cmiBlockDisplay = new CMIBlockDisplay(cmiLocation);
    BlockData blockData =
        this.getHologramBase().getBoardSettings().getMaterial().getMaterial().createBlockData();
    cmiBlockDisplay.setBlockData(blockData);
    cmiBlockDisplay.setBillboard(
        this.getHologramBase().getSettings().getBillboard().getBillboard());
    cmiBlockDisplay.setRange(this.getHologramBase().getSettings().getVisibilityRange());
    cmiBlockDisplay.setBrightness(
        this.getHologramBase().getSettings().getSkyLevel(),
        this.getHologramBase().getSettings().getBlockLevel());
    this.setBoardCenter(cmiHologramUserData, cmiBlockDisplay, cmiHologramPage);
    return cmiBlockDisplay;
  }

  private double getBoardWidth(
      CMIHologramUserData cmiHologramUserData, CMIHologramPage cmiHologramPage) {
    double number =
        cmiHologramPage.getWidth() / 74.0 * 2.0
            + this.getHologramBase().getBoardSettings().getExtraWidth();
    return number * this.getHologramBase().getSettings().getScale().getX();
  }

  private double getBoardHeight(
      CMIHologramUserData cmiHologramUserData, CMIHologramPage cmiHologramPage) {
    return cmiHologramPage.getHeight() + this.getHologramBase().getBoardSettings().getExtraHeight();
  }

  private double getBoardDepth(
      CMIHologramUserData cmiHologramUserData, CMIHologramPage cmiHologramPage) {
    return this.getHologramBase().getBoardSettings().getExtraDepth();
  }

  private void setBoardCenter(
      CMIHologramUserData cmiHologramUserData,
      CMIBlockDisplay cmiBlockDisplay,
      CMIHologramPage cmiHologramPage) {
    if (cmiHologramPage != null
        && cmiBlockDisplay != null
        && cmiBlockDisplay.getDisplayEntity() != null) {
      float floatValue = (float) this.getBoardWidth(cmiHologramUserData, cmiHologramPage);
      float boardHeight = (float) this.getBoardHeight(cmiHologramUserData, cmiHologramPage);
      float boardDepth = (float) this.getBoardDepth(cmiHologramUserData, cmiHologramPage);
      CMIHologramBoardSettings cmiHologramBoardSettings = this.getHologramBase().getBoardSettings();
      CMIHologramSettings cmiHologramSettings = this.getHologramBase().getSettings();
      CMIVector3D cmiVector3D = cmiHologramBoardSettings.getDirection();
      float dirX = (float) cmiVector3D.getX();
      float dirY = (float) cmiVector3D.getY();
      float dirZ = (float) cmiVector3D.getZ();
      Transformation transformation = cmiBlockDisplay.getTransformation();
      Quaternionf quaternionf =
          new Quaternionf()
              .rotateXYZ(
                  (float) Math.toRadians(dirX),
                  (float) Math.toRadians(dirY),
                  (float) Math.toRadians(dirZ));
      Vector3f xAxis = new Vector3f(1.0F, 0.0F, 0.0F).rotate(quaternionf);
      Vector3f yAxis = new Vector3f(0.0F, 1.0F, 0.0F).rotate(quaternionf);
      Vector3f zAxis = new Vector3f(0.0F, 0.0F, 1.0F).rotate(quaternionf);
      float sizeX =
          Math.abs(xAxis.x) * floatValue
              + Math.abs(xAxis.y) * boardHeight
              + Math.abs(xAxis.z) * boardDepth;
      float sizeY =
          Math.abs(yAxis.x) * floatValue
              + Math.abs(yAxis.y) * boardHeight
              + Math.abs(yAxis.z) * boardDepth;
      float sizeZ =
          Math.abs(zAxis.x) * floatValue
              + Math.abs(zAxis.y) * boardHeight
              + Math.abs(zAxis.z) * boardDepth;
      Vector3f halfExtent = new Vector3f(0.5F, 0.5F, 0.5F).negate();
      halfExtent.mul(sizeX, sizeY, sizeZ);
      halfExtent.rotate(quaternionf);
      if (cmiHologramUserData.isFadeAnimationActive()) {
        double number = cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base);
        sizeX *= (float) number;
        sizeY *= (float) number;
        sizeZ *= (float) number;
      }

      float baseY = halfExtent.y;
      float heightDelta = (float) (boardHeight - cmiHologramBoardSettings.getExtraHeight());
      baseY += heightDelta / 2.0F;
      float translateX = (float) (halfExtent.x + cmiHologramBoardSettings.getOffset().getX());
      float translateY = (float) (baseY + cmiHologramBoardSettings.getOffset().getY());
      float translateZ = (float) (halfExtent.z + cmiHologramBoardSettings.getOffset().getZ());
      translateX = (float) (translateX + cmiHologramSettings.getOffset().getX());
      translateY = (float) (translateY + cmiHologramSettings.getOffset().getY());
      translateZ = (float) (translateZ + cmiHologramSettings.getOffset().getZ());
      if (cmiHologramUserData.isFadeAnimationActive()) {
        float fadeScale =
            (float) cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base);
        translateX *= fadeScale;
        translateY *= fadeScale;
        translateZ *= fadeScale;
      }

      transformation.getTranslation().set(translateX, translateY, translateZ);
      transformation.getLeftRotation().set(quaternionf);
      transformation.getScale().set(sizeX, sizeY, sizeZ);
      cmiBlockDisplay.setTransformation(transformation);
    }
  }

  private void updateTextAnimation(
      CMIHologramUserData cmiHologramUserData, CMITextDisplay cmiTextDisplay) {
    cmiTextDisplay.setBackgroundColor(this.getTextBackground(cmiHologramUserData));
    cmiTextDisplay.setWidth(this.getTextWidth(cmiHologramUserData));
    cmiTextDisplay.setHeight(this.getTextHeight(cmiHologramUserData));
    cmiTextDisplay.setTextOpacity(this.getTextAlpha(cmiHologramUserData));
  }

  private Color getTextBackground(CMIHologramUserData cmiHologramUserData) {
    return this.getHologramBase()
        .getTextSettings()
        .getBackgroundColor()
        .getRGBColor()
        .setAlpha(this.getHologramBase().getTextSettings().getBackgroundAlpha());
  }

  private byte getTextAlpha(CMIHologramUserData cmiHologramUserData) {
    double number = this.getHologramBase().getTextSettings().getTextAlpha();
    return (byte) number;
  }

  private double getTextWidth(CMIHologramUserData cmiHologramUserData) {
    double number = this.getHologramBase().getSettings().getScale().getX();
    if (cmiHologramUserData.isFadeAnimationActive()) {
      number *= (float) cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base);
    }

    return number;
  }

  private double getTextHeight(CMIHologramUserData cmiHologramUserData) {
    double number = this.getHologramBase().getSettings().getScale().getY();
    if (cmiHologramUserData.isFadeAnimationActive()) {
      number *= (float) cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base);
    }

    return number;
  }

  private void updateIconScaleDirectionOffset(
      CMIHologramUserData cmiHologramUserData,
      CMIHologramBatchGroupDisplay cmiHologramBatchGroupDisplay) {
    CMIItemDisplay cmiItemDisplay = (CMIItemDisplay) cmiHologramBatchGroupDisplay.getDisplay();
    if (cmiItemDisplay != null && cmiItemDisplay.getDisplay() != null) {
      CMIBillboard cmiBillboard = this.getHologramBase().getIconSettings().getBillboard();
      if (cmiBillboard == null) {
        cmiBillboard = this.getHologramBase().getSettings().getBillboard();
      }

      cmiItemDisplay.setScale(this.getIconScale(cmiHologramUserData));
      float floatValue = this.getHologramBase().getIconSettings().getPitch();
      if (floatValue > 90.0F) {
        floatValue = 180.0F - floatValue;
      } else if (floatValue < -90.0F) {
        floatValue = -180.0F - floatValue;
      }

      float yaw = this.getHologramBase().getIconSettings().getYaw();
      int value = this.getHologramBase().getIconSettings().getRoll();
      cmiItemDisplay.setFacing(floatValue, yaw, value);
      cmiItemDisplay.setOffset(
          this.getIconOffset(cmiHologramBatchGroupDisplay.getOffset(), cmiHologramUserData));
    }
  }

  private CMIVector3D getIconScale(CMIHologramUserData cmiHologramUserData) {
    CMIVector3D cmiVector3D =
        this.getHologramBase().getIconSettings().getScale().clone().multiply(0.5);
    cmiVector3D.setX(cmiVector3D.getX() * this.getHologramBase().getSettings().getScale().getX());
    cmiVector3D.setY(cmiVector3D.getY() * this.getHologramBase().getSettings().getScale().getY());
    if (cmiHologramUserData.isFadeAnimationActive()) {
      cmiVector3D =
          cmiVector3D.multiply(
              (float) cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base));
    }

    return cmiVector3D;
  }

  private CMIVector3D getIconPositionOffset(int value, int targetIndex) {
    double number = this.getHologramBase().getLineHeight() / 1.8;
    value = targetIndex - value;
    double yPosition = value * this.getHologramBase().getLineHeight() + number;
    return new CMIVector3D(0.0, yPosition, 0.0);
  }

  private CMIVector3D getIconOffset(
      CMIVector3D cmiVector3D, CMIHologramUserData cmiHologramUserData) {
    CMIVector3D finalOffset = this.getFinalIconOffset(cmiVector3D).clone();
    if (cmiHologramUserData.isFadeAnimationActive()) {
      finalOffset =
          finalOffset.multiply(
              (float) cmiHologramUserData.getAnimationCurrentValue(CMIHologramAnimationType.Base));
    }

    return finalOffset;
  }

  private CMIVector3D getFinalIconOffset(CMIVector3D cmiVector3D) {
    CMIVector3D iconOffset = this.getHologramBase().getIconSettings().getOffset().clone();
    iconOffset = this.getHologramBase().getSettings().getOffset().clone().plus(iconOffset);
    iconOffset =
        iconOffset.plus(0.0, 0.0, this.getHologramBase().getBoardSettings().getExtraDepth() / 2.0);
    return cmiVector3D.clone().plus(iconOffset.getX(), iconOffset.getY(), iconOffset.getZ());
  }

  private static CMIComponentBase createComponent(List<String> list) {
    CMIComponentBase cmiComponentBase = CMIComponentBase.builder(true);
    int value = 0;

    for (String string : list) {
      cmiComponentBase.add(value, new CMIComponentText(string));
      value++;
    }

    return cmiComponentBase;
  }

  public synchronized void resetBoardCache() {
    for (var entry : this.getHologramBase().userData.entrySet()) {
      CMIHologramBatch cmiHologramBatch =
          ((CMIHologramUserData) entry.getValue()).getHologramBatch();
      if (cmiHologramBatch instanceof CMIHologramBatchDisplay) {
        ((CMIHologramBatchDisplay) ((CMIHologramUserData) entry.getValue()).getHologramBatch())
            .destroyBoard(CMIUser.getOnlinePlayer((UUID) entry.getKey()));
      }
    }
  }
}
