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
   public void rightClickEffects(PlayerInteractEvent event) {
      if (event.hasItem()) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getItem());
         Type type = Type.get(nBTItem);
         if (type != null) {
            Player player = event.getPlayer();
            UseItem useItem = type.toUseItem(player, nBTItem);
            if (!(useItem instanceof Consumable) || !((Consumable)useItem).hasVanillaEating()) {
               if (!useItem.checkItemRequirements()) {
                  event.setUseItemInHand(Result.DENY);
               } else {
                  boolean flag = event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK;
                  if (flag) {
                     if (useItem.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(useItem.getMMOItem())) {
                        double cooldown = useItem.getPlayerData().getMMOPlayerData().getCooldownMap().getCooldown(useItem.getMMOItem());
                        Message.ITEM_ON_COOLDOWN
                           .format(ChatColor.RED, "#left#", MythicLib.plugin.getMMOConfig().decimal.format(cooldown), "#s#", cooldown >= 2.0 ? "s" : "")
                           .send(player);
                        event.setUseItemInHand(Result.DENY);
                        return;
                     }

                     if (useItem instanceof Consumable) {
                        event.setUseItemInHand(Result.DENY);
                        Consumable.ConsumableConsumeResult consumableConsumeResult = ((Consumable)useItem).useOnPlayer(event.getHand(), false);
                        if (consumableConsumeResult == Consumable.ConsumableConsumeResult.CANCEL) {
                           return;
                        }

                        if (consumableConsumeResult == Consumable.ConsumableConsumeResult.CONSUME) {
                           event.getItem().setAmount(event.getItem().getAmount() - 1);
                        }
                     }

                     useItem.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(useItem.getMMOItem(), useItem.getNBTItem().getStat("ITEM_COOLDOWN"));
                     useItem.executeCommands();
                  }

                  if (useItem instanceof Weapon) {
                     ((Weapon)useItem).handleUntargetedAttack(flag, EquipmentSlot.fromBukkit(event.getHand()));
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void meleeAttacks(PlayerAttackEvent event) {
      if (event.getAttack() instanceof MeleeAttackMetadata) {
         Player player = event.getPlayer();
         ItemStack item = player.getInventory().getItem(((MeleeAttackMetadata)event.getAttack()).getHand().toBukkit());
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(item);
         Type type = Type.get(nBTItem);
         if (type != null && type != Type.BLOCK) {
            if (!type.hasMeleeAttacks()) {
               event.setCancelled(true);
            } else {
               PlayerData playerData = PlayerData.get(player);
               Weapon weapon = new Weapon(playerData, nBTItem);
               if (!weapon.checkItemRequirements()) {
                  event.setCancelled(true);
               } else {
                  if (!weapon.handleTargetedAttack(event.getAttack(), event.getAttacker(), event.getEntity())) {
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void specialToolAbilities(BlockBreakEvent event) {
      if (!UtilityMethods.isFake(event)) {
         Player player = event.getPlayer();
         Block block = event.getBlock();
         if (player.getGameMode() != GameMode.CREATIVE) {
            NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItemInMainHand());
            if (nBTItem.hasType()) {
               Tool tool = new Tool(PlayerData.get(player), nBTItem);
               if (!tool.checkItemRequirements()) {
                  event.setCancelled(true);
               } else {
                  if (tool.miningEffects(block)) {
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void rightClickWeaponInteractions(PlayerInteractEntityEvent event) {
      Player player = event.getPlayer();
      if (event.getRightClicked() instanceof LivingEntity) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(player.getInventory().getItem(event.getHand()));
         Type type = Type.get(nBTItem);
         if (type != null) {
            LivingEntity rightClicked = (LivingEntity)event.getRightClicked();
            if (UtilityMethods.canTarget(player, rightClicked, InteractionType.OFFENSE_ACTION)) {
               UseItem useItem = type.toUseItem(player, nBTItem);
               if (useItem.checkItemRequirements()) {
                  SkillHandler skillHandler = useItem.getMMOItem().getType().onEntityInteract();
                  if (skillHandler != null) {
                     SpecialWeaponAttackEvent attackEvent = new SpecialWeaponAttackEvent(useItem.getPlayerData(), (Weapon)useItem, rightClicked);
                     Bukkit.getPluginManager().callEvent(attackEvent);
                     if (!attackEvent.isCancelled()) {
                        new SimpleSkill(skillHandler).cast(new TriggerMetadata(useItem.getPlayerData().getMMOPlayerData(), TriggerType.API, rightClicked));
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void gemStonesAndItemStacks(InventoryClickEvent event) {
      Player whoClicked = (Player)event.getWhoClicked();
      if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
         NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCursor());
         Type type = Type.get(nBTItem);
         if (type != null) {
            UseItem useItem = type.toUseItem(whoClicked, nBTItem);
            if (useItem.checkItemRequirements()) {
               if (useItem instanceof ItemSkin) {
                  NBTItem nBTItem2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem());
                  if (!nBTItem2.hasType()) {
                     return;
                  }

                  ItemSkin.ApplyResult applyResult = ((ItemSkin)useItem).applyOntoItem(nBTItem2, Type.get(nBTItem2.getType()));
                  if (applyResult.getType() == ItemSkin.ResultType.NONE) {
                     return;
                  }

                  event.setCancelled(true);
                  nBTItem.getItem().setAmount(nBTItem.getItem().getAmount() - 1);
                  if (applyResult.getType() == ItemSkin.ResultType.FAILURE) {
                     return;
                  }

                  event.setCurrentItem(applyResult.getResult());
               }

               if (useItem instanceof GemStone) {
                  NBTItem nBTItem2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem());
                  if (!nBTItem2.hasType()) {
                     return;
                  }

                  GemStone.ApplyResult applyResult = ((GemStone)useItem).applyOntoItem(nBTItem2, Type.get(nBTItem2.getType()));
                  if (applyResult.getType() == GemStone.ResultType.NONE) {
                     return;
                  }

                  event.setCancelled(true);
                  nBTItem.getItem().setAmount(nBTItem.getItem().getAmount() - 1);
                  if (applyResult.getType() == GemStone.ResultType.FAILURE) {
                     return;
                  }

                  event.setCurrentItem(applyResult.getResult());
               }

               if (useItem instanceof Consumable
                  && event.getCurrentItem() != null
                  && event.getCurrentItem().getType() != Material.AIR
                  && ((Consumable)useItem).useOnItem(event, MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getCurrentItem()))) {
                  event.setCancelled(true);
                  event.getCursor().setAmount(event.getCursor().getAmount() - 1);
               }
            }
         }
      }
   }

   @EventHandler
   public void handleCustomBows(EntityShootBowEvent event) {
      if (event.getProjectile() instanceof AbstractArrow && event.getEntity() instanceof Player) {
         NBTItem nbtItem = NBTItem.get(event.getBow());
         Type type = Type.get(nbtItem.getType());
         if (type != null) {
            PlayerData playerData = PlayerData.get((Player)event.getEntity());
            Weapon weapon = new Weapon(playerData, nbtItem);
            if (!weapon.checkItemRequirements() || !weapon.checkAndApplyWeaponCosts()) {
               event.setCancelled(true);
               return;
            }

            ItemStack itemInMainHand = playerData.getPlayer().getInventory().getItemInMainHand();
            EquipmentSlot equipmentSlot = itemInMainHand.isSimilar(event.getBow()) ? EquipmentSlot.MAIN_HAND : EquipmentSlot.OFF_HAND;
            ProjectileMetadata projectileMetadata = ProjectileMetadata.create(playerData.getMMOPlayerData(), equipmentSlot, ProjectileType.ARROW, event.getProjectile());
            projectileMetadata.setSourceItem(nbtItem);
            projectileMetadata.setCustomDamage(true);
            projectileMetadata.setDamageMultiplier(MMOUtils.getForce(event));
            if (nbtItem.hasTag("MMOITEMS_ARROW_PARTICLES")) {
               new ArrowParticles((AbstractArrow)event.getProjectile(), nbtItem);
            }

            AbstractArrow projectile = (AbstractArrow)event.getProjectile();
            double stat = projectileMetadata.getShooter().getStat("ARROW_VELOCITY");
            if (stat > 0.0) {
               projectile.setVelocity(projectile.getVelocity().multiply(stat));
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleVanillaEatenConsumables(PlayerItemConsumeEvent event) {
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getItem());
      Type type = Type.get(nBTItem);
      if (type != null) {
         Player player = event.getPlayer();
         UseItem useItem = type.toUseItem(player, nBTItem);
         if (!useItem.checkItemRequirements()) {
            event.setCancelled(true);
         } else {
            if (useItem instanceof Consumable) {
               if (useItem.getPlayerData().getMMOPlayerData().getCooldownMap().isOnCooldown(useItem.getMMOItem())) {
                  double cooldown = useItem.getPlayerData().getMMOPlayerData().getCooldownMap().getCooldown(useItem.getMMOItem());
                  Message.ITEM_ON_COOLDOWN
                     .format(ChatColor.RED, "#left#", MythicLib.plugin.getMMOConfig().decimal.format(cooldown), "#s#", cooldown >= 2.0 ? "s" : "")
                     .send(player);
                  event.setCancelled(true);
                  return;
               }

               Consumable.ConsumableConsumeResult consumableConsumeResult = ((Consumable)useItem)
                  .useOnPlayer(
                     event.getItem().equals(player.getInventory().getItemInMainHand())
                        ? org.bukkit.inventory.EquipmentSlot.HAND
                        : org.bukkit.inventory.EquipmentSlot.OFF_HAND,
                     true
                  );
               if (consumableConsumeResult == Consumable.ConsumableConsumeResult.CANCEL) {
                  event.setCancelled(true);
                  return;
               }

               if (consumableConsumeResult == Consumable.ConsumableConsumeResult.NOT_CONSUME) {
                  event.setCancelled(true);
               }

               useItem.getPlayerData().getMMOPlayerData().getCooldownMap().applyCooldown(useItem.getMMOItem(), useItem.getNBTItem().getStat("ITEM_COOLDOWN"));
               useItem.executeCommands();
            }
         }
      }
   }
}
