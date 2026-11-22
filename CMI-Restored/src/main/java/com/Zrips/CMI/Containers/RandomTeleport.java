package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.World;

public class RandomTeleport {
  private boolean enabled = true;
  private int MaxDistance;
  private int MinDistance;
  private CMILocation loc;
  private boolean circle = false;
  private boolean ignoreWater = true;
  private boolean ignoreLava = true;
  private boolean ignorePowderSnow = true;
  private boolean ignoreLeaves = true;
  private boolean surfaceOnly = false;
  private boolean IgnoreCrawl = false;
  private boolean fromAnotherWorld = true;
  private boolean requireWorldPermission = false;
  private Set<CMIMaterial> ignoredMaterials = new HashSet<>();
  private Set<String> ignoredBiomes = new HashSet<>();
  private Set<String> preferredBiomes = new HashSet<>();
  private int maxTries = 10;
  private int MaxY = 256;
  private int MinY = 0;
  static Random random = new Random(System.nanoTime());
  private int cooldown = 0;

  public RandomTeleport(boolean flag, int value, int count, CMILocation cmiLocation) {
    this.loc = cmiLocation;
    this.MaxDistance = value;
    this.MinDistance = count;
    this.enabled = flag;
  }

  public RandomTeleport(World world, int value, int count, int countSecond, int countThird) {
    this(true, value, count, new CMILocation(world, countSecond, 63.0, countThird));
  }

  public RandomTeleport(World world) {
    this(true, 100, 0, new CMILocation(world, 0.0, 63.0, 0.0));
  }

  public Location getCenter() {
    return this.loc;
  }

  @Deprecated
  public int getMaxCord() {
    return this.MaxDistance;
  }

  public int getMaxDistance() {
    return this.MaxDistance;
  }

  @Deprecated
  public int getMinCord() {
    return this.MinDistance;
  }

  public int getMinDistance() {
    return this.MinDistance;
  }

  public boolean isCircle() {
    return this.circle;
  }

  public RandomTeleport setCircle(boolean flag) {
    this.circle = flag;
    return this;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public RandomTeleport setEnabled(boolean flag) {
    this.enabled = flag;
    return this;
  }

  public RandomTeleport setMinDistance(int value) {
    this.MinDistance = value;
    return this;
  }

  public RandomTeleport setMaxDistance(int value) {
    this.MaxDistance = value;
    return this;
  }

  public RandomTeleport setCenter(CMILocation cmiLocation) {
    this.loc = cmiLocation;
    return this;
  }

  public boolean isIgnoreWater() {
    return this.ignoreWater;
  }

  public RandomTeleport setIgnoreWater(boolean flag) {
    this.ignoreWater = flag;
    return this;
  }

  public boolean isIgnoreLava() {
    return this.ignoreLava;
  }

  public RandomTeleport setIgnoreLava(boolean flag) {
    this.ignoreLava = flag;
    return this;
  }

  public int getMaxY() {
    return this.MaxY;
  }

  public RandomTeleport setMaxY(int value) {
    this.MaxY = value;
    return this;
  }

  public int getMinY() {
    return this.MinY;
  }

  public RandomTeleport setMinY(int value) {
    this.MinY = value;
    return this;
  }

  public boolean isIgnorePowderedSnow() {
    return this.ignorePowderSnow;
  }

  public RandomTeleport setIgnorePowderSnow(boolean flag) {
    this.ignorePowderSnow = flag;
    return this;
  }

  public boolean isSurfaceOnly() {
    return this.surfaceOnly;
  }

  public RandomTeleport setSurfaceOnly(boolean flag) {
    this.surfaceOnly = flag;
    return this;
  }

  public CMIVectorInt3D getRandomLocation() {
    int value = this.getMinDistance();
    int count = this.getMaxDistance();
    if (count < 1) {
      count = 1;
    }

    if (count <= value) {
      count = value + 1;
    }

    double number;
    double amount;
    if (this.isCircle()) {
      double amountSecond = Math.random() * 2.0 * Math.PI;
      double amountThird = (count - value) * Math.sqrt(Math.random()) + value;
      number = amountThird * Math.cos(amountSecond);
      amount = amountThird * Math.sin(amountSecond);
      number = (int) number + this.getCenter().getX();
      amount = (int) amount + this.getCenter().getZ();
    } else {
      int countSecond = (count - value) * 2;
      number = random.nextInt(countSecond);
      number = number > countSecond / 2.0 ? number - countSecond / 2.0 : -number;
      number = number > 0.0 ? number + value : number - value;
      number += this.getCenter().getBlockX();
      amount = random.nextInt(countSecond);
      amount = amount > countSecond / 2.0 ? amount - countSecond / 2.0 : -amount;
      amount = amount > 0.0 ? amount + value : amount - value;
      amount += this.getCenter().getBlockZ();
    }

    return new CMIVectorInt3D((int) number, (int) this.getCenter().getY(), (int) amount);
  }

  public boolean isIgnoreLeaves() {
    return this.ignoreLeaves;
  }

  public RandomTeleport setIgnoreLeaves(boolean flag) {
    this.ignoreLeaves = flag;
    return this;
  }

  public boolean isIgnoreMaterials() {
    return !this.ignoredMaterials.isEmpty();
  }

  public boolean isIgnoredMaterial(CMIMaterial cmiMaterial) {
    return this.ignoredMaterials.contains(cmiMaterial);
  }

  public RandomTeleport setIgnoredMaterials(Set<CMIMaterial> set) {
    this.ignoredMaterials = set;
    return this;
  }

  public boolean isAllowFromAnotherWorld() {
    return this.fromAnotherWorld;
  }

  public RandomTeleport setFromAnotherWorld(boolean flag) {
    this.fromAnotherWorld = flag;
    return this;
  }

  public boolean isRequireWorldPermission() {
    return this.requireWorldPermission;
  }

  public RandomTeleport setRequireWorldPermission(boolean flag) {
    this.requireWorldPermission = flag;
    return this;
  }

  public Set<String> getIgnoredBiomes() {
    return this.ignoredBiomes;
  }

  public RandomTeleport setIgnoredBiomes(Set<String> set) {
    this.ignoredBiomes = set;
    return this;
  }

  public boolean isIgnoredBiome(String string) {
    string = string.toLowerCase().replace("_", "").replace(" ", "");
    return this.ignoredBiomes.contains(string);
  }

  public Set<String> getPreferredBiomes() {
    return this.preferredBiomes;
  }

  public RandomTeleport setPreferredBiomes(Set<String> set) {
    this.preferredBiomes = set;
    return this;
  }

  public boolean isPreferredBiome(String string) {
    string = string.toLowerCase().replace("_", "").replace(" ", "");
    return this.preferredBiomes.contains(string);
  }

  public int getMaxTries() {
    return this.maxTries;
  }

  public RandomTeleport setMaxTries(int value) {
    this.maxTries = value;
    return this;
  }

  public int getCooldownWithDefault() {
    return this.cooldown == 0
        ? CMI.getInstance().getRandomTeleportationManager().getRandomTeleportCooldown()
        : this.cooldown;
  }

  public int getCooldown() {
    return this.cooldown;
  }

  public RandomTeleport setCooldown(int value) {
    this.cooldown = value;
    return this;
  }

  public boolean isIgnoreCrawl() {
    return this.IgnoreCrawl;
  }

  public RandomTeleport setIgnoreCrawl(boolean flag) {
    this.IgnoreCrawl = flag;
    return this;
  }
}
