package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class killall implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("infoList", "{gcs}[category]{gcp}: {gcs}[amount] &e([list]&e)");
    configReader.get("removedList", "{gcs}[type]{gcp}: {gcs}[amount]");
    configReader.get("total", "{gcp}Removed a total of: {gcs}[amount] {gcp}([list]{gcp})");
    configReader.get("noRemove", "{gcp}Nothing to remove");
    killall.removalCriteria[] removalCriterias;
    int value = (removalCriterias = killall.removalCriteria.values()).length;

    for (int index = 0; index < value; index++) {
      killall.removalCriteria criteria = removalCriterias[index];
      if (criteria.getCategory() != null) {
        configReader.get(criteria.name(), criteria.getCategory());
      }
    }
  }

  @CAnnotation(
      priority = 48,
      info = "Kill mobs around you",
      args =
          "(-monsters/-pets/-npc/-animals/-ambient/-named/-all/-force/-lightning/-list/-m:[mobType])"
              + " (-r:range) (-s) (-w:[worldName])",
      multiTab =
          "!-monsters,!-pets,!-npc,!-animals,!-ambient,!-named,!-all,!-withitems,!-force,!-lightning,!-list,[EntityType]"
              + " !-monsters,!-pets,!-npc,!-animals,!-ambient,!-named,!-all,!-withitems,!-force,!-lightning,!-list,[EntityType]",
      explanation = {},
      regVar = -666,
      consoleVar = -666,
      others = false)
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    killall.entityRemoval removal = new killall.entityRemoval(commandSender);
    if (commandSender instanceof Player) {
      removal.setCenter(((Player) commandSender).getLocation());
    } else if (commandSender instanceof BlockCommandSender) {
      removal.setCenter(((BlockCommandSender) commandSender).getBlock().getLocation());
    }

    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String string = argsCopy[argIndex];
      if (string.equalsIgnoreCase("-s")
          && PermissionsManager.CMIPerm.command_silent.hasPermission(commandSender)) {
        removal.setSilent(true);
      } else {
        killall.Action action = killall.Action.get(string);
        if (action != null) {
          if (action.equals(killall.Action.all)) {
            removal.addRemovalCriteria(killall.removalCriteria.monsters);
            removal.addRemovalCriteria(killall.removalCriteria.pets);
            removal.addRemovalCriteria(killall.removalCriteria.npc);
            removal.addRemovalCriteria(killall.removalCriteria.animals);
            removal.addRemovalCriteria(killall.removalCriteria.ambient);
            removal.addRemovalCriteria(killall.removalCriteria.vehicles);
          } else {
            removal.addAction(action);
          }
        } else {
          killall.removalCriteria criteria = killall.removalCriteria.get(string);
          if (criteria != null) {
            removal.addRemovalCriteria(criteria);
          } else if (string.startsWith("-r:")) {
            try {
              removal.setRange(Integer.parseInt(string.substring("-r:".length())));
            } catch (Exception exception) {
            }
          } else {
            try {
              removal.setRange(Integer.parseInt(string));
            } catch (Exception exception2) {
              if (string.startsWith("-w:")) {
                String worldName = string.substring("-w:".length());
                World world = cmi.getUtilManager().getWorld(worldName);
                if (world != null) {
                  removal.setWorld(world);
                }
              } else if (string.startsWith("-p:")) {
                String permissionName = string.substring("-p:".length());
                Player player = CMIUser.getOnlinePlayer(permissionName);
                if (player != null) {
                  removal.setCenter(player.getLocation());
                }
              } else if (string.startsWith("-m:")) {
                String materialName = string.substring("-m:".length());
                CMIEntityType cmiEntityType =
                    CMIEntityType.get(
                        materialName.startsWith("-") ? materialName.substring(1) : materialName);
                if (cmiEntityType == null) {
                  LC.info_cantDetermineMobType.sendMessage(
                      commandSender, new Object[] {"[type]", materialName});
                  return null;
                }

                removal.addType(cmiEntityType.getType());
                removal.addRemovalCriteria(killall.removalCriteria.custom);
              } else {
                CMIEntityType entityType =
                    CMIEntityType.get(string.startsWith("-") ? string.substring(1) : string);
                if (entityType == null) {
                  LC.info_cantDetermineMobType.sendMessage(
                      commandSender, new Object[] {"[type]", string});
                  return null;
                }

                removal.addType(entityType.getType());
                removal.addRemovalCriteria(killall.removalCriteria.custom);
              }
            }
          }
        }
      }
    }

    if (removal.getActions().isEmpty() && removal.getRemovalCriteria().isEmpty()) {
      removal.addRemovalCriteria(killall.removalCriteria.monsters);
      removal.addRemovalCriteria(killall.removalCriteria.withitems);
    }

    if (removal.hasAction(killall.Action.list) && removal.getRemovalCriteria().isEmpty()) {
      removal.addRemovalCriteria(killall.removalCriteria.monsters);
      removal.addRemovalCriteria(killall.removalCriteria.animals);
      removal.addRemovalCriteria(killall.removalCriteria.pets);
      removal.addRemovalCriteria(killall.removalCriteria.vehicles);
      removal.addRemovalCriteria(killall.removalCriteria.npc);
      removal.addRemovalCriteria(killall.removalCriteria.withitems);
    }

    removal.clean();
    return true;
  }

  private static boolean isEmptyArmorStand(Entity entity) {
    LivingEntity livingEntity = (LivingEntity) entity;
    EntityEquipment entityEquipment = livingEntity.getEquipment();
    if (entityEquipment == null) {
      return true;
    }

    ItemStack[] itemStacks;
    int value = (itemStacks = entityEquipment.getArmorContents()).length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = itemStacks[index];
      if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
        return false;
      }
    }

    return entityEquipment.getItemInHand() != null
            && !entityEquipment.getItemInHand().getType().equals(Material.AIR)
        ? false
        : !Version.isCurrentHigher(Version.v1_8_R3)
            || entityEquipment.getItemInOffHand() == null
            || entityEquipment.getItemInOffHand().getType().equals(Material.AIR);
  }

  private static boolean hasEquipment(Entity entity) {
    if (entity.getType().name().equalsIgnoreCase("ARMOR_STAND")) {
      return !isEmptyArmorStand(entity);
    }

    if (!(entity instanceof LivingEntity)) {
      return false;
    }

    LivingEntity livingEntity = (LivingEntity) entity;
    EntityEquipment entityEquipment = livingEntity.getEquipment();
    if (entityEquipment == null) {
      return false;
    }

    ItemStack[] itemStacks = entityEquipment.getArmorContents();
    ItemStack[] itemsCopy = itemStacks;
    int value = itemStacks.length;

    for (int index = 0; index < value; index++) {
      ItemStack itemStack = itemsCopy[index];
      if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
        return true;
      }
    }

    if (entityEquipment.getItemInHand() != null
        && !entityEquipment.getItemInHand().getType().equals(Material.AIR)) {
      return true;
    }

    if (Version.isCurrentHigher(Version.v1_8_R3)
        && entityEquipment.getItemInOffHand() != null
        && !entityEquipment.getItemInOffHand().getType().equals(Material.AIR)) {
      return true;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_21_R5)
        && entity.getType().equals(EntityType.HAPPY_GHAST)) {
      HappyGhast happyGhast = (HappyGhast) entity;
      ItemStack equippedItem = happyGhast.getEquipment().getItem(EquipmentSlot.BODY);
      if (equippedItem != null && equippedItem.getType() != Material.AIR) {
        return true;
      }
    }

    return false;
  }

  private static boolean isNamed(Entity entity) {
    return entity.getCustomName() != null
        && !entity.getCustomName().equalsIgnoreCase("CMIFakeSlime");
  }

  public static boolean isMonster(Entity entity) {
    return entity instanceof Monster
        || entity instanceof Slime
        || entity instanceof Ghast
        || entity.getType().toString().equalsIgnoreCase("Phantom");
  }

  private static boolean isTamed(Entity entity) {
    return entity instanceof Tameable ? ((Tameable) entity).isTamed() : false;
  }

  private enum Action {
    all,
    lightning,
    list;

    public static killall.Action get(String string) {
      if (string.startsWith("-")) {
        string = string.substring(1);
      }

      killall.Action[] actions;
      int value = (actions = values()).length;

      for (int index = 0; index < value; index++) {
        killall.Action action = actions[index];
        if (action.toString().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }

  private class entityRemoval {
    private CommandSender sender = null;
    private HashMap<killall.removalCriteria, Set<Entity>> fullList = new HashMap<>();
    private Set<killall.Action> actions = new HashSet<>();
    private Set<killall.removalCriteria> criteria = new HashSet<>();
    private Set<EntityType> customTypes = new HashSet<>();
    private int range = 0;
    private boolean silent = false;
    private World world = null;
    private Location center = null;

    public entityRemoval(CommandSender commandSender) {
      this.sender = commandSender;
    }

    public entityRemoval(Location location) {
      this.center = location;
    }

    public killall.entityRemoval addAction(killall.Action action) {
      this.actions.add(action);
      return this;
    }

    public killall.entityRemoval addType(EntityType entityType) {
      this.customTypes.add(entityType);
      return this;
    }

    public Set<killall.Action> getActions() {
      return this.actions;
    }

    public Set<EntityType> getTypes() {
      return this.customTypes;
    }

    public boolean hasAction(killall.Action action) {
      return this.actions.contains(action);
    }

    public boolean hasType(EntityType entityType) {
      return this.customTypes.contains(entityType);
    }

    public int getRange() {
      return this.range;
    }

    public killall.entityRemoval setRange(int value) {
      this.range = value;
      return this;
    }

    public boolean isSilent() {
      return this.silent;
    }

    public killall.entityRemoval setSilent(boolean flag) {
      this.silent = flag;
      return this;
    }

    public World getWorld() {
      return this.world;
    }

    public killall.entityRemoval setWorld(World targetWorld) {
      this.world = targetWorld;
      return this;
    }

    public Location getCenter() {
      return this.center;
    }

    public killall.entityRemoval setCenter(Location location) {
      this.center = location;
      return this;
    }

    public HashMap<killall.removalCriteria, Set<Entity>> getFullList() {
      return this.fullList;
    }

    public void setFullList(HashMap<killall.removalCriteria, Set<Entity>> hashMap) {
      this.fullList = hashMap;
    }

    public void clean() {
      ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();

      for (World loopWorld : Bukkit.getWorlds()) {
        if ((this.getCenter() == null
                || this.getRange() <= 0
                || this.getCenter().getWorld().equals(loopWorld))
            && (this.getWorld() == null || loopWorld == this.getWorld())) {
          if (this.getCenter() != null && this.getRange() > 0) {
            int value = this.getCenter().getBlockX() - this.getRange() - 16;
            int maxX = this.getCenter().getBlockX() + this.getRange() + 16;
            int minZ = this.getCenter().getBlockZ() - this.getRange() - 16;
            int maxZ = this.getCenter().getBlockZ() + this.getRange() + 16;
            Chunk[] chunks;
            int chunkCount = (chunks = loopWorld.getLoadedChunks()).length;

            for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
              Chunk chunk = chunks[chunkIndex];
              if (chunk.getX() * 16 >= value
                  && chunk.getX() * 16 <= maxX
                  && chunk.getZ() * 16 >= minZ
                  && chunk.getZ() * 16 <= maxZ) {
                arrayList.add(this.addFuture(chunk));
              }
            }
          } else {
            Chunk[] chunksCopy;
            int loadedCount = (chunksCopy = loopWorld.getLoadedChunks()).length;

            for (int loadedIndex = 0; loadedIndex < loadedCount; loadedIndex++) {
              Chunk chunk = chunksCopy[loadedIndex];
              arrayList.add(this.addFuture(chunk));
            }
          }
        }
      }

      CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]))
          .thenRun(
              () -> {
                if (this.hasAction(killall.Action.list)) {
                  this.list();
                } else {
                  this.removeEntities();
                }
              });
    }

    private CompletableFuture<Void> addFuture(Chunk chunk) {
      return CMIScheduler.runAtLocation(
          CMI.getInstance(),
          new Location(chunk.getWorld(), chunk.getX() * 16, 0.0, chunk.getZ() * 16),
          () -> {
            Entity[] entitys;
            int value = (entitys = chunk.getEntities()).length;

            for (int index = 0; index < value; index++) {
              Entity entity = entitys[index];
              this.processEntity(entity);
            }
          });
    }

    public void processEntity(Entity entity) {
      if ((!(entity instanceof Player) || entity.hasMetadata("NPC")) && !entity.isInsideVehicle()) {
        boolean flag = false;
        killall.removalCriteria criteria = null;

        for (killall.removalCriteria candidate : this.getRemovalCriteria()) {
          if (!candidate.isSecondaryCheck() && candidate.addForRemoval(entity)) {
            flag = true;
            criteria = candidate;
            break;
          }
        }

        boolean matches = true;
        killall.removalCriteria[] removalCriterias;
        int value = (removalCriterias = killall.removalCriteria.values()).length;

        for (int index = 0; index < value; index++) {
          killall.removalCriteria entryCriteria = removalCriterias[index];
          if (entryCriteria.isSecondaryCheck()
              && !this.hasRemovalCriteria(entryCriteria)
              && entryCriteria.addForRemoval(entity)) {
            matches = false;
            break;
          }
        }

        if (!this.hasType(entity.getType())
            || !matches && !this.hasRemovalCriteria(killall.removalCriteria.force)) {
          if (this.getTypes().isEmpty()) {
            if (flag && matches || this.hasRemovalCriteria(killall.removalCriteria.force)) {
              this.addForRemoval(entity, criteria);
            }
          }
        } else {
          this.addForRemoval(entity, killall.removalCriteria.custom);
        }
      }
    }

    private void list() {
      HashMap<com.Zrips.CMI.commands.list.killall.removalCriteria, HashMap<EntityType, Integer>> hashMap = new HashMap<>();

      for (var entry : this.getFullList().entrySet()) {
        for (Entity entity : (Set<Entity>) entry.getValue()) {
          if (entity.getType() != null) {
            HashMap<EntityType, Integer> innerMap = hashMap.get(entry.getKey());
            if (innerMap == null) {
              innerMap = new HashMap<>();
              hashMap.put((killall.removalCriteria) entry.getKey(), innerMap);
            }

            Integer integer = (Integer) innerMap.get(entity.getType());
            if (integer == null) {
              integer = 0;
            }

            innerMap.put(entity.getType(), integer + 1);
          }
        }
      }

      LC.info_Spliter.sendMessage(this.sender, new Object[0]);

      for (var outerEntry : hashMap.entrySet()) {
        int value = 0;
        StringBuilder stringBuilder = new StringBuilder();

        for (var innerEntry : outerEntry.getValue().entrySet()) {
          value += innerEntry.getValue();
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          stringBuilder.append(
              CMILC.getIM(
                  killall.class,
                  "removedList",
                  "[type]",
                  CMIEntityType.getRealNameByType((EntityType) innerEntry.getKey()),
                  "[amount]",
                  innerEntry.getValue()));
        }

        CMILC.info(
            killall.class,
            this.sender,
            "infoList",
            "[category]",
            CMILC.getIM(killall.class, ((killall.removalCriteria) outerEntry.getKey()).name()),
            "[amount]",
            value,
            "[list]",
            stringBuilder);
      }

      LC.info_Spliter.sendMessage(this.sender, new Object[0]);
    }

    private void removeEntities() {
      HashMap<killall.removalCriteria, HashMap<EntityType, Integer>> hashMap = new HashMap<>();

      for (killall.removalCriteria criteria : this.getRemovalCriteria()) {
        var set = this.getFullList().get(criteria);
        if (set != null && !set.isEmpty()) {
          for (Entity entity : set) {
            if (this.getRange() > 0 && this.getCenter() != null) {
              if (!entity.getWorld().equals(this.getCenter().getWorld())) {
                continue;
              }

              double number = entity.getLocation().distance(this.getCenter());
              if (number > this.getRange()) {
                continue;
              }
            }

            if (this.hasAction(killall.Action.lightning)) {
              entity.getWorld().strikeLightning(entity.getLocation());
            }

            CMIScheduler.runAtEntity(CMI.getInstance(), entity, entity::remove);
            HashMap<EntityType, Integer> criteriaMap = hashMap.computeIfAbsent(criteria, object -> new HashMap<>());
            criteriaMap.put(entity.getType(), criteriaMap.getOrDefault(entity.getType(), 0) + 1);
          }
        }
      }

      int value = 0;
      StringBuilder stringBuilder = new StringBuilder();

      for (var entry : hashMap.entrySet()) {
        for (var criteriaEntry : entry.getValue().entrySet()) {
          value += criteriaEntry.getValue();
          if (!stringBuilder.toString().isEmpty()) {
            stringBuilder.append(LC.info_ListSpliter.getLocale(new Object[0]));
          }

          try {
            stringBuilder.append(
                CMILC.getIM(
                    killall.class,
                    "removedList",
                    "[type]",
                    CMIEntityType.get((EntityType) criteriaEntry.getKey()).getName(),
                    "[amount]",
                    criteriaEntry.getValue()));
          } catch (Exception | Error value2) {
            value2.printStackTrace();
          }
        }
      }

      if (!this.silent) {
        if (value > 0) {
          CMILC.info(
              killall.class, this.getSender(), "total", "[amount]", value, "[list]", stringBuilder);
        } else {
          CMILC.info(killall.class, this.getSender(), "noRemove");
        }
      }
    }

    private void addForRemoval(Entity entity, killall.removalCriteria criteria) {
      this.fullList.computeIfAbsent(criteria, key -> new HashSet<>()).add(entity);
    }

    public CommandSender getSender() {
      return this.sender;
    }

    public void setSender(CommandSender commandSender) {
      this.sender = commandSender;
    }

    public Set<killall.removalCriteria> getRemovalCriteria() {
      return this.criteria;
    }

    public boolean hasRemovalCriteria(killall.removalCriteria criteria) {
      return this.criteria.contains(criteria);
    }

    public killall.entityRemoval setRemovalCriteria(Set<killall.removalCriteria> set) {
      this.criteria = set;
      return this;
    }

    public killall.entityRemoval addRemovalCriteria(killall.removalCriteria criteria) {
      this.criteria.add(criteria);
      return this;
    }
  }

  private enum removalCriteria {
    monsters("Monsters") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return killall.isMonster(entity);
      }
    },
    pets(true, "Pets") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return entity instanceof Tameable && !killall.isTamed(entity);
      }
    },
    npc(true, "NPCs") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return entity.hasMetadata("NPC");
      }
    },
    animals("Animals") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return entity instanceof Animals;
      }
    },
    ambient("Ambient") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return entity instanceof Ambient;
      }
    },
    vehicles("Vehicles") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return (entity instanceof Boat || entity instanceof Minecart)
            && entity.getPassenger() == null;
      }
    },
    named(true, "With custom name") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return killall.isNamed(entity);
      }
    },
    withitems(true, "With items") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return killall.hasEquipment(entity);
      }
    },
    force("Force") {
      @Override
      public boolean addForRemoval(Entity entity) {
        return true;
      }
    },
    custom("By type");

    private String category;
    private boolean secondaryCheck = false;

    removalCriteria(boolean flag, String string) {
      this.secondaryCheck = flag;
      this.category = string;
    }

    removalCriteria(String string) {
      this.category = string;
    }

    public String getCategory() {
      return this.category;
    }

    public boolean isSecondaryCheck() {
      return this.secondaryCheck;
    }

    public static killall.removalCriteria get(String string) {
      if (string.startsWith("-")) {
        string = string.substring(1);
      }

      killall.removalCriteria[] removalCriterias;
      int value = (removalCriterias = values()).length;

      for (int index = 0; index < value; index++) {
        killall.removalCriteria criteria = removalCriterias[index];
        if (criteria.toString().equalsIgnoreCase(string)) {
          return criteria;
        }
      }

      return null;
    }

    public boolean addForRemoval(Entity entity) {
      return false;
    }
  }
}
