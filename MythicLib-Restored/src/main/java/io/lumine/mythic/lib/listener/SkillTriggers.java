package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.PlayerKillEntityEvent;
import io.lumine.mythic.lib.api.event.armorequip.ArmorEquipEvent;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.comp.profile.ProfileMode;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.damage.ProjectileAttackMetadata;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.entity.ProjectileType;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class SkillTriggers implements Listener {
   public SkillTriggers() {
      Bukkit.getScheduler().runTaskTimer(MythicLib.plugin, () -> MMOPlayerData.forEachOnline(online -> online.getPassiveSkillMap().tickTimerSkills()), 0L, 1L);
   }

   @EventHandler
   public void killEntity(PlayerKillEntityEvent event) {
      PlayerMetadata killer = (PlayerMetadata)event.getAttack().getAttacker();
      TriggerMetadata triggerMetadata = new TriggerMetadata(killer, TriggerType.KILL_ENTITY, event.getTarget(), null);
      Collection<PassiveSkill> isolatedSkills = this.getSkills(triggerMetadata, killer, event.getAttack());
      event.getData().triggerSkills(triggerMetadata, isolatedSkills);
      if (event.getTarget() instanceof Player) {
         triggerMetadata = new TriggerMetadata(killer, TriggerType.KILL_PLAYER, event.getTarget(), null);
         event.getData().triggerSkills(triggerMetadata, isolatedSkills);
      }
   }

   @NotNull
   private Collection<PassiveSkill> getSkills(@NotNull TriggerMetadata triggerMeta, @NotNull PlayerMetadata damager, @NotNull AttackMetadata attack) {
      return attack instanceof ProjectileAttackMetadata && ((ProjectileAttackMetadata)attack).getProjectileMetadata() != null
         ? ((ProjectileAttackMetadata)attack).getProjectileMetadata().getEffectiveSkills()
         : damager.getData().isolateSkills(triggerMeta);
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void attack(PlayerAttackEvent event) {
      TriggerMetadata triggerMetadata = new TriggerMetadata(event, TriggerType.ATTACK);
      Collection<PassiveSkill> skills = this.getSkills(triggerMetadata, event.getAttacker(), event.getAttack());
      event.getAttacker().getData().triggerSkills(triggerMetadata, skills);
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void attack(AttackEvent event) {
      MMOPlayerData caster;
      if (event.getEntity() instanceof Player && (caster = MMOPlayerData.online((Player)event.getEntity())) != null) {
         TriggerMetadata triggerMetadata = new TriggerMetadata(caster, TriggerType.DAMAGED, EquipmentSlot.MAIN_HAND, null, null, null, event.getAttack(), null);
         caster.triggerSkills(triggerMetadata);
         if (event.getAttack().hasAttacker()) {
            triggerMetadata = new TriggerMetadata(
               caster, TriggerType.DAMAGED_BY_ENTITY, EquipmentSlot.MAIN_HAND, null, event.getAttack().getAttacker().getEntity(), null, event.getAttack(), null
            );
            caster.triggerSkills(triggerMetadata);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void death(PlayerDeathEvent event) {
      MMOPlayerData caster;
      if ((caster = MMOPlayerData.online(event.getEntity())) != null) {
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.DEATH));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void login(PlayerJoinEvent event) {
      if (MythicLib.plugin.getProfileMode() != ProfileMode.LEGACY) {
         MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.LOGIN));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   private void shootBow(EntityShootBowEvent event) {
      MMOPlayerData caster;
      if (event.getEntity() instanceof Player && (caster = MMOPlayerData.online((Player)event.getEntity())) != null) {
         EquipmentSlot actionHand = this.getShootHand(((Player)event.getEntity()).getInventory());
         ProjectileMetadata proj = ProjectileMetadata.create(caster, actionHand, ProjectileType.ARROW, event.getProjectile());
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.SHOOT_BOW, actionHand, null, event.getProjectile(), null, null, proj.getShooter()));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void shootTrident(ProjectileLaunchEvent event) {
      MMOPlayerData caster;
      if (event.getEntity() instanceof Trident
         && event.getEntity().getShooter() instanceof Player
         && (caster = MMOPlayerData.online((Player)event.getEntity().getShooter())) != null) {
         Player shooter = (Player)event.getEntity().getShooter();
         EquipmentSlot actionHand = this.getShootHand(shooter.getInventory());
         ProjectileMetadata proj = ProjectileMetadata.create(caster, actionHand, ProjectileType.TRIDENT, event.getEntity());
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.SHOOT_TRIDENT, actionHand, null, event.getEntity(), null, null, proj.getShooter()));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void sneak(PlayerToggleSneakEvent event) {
      if (event.isSneaking()) {
         MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.SNEAK));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void placeBlock(BlockPlaceEvent event) {
      MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
      caster.triggerSkills(new TriggerMetadata(caster, TriggerType.PLACE_BLOCK, event.getBlock().getLocation().add(0.5, 0.5, 0.5)));
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void breakBlock(BlockBreakEvent event) {
      MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
      caster.triggerSkills(new TriggerMetadata(caster, TriggerType.BREAK_BLOCK, event.getBlock().getLocation().add(0.5, 0.5, 0.5)));
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void dropItem(PlayerDropItemEvent event) {
      MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
      boolean sneaking = event.getPlayer().isSneaking() && !MythicLib.plugin.getMMOConfig().ignoreShiftTriggers;
      caster.triggerSkills(new TriggerMetadata(caster, sneaking ? TriggerType.SHIFT_DROP_ITEM : TriggerType.DROP_ITEM, event.getItemDrop()));
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void teleport(PlayerTeleportEvent event) {
      MMOPlayerData caster = MMOPlayerData.online(event.getPlayer());
      if (caster != null) {
         caster.triggerSkills(new TriggerMetadata(caster, TriggerType.TELEPORT, event.getFrom(), event.getTo()));
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void swapItems(PlayerSwapHandItemsEvent event) {
      MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
      boolean sneaking = event.getPlayer().isSneaking() && !MythicLib.plugin.getMMOConfig().ignoreShiftTriggers;
      caster.triggerSkills(new TriggerMetadata(caster, sneaking ? TriggerType.SHIFT_SWAP_ITEMS : TriggerType.SWAP_ITEMS));
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void equipArmor(ArmorEquipEvent event) {
      MMOPlayerData caster = MMOPlayerData.online(event.getPlayer());
      boolean unequip = UtilityMethods.isAir(event.getNewArmorPiece());
      caster.triggerSkills(new TriggerMetadata(caster, unequip ? TriggerType.UNEQUIP_ARMOR : TriggerType.EQUIP_ARMOR));
   }

   @EventHandler(priority = EventPriority.LOW)
   public void click(PlayerInteractEvent event) {
      if (event.getAction() != Action.PHYSICAL) {
         if (!MythicLib.plugin.getMMOConfig().ignoreOffhandClickTriggers || event.getHand() != org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
            MMOPlayerData caster = MMOPlayerData.get(event.getPlayer());
            boolean left = event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK;
            boolean sneaking = event.getPlayer().isSneaking() && !MythicLib.plugin.getMMOConfig().ignoreShiftTriggers;
            TriggerType triggerType = sneaking
               ? (left ? TriggerType.SHIFT_LEFT_CLICK : TriggerType.SHIFT_RIGHT_CLICK)
               : (left ? TriggerType.LEFT_CLICK : TriggerType.RIGHT_CLICK);
            TriggerMetadata triggerMetadata = new TriggerMetadata(caster, triggerType, EquipmentSlot.fromBukkit(event.getHand()), null, null, null, null, null);
            caster.triggerSkills(triggerMetadata);
         }
      }
   }

   @NotNull
   private EquipmentSlot getShootHand(@NotNull PlayerInventory inv) {
      ItemStack main = inv.getItemInMainHand();
      return main != null && this.isShootable(main.getType()) ? EquipmentSlot.MAIN_HAND : EquipmentSlot.OFF_HAND;
   }

   private boolean isShootable(@NotNull Material material) {
      return material == Material.BOW || material == Material.CROSSBOW || material == Material.TRIDENT;
   }
}
