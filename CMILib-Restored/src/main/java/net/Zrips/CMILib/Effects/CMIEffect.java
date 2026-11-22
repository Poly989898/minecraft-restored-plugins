package net.Zrips.CMILib.Effects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIEffect {
  private CMIEffectManager.CMIParticle particle;
  private int amount = 1;
  private float speed = 0.0F;
  private Vector offset = new Vector();
  private CMIParticleOptions options = null;
  private Object particleParameters = null;
  private static final String VECTOR_REGEX = "[^;{}]*(?:\\{[^}]*\\}[^;{}]*)*";
  private static final Pattern VECTOR_PATTERN =
      Pattern.compile("[^;{}]*(?:\\{[^}]*\\}[^;{}]*)*", 8);

  public CMIEffect(CMIEffectManager.CMIParticle particle) {
    this.particle = particle;
    this.rebuildOptions();
  }

  private void rebuildOptions() {
    switch (this.getParticle().getDataType()) {
      case BlockData:
        this.options = CMIParticleOptions.buildBlockData(null);
        break;
      case Color:
        this.options = CMIParticleOptions.buildColor(null);
        break;
      case DustOptions:
        this.options = CMIParticleOptions.buildDustOptions(null, 1.0F);
        break;
      case DustTransition:
        this.options = CMIParticleOptions.buildDustTransition(null, null, 1.0F);
        break;
      case Float:
        this.options = CMIParticleOptions.buildFloat(0.0F);
        break;
      case Int:
        this.options = CMIParticleOptions.buildInteger(0);
        break;
      case ItemStack:
        this.options = CMIParticleOptions.buildItemStack(null);
        break;
      case Spell:
        this.options = CMIParticleOptions.buildSpell(null, 1.0F);
        break;
      case Trail:
        this.options = CMIParticleOptions.buildTrail(null, null, 1);
        break;
      case Vibration:
        this.options = CMIParticleOptions.buildVibration(null, null, 1);
        break;
      case Void:
      default:
        this.options = CMIParticleOptions.buildEmpty();
    }
  }

  public CMIEffectManager.CMIParticle getParticle() {
    if (this.particle == null) {
      this.particle = CMIEffectManager.CMIParticle.DUST;
      this.rebuildOptions();
    }

    return this.particle;
  }

  @NotNull
  public CMIParticleOptions getOptions() {
    return this.options;
  }

  public void setParticle(CMIEffectManager.CMIParticle particle) {
    this.particle = particle;
    this.particleParameters = null;
    this.rebuildOptions();
  }

  @Deprecated
  @Nullable
  public Color getColor() {
    return this.getColorFrom();
  }

  @Deprecated
  public void setColor(Color color) {
    this.setColorFrom(color);
  }

  @Deprecated
  @Nullable
  public Color getColorFrom() {
    return this.getOptions() instanceof CMIParticleColor
        ? ((CMIParticleColor) this.getOptions()).getColor()
        : null;
  }

  @Deprecated
  public void setColorFrom(Color color) {
    if (this.getOptions() instanceof CMIParticleColor) {
      ((CMIParticleColor) this.getOptions()).setColor(color);
    }
  }

  @Deprecated
  @Nullable
  public Color getColorTo() {
    return this.getOptions() instanceof CMIParticleDustTransition
        ? ((CMIParticleDustTransition) this.getOptions()).getColorTo()
        : null;
  }

  @Deprecated
  public void setColorTo(Color color) {
    if (this.getOptions() instanceof CMIParticleDustTransition) {
      ((CMIParticleDustTransition) this.getOptions()).setColorTo(color);
    }
  }

  public Vector getOffset() {
    return this.offset;
  }

  public void setOffset(Vector offset) {
    this.offset = offset;
  }

  public int getAmount() {
    return this.amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public float getSpeed() {
    return this.speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  @Deprecated
  public int getSize() {
    return this.getOptions() instanceof CMIParticleDustOptions
        ? (int) ((CMIParticleDustOptions) this.getOptions()).getSize()
        : 1;
  }

  @Deprecated
  public void setSize(int size) {
    if (this.getOptions() instanceof CMIParticleDustOptions) {
      ((CMIParticleDustOptions) this.getOptions()).setSize(size);
    }
  }

  @Deprecated
  @Nullable
  public CMIMaterial getMaterial() {
    return this.getOptions() instanceof CMIParticleMaterial
        ? ((CMIParticleMaterial) this.getOptions()).getMaterial()
        : null;
  }

  @Deprecated
  public void setMaterial(CMIMaterial material) {
    if (this.getOptions() instanceof CMIParticleMaterial) {
      ((CMIParticleMaterial) this.getOptions()).setMaterial(material);
    }
  }

  @Deprecated
  public int getDuration() {
    return this.getOptions() instanceof CMIParticleTrail
        ? ((CMIParticleTrail) this.getOptions()).getDuration()
        : 1;
  }

  @Deprecated
  public void setDuration(int duration) {
    if (this.getOptions() instanceof CMIParticleTrail) {
      ((CMIParticleTrail) this.getOptions()).setDuration(duration);
    }
  }

  public static CMIEffect deserialize(String name) {
    return get(name);
  }

  public static CMIEffect get(String name) {
    if (name == null) {
      return null;
    }

    name = name.replace("_", "").toLowerCase();
    CMIMaterial mat = CMIMaterial.NONE;
    Color colorFrom = null;
    Color colorTo = null;
    float size = 0.0F;
    String sub = null;
    if (name.contains(";")) {
      sub = name.split(";", 2)[1];
      name = name.split(";", 2)[0];
    }

    CMIEffectManager.CMIParticle cmiParticle = CMIEffectManager.CMIParticle.get(name);
    if (cmiParticle == null) {
      return null;
    }

    CMIEffect cmiEffect = new CMIEffect(cmiParticle);
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && cmiEffect.getParticle() == null) {
      return null;
    }

    if (Version.isCurrentLower(Version.v1_13_R1) && cmiEffect.getParticle().getEffect() == null) {
      return null;
    }

    if (sub == null) {
      return cmiEffect;
    }

    Matcher matcher = VECTOR_PATTERN.matcher(sub);
    List<String> parts = new ArrayList<>();

    while (matcher.find()) {
      String part = matcher.group();
      if (!part.isEmpty()) {
        parts.add(part);
      }
    }

    String[] split = parts.toArray(new String[0]);

    for (String one : split) {
      if (one.startsWith("a{") && one.endsWith("}")) {
        try {
          int amount = Integer.parseInt(one.substring(2, one.length() - 1));
          cmiEffect.setAmount(amount);
          continue;
        } catch (Throwable var25) {
        }
      }

      if (one.startsWith("s{") && one.endsWith("}")) {
        try {
          float speed = Float.parseFloat(one.substring(2, one.length() - 1));
          cmiEffect.setSpeed(speed);
          continue;
        } catch (Throwable var24) {
        }
      }

      if (one.startsWith("o{") && one.endsWith("}")) {
        try {
          String temp = one.substring(2, one.length() - 1);
          CMIVector3D loc = CMIVector3D.fromString(temp);
          if (loc != null) {
            cmiEffect.setOffset(loc.toVector());
          }
          continue;
        } catch (Throwable var23) {
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleTrail) {
        if (!one.contains("{")) {
          try {
            int duration = Integer.parseInt(one);
            ((CMIParticleTrail) cmiEffect.getOptions()).setDuration(duration);
            continue;
          } catch (Throwable var22) {
          }
        }

        Color color = processColor(one);
        if (color != null) {
          ((CMIParticleTrail) cmiEffect.getOptions()).setColor(color);
          continue;
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleColor && colorFrom == null) {
        colorFrom = processColor(one);
        if (colorFrom != null) {
          ((CMIParticleColor) cmiEffect.getOptions()).setColor(colorFrom);
          continue;
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleDustTransition
          && colorTo == null
          && colorFrom != null) {
        colorTo = processColor(one);
        if (colorTo != null) {
          ((CMIParticleDustTransition) cmiEffect.getOptions()).setColorTo(colorTo);
          continue;
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleMaterial && mat.equals(CMIMaterial.NONE)) {
        mat = CMIMaterial.get(one);
        if (!mat.equals(CMIMaterial.NONE)) {
          if (cmiEffect.getOptions() instanceof CMIParticleBlockData) {
            ((CMIParticleBlockData) cmiEffect.getOptions()).setMaterial(mat);
          } else if (cmiEffect.getOptions() instanceof CMIParticleItemStack) {
            ((CMIParticleItemStack) cmiEffect.getOptions()).setMaterial(mat);
          }
          continue;
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleLocation
          && ((CMIParticleLocation) cmiEffect.getOptions()).getOffset().isZero()) {
        try {
          String temp = one.replace(",", ";").replace("{", "").replace("}", "");
          CMIVector3D loc = CMIVector3D.fromString(temp);
          if (loc != null) {
            ((CMIParticleLocation) cmiEffect.getOptions()).setOffset(loc);
            continue;
          }
        } catch (Throwable var21) {
        }
      }

      if (cmiEffect.getOptions() instanceof CMIParticleVibration
          && ((CMIParticleVibration) cmiEffect.getOptions()).getDestination() == null) {
        try {
          String temp = one.replace(",", ";").replace("{", "").replace("}", "");
          CMIVector3D loc = CMIVector3D.fromString(temp);
          if (loc != null) {
            ((CMIParticleVibration) cmiEffect.getOptions()).setDestination(loc);
            continue;
          }
        } catch (Throwable var20) {
        }
      }

      if (size == 0.0F && cmiEffect.getOptions() instanceof CMIParticleDustOptions) {
        try {
          size = Float.parseFloat(one);
          ((CMIParticleDustOptions) cmiEffect.getOptions()).setSize(size);
        } catch (Throwable var19) {
        }
      } else if (size == 0.0F && cmiEffect.getOptions() instanceof CMIParticleFloat) {
        try {
          size = Float.parseFloat(one);
          ((CMIParticleFloat) cmiEffect.getOptions()).setValue(size);
        } catch (Throwable var18) {
        }
      } else if (size == 0.0F && cmiEffect.getOptions() instanceof CMIParticleInteger) {
        try {
          size = Integer.parseInt(one);
          ((CMIParticleInteger) cmiEffect.getOptions()).setValue((int) size);
        } catch (Throwable var17) {
        }
      }
    }

    return cmiEffect;
  }

  private boolean isZero(Vector vector) {
    return vector.getX() == 0.0 && vector.getY() == 0.0 && vector.getZ() == 0.0;
  }

  public String serialize() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getParticle().getName().replace(" ", ""));
    if (this.getAmount() != 1) {
      sb.append(";a{").append(this.getAmount()).append("}");
    }

    if (this.getSpeed() != 0.0F) {
      sb.append(";s{").append(this.getSpeed()).append("}");
    }

    if (!this.isZero(this.getOffset())) {
      sb.append(";o{").append(new CMIVector3D(this.getOffset()).toString()).append("}");
    }

    if (this.getOptions() instanceof CMIParticleDustOptions) {
      float size = ((CMIParticleDustOptions) this.getOptions()).getSize();
      if (size != 1.0F) {
        sb.append(";").append(size);
      }
    }

    if (this.getOptions() instanceof CMIParticleLocation) {
      CMIVector3D offset = ((CMIParticleLocation) this.getOptions()).getOffset();
      if (!offset.isZero()) {
        sb.append(";{").append(offset.toString()).append("}");
      }
    }

    if (this.getOptions() instanceof CMIParticleDustTransition) {
      Color colorFrom = ((CMIParticleDustTransition) this.getOptions()).getColor();
      if (colorFrom.getRed() != 200 || colorFrom.getGreen() != 0 || colorFrom.getBlue() != 50) {
        CMIChatColor cmiColorFrom = new CMIChatColor(colorFrom);
        sb.append(";").append(cmiColorFrom.getFormatedHex());
      }

      Color colorTo = ((CMIParticleDustTransition) this.getOptions()).getColorTo();
      if (colorTo.getRed() != 50 || colorTo.getGreen() != 0 || colorTo.getBlue() != 200) {
        CMIChatColor cmiColorTo = new CMIChatColor(colorTo);
        sb.append(";").append(cmiColorTo.getFormatedHex());
      }
    } else if (this.getOptions() instanceof CMIParticleColor) {
      Color colorTo = ((CMIParticleColor) this.getOptions()).getColor();
      if (colorTo.getRed() != 200 || colorTo.getGreen() != 0 || colorTo.getBlue() != 50) {
        CMIChatColor cmiColorTo = new CMIChatColor(colorTo);
        sb.append(";").append(cmiColorTo.getFormatedHex());
      }
    }

    if (this.getOptions() instanceof CMIParticleMaterial) {
      CMIMaterial mat = null;
      if (this.getOptions() instanceof CMIParticleBlockData) {
        mat = ((CMIParticleBlockData) this.getOptions()).getMaterial();
      } else if (this.getOptions() instanceof CMIParticleItemStack) {
        mat = ((CMIParticleItemStack) this.getOptions()).getMaterial();
      }

      if (mat != null && !mat.equals(CMIMaterial.NONE)) {
        sb.append(";").append(mat.getName());
      }
    }

    if (this.getOptions() instanceof CMIParticleTrail) {
      int duration = ((CMIParticleTrail) this.getOptions()).getDuration();
      if (duration != 1) {
        sb.append(";").append(duration);
      }

      Color colorFrom = ((CMIParticleTrail) this.getOptions()).getColor();
      if (colorFrom.getRed() != 200 || colorFrom.getGreen() != 0 || colorFrom.getBlue() != 50) {
        CMIChatColor cmiColorTo = new CMIChatColor(colorFrom);
        sb.append(";").append(cmiColorTo.getFormatedHex());
      }
    }

    if (this.getOptions() instanceof CMIParticleVibration) {
      CMIVector3D dest = ((CMIParticleVibration) this.getOptions()).getDestination();
      if (dest != null) {
        sb.append(";{").append(dest.toString()).append("}");
      }
    }

    if (this.getOptions() instanceof CMIParticleFloat) {
      float value = ((CMIParticleFloat) this.getOptions()).getValue();
      if (value != 0.0F) {
        sb.append(";").append(value);
      }
    }

    if (this.getOptions() instanceof CMIParticleInteger) {
      int value = ((CMIParticleInteger) this.getOptions()).getValue();
      if (value != 0) {
        sb.append(";").append(value);
      }
    }

    return sb.toString().replace(" ", "");
  }

  private static Color processColor(String colorString) {
    if (colorString.contains(",")) {
      String[] split = colorString.split(",");

      try {
        return Color.fromRGB(
            Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
      } catch (Throwable var3) {
      }
    }

    CMIChatColor c = CMIChatColor.getColor(colorString);
    if (c != null) {
      return c.getRGBColor();
    }

    CMIChatColor cmicolor = CMIChatColor.getColor(colorString);
    return cmicolor != null
        ? Color.fromRGB(cmicolor.getRed(), cmicolor.getGreen(), cmicolor.getBlue())
        : null;
  }

  public void show(Object playerConnection, Location location) {
    CMIEffectManager.playEffect(playerConnection, location, this);
  }

  public void show(Player player, Location location) {
    CMIEffectManager.playEffect(player, location, this);
  }

  @Nullable
  public Object getParticleParameter(@Nullable Location location) {
    return CMIEffectManager.getParticleParameter(location, this);
  }

  @Nullable
  public Object getParticleOptions(@Nullable Location location) {
    if (this.particleParameters == null || this.getOptions() instanceof CMIParticleLocation) {
      this.particleParameters = CMIEffectManager.getParticleOptions(location, this);
    }

    return this.particleParameters;
  }

  @Nullable
  public Object generateParameterObject(@Nullable Location location) {
    return CMIEffectManager.getParticleParameter(location, this);
  }

  public void setParticleParameters(Object particleParameters) {
    this.particleParameters = particleParameters;
  }

  public void resetParticleParameters(@Nullable Location location) {
    this.particleParameters = CMIEffectManager.getParticleOptions(location, this);
  }
}
