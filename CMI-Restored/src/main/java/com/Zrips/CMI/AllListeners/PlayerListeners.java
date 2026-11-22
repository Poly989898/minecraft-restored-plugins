package com.Zrips.CMI.AllListeners;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMIBanEntry;
import com.Zrips.CMI.Containers.CMIBanRecords;
import com.Zrips.CMI.Containers.CMIColorTypes;
import com.Zrips.CMI.Containers.CMIEvent;
import com.Zrips.CMI.Containers.CMIGod;
import com.Zrips.CMI.Containers.CMITextCleaner;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIWorldLimits;
import com.Zrips.CMI.Containers.SleepStats;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.PlayerManager;
import com.Zrips.CMI.commands.list.donate;
import com.Zrips.CMI.commands.list.maintenance;
import com.Zrips.CMI.commands.list.sc;
import com.Zrips.CMI.commands.list.util;
import com.Zrips.CMI.utils.CMINPCChecker;
import com.Zrips.CMI.utils.DateFormat;
import com.Zrips.CMI.utils.SpawnUtil;
import com.Zrips.CMI.utils.Util;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Entities.MobHeadInfo;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.NBT.CMINBT;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.sign.Side;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Golem;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;

public class PlayerListeners implements Listener {
  private CMI plugin;
  HashMap<UUID, PlayerListeners.HungerCache> hungerMap = new HashMap<>();
  public static HashMap<UUID, String[]> scMap = new HashMap<>();

  public PlayerListeners(CMI cmi) {
    this.plugin = cmi;
  }

  private static boolean isMonster(Entity entity) {
    return entity instanceof Monster;
  }

