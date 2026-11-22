package com.Zrips.CMI.Modules.SavedInv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class CMIInventory {
  private int id = 0;
  private String owner;
  private long time = 0L;
  private String killer;
  private String DeathReason;
  private double Exp;
  private double Health;
  private double MaxHealth;
  private float Saturation;
  private int Hunger;
  private GameMode GameMode;
  private boolean Fly;
  private Collection<PotionEffect> potions = new ArrayList<>();
  private HashMap<Integer, ItemStack> items = new HashMap<>();
  private Location loc;
  private boolean editable = false;
  private UUID uuid;

  public CMIInventory() {}

  public CMIInventory(String string, UUID newUuid) {
    this.owner = string;
    this.uuid = newUuid;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int value) {
    this.id = value;
  }

  public void afterSave() {}

  public Long getTime() {
    return this.time;
  }

  public void setTime(Long newTime) {
    this.time = newTime;
  }

  public String getKiller() {
    return this.killer;
  }

  public void setKiller(String string) {
    this.killer = string;
  }

  public String getDeathReason() {
    return this.DeathReason;
  }

  public void setDeathReason(String string) {
    this.DeathReason = string;
  }

  public double getExp() {
    return this.Exp;
  }

  public void setExp(double number) {
    this.Exp = number;
  }

  public double getHealth() {
    return this.Health;
  }

  public void setHealth(double number) {
    this.Health = number;
  }

  public double getMaxHealth() {
    return this.MaxHealth;
  }

  public void setMaxHealth(double number) {
    this.MaxHealth = number;
  }

  public float getSaturation() {
    return this.Saturation;
  }

  public void setSaturation(float floatValue) {
    this.Saturation = floatValue;
  }

  public int getHunger() {
    return this.Hunger;
  }

  public void setHunger(int value) {
    this.Hunger = value;
  }

  public GameMode getGameMode() {
    return this.GameMode;
  }

  public void setGameMode(GameMode gameMode) {
    this.GameMode = gameMode;
  }

  public boolean isFly() {
    return this.Fly;
  }

  public void setFly(boolean flag) {
    this.Fly = flag;
  }

  public Location getLocation() {
    return this.loc;
  }

  public void setLoc(Location location) {
    this.loc = location;
  }

  public Collection<PotionEffect> getPotions() {
    return this.potions;
  }

  public void setPotions(Collection<PotionEffect> collection) {
    this.potions = collection;
  }

  public HashMap<Integer, ItemStack> getItems() {
    return this.items;
  }

  public ItemStack getBoots() {
    return this.items.get(36);
  }

  public ItemStack getPants() {
    return this.items.get(37);
  }

  public ItemStack getChest() {
    return this.items.get(38);
  }

  public ItemStack getHelmet() {
    return this.items.get(39);
  }

  public ItemStack getOffHand() {
    return this.items.get(40);
  }

  public void setItems(HashMap<Integer, ItemStack> hashMap) {
    this.items = hashMap;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setOwner(String string) {
    this.owner = string;
  }

  public boolean isEditable() {
    return this.editable;
  }

  public void setEditable(boolean flag) {
    this.editable = flag;
  }

  @Deprecated
  public UUID getUuid() {
    return this.getUniqueId();
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public void setUuid(UUID newUuid) {
    this.uuid = newUuid;
  }
}
