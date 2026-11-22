package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import com.Zrips.CMI.Modules.Display.CMITextDisplay;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramAnimationSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramBoardSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramIconSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramPageSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import com.Zrips.CMI.Modules.Particl.CMIPEAnimationInterface;
import com.Zrips.CMI.Modules.Particl.CMIVisualEffect;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Cuboids.CMIBlockArea;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CMIHologram {
  public static final double LINE_HEIGHT = 0.25;
  private static String left = "!leftclick!";
  private static String right = "!rightclick!";
  private static String shiftLeft = "!shiftleftclick!";
  private static String shiftRight = "!shiftrightclick!";
  protected CMILocation loc = null;
  protected String name = null;
  private boolean enabled = true;
  private CMIBlockArea updateArea;
  private CMIBlockArea visibilityArea;
  private Vector centerLocation = null;
  private CMIHologramType type = CMIHologramType.TextDisplay;
  private Set<UUID> playersInHologramUpdateRange = Collections.synchronizedSet(new HashSet<>());
  private Set<UUID> playersInHologramVisibilityRange = Collections.synchronizedSet(new HashSet<>());
  ConcurrentHashMap<CMIHologramType, CMIHologramProcessor> processors = new ConcurrentHashMap<>();
  private CMIHologramSettings settings = new CMIHologramSettings();
  private CMIHologramTextSettings textSettings = new CMIHologramTextSettings();
  private CMIHologramIconSettings iconSettings = new CMIHologramIconSettings();
  private CMIHologramInteraction interactionSettings = new CMIHologramInteraction();
  private CMIHologramAnimationSettings animationSettings = new CMIHologramAnimationSettings();
  private CMIHologramBoardSettings boardSettings = new CMIHologramBoardSettings();
  private CMIHologramPageSettings pageSettings = new CMIHologramPageSettings();
  private CMIHologramHoverModule hoverModule = new CMIHologramHoverModule(this);
  private CMIHologramPages pages = new CMIHologramPages(this);
  private CMIHologramLine autoPageLine = null;
  protected ConcurrentHashMap<UUID, CMIHologramUserData> userData = new ConcurrentHashMap<>();
  private CMITask scheduler = null;
  private ConcurrentHashMap<CMIHologramSchedulerType, CMIHologramSchedulerData> schedulerUpdates =
      new ConcurrentHashMap<>();

  private void nullIt() {
    this.playersInHologramUpdateRange = Collections.synchronizedSet(new HashSet<>());
    this.playersInHologramVisibilityRange = Collections.synchronizedSet(new HashSet<>());
    this.getPages().clear();
    this.textSettings = new CMIHologramTextSettings();
    this.iconSettings = new CMIHologramIconSettings();
    this.interactionSettings = new CMIHologramInteraction();
    this.settings = new CMIHologramSettings();
    this.animationSettings = new CMIHologramAnimationSettings();
    this.boardSettings = new CMIHologramBoardSettings();
    this.pageSettings = new CMIHologramPageSettings();
    this.hoverModule = new CMIHologramHoverModule(this);
    this.userData = new ConcurrentHashMap<>();
    this.resetUpdateArea();
    this.resetVisibilityArea();
  }

  @Deprecated
  public CMIHologram(Location location, Player player, List<String> list) {
    if (!Version.isCurrentLower(Version.v1_19_R3)) {
      CMITextDisplay cmiTextDisplay = new CMITextDisplay(location);
      cmiTextDisplay.setText(list);
      cmiTextDisplay.setShadowed(false);
      cmiTextDisplay.setBrightness(15, 15);

      try {
        Color color = cmiTextDisplay.getBackgroundColor();
        if (color == null) {
          color = Color.BLACK;
        }

        color = color.setAlpha(0);
        cmiTextDisplay.setBackgroundColor(color);
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      cmiTextDisplay.setSeeThrough(true);
      cmiTextDisplay.show(player);
      CMIScheduler.runAtLocationLater(
          CMI.getInstance(), location, () -> cmiTextDisplay.destroy(player), 60L);
    }
  }

  public CMIHologram(String string, Location location) {
    this(string, new CMILocation(location));
  }

  public CMIHologram(String string, CMILocation cmiLocation) {
    this.setLocation(cmiLocation);
    this.name = string;
  }

  public Location getCenterLocation(Player player) {
    CMIHologramPage cmiHologramPage = this.getPlayerPage(player);
    double number = cmiHologramPage == null ? 0.0 : cmiHologramPage.getHeight();
    CMILocation cmiLocation = this.getLocation().clone();
    cmiLocation.add(0.0, 0.25, 0.0);
    cmiLocation.add(0.0, number / 2.0, 0.0);
    return cmiLocation;
  }

  protected CMIHologramLine getAutoPageLine() {
    if (!this.getPageSettings().isAutoPagination()) {
      return null;
    }

    if (this.autoPageLine == null) {
      this.autoPageLine =
          new CMIHologramLine(
              this.getTextSettings(),
              CMI.getInstance().getHologramManager().getAutoPageLine().getText());
    }

    return this.autoPageLine;
  }

  public Vector getCenterVector() {
    if (this.centerLocation != null) {
      return this.centerLocation.clone();
    }

    this.centerLocation = this.getCenterLocation().toVector();
    return this.centerLocation.clone();
  }

  public Location getCenterLocation() {
    double number = this.getPages().getHeight();
    CMILocation cmiLocation = this.getLocation().clone();
    cmiLocation.add(0.0, 0.25, 0.0);
    cmiLocation.add(0.0, number / 2.0, 0.0);
    return cmiLocation;
  }

  public double getLineHeight() {
    return 0.25 * this.getSettings().getScale().getY();
  }

  public CMIHologramTextSettings getTextSettings() {
    return this.textSettings;
  }

  private CMIHologram setTextSettings(CMIHologramTextSettings cmiHologramTextSettings) {
    this.textSettings = cmiHologramTextSettings;
    return this;
  }

  public CMIHologramInteraction getInteractionSettings() {
    return this.interactionSettings;
  }

  private CMIHologram setInteractionSettings(CMIHologramInteraction cmiHologramInteraction) {
    this.interactionSettings = cmiHologramInteraction;
    return this;
  }

  public CMIHologramPages getPages() {
    return this.pages;
  }

  public World getWorld() {
    return this.loc.getWorld();
  }

  public String getWorldName() {
    return this.loc.getWorldName();
  }

  public CMIBlockArea getUpdateArea() {
    if (this.updateArea == null) {
      this.resetUpdateArea();
    }

    return this.updateArea;
  }

  public CMIBlockArea resetUpdateArea() {
    this.updateArea =
        new CMIBlockArea(
            this.getLocation()
                .clone()
                .add(
                    -this.getSettings().getUpdateRange(),
                    -this.getSettings().getUpdateRange(),
                    -this.getSettings().getUpdateRange()),
            this.getLocation()
                .clone()
                .add(
                    this.getSettings().getUpdateRange(),
                    this.getSettings().getUpdateRange(),
                    this.getSettings().getUpdateRange()));
    return this.updateArea;
  }

  public CMIBlockArea getVisibilityArea() {
    if (this.visibilityArea == null) {
      this.resetVisibilityArea();
    }

    return this.visibilityArea;
  }

  public CMIBlockArea resetVisibilityArea() {
    this.visibilityArea =
        new CMIBlockArea(
            this.getLocation()
                .clone()
                .add(
                    -this.getSettings().getVisibilityRange(),
                    -this.getSettings().getVisibilityRange(),
                    -this.getSettings().getVisibilityRange()),
            this.getLocation()
                .clone()
                .add(
                    this.getSettings().getVisibilityRange(),
                    this.getSettings().getVisibilityRange(),
                    this.getSettings().getVisibilityRange()));
    return this.visibilityArea;
  }

  public CMILocation getLocation() {
    return this.loc;
  }

  protected CMILocation getLocationWithPitchYaw() {
    CMILocation cmiLocation = this.getLocation().clone();
    float floatValue = (this.getName().hashCode() & 2147483647) / 2.1474836E9F * 1.0E-4F;
    cmiLocation.add(floatValue, floatValue, floatValue);
    cmiLocation.setPitch((float) this.getSettings().getPitch());
    cmiLocation.setYaw((float) this.getSettings().getYaw());
    return cmiLocation;
  }

  public void relocateGroup(Location location) {
    CMI.getInstance()
        .getHologramManager()
        .relocateHologramGroup(location, this.getSettings().getGroup());
  }

  public void setLocation(Location location) {
    this.setLocation(new CMILocation(location));
  }

  public void setLocation(CMILocation cmiLocation) {
    if (cmiLocation != null) {
      cmiLocation.setPitch(0.0F);
      cmiLocation.setYaw(0.0F);
      this.loc = cmiLocation;
      if (this.getLocation() != null
          && this.getWorldName() != null
          && cmiLocation.getWorldName() != null
          && (!cmiLocation.getWorldName().equals(this.getLocation().getWorldName())
              || cmiLocation.getX() != this.getLocation().getX()
              || cmiLocation.getY() != this.getLocation().getY()
              || cmiLocation.getZ() != this.getLocation().getZ())) {
        this.resetVisibilityArea();
        this.resetUpdateArea();
        this.recalculateChunks();
        this.requestUpdate(CMIHologramUpdateType.Location);
      }

      this.centerLocation = null;
    }
  }

  public CompletableFuture<Void> requestFullUpdate() {
    return this.requestUpdate(
        CMIHologramUpdateType.Show,
        CMIHologramUpdateType.Location,
        CMIHologramUpdateType.Billboard,
        CMIHologramUpdateType.Text,
        CMIHologramUpdateType.Icon);
  }

  public CompletableFuture<Void> requestUpdate(List<CMIHologramUpdateType> list) {
    return this.requestUpdate(list.toArray(new CMIHologramUpdateType[0]));
  }

  public CompletableFuture<Void> requestTextUpdate() {
    return this.requestUpdate(CMIHologramUpdateType.Text);
  }

  public CompletableFuture<Void> requestUpdate(CMIHologramUpdateType... cmiHologramUpdateTypes) {
    ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();

    for (UUID uuid : this.getPlayersFromVisibilityRange()) {
      arrayList.add(this.requestUpdate(uuid, cmiHologramUpdateTypes));
    }

    return CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]));
  }

  public CompletableFuture<Void> requestUpdate(Player player) {
    return this.requestUpdate(player.getUniqueId());
  }

  public CompletableFuture<Void> requestUpdate(UUID uuid) {
    return this.requestUpdate(uuid, CMIHologramUpdateType.Text, CMIHologramUpdateType.Icon);
  }

  public CompletableFuture<Void> requestUpdate(
      UUID uuid, CMIHologramUpdateType... cmiHologramUpdateTypes) {
    return CMIHologramUpdator.requestUpdate(uuid, this, cmiHologramUpdateTypes);
  }

  public static void onShutdown() {
    CMIHologramUpdator.shutdown();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public synchronized CMIHologramUserData getData(Player player) {
    return this.getData(player.getUniqueId());
  }

  public synchronized CMIHologramUserData getData(UUID uuid) {
    return this.userData.computeIfAbsent(uuid, key -> new CMIHologramUserData(uuid));
  }

  public synchronized void resetCache() {
    for (var entry : this.userData.entrySet()) {
      if (((CMIHologramUserData) entry.getValue()).getHologramBatch() != null) {
        ((CMIHologramUserData) entry.getValue())
            .getHologramBatch()
            .destroy(CMIUser.getOnlinePlayer((UUID) entry.getKey()));
      }
    }
  }

  public synchronized void resetIconCache() {
    for (var entry : this.userData.entrySet()) {
      if (((CMIHologramUserData) entry.getValue()).getHologramBatch() != null) {
        ((CMIHologramUserData) entry.getValue())
            .getHologramBatch()
            .destroyIcons(CMIUser.getOnlinePlayer((UUID) entry.getKey()));
      }
    }
  }

  protected synchronized CMIHologramUserData removeData(UUID uuid) {
    return this.userData.remove(uuid);
  }

  public synchronized Set<UUID> getPlayersFromUpdateRange() {
    return this.playersInHologramUpdateRange;
  }

  public synchronized void addPlayerToUpdateRange(UUID uuid) {
    this.getPlayersFromUpdateRange().add(uuid);
    this.recheckScheduler();
  }

  public synchronized void removePlayerFromUpdateRange(UUID uuid) {
    this.getPlayersFromUpdateRange().remove(uuid);
    this.recheckScheduler();
  }

  public synchronized Set<UUID> getPlayersFromVisibilityRange() {
    return this.playersInHologramVisibilityRange;
  }

  public synchronized void addPlayerToVisibilityRange(UUID uuid) {
    this.getPlayersFromVisibilityRange().add(uuid);
    this.recheckScheduler();
  }

  public synchronized CompletableFuture<Void> removePlayerFromVisibilityRange(UUID uuid) {
    this.getPlayersFromVisibilityRange().remove(uuid);
    return this.hide(uuid);
  }

  public void removeFromCache(UUID uuid) {
    this.removeFromCache(uuid, true);
  }

  public void removeFromCache(UUID uuid, boolean flag) {
    CMIHologramUserData cmiHologramUserData = this.getData(uuid);
    CMIHologramBatch cmiHologramBatch = cmiHologramUserData.getHologramBatch();
    if (cmiHologramBatch != null) {
      cmiHologramBatch.destroy(CMIUser.getOnlinePlayer(uuid));
      cmiHologramUserData.getHologramBatch().clear();
    }

    Player player = CMIUser.getOnlinePlayer(uuid);
    if (player != null && player.isOnline()) {
      if (flag) {
        this.removeData(uuid);
      }
    }
  }

  public CompletableFuture<Void> instantHide() {
    var hashSet = new HashSet<>(this.getPlayersFromVisibilityRange());
    if (hashSet != null && !hashSet.isEmpty()) {
      CompletableFuture[] completableFutures =
          hashSet.stream().map(this::instantHide).toArray(CompletableFuture[]::new);
      return CompletableFuture.allOf(completableFutures);
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }

  public CompletableFuture<Void> hide() {
    var set = this.getPlayersFromVisibilityRange();
    if (set != null && !set.isEmpty()) {
      List<CompletableFuture<Void>> list =
          set.stream().map(this::hide).collect(Collectors.toList());
      return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    } else {
      return CompletableFuture.completedFuture(null);
    }
  }

  public CompletableFuture<Void> hide(UUID uuid) {
    return this.requestUpdate(uuid, CMIHologramUpdateType.Hide);
  }

  public CompletableFuture<Void> instantHide(UUID uuid) {
    return this.requestUpdate(uuid, CMIHologramUpdateType.InstantHide);
  }

  @Nullable
  public CMIHologramPage getPlayerPage(Player player) {
    return this.getPages().getPlayerPage(player);
  }

  @Nullable
  public CMIHologramPage getPlayerPage(UUID uuid) {
    return this.getPages().getPlayerPage(uuid);
  }

  private List<String> getCommandsByPlayerPage(UUID uuid) {
    if (uuid == null) {
      return new ArrayList<>(this.getInteractionSettings().getCommands());
    }

    CMIHologramPage cmiHologramPage = this.getPlayerPage(uuid);
    if (cmiHologramPage == null) {
      return new ArrayList<>(this.getInteractionSettings().getCommands());
    }

    var list = cmiHologramPage.getCommands();
    if (list.isEmpty() && !this.getInteractionSettings().getCommands().isEmpty()) {
      list = new ArrayList<>(this.getInteractionSettings().getCommands());
    }

    return list;
  }

  protected List<CMIHologramLine> getLinesByPlayerPage(UUID uuid) {
    return this.getPages().getLinesByPlayerPage(uuid);
  }

  public void goToNextPage(Player player) {
    this.getPages().goToNextPage(player);
  }

  public void goToNextPage(UUID uuid) {
    this.getPages().goToNextPage(uuid);
  }

  public void goToPrevPage(Player player) {
    this.getPages().goToPrevPage(player);
  }

  public void goToPrevPage(UUID uuid) {
    this.getPages().goToPrevPage(uuid);
  }

  public void goToPage(UUID uuid, int value) {
    this.getPages().goToPage(uuid, value);
  }

  protected CompletableFuture<Void> updateCycle(UUID uuid, Set<CMIHologramUpdateType> set) {
    if (!this.isEnabled() && !set.contains(CMIHologramUpdateType.Hide)) {
      return CompletableFuture.completedFuture(null);
    }

    try {
      if (set.remove(CMIHologramUpdateType.LineOfSight)) {
        this.checkLineOfSight(uuid);
      }

      if (set.remove(CMIHologramUpdateType.Hover)) {
        this.getHoverModule().checkHoverPosition(uuid);
      }

      if (set.remove(CMIHologramUpdateType.PageChange)) {
        this.getPages().goToNextPageAuto(uuid);
      }

      if (!set.isEmpty()) {
        return this.updateBillboard(uuid, set);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return CompletableFuture.completedFuture(null);
  }

  private void checkLineOfSight(UUID uuid) {
    if (!this.getPlayersFromVisibilityRange().isEmpty()
        && this.getSettings().isCheckLineOfSight()) {
      if (this.getHologramType(uuid).equals(CMIHologramType.ArmorStand)) {
        Player player = CMIUser.getOnlinePlayer(uuid);
        if (player != null
            && player.isOnline()
            && player.getWorld() == this.getLocation().getWorld()) {
          Vector vector = player.getEyeLocation().toVector();
          double number = vector.distance(this.getCenterLocation().toVector());
          if (!(number > this.getSettings().getVisibilityRange())
              && !(number > 16 * (Bukkit.getViewDistance() - 1))) {
            Block block =
                CMI.getInstance()
                    .getNMS()
                    .getBlockLookingAt(
                        this.getLocation().getWorld(),
                        vector,
                        this.getCenterLocation().toVector(),
                        false);
            if (block != null) {
              if (!this.getData(player).isHidden()) {
                this.requestUpdate(player.getUniqueId(), CMIHologramUpdateType.ShadowHide);
              }
            } else if (this.getData(player).isHidden()) {
              this.requestUpdate(player.getUniqueId(), CMIHologramUpdateType.InstantShow);
            }
          }
        }
      }
    }
  }

  protected List<CMIHologramLine> getAllLines(CMIHologramPage cmiHologramPage) {
    ArrayList<CMIHologramLine> arrayList = new ArrayList();
    arrayList.addAll(cmiHologramPage.getLines());
    if (this.getPageSettings().isAutoPagination()) {
      arrayList.add(this.getAutoPageLine());
    }

    return arrayList;
  }

  public void onInteraction(Player player, CMIInteractType cmiInteractType) {}

  protected void processClick(
      Player player,
      CMIInteractType cmiInteractType,
      CMIVector3D cmiVector3D,
      CMIHologramLinePortion cmiHologramLinePortion) {
    if (cmiHologramLinePortion != null && cmiHologramLinePortion.getClickActions() != null) {
      String string = cmiHologramLinePortion.getClickActions().getCommand();
      this.onInteraction(player, cmiInteractType);
      if (string != null) {
        if (string.equalsIgnoreCase("[prevPage]")) {
          this.goToPrevPage(player);
        } else if (string.equalsIgnoreCase("[nextPage]")) {
          this.goToNextPage(player);
        } else {
          Snd snd = new Snd();
          snd.setSender(player).setTarget(player);
          string = CMI.getInstance().getLM().updateSnd(snd, string);
          CMI.getInstance()
              .getSpecializedCommandManager()
              .processCmds(
                  "CMIHologram", Arrays.asList(string.split(";;")), player, cmiInteractType);
          if (this.getInteractionSettings().isShowClickParticle()
              && ParticleManager.CMIPresetAnimations.HologramNewInteraction.isEnabled()) {
            CMIPEAnimationInterface cmipeAnimationInterface =
                ParticleManager.CMIPresetAnimations.HologramNewInteraction.getInter(player);
            cmipeAnimationInterface.setCenter(cmiVector3D.toLocation(this.getWorld()));
            CMIVisualEffect cmiVisualEffect = new CMIVisualEffect(player, cmipeAnimationInterface);
            cmiVisualEffect.setUntil(System.currentTimeMillis() + 2000L);
            cmiVisualEffect.show(player);
          }
        }

        this.requestUpdate(
            player.getUniqueId(), CMIHologramUpdateType.Text, CMIHologramUpdateType.Billboard);
      }
    } else if (!this.getInteractionSettings().getCommands().isEmpty()) {
      Snd sound = new Snd();
      sound.setSender(player).setTarget(player);
      var arrayList = new ArrayList<>(this.getInteractionSettings().getCommands());
      CMI.getInstance().getLM().updateSnd(sound, arrayList);

      for (String line : arrayList) {
        if (line.equalsIgnoreCase("[prevPage]")) {
          this.goToPrevPage(player);
        } else if (line.equalsIgnoreCase("[nextPage]")) {
          this.goToNextPage(player);
        }
      }

      CMI.getInstance()
          .getSpecializedCommandManager()
          .processCmds("CMIHologram", arrayList, player, cmiInteractType);
      if (this.getInteractionSettings().isShowClickParticle()
          && ParticleManager.CMIPresetAnimations.HologramInteraction.isEnabled()) {
        CMIPEAnimationInterface animationInterface =
            ParticleManager.CMIPresetAnimations.HologramInteraction.getInter(player);
        animationInterface.setCenter(cmiVector3D.toLocation(this.getWorld()));
        CMIVisualEffect visualEffect = new CMIVisualEffect(player, animationInterface);
        visualEffect.setTargetPlayer(player);
        visualEffect.setUntil(System.currentTimeMillis() + 2000L);
        visualEffect.show(player);
      }
    }
  }

  protected CompletableFuture<Void> updateBillboard(UUID uuid, Set<CMIHologramUpdateType> set) {
    CMIHologramUserData cmiHologramUserData = this.getData(uuid);
    CMIHologramProcessor cmiHologramProcessor =
        this.getProcessor(cmiHologramUserData.getHologramType());
    if (Version.isFolia()) {
      CompletableFuture completableFuture = new CompletableFuture();
      CMIScheduler.runAtLocation(
          CMI.getInstance(),
          this.getLocation(),
          () ->
              cmiHologramProcessor
                  .updateBillboard(uuid, set)
                  .whenComplete(
                      (ignored, throwable) -> {
                        if (throwable != null) {
                          completableFuture.completeExceptionally(throwable);
                        } else {
                          completableFuture.complete(null);
                        }
                      }));
      return completableFuture;
    } else {
      return cmiHologramProcessor.updateBillboard(uuid, set);
    }
  }

  public void show(Player player) {
    this.requestUpdate(
        player.getUniqueId(),
        CMIHologramUpdateType.Location,
        CMIHologramUpdateType.Billboard,
        CMIHologramUpdateType.Text,
        CMIHologramUpdateType.Show);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
    if (flag) {
      this.requestUpdate(CMIHologramUpdateType.Show);
    } else {
      this.requestUpdate(CMIHologramUpdateType.Hide);
      this.hide();
    }
  }

  public void disable() {
    this.enabled = false;
    this.hide();
  }

  public void enable() {
    this.enabled = true;
    this.requestUpdate(CMIHologramUpdateType.Show);
  }

  public List<String> getCommands(Player player, CMIInteractType cmiInteractType) {
    Snd snd = new Snd().setSender(player).setTarget(player);
    ArrayList<String> arrayList = new ArrayList();

    for (String string :
        this.getCommandsByPlayerPage(player == null ? null : player.getUniqueId())) {
      String text = string;
      if (!text.startsWith(left) && !text.startsWith(right)) {
        if (!text.startsWith(shiftLeft) && !text.startsWith(shiftRight)) {
          arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
        } else if (cmiInteractType != null) {
          switch (cmiInteractType) {
            case LeftClick:
              if (text.startsWith(shiftLeft)) {
                text = text.substring(shiftLeft.length());
                if (player.isSneaking()) {
                  arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
                }
              }
              break;
            case RightClick:
              if (text.startsWith(shiftRight)) {
                text = text.substring(shiftRight.length());
                if (player.isSneaking()) {
                  arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
                }
              }
              break;
            default:
              if (text.startsWith(shiftLeft)) {
                text = text.substring(shiftLeft.length());
              }

              if (text.startsWith(shiftRight)) {
                text = text.substring(shiftRight.length());
              }

              arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
          }
        } else {
          if (text.startsWith(left)) {
            text = text.substring(left.length());
          }

          if (text.startsWith(right)) {
            text = text.substring(right.length());
          }

          if (text.startsWith(shiftLeft)) {
            text = text.substring(shiftLeft.length());
          }

          if (text.startsWith(shiftRight)) {
            text = text.substring(shiftRight.length());
          }

          arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
        }
      } else if (cmiInteractType != null) {
        switch (cmiInteractType) {
          case LeftClick:
            if (text.startsWith(left)) {
              text = text.substring(left.length());
              if (!player.isSneaking()) {
                arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
              }
            }
            break;
          case RightClick:
            if (text.startsWith(right)) {
              text = text.substring(right.length());
              if (!player.isSneaking()) {
                arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
              }
            }
            break;
          default:
            if (text.startsWith(left)) {
              text = text.substring(left.length());
            }

            if (text.startsWith(right)) {
              text = text.substring(right.length());
            }

            arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
        }
      } else {
        if (text.startsWith(left)) {
          text = text.substring(left.length());
        }

        if (text.startsWith(right)) {
          text = text.substring(right.length());
        }

        if (text.startsWith(shiftLeft)) {
          text = text.substring(shiftLeft.length());
        }

        if (text.startsWith(shiftRight)) {
          text = text.substring(shiftRight.length());
        }

        arrayList.add(CMI.getInstance().getLM().updateSnd(snd, text));
      }
    }

    return arrayList;
  }

  public void recheckScheduler() {
    int value = Integer.MAX_VALUE;

    for (CMIHologramSchedulerData cmiHologramSchedulerData : this.schedulerUpdates.values()) {
      if (cmiHologramSchedulerData.getTickInterval() < value) {
        value = cmiHologramSchedulerData.getTickInterval();
      }
    }

    this.resetScheduler();
    if (this.getSettings().getUpdateIntervalTicks() > 0) {
      this.schedulerUpdates.put(
          CMIHologramSchedulerType.Base,
          new CMIHologramSchedulerData(
              this.getSettings().getUpdateIntervalTicks(),
              CMIHologramUpdateType.Text,
              CMIHologramUpdateType.Icon));
    }

    if (this.getSettings().isCheckLineOfSight()) {
      this.schedulerUpdates.put(
          CMIHologramSchedulerType.LineOfSight,
          new CMIHologramSchedulerData(
              this.getSettings().getUpdateIntervalTicks(), CMIHologramUpdateType.LineOfSight));
    }

    if (this.getInteractionSettings().isInteractable()) {
      this.schedulerUpdates.put(
          CMIHologramSchedulerType.Hover,
          new CMIHologramSchedulerData(1, CMIHologramUpdateType.Hover));
    }

    if (this.getAnimationSettings().getAutoRotate() != 0) {
      this.schedulerUpdates.put(
          CMIHologramSchedulerType.AutoRotate,
          new CMIHologramSchedulerData(1, CMIHologramUpdateType.AutoRotate));
    }

    if (this.getPageSettings().getPageChangeIntervalTicks() > 0
        && this.getPages().getPages().size() > 1) {
      this.schedulerUpdates.put(
          CMIHologramSchedulerType.PageChange,
          new CMIHologramSchedulerData(
              this.getPageSettings().getPageChangeIntervalTicks(),
              CMIHologramUpdateType.PageChange,
              CMIHologramUpdateType.Text));
    }
  }

  public Set<CMIHologramUpdateType> getThingsToUpdate() {
    HashSet hashSet = new HashSet();

    for (CMIHologramSchedulerData cmiHologramSchedulerData : this.schedulerUpdates.values()) {
      if (cmiHologramSchedulerData.cycleTick()) {
        hashSet.addAll(cmiHologramSchedulerData.getUpdateTypes());
      }
    }

    return hashSet;
  }

  public Set<CMIHologramUpdateType> initializeAutoCycle() {
    var set = this.getThingsToUpdate();
    if (!set.isEmpty() && this.getAnimationSettings().getAutoRotate() != 0) {
      for (UUID uuid : this.getPlayersFromUpdateRange()) {
        this.getData(uuid).incrementTick();
      }
    }

    return set;
  }

  public void remove() {
    CMI.getInstance().getHologramManager().remove(this);
  }

  public void recheckPermissionAccess() {
    if (this.getSettings().isRequiresPermission()) {
      for (Player player : this.getLocation().getWorld().getPlayers()) {
        if (this.getVisibilityArea().containsLoc(player.getLocation())) {
          if (PermissionsManager.CMIPerm.hologram_$1.hasPermission(
              player, 1000L, this.getName().toLowerCase())) {
            this.updatePlayer(player);
          } else {
            this.removePlayer(player.getUniqueId());
          }
        }
      }
    }
  }

  protected synchronized void resetScheduler() {
    this.schedulerUpdates.clear();
    if (this.scheduler != null) {
      this.scheduler.cancel();
      this.scheduler = null;
    }
  }

  public CMIHologramSettings getSettings() {
    return this.settings;
  }

  private CMIHologram setSettings(CMIHologramSettings cmiHologramSettings) {
    this.settings = cmiHologramSettings;
    return this;
  }

  public CMIHologramIconSettings getIconSettings() {
    return this.iconSettings;
  }

  public CMIHologramBoardSettings getBoardSettings() {
    return this.boardSettings;
  }

  private CMIHologram setBoardSettings(CMIHologramBoardSettings cmiHologramBoardSettings) {
    this.boardSettings = cmiHologramBoardSettings;
    return this;
  }

  private CMIHologram setIconSettings(CMIHologramIconSettings cmiHologramIconSettings) {
    this.iconSettings = cmiHologramIconSettings;
    return this;
  }

  public CMIHologramAnimationSettings getAnimationSettings() {
    return this.animationSettings;
  }

  private CMIHologram setAnimationSettings(
      CMIHologramAnimationSettings cmiHologramAnimationSettings) {
    this.animationSettings = cmiHologramAnimationSettings;
    return this;
  }

  public CMIHologramPageSettings getPageSettings() {
    return this.pageSettings;
  }

  private CMIHologram setPageSettings(CMIHologramPageSettings cmiHologramPageSettings) {
    this.pageSettings = cmiHologramPageSettings;
    return this;
  }

  public CMIHologramHoverModule getHoverModule() {
    return this.hoverModule;
  }

  @NotNull
  public CMIHologramType getHologramType(UUID uuid) {
    CMIHologramUserData cmiHologramUserData = this.getData(uuid);
    return !cmiHologramUserData.getHologramType().equals(CMIHologramType.Auto)
        ? cmiHologramUserData.getHologramType()
        : this.getType();
  }

  @NotNull
  public CMIHologramProcessor getProcessor(@Nullable CMIHologramType cmiHologramType) {
    CMIHologramType hologramType =
        cmiHologramType != null && cmiHologramType != CMIHologramType.Auto
            ? cmiHologramType
            : this.getType();
    return this.processors.computeIfAbsent(
        hologramType,
        type -> {
          if (Version.isCurrentLower(Version.v1_20_R1)) {
            return new CMIHologramProcessorArmorStand(this);
          } else {
            return type.equals(CMIHologramType.TextDisplay)
                ? new CMIHologramProcessorDisplay(this)
                : new CMIHologramProcessorArmorStand(this);
          }
        });
  }

  public CMIHologramType getType() {
    return this.type;
  }

  public CMIHologram setType(@NotNull CMIHologramType cmiHologramType) {
    this.type =
        cmiHologramType.equals(CMIHologramType.Auto)
            ? (Version.isCurrentLower(Version.v1_20_R1)
                ? CMIHologramType.ArmorStand
                : CMIHologramType.TextDisplay)
            : cmiHologramType;
    return this;
  }

  @Nullable
  public static CMIHologram deserialize(Map<String, Object> map) {
    String string = null;
    if (map.get("Name") instanceof String) {
      string = (String) map.get("Name");
    }

    if (map.size() == 1) {
      string = (String) map.keySet().iterator().next();
      map = (Map) map.get(string);
    }

    if (string == null) {
      return null;
    }

    CMILocation cmiLocation = null;
    if (map.get("Loc") instanceof String) {
      cmiLocation = CMILocation.fromString((String) map.get("Loc"));
    }

    if (cmiLocation == null) {
      return null;
    }

    boolean flag = false;
    CMIHologram cmiHologram = CMI.getInstance().getHologramManager().getByName(string);
    boolean hasHologram = cmiHologram != null;
    CMIHologram existingHologram =
        cmiHologram instanceof CMIHologram ? cmiHologram : new CMIHologram(string, cmiLocation);
    if (hasHologram) {
      existingHologram.nullIt();
      existingHologram.setLocation(cmiLocation);
    }

    if (map.get("NewDisplay") instanceof Boolean) {
      existingHologram.setType(
          (Boolean) map.get("NewDisplay")
              ? CMIHologramType.TextDisplay
              : CMIHologramType.ArmorStand);
    }

    if (map.get("ArmorStand") instanceof Boolean) {
      existingHologram.setType(
          (Boolean) map.get("ArmorStand")
              ? CMIHologramType.ArmorStand
              : CMIHologramType.TextDisplay);
    }

    if (map.get("Enabled") instanceof Boolean) {
      existingHologram.setEnabled((Boolean) map.get("Enabled"));
    }

    if (map.get("Interaction") instanceof MemorySection) {
      var interactionValues = ((MemorySection) map.get("Interaction")).getValues(true);
      existingHologram.setInteractionSettings(
          CMIHologramInteraction.deserialize(interactionValues));
    } else {
      CMIHologramInteraction cmiHologramInteraction = existingHologram.getInteractionSettings();
      if (map.get("ShowParticles") instanceof Boolean) {
        cmiHologramInteraction.setShowClickParticle((Boolean) map.get("ShowParticles"));
      }

      if (map.get("HoverParticles") instanceof MemorySection) {
        flag = true;
        var hoverParticleValues = ((MemorySection) map.get("HoverParticles")).getValues(true);
        if (hoverParticleValues.get("ShowHover") instanceof Boolean) {
          cmiHologramInteraction.setShowHoverParticle(
              (Boolean) hoverParticleValues.get("ShowHover"));
        }

        if (hoverParticleValues.get("Count") instanceof Integer) {
          cmiHologramInteraction.setParticleCount((Integer) hoverParticleValues.get("Count"));
        }

        if (hoverParticleValues.get("ExtraHeight") instanceof Double) {
          cmiHologramInteraction
              .getParticleDimensions()
              .setY((Double) hoverParticleValues.get("ExtraHeight"));
        }

        if (hoverParticleValues.get("ExtraWidth") instanceof Double) {
          cmiHologramInteraction
              .getParticleDimensions()
              .setX((Double) hoverParticleValues.get("ExtraWidth"));
        }

        if (hoverParticleValues.get("OffsetX") instanceof Double) {
          cmiHologramInteraction
              .getParticleOffset()
              .setX((Double) hoverParticleValues.get("OffsetX"));
        }

        if (hoverParticleValues.get("OffsetY") instanceof Double) {
          cmiHologramInteraction
              .getParticleOffset()
              .setY((Double) hoverParticleValues.get("OffsetY"));
        }

        if (hoverParticleValues.get("Effect") instanceof String) {
          cmiHologramInteraction.setHoverEffect(
              CMIParticle.get((String) hoverParticleValues.get("Effect")));
        }

        if (hoverParticleValues.get("Spacing") instanceof Double) {
          cmiHologramInteraction.setParticleSpacing((Double) hoverParticleValues.get("Spacing"));
        }
      }

      if (map.get("Interactable") instanceof Boolean) {
        cmiHologramInteraction.setInteractable((Boolean) map.get("Interactable"));
      }

      existingHologram.setInteractionSettings(cmiHologramInteraction);
    }

    if (map.get("Page") instanceof MemorySection) {
      var pageValues = ((MemorySection) map.get("Page")).getValues(true);
      existingHologram.setPageSettings(CMIHologramPageSettings.deserialize(pageValues));
    } else {
      CMIHologramPageSettings cmiHologramPageSettings = new CMIHologramPageSettings();
      if (map.get("ND") instanceof MemorySection) {
        flag = true;
        var ndValuesFirst = ((MemorySection) map.get("ND")).getValues(true);
        if (ndValuesFirst.get("AutoPage") instanceof Boolean) {
          cmiHologramPageSettings.setAutoPagination((Boolean) ndValuesFirst.get("AutoPage"));
        }
      }

      if (map.get("PageInterval") instanceof Double) {
        cmiHologramPageSettings.setPageChangeIntervalTicks(
            (int) ((Double) map.get("PageInterval") * 20.0));
      }

      existingHologram.setPageSettings(cmiHologramPageSettings);
    }

    if (map.get("Text") instanceof MemorySection) {
      var textValues = ((MemorySection) map.get("Text")).getValues(true);
      existingHologram.setTextSettings(CMIHologramTextSettings.deserialize(textValues));
    } else {
      CMIHologramTextSettings cmiHologramTextSettings = new CMIHologramTextSettings();
      if (map.get("Filler") instanceof Integer) {
        cmiHologramTextSettings.setFillerAmount((Integer) map.get("Filler"));
      }

      if (map.get("ND") instanceof MemorySection) {
        flag = true;
        var ndValuesSecond = ((MemorySection) map.get("ND")).getValues(true);
        if (ndValuesSecond.get("Align") instanceof String) {
          cmiHologramTextSettings.setTextAlignment(
              CMITextAlignment.getByName((String) ndValuesSecond.get("Align")));
        }

        if (ndValuesSecond.get("TAlpha") instanceof Integer) {
          cmiHologramTextSettings.setTextAlpha((Integer) ndValuesSecond.get("TAlpha"));
        }

        if (ndValuesSecond.get("BAlpha") instanceof Integer) {
          cmiHologramTextSettings.setBackgroundAlpha((Integer) ndValuesSecond.get("BAlpha"));
        }

        if (ndValuesSecond.get("Shadow") instanceof Boolean) {
          cmiHologramTextSettings.setShadowed((Boolean) ndValuesSecond.get("Shadow"));
        }

        if (ndValuesSecond.get("Double") instanceof Boolean) {
          cmiHologramTextSettings.setDoubleSided((Boolean) ndValuesSecond.get("Double"));
        }

        if (ndValuesSecond.get("BColor") instanceof String) {
          cmiHologramTextSettings.setBackgroundColor(
              CMIChatColor.getColor((String) ndValuesSecond.get("BColor")));
        }

        if (ndValuesSecond.get("LineW") instanceof Integer) {
          cmiHologramTextSettings.setLineWidth((Integer) ndValuesSecond.get("LineW"));
        }
      }

      existingHologram.setTextSettings(cmiHologramTextSettings);
    }

    if (map.get("Settings") instanceof MemorySection) {
      var settingsValues = ((MemorySection) map.get("Settings")).getValues(true);
      existingHologram.setSettings(CMIHologramSettings.deserialize(settingsValues));
    } else {
      CMIHologramSettings cmiHologramSettings = new CMIHologramSettings();
      if (map.get("Interval") instanceof Double) {
        cmiHologramSettings.setUpdateIntervalTicks((int) (20.0 * (Double) map.get("Interval")));
      }

      if (map.get("Group") instanceof String) {
        cmiHologramSettings.setGroup((String) map.get("Group"));
      }

      if (map.get("RangeExtra") instanceof Integer) {
        cmiHologramSettings.setVisibilityRange((Integer) map.get("RangeExtra"));
        flag = true;
      }

      if (map.get("Range") instanceof Integer) {
        flag = true;
        int value = (Integer) map.get("Range");
        if (cmiHologramSettings.getVisibilityRange() < value) {
          cmiHologramSettings.setVisibilityRange(value);
        }

        cmiHologramSettings.setUpdateRange(value);
      }

      if (map.get("RequiresPermission") instanceof Boolean) {
        cmiHologramSettings.setPermissionRequirement((Boolean) map.get("RequiresPermission"));
      }

      if (map.get("ND") instanceof MemorySection) {
        var ndValuesThird = ((MemorySection) map.get("ND")).getValues(true);
        if (ndValuesThird.get("Bill") instanceof String) {
          cmiHologramSettings.setBillboard(
              CMIBillboard.getByName((String) ndValuesThird.get("Bill")));
        }

        if (ndValuesThird.get("SL") instanceof Integer) {
          cmiHologramSettings.setSkyLevel((Integer) ndValuesThird.get("SL"));
        }

        if (ndValuesThird.get("BL") instanceof Integer) {
          cmiHologramSettings.setBlockLevel((Integer) ndValuesThird.get("BL"));
        }

        if (ndValuesThird.get("Yaw") instanceof Integer) {
          cmiHologramSettings.setYaw(((Integer) ndValuesThird.get("Yaw")).intValue());
        }

        if (ndValuesThird.get("Pitch") instanceof Integer) {
          cmiHologramSettings.setPitch(((Integer) ndValuesThird.get("Pitch")).intValue());
        }

        if (ndValuesThird.get("ScaleW") instanceof Double) {
          cmiHologramSettings.getScale().setX((Double) ndValuesThird.get("ScaleW"));
        }

        if (ndValuesThird.get("ScaleH") instanceof Double) {
          cmiHologramSettings.getScale().setY((Double) ndValuesThird.get("ScaleH"));
        }
      }

      existingHologram.setSettings(cmiHologramSettings);
    }

    if (map.get("Icon") instanceof MemorySection) {
      var iconValues = ((MemorySection) map.get("Icon")).getValues(true);
      existingHologram.setIconSettings(CMIHologramIconSettings.deserialize(iconValues));
    } else {
      CMIHologramIconSettings cmiHologramIconSettings = new CMIHologramIconSettings();
      if (map.get("ND") instanceof MemorySection) {
        var ndValuesFourth = ((MemorySection) map.get("ND")).getValues(true);
        flag = true;
        if (ndValuesFourth.get("IBill") instanceof String) {
          cmiHologramIconSettings.setBillboard(
              CMIBillboard.getByName((String) ndValuesFourth.get("IBill")));
        }

        if (ndValuesFourth.get("IScale") instanceof Double) {
          cmiHologramIconSettings.setScale((Double) ndValuesFourth.get("IScale"));
        }

        if (ndValuesFourth.get("IPitch") instanceof Integer) {
          cmiHologramIconSettings.setPitch((Integer) ndValuesFourth.get("IPitch"));
        }

        if (ndValuesFourth.get("IYaw") instanceof Integer) {
          cmiHologramIconSettings.setYaw((Integer) ndValuesFourth.get("IYaw"));
        }

        if (ndValuesFourth.get("IXOff") instanceof Double) {
          cmiHologramIconSettings.setOffset(
              new CMIVector3D(((Float) ndValuesFourth.get("IXOff")).floatValue(), 0.0, 0.0));
        }

        if (ndValuesFourth.get("IOff") instanceof String) {
          cmiHologramIconSettings.setOffset(
              CMIVector3D.fromString((String) ndValuesFourth.get("IOff")));
        }
      }

      existingHologram.setIconSettings(cmiHologramIconSettings);
    }

    if (map.get("Animation") instanceof MemorySection) {
      var animationValues = ((MemorySection) map.get("Animation")).getValues(true);
      existingHologram.setAnimationSettings(
          CMIHologramAnimationSettings.deserialize(animationValues));
    }

    if (map.get("Board") instanceof MemorySection) {
      var boardValues = ((MemorySection) map.get("Board")).getValues(true);
      existingHologram.setBoardSettings(CMIHologramBoardSettings.deserialize(boardValues));
    }

    if (map.get("Commands") instanceof List) {
      List list = (List) map.get("Commands");
      existingHologram.getInteractionSettings().setCommands(list);
    }

    List pageList = new ArrayList();
    if (map.get("Lines") instanceof List) {
      pageList = (List) map.get("Lines");
    }

    existingHologram.getPages().setLines(pageList);
    if (flag && !(map.get("DownOrner") instanceof Boolean)) {
      double number = existingHologram.getPages().getHeight();
      cmiLocation = existingHologram.getLocation();
      cmiLocation.add(0.0, -number * existingHologram.getSettings().getScale().getY(), 0.0);
      existingHologram.setLocation(cmiLocation);
    }

    return existingHologram;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.getType().equals(CMIHologramType.ArmorStand)) {
      hashMap.put("ArmorStand", true);
    }

    hashMap.put("Loc", CMILocation.toString(this.getLocation(), true));
    if (!this.isEnabled()) {
      hashMap.put("Enabled", false);
    }

    var map = this.getSettings().serialize();
    if (map != null) {
      hashMap.put("Settings", map);
    }

    map = this.getIconSettings().serialize();
    if (map != null) {
      hashMap.put("Icon", map);
    }

    map = this.getTextSettings().serialize();
    if (map != null) {
      hashMap.put("Text", map);
    }

    map = this.getPageSettings().serialize();
    if (map != null) {
      hashMap.put("Page", map);
    }

    map = this.getInteractionSettings().serialize();
    if (map != null) {
      hashMap.put("Interaction", map);
    }

    map = this.getAnimationSettings().serialize();
    if (map != null) {
      hashMap.put("Animation", map);
    }

    map = this.getBoardSettings().serialize();
    if (map != null) {
      hashMap.put("Board", map);
    }

    hashMap.put("Lines", this.getPages().getLines());
    return hashMap;
  }

  public void updatePlayers() {
    for (UUID uuid : this.detectPlayersInVisibilityRange()) {
      CMI.getInstance().getHologramManager().updatePlayer(uuid);
    }
  }

  public CompletableFuture<Void> removePlayers() {
    var list =
        this.detectPlayersInVisibilityRange().stream()
            .map(
                uuid -> CMI.getInstance().getHologramManager().removePlayerFromHologram(uuid, this))
            .toList();
    return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
  }

  public void updatePlayer(Player player) {
    CMI.getInstance().getHologramManager().updatePlayer(player);
  }

  public CompletableFuture<Void> removePlayer(UUID uuid) {
    return this.hide(uuid)
        .thenCompose(
            ignored -> CMI.getInstance().getHologramManager().removePlayerFromHologram(uuid, this));
  }

  public void recalculateChunks() {
    CMI.getInstance().getHologramManager().recalculateChunks(this);
  }

  public List<UUID> detectPlayersInVisibilityRange() {
    ArrayList<UUID> arrayList = new ArrayList();

    for (Player player : this.getLocation().getWorld().getPlayers()) {
      if (this.getVisibilityArea().containsLoc(player.getLocation())) {
        arrayList.add(player.getUniqueId());
      }
    }

    return arrayList;
  }

  public List<UUID> detectPlayersInUpdateRange() {
    ArrayList<UUID> arrayList = new ArrayList();

    for (Player player : this.getLocation().getWorld().getPlayers()) {
      if (this.getUpdateArea().containsLoc(player.getLocation())) {
        arrayList.add(player.getUniqueId());
      }
    }

    return arrayList;
  }

  public void show() {
    this.requestUpdate(CMIHologramUpdateType.Show);
  }

  public void update() {
    this.resetScheduler();
    this.recheckScheduler();
    this.requestFullUpdate();
  }

  public void saveToFile() {
    CMI.getInstance().getHologramManager().save();
  }

  public void delete() {
    CMI.getInstance().getHologramManager().remove(this);
  }

  public void reset() {
    CMI.getInstance().getHologramManager().reset(this);
  }

  public boolean isInUpdateRange(UUID uuid) {
    return this.playersInHologramUpdateRange.contains(uuid);
  }

  public boolean isInVisibilityRange(UUID uuid) {
    return this.playersInHologramVisibilityRange.contains(uuid);
  }

  @Deprecated(forRemoval = true)
  public void update(Player player) {
    this.requestUpdate(player);
  }

  @Deprecated(forRemoval = true)
  public void moveTo(Location location) {
    this.setLocation(location);
    if (this.getSettings().isSaveToFile()) {
      CMI.getInstance().getHologramManager().save();
    }
  }

  @Deprecated(forRemoval = true)
  public void superficialMoveTo(Location location) {
    this.setLocation(location);
    this.requestUpdate(CMIHologramUpdateType.Location);
  }

  @Deprecated(forRemoval = true)
  public void setLoc(Location location) {
    this.setLocation(location);
  }

  @Deprecated(forRemoval = true)
  public double getHeight() {
    return this.getPages().getHeight();
  }

  @Deprecated(forRemoval = true)
  public CuboidArea getAreaExtra() {
    return new CuboidArea(
        this.getWorld(),
        this.getVisibilityArea().getLowPoint(),
        this.getVisibilityArea().getHighPoint());
  }

  @Deprecated(forRemoval = true)
  public void setAreaExtra(CuboidArea cuboidArea) {
    this.visibilityArea = new CMIBlockArea(cuboidArea.getLowPoint(), cuboidArea.getHighPoint());
  }

  @Deprecated(forRemoval = true)
  public CMILocation getLoc() {
    return this.getLocation();
  }

  @Deprecated(forRemoval = true)
  public synchronized void setLines(List<String> list) {
    this.getPages().setLines(list);
  }

  @Deprecated(forRemoval = true)
  public void addLine(String string) {
    this.getPages().addLine(string);
  }

  @Deprecated(forRemoval = true)
  public void setLine(int value, String string) {
    this.getPages().setLine(value, string);
  }

  @Deprecated(forRemoval = true)
  public String getLine(int value) {
    var list = this.getPages().getLines();
    return list.size() - 1 >= value && value >= 0
        ? (list.get(value) == null ? "" : (String) list.get(value))
        : "";
  }

  @Deprecated(forRemoval = true)
  public void removeLine(int value) {
    this.getPages().removeLine(value);
  }

  @Deprecated(forRemoval = true)
  public List<String> getLines() {
    return this.getPages().getLines();
  }

  @Deprecated(forRemoval = true)
  public void recheckLines() {
    this.getPages().recheckLines();
  }

  @Deprecated(forRemoval = true)
  public boolean isChangedLine() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void removeFromCache(UUID uuid, int value) {
    this.removeFromCache(uuid, true);
  }

  @Deprecated(forRemoval = true)
  public void clearFakeEntities() {}

  @Deprecated(forRemoval = true)
  public void updatePages() {
    this.getPages().recheckLines();
  }

  @Deprecated(forRemoval = true)
  public void hide(Player player) {
    this.hide(player.getUniqueId());
  }

  @Deprecated(forRemoval = true)
  public void refresh() {
    this.requestFullUpdate();
  }

  @Deprecated(forRemoval = true)
  public void processPrevNextPageClick(Player player) {}

  @Deprecated(forRemoval = true)
  public void checkHoverPosition(Player player) {
    this.getHoverModule().checkHoverPosition(player.getUniqueId());
  }

  @Deprecated(forRemoval = true)
  void generalUpdate(Player player, boolean flag) {
    this.requestUpdate(player);
  }

  @Deprecated(forRemoval = true)
  public void update(Player player, boolean flag) {
    this.requestUpdate(player);
  }

  @Deprecated(forRemoval = true)
  public List<String> getCommands() {
    return this.getInteractionSettings().getCommands();
  }

  @Deprecated(forRemoval = true)
  public List<String> getCommands(Player player) {
    return this.getInteractionSettings().getCommands();
  }

  @Deprecated(forRemoval = true)
  public void setCommands(List<String> list) {
    this.getInteractionSettings().setCommands(list);
  }

  @Deprecated(forRemoval = true)
  public double getUpdateIntervalSec() {
    return this.getSettings().getUpdateIntervalTicks() / 20.0;
  }

  @Deprecated(forRemoval = true)
  public void setUpdateIntervalSec(double number) {
    this.getSettings().setUpdateIntervalTicks((int) (number * 20.0));
  }

  @Deprecated(forRemoval = true)
  public int getUpdateRange() {
    return this.getSettings().getUpdateRange();
  }

  @Deprecated(forRemoval = true)
  public void setUpdateRange(int value) {
    this.getSettings().setUpdateRange(value);
  }

  @Deprecated(forRemoval = true)
  public int getShowRange() {
    return this.getSettings().getVisibilityRange();
  }

  @Deprecated(forRemoval = true)
  public void setShowRange(int value) {
    this.getSettings().setVisibilityRange(value);
  }

  @Deprecated(forRemoval = true)
  public Double getIconSpacing() {
    return 0.25;
  }

  @Deprecated(forRemoval = true)
  public void setIconSpacing(Double iconSpacing) {}

  @Deprecated(forRemoval = true)
  public Double getSpacing() {
    return 0.25;
  }

  @Deprecated(forRemoval = true)
  public void setSpacing(Double spacing) {}

  @Deprecated(forRemoval = true)
  public Boolean isDownOrder() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void setDownOrder(Boolean downOrder) {}

  @Deprecated(forRemoval = true)
  public boolean isInteractable() {
    return this.getInteractionSettings().isInteractable();
  }

  @Deprecated(forRemoval = true)
  public void setInteractable(boolean flag) {
    this.getInteractionSettings().setInteractable(flag);
  }

  @Deprecated(forRemoval = true)
  public boolean isShowParticle() {
    return this.getInteractionSettings().isShowClickParticle();
  }

  @Deprecated(forRemoval = true)
  public void setShowParticle(boolean flag) {
    this.getInteractionSettings().setShowClickParticle(flag);
  }

  @Deprecated(forRemoval = true)
  public boolean isBigButton() {
    return true;
  }

  @Deprecated(forRemoval = true)
  public void setBigButton(boolean flag) {}

  @Deprecated(forRemoval = true)
  public synchronized CMITask getSched() {
    return null;
  }

  @Deprecated(forRemoval = true)
  public synchronized void setSched(CMITask cmiTask) {}

  @Deprecated(forRemoval = true)
  public void stop() {
    this.resetScheduler();

    for (UUID uuid : this.getPlayersFromVisibilityRange()) {
      CMIHologramHoverData cmiHologramHoverData = CMIHologramHoverData.getData(uuid);
      cmiHologramHoverData.setHoveringLine(null);
    }
  }

  @Deprecated(forRemoval = true)
  public Set<UUID> getLastHoloInRange() {
    return new HashSet<>(this.playersInHologramUpdateRange);
  }

  @Deprecated(forRemoval = true)
  public void clearLastHoloInRange() {
    this.playersInHologramUpdateRange.clear();
  }

  @Deprecated(forRemoval = true)
  public boolean isEmptyLastHoloInRange() {
    return this.playersInHologramUpdateRange.isEmpty();
  }

  @Deprecated(forRemoval = true)
  public boolean containsLastHoloInRange(UUID uuid) {
    return this.playersInHologramUpdateRange.contains(uuid);
  }

  @Deprecated(forRemoval = true)
  public synchronized void recheckTaskers() {
    this.recheckScheduler();
  }

  @Deprecated(forRemoval = true)
  public synchronized void addLastHoloInRange(UUID uuid) {
    this.playersInHologramUpdateRange.add(uuid);
  }

  @Deprecated(forRemoval = true)
  public synchronized void removeLastHoloInRange(UUID uuid) {
    this.playersInHologramUpdateRange.remove(uuid);
  }

  @Deprecated(forRemoval = true)
  public boolean isEmptyLastHoloInRangeExtra() {
    return this.playersInHologramVisibilityRange.isEmpty();
  }

  @Deprecated(forRemoval = true)
  public void clearLastHoloInRangeExtra() {
    this.playersInHologramVisibilityRange.clear();
  }

  @Deprecated(forRemoval = true)
  public boolean containsLastHoloInRangeExtra(UUID uuid) {
    return this.playersInHologramVisibilityRange.contains(uuid);
  }

  @Deprecated(forRemoval = true)
  public Set<UUID> getLastHoloInRangeExtra() {
    return new HashSet<>(this.playersInHologramVisibilityRange);
  }

  @Deprecated(forRemoval = true)
  public void addLastHoloInRangeExtra(UUID uuid) {
    this.playersInHologramVisibilityRange.add(uuid);
  }

  @Deprecated(forRemoval = true)
  public void removeLastHoloInRangeExtra(UUID uuid) {
    if (this.playersInHologramVisibilityRange.remove(uuid)) {
      this.hide(uuid);
    }
  }

  @Deprecated(forRemoval = true)
  public boolean isuSync() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void setuSync(boolean flag) {}

  @Deprecated(forRemoval = true)
  public boolean isSaveToFile() {
    return this.getSettings().isSaveToFile();
  }

  @Deprecated(forRemoval = true)
  public void setSaveToFile(boolean flag) {
    this.getSettings().setSaveToFile(flag);
  }

  @Deprecated(forRemoval = true)
  public void makePersistent() {
    this.getSettings().setSaveToFile(true);
  }

  @Deprecated(forRemoval = true)
  public int getPageCount() {
    return this.getPages().getPageCount();
  }

  @Deprecated(forRemoval = true)
  public boolean isRequiresPermission() {
    return this.getSettings().isRequiresPermission();
  }

  @Deprecated(forRemoval = true)
  public void setPermissionRequirement(boolean flag) {
    this.getSettings().setPermissionRequirement(flag);
  }

  @Deprecated(forRemoval = true)
  public double getPageChangeIntervalSec() {
    return this.getPageSettings().getPageChangeIntervalTicks() / 20.0;
  }

  @Deprecated(forRemoval = true)
  public void setPageChangeIntervalSec(double number) {
    this.getPageSettings().setPageChangeIntervalTicks((int) (number * 20.0));
  }

  @Deprecated(forRemoval = true)
  public boolean isSticky() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void setSticky(boolean flag) {}

  @Deprecated(forRemoval = true)
  public boolean isCheckLineOfSight() {
    return this.getSettings().isCheckLineOfSight();
  }

  @Deprecated(forRemoval = true)
  public void setLOSInterval(double number) {}

  @Deprecated(forRemoval = true)
  public double getLOSupdateIntervalSec() {
    return 0.05;
  }

  @Deprecated(forRemoval = true)
  public boolean isFollowPlayer() {
    return this.getSettings().isCheckLineOfSight();
  }

  @Deprecated(forRemoval = true)
  public void setFollowPlayer(double number) {}

  @Deprecated(forRemoval = true)
  public double getFollowPlayer() {
    return 0.05;
  }

  @Deprecated(forRemoval = true)
  public int getHoveringLine(UUID uuid) {
    return CMIHologramHoverData.getData(uuid).getHoveringLine();
  }

  @Deprecated(forRemoval = true)
  public boolean isShowHoverParticle() {
    return this.getInteractionSettings().isShowHoverParticle();
  }

  @Deprecated(forRemoval = true)
  public void setShowHoverParticle(boolean flag) {
    this.getInteractionSettings().setShowHoverParticle(flag);
  }

  @Deprecated(forRemoval = true)
  public boolean isNewIneractionMethod() {
    return this.getType().equals(CMIHologramType.TextDisplay);
  }

  @Deprecated(forRemoval = true)
  public void setNewIneractionMethod(boolean flag) {
    this.setType(flag ? CMIHologramType.TextDisplay : CMIHologramType.ArmorStand);
  }

  @Deprecated(forRemoval = true)
  public int getParticleCount() {
    return this.getInteractionSettings().getParticleCount();
  }

  @Deprecated(forRemoval = true)
  public void setParticleCount(int value) {
    this.getInteractionSettings().setParticleCount(value);
  }

  @Deprecated(forRemoval = true)
  public double getParticleSpacing() {
    return this.getInteractionSettings().getParticleSpacing();
  }

  @Deprecated(forRemoval = true)
  public void setParticleSpacing(double number) {
    this.getInteractionSettings().setParticleSpacing(number);
  }

  @Deprecated(forRemoval = true)
  public double getExtraParticleHeight() {
    return this.getInteractionSettings().getParticleDimensions().getX();
  }

  @Deprecated(forRemoval = true)
  public void setExtraParticleHeight(double number) {
    this.getInteractionSettings().getParticleDimensions().setX(number);
  }

  @Deprecated(forRemoval = true)
  public double getExtraParticleWidth() {
    return this.getInteractionSettings().getParticleDimensions().getY();
  }

  @Deprecated(forRemoval = true)
  public void setExtraParticleWidth(double number) {
    this.getInteractionSettings().getParticleDimensions().setY(number);
  }

  @Deprecated(forRemoval = true)
  public double getExtraInteractionHeight() {
    return this.getInteractionSettings().getDimensions().getX();
  }

  @Deprecated(forRemoval = true)
  public void setExtraInteractionHeight(double number) {
    this.getInteractionSettings().getDimensions().setX(number);
  }

  @Deprecated(forRemoval = true)
  public double getExtraInteractionWidth() {
    return this.getInteractionSettings().getDimensions().getY();
  }

  @Deprecated(forRemoval = true)
  public void setExtraInteractionWidth(double number) {
    this.getInteractionSettings().getDimensions().setY(number);
  }

  @Deprecated(forRemoval = true)
  public CMIEffect getHoverEffect() {
    return this.getInteractionSettings().getHoverEffect();
  }

  @Deprecated(forRemoval = true)
  public void setHoverEffect(CMIParticle cmiParticle) {
    this.getInteractionSettings().setHoverEffect(cmiParticle);
  }

  @Deprecated(forRemoval = true)
  public double getParticleOffsetX() {
    return this.getInteractionSettings().getParticleOffset().getX();
  }

  @Deprecated(forRemoval = true)
  public void setParticleOffsetX(double number) {
    this.getInteractionSettings().getParticleOffset().setX(number);
  }

  @Deprecated(forRemoval = true)
  public double getParticleOffsetY() {
    return this.getInteractionSettings().getParticleOffset().getY();
  }

  @Deprecated(forRemoval = true)
  public void setParticleOffsetY(double number) {
    this.getInteractionSettings().getParticleOffset().setY(number);
  }

  @Deprecated(forRemoval = true)
  public boolean isStabilizedHoverParticles() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void setStabilizedHoverParticles(boolean flag) {}

  @Deprecated(forRemoval = true)
  public boolean isShowWhenNotPersistent() {
    return false;
  }

  @Deprecated(forRemoval = true)
  public void setShowWhenNotPersistent(boolean flag) {}

  @Deprecated(forRemoval = true)
  public boolean isNewDisplayMethod() {
    return this.getType().equals(CMIHologramType.TextDisplay);
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setNewDisplayMethod(boolean flag) {
    this.setType(flag ? CMIHologramType.TextDisplay : CMIHologramType.ArmorStand);
    return this;
  }

  @Deprecated(forRemoval = true)
  public CMIBillboard getBillboard() {
    return this.getSettings().getBillboard();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setBillboard(CMIBillboard cmiBillboard) {
    this.getSettings().setBillboard(cmiBillboard);
    return this;
  }

  @Deprecated(forRemoval = true)
  public CMIBillboard getIconBillboard() {
    return this.getIconSettings().getBillboard();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setIconBillboard(CMIBillboard cmiBillboard) {
    this.getIconSettings().setBillboard(cmiBillboard);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getYaw() {
    return (int) this.getSettings().getDirection().getY();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setYaw(int value) {
    this.getSettings().setYaw(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getPitch() {
    return (int) this.getSettings().getDirection().getX();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setPitch(int value) {
    this.getSettings().setPitch(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public CMITextAlignment getTextAlignment() {
    return this.getTextSettings().getTextAlignment();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setTextAlignment(CMITextAlignment cmiTextAlignment) {
    this.getTextSettings().setTextAlignment(cmiTextAlignment);
    return this;
  }

  @Deprecated(forRemoval = true)
  public CMIChatColor getBackgroundColor() {
    return this.getTextSettings().getBackgroundColor();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setBackgroundColor(CMIChatColor cmiChatColor) {
    this.getTextSettings().setBackgroundColor(cmiChatColor);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getBackgroundAlpha() {
    return this.getTextSettings().getBackgroundAlpha();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setBackgroundAlpha(int value) {
    this.getTextSettings().setBackgroundAlpha(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getTextAlpha() {
    return this.getTextSettings().getTextAlpha();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setTextAlpha(int value) {
    this.getTextSettings().setTextAlpha(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public boolean isDoubleSided() {
    return this.getTextSettings().isDoubleSided();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setDoubleSided(boolean flag) {
    this.getTextSettings().setDoubleSided(flag);
    return this;
  }

  @Deprecated(forRemoval = true)
  public boolean isShadowed() {
    return this.getTextSettings().isShadowed();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setShadowed(boolean flag) {
    this.getTextSettings().setShadowed(flag);
    return this;
  }

  @Deprecated(forRemoval = true)
  public double getScaleW() {
    return this.getSettings().getScale().getX();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setScaleW(double number) {
    this.getSettings().getScale().setX(number);
    return this;
  }

  @Deprecated(forRemoval = true)
  public double getScaleH() {
    return this.getSettings().getScale().getY();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setScaleH(double number) {
    this.getSettings().getScale().setY(number);
    return this;
  }

  @Deprecated(forRemoval = true)
  public double getIconScale() {
    return this.getIconSettings().getScale().getY();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setIconScale(double number) {
    this.getIconSettings().setScale(number);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getIconPitch() {
    return this.getIconSettings().getPitch();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setIconPitch(int value) {
    this.getIconSettings().setPitch(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getIconYaw() {
    return this.getIconSettings().getYaw();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setIconYaw(int value) {
    this.getIconSettings().setYaw(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getSkyLevel() {
    return this.getSettings().getSkyLevel();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setSkyLevel(int value) {
    this.getSettings().setSkyLevel(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getBlockLevel() {
    return this.getSettings().getBlockLevel();
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setBlockLevel(int value) {
    this.getSettings().setBlockLevel(value);
    return this;
  }

  @Deprecated(forRemoval = true)
  public Player getPlayerToShowFor() {
    return null;
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setPlayerToShowFor(Player player) {
    return this;
  }

  @Deprecated(forRemoval = true)
  public int getSelfDestructIn() {
    return 0;
  }

  @Deprecated(forRemoval = true)
  public CMIHologram setSelfDestructIn(int value) {
    return this;
  }

  @Deprecated(forRemoval = true)
  public void showToPlayer() {}

  @Deprecated(forRemoval = true)
  public boolean isAutoPagination() {
    return this.getPageSettings().isAutoPagination();
  }

  @Deprecated(forRemoval = true)
  public void setAutoPagination(boolean flag) {
    this.getPageSettings().setAutoPagination(flag);
  }

  @Deprecated(forRemoval = true)
  public boolean isSeeThrough() {
    return this.getTextSettings().isSeeThrough();
  }

  @Deprecated(forRemoval = true)
  public void setSeeThrough(boolean flag) {
    this.getTextSettings().setSeeThrough(flag);
  }

  @Deprecated(forRemoval = true)
  public boolean isFadeInAnimation() {
    return this.getAnimationSettings().isFadeInAnimation();
  }

  @Deprecated(forRemoval = true)
  public void setFadeInAnimation(boolean flag) {
    this.getAnimationSettings().setFadeInAnimation(1);
  }

  @Deprecated(forRemoval = true)
  public int getAutoPaginationOffset() {
    return 0;
  }

  @Deprecated(forRemoval = true)
  public void setAutoPaginationOffset(int value) {}

  @Deprecated(forRemoval = true)
  public String getGroup() {
    return this.getSettings().getGroup();
  }

  @Deprecated(forRemoval = true)
  public void setGroup(String string) {
    this.getSettings().setGroup(string);
  }

  @Deprecated(forRemoval = true)
  public float getIconOffsetX() {
    return (float) this.getIconSettings().getOffset().getX();
  }

  @Deprecated(forRemoval = true)
  public void setIconOffsetX(float floatValue) {
    this.getIconSettings().getOffset().setX(floatValue);
  }

  @Deprecated(forRemoval = true)
  public int getLineWidth() {
    return this.getTextSettings().getLineWidth();
  }

  @Deprecated(forRemoval = true)
  public void setLineWidth(int value) {
    this.getTextSettings().setLineWidth(value);
  }

  @Deprecated(forRemoval = true)
  public int getFillerAmount() {
    return this.getTextSettings().getFillerAmount();
  }

  @Deprecated(forRemoval = true)
  public void setFillerAmount(int value) {
    this.getTextSettings().setFillerAmount(value);
  }

  @Deprecated(forRemoval = true)
  public CuboidArea getArea() {
    return new CuboidArea(
        this.getWorld(), this.getUpdateArea().getLowPoint(), this.getUpdateArea().getHighPoint());
  }

  @Deprecated(forRemoval = true)
  public void setArea(CuboidArea cuboidArea) {
    this.updateArea = new CMIBlockArea(cuboidArea.getLowPoint(), cuboidArea.getHighPoint());
  }
}
