package com.Zrips.CMI.Modules.Holograms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIHologramInteraction {
  private boolean interactable = false;
  private CMIVector2D interactionDimensions = new CMIVector2D(0.0, 0.0);
  private CMIVector3D interactionOffset = new CMIVector3D(0.0, 0.0, 0.0);
  private CMIVector2D particleDimensions = new CMIVector2D(0.0, 0.0);
  private CMIVector3D particleOffset = new CMIVector3D(0.0, 0.0, 0.0);
  private short particlePosition = 0;
  private double particleSpacing = 0.1;
  private int particleCount = 1;
  private CMIEffect effect = new CMIEffect(CMIParticle.SMALL_SMOKE);
  private boolean showClickParticle = true;
  private boolean showHoverParticle = true;
  private String basePrefix = "";
  private String hoverPrefix = "";
  private List<String> globalCommands = new ArrayList<>();

  @NotNull
  public List<String> getCommands() {
    return this.globalCommands;
  }

  public void setCommands(@NotNull List<String> list) {
    this.globalCommands.addAll(list);
  }

  public boolean isInteractable() {
    return this.interactable;
  }

  public void setInteractable(boolean flag) {
    this.interactable = flag;
  }

  public CMIVector2D getDimensions() {
    return this.interactionDimensions;
  }

  public void setDimensions(CMIVector2D cmiVector2D) {
    this.interactionDimensions = cmiVector2D;
  }

  public CMIVector2D getParticleDimensions() {
    return this.particleDimensions;
  }

  public void setParticleDimensions(CMIVector2D cmiVector2D) {
    this.particleDimensions = cmiVector2D;
  }

  public short getParticlePosition() {
    return this.particlePosition;
  }

  public void setParticlePosition(short shortValue) {
    this.particlePosition = shortValue;
  }

  public double getParticleSpacing() {
    return this.particleSpacing;
  }

  public void setParticleSpacing(double number) {
    this.particleSpacing = number;
  }

  public int getParticleCount() {
    return this.particleCount;
  }

  public void setParticleCount(int value) {
    this.particleCount = CMINumber.clamp(value, 0, 2000);
  }

  public CMIEffect getHoverEffect() {
    return this.effect;
  }

  public void setHoverEffect(CMIEffect cmiEffect) {
    this.effect = cmiEffect;
  }

  public void setHoverEffect(CMIParticle cmiParticle) {
    this.effect = new CMIEffect(cmiParticle);
    this.effect.setAmount(0);
    this.effect.setSpeed(1.0F);
  }

  public boolean isShowHoverParticle() {
    return this.showHoverParticle;
  }

  public void setShowHoverParticle(boolean flag) {
    this.showHoverParticle = flag;
  }

  public CMIVector3D getParticleOffset() {
    return this.particleOffset;
  }

  public CMIHologramInteraction setParticleOffset(CMIVector3D cmiVector3D) {
    this.particleOffset = cmiVector3D;
    return this;
  }

  public boolean isShowClickParticle() {
    return this.showClickParticle;
  }

  public CMIHologramInteraction setShowClickParticle(boolean flag) {
    this.showClickParticle = flag;
    return this;
  }

  @NotNull
  public String getBasePrefix() {
    return this.basePrefix;
  }

  public CMIHologramInteraction setBasePrefix(@NotNull String string) {
    this.basePrefix = string;
    return this;
  }

  @NotNull
  public String getHoverPrefix() {
    return this.hoverPrefix;
  }

  public CMIHologramInteraction setHoverPrefix(@NotNull String string) {
    this.hoverPrefix = string;
    return this;
  }

  @Nullable
  public Map<String, Object> serialize() {
    HashMap hashMap = new HashMap();
    if (this.isInteractable()) {
      hashMap.put("Enabled", true);
    }

    if (this.getDimensions().getX() != 0.0 || this.getDimensions().getY() != 0.0) {
      hashMap.put("Dimension", this.getDimensions().toString());
    }

    if (this.getOffset().getX() != 0.0
        || this.getOffset().getY() != 0.0
        || this.getOffset().getZ() != 0.0) {
      hashMap.put("Offset", this.getDimensions().toString());
    }

    if (!this.getCommands().isEmpty()) {
      hashMap.put("Commands", this.getCommands());
    }

    if (this.getBasePrefix() != null && !this.getBasePrefix().isEmpty()) {
      hashMap.put("BasePrefix", this.getBasePrefix());
    }

    if (this.getHoverPrefix() != null && !this.getHoverPrefix().isEmpty()) {
      hashMap.put("HoverPrefix", this.getHoverPrefix());
    }

    HashMap particleMap = new HashMap();
    if (!this.isShowClickParticle()) {
      particleMap.put("ShowClick", false);
    }

    if (this.getParticleSpacing() != 0.1) {
      particleMap.put("Spacing", this.getParticleSpacing());
    }

    if (this.getParticleCount() != 1) {
      particleMap.put("Count", this.getParticleCount());
    }

    if (this.getHoverEffect().getParticle() != CMIParticle.SMALL_SMOKE) {
      particleMap.put("Effect", this.getHoverEffect().serialize());
    }

    if (!this.isShowHoverParticle()) {
      particleMap.put("Hover", false);
    }

    if (this.getParticleDimensions().getX() != 0.0 || this.getParticleDimensions().getY() != 0.0) {
      particleMap.put("Dimension", this.getParticleDimensions().toString());
    }

    if (this.getParticleOffset().getX() != 0.0
        || this.getParticleOffset().getY() != 0.0
        || this.getParticleOffset().getZ() != 0.0) {
      particleMap.put("Offset", this.getParticleOffset().toString());
    }

    if (!particleMap.isEmpty()) {
      hashMap.put("Particle", particleMap);
    }

    return hashMap.isEmpty() ? null : hashMap;
  }

  @NotNull
  public static CMIHologramInteraction deserialize(Map<String, Object> map) {
    CMIHologramInteraction cmiHologramInteraction = new CMIHologramInteraction();
    if (map.get("Enabled") instanceof Boolean) {
      cmiHologramInteraction.setInteractable((Boolean) map.get("Enabled"));
    }

    if (map.get("Dimension") instanceof String) {
      cmiHologramInteraction.setDimensions(CMIVector2D.fromString((String) map.get("Dimension")));
    }

    if (map.get("Offset") instanceof String) {
      cmiHologramInteraction.setOffset(CMIVector3D.fromString((String) map.get("Offset")));
    }

    if (map.get("Particle") instanceof MemorySection) {
      var particleSection = ((MemorySection) map.get("Particle")).getValues(false);
      if (particleSection.get("ShowClick") instanceof Boolean) {
        cmiHologramInteraction.setShowClickParticle((Boolean) particleSection.get("ShowClick"));
      }

      if (particleSection.get("Hover") instanceof Boolean) {
        cmiHologramInteraction.setShowHoverParticle((Boolean) particleSection.get("Hover"));
      }

      if (particleSection.get("Dimension") instanceof String) {
        cmiHologramInteraction.setParticleDimensions(
            CMIVector2D.fromString((String) particleSection.get("Dimension")));
      }

      if (particleSection.get("Offset") instanceof String) {
        cmiHologramInteraction.setParticleOffset(
            CMIVector3D.fromString((String) particleSection.get("Offset")));
      }

      if (particleSection.get("Spacing") instanceof Double) {
        cmiHologramInteraction.setParticleSpacing((Double) particleSection.get("Spacing"));
      }

      if (particleSection.get("Count") instanceof Integer) {
        cmiHologramInteraction.setParticleCount((Integer) particleSection.get("Count"));
      }

      if (particleSection.get("Effect") instanceof String) {
        cmiHologramInteraction.setHoverEffect(
            CMIEffect.deserialize((String) particleSection.get("Effect")));
      }
    }

    if (map.get("Commands") instanceof List) {
      cmiHologramInteraction.setCommands((List<String>) map.get("Commands"));
    }

    if (map.get("BasePrefix") instanceof String) {
      cmiHologramInteraction.setBasePrefix((String) map.get("BasePrefix"));
    }

    if (map.get("HoverPrefix") instanceof String) {
      cmiHologramInteraction.setHoverPrefix((String) map.get("HoverPrefix"));
    }

    return cmiHologramInteraction;
  }

  public CMIVector3D getOffset() {
    return this.interactionOffset;
  }

  public void setOffset(CMIVector3D cmiVector3D) {
    this.interactionOffset = cmiVector3D;
  }
}
