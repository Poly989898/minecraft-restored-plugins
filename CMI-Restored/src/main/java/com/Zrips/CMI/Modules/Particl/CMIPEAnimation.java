package com.Zrips.CMI.Modules.Particl;

import java.util.List;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIPEAnimation implements CMIPEAnimationInterface {
  protected Location center;
  protected boolean fixedLocation = false;
  protected CMIEffect effect = new CMIEffect(CMIParticle.DUST);
  protected Vector offset = new Vector(0, 0, 0);
  protected int updateTimes = 0;
  protected double duration = 5.0;
  private Player playerMove;
  private boolean hideWithVanish = false;
  private int interval = 1;

  public CMIPEAnimation() {}

  @Override
  public Player getPlayerMove() {
    return this.playerMove;
  }

  public void setPlayerMove(Player player) {
    this.playerMove = player;
  }

  public CMIPEAnimation(CMIEffect cmiEffect) {
    this.effect = cmiEffect;
  }

  public CMIPEAnimation(CMIParticle cmiParticle) {
    this.effect.setParticle(cmiParticle);
  }

  @Override
  public boolean render(List<Player> list) {
    return true;
  }

  @Override
  public Location getCenter() {
    return this.center;
  }

  @Override
  public void setCenter(Location location) {
    this.center = location;
  }

  public Vector getOffset() {
    return this.offset;
  }

  public void setOffset(Vector vector) {
    this.offset = vector;
  }

  public CMIEffect getCMIEffect() {
    return this.effect;
  }

  public void setCMIEffect(CMIEffect cmiEffect) {
    this.effect = cmiEffect;
  }

  @Deprecated
  public CMIParticle getEffect() {
    return this.getParticle();
  }

  @Deprecated
  public void setEffect(CMIParticle cmiParticle) {
    this.setParticle(cmiParticle);
  }

  public CMIParticle getParticle() {
    return this.effect.getParticle();
  }

  public void setParticle(CMIParticle cmiParticle) {
    this.effect.setParticle(cmiParticle);
  }

  public Color getColor() {
    return this.effect.getColorFrom();
  }

  @Override
  public void setColor(java.awt.Color color) {
    this.effect.setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
  }

  @Override
  public void setColor(Color color) {
    this.effect.setColor(color);
  }

  @Override
  public void setColor(CMIChatColor cmiChatColor) {
    this.effect.setColorFrom(
        Color.fromRGB(cmiChatColor.getRed(), cmiChatColor.getGreen(), cmiChatColor.getBlue()));
  }

  @Override
  public void setDuration(double number) {
    this.duration = number;
  }

  @Override
  public double getDuration() {
    return this.duration;
  }

  @Override
  public boolean isFixedLocation() {
    return this.fixedLocation;
  }

  public void setFixedLocation(boolean flag) {
    this.fixedLocation = flag;
  }

  @Override
  public void show() {
    CMIVisualEffect cmiVisualEffect = new CMIVisualEffect(this.center, this);
    cmiVisualEffect.setUntil(System.currentTimeMillis() + (int) (1000.0 * this.duration));
    cmiVisualEffect.show();
  }

  @Override
  public int getInterval() {
    return this.interval;
  }

  public void setInterval(int value) {
    this.interval = value;
  }

  @Override
  public boolean isHideWithVanish() {
    return this.hideWithVanish;
  }

  public void setHideWithVanish(boolean flag) {
    this.hideWithVanish = flag;
  }
}
