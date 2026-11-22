package com.Zrips.CMI.Modules.InteractiveCommand;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class InteractiveCommandManager {
  CMI plugin;
  private boolean checkCitizens = false;
  HashMap<String, Map<CuboidArea.ChunkRef, Set<CMIInteractiveCommand>>> map = new HashMap<>();
  HashMap<UUID, CMIInteractiveCommand> entMap = new HashMap<>();
  HashMap<Integer, CMIInteractiveCommand> citiznesMap = new HashMap<>();
  LinkedHashMap<String, CMIInteractiveCommand> nameMap = new LinkedHashMap<>();
  private CMITask saveTask = null;
  boolean saving = false;
  private String fileName = "InteractiveCommands.yml";

  public InteractiveCommandManager(CMI cmi) {
    this.plugin = cmi;
  }

  public Entity getEntityByUUID(UUID uuid) {
    for (World world : Bukkit.getWorlds()) {
      for (Entity entity : world.getEntities()) {
        if (entity.getUniqueId().equals(uuid)) {
          return entity;
        }
      }
    }

    return null;
  }

  public void deleteIC(CMIInteractiveCommand cmiInteractiveCommand) {
    this.nameMap.remove(cmiInteractiveCommand.getName().toLowerCase());
    this.clearLocationsFromIC(cmiInteractiveCommand);

    for (UUID uuid : cmiInteractiveCommand.getUUIDList()) {
      this.entMap.remove(uuid);
    }

    for (CMINPC cminpc : cmiInteractiveCommand.getUUIDMap().values()) {
      if (cminpc.getId() != null) {
        this.citiznesMap.remove(cminpc.getId());
      }
    }
  }

  public void clearLocationsFromIC(CMIInteractiveCommand cmiInteractiveCommand) {
    for (var entry : this.map.entrySet()) {
      for (Entry chunkEntry : ((Iterable<Entry>) ((Map) entry.getValue()).entrySet())) {
        ((Set) chunkEntry.getValue()).remove(cmiInteractiveCommand);
      }
    }
  }

  public void addEntity(UUID uuid, CMIInteractiveCommand cmiInteractiveCommand) {
    this.entMap.put(uuid, cmiInteractiveCommand);
    if (CMIHookType.Citizens.isEnabled()) {
      Integer integer = this.plugin.getCitizensManager().getEntityId(uuid);
      if (integer != null) {
        this.citiznesMap.put(integer, cmiInteractiveCommand);
      }
    }
  }

  public CMIInteractiveCommand addInteractiveCommand(CMIInteractiveCommand cmiInteractiveCommand) {
    return this.addInteractiveCommand(cmiInteractiveCommand, true);
  }

  @Deprecated
  public CMIInteractiveCommand addInteractiveCommand(
      CMIInteractiveCommand cmiInteractiveCommand, boolean flag) {
    return this.addInteractiveCommand(cmiInteractiveCommand, flag, null);
  }

  public CMIInteractiveCommand addInteractiveCommand(
      CMIInteractiveCommand cmiInteractiveCommand, boolean flag, Location location) {
    if (!cmiInteractiveCommand.getLocList().isEmpty()) {
      HashSet<CMILocation> hashSet = new HashSet();
      if (location == null) {
        hashSet.addAll(cmiInteractiveCommand.getLocList());
      } else {
        hashSet.add(new CMILocation(location));
      }

      for (CMILocation cmiLocation : cmiInteractiveCommand.getLocList()) {
        if (cmiLocation == null || cmiLocation.getWorldName() == null) {
          this.plugin.consoleMessage(
              "&eCant determine world for ("
                  + cmiInteractiveCommand.getName()
                  + ") interactable command. Skipping location");
        } else if (location != null
            && flag
            && (cmiLocation.getBlock() == null
                || cmiLocation.getBlock().getType().equals(Material.AIR))) {
          if (!cmiInteractiveCommand.isPublic()) {
            this.plugin.consoleMessage(
                "&eCant add interactive command for air type block ("
                    + cmiInteractiveCommand.getName()
                    + ")");
          }
        } else {
          var worldMap = this.map.get(cmiLocation.getWorldName());
          if (worldMap == null) {
            worldMap = new HashMap();
          }

          CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(cmiLocation);
          Set set = (Set) worldMap.get(chunkRef);
          if (set == null) {
            set = new HashSet();
          }

          set.add(cmiInteractiveCommand);
          worldMap.put(chunkRef, set);
          this.map.put(cmiLocation.getWorldName(), worldMap);
        }
      }
    }

    if (!cmiInteractiveCommand.getUUIDList().isEmpty()) {
      for (UUID uuid : cmiInteractiveCommand.getUUIDList()) {
        this.entMap.put(uuid, cmiInteractiveCommand);
        if (CMIHookType.Citizens.isEnabled()) {
          Integer integer = this.plugin.getCitizensManager().getEntityId(uuid);
          if (integer != null) {
            this.citiznesMap.put(integer, cmiInteractiveCommand);
          }
        }
      }
    }

    this.nameMap.put(cmiInteractiveCommand.getName().toLowerCase(), cmiInteractiveCommand);
    if (flag) {
      this.save();
    }

    return cmiInteractiveCommand;
  }

  public Set<CMIInteractiveCommand> getFullList() {
    return new HashSet<>(this.nameMap.values());
  }

  public Set<CMIInteractiveCommand> getSortedByDistance(Location location) {
    HashMap<CMIInteractiveCommand, Integer> hashMap = new HashMap();
    ArrayList<CMIInteractiveCommand> arrayList = new ArrayList();
    TreeMap<String, CMIInteractiveCommand> treeMap = new TreeMap();

    for (var entry : this.nameMap.entrySet()) {
      if (!((CMIInteractiveCommand) entry.getValue()).getLocList().isEmpty()
          && Config.InteractiveCommandsSort) {
        boolean flag = false;
        double number = 0.0;
        int value = 0;

        for (CMILocation cmiLocation : ((CMIInteractiveCommand) entry.getValue()).getLocList()) {
          if (location.getWorld() == cmiLocation.getWorld()) {
            flag = true;
            value++;
            number += location.distance(cmiLocation);
            if (value >= 10) {
              break;
            }
          }
        }

        if (!flag) {
          arrayList.add((CMIInteractiveCommand) entry.getValue());
        } else {
          int avgDistance = (int) (number / value);
          hashMap.put(entry.getValue(), avgDistance);
        }
      } else {
        treeMap.put((String) entry.getKey(), (CMIInteractiveCommand) entry.getValue());
      }
    }

    hashMap = (HashMap) this.plugin.getSortingManager().sortByValueObject((Map) hashMap);
    LinkedHashSet<CMIInteractiveCommand> linkedHashSet = new LinkedHashSet();

    for (Entry sortedEntry : hashMap.entrySet()) {
      linkedHashSet.add((CMIInteractiveCommand) sortedEntry.getKey());
    }

    if (!arrayList.isEmpty()) {
      for (CMIInteractiveCommand cmiInteractiveCommand : arrayList) {
        treeMap.put(cmiInteractiveCommand.getName(), cmiInteractiveCommand);
      }
    }

    for (Entry treeEntry : treeMap.entrySet()) {
      linkedHashSet.add((CMIInteractiveCommand) treeEntry.getValue());
    }

    return linkedHashSet;
  }

  public CMIInteractiveCommand getByUUID(UUID uuid) {
    if (this.checkCitizens && this.plugin.isServerLoaded()) {
      this.checkCitizens();
      this.checkCitizens = false;
    }

    return this.entMap.get(uuid);
  }

  public CMIInteractiveCommand getByCitizensId(Integer integer) {
    if (integer == null) {
      return null;
    }

    if (this.checkCitizens && this.plugin.isServerLoaded()) {
      this.checkCitizens();
      this.checkCitizens = false;
    }

    return this.citiznesMap.get(integer);
  }

  private void checkCitizens() {
    int value = 0;
    CMIHookType.Citizens.update();
    if (CMIHookType.Citizens.isEnabled()) {
      boolean flag = false;

      for (var entry : this.nameMap.entrySet()) {
        for (CMINPC cminpc : ((CMIInteractiveCommand) entry.getValue()).getCitizensCache()) {
          Integer integer = null;
          boolean foundNpc = false;
          if (cminpc.getUuid() != null) {
            integer = this.plugin.getCitizensManager().getEntityId(cminpc.getUuid());
            if (integer != null) {
              cminpc.setId(integer);
              ((CMIInteractiveCommand) entry.getValue()).addUUID(cminpc.getUuid(), integer);
              cminpc.setSecondaryUUID(
                  this.plugin.getCitizensManager().getEntityUUID(cminpc.getId()));
              this.entMap.put(cminpc.getUuid(), (CMIInteractiveCommand) entry.getValue());
              this.citiznesMap.put(integer, (CMIInteractiveCommand) entry.getValue());
              value++;
              foundNpc = true;
              flag = true;
            }
          }

          if (integer == null) {
            UUID uuid = this.plugin.getCitizensManager().getEntityRealUUID(cminpc.getId());
            if (uuid != null) {
              ((CMIInteractiveCommand) entry.getValue()).addUUID(uuid, cminpc.getId());
              cminpc.setSecondaryUUID(
                  this.plugin.getCitizensManager().getEntityUUID(cminpc.getId()));
              this.entMap.put(uuid, (CMIInteractiveCommand) entry.getValue());
              value++;
              foundNpc = true;
              flag = true;
            }

            this.citiznesMap.put(cminpc.getId(), (CMIInteractiveCommand) entry.getValue());
          }

          if (!foundNpc) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("&eFailed to find NPC for " + (String) entry.getKey() + " ");
            if (cminpc.getId() != null) {
              stringBuilder.append("by " + cminpc.getId() + " id ");
            }

            if (cminpc.getUuid() != null) {
              stringBuilder.append(" " + cminpc.getUuid().toString() + " uuid ");
            }

            CMI.getInstance().consoleMessage(stringBuilder.toString());
          }
        }

        ((CMIInteractiveCommand) entry.getValue()).getCitizensCache().clear();
      }

      CMI.getInstance()
          .consoleMessage("Loaded " + value + " citizen NPC's as interactable entities");
      if (flag) {
        this.save();
      }
    }
  }

  public CMIInteractiveCommand getByName(String string) {
    return this.nameMap.get(string.toLowerCase());
  }

  public CMIInteractiveCommand removeLoc(CMILocation cmiLocation) {
    if (cmiLocation == null) {
      return null;
    }

    World world = cmiLocation.getWorld();
    if (world == null) {
      return null;
    }

    String string = world.getName();
    if (string != null && this.map.containsKey(string)) {
      CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(cmiLocation);
      var worldMap = this.map.get(string);
      if (worldMap.containsKey(chunkRef)) {
        for (CMIInteractiveCommand cmiInteractiveCommand :
            (Set<CMIInteractiveCommand>) worldMap.get(chunkRef)) {
          if (cmiInteractiveCommand != null) {
            for (CMILocation storedLocation : cmiInteractiveCommand.getLocList()) {
              if (storedLocation.getBlockX() == cmiLocation.getBlockX()
                  && storedLocation.getBlockY() == cmiLocation.getBlockY()
                  && storedLocation.getBlockZ() == cmiLocation.getBlockZ()) {
                cmiInteractiveCommand.getLocList().remove(storedLocation);
                this.save();
                return cmiInteractiveCommand;
              }
            }
          }
        }
      }

      return null;
    } else {
      return null;
    }
  }

  public CMIInteractiveCommand removeEntity(UUID uuid) {
    if (uuid == null) {
      return null;
    }

    CMIInteractiveCommand cmiInteractiveCommand = this.entMap.remove(uuid);
    if (cmiInteractiveCommand == null) {
      return null;
    }

    if (CMIHookType.Citizens.isEnabled()) {
      Integer integer = this.plugin.getCitizensManager().getEntityId(uuid);
      if (integer != null) {
        this.citiznesMap.remove(integer);
      }
    }

    cmiInteractiveCommand.getUUIDMap().remove(uuid);
    return cmiInteractiveCommand;
  }

  public CMIInteractiveCommand getByLoc(Location location) {
    if (location == null) {
      return null;
    }

    World world = location.getWorld();
    if (world == null) {
      return null;
    }

    String string = world.getName();
    if (string != null && this.map.containsKey(string)) {
      CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
      var worldMap = this.map.get(string);
      if (worldMap.containsKey(chunkRef)) {
        for (CMIInteractiveCommand cmiInteractiveCommand :
            (Set<CMIInteractiveCommand>) worldMap.get(chunkRef)) {
          if (cmiInteractiveCommand != null) {
            for (CMILocation cmiLocation : cmiInteractiveCommand.getLocList()) {
              if (cmiLocation.getBlockX() == location.getBlockX()
                  && cmiLocation.getBlockY() == location.getBlockY()
                  && cmiLocation.getBlockZ() == location.getBlockZ()) {
                return cmiInteractiveCommand;
              }
            }
          }
        }
      }

      return null;
    } else {
      return null;
    }
  }

  public void save() {
    if (this.saveTask == null) {
      this.saveTask =
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                this.forceSave();
                this.saveTask = null;
              },
              100L);
    }
  }

  public void forceSave() {
    if (!this.saving) {
      this.saving = true;

      try {
        ConfigReader configReader = null;

        try {
          configReader =
              new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
        } catch (Exception exception) {
        }

        if (configReader == null) {
          return;
        }

        for (CMIInteractiveCommand cmiInteractiveCommand : this.getFullList()) {
          try {
            String string = cmiInteractiveCommand.getName().replace(".", "_");
            var list = cmiInteractiveCommand.getLocListAsString();
            configReader.set(string + ".Locations", list.isEmpty() ? null : list);
            configReader.set(
                string + ".UUID",
                cmiInteractiveCommand.getUUIDStringListSave().isEmpty()
                    ? null
                    : cmiInteractiveCommand.getUUIDStringListSave());
            var npcList = cmiInteractiveCommand.getNPCList();
            if (!npcList.isEmpty()) {
              ArrayList<String> arrayList = new ArrayList();

              for (CMINPC cminpc : npcList) {
                arrayList.add(
                    (cminpc.getId() == null ? -1 : cminpc.getId())
                        + "_"
                        + (cminpc.getUuid() == null
                            ? this.plugin.getServerUUID().toString()
                            : cminpc.getUuid().toString())
                        + (cminpc.getSecondaryUUID() == null
                            ? ""
                            : "_" + cminpc.getSecondaryUUID().toString()));
              }

              configReader.set(string + ".NPC", arrayList.isEmpty() ? null : arrayList);
            }

            configReader.set(string + ".Commands", cmiInteractiveCommand.getCommands());
            if (cmiInteractiveCommand.containsSigntext()) {
              configReader.set(string + ".SignLines", cmiInteractiveCommand.getSignLines());
            } else {
              configReader.set(string + ".SignLines", null);
            }

            if (cmiInteractiveCommand.isPublic()) {
              configReader.set(string + ".Public", true);
            } else {
              configReader.set(string + ".Public", null);
            }

            if (cmiInteractiveCommand.isCancelInteraction()) {
              configReader.set(string + ".CancelInt", true);
            } else {
              configReader.set(string + ".CancelInt", null);
            }

            if (!cmiInteractiveCommand.isSignRequiresPerm()) {
              configReader.set(string + ".ReqPerm", false);
            } else {
              configReader.set(string + ".ReqPerm", null);
            }
          } catch (Exception exception2) {
            exception2.printStackTrace();
          }
        }

        configReader.save();
      } catch (Exception | Error value) {
        value.printStackTrace();
      }

      this.saving = false;
    }
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    this.plugin.safeRenameFile("interactiveCommands.yml", "Saves" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
    } catch (Exception exception) {
    }

    if (configReader != null) {
      this.map.clear();
      this.entMap.clear();
      this.citiznesMap.clear();
      this.nameMap.clear();
      if (!configReader.getC().getKeys(false).isEmpty()) {
        var configValues = configReader.getC().getValues(false);

        for (Entry entry : configValues.entrySet()) {
          String string = (String) entry.getKey();
          CMIInteractiveCommand cmiInteractiveCommand = new CMIInteractiveCommand(string);

          try {
            var sectionValues =
                configReader
                    .getC()
                    .getConfigurationSection((String) entry.getKey())
                    .getValues(false);
            if (sectionValues.containsKey("Location")) {
              for (Location location : (List<Location>) sectionValues.get("Location")) {
                try {
                  cmiInteractiveCommand.addLoc(new CMILocation(location));
                } catch (Exception exception2) {
                  exception2.printStackTrace();
                }
              }
            }

            if (sectionValues.containsKey("Locations")) {
              for (String locationString : (List<String>) sectionValues.get("Locations")) {
                try {
                  CMILocation cmiLocation =
                      this.plugin.getPlayerManager().convertStringToLocation(locationString);
                  if (cmiLocation != null) {
                    cmiInteractiveCommand.addLoc(cmiLocation);
                  }
                } catch (Exception exception3) {
                  exception3.printStackTrace();
                }
              }
            }

            if (sectionValues.containsKey("ID")) {
              try {
                for (Integer integer : (List<Integer>) sectionValues.get("ID")) {
                  cmiInteractiveCommand.getCitizensCache().add(new CMINPC(integer));
                  this.checkCitizens = true;
                }
              } catch (Exception exception4) {
              }
            }

            if (sectionValues.containsKey("NPC")) {
              for (String npcEntry : (List<String>) sectionValues.get("NPC")) {
                String[] strings = npcEntry.split("_");
                int value = -1;

                try {
                  value = Integer.parseInt(strings[0]);
                } catch (Exception exception5) {
                  exception5.printStackTrace();
                }

                UUID uuid = null;

                try {
                  if (strings.length > 1 && strings[1].length() == 36) {
                    uuid = UUID.fromString(strings[1]);
                  }
                } catch (Exception exception6) {
                  exception6.printStackTrace();
                }

                UUID secondaryUuid = null;

                try {
                  if (strings.length > 2 && strings[2].length() == 36) {
                    secondaryUuid = UUID.fromString(strings[2]);
                  }
                } catch (Exception exception7) {
                  exception7.printStackTrace();
                }

                cmiInteractiveCommand
                    .getCitizensCache()
                    .add(new CMINPC(uuid).setId(value).setSecondaryUUID(secondaryUuid));
                this.checkCitizens = true;
              }
            }

            if (sectionValues.containsKey("UUID")) {
              for (String uuidEntry : (List<String>) sectionValues.get("UUID")) {
                try {
                  if (!cmiInteractiveCommand.hasUUID(UUID.fromString(uuidEntry))) {
                    cmiInteractiveCommand.addUUID(UUID.fromString(uuidEntry), null);
                  }
                } catch (Exception exception8) {
                  exception8.printStackTrace();
                }
              }
            }

            if (sectionValues.containsKey("Commands")) {
              cmiInteractiveCommand.setCommands((List<String>) sectionValues.get("Commands"));
            }

            if (sectionValues.containsKey("SignLines")) {
              cmiInteractiveCommand.setSignLines((List<String>) sectionValues.get("SignLines"));
            }

            if (sectionValues.containsKey("CancelInt")) {
              cmiInteractiveCommand.setCancelInteraction((Boolean) sectionValues.get("CancelInt"));
            }

            if (sectionValues.containsKey("Public")) {
              cmiInteractiveCommand.setPublic((Boolean) sectionValues.get("Public"));
            }

            if (sectionValues.containsKey("ReqPerm")) {
              cmiInteractiveCommand.setSignRequiresPerm((Boolean) sectionValues.get("ReqPerm"));
            }
          } catch (Exception exception9) {
            CMIMessages.sendMessage(
                Bukkit.getConsoleSender(),
                "Cant load interactive command (" + (String) entry.getKey() + ")");
            exception9.printStackTrace();
            continue;
          }

          this.addInteractiveCommand(cmiInteractiveCommand, false);
        }

        this.plugin.loadMessage(
            this.nameMap.size(), "interactive commands", System.currentTimeMillis() - startTime);
      }
    }
  }

  public CMITask getSaveTask() {
    return this.saveTask;
  }
}
