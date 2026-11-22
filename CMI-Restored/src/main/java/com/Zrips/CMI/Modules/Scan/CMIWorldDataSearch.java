package com.Zrips.CMI.Modules.Scan;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Search.CMIDataResultBase;
import com.Zrips.CMI.Modules.Search.CMIDataResultItem;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationBase;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationBlock;
import com.Zrips.CMI.Modules.Search.CMIDataResultLocationEntity;
import com.Zrips.CMI.Modules.Search.CMIDataSearch;
import com.Zrips.CMI.Modules.Search.CMIPlayerSearchPlaceType;
import com.Zrips.CMI.Modules.Search.SearchTypeBase;
import com.Zrips.CMI.Modules.Search.SearchTypeItem;
import com.Zrips.CMI.commands.list.scan;
import com.Zrips.CMI.commands.list.search;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVectorInt2D;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.PaperMethods.PaperLib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CMIWorldDataSearch extends CMIDataSearch {
  protected static CMIWorldDataSearch activeSearch = null;
  World world = null;
  CMIVectorInt2D center = null;
  int range = -1;
  int maxY = 255;
  int minY = 0;
  long skippedChunks = 0L;
  private boolean startScan = false;
  boolean paused = false;
  CompletableFuture<CMIWorldChunksRanged> complete = null;
  private Map<UUID, CMIDataResultLocationBase> foundIn =
      Collections.synchronizedMap(new LinkedHashMap<>());
  private CMIWorldChunksRanged worldInformation = null;
  int chunksLoading = 0;

  public CMIWorldDataSearch(CommandSender commandSender, Location location, int value) {
    super(commandSender);
    this.setWorld(location.getWorld());
    this.setCenter(location.getChunk().getX(), location.getChunk().getZ());
    this.setRange(value);
  }

  public CMIWorldDataSearch(CommandSender commandSender) {
    super(commandSender);
  }

  public void onDataLoadStart() {}

  public World getWorld() {
    return this.world;
  }

  public void setWorld(World newWorld) {
    this.maxY = CMIWorld.getMaxHeight(newWorld);
    this.minY = CMIWorld.getMinHeight(newWorld);
    this.world = newWorld;
    this.recheckWorldInfo();
  }

  public void setCenter(Chunk chunk) {
    this.setCenter(chunk.getX(), chunk.getZ());
  }

  public long getSkippedChunks() {
    return this.skippedChunks;
  }

  public void setCenter(int value, int centerY) {
    this.center = new CMIVectorInt2D(value, centerY);
    this.recheckWorldInfo();
  }

  public CMIVectorInt2D getCenter() {
    return this.center;
  }

  public void setRange(int value) {
    this.range = value;
    this.recheckWorldInfo();
  }

  public int getRange() {
    return this.range;
  }

  private void recheckWorldInfo() {
    if (this.world != null && this.center != null) {
      if (this.range < 0) {
        this.range = CMI.getInstance().getScanManager().getDefaultRange();
      }

      this.onDataLoadStart();
      this.complete =
          CMI.getInstance()
              .getScanManager()
              .getWorldChunkInfoInRangeAsync(this.getWorld(), this.getCenter(), this.range);
      this.complete.thenAccept(
          cmiWorldChunksRanged -> {
            if (cmiWorldChunksRanged != null) {
              this.worldInformation = cmiWorldChunksRanged;
              this.totalToCheck = this.worldInformation.getTotalChunks();
              if (CMI.getInstance().getScanManager().isLogIntoFile()) {
                this.createFileAppender("ScanInfo");
                this.appender.appendData("World -> " + this.world.getName());
                this.appender.appendData(
                    this.worldInformation.getRange() > 0
                        ? "Starting chunk -> "
                            + this.worldInformation.getCenter().getX()
                            + ";"
                            + this.worldInformation.getCenter().getZ()
                            + " Range -> "
                            + this.worldInformation.getRange()
                        : "Global scan ");
                this.appender.appendData(
                    "Aproximate chunks to scan -> " + this.worldInformation.getTotalChunks());
                this.appender.appendData("______________________________");
              }
            }

            if (this.startScan) {
              this.scanNextChunk();
            }

            this.complete = null;
          });
    }
  }

  public static CMIWorldDataSearch getActiveSearch() {
    return activeSearch;
  }

  protected static void setActiveSearch(CMIWorldDataSearch cmiWorldDataSearch) {
    activeSearch = cmiWorldDataSearch;
  }

  @Override
  public void search() {
    if (this.getSearchingFor().isEmpty()) {
      CMILC.info(scan.class, this.getInitiator(), "nothing");
    } else if (getActiveSearch() != null && getActiveSearch().isRunning()) {
      CMILC.info(search.class, this.getInitiator(), "active");
    } else {
      this.searchSpeed = CMI.getInstance().getScanManager().getScanDefaultSpeed();
      setActiveSearch(this);
      this.initSearch();
    }
  }

  @Override
  protected void initSearch() {
    CMILC.info(search.class, this.getInitiator(), "searching");
    if (this.complete != null) {
      this.startScan = true;
    } else {
      this.scanNextChunk();
    }
  }

  public void setPaused(boolean flag) {
    this.paused = flag;
    this.onPauseChange();
  }

  public boolean isPaused() {
    return this.paused;
  }

  public void onPauseChange() {}

  private void scanNextChunk() {
    this.task =
        CMIScheduler.scheduleSyncRepeatingTask(
            CMI.getInstance(),
            () -> {
              if (!this.isPaused()) {
                double number = CMI.getInstance().getLagMeter().getTPS(1);
                if (number < CMI.getInstance().getScanManager().getScanSoftCap()
                    || this.chunksLoading > 0) {
                  this.searchSpeed *= 0.99;
                } else if (this.chunksLoading <= 0) {
                  double speedIncrement = 0.5 / this.searchSpeed;
                  this.searchSpeed += speedIncrement;
                }

                this.searchSpeed = CMINumber.clamp(this.searchSpeed, 1.0, 100.0);
                int value = 0;
                if (this.chunksLoading <= 0) {
                  this.chunksLoading = 0;

                  for (double checkBudget = 0.0;
                      checkBudget < this.searchSpeed && !this.worldInformation.allChecked();
                      checkBudget++) {
                    value++;
                    CMIVectorInt2D cmiVectorInt2D =
                        this.worldInformation.getNextChunkLocation(true);
                    this.worldInformation.addLastChecked();
                    this.addChecked();
                    this.chunksLoading++;
                    if (cmiVectorInt2D == null) {
                      checkBudget -= 0.95;
                      this.skippedChunks++;
                      this.chunksLoading--;
                    } else if (Version.isCurrentEqualOrHigher(Version.v1_18_R1)
                        && !this.world.isChunkGenerated(
                            cmiVectorInt2D.getX(), cmiVectorInt2D.getZ())) {
                      this.skippedChunks++;
                      this.chunksLoading--;
                    } else {
                      PaperLib.getChunkAtAsync(
                              this.world, cmiVectorInt2D.getX(), cmiVectorInt2D.getZ(), false)
                          .thenCompose(
                              chunk -> {
                                this.chunksLoading--;
                                if (chunk == null) {
                                  this.skippedChunks++;
                                  return null;
                                } else {
                                  this.scanChunkBlocks(chunk);
                                  this.scanChunkEntities(chunk);
                                  chunk.unload();
                                  return null;
                                }
                              });
                    }
                  }
                }

                if (!this.worldInformation.allChecked()) {
                  this.onCycle(value - 1);
                } else {
                  this.onCycle(value - 1);
                  this.cancel();
                }
              }
            },
            0L,
            1L);
  }

  private void scanChunkBlocks(Chunk chunk) {
    if (chunk != null) {
      for (int value = 0; value < 16; value++) {
        for (int blockZ = 0; blockZ < 16; blockZ++) {
          for (int blockY = this.minY; blockY < this.maxY; blockY++) {
            Block block = chunk.getBlock(value, blockY, blockZ);
            if (!CMIMaterial.isAir(block.getType())
                && block.getState() instanceof InventoryHolder) {
              InventoryHolder inventoryHolder = (InventoryHolder) block.getState();
              if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                if (chest.getInventory().getHolder() instanceof DoubleChest) {
                  DoubleChest doubleChest = (DoubleChest) chest.getInventory().getHolder();
                  Chest leftChest = (Chest) doubleChest.getLeftSide();
                  if (!leftChest.getLocation().equals(block.getLocation())) {
                    continue;
                  }
                }
              }

              ItemStack[] itemStacks = inventoryHolder.getInventory().getContents();
              this.checkContents(block, itemStacks);
            }
          }
        }
      }
    }
  }

  private void scanChunkEntities(Chunk chunk) {
    if (chunk != null) {
      Object object = null;
      Entity[] entitys;
      int value = (entitys = chunk.getEntities()).length;

      for (int entityIndex = 0; entityIndex < value; entityIndex++) {
        Entity entity = entitys[entityIndex];
        Location location = entity.getLocation();
        CMIEntityType cmiEntityType = CMIEntityType.get(entity.getType());
        ItemStack[] itemStacks = new ItemStack[0];
        if (cmiEntityType != null) {
          switch (cmiEntityType) {
            case ITEM_FRAME:
            case GLOW_ITEM_FRAME:
              ItemFrame itemFrame = (ItemFrame) entity;
              itemStacks = new ItemStack[] {itemFrame.getItem()};
          }
        }

        if (itemStacks.length != 0) {
          this.checkContents(entity, itemStacks);
        }

        if (entity instanceof InventoryHolder && !(entity instanceof Player)) {
          Inventory inventory = ((InventoryHolder) entity).getInventory();
          if (inventory != null) {
            this.checkContents(entity, inventory.getContents());
          }
        }

        if (entity instanceof LivingEntity && !(entity instanceof Player)) {
          LivingEntity livingEntity = (LivingEntity) entity;
          itemStacks = livingEntity.getEquipment().getArmorContents();
          itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
          itemStacks[itemStacks.length - 1] = livingEntity.getEquipment().getItemInHand();
          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
            itemStacks = Arrays.copyOf(itemStacks, itemStacks.length + 1);
            itemStacks[itemStacks.length - 1] = livingEntity.getEquipment().getItemInOffHand();
          }

          this.checkContents(entity, itemStacks);
        }
      }
    }
  }

  public static UUID locationToUUID(Vector vector) {
    String string = vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ();
    return UUID.nameUUIDFromBytes(string.getBytes(StandardCharsets.UTF_8));
  }

  private void checkContents(Block block, ItemStack[] itemStacks) {
    UUID uuid = locationToUUID(block.getLocation().toVector());

    for (SearchTypeBase searchTypeBase : this.searchingFor) {
      if (searchTypeBase instanceof SearchTypeItem) {
        SearchTypeItem searchTypeItem = (SearchTypeItem) searchTypeBase;
        searchTypeItem.setCurrentUUID(uuid);
        searchTypeItem.setCurrentCheckedLocation(CMIPlayerSearchPlaceType.Custom);
        var set = searchTypeItem.checkContent(itemStacks);
        if (set != null && !set.isEmpty()) {
          HashSet<CMIDataResultBase> hashSet = new HashSet<>();

          for (CMIDataResultBase cmiDataResultBase : set) {
            if (cmiDataResultBase instanceof CMIDataResultItem) {
              CMIDataResultItem cmiDataResultItem = (CMIDataResultItem) cmiDataResultBase;
              hashSet.add(
                  new CMIDataResultLocationBlock(
                      CMIMaterial.get(block),
                      new CMIVectorInt3D(block.getLocation().toVector()),
                      cmiDataResultItem.getAmount()));
            }
          }

          this.add(hashSet);
          searchTypeItem.finishedLocationCheck();
        } else {
          searchTypeItem.finishedLocationCheck();
        }
      }
    }
  }

  public Map<UUID, CMIDataResultLocationBase> getSearchResults() {
    synchronized (this.foundIn) {
      return new LinkedHashMap<>(this.foundIn);
    }
  }

  private void checkContents(Entity entity, ItemStack[] itemStacks) {
    UUID uuid = entity.getUniqueId();

    for (SearchTypeBase searchTypeBase : this.searchingFor) {
      if (searchTypeBase instanceof SearchTypeItem) {
        SearchTypeItem searchTypeItem = (SearchTypeItem) searchTypeBase;
        searchTypeItem.setCurrentUUID(uuid);
        searchTypeItem.setCurrentCheckedLocation(CMIPlayerSearchPlaceType.Custom);
        var set = searchTypeItem.checkContent(itemStacks);
        if (set != null && !set.isEmpty()) {
          HashSet<CMIDataResultBase> hashSet = new HashSet<>();

          for (CMIDataResultBase cmiDataResultBase : set) {
            if (cmiDataResultBase instanceof CMIDataResultItem) {
              CMIDataResultItem cmiDataResultItem = (CMIDataResultItem) cmiDataResultBase;
              hashSet.add(
                  new CMIDataResultLocationEntity(
                      entity,
                      new CMIVectorInt3D(entity.getLocation().toVector()),
                      cmiDataResultItem.getAmount()));
            }
          }

          this.add(hashSet);
          searchTypeItem.finishedLocationCheck();
        } else {
          searchTypeItem.finishedLocationCheck();
        }
      }
    }
  }

  private void add(Set<CMIDataResultBase> set) {
    if (set != null && !set.isEmpty()) {
      ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

      for (CMIDataResultBase cmiDataResultBase : set) {
        if (cmiDataResultBase instanceof CMIDataResultLocationBase) {
          CMIDataResultLocationBase cmiDataResultLocationBase = null;
          if (cmiDataResultBase instanceof CMIDataResultLocationEntity) {
            CMIDataResultLocationEntity cmiDataResultLocationEntity =
                (CMIDataResultLocationEntity) cmiDataResultBase;
            cmiDataResultLocationBase =
                this.foundIn.computeIfAbsent(
                    cmiDataResultLocationEntity.getUUID(),
                    uuid ->
                        new CMIDataResultLocationEntity(
                            cmiDataResultLocationEntity.getType(),
                            cmiDataResultLocationEntity.getVector(),
                            0));
            String string =
                cmiDataResultLocationEntity.getType()
                    + " "
                    + cmiDataResultLocationEntity.getVector().getX()
                    + ";"
                    + cmiDataResultLocationEntity.getVector().getY()
                    + ";"
                    + cmiDataResultLocationEntity.getVector().getZ();
            concurrentHashMap.put(
                string,
                concurrentHashMap.getOrDefault(string, 0)
                    + cmiDataResultLocationEntity.getAmount());
          } else {
            CMIDataResultLocationBlock cmiDataResultLocationBlock =
                (CMIDataResultLocationBlock) cmiDataResultBase;
            cmiDataResultLocationBase =
                this.foundIn.computeIfAbsent(
                    cmiDataResultLocationBlock.getUUID(),
                    playerUuid ->
                        new CMIDataResultLocationBlock(
                            cmiDataResultLocationBlock.getType(),
                            cmiDataResultLocationBlock.getVector(),
                            0));
            String playerName =
                cmiDataResultLocationBlock.getType()
                    + " "
                    + cmiDataResultLocationBlock.getVector().getX()
                    + ";"
                    + cmiDataResultLocationBlock.getVector().getY()
                    + ";"
                    + cmiDataResultLocationBlock.getVector().getZ();
            concurrentHashMap.put(
                playerName,
                concurrentHashMap.getOrDefault(playerName, 0)
                    + cmiDataResultLocationBlock.getAmount());
          }

          cmiDataResultLocationBase.addAmount(
              ((CMIDataResultLocationBase) cmiDataResultBase).getAmount());
        }
      }

      this.totalFound++;
      if (CMI.getInstance().getScanManager().isLogIntoFile() && this.appender != null) {
        for (Entry entry : concurrentHashMap.entrySet()) {
          if ((Integer) entry.getValue() > 0) {
            this.appender.appendData((String) entry.getKey() + " -> " + entry.getValue());
          } else {
            this.appender.appendData(entry.getKey());
          }
        }
      }
    }
  }
}