  private static boolean isAnimal(Entity entity) {
    return entity instanceof Animals;
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void PlayerPickupItemEvent(PlayerPickupItemEvent playerPickupItemEvent) {
    if (playerPickupItemEvent.getItem().hasMetadata("CMIShoot")) {
      var list = playerPickupItemEvent.getItem().getMetadata("CMIShoot");
      if (list != null && !list.isEmpty()) {
        playerPickupItemEvent.setCancelled(true);
        playerPickupItemEvent.getItem().remove();
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onRespawn(PlayerRespawnEvent playerRespawnEvent) {
    try {
      if (this.plugin.getHomeManager().getRespawnImmortality() > 0
          && this.plugin.getHomeManager().getRespawnImmortality() <= 60) {
        CMIScheduler.runTask(
            this.plugin,
            () -> {
              if (playerRespawnEvent.getPlayer().isOnline()) {
                CMIGod.applyNoDamageTick(
                    playerRespawnEvent.getPlayer(),
                    this.plugin.getHomeManager().getRespawnImmortality() * 20);
              }
            });
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onRespawnSetLocation(PlayerRespawnEvent playerRespawnEvent) {
    try {
      Location location =
          this.plugin
              .getHomeManager()
              .getReSpawnLocation(
                  playerRespawnEvent.getPlayer(), playerRespawnEvent.getRespawnLocation());
      if (location == null || location.getWorld() == null) {
        return;
      }

      playerRespawnEvent.setRespawnLocation(location);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerDeathEvent(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    if (PermissionsManager.CMIPerm.keepinventory.hasSetPermission(player)) {
      playerDeathEvent.setKeepInventory(true);
      if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
        playerDeathEvent.getDrops().clear();
      }
    }

    if (PermissionsManager.CMIPerm.keepexp.hasSetPermission(player)) {
      playerDeathEvent.setKeepLevel(true);
      playerDeathEvent.setDroppedExp(0);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerDeathEventHunger(PlayerDeathEvent playerDeathEvent) {
    Player player = playerDeathEvent.getEntity();
    this.hungerMap.put(
        player.getUniqueId(),
        new PlayerListeners.HungerCache(player.getFoodLevel(), player.getSaturation()));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void PlayerRespawnEventHunger(PlayerRespawnEvent playerRespawnEvent) {
    Player player = playerRespawnEvent.getPlayer();
    PlayerListeners.HungerCache hungerCache = this.hungerMap.remove(player.getUniqueId());
    if (hungerCache != null
        && PermissionsManager.CMIPerm.hunger_keepafterdeath.hasSetPermission(player)) {
      CMIScheduler.runTaskLater(
          this.plugin,
          () -> {
            if (player.isOnline()) {
              player.setFoodLevel(hungerCache.getHunger());
              player.setSaturation(hungerCache.getSaturation());
            }
          },
          5L);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onFishingRodUse(PlayerFishEvent playerFishEvent) {
    Player player = playerFishEvent.getPlayer();
    if (this.plugin.getConfigManager().isPreventHook()
        && playerFishEvent.getCaught() != null
        && playerFishEvent.getCaught() instanceof LivingEntity) {
      playerFishEvent.setCancelled(true);
      int value = player.getInventory().getHeldItemSlot();
      int nextSlot = value >= 8 ? 0 : value + 1;
      player.getInventory().setHeldItemSlot(nextSlot);
      CMIScheduler.runTaskLater(
          this.plugin, () -> player.getInventory().setHeldItemSlot(value), 1L);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void VehicleEnterEvent(VehicleEnterEvent vehicleEnterEvent) {
    if (vehicleEnterEvent.getVehicle() instanceof Boat) {
      if (this.plugin.getConfigManager().isPreventEntityBoatEnterMonsters()
          && isMonster(vehicleEnterEvent.getEntered())) {
        vehicleEnterEvent.setCancelled(true);
      }

      if (this.plugin.getConfigManager().isPreventEntityBoatEnterAnimals()
          && isAnimal(vehicleEnterEvent.getEntered())) {
        vehicleEnterEvent.setCancelled(true);
      }

      if (this.plugin.getConfigManager().isPreventEntityBoatEnterVillagers()
          && vehicleEnterEvent.getEntered() instanceof Villager) {
        vehicleEnterEvent.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
      Block block = playerInteractEvent.getClickedBlock();

      try {
        if (!CMIMaterial.isBed(block.getType())) {
          return;
        }
      } catch (Exception exception) {
        return;
      }

      Environment environment = block.getLocation().getWorld().getEnvironment();
      if (environment != Environment.NORMAL) {
        if (this.plugin.getConfigManager().isPreventBedExplosionNether()
            || block.getLocation().getWorld().getEnvironment() != Environment.NETHER) {
          if (this.plugin.getConfigManager().isPreventBedExplosionTheEnd()
              || block.getLocation().getWorld().getEnvironment() != Environment.THE_END) {
            playerInteractEvent.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onInteractHead(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK && Config.ShowSkullOwner) {
      try {
        if (Version.isCurrentHigher(Version.v1_8_R3)
            && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception exception) {
      }

      Block block = playerInteractEvent.getClickedBlock();

      try {
        CMIMaterial cmiMaterial = CMIMaterial.get(block);
        if (!cmiMaterial.isPlayerHead()) {
          return;
        }
      } catch (Throwable throwable) {
        return;
      }

      Skull skull = (Skull) block.getState();
      String string = skull.getOwner();
      String line = null;
      if (Version.isCurrentEqualOrHigher(Version.v1_9_R1) && (string == null || string.isEmpty())) {
        try {
          CMINBT cminbt = new CMINBT(block);
          var list = cminbt.getList("SkullOwner.Properties.textures");
          if (list != null) {
            line = ((String) list.get(0)).split("Value:\"", 2)[1].split("\"", 2)[0];
            String secondLine = null;

            try {
              byte[] bs = Base64.getDecoder().decode(line);
              secondLine = new String(bs);
            } catch (Throwable throwable2) {
              byte[] decoded = Base64.getMimeDecoder().decode(line);
              secondLine = new String(decoded);
            }

            if (secondLine.contains("profileName\" : \"")) {
              string = secondLine.split("profileName\" : \"", 2)[1].split("\"", 2)[0];
            }
          }
        } catch (Throwable throwable3) {
        }
      }

      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(string);
      if ((string == null || string.isEmpty()) && line != null) {
        try {
          CMIEntityType cmiEntityType = CMIEntityType.getByTexture(line);
          if (cmiEntityType != null) {
            MobHeadInfo mobHeadInfo = null;

            try {
              mobHeadInfo = cmiEntityType.getHeadInfo(line);
            } catch (Throwable throwable4) {
            }

            if (mobHeadInfo != null && mobHeadInfo.getCustomName() != null) {
              string = mobHeadInfo.getCustomName();
            } else {
              string = cmiEntityType.getName();
            }
          }
        } catch (Throwable throwable5) {
        }
      }

      if (string == null) {
        string = LC.info_unknown.getLocale(new Object[0]);
      }

      Snd snd = new Snd();
      snd.setTarget(cmiUser);
      if (cmiUser == null) {
        snd.setTargetName(string);
      }

      CMILC.sendMessage(
          playerInteractEvent.getPlayer(), LC.info_skullOwner.getLocale(new Object[] {snd}));
    }
  }

  @EventHandler
  public void onInteractBeeHive(PlayerInteractEvent playerInteractEvent) {
    if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK
        && !Version.isCurrentLower(Version.v1_15_R1)
        && Config.ShowBeeHive) {
      try {
        if (Version.isCurrentHigher(Version.v1_8_R3)
            && playerInteractEvent.getHand() != EquipmentSlot.HAND) {
          return;
        }
      } catch (Exception exception) {
      }

      Block block = playerInteractEvent.getClickedBlock();

      try {
        CMIMaterial cmiMaterial = CMIMaterial.get(block);
        if (cmiMaterial != CMIMaterial.BEE_NEST && cmiMaterial != CMIMaterial.BEEHIVE) {
          return;
        }
      } catch (Throwable throwable) {
        return;
      }

      byte b = 5;
      int value = 3;
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && block.getState() instanceof Beehive) {
        Beehive beehive = (Beehive) block.getState();
        value = beehive.getMaxEntities();
      }

      int honeyLevel = this.plugin.getReflectionManager().getHoneyLevel(block);
      if (honeyLevel >= 0) {
        CMILC.info_beeinfo.sendMessage(
            playerInteractEvent.getPlayer(),
            "[level]",
            honeyLevel,
            "[maxlevel]",
            Integer.valueOf(b),
            "[maxcount]",
            value,
            "[count]",
            this.plugin.getReflectionManager().getBeesInside(block));
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void PlayerEditBookEvent(PlayerEditBookEvent playerEditBookEvent) {
    if (!playerEditBookEvent.isCancelled()) {
      BookMeta bookMeta = playerEditBookEvent.getNewBookMeta();
      if (Config.BooksAddDate && bookMeta.getAuthor() != null) {
        var list = bookMeta.getLore();
        if (list == null) {
          list = new ArrayList();
        }

        list.add(
            CMILC.info_bookDate.getLocale(
                "[date]", DateFormat.MiliToDate(System.currentTimeMillis())));
        bookMeta.setLore(list);
      }

      if (Version.isCurrentLower(Version.v1_15_R1)
          || Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
        Player player = playerEditBookEvent.getPlayer();

        for (int value = 0; value < bookMeta.getPages().size(); value++) {
          String string = bookMeta.getPage(value + 1);
          string = string.replace("§0\n", "\n");
          string = CMITextCleaner.cleanFromColorCodes(player, string, CMIColorTypes.books, true);
          string = string.replace("＆", "&");
          bookMeta.setPage(value + 1, string);
        }
      }

      playerEditBookEvent.setNewBookMeta(bookMeta);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void EntityDeathEvent(EntityDeathEvent entityDeathEvent) {
    try {
      if (!this.plugin.getConfigManager().isPreventIronGolemRoses()
          || !(entityDeathEvent.getEntity() instanceof Golem)) {
        return;
      }

      for (ItemStack itemStack : new ArrayList<ItemStack>(entityDeathEvent.getDrops())) {
        if (CMIMaterial.POPPY.equals(itemStack.getType())
            || CMIMaterial.ROSE_RED.equals(itemStack.getType())) {
          entityDeathEvent.getDrops().remove(itemStack);
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onCommand(ServerCommandEvent serverCommandEvent) {
    if (this.plugin.getConfigManager().isMulticraftDisableList()
        && (serverCommandEvent.getCommand().equalsIgnoreCase("list")
            || serverCommandEvent.getCommand().equalsIgnoreCase("cmi list"))) {
      try {
        serverCommandEvent.setCancelled(true);
      } catch (NoSuchMethodError noSuchMethodError) {
      }

      System.out.print(" ");
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void EntityPortalEnterEvent(EntityPortalEnterEvent entityPortalEnterEvent) {
    if (entityPortalEnterEvent.getEntityType() == EntityType.EXPERIENCE_ORB
        && this.plugin.getConfigManager().isPreventExpPortals()) {
      ExperienceOrb experienceOrb = (ExperienceOrb) entityPortalEnterEvent.getEntity();
      experienceOrb.setExperience(0);
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onTeleport(PlayerTeleportEvent playerTeleportEvent) {
    Player player = playerTeleportEvent.getPlayer();
    if (playerTeleportEvent.getCause().name().equalsIgnoreCase("CHORUS_FRUIT")
        && !PermissionsManager.CMIPerm.chorusteleport.hasPermission(player)) {
      playerTeleportEvent.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerLoginLimit(PlayerLoginEvent playerLoginEvent) {
    int value = Bukkit.getMaxPlayers();
    int playerCount = this.plugin.getUtilManager().getOnlinePlayerCount();
    if (playerLoginEvent.getResult().equals(Result.KICK_FULL)
        && playerCount >= value
        && PermissionsManager.CMIPerm.fullserver_bypass.hasPermission(
            playerLoginEvent.getPlayer())) {
      playerLoginEvent.allow();

      try {
        playerLoginEvent.setResult(Result.ALLOWED);
      } catch (Exception | Error value3) {
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerLoginFakeUser(PlayerLoginEvent playerLoginEvent) {
    if (playerLoginEvent
            .getPlayer()
            .getName()
            .equalsIgnoreCase(this.plugin.getPlayerManager().getFakeUserName())
        || playerLoginEvent
            .getPlayer()
            .getName()
            .equalsIgnoreCase(this.plugin.getPlayerManager().getFakeUserName().replace("-", ""))
        || playerLoginEvent
            .getPlayer()
            .getUniqueId()
            .equals(this.plugin.getPlayerManager().getFakeUserUUID())
        || playerLoginEvent.getPlayer().getUniqueId().equals(PlayerManager.oldFakeUserUUID)) {
      playerLoginEvent.disallow(
          Result.KICK_OTHER, "Sneaky one, change NickName or suffer my wrath!");
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerLogin(PlayerLoginEvent playerLoginEvent) {
    if (this.plugin.getConfigManager().isMaintenance()) {
      if (!PermissionsManager.CMIPerm.command_maintenance_bypass.hasPermission(
          playerLoginEvent.getPlayer())) {
        String string =
            this.plugin.getConfigManager().getMaintenanceMessage() == null
                ? CMILC.getIM(maintenance.class, "feedback")
                : this.plugin.getConfigManager().getMaintenanceMessage();
        string =
            this.plugin
                .getPlaceholderAPIManager()
                .updatePlaceHolders(playerLoginEvent.getPlayer(), string);
        playerLoginEvent.disallow(Result.KICK_OTHER, CMIChatColor.translate(string));
      } else {
        CMIScheduler.runTaskLater(
            this.plugin,
            () -> {
              if (playerLoginEvent.getPlayer().isOnline() && Config.maintenanceBossbar) {
                String text =
                    this.plugin.getConfigManager().getMaintenanceMessage() == null
                        ? CMILC.info_maintenance.getLocale()
                        : this.plugin.getConfigManager().getMaintenanceMessage();
                text =
                    this.plugin
                        .getPlaceholderAPIManager()
                        .updatePlaceHolders(playerLoginEvent.getPlayer(), text);
                if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
                  try {
                    CMIUser cmiUser =
                        this.plugin.getPlayerManager().getUser(playerLoginEvent.getPlayer());
                    BossBarInfo bossBarInfo = cmiUser.getBossBar("CMIMaintenance");
                    if (bossBarInfo == null) {
                      bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIMaintenance");
                    }

                    bossBarInfo.setKeepForTicks(-1);
                    bossBarInfo.setColor(BarColor.BLUE);
                    bossBarInfo.setTitleOfBar(text);
                    bossBarInfo.setStyle(BarStyle.SOLID);
                    cmiUser.addBossBar(bossBarInfo);
                  } catch (Exception | Error value) {
                  }
                }
              }
            },
            40L);
      }
    }

    if (playerLoginEvent.getResult() == Result.KICK_BANNED
        && this.plugin.getConfigManager().isOverrideLoginMessage()) {
      BanEntry banEntry =
          CMIBanRecords.getBanEntryByIP(playerLoginEvent.getAddress().getHostAddress());
      if (banEntry != null) {
        playerLoginEvent.setKickMessage(CMIBanEntry.getIpBanMessage(banEntry));
      } else {
        banEntry = CMIBanRecords.getBanEntry(playerLoginEvent.getPlayer());
        if (banEntry != null) {
          playerLoginEvent.setKickMessage(CMIBanEntry.getBanMessage(banEntry));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerFirstJoin(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (!player.hasPlayedBefore()
        && this.plugin.getConfigManager().getFirstSpawnPoint() != null
        && !CMINPCChecker.isNPC(player)) {
      this.plugin
          .getTeleportHandler()
          .teleportPlayer(player, this.plugin.getConfigManager().getFirstSpawnPoint(), false);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoinInSpawn(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    if (player.hasPlayedBefore()
        && SpawnUtil.isSpawnSpawnOnJoin()
        && !CMINPCChecker.isNPC(player)
        && !PermissionsManager.CMIPerm.spawnonjoin_bypass.hasSetPermission(player)) {
      if (!SpawnUtil.isIgnoresSpawnWorld(player.getWorld().getName())) {
        CMILocation cmiLocation = SpawnUtil.getSpawnPoint(player);
        if (cmiLocation != null) {
          this.plugin.getTeleportHandler().teleportPlayer(player, cmiLocation);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void PlayerQuitEvent(PlayerQuitEvent playerQuitEvent) {
    CMIScheduler.runTask(
        this.plugin,
        () -> {
          Player player = playerQuitEvent.getPlayer();
          SleepStats.informMap.remove(player.getUniqueId());
          CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
          if (cmiUser != null) {
            if (cmiUser.getTfly() == -1L && cmiUser.getFlightCharge().getCharge() == 0.0) {
              cmiUser.setFlying(false);
              cmiUser.setAllowFlight(false);
            } else if (cmiUser.getTfly() > System.currentTimeMillis()) {
              cmiUser.setFlying(true);
            }

            cmiUser.setWasFlying(player.isFlying());
            if (cmiUser.getTfly() == 0L) {
              cmiUser.setHadAllowFlight(player.getAllowFlight());
            } else {
              cmiUser.setHadAllowFlight(false);
            }

            cmiUser.cacheClear();
          }
        });
  }

  @EventHandler(priority = EventPriority.LOW)
  public void PlayerQuitEventPermissioCache(PlayerQuitEvent playerQuitEvent) {
    CMIScheduler.runTask(
        this.plugin,
        () -> {
          Player player = playerQuitEvent.getPlayer();
          this.plugin.getPermissionsManager().removeFromCache(player);
          if (player.getVehicle() != null
              && player.getVehicle().getType().equals(EntityType.PLAYER)) {
            player.leaveVehicle();
          }
        });
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerJoinSafe(PlayerJoinEvent playerJoinEvent) {
    Player player = playerJoinEvent.getPlayer();
    CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
    if (cmiUser != null) {
      try {
        if (this.plugin.getPlayerManager().getFakeOperatorRaw() != null) {
          playerJoinEvent
              .getPlayer()
              .hidePlayer(this.plugin, this.plugin.getPlayerManager().getFakeOperatorRaw());
        }
      } catch (Throwable throwable) {
      }

      if (Config.ImmortalityOnJoin > 0 && Config.ImmortalityOnJoin <= 60) {
        CMIGod.applyNoDamageTick(player, Config.ImmortalityOnJoin * 20);
      }

      boolean flag = PermissionsManager.CMIPerm.command_$1.hasPermission(player, "fly");
      if (flag && cmiUser.hadAllowFlight() || cmiUser.wasFlying() && cmiUser.hadAllowFlight()) {
        player.setAllowFlight(true);
      }

      if (PermissionsManager.CMIPerm.command_fly_safelogin.hasPermission(player)) {
        if (cmiUser.wasFlying() && (flag || cmiUser.getTfly() > System.currentTimeMillis())
            || !player.isOnGround() && flag) {
          var completableFuture =
              this.plugin
                  .getTeleportHandler()
                  .getSafeLocation(player, player.getLocation(), 0, 6, false);
          completableFuture.thenAccept(
              object -> {
                if (player.isOnline()
                    && !(object.getLocation().distance(player.getLocation()) < 4.0)
                    && !player.isOnGround()
                    && player.getVehicle() == null) {
                  player.setVelocity(new Vector());
                  player.setAllowFlight(true);
                  player.setFlying(true);
                }
              });
        }
      }
    }
  }

  @EventHandler
  public void onSignInteract(PlayerInteractEvent playerInteractEvent) {
    if (!playerInteractEvent.isCancelled()
        && !scMap.isEmpty()
        && !playerInteractEvent.getAction().equals(Action.PHYSICAL)) {
      Player player = playerInteractEvent.getPlayer();
      if (scMap.containsKey(player.getUniqueId())) {
        playerInteractEvent.setCancelled(true);
        String[] strings = scMap.get(player.getUniqueId());
        if (!player.isSneaking()) {
          Block block = playerInteractEvent.getClickedBlock();
          if (!(block.getState() instanceof Sign)) {
            scMap.remove(player.getUniqueId());
            CMILC.info(sc.class, player, "canceled");
          } else {
            Sign sign = (Sign) block.getState();
            BlockPlaceEvent blockPlaceEvent = CMIEvent.placeSignEvent(block, sign, player);
            Bukkit.getServer().getPluginManager().callEvent(blockPlaceEvent);
            if (!blockPlaceEvent.isCancelled()) {
              if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
                String[] lines = new String[4];
                String[] lineCopy = new String[4];
                boolean flag = strings.length <= 4;
                boolean changed = false;
                String[] lineParts = new String[4];

                for (int value = 0; value < 4; value++) {
                  lineParts[value] = strings[value];
                  lines[value] = strings[value];
                }

                if (!flag) {
                  for (int lineIndex = 0; lineIndex < 4; lineIndex++) {
                    lineCopy[lineIndex] = strings[4 + lineIndex];
                  }
                }

                Util.SignSide signSide = Util.getSignSide(block, player.getLocation());
                if (signSide.equals(Util.SignSide.BACK)) {
                  changed = true;
                }

                SignChangeEvent signChangeEvent =
                    new SignChangeEvent(
                        block, player, (String[]) lines.clone(), changed ? Side.BACK : Side.FRONT);
                Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                if (signChangeEvent.isCancelled()) {
                  return;
                }

                if (!flag) {
                  signChangeEvent =
                      new SignChangeEvent(
                          block,
                          player,
                          (String[]) lineCopy.clone(),
                          changed ? Side.FRONT : Side.BACK);
                  Bukkit.getServer().getPluginManager().callEvent(signChangeEvent);
                  if (signChangeEvent.isCancelled()) {
                    return;
                  }
                }

                for (int partIndex = 0; partIndex < 4; partIndex++) {
                  sign.getSide(changed ? Side.BACK : Side.FRONT)
                      .setLine(partIndex, lines[partIndex]);
                }

                if (!flag) {
                  for (int colIndex = 0; colIndex < 4; colIndex++) {
                    sign.getSide(changed ? Side.FRONT : Side.BACK)
                        .setLine(colIndex, lineCopy[colIndex]);
                  }
                }
              } else {
                SignChangeEvent signEvent =
                    new SignChangeEvent(block, player, (String[]) strings.clone());
                Bukkit.getServer().getPluginManager().callEvent(signEvent);
                if (signEvent.isCancelled()) {
                  return;
                }

                strings = signEvent.getLines();

                for (int lineScan = 0; lineScan < 4; lineScan++) {
                  sign.setLine(lineScan, strings[lineScan]);
                }
              }

              sign.update();
              CMILC.info(sc.class, player, "updated");
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChatIgnore(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()
        && this.plugin.getChatManager().isChatIgnorePublicMessage()) {
      ArrayList<Player> arrayList = new ArrayList();
      UUID uuid = asyncPlayerChatEvent.getPlayer().getUniqueId();
      if (!PermissionsManager.CMIPerm.command_ignore_bypass.hasPermission(
          asyncPlayerChatEvent.getPlayer())) {
        for (Player player : asyncPlayerChatEvent.getRecipients()) {
          if (!uuid.equals(player.getUniqueId())) {
            CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
            if (cmiUser != null && cmiUser.isIgnoring(uuid)) {
              arrayList.add(player);
            }
          }
        }

        if (!arrayList.isEmpty()) {
          HashSet hashSet = new HashSet(asyncPlayerChatEvent.getRecipients());
          hashSet.removeAll(arrayList);
          asyncPlayerChatEvent.getRecipients().clear();
          asyncPlayerChatEvent.getRecipients().addAll(hashSet);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
    if (!asyncPlayerChatEvent.isCancelled()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(asyncPlayerChatEvent.getPlayer());
      if (cmiUser != null) {
        ArrayList<Player> arrayList = new ArrayList();
        if (cmiUser.isSilenceMode()) {
          asyncPlayerChatEvent.getRecipients().clear();
          asyncPlayerChatEvent.getRecipients().add(asyncPlayerChatEvent.getPlayer());
          asyncPlayerChatEvent.setFormat(
              CMIChatColor.RED + "[!]" + asyncPlayerChatEvent.getFormat());
        } else {
          if (!PermissionsManager.CMIPerm.command_silence_bypass.hasPermission(
              asyncPlayerChatEvent.getPlayer())) {
            for (Player player : asyncPlayerChatEvent.getRecipients()) {
              cmiUser = this.plugin.getPlayerManager().getUser(player);
              if (cmiUser != null && cmiUser.isSilenceMode()) {
                arrayList.add(player);
              }
            }
          }

          if (!arrayList.isEmpty()) {
            HashSet hashSet = new HashSet(asyncPlayerChatEvent.getRecipients());
            hashSet.removeAll(arrayList);
            asyncPlayerChatEvent.getRecipients().clear();
            asyncPlayerChatEvent.getRecipients().addAll(hashSet);
          }
        }
      }
    }
  }

  @EventHandler
  public void FoodLevelChangeEvent(FoodLevelChangeEvent foodLevelChangeEvent) {
    HumanEntity humanEntity = foodLevelChangeEvent.getEntity();
    if (humanEntity instanceof Player) {
      Player player = (Player) humanEntity;
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser != null) {
        int value = foodLevelChangeEvent.getFoodLevel() - player.getFoodLevel();
        if (value <= 0 && cmiUser.isGod()) {
          if (Version.isCurrentEqualOrLower(Version.v1_16_R3)) {
            int food = player.getFoodLevel();
            CMIScheduler.runTaskLater(this.plugin, () -> player.setFoodLevel(food), 0L);
          }

          foodLevelChangeEvent.setCancelled(true);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void CreatureSpawnEvent(CreatureSpawnEvent creatureSpawnEvent) {
    SpawnReason spawnReason = creatureSpawnEvent.getSpawnReason();
    if (CMIWorldLimits.isBlockedSpawnReason(
        creatureSpawnEvent.getEntity().getWorld().getName(), spawnReason)) {
      creatureSpawnEvent.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPotionSplashEvent(PotionSplashEvent potionSplashEvent) {
    for (LivingEntity livingEntity : potionSplashEvent.getAffectedEntities()) {
      if (livingEntity instanceof Player) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser((Player) livingEntity);
        if (cmiUser == null) {
          return;
        }

        if (cmiUser.isGod()) {
          potionSplashEvent.setIntensity(livingEntity, 0.0);
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
    Entity entity = entityDamageEvent.getEntity();
    if (entity instanceof Player) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser((Player) entity);
      if (cmiUser != null && cmiUser.isGod()) {
        Player player = cmiUser.getPlayer();
        if (util.imortaldebug) {
          CMIMessages.consoleMessage("PlayerListener onEntityDamage->EntityDamageEvent");
        }

        player.setFireTicks(0);
        player.setRemainingAir(player.getMaximumAir());
        entityDamageEvent.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onPlayerDropItemEvent(PlayerDropItemEvent playerDropItemEvent) {
    Player player = playerDropItemEvent.getPlayer();
    Long lockTime = donate.donateLock.get(player.getUniqueId());
    if (lockTime != null && lockTime + 500L >= System.currentTimeMillis()) {
      playerDropItemEvent.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onPlayerDropItemEvent(PlayerToggleSprintEvent playerToggleSprintEvent) {}

  class HungerCache {
    private int hunger = 20;
    private float saturation = 5.0F;

    public HungerCache(int value, float floatValue) {
      this.hunger = value;
      this.saturation = floatValue;
    }

    public int getHunger() {
      return this.hunger;
    }

    public void setHunger(int value) {
      this.hunger = value;
    }

    public float getSaturation() {
      return this.saturation;
    }

    public void setSaturation(float floatValue) {
      this.saturation = floatValue;
    }
  }
}
