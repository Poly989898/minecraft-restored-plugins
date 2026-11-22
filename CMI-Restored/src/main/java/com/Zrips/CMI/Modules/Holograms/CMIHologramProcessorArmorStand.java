package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIAsyncHead;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CMIHologramProcessorArmorStand extends CMIHologramProcessor {
  public CMIHologramProcessorArmorStand(CMIHologram cmiHologram) {
    super(cmiHologram);
  }

  @Override
  void onPageChange(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.getHologramBase().getData(uuid);
    if (cmiHologramUserData != null && cmiHologramUserData.getHologramBatch() != null) {
      for (CMIHologramBatchGroup cmiHologramBatchGroup :
          cmiHologramUserData.getHologramBatch().getIcons().values()) {
        cmiHologramBatchGroup.destroy(Bukkit.getPlayer(uuid));
      }

      cmiHologramUserData.getHologramBatch().getIcons().clear();
    }
  }

  @Override
  public CompletableFuture<Void> updateBillboard(UUID uuid, Set<CMIHologramUpdateType> set) {
    if (this.isHiding(set)) {
      CMIHologramUserData cmiHologramUserData = this.getHologramBase().getData(uuid);
      if (cmiHologramUserData != null) {
        if (this.getHologramBase().getHologramType(uuid).equals(CMIHologramType.ArmorStand)) {
          cmiHologramUserData.setHidden(true);
          this.getHologramBase().removeFromCache(uuid, false);
        }

        CMIHologramHoverData.getData(uuid).setHoveringLine(-1);
      }

      return CompletableFuture.completedFuture(null);
    } else {
      if (this.getHologramBase().getLocation().getWorld() == null) {
        return CompletableFuture.completedFuture(null);
      }

      Player player = Bukkit.getPlayer(uuid);
      if (player != null
          && player.isOnline()
          && player.getWorld().equals(this.getLocation().getWorld())) {
        CMIHologramUserData userData = this.getHologramBase().getData(uuid);
        var list = this.getHologramBase().getLinesByPlayerPage(uuid);
        if (list.isEmpty()) {
          this.getHologramBase().removeFromCache(uuid);
          return CompletableFuture.completedFuture(null);
        }

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
          this.getHologramBase().removeFromCache(uuid);
          return CompletableFuture.completedFuture(null);
        }

        CMIHologramPage cmiHologramPage = this.getHologramBase().getPages().getPage(value);
        if (cmiHologramPage == null) {
          return CompletableFuture.completedFuture(null);
        }

        ArrayList existingLines = new ArrayList();
        CMIHologramBatch cmiHologramBatch = userData.getHologramBatch();
        if (!(cmiHologramBatch instanceof CMIHologramBatchArmorStand)) {
          if (cmiHologramBatch != null) {
            cmiHologramBatch.destroy(player);
          }

          cmiHologramBatch = new CMIHologramBatchArmorStand();
          userData.setHologramBatch(cmiHologramBatch);
        }

        CMIHologramBatchArmorStand cmiHologramBatchArmorStand =
            (CMIHologramBatchArmorStand) cmiHologramBatch;
        cmiHologramPage.setLineCount(arrayList.size());
        if (!set.contains(CMIHologramUpdateType.Show)
            && !set.contains(CMIHologramUpdateType.InstantShow)) {
          if (userData.isHidden()) {
            return CompletableFuture.completedFuture(null);
          }
        } else {
          userData.setHidden(false);
        }

        int addedCount = 0;
        int removedCount = 0;
        ArrayList<CompletableFuture<Void>> futures = new ArrayList();

        for (CMIHologramLine line : arrayList) {
          removedCount++;
          switch (line.getType()) {
            case plainText:
              addedCount++;
              existingLines.add(this.processTextLine(player, line));
              CMIHologramBatchGroup cmiHologramBatchGroup =
                  cmiHologramBatchArmorStand.getIcon(removedCount);
              if (cmiHologramBatchGroup != null) {
                cmiHologramBatchGroup.destroy(player);
                cmiHologramBatchGroup.setEnabled(false);
              }
              break;
            case icon:
            case staticIcon:
              CMIHologramLineIcon cmiHologramLineIcon =
                  (CMIHologramLineIcon) line.getLinePortions().get(0).getBaseLineData();
              CMIHologramBatchGroup batchGroup = cmiHologramBatchArmorStand.getIcon(removedCount);
              CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand = null;
              if (batchGroup != null) {
                cmiHologramBatchGroupArmorStand = (CMIHologramBatchGroupArmorStand) batchGroup;
              } else {
                CMIVector3D cmiVector3D =
                    this.getIconPositionOffset(removedCount, cmiHologramPage.getLineCount());
                cmiHologramBatchGroupArmorStand =
                    new CMIHologramBatchGroupArmorStand(null, cmiHologramLineIcon);
                cmiHologramBatchGroupArmorStand.setOffset(cmiVector3D);
                if (set.contains(CMIHologramUpdateType.Show)
                    || set.contains(CMIHologramUpdateType.InstantShow)) {
                  FakeEntityInfo fakeEntityInfo =
                      this.createIcon(
                          player,
                          cmiHologramBatchGroupArmorStand,
                          cmiHologramPage.getLineCount() - removedCount);
                  if (cmiHologramBatchArmorStand.getIcon(removedCount) == null) {
                    cmiHologramBatchGroupArmorStand.setDisplay(fakeEntityInfo);
                    cmiHologramBatchArmorStand.addIcon(
                        removedCount, cmiHologramBatchGroupArmorStand);
                  }
                }
              }

              this.updateItemDisplay(cmiHologramBatchGroupArmorStand, player, set);
              existingLines.add(addedCount == 0 ? " " : "");
              addedCount++;
              break;
            case image:
              CMIHologramLineImage cmiHologramLineImage =
                  (CMIHologramLineImage) line.getLinePortions().get(0).getBaseLineData();
              var imageLines = cmiHologramLineImage.getImage();
              if (imageLines != null) {
                existingLines.addAll(imageLines);
                addedCount += imageLines.size();
              }
          }
        }

        for (var entry : cmiHologramBatchArmorStand.getIcons().entrySet()) {
          if ((Integer) entry.getKey() > cmiHologramPage.getLineCount()) {
            ((CMIHologramBatchGroup) entry.getValue()).destroy(player);
            ((CMIHologramBatchGroup) entry.getValue()).setEnabled(false);
          }
        }

        if (existingLines.isEmpty()) {
          return CompletableFuture.completedFuture(null);
        }

        double number = 0.0;
        double baseY = number;
        if (cmiHologramBatchArmorStand.getDisplays().size() > existingLines.size()) {
          var displays = cmiHologramBatchArmorStand.getDisplays();
          int missing = displays.size() - existingLines.size();

          for (int index = 0; index < missing && !displays.isEmpty(); index++) {
            FakeEntityInfo display = (FakeEntityInfo) displays.remove(0);
            CMI.getInstance().getNMS().removeHologram(player, display.getId());
          }
        }

        if (cmiHologramBatchArmorStand.getDisplays().isEmpty()
                && (set.contains(CMIHologramUpdateType.Show)
                    || set.contains(CMIHologramUpdateType.InstantShow))
            || !cmiHologramBatchArmorStand.getDisplays().isEmpty()
                && cmiHologramBatchArmorStand.getDisplays().size() < existingLines.size()) {
          var displayCopy = new ArrayList<>(cmiHologramBatchArmorStand.getDisplays());
          if (!displayCopy.isEmpty()) {
            for (int extraIndex = 0;
                extraIndex < existingLines.size() - displayCopy.size();
                extraIndex++) {
              cmiHologramBatchArmorStand.getDisplays().add(0, new FakeEntityInfo());
            }
          }

          if (cmiHologramBatchArmorStand.getDisplays().size() < existingLines.size()) {
            cmiHologramBatchArmorStand.addDisplay(existingLines.size() - 1, new FakeEntityInfo());
          }

          futures.add(
              CMIScheduler.runAtLocation(
                  CMI.getInstance(),
                  this.getHologramBase().getLocation(),
                  () -> {
                    try {
                      ConcurrentHashMap concurrentHashMap =
                          this.createDisplay(player, userData, existingLines, -baseY);

                      for (Entry iconEntry : ((java.util.Map<?, ?>) concurrentHashMap).entrySet()) {
                        cmiHologramBatchArmorStand.addDisplay(
                            (Integer) iconEntry.getKey(), (FakeEntityInfo) iconEntry.getValue());
                      }
                    } catch (Throwable throwable) {
                      throwable.printStackTrace();
                    }
                  }));
        }

        if (set.contains(CMIHologramUpdateType.Location)) {
          int displayCount = cmiHologramBatchArmorStand.getDisplays().size();

          for (FakeEntityInfo displayItem : cmiHologramBatchArmorStand.getDisplays()) {
            displayCount--;
            displayItem.setLocation(
                this.positionAdjustedByLine(displayCount - 1).clone().add(0.0, -0.05, 0.0));
          }

          for (var iconPair : cmiHologramBatchArmorStand.getIcons().entrySet()) {
            Location location =
                this.getIconRealPosition(
                    ((CMIHologramBatchGroup) iconPair.getValue()).getLine().getType(),
                    cmiHologramPage.getLineCount() - (Integer) iconPair.getKey());
            ((CMIHologramBatchGroupArmorStand) iconPair.getValue())
                .getDisplay()
                .setLocation(location);
          }
        }

        cmiHologramPage.setHeight(
            cmiHologramPage.getLineCount() * this.getHologramBase().getLineHeight());
        if (!futures.isEmpty()) {
          CompletableFuture completableFuture =
              CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
          return completableFuture.thenRun(
              () -> {
                boolean flag = false;
                this.updateDisplaySettings(userData, cmiHologramBatchArmorStand);

                for (CMIHologramBatchGroup group : cmiHologramBatchArmorStand.getIcons().values()) {
                  group.show(player);
                }
              });
        }

        var displayList = cmiHologramBatchArmorStand.getDisplays();
        int displaySize = displayList.size();

        for (FakeEntityInfo displayEntry : displayList) {
          displaySize--;
          String string = (String) existingLines.get(displayList.size() - displaySize - 1);
          CMI.getInstance()
              .getNMS()
              .updateHologram(
                  player,
                  new CMIDataWatcher(displayEntry.getId(), displayEntry.getEntity(), null),
                  CMIChatColor.translate(string),
                  null);
          if (set.contains(CMIHologramUpdateType.Location)) {
            CMILib.getInstance()
                .getReflectionManager()
                .superficialEntityTeleport(
                    player, displayEntry.getEntity(), displayEntry.getLocation());
          }
        }

        for (var iconEntryAlt : cmiHologramBatchArmorStand.getIcons().entrySet()) {
          if (set.contains(CMIHologramUpdateType.Location)) {
            FakeEntityInfo displayInfo =
                ((CMIHologramBatchGroupArmorStand) iconEntryAlt.getValue()).getDisplay();
            Location displayLocation = displayInfo.getLocation().clone();
            CMILib.getInstance()
                .getReflectionManager()
                .superficialEntityTeleport(player, displayInfo.getEntity(), displayLocation);
          }
        }

        return CompletableFuture.completedFuture(null);
      } else {
        this.getHologramBase().removeData(uuid);
        return CompletableFuture.completedFuture(null);
      }
    }
  }

  private Location getIconRealPosition(CMIHoloLineType cmiHoloLineType, int value) {
    Location location = this.positionAdjustedByLine(value);
    if (cmiHoloLineType.equals(CMIHoloLineType.staticIcon)) {
      location.add(0.0, -0.85, 0.0);
    }

    location.add(this.getFinalIconOffset());
    return location;
  }

  private Location positionAdjustedByLine(int value) {
    CMILocation cmiLocation = this.getHologramBase().getLocation().clone();
    cmiLocation.add(0.0, value * 0.25, 0.0);
    return cmiLocation;
  }

  private FakeEntityInfo createIcon(
      Player player, CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand, int value) {
    return cmiHologramBatchGroupArmorStand.getLine().getType() == CMIHoloLineType.icon
        ? this.createFloatingIcon(player, cmiHologramBatchGroupArmorStand, value)
        : this.createStaticIcon(player, cmiHologramBatchGroupArmorStand, value);
  }

  private FakeEntityInfo createIcon(
      Player player,
      CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand,
      Location location) {
    return cmiHologramBatchGroupArmorStand.getLine().getType() == CMIHoloLineType.icon
        ? this.createFloatingIcon(player, cmiHologramBatchGroupArmorStand, location)
        : this.createStaticIcon(player, cmiHologramBatchGroupArmorStand, location);
  }

  private FakeEntityInfo createFloatingIcon(
      Player player, CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand, int value) {
    return !this.getHologramBase().getPlayersFromVisibilityRange().contains(player.getUniqueId())
        ? null
        : this.createFloatingIcon(
            player, cmiHologramBatchGroupArmorStand, this.positionAdjustedByLine(value));
  }

  private FakeEntityInfo createFloatingIcon(
      Player player,
      CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand,
      Location location) {
    if (!player.getWorld().equals(this.getLocation().getWorld())) {
      return null;
    }

    CMIHologramLineIcon cmiHologramLineIcon =
        (CMIHologramLineIcon) cmiHologramBatchGroupArmorStand.getLine();
    CMIDataWatcher cmiDataWatcher =
        CMI.getInstance()
            .getNMS()
            .makeFloatingIcon(player, location.clone(), cmiHologramLineIcon.getIcon(player));
    FakeEntityInfo fakeEntityInfo =
        new FakeEntityInfo(cmiDataWatcher.getId(), player.getUniqueId(), location);
    fakeEntityInfo.setObjectSecond(cmiHologramLineIcon.getIcon(player));
    fakeEntityInfo.setEntity(cmiDataWatcher.getStand());
    if (cmiDataWatcher != null) {
      cmiDataWatcher.setLoc(location);
    }

    return fakeEntityInfo;
  }

  private FakeEntityInfo createStaticIcon(
      Player player, CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand, int value) {
    return !this.getHologramBase().getPlayersFromVisibilityRange().contains(player.getUniqueId())
        ? null
        : this.createStaticIcon(
            player,
            cmiHologramBatchGroupArmorStand,
            this.getIconRealPosition(cmiHologramBatchGroupArmorStand.getLine().getType(), value));
  }

  private FakeEntityInfo createStaticIcon(
      Player player,
      CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand,
      Location location) {
    if (!player.getWorld().equals(this.getLocation().getWorld())) {
      return null;
    }

    Location displayLocation = location.clone();
    displayLocation.add(-0.05, 0.3, 0.2);
    CMIHologramLineIcon cmiHologramLineIcon =
        (CMIHologramLineIcon) cmiHologramBatchGroupArmorStand.getLine();
    CMIDataWatcher cmiDataWatcher =
        CMI.getInstance()
            .getNMS()
            .spawnHologram(player, displayLocation, null, cmiHologramLineIcon.getIcon(player));
    FakeEntityInfo fakeEntityInfo =
        new FakeEntityInfo(cmiDataWatcher.getId(), player.getUniqueId(), location);
    CMILib.getInstance()
        .getReflectionManager()
        .superficialEntityTeleport(player, cmiDataWatcher.getStand(), location.clone());
    fakeEntityInfo.setObjectSecond(cmiHologramLineIcon.getIcon(player));
    fakeEntityInfo.setEntity(cmiDataWatcher.getStand());
    return fakeEntityInfo;
  }

  private CMIVector3D getIconPositionOffset(int value, int index) {
    double number = this.getHologramBase().getLineHeight() / 1.8;
    value = index - value;
    double offsetY = value * this.getHologramBase().getLineHeight() + number;
    return new CMIVector3D(0.0, offsetY, 0.0);
  }

  private void updateItemDisplay(
      final CMIHologramBatchGroupArmorStand cmiHologramBatchGroupArmorStand,
      final Player player,
      Set<CMIHologramUpdateType> set) {
    final CMIHologramLineIcon cmiHologramLineIcon =
        (CMIHologramLineIcon) cmiHologramBatchGroupArmorStand.getLine();
    final FakeEntityInfo fakeEntityInfo = cmiHologramBatchGroupArmorStand.getDisplay();
    if (fakeEntityInfo != null) {
      CMIAsyncHead cmiAsyncHead =
          new CMIAsyncHead() {
            public void afterAsyncUpdate(ItemStack itemStack) {
              cmiHologramLineIcon.setIcon(itemStack);
              fakeEntityInfo.setObjectSecond(itemStack);
              cmiHologramBatchGroupArmorStand.update(player);
            }
          };
      cmiAsyncHead.setForce(true);
      ItemStack icon = cmiHologramLineIcon.getIcon(player, cmiAsyncHead);
      if (icon != null) {
        Object object = fakeEntityInfo.getObjectSecond();
        ItemStack iconItem = object instanceof ItemStack ? (ItemStack) object : null;
        if (iconItem == null || !iconItem.equals(icon)) {
          fakeEntityInfo.setObjectSecond(icon);
          if (!this.getHologramBase()
              .getPlayersFromVisibilityRange()
              .contains(player.getUniqueId())) {
            CMI.getInstance().getNMS().removeHologram(player, fakeEntityInfo.getId());
          } else {
            if (iconItem == null || iconItem.getType().isAir()) {
              FakeEntityInfo display =
                  this.createIcon(
                      player,
                      cmiHologramBatchGroupArmorStand,
                      fakeEntityInfo.getLocation().clone());
              if (display != null) {
                fakeEntityInfo.setEntity(display.getEntity());
                fakeEntityInfo.setId(display.getId());
              }
            } else if (iconItem != null && !iconItem.equals(icon)) {
              CMI.getInstance().getNMS().removeHologram(player, fakeEntityInfo.getId());
              FakeEntityInfo displayAlt =
                  this.createIcon(
                      player,
                      cmiHologramBatchGroupArmorStand,
                      fakeEntityInfo.getLocation().clone());
              if (displayAlt != null) {
                fakeEntityInfo.setEntity(displayAlt.getEntity());
                fakeEntityInfo.setId(displayAlt.getId());
              }
            }
          }
        }
      } else {
        Object iconObject = null;
        fakeEntityInfo.setObjectSecond(iconObject);
        if (this.getHologramBase().getPlayersFromVisibilityRange().contains(player.getUniqueId())) {
          cmiHologramBatchGroupArmorStand.destroy(player);
        }
      }
    }
  }

  private ConcurrentHashMap<Integer, FakeEntityInfo> createDisplay(
      Player player, CMIHologramUserData cmiHologramUserData, List<String> list, double number) {
    ConcurrentHashMap<Integer, FakeEntityInfo> concurrentHashMap = new ConcurrentHashMap();
    if (!player.getWorld().equals(this.getLocation().getWorld())) {
      return concurrentHashMap;
    }

    CMIHologramBatchArmorStand cmiHologramBatchArmorStand =
        (CMIHologramBatchArmorStand) cmiHologramUserData.getHologramBatch();

    for (int value = 0; value <= list.size() - 1; value++) {
      FakeEntityInfo fakeEntityInfo =
          cmiHologramBatchArmorStand.getDisplays().size() > value
              ? cmiHologramBatchArmorStand.getDisplays().get(value)
              : null;
      if (fakeEntityInfo != null && fakeEntityInfo.getId() > 0) {
        CMI.getInstance()
            .getNMS()
            .updateHologram(
                player,
                new CMIDataWatcher(fakeEntityInfo.getId(), fakeEntityInfo.getEntity(), null),
                CMIChatColor.translate((String) list.get(value)),
                null);
        concurrentHashMap.put(value, fakeEntityInfo);
      } else {
        Location location = this.positionAdjustedByLine(list.size() - value).add(0.0, -0.1, 0.0);
        CMIDataWatcher cmiDataWatcher =
            CMI.getInstance()
                .getNMS()
                .spawnHologram(player, location, (String) list.get(value), null);
        FakeEntityInfo display =
            new FakeEntityInfo(cmiDataWatcher.getId(), player.getUniqueId(), location);
        display.setEntity(cmiDataWatcher.getStand());
        concurrentHashMap.put(value, display);
      }
    }

    return concurrentHashMap;
  }

  private void updateDisplaySettings(
      CMIHologramUserData cmiHologramUserData,
      CMIHologramBatchArmorStand cmiHologramBatchArmorStand) {}

  private Vector getFinalIconOffset() {
    CMIVector3D cmiVector3D = this.getHologramBase().getIconSettings().getOffset();
    return new Vector(cmiVector3D.getX(), cmiVector3D.getY(), cmiVector3D.getZ());
  }
}
