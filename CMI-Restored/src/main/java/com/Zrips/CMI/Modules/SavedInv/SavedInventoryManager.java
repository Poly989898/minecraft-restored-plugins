package com.Zrips.CMI.Modules.SavedInv;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPaperItemSerializator;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.commands.list.invcheck;
import com.Zrips.CMI.utils.DateFormat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIAttribute;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIPlayer;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.CmiInventoryType;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIFieldType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.GUI.GUIManager.InvType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class SavedInventoryManager {
  private ConcurrentHashMap<UUID, SavedInventories> map = new ConcurrentHashMap<>();
  private CMI plugin;
  private boolean restoreHP = true;
  private boolean restoreXP = true;
  private boolean restoreFood = true;
  private boolean restoreSaturation = true;
  private boolean restorePotions = true;
  private boolean restoreItems = true;
  private boolean SaveOnDeath = false;
  private boolean ignoreEmpty = false;
  private boolean SaveOnDeathRequiresPermission = false;
  private int SavedInventorys = 10;
  List<Material> blackListedMaterials = new ArrayList<>();

  public SavedInventoryManager(CMI cmi) {
    this.plugin = cmi;
  }

  private SavedInventories getInventories(UUID uuid) {
    synchronized (this.map) {
      return this.map.get(uuid);
    }
  }

  public SavedInventories removeCacheInventory(UUID uuid) {
    synchronized (this.map) {
      return this.map.remove(uuid);
    }
  }

  public void addInventory(CMIUser cmiUser, CMIInventory cmiInventory) {
    this.addInventory(cmiUser, cmiInventory, null, true);
  }

  public void addInventory(CMIUser cmiUser, CMIInventory cmiInventory, Integer integer) {
    this.addInventory(cmiUser, cmiInventory, integer, true);
  }

  public void addInventory(CMIUser cmiUser, CMIInventory cmiInventory, boolean flag) {
    this.addInventory(cmiUser, cmiInventory, null, flag);
  }

  public void addInventory(
      CMIUser cmiUser, CMIInventory cmiInventory, Integer integer, boolean flag) {
    SavedInventories savedInventories = this.getInventories(cmiUser.getUniqueId());
    if (savedInventories == null) {
      savedInventories = new SavedInventories(cmiUser);
    }

    cmiInventory.setId(
        integer == null
            ? (cmiInventory.getId() == 0 ? savedInventories.getNextId() : cmiInventory.getId())
            : integer);
    savedInventories.addInventory(cmiInventory);
    if (flag) {
      this.checkInvLimit(savedInventories);
    }

    this.map.put(cmiUser.getUniqueId(), savedInventories);
  }

  private void checkInvLimit(SavedInventories savedInventories) {
    int value = 0;

    while (savedInventories.getSavedInventoryCount() > this.SavedInventorys && value < 2) {
      CMIInventory cmiInventory = this.getFirstInv(savedInventories);
      value++;
      if (cmiInventory == null) {
        break;
      }

      CMIInventory removed = savedInventories.removeInventory(cmiInventory);
      if (removed == null) {
        break;
      }
    }
  }

  public CompletableFuture<CMIInventory> getInventoryById(CMIUser cmiUser, int value) {
    return cmiUser == null
        ? CompletableFuture.completedFuture(null)
        : this.getUserInventories(cmiUser)
            .thenApply(
                savedInventories -> {
                  if (savedInventories == null) {
                    return null;
                  } else {
                    return value == -1
                        ? this.getLastInv(cmiUser)
                        : savedInventories.getInventory(value);
                  }
                });
  }

  @Deprecated
  public CMIInventory getInvById(CMIUser cmiUser, int value) {
    SavedInventories savedInventories = this.getInventories(cmiUser);
    if (savedInventories == null) {
      return null;
    } else {
      return value == -1 ? this.getLastInv(cmiUser) : savedInventories.getInventory(value);
    }
  }

  @Deprecated
  public CMIInventory getFirstInv(CMIUser cmiUser) {
    return this.getFirstInv(this.getInventories(cmiUser));
  }

  @Deprecated
  public CMIInventory getFirstInv(SavedInventories savedInventories) {
    return savedInventories == null ? null : savedInventories.getFirstInv();
  }

  @Deprecated
  public CMIInventory getLastInv(CMIUser cmiUser) {
    SavedInventories savedInventories = this.getInventories(cmiUser);
    return savedInventories == null ? null : savedInventories.getLastInv();
  }

  public CompletableFuture<CMIInventory> getLastInventory(CMIUser cmiUser) {
    var completableFuture = this.getUserInventories(cmiUser);
    return completableFuture == null
        ? CompletableFuture.completedFuture(null)
        : completableFuture.thenApply(object -> object.getLastInv());
  }

  @Deprecated
  public CMIInventory getNextInv(CMIUser cmiUser, int value) {
    SavedInventories savedInventories = this.getInventories(cmiUser);
    return savedInventories == null ? null : savedInventories.getNextInv(value);
  }

  public CompletableFuture<CMIInventory> getNextInventory(CMIUser cmiUser, int value) {
    var completableFuture = this.getUserInventories(cmiUser);
    return completableFuture == null
        ? null
        : completableFuture.thenApply(object -> object.getNextInv(value));
  }

  @Deprecated
  public CMIInventory getPrevInv(CMIUser cmiUser, int value) {
    SavedInventories savedInventories = this.getInventories(cmiUser);
    return savedInventories == null ? null : savedInventories.getPrevInv(value);
  }

  public CompletableFuture<CMIInventory> getPrevInventory(CMIUser cmiUser, int value) {
    var completableFuture = this.getUserInventories(cmiUser);
    return completableFuture == null
        ? null
        : completableFuture.thenApply(object -> object.getPrevInv(value));
  }

  @Deprecated
  public SavedInventories getInventories(CMIUser cmiUser) {
    SavedInventories savedInventories = this.getInventories(cmiUser.getUniqueId());
    if (savedInventories == null) {
      this.loadInventories(cmiUser);
    }

    return this.getInventories(cmiUser.getUniqueId());
  }

  @Deprecated
  public void loadInventories(CMIUser cmiUser) {
    if (cmiUser != null) {
      String string = this.plugin.getDbManager().getInv(cmiUser);
      if (string != null) {
        this.loadInventories(cmiUser, string);
      }
    }
  }

  @Deprecated
  public void loadInventories(CMIUser cmiUser, String string) {
    if (cmiUser != null) {
      if (string != null) {
        ArrayList<String> arrayList = new ArrayList();
        if (string.contains(";")) {
          arrayList.addAll(Arrays.asList(string.split(";")));
        } else {
          arrayList.add(string);
        }

        for (String invName : arrayList) {
          if (invName != null) {
            try {
              var hashMap = deserialize(invName);
              if (hashMap.isEmpty()) {
                return;
              }

              CMIInventory cmiInventory = this.generateInvFromMap(hashMap);
              cmiInventory.setOwner(cmiUser.getName());
              cmiInventory.setUuid(cmiUser.getUniqueId());
              this.addInventory(cmiUser, cmiInventory, false);
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          }
        }
      }
    }
  }

  public CompletableFuture<SavedInventories> getUserInventories(CMIUser cmiUser) {
    SavedInventories savedInventories = this.getInventories(cmiUser.getUniqueId());
    if (savedInventories != null && !Version.isTestServer()) {
      return CompletableFuture.completedFuture(savedInventories);
    }

    var completableFuture = this.loadUserInventories(cmiUser);
    return completableFuture == null
        ? CompletableFuture.completedFuture(null)
        : completableFuture.thenApply(object -> this.getInventories(cmiUser.getUniqueId()));
  }

  public CompletableFuture<CMIInventory> loadUserInventories(CMIUser cmiUser) {
    if (cmiUser == null) {
      return CompletableFuture.completedFuture(null);
    }

    var completableFuture = this.plugin.getDbManager().getInventoriesString(cmiUser);
    return completableFuture == null
        ? CompletableFuture.completedFuture(null)
        : completableFuture.thenCompose(object -> this.loadUserInventories(cmiUser, object));
  }

  public CompletableFuture<CMIInventory> loadUserInventories(CMIUser cmiUser, String string) {
    return cmiUser != null && string != null && !string.isEmpty()
        ? CompletableFuture.supplyAsync(
            () -> {
              CMIInventory cmiInventory = null;
              String[] strings = string.split(";");
              String[] args = strings;
              int value = strings.length;

              for (int argIndex = 0; argIndex < value; argIndex++) {
                String arg = args[argIndex];
                if (arg != null && !arg.isEmpty()) {
                  try {
                    var hashMap = deserialize(arg);
                    if (!hashMap.isEmpty()) {
                      CMIInventory generated = this.generateInvFromMap(hashMap);
                      generated.setOwner(cmiUser.getName());
                      generated.setUuid(cmiUser.getUniqueId());
                      this.addInventory(cmiUser, generated, false);
                      cmiInventory = generated;
                    }
                  } catch (Throwable throwable) {
                    throwable.printStackTrace();
                  }
                }
              }

              return cmiInventory;
            })
        : CompletableFuture.completedFuture(null);
  }

  @Deprecated
  public boolean saveAllInventories(UUID uuid) {
    StringBuilder stringBuilder = new StringBuilder();
    SavedInventories savedInventories = this.getInventories(uuid);
    if (savedInventories == null) {
      return false;
    }

    for (var entry : savedInventories.getInventories().entrySet()) {
      if (!stringBuilder.toString().isEmpty()) {
        stringBuilder.append(";");
      }

      var hashMap = this.generateMapFromPlayerInv((CMIInventory) entry.getValue());
      stringBuilder.append(serialize(hashMap));
    }

    this.plugin.getDbManager().addForSave(CMIUser.getUser(uuid), stringBuilder.toString());
    return true;
  }

  public CompletableFuture<Boolean> saveUserInventories(UUID uuid) {
    return uuid == null
        ? CompletableFuture.completedFuture(false)
        : CompletableFuture.supplyAsync(
            () -> {
              SavedInventories savedInventories = this.getInventories(uuid);
              if (savedInventories == null) {
                return false;
              }

              StringBuilder stringBuilder = new StringBuilder();

              for (var entry : savedInventories.getInventories().entrySet()) {
                if (stringBuilder.length() > 0) {
                  stringBuilder.append(";");
                }

                var hashMap = this.generateMapFromPlayerInv((CMIInventory) entry.getValue());
                stringBuilder.append(serialize(hashMap));
              }

              this.plugin
                  .getDbManager()
                  .addForSave(CMIUser.getUser(uuid), stringBuilder.toString());
              return true;
            });
  }

  @Deprecated
  public CMIInventory saveInv(Player player, Entity entity, DamageCause damageCause) {
    return this.saveInv(player, entity, damageCause, null);
  }

  @Deprecated
  public CMIInventory saveInv(
      Player player, Entity entity, DamageCause damageCause, Integer integer) {
    CMIUser cmiUser = CMIUser.getUser(player);
    return cmiUser == null
        ? null
        : this.saveInv(this.generateCMIInventory(player, entity, damageCause), integer);
  }

  @Deprecated
  public CMIInventory saveInv(CMIInventory cmiInventory, Integer integer) {
    if (cmiInventory == null) {
      return null;
    }

    if (this.ignoreEmpty
        && (cmiInventory.getDeathReason() != null || cmiInventory.getKiller() != null)
        && cmiInventory.getItems().isEmpty()) {
      return null;
    }

    CMIUser cmiUser = CMIUser.getUser(cmiInventory.getUniqueId());
    if (cmiUser == null) {
      return null;
    }

    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          try {
            if (integer != null) {
              this.removeInventory(cmiUser, integer);
            }

            this.getInventories(cmiUser);
            this.addInventory(cmiUser, cmiInventory, integer);
            this.saveAllInventories(cmiInventory.getUniqueId());
          } catch (Throwable throwable) {
            throwable.printStackTrace();
            return;
          }

          CMIScheduler.runTask(this.plugin, cmiInventory::afterSave);
        });
    return cmiInventory;
  }

  public CompletableFuture<CMIInventory> saveInventory(CMIInventory cmiInventory, Integer integer) {
    if (cmiInventory == null) {
      return CompletableFuture.completedFuture(null);
    }

    if (this.ignoreEmpty
        && (cmiInventory.getDeathReason() != null || cmiInventory.getKiller() != null)
        && cmiInventory.getItems().isEmpty()) {
      return CompletableFuture.completedFuture(null);
    }

    CMIUser cmiUser = CMIUser.getUser(cmiInventory.getUniqueId());
    if (cmiUser == null) {
      return CompletableFuture.completedFuture(null);
    }

    int value = integer != null ? integer : -3;
    return this.removeUserInventory(cmiUser, value)
        .thenCompose(
            ignored ->
                !ignored
                    ? CompletableFuture.completedFuture(null)
                    : this.getUserInventories(cmiUser))
        .thenCompose(
            savedInventories -> {
              if (savedInventories == null) {
                return CompletableFuture.completedFuture(null);
              }

              this.addInventory(cmiUser, cmiInventory, integer);
              return this.saveUserInventories(cmiInventory.getUniqueId());
            })
        .thenApply(ignored -> cmiInventory)
        .exceptionally(
            throwable -> {
              throwable.printStackTrace();
              return null;
            });
  }

  public CMIInventory generateCMIInventory(Player player, Entity entity, DamageCause damageCause) {
    return this.generateCMIInventory(
        new CMIInventory(player.getName(), player.getUniqueId()), entity, damageCause);
  }

  public CMIInventory generateCMIInventory(
      CMIInventory cmiInventory, Entity entity, DamageCause damageCause) {
    CMIUser cmiUser = CMIUser.getUser(cmiInventory.getUniqueId());
    if (cmiUser == null) {
      return null;
    }

    Player player = cmiUser.getPlayer();
    if (player == null) {
      return null;
    }

    long longValue = System.currentTimeMillis();
    cmiInventory.setTime(longValue);
    if (entity != null) {
      cmiInventory.setKiller(entity.getName());
    }

    if (damageCause != null) {
      cmiInventory.setDeathReason(
          this.plugin.getUtilManager().translateDamageCause(damageCause.name()));
    }

    cmiInventory.setExp(cmiUser.getTotalExp());
    cmiInventory.setHealth(player.getHealth());
    cmiInventory.setMaxHealth(CMIPlayer.getMaxHealth(player));
    cmiInventory.setSaturation(player.getSaturation());
    cmiInventory.setHunger(player.getFoodLevel());
    cmiInventory.setGameMode(player.getGameMode());
    cmiInventory.setFly(player.getAllowFlight());
    cmiInventory.setPotions(player.getActivePotionEffects());
    cmiInventory.setLoc(player.getLocation());
    ItemStack[] itemStacks = player.getInventory().getContents();
    HashMap<Integer, ItemStack> hashMap = new HashMap();

    for (int value = 0; value < itemStacks.length; value++) {
      if (itemStacks[value] != null
          && !this.blackListedMaterials.contains(itemStacks[value].getType())) {
        hashMap.put(value, itemStacks[value].clone());
      }
    }

    if (Version.isCurrentEqualOrLower(Version.v1_8_R3)) {
      itemStacks = player.getInventory().getArmorContents();

      for (int slot = 0; slot < itemStacks.length; slot++) {
        if (itemStacks[slot] != null
            && itemStacks[slot].clone().getType() != Material.AIR
            && !this.blackListedMaterials.contains(itemStacks[slot].getType())) {
          hashMap.put(slot + 36, itemStacks[slot].clone());
        }
      }
    }

    cmiInventory.setItems(hashMap);
    return cmiInventory;
  }

  public CMIInventory generateInvFromMap(HashMap<String, Object> hashMap) {
    CMIInventory cmiInventory = new CMIInventory();
    cmiInventory.setId((Integer) hashMap.get("Id"));
    cmiInventory.setTime((Long) hashMap.get("Time"));
    if (hashMap.containsKey("Killer")) {
      cmiInventory.setKiller((String) hashMap.get("Killer"));
    }

    if (hashMap.containsKey("DeathReason")) {
      cmiInventory.setDeathReason((String) hashMap.get("DeathReason"));
    }

    cmiInventory.setExp((Double) hashMap.get("Exp"));
    cmiInventory.setHealth((Double) hashMap.get("Health"));
    cmiInventory.setMaxHealth((Double) hashMap.get("MaxHealth"));
    cmiInventory.setSaturation((Float) hashMap.get("Saturation"));
    cmiInventory.setHunger((Integer) hashMap.get("Hunger"));
    cmiInventory.setGameMode((GameMode) hashMap.get("GameMode"));
    cmiInventory.setFly((Boolean) hashMap.get("FlyModeOn"));
    ArrayList arrayList = (ArrayList) hashMap.get("Potions");
    ArrayList<PotionEffect> effects = new ArrayList();

    for (Object object : arrayList) {
      if (object instanceof Map) {
        effects.add(new PotionEffect((Map) object));
      } else if (object instanceof PotionEffect) {
        effects.add((PotionEffect) object);
      }
    }

    cmiInventory.setPotions(effects);

    try {
      if (hashMap.containsKey("Location")) {
        Object locationData = hashMap.get("Location");
        Location locationRaw = null;
        if (locationData instanceof Location) {
          locationRaw = (Location) hashMap.get("Location");
        } else if (locationData instanceof String) {
          CMILocation cmiLocation =
              this.plugin.getPlayerManager().convertStringToLocation((String) locationData);
          if (cmiLocation.getWorld() == null) {
            cmiLocation.setWorld((World) Bukkit.getWorlds().get(0));
          }

          locationRaw = cmiLocation.clone();
        }

        if (locationRaw == null || locationRaw.getWorld() == null) {
          locationRaw = new Location((World) Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0, 0.0F, 0.0F);
        }

        cmiInventory.setLoc((Location) locationRaw);
      }
    } catch (Exception exception) {
    }

    HashMap<Integer, Object> itemsMap = (HashMap<Integer, Object>) hashMap.get("Items");
    HashMap<Integer, ItemStack> itemsByIndex = new HashMap<>();

    for (int value : itemsMap.keySet()) {
      Object itemData = itemsMap.get(value);
      if (itemData instanceof Map) {
        ItemStack itemStack = ItemStack.deserialize((Map) itemData);
        if (itemStack != null) {
          itemsByIndex.put(value, itemStack);
        }
      } else {
        ItemStack itemFromData = CMIPaperItemSerializator.getItemFromObject(itemsMap.get(value));
        if (itemFromData != null) {
          itemsByIndex.put(value, itemFromData);
        }
      }
    }

    cmiInventory.setItems(itemsByIndex);
    return cmiInventory;
  }

  public HashMap<String, Object> generateMapFromPlayerInv(CMIInventory cmiInventory) {
    HashMap hashMap = new HashMap();
    hashMap.put("Id", cmiInventory.getId());
    hashMap.put("Time", cmiInventory.getTime());
    if (cmiInventory.getKiller() != null) {
      hashMap.put("Killer", cmiInventory.getKiller());
    }

    if (cmiInventory.getDeathReason() != null) {
      hashMap.put("DeathReason", cmiInventory.getDeathReason());
    }

    hashMap.put("Exp", cmiInventory.getExp());
    hashMap.put("Health", cmiInventory.getHealth());
    hashMap.put("MaxHealth", cmiInventory.getMaxHealth());
    hashMap.put("Saturation", cmiInventory.getSaturation());
    hashMap.put("Hunger", cmiInventory.getHunger());
    hashMap.put("GameMode", cmiInventory.getGameMode());
    hashMap.put("FlyModeOn", cmiInventory.isFly());
    ArrayList<Map<String, Object>> arrayList = new ArrayList();

    for (PotionEffect potionEffect : cmiInventory.getPotions()) {
      arrayList.add(potionEffect.serialize());
    }

    hashMap.put("Potions", arrayList);
    hashMap.put(
        "Location", this.plugin.getPlayerManager().convertLocToString(cmiInventory.getLocation()));
    hashMap.put("Items", cmiInventory.getItems());
    return hashMap;
  }

  private static String serialize(HashMap<String, Object> hashMap) {
    if (Version.isCurrentEqualOrHigher(Version.v1_21_R1)) {
      Object object = hashMap.get("Items");
      if (object instanceof HashMap) {
        HashMap<?, ?> serialized = (HashMap<?, ?>) object;
        HashMap<Integer, Object> serializedItems = new HashMap<>();

        for (Entry<?, ?> entry : serialized.entrySet()) {
          serializedItems.put(
              (Integer) entry.getKey(),
              CMIPaperItemSerializator.serializeItemStack((ItemStack) entry.getValue()));
        }

        hashMap.put("Items", serializedItems);
      }
    }

    ByteArrayOutputStream byteArrayOutputStream = null;
    ObjectOutputStream objectOutputStream = null;

    try {
      byteArrayOutputStream = new ByteArrayOutputStream();
      objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      byteArrayOutputStream.close();
      objectOutputStream.writeObject(hashMap);
      objectOutputStream.close();
      return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    } catch (Exception exception) {
      if (Version.isSpigot()) {
        try {
          ByteArrayOutputStream output = new ByteArrayOutputStream();
          BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(output);
          bukkitObjectOutputStream.writeObject(hashMap);
          bukkitObjectOutputStream.close();
          return Base64Coder.encodeLines(output.toByteArray());
        } catch (Exception exception2) {
          exception2.printStackTrace();
        }
      }

      exception.printStackTrace();
    } finally {
      try {
        if (objectOutputStream != null) {
          objectOutputStream.close();
        }

        if (byteArrayOutputStream != null) {
          byteArrayOutputStream.close();
        }
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    return null;
  }

  private static HashMap<String, Object> deserialize(String string) {
    if (string != null && !string.isEmpty()) {
      ByteArrayInputStream byteArrayInputStream = null;
      ObjectInputStream objectInputStream = null;

      try {
        byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(string));
        objectInputStream = new ObjectInputStream(byteArrayInputStream);
        byteArrayInputStream.close();
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        if (object instanceof Map) {
          return (HashMap<String, Object>) object;
        }
      } catch (Exception exception) {
      } finally {
        try {
          if (byteArrayInputStream != null) {
            byteArrayInputStream.close();
          }

          if (objectInputStream != null) {
            objectInputStream.close();
          }
        } catch (IOException ioException) {
          ioException.printStackTrace();
        }
      }

      ByteArrayInputStream input = null;
      BukkitObjectInputStream bukkitObjectInputStream = null;

      try {
        input = new ByteArrayInputStream(Base64Coder.decodeLines(string));
        bukkitObjectInputStream = new BukkitObjectInputStream(input);
        Object deserialized = bukkitObjectInputStream.readObject();
        bukkitObjectInputStream.close();
        input.close();
        if (deserialized instanceof Map) {
          return (HashMap<String, Object>) deserialized;
        }
      } catch (Exception exception2) {
      } finally {
        try {
          if (bukkitObjectInputStream != null) {
            bukkitObjectInputStream.close();
          }

          if (input != null) {
            input.close();
          }
        } catch (IOException ioException2) {
          ioException2.printStackTrace();
        }
      }

      return new HashMap<>();
    } else {
      return new HashMap<>();
    }
  }

  private void fillFields(
      final CMIGui cmiGui, final CMIInventory cmiInventory, final boolean flag) {
    long longValue = cmiInventory.getTime();
    cmiGui.setTitle(
        CMIChatColor.DARK_GREEN + DateFormat.MiliToDate(longValue) + " #" + cmiInventory.getId());
    ArrayList<String> arrayList = new ArrayList();
    if (!cmiInventory.getPotions().isEmpty()) {
      for (PotionEffect potionEffect : cmiInventory.getPotions()) {
        String object = CMIText.firstToUpperCase(potionEffect.getType().getName());
        int value = potionEffect.getAmplifier();
        String candidate = null;
        switch (potionEffect.getAmplifier()) {
          case 0:
            candidate = "";
            break;
          case 1:
            candidate = " II";
            break;
          default:
            candidate = " " + String.valueOf(value);
        }

        Double seconds = potionEffect.getDuration() / 20.0;
        String string =
            CMIChatColor.DARK_GREEN
                + object
                + candidate
                + CMIChatColor.DARK_AQUA
                + " Duration: "
                + seconds
                + " sec";
        arrayList.add(string);
      }
    } else {
      arrayList.add(CMILC.Potion_NoPotions.getLocale());
    }

    ItemStack itemStack = CMIMaterial.BLACK_STAINED_GLASS_PANE.newItemStack();
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.setDisplayName(" ");
    itemStack.setItemMeta(itemMeta);
    final CMIUser cmiUser = this.plugin.getPlayerManager().getUser(cmiInventory.getOwner());
    cmiGui.addEmptyButton(6);
    cmiGui.addButton(
        new CMIGuiButton(7, Material.HAY_BLOCK, CMILC.info_InventorySave_LoadForSelf.getLocale())
            .addCommand("cmi invload " + cmiInventory.getOwner() + " " + cmiInventory.getId()));
    cmiGui.addButton(
        new CMIGuiButton(
                8, CMIMaterial.BLUE_WOOL, CMILC.info_InventorySave_LoadForOwner.getLocale())
            .addCommand(
                "cmi invload "
                    + cmiInventory.getOwner()
                    + " "
                    + cmiInventory.getOwner()
                    + " "
                    + cmiInventory.getId()));
    cmiGui.addEmptyButton(11);
    cmiGui.addEmptyButton(12);
    cmiGui.addEmptyButton(13);
    if (PermissionsManager.CMIPerm.command_invcheck_edit.hasPermission(cmiGui.getPlayer())) {
      if (!cmiInventory.isEditable()) {
        cmiGui.addButton(
            new CMIGuiButton(
                    14, CMIMaterial.RED_WOOL, CMILC.info_InventorySave_NonEditable.getLocale())
                .addCommand(
                    "cmi invcheck "
                        + cmiInventory.getOwner()
                        + " "
                        + cmiInventory.getId()
                        + " -e"));
      } else {
        cmiGui.addButton(
            new CMIGuiButton(
                    14, CMIMaterial.BLUE_WOOL, CMILC.info_InventorySave_Editable.getLocale())
                .addCommand(
                    "cmi invcheck " + cmiInventory.getOwner() + " " + cmiInventory.getId()));
      }
    } else {
      cmiGui.addEmptyButton(14);
    }

    cmiGui.addEmptyButton(15);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(16, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType guiClickType) {
            SavedInventoryManager.this
                .getPrevInventory(cmiUser, cmiInventory.getId())
                .thenAccept(
                    inventory ->
                        SavedInventoryManager.this.openSavedInv(
                            cmiGui.getPlayer(), inventory, flag));
          }
        };
    cmiGuiButton.setName(CMILC.info_InventorySave_PreviousInventory.getLocale());
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(17, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            SavedInventoryManager.this
                .getNextInventory(cmiUser, cmiInventory.getId())
                .thenAccept(
                    inventory ->
                        SavedInventoryManager.this.openSavedInv(
                            cmiGui.getPlayer(), inventory, flag));
          }
        };
    cmiGuiButton.setName(CMILC.info_InventorySave_NextInventory.getLocale());
    cmiGui.addButton(cmiGuiButton);
    cmiGui.addButton(new CMIGuiButton(10, GUIFieldType.Free, cmiInventory.getBoots()));
    cmiGui.addButton(new CMIGuiButton(9, GUIFieldType.Free, cmiInventory.getChest()));
    cmiGui.addButton(new CMIGuiButton(0, GUIFieldType.Free, cmiInventory.getHelmet()));
    cmiGui.addButton(new CMIGuiButton(1, GUIFieldType.Free, cmiInventory.getPants()));
    if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
      cmiGui.addButton(new CMIGuiButton(2, GUIFieldType.Free, cmiInventory.getOffHand()));
    } else {
      cmiGui.addEmptyButton(2);
    }

    int counter = 0;

    for (int bottomSlot = 45; bottomSlot <= 53; bottomSlot++) {
      cmiGui.addButton(
          new CMIGuiButton(bottomSlot, GUIFieldType.Free, cmiInventory.getItems().get(counter)));
      counter++;
    }

    for (int slotIndex = 18; slotIndex <= 44; slotIndex++) {
      cmiGui.addButton(
          new CMIGuiButton(slotIndex, GUIFieldType.Free, cmiInventory.getItems().get(counter)));
      counter++;
    }

    ItemStack glassBottle = CMIMaterial.GLASS_BOTTLE.newItemStack();
    ItemMeta glassMeta = glassBottle.getItemMeta();
    glassMeta.setLore(arrayList);
    glassMeta.setDisplayName(CMIChatColor.GRAY + "Potion effects");
    glassBottle.setItemMeta(glassMeta);
    cmiGui.addButton(new CMIGuiButton(3, glassBottle));
    ArrayList<String> mapLore = new ArrayList();
    ItemStack mapItem = CMIMaterial.MAP.newItemStack();
    ItemMeta mapMeta = mapItem.getItemMeta();
    if (cmiInventory.getKiller() != null) {
      mapLore.add(
          LC.Location_Killer.getLocale(new Object[] {"[killer]", cmiInventory.getKiller()}));
    }

    if (cmiInventory.getDeathReason() != null) {
      mapLore.add(
          LC.Location_DeathReason.getLocale(
              new Object[] {
                "[reason]",
                cmiInventory.getDeathReason(),
                "[reasson]",
                cmiInventory.getDeathReason()
              }));
    }

    if (cmiInventory.getLocation() != null && cmiInventory.getLocation().getWorld() != null) {
      mapLore.add(
          LC.Location_World.getLocale(
              new Object[] {"[world]", cmiInventory.getLocation().getWorld().getName()}));
      mapLore.add(
          LC.Location_X.getLocale(new Object[] {"[x]", cmiInventory.getLocation().getBlockX()}));
      mapLore.add(
          LC.Location_Y.getLocale(new Object[] {"[y]", cmiInventory.getLocation().getBlockY()}));
      mapLore.add(
          LC.Location_Z.getLocale(new Object[] {"[z]", cmiInventory.getLocation().getBlockZ()}));
      mapLore.add(
          LC.Location_Pitch.getLocale(
              new Object[] {"[pitch]", cmiInventory.getLocation().getPitch()}));
      mapLore.add(
          LC.Location_Yaw.getLocale(new Object[] {"[yaw]", cmiInventory.getLocation().getYaw()}));
    }

    mapMeta.setLore(mapLore);
    mapMeta.setDisplayName(LC.Location_Title.getLocale(new Object[0]));
    mapItem.setItemMeta(mapMeta);
    cmiGui.addButton(
        new CMIGuiButton(4, mapItem)
            .addCommand(cmiInventory.getLocation())
            .addPermission("cmi.buttonteleport"));
    ArrayList<String> bookLore = new ArrayList();
    ItemStack bookItem = new ItemStack(Material.BOOK);
    ItemMeta bookMeta = bookItem.getItemMeta();
    bookLore.add(
        CMILC.Information_Health.getLocale(
            "[Health]", cmiInventory.getHealth(), "[maxHealth]", cmiInventory.getMaxHealth()));
    bookLore.add(CMILC.Information_Hunger.getLocale("[Hunger]", cmiInventory.getHunger()));
    bookLore.add(
        CMILC.Information_Saturation.getLocale("[Saturation]", cmiInventory.getSaturation()));
    bookLore.add(CMILC.Information_Exp.getLocale("[Exp]", cmiInventory.getExp()));
    bookLore.add(CMILC.Information_GameMode.getLocale("[GameMode]", cmiInventory.getGameMode()));
    bookLore.add(CMILC.Information_Flying.getLocale("[Flying]", cmiInventory.isFly()));
    bookMeta.setLore(bookLore);
    bookMeta.setDisplayName(CMILC.Information_Title.getLocale());
    bookItem.setItemMeta(bookMeta);
    cmiGui.addButton(new CMIGuiButton(5, bookItem));
  }

  public void updateCMIInventoryItems(Inventory inventory, CMIInventory cmiInventory) {
    if (cmiInventory != null) {
      if (cmiInventory.isEditable()) {
        var hashMap = cmiInventory.getItems();
        if (inventory != null) {
          hashMap.put(36, inventory.getItem(10));
          hashMap.put(39, inventory.getItem(0));
          hashMap.put(38, inventory.getItem(9));
          hashMap.put(37, inventory.getItem(1));
          hashMap.put(40, inventory.getItem(2));
          int value = 0;

          for (int bottomSlot = 45; bottomSlot <= 53; bottomSlot++) {
            hashMap.put(value, inventory.getItem(bottomSlot));
            value++;
          }

          for (int slotIndex = 18; slotIndex <= 44; slotIndex++) {
            hashMap.put(value, inventory.getItem(slotIndex));
            value++;
          }

          cmiInventory.setItems(hashMap);
        }
      }
    }
  }

  public void openSavedInv(Player player, CMIInventory cmiInventory, boolean flag) {
    if (cmiInventory != null) {
      CMIGui cmiGui = new CMIGui(player);
      cmiGui.setInvSize(GUIRows.r6);
      cmiGui.setWhatShows(cmiInventory);
      if (flag) {
        cmiGui.addLock(InvType.Gui, GUIFieldType.Locked);
        cmiGui.setCmiInventoryType(CmiInventoryType.SavedInv);
        cmiInventory.setEditable(false);
      } else {
        cmiGui.addLock(InvType.Gui, GUIFieldType.Free);
        cmiGui.setCmiInventoryType(CmiInventoryType.EditableInv);
        cmiInventory.setEditable(true);
      }

      this.fillFields(cmiGui, cmiInventory, flag);
      CMILib.getInstance().getGUIManager().openGui(cmiGui);
    }
  }

  public void InvList(CommandSender commandSender, Player player) {
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    this.InvList(commandSender, cmiUser);
  }

  public void InvList(CommandSender commandSender, CMIUser cmiUser) {
    var completableFuture = this.getUserInventories(cmiUser);
    if (completableFuture != null) {
      completableFuture.whenComplete(
          (userInventories, error) -> {
            if (userInventories != null && !userInventories.getInventories().isEmpty()) {
              Snd snd = new Snd().setSender(commandSender).setTarget(cmiUser);
              CMILC.info_InventorySave_TopLine.sendMessage(
                  commandSender,
                  "[name]",
                  cmiUser.getName(),
                  "[playerDisplayName]",
                  cmiUser.getName(),
                  snd);

              for (Entry entry : userInventories.getInventories().entrySet()) {
                boolean flag =
                    ((CMIInventory) entry.getValue()).getKiller() != null
                        || ((CMIInventory) entry.getValue()).getDeathReason() != null;
                String string =
                    CMILC.info_InventorySave_List.getLocale(
                            "[id]",
                            entry.getKey(),
                            "[time]",
                            DateFormat.MiliToDate(((CMIInventory) entry.getValue()).getTime()))
                        + (flag ? CMILC.info_InventorySave_KillerSymbol.getLocale() : "");
                RawMessage rawMessage = new RawMessage();
                rawMessage
                    .addText(string)
                    .addHover(CMILC.info_InventorySave_Click.getLocale("[id]", entry.getKey()))
                    .addCommand(
                        "/"
                            + CommandsHandler.getLabel()
                            + " "
                            + invcheck.class.getSimpleName()
                            + " "
                            + cmiUser.getName()
                            + " "
                            + entry.getKey());
                rawMessage.show(commandSender);
              }

              LC.info_Spliter.sendMessage(commandSender, new Object[0]);
            } else {
              CMILC.info_InventorySave_NoSavedInv.sendMessage(commandSender);
            }
          });
    }
  }

  public boolean loadInv(CommandSender commandSender, CMIUser cmiUser, CMIInventory cmiInventory) {
    if (cmiInventory == null) {
      CMILC.info_InventorySave_CantFind.sendMessage(commandSender);
      return false;
    } else {
      Player player = cmiUser.getPlayer();
      CMIScheduler.runAtLocation(
          CMI.getInstance(),
          player.getLocation(),
          () -> {
            if (this.restorePotions && player.isOnline()) {
              for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                if (potionEffect != null && potionEffect.getType() != null) {
                  player.removePotionEffect(potionEffect.getType());
                }
              }

              for (PotionEffect effect : cmiInventory.getPotions()) {
                if (effect != null) {
                  player.addPotionEffect(effect);
                }
              }
            }

            if (this.restoreItems) {
              player.getInventory().clear();

              for (var entry : cmiInventory.getItems().entrySet()) {
                try {
                  player
                      .getInventory()
                      .setItem((Integer) entry.getKey(), (ItemStack) entry.getValue());
                } catch (Exception exception) {
                }
              }
            }

            if (this.restoreXP) {
              player.setTotalExperience(0);
              player.setLevel(0);
              player.setExp(0.0F);
              player.giveExp((int) cmiInventory.getExp());
            }

            if (this.restoreHP) {
              if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
                CMIAttribute.MAX_HEALTH.setBaseValue(player, cmiInventory.getMaxHealth());
              } else {
                player.setMaxHealth(cmiInventory.getMaxHealth());
              }

              if (cmiInventory.getHealth() > 0.0) {
                player.setHealth(Math.min(cmiInventory.getMaxHealth(), cmiInventory.getHealth()));
              } else {
                player.setHealth(1.0);
              }
            }

            if (this.restoreSaturation) {
              player.setSaturation(cmiInventory.getSaturation());
            }

            if (this.restoreFood) {
              player.setFoodLevel(cmiInventory.getHunger());
            }

            if (commandSender != player) {
              CMILC.info_InventorySave_Restored.sendMessage(
                  commandSender,
                  "[sourcename]",
                  cmiInventory.getOwner(),
                  "[targetname]",
                  player.getName());
            }

            this.plugin.save(player);
            if (player.isOnline()) {
              player.updateInventory();
              CMILC.info_InventorySave_GotRestored.sendMessage(
                  player,
                  "[sourcename]",
                  cmiInventory.getOwner(),
                  "[time]",
                  DateFormat.MiliToDate(cmiInventory.getTime()));
            }
          });
      return true;
    }
  }

  @Deprecated
  public boolean removeInventory(CMIUser cmiUser, int value) {
    if (cmiUser == null) {
      return false;
    }

    SavedInventories savedInventories = this.getInventories(cmiUser);
    if (savedInventories == null) {
      return false;
    }

    if (value == -2) {
      savedInventories.removeAllInventories();
      this.saveAllInventories(cmiUser.getUniqueId());
      return true;
    }

    if (value == -1) {
      CMIInventory cmiInventory = this.getLastInv(cmiUser);
      if (cmiInventory == null) {
        return false;
      }

      this.removeInventory(cmiUser, cmiInventory.getId());
      this.saveAllInventories(cmiUser.getUniqueId());
      return true;
    } else {
      CMIInventory removed = savedInventories.removeInventory(value);
      if (removed == null) {
        return false;
      }

      this.saveAllInventories(cmiUser.getUniqueId());
      return true;
    }
  }

  public CompletableFuture<Boolean> removeUserInventory(CMIUser cmiUser, int value) {
    if (value < -2) {
      return CompletableFuture.completedFuture(true);
    } else {
      return cmiUser == null
          ? CompletableFuture.completedFuture(false)
          : this.getUserInventories(cmiUser)
              .thenCompose(
                  savedInventories -> {
                    if (savedInventories == null) {
                      return CompletableFuture.completedFuture(false);
                    }

                    if (value == -2) {
                      savedInventories.removeAllInventories();
                      return this.saveUserInventories(cmiUser.getUniqueId());
                    }

                    if (value == -1) {
                      return this.getLastInventory(cmiUser)
                          .thenCompose(
                              cmiInventory -> {
                                if (cmiInventory == null) {
                                  return CompletableFuture.completedFuture(false);
                                }

                                savedInventories.removeInventory(cmiInventory.getId());
                                return this.saveUserInventories(cmiUser.getUniqueId());
                              });
                    }

                    CMIInventory removed = savedInventories.removeInventory(value);
                    return removed == null
                        ? CompletableFuture.completedFuture(false)
                        : this.saveUserInventories(cmiUser.getUniqueId());
                  });
    }
  }

  public int showTimer(
      Player player, int value, int startSeconds, int totalSeconds, int colorCode, int style) {
    String string = "";
    switch (value) {
      case 1:
        string = "&4-&2----";
        value = 2;
        break;
      case 2:
        string = "&2-&4-&2---";
        value = 3;
        break;
      case 3:
        string = "&2--&4-&2--";
        value = 4;
        break;
      case 4:
        string = "&2---&4-&2-";
        value = 5;
        break;
      case 5:
        string = "&2----&4-&2";
        value = 1;
    }

    if (player != null) {
      CMIActionBar.send(
          player,
          this.plugin
              .getLM()
              .getMessage(
                  "command.saveall.info.saving",
                  "[simbol]",
                  string,
                  "[symbol]",
                  string,
                  "[checked]",
                  style,
                  "[total]",
                  totalSeconds,
                  "[saved]",
                  startSeconds,
                  "[skipped]",
                  colorCode));
    }

    return value;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "inv.SaveOnDeath",
        new String[] {"Do you want to save the player's inventory on his death"});
    this.SaveOnDeath = configReader.get("inv.SaveOnDeath", false);
    configReader.addComment(
        "inv.IgnoreEmpty",
        new String[] {
          "When set to true, empty inventories (no items in inventory) will not be saved on players"
              + " death"
        });
    this.ignoreEmpty = configReader.get("inv.IgnoreEmpty", false);
    configReader.addComment(
        "inv.RequiresPermission",
        new String[] {
          "If set to true then player should have cmi.saveinv permission node for inventory to be"
              + " saved on death"
        });
    this.SaveOnDeathRequiresPermission = configReader.get("inv.RequiresPermission", false);
    configReader.addComment(
        "inv.SavedInventories",
        new String[] {"How many inventories, we will keep for each player"});
    this.SavedInventorys = CMINumber.clamp(configReader.get("inv.SavedInventories", 5), 0, 99);
    configReader.addComment(
        "inv.restore.HP",
        new String[] {
          "Set to false if you don't want to restore hp state on inventory load with /cmi invload"
              + " command"
        });
    this.restoreHP = configReader.get("inv.restore.HP", true);
    configReader.addComment(
        "inv.restore.XP",
        new String[] {"Set to false if you don't want to restore players experience points"});
    this.restoreXP = configReader.get("inv.restore.XP", true);
    configReader.addComment(
        "inv.restore.Food", new String[] {"Set to false if you don't want to restore food state"});
    this.restoreFood = configReader.get("inv.restore.Food", true);
    configReader.addComment(
        "inv.restore.Saturation",
        new String[] {"Set to false if you don't want to restore saturation level"});
    this.restoreSaturation = configReader.get("inv.restore.Saturation", true);
    configReader.addComment(
        "inv.restore.Potions",
        new String[] {"Set to false if you don't want to restore potion effects"});
    this.restorePotions = configReader.get("inv.restore.Potions", true);
    configReader.addComment(
        "inv.restore.Items", new String[] {"Set to false if you don't want to restore items"});
    this.restoreItems = configReader.get("inv.restore.Items", true);
    configReader.addComment(
        "inv.restore.BlackListedItems",
        new String[] {
          "List of materials we should not save",
          "Keep in mind that you might need to include all materials for colored items, like wool"
              + " or shulker boxes"
        });
    var list = configReader.get("inv.restore.BlackListedItems", Arrays.asList(""));
    this.blackListedMaterials.clear();

    for (String string : list) {
      CMIMaterial cmiMaterial = CMIMaterial.get(string);
      if (cmiMaterial.isValidItem()) {
        this.blackListedMaterials.add(cmiMaterial.getMaterial());
      }
    }
  }

  public Boolean isSaveOnDeath() {
    return this.SaveOnDeath;
  }

  public Boolean isSaveOnDeathRequiresPermission() {
    return this.SaveOnDeathRequiresPermission;
  }

  public int getMaxSavedInventorys() {
    return this.SavedInventorys;
  }

  public ConcurrentHashMap<UUID, SavedInventories> getMap() {
    return this.map;
  }

  public void clearAll() {
    this.map.clear();
  }
}
