package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Images.CMIImage;
import org.bukkit.entity.Player;

public class CMIHologramLineImage extends CMIHologramLineBase {
  protected List<String> image = null;

  public CMIHologramLineImage clone() {
    return new CMIHologramLineImage();
  }

  public CMIHologramLineImage(String string) {
    super(string);
    this.setType(CMIHoloLineType.image);
    var completableFuture =
        CMIImage.convertLines(
            null, Config.imageFolder, new ArrayList<>(Arrays.asList(string)), false);
    completableFuture.thenApply(
        object -> {
          this.image = object;
          return null;
        });
  }

  public CMIHologramLineImage() {}

  @Override
  public String getLineOutput(Player player) {
    return this.getOriginalText();
  }

  public List<String> getImage() {
    return this.image;
  }

  public CompletableFuture<List<String>> getImage(Player player) {
    return this.isDynamic()
        ? CMIImage.convertLines(
            player,
            CMI.getInstance().getDataFolder().getPath(),
            new ArrayList<>(Arrays.asList(this.getOriginalText())),
            true)
        : CompletableFuture.supplyAsync(() -> this.getImage());
  }
}
