package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import com.Zrips.CMI.commands.list.hologram;
import java.awt.Color;
import java.util.Random;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.entity.Player;

public class CMIHologramLineStaticText extends CMIHologramLineBase {
  protected boolean randomU = false;
  protected Float hue = null;
  protected boolean filler = false;
  private static final String fillerBaseString = "%filler%";
  CMIHologramTextSettings settings = null;

  public CMIHologramLineStaticText clone() {
    return new CMIHologramLineStaticText();
  }

  public CMIHologramLineStaticText(CMIHologramTextSettings cmiHologramTextSettings, String string) {
    super(string);
    this.setType(CMIHoloLineType.plainText);
    this.settings = cmiHologramTextSettings;
    this.filler = string.contains("%filler%");
    if (string.contains("&u") || string.contains("§u")) {
      this.randomU = true;
    }
  }

  public CMIHologramLineStaticText() {}

  @Override
  public String getLineOutput(Player player) {
    return this.checkText(player, this.getOriginalText());
  }

  private String checkText(Player player, String string) {
    if (this.randomU) {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
        if (this.hue == null) {
          this.hue = new Random().nextFloat();
        }

        this.hue = this.hue + 0.05F;
        this.hue = this.hue > 1.0F ? 0.0F : this.hue;
        Color color = Color.getHSBColor(this.hue, 0.9F, 0.9F);
        String hexCode =
            Integer.toHexString(
                (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue() & 16777215);

        while (hexCode.length() < 6) {
          hexCode = "0" + hexCode;
        }

        string = string.replace("&u", "{#" + hexCode + "}");
      } else {
        string = string.replace("&u", CMIChatColor.getRandomColor().getColorCode());
      }
    }

    if (this instanceof CMIHologramLineDynamicText) {
      string = CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
      string = this.processFiller(string);
    }

    return string != null && !string.equalsIgnoreCase("null") ? string : "";
  }

  private String processFiller(String string) {
    if (this.settings == null || this.settings.getFillerAmount() <= 0) {
      return string;
    }

    if (!this.filler) {
      return string;
    }

    String filler = CMILC.getIM(hologram.class, "filler");
    double number = CMIFont.getWidth(filler);
    String fillerSample =
        CMIChatColor.stripColor(CMIComponentBase.stripMiniMessageTags(string))
            .replace("%filler%", "");
    double fillerSampleWidth = CMIFont.getWidth(fillerSample);
    double fillerCount =
        CMINumber.clamp((this.settings.getFillerAmount() - fillerSampleWidth) / number, 0.0, 999);
    StringBuilder stringBuilder = new StringBuilder();

    for (int value = 0; value < fillerCount; value++) {
      stringBuilder.append(filler);
    }

    return string.replace(
        "%filler%", CMIChatColor.DARK_GRAY + stringBuilder.toString() + CMIChatColor.RESET);
  }
}
