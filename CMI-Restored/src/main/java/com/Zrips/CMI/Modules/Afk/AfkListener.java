package com.Zrips.CMI.Modules.Afk;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIDirection;
import com.Zrips.CMI.Containers.CMIHitBox;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CommandAlias;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.events.CMIAfkEnterEvent;
import com.Zrips.CMI.events.CMIAfkKickEvent;
import com.Zrips.CMI.events.CMIAfkLeaveEvent;
import com.Zrips.CMI.utils.CMINPCChecker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AfkListener implements Listener {

    private CMI plugin;
    HashMap<UUID, previousLoc> antiJump = new HashMap<>();
    static HashMap<UUID, checkInfo> pitchChange = new HashMap<>();
    HashMap<UUID, Long> antiPush = new HashMap<>();

    public AfkListener(CMI plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (CMINPCChecker.isNPC(player)) {
            return;
        }
        boolean wasAfk = this.plugin.getPlayerManager().getUser(player).isAfk();
        if (this.plugin.getAfkManager().isDisableOnCommand()) {
            CMIScheduler.runTask(this.plugin, () -> {
                CMIUser user = this.plugin.getPlayerManager().getUser(player);
                if (user == null) {
                    return;
                }
                if (wasAfk && user.isAfk()) {
                    user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                }
                this.plugin.getAfkManager().updateUser(user);
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void CreatureSpawnEvent(CreatureSpawnEvent event) {
        if (!this.plugin.getAfkManager().isPreventMobSpawning()) {
            return;
        }
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        boolean blockedSpawn = (reason == CreatureSpawnEvent.SpawnReason.SPAWNER && this.plugin.getAfkManager().isPreventMobSpawningSpawners())
            || ((reason == CreatureSpawnEvent.SpawnReason.NATURAL || reason == CreatureSpawnEvent.SpawnReason.EGG) && this.plugin.getAfkManager().isPreventMobSpawningNatural())
            || (reason == CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE && this.plugin.getAfkManager().isPreventMobVillageDefence());
        if (blockedSpawn && !this.plugin.getAfkManager().nearActivePlayer(event.getLocation(), reason)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAliasCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (CMINPCChecker.isNPC(player)) {
            return;
        }
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        CommandAlias alias = CMI.getInstance().getAliasManager().getAliasForCommand(command);
        if (alias == null || alias.getAlias() == null || command == null) {
            return;
        }
        boolean wasAfk = this.plugin.getPlayerManager().getUser(player).isAfk();
        if (this.plugin.getAfkManager().isDisableOnCommand()) {
            CMIScheduler.runTask(this.plugin, () -> {
                CMIUser user = this.plugin.getPlayerManager().getUser(player);
                if (user == null) {
                    return;
                }
                if (wasAfk && user.isAfk()) {
                    user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                }
                this.plugin.getAfkManager().updateUser(user);
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (from.getWorld() == to.getWorld() && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (CMINPCChecker.isNPC(player)) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (from.getWorld() == to.getWorld() && AfkListener.isSameDirection(user)) {
            return;
        }
        if (this.plugin.getAfkManager().isDisableOnMove()) {
            user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        }
        CMIScheduler.runTask(this.plugin, () -> this.plugin.getAfkManager().updateUser(user));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFishingRodUseOnPlayer(PlayerFishEvent event) {
        if (!this.plugin.getAfkManager().isPreventHook()) {
            return;
        }
        if (event.getCaught() == null) {
            return;
        }
        if (!(event.getCaught() instanceof Player)) {
            return;
        }
        Player caught = (Player) event.getCaught();
        CMIUser user = CMIUser.getUser(caught);
        if (user == null || !user.isAfk()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (event.getTo() == null) {
            return;
        }
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ() && to.getPitch() == from.getPitch()) {
            return;
        }
        if (CMINPCChecker.isNPC(player)) {
            return;
        }
        if (this.plugin.getAfkManager().isPreventJumping() && from.getBlockY() != to.getBlockY()) {
            previousLoc jumpTracker = this.antiJump.get(player.getUniqueId());
            if (jumpTracker == null) {
                jumpTracker = new previousLoc();
            }
            Location oldest = jumpTracker.addLoc(to);
            if (oldest != null) {
                if (oldest.getBlockY() == to.getBlockY()) {
                    jumpTracker.addJumpCount();
                } else {
                    jumpTracker.setJumpCount(0);
                }
                if (oldest.getBlockX() != to.getBlockX() || oldest.getBlockZ() != to.getBlockZ()) {
                    jumpTracker.setJumpCount(0);
                }
                if (jumpTracker.getJumpCount() > 10) {
                    return;
                }
            }
            this.antiJump.put(player.getUniqueId(), jumpTracker);
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (this.plugin.getAfkManager().isPreventPushing() && user.isAfk() && from.getBlockY() == to.getBlockY() && user.isCollidable()) {
            Long pushBlockedUntil = this.antiPush.get(player.getUniqueId());
            if (pushBlockedUntil != null && pushBlockedUntil > System.currentTimeMillis() && to.getPitch() == from.getPitch()) {
                this.antiPush.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
                event.setCancelled(true);
                return;
            }
            double nearestDistance = Double.MAX_VALUE;
            Entity nearest = null;
            for (Entity nearby : from.getWorld().getNearbyEntities(from, 20.0, 20.0, 20.0)) {
                double distance;
                if (nearby.getUniqueId().equals(player.getUniqueId()) || !((distance = from.distance(nearby.getLocation())) < nearestDistance)) {
                    continue;
                }
                nearestDistance = distance;
                nearest = nearby;
            }
            CMIHitBox hitBox = new CMIHitBox(player);
            if (hitBox.collides(nearest) && to.getPitch() == from.getPitch()) {
                this.antiPush.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
                event.setCancelled(true);
                return;
            }
        }
        if (AfkListener.isSameDirection(user)) {
            return;
        }
        if (user.isAfk() && user.getAfkInfo().getAfkFrom() != 0L && from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()
            && user.getAfkInfo().getAfkFrom() < System.currentTimeMillis() && user.getAfkInfo().getAfkFrom() + 2500L > System.currentTimeMillis()) {
            return;
        }
        if (user.isAfk() && !event.getPlayer().isOnGround() && !event.getPlayer().isFlying() && from.getBlockX() == to.getBlockX() && from.getY() > to.getY()
            && from.getBlockZ() == to.getBlockZ() && to.getPitch() == from.getPitch()) {
            return;
        }
        if (user.isAfk() && !this.plugin.getAfkManager().isDisableOnLookAround()) {
            CMIVectorInt3D position = new CMIVectorInt3D(event.getPlayer().getLocation());
            if (user.getAfkInfo().getAfkAt() != null && position.getX() == user.getAfkInfo().getAfkAt().getX()
                && position.getY() == user.getAfkInfo().getAfkAt().getY() && position.getZ() == user.getAfkInfo().getAfkAt().getZ()) {
                return;
            }
        }
        if (this.plugin.getAfkManager().isDisableOnMove() || this.plugin.getAfkManager().isDisableOnLookAround()) {
            user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        }
        if (event.getFrom().getYaw() == event.getTo().getYaw() && event.getFrom().getPitch() == event.getTo().getPitch()) {
            return;
        }
        this.plugin.getAfkManager().updateUser(user);
    }

    public static boolean isSameDirection(CMIUser user) {
        if (!CMI.getInstance().getAfkManager().isAntiAfkMachines()) {
            return false;
        }
        checkInfo info = pitchChange.get(user.getUniqueId());
        if (!user.isOnline() || user.getPlayer() == null) {
            return false;
        }
        Location location = user.getLocation();
        boolean created = false;
        if (info == null) {
            info = new checkInfo(location);
            pitchChange.put(user.getUniqueId(), info);
            created = true;
        }
        if (!info.timeToCheck()) {
            return true;
        }
        info.updateTime();
        if (info.isSameDirection(location)) {
            if (!created && !Version.isFolia()) {
                boolean eyeInWater = new CMIBlock(user.getPlayer().getEyeLocation().getBlock()).isWaterlogged();
                boolean feetInWater = new CMIBlock(user.getPlayer().getLocation().getBlock()).isWaterlogged();
                if (!feetInWater) {
                    feetInWater = new CMIBlock(user.getPlayer().getLocation().clone().add(0.0, 0.5, 0.0).getBlock()).isWaterlogged();
                }
                float angle = CMIDirection.getAngle(location, info.getLocation());
                if (Math.abs((double) angle - info.getPrevAngle()) > 90.0 && user.getPlayer().getVehicle() == null) {
                    if (eyeInWater || feetInWater) {
                        return true;
                    }
                    info.setPrevAngle(angle);
                    info.updateLocation(location);
                    return false;
                }
            }
            try {
                info.setPrevAngle(CMIDirection.getAngle(location, info.getLocation()));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            info.updateLocation(location);
            return true;
        }
        info.updateLocation(location);
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void PlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (!this.plugin.getAfkManager().isDisableItemPickup()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (user.isAfk()) {
            event.setCancelled(true);
            event.getItem().setPickupDelay(event.getItem().getPickupDelay() < 20 ? 20 : event.getItem().getPickupDelay());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFishingRodUse(PlayerFishEvent event) {
        if (!this.plugin.getAfkManager().isDisableOnFishing()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH && event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        this.plugin.getAfkManager().updateUser(user);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void PlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (!this.plugin.getAfkManager().isDisableOnInteract()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (AfkListener.isSameDirection(user)) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        this.plugin.getAfkManager().updateUser(user);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        try {
            if (Version.isCurrentHigher(Version.v1_8_R3) && event.getHand() != EquipmentSlot.HAND) {
                return;
            }
        } catch (Exception exception) {
            // Hand API is not available on older servers; treat the event as main hand.
        }
        if (!this.plugin.getAfkManager().isDisableOnInteract()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (this.plugin.getAfkManager().isSmartInteractCheck()) {
            Block clicked = event.getClickedBlock();
            CMIMaterial clickedMaterial = CMIMaterial.get(clicked);
            if (clickedMaterial != null) {
                switch (clickedMaterial) {
                    case JUKEBOX:
                    case LAVA:
                    case LEVER:
                    case NOTE_BLOCK:
                    case WATER:
                        return;
                    default:
                        break;
                }
                if (clickedMaterial.isButton() || clickedMaterial.isGate() || clickedMaterial.isDoor()) {
                    return;
                }
                ItemStack mainHand = user.getItemInHand();
                CMIMaterial mainHandMaterial = CMIMaterial.get(mainHand);
                if (mainHandMaterial.equals(CMIMaterial.FISHING_ROD) || mainHandMaterial.isDye()) {
                    return;
                }
                ItemStack offHand = user.getItemInOffHand();
                CMIMaterial offHandMaterial = CMIMaterial.get(offHand);
                if (offHandMaterial.equals(CMIMaterial.FISHING_ROD) || offHandMaterial.isDye()) {
                    return;
                }
            }
        }
        if (AfkListener.isSameDirection(user)) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        this.plugin.getAfkManager().updateUser(user);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void InventoryClickEvent(InventoryClickEvent event) {
        if (!this.plugin.getAfkManager().isDisableOnInventoryClick()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser((Player) event.getWhoClicked());
        if (user == null) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void PlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        if (!this.plugin.getAfkManager().isDisableOnMove()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        CMIVectorInt3D position = new CMIVectorInt3D(event.getPlayer().getLocation());
        if (user.isAfk() && user.getAfkInfo().getAfkAt() != null && position.getX() == user.getAfkInfo().getAfkAt().getX()
            && position.getY() == user.getAfkInfo().getAfkAt().getY() && position.getZ() == user.getAfkInfo().getAfkAt().getZ()) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
        this.plugin.getAfkManager().updateUser(user);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (!this.plugin.getAfkManager().isPreventDamage()) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser((Player) event.getEntity());
        if (user == null) {
            return;
        }
        if (user.isAfk()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!this.plugin.getAfkManager().isPreventMobDamage()) {
            return;
        }
        if (this.plugin.getAfkManager().getAfkPlayerCount() == 0) {
            return;
        }
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser((Player) damager);
        if (user == null) {
            return;
        }
        if (!user.isAfk()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (!this.plugin.getAfkManager().isDisableOnPublicChat()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (Version.isPaper()) {
            CMIScheduler.runTask(this.plugin, () -> {
                user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
                this.plugin.getAfkManager().updateUser(user);
            });
        } else {
            user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
            this.plugin.getAfkManager().updateUser(user);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void CMIAfkEnterEvent(CMIAfkEnterEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!PermissionsManager.CMIPerm.command_afk_auto.hasPermission((CommandSender) player) && event.getType().equals(CMIAfkEnterEvent.AfkType.auto)) {
            event.setCancelled(true);
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        this.plugin.getPlayTimeRewardManager().updateAfkAction(user);
        AfkListener.isSameDirection(user);
        if (this.plugin.getAfkManager().isStopPlayTime()) {
            this.plugin.getAfkManager().AddToAfkTimePreventer(user);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void CMIAfkKickEvent(CMIAfkKickEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (!PermissionsManager.CMIPerm.command_afk_auto.hasPermission((CommandSender) player)) {
            event.setCancelled(true);
        }
        if (PermissionsManager.CMIPerm.command_afk_kickbypass.hasPermission((CommandSender) player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void CMIAfkLeaveEvent(CMIAfkLeaveEvent event) {
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        this.plugin.getPlayTimeRewardManager().removeFromAfkAction(user);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerLoginEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CMIUser user = CMIUser.getUser(player);
        if (user == null) {
            return;
        }
        this.plugin.getPlayTimeRewardManager().removeFromAfkAction(user);
        CMIScheduler.runTaskLater(this.plugin, () -> {
            this.plugin.getAfkManager().updateUser(user);
            this.plugin.getAfkManager().setLastAction(user, System.currentTimeMillis());
        }, 60L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        CMIUser user = CMIUser.getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        this.plugin.getPlayTimeRewardManager().removeFromAfkAction(user);
        CMIScheduler.runTask(this.plugin, () -> {
            user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
            this.plugin.getAfkManager().removeUser(user);
            this.antiJump.remove(user.getUniqueId());
            pitchChange.remove(user.getUniqueId());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!this.plugin.getAfkManager().isDisableExpPickup()) {
            return;
        }
        if (event.getEntity().getType() != EntityType.EXPERIENCE_ORB) {
            return;
        }
        if (event.getTarget() == null) {
            return;
        }
        if (event.getTarget().getType() != EntityType.PLAYER) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser((Player) event.getTarget());
        if (user == null) {
            return;
        }
        if (user.isAfk()) {
            ExperienceOrb orb = (ExperienceOrb) event.getEntity();
            orb.setExperience(0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!this.plugin.getAfkManager().isDisableOnItemDrop()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (!user.isAfk()) {
            return;
        }
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (!user.isAfk()) {
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        CMIUser user = this.plugin.getPlayerManager().getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        if (!user.isAfk()) {
            return;
        }
        this.plugin.getAfkManager().updateUser(user);
        user.setAfk(false, CMIAfkEnterEvent.AfkType.auto);
    }

    private class previousLoc {

        List<Location> locs = new ArrayList<>();
        private int jumpCount = 0;

        private previousLoc() {
        }

        public Location addLoc(Location location) {
            this.locs.add(location);
            if (this.locs.size() > 2) {
                return this.locs.remove(0);
            }
            return null;
        }

        public int getJumpCount() {
            return this.jumpCount;
        }

        public void setJumpCount(int count) {
            this.jumpCount = count;
        }

        public void addJumpCount() {
            ++this.jumpCount;
        }
    }
}
