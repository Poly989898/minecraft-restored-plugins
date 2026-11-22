package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramAnimationSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramBoardSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramIconSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramPageSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Logs.CMIDebug;
import org.bukkit.entity.Player;

public class CMIHologramProcessor {
  protected CMIHologram baseHologram = null;

  public CMIHologram getHologramBase() {
    return this.baseHologram;
  }

  public CMIHologramProcessor(CMIHologram cmiHologram) {
    this.baseHologram = cmiHologram;
  }

  public CompletableFuture<Void> requestFullUpdate() {
    return this.requestUpdate(
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
    return this.getHologramBase().requestUpdate(cmiHologramUpdateTypes);
  }

  public CompletableFuture<Void> requestUpdate(UUID uuid) {
    return this.getHologramBase().requestUpdate(uuid);
  }

  public CompletableFuture<Void> requestUpdate(
      UUID uuid, CMIHologramUpdateType... cmiHologramUpdateTypes) {
    return this.getHologramBase().requestUpdate(uuid, cmiHologramUpdateTypes);
  }

  public CompletableFuture<Void> hide(UUID uuid) {
    return this.getHologramBase().hide(uuid);
  }

  public void show(Player player) {
    this.getHologramBase().show(player);
  }

  public CompletableFuture<Void> instantHide() {
    return this.getHologramBase().instantHide();
  }

  public CMIHologramSettings getSettings() {
    return this.getHologramBase().getSettings();
  }

  public CMIHologramIconSettings getIconSettings() {
    return this.getHologramBase().getIconSettings();
  }

  public CMIHologramBoardSettings getBoardSettings() {
    return this.getHologramBase().getBoardSettings();
  }

  public CMIHologramAnimationSettings getAnimationSettings() {
    return this.getHologramBase().getAnimationSettings();
  }

  public CMIHologramPageSettings getPageSettings() {
    return this.getHologramBase().getPageSettings();
  }

  public CMIHologramHoverModule getHoverModule() {
    return this.getHologramBase().getHoverModule();
  }

  public CMIHologramTextSettings getTextSettings() {
    return this.getHologramBase().getTextSettings();
  }

  public CMIHologramInteraction getInteractionSettings() {
    return this.getHologramBase().getInteractionSettings();
  }

  public CMILocation getLocation() {
    return this.getHologramBase().getLocation();
  }

  public String getName() {
    return this.getHologramBase().getName();
  }

  public List<String> getCommands() {
    return this.getHologramBase().getInteractionSettings().getCommands();
  }

  public List<String> getLines() {
    return this.getHologramBase().getPages().getLines();
  }

  public boolean isEnabled() {
    return this.getHologramBase().isEnabled();
  }

  CompletableFuture<Void> updateBillboard(UUID uuid, Set<CMIHologramUpdateType> set) {
    return new CompletableFuture<>();
  }

  void onPageChange(UUID uuid) {}

  protected String processTextLine(Player player, CMIHologramLine cmiHologramLine) {
    CMIHologramLinePortion cmiHologramLinePortion =
        CMIHologramHoverData.getData(player).getHoveringLinePortion();
    StringBuilder stringBuilder = new StringBuilder();

    for (CMIHologramLinePortion linePortion : cmiHologramLine.getLinePortions()) {
      stringBuilder.append("§r");
      if (!(linePortion.getBaseLineData() instanceof CMIHologramLineStaticText)) {
        CMIDebug.c(
            new Object[] {
              "Fail to recognize line portion: ", linePortion.getBaseLineData().getOriginalText()
            });
      } else {
        stringBuilder.append(this.getTextLine(cmiHologramLinePortion, linePortion, player));
      }
    }

    return CMI.getInstance()
        .getPlaceholderAPIManager()
        .updatePlaceHolders(
            player, stringBuilder.toString().isEmpty() ? " " : stringBuilder.toString());
  }

  protected StringBuilder getTextLine(
      CMIHologramLinePortion cmiHologramLinePortion,
      CMIHologramLinePortion linePortion,
      Player player) {
    StringBuilder stringBuilder = new StringBuilder();
    CMIHologramLineStaticText cmiHologramLineStaticText =
        (CMIHologramLineStaticText) linePortion.getBaseLineData();
    if (linePortion.equals(cmiHologramLinePortion)) {
      if (linePortion.getClickActions() != null) {
        String string = this.getHologramBase().getInteractionSettings().getHoverPrefix();
        if (string.isEmpty()) {
          string = HologramManager.getHoverPrefix();
        }

        stringBuilder.append(string);
        if (linePortion.getHoverLineData() != null) {
          stringBuilder.append(linePortion.getHoverLineData().getLineOutput(player));
        } else {
          stringBuilder.append(cmiHologramLineStaticText.getLineOutput(player));
        }
      } else if (linePortion.getHoverLineData() != null) {
        stringBuilder.append(linePortion.getHoverLineData().getLineOutput(player));
      } else {
        stringBuilder.append(cmiHologramLineStaticText.getLineOutput(player));
      }
    } else {
      if (linePortion.getClickActions() != null) {
        String basePrefix = this.getHologramBase().getInteractionSettings().getBasePrefix();
        if (basePrefix.isEmpty()) {
          basePrefix = HologramManager.getBasePrefix();
        }

        stringBuilder.append(basePrefix);
      }

      stringBuilder.append(cmiHologramLineStaticText.getLineOutput(player));
    }

    return stringBuilder;
  }

  protected boolean isHiding(Set<CMIHologramUpdateType> set) {
    return set.contains(CMIHologramUpdateType.Hide)
        || set.contains(CMIHologramUpdateType.InstantHide)
        || set.contains(CMIHologramUpdateType.ShadowHide);
  }

  protected boolean isShowing(Set<CMIHologramUpdateType> set) {
    return set.contains(CMIHologramUpdateType.Show)
        || set.contains(CMIHologramUpdateType.InstantShow);
  }
}
