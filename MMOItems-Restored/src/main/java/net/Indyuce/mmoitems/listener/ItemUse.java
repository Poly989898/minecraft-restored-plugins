package net.Indyuce.mmoitems.listener;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.MeleeAttackMetadata;
import io.lumine.mythic.lib.entity.ProjectileMetadata;
import io.lumine.mythic.lib.entity.ProjectileType;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.SpecialWeaponAttackEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.interaction.GemStone;
import net.Indyuce.mmoitems.api.interaction.ItemSkin;
import net.Indyuce.mmoitems.api.interaction.Tool;
import net.Indyuce.mmoitems.api.interaction.UseItem;
import net.Indyuce.mmoitems.api.interaction.projectile.ArrowParticles;
import net.Indyuce.mmoitems.api.interaction.weapon.Weapon;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUse implements Listener {
   @EventHandler
   public void rightClickEffects(PlayerInteractEvent var1) {
      if (var1.hasItem()) {
         NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItem());
         Type var3 = Type.get(var2);
         if (var3 != null) {
            Player var4 = var1.getPlayer();
            UseItem var5 = var3.toUseItem(var4, var2);
            if (!(var5 instanceof Consumable) || !((Consumable)var5).hasVanillaEating()) {
               if (!var5.checkItemRequirements()) {
                  var1.setUseItemInHand(Result.DENY);
               } else {
                  boolean var6 = var1.getAction() == Action.RIGHT_CLICK_AIR || var1.getAction() == Action.RIGHT_CLICK_BLOCK;
                  if (var6) {
                     if (var5.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(var5.getMMOItem())) {
                        double var9 = var5.getPlayerData().getMMOPlayerData().getCooldownMap().getCooldown(var5.getMMOItem());
                        Message.ITEM_ON_COOLDOWN
                           .format(ChatColor.RED, "#left#", MythicLib.plugin.getMMOConfig().decimal.format(var9), "#s#", var9 >= 2.0 ? "s" : "")
                           .send(var4);
                        var1.setUseItemInHand(Result.DENY);
                        return;
                     }

                     if (var5 instanceof Consumable) {
                        var1.setUseItemInHand(Result.DENY);
                        Consumable.ConsumableConsumeResult var7 = ((Consumable)var5).useOnPlayer(var1.getHand(), false);
                        if (var7 == Consumable.ConsumableConsumeResult.CANCEL) {
                           return;
                        }

                        if (var7 == Consumable.ConsumableConsumeResult.CONSUME) {
                           var1.getItem().setAmount(var1.getItem().getAmount() - 1);
                        }
                     }

                     var5.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(var5.getMMOItem(), var5.getNBTItem().getStat("ITEM_COOLDOWN"));
                     var5.executeCommands();
                  }

                  if (var5 instanceof Weapon) {
                     ((Weapon)var5).handleUntargetedAttack(var6, EquipmentSlot.fromBukkit(var1.getHand()));
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void meleeAttacks(PlayerAttackEvent var1) {
      if (var1.getAttack() instanceof MeleeAttackMetadata) {
         Player var2 = var1.getPlayer();
         ItemStack var3 = var2.getInventory().getItem(((MeleeAttackMetadata)var1.getAttack()).getHand().toBukkit());
         NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var3);
         Type var5 = Type.get(var4);
         if (var5 != null && var5 != Type.BLOCK) {
            if (!var5.hasMeleeAttacks()) {
               var1.setCancelled(true);
            } else {
               PlayerData var6 = PlayerData.get(var2);
               Weapon var7 = new Weapon(var6, var4);
               if (!var7.checkItemRequirements()) {
                  var1.setCancelled(true);
               } else {
                  if (!var7.handleTargetedAttack(var1.getAttack(), var1.getAttacker(), var1.getEntity())) {
                     var1.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void specialToolAbilities(BlockBreakEvent var1) {
      if (!UtilityMethods.isFake(var1)) {
         Player var2 = var1.getPlayer();
         Block var3 = var1.getBlock();
         if (var2.getGameMode() != GameMode.CREATIVE) {
            NBTItem var4 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2.getInventory().getItemInMainHand());
            if (var4.hasType()) {
               Tool var5 = new Tool(PlayerData.get(var2), var4);
               if (!var5.checkItemRequirements()) {
                  var1.setCancelled(true);
               } else {
                  if (var5.miningEffects(var3)) {
                     var1.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void rightClickWeaponInteractions(PlayerInteractEntityEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getRightClicked() instanceof LivingEntity) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var2.getInventory().getItem(var1.getHand()));
         Type var4 = Type.get(var3);
         if (var4 != null) {
            LivingEntity var5 = (LivingEntity)var1.getRightClicked();
            if (UtilityMethods.canTarget(var2, var5, InteractionType.OFFENSE_ACTION)) {
               UseItem var6 = var4.toUseItem(var2, var3);
               if (var6.checkItemRequirements()) {
                  SkillHandler var7 = var6.getMMOItem().getType().onEntityInteract();
                  if (var7 != null) {
                     SpecialWeaponAttackEvent var8 = new SpecialWeaponAttackEvent(var6.getPlayerData(), (Weapon)var6, var5);
                     Bukkit.getPluginManager().callEvent(var8);
                     if (!var8.isCancelled()) {
                        new SimpleSkill(var7).cast(new TriggerMetadata(var6.getPlayerData().getMMOPlayerData(), TriggerType.API, var5));
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void gemStonesAndItemStacks(InventoryClickEvent var1) {
      Player var2 = (Player)var1.getWhoClicked();
      if (var1.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
         NBTItem var3 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCursor());
         Type var4 = Type.get(var3);
         if (var4 != null) {
            UseItem var5 = var4.toUseItem(var2, var3);
            if (var5.checkItemRequirements()) {
               if (var5 instanceof ItemSkin) {
                  NBTItem var6 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem());
                  if (!var6.hasType()) {
                     return;
                  }

                  ItemSkin.ApplyResult var7 = ((ItemSkin)var5).applyOntoItem(var6, Type.get(var6.getType()));
                  if (var7.getType() == ItemSkin.ResultType.NONE) {
                     return;
                  }

                  var1.setCancelled(true);
                  var3.getItem().setAmount(var3.getItem().getAmount() - 1);
                  if (var7.getType() == ItemSkin.ResultType.FAILURE) {
                     return;
                  }

                  var1.setCurrentItem(var7.getResult());
               }

               if (var5 instanceof GemStone) {
                  NBTItem var8 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem());
                  if (!var8.hasType()) {
                     return;
                  }

                  GemStone.ApplyResult var9 = ((GemStone)var5).applyOntoItem(var8, Type.get(var8.getType()));
                  if (var9.getType() == GemStone.ResultType.NONE) {
                     return;
                  }

                  var1.setCancelled(true);
                  var3.getItem().setAmount(var3.getItem().getAmount() - 1);
                  if (var9.getType() == GemStone.ResultType.FAILURE) {
                     return;
                  }

                  var1.setCurrentItem(var9.getResult());
               }

               if (var5 instanceof Consumable
                  && var1.getCurrentItem() != null
                  && var1.getCurrentItem().getType() != Material.AIR
                  && ((Consumable)var5).useOnItem(var1, MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getCurrentItem()))) {
                  var1.setCancelled(true);
                  var1.getCursor().setAmount(var1.getCursor().getAmount() - 1);
               }
            }
         }
      }
   }

   @EventHandler
   public void handleCustomBows(EntityShootBowEvent var1) {
      if (var1.getProjectile() instanceof AbstractArrow && var1.getEntity() instanceof Player) {
         NBTItem var2 = NBTItem.get(var1.getBow());
         Type var3 = Type.get(var2.getType());
         if (var3 != null) {
            PlayerData var4 = PlayerData.get((Player)var1.getEntity());
            Weapon var5 = new Weapon(var4, var2);
            if (!var5.checkItemRequirements() || !var5.checkAndApplyWeaponCosts()) {
               var1.setCancelled(true);
               return;
            }

            ItemStack var6 = var4.getPlayer().getInventory().getItemInMainHand();
            EquipmentSlot var7 = var6.isSimilar(var1.getBow()) ? EquipmentSlot.MAIN_HAND : EquipmentSlot.OFF_HAND;
            ProjectileMetadata var8 = ProjectileMetadata.create(var4.getMMOPlayerData(), var7, ProjectileType.ARROW, var1.getProjectile());
            var8.setSourceItem(var2);
            var8.setCustomDamage(true);
            var8.setDamageMultiplier(MMOUtils.getForce(var1));
            if (var2.hasTag("MMOITEMS_ARROW_PARTICLES")) {
               new ArrowParticles((AbstractArrow)var1.getProjectile(), var2);
            }

            AbstractArrow var9 = (AbstractArrow)var1.getProjectile();
            double var10 = var8.getShooter().getStat("ARROW_VELOCITY");
            if (var10 > 0.0) {
               var9.setVelocity(var9.getVelocity().multiply(var10));
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleVanillaEatenConsumables(PlayerItemConsumeEvent var1) {
      NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItem());
      Type var3 = Type.get(var2);
      if (var3 != null) {
         Player var4 = var1.getPlayer();
         UseItem var5 = var3.toUseItem(var4, var2);
         if (!var5.checkItemRequirements()) {
            var1.setCancelled(true);
         } else {
            if (var5 instanceof Consumable) {
               if (var5.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(var5.getMMOItem())) {
                  double var8 = var5.getPlayerData().getMMOPlayerData().getCooldownMap().getCooldown(var5.getMMOItem());
                  Message.ITEM_ON_COOLDOWN
                     .format(ChatColor.RED, "#left#", MythicLib.plugin.getMMOConfig().decimal.format(var8), "#s#", var8 >= 2.0 ? "s" : "")
                     .send(var4);
                  var1.setCancelled(true);
                  return;
               }

               Consumable.ConsumableConsumeResult var6 = ((Consumable)var5)
                  .useOnPlayer(
                     var1.getItem().equals(var4.getInventory().getItemInMainHand())
                        ? org.bukkit.inventory.EquipmentSlot.HAND
                        : org.bukkit.inventory.EquipmentSlot.OFF_HAND,
                     true
                  );
               if (var6 == Consumable.ConsumableConsumeResult.CANCEL) {
                  var1.setCancelled(true);
                  return;
               }

               if (var6 == Consumable.ConsumableConsumeResult.NOT_CONSUME) {
                  var1.setCancelled(true);
               }

               var5.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(var5.getMMOItem(), var5.getNBTItem().getStat("ITEM_COOLDOWN"));
               var5.executeCommands();
            }
         }
      }
   }
}
