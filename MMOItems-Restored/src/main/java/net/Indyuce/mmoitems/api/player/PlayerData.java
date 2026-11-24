package net.Indyuce.mmoitems.api.player;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import io.lumine.mythic.lib.player.PlayerMetadata;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.util.Closeable;
import io.lumine.mythic.lib.version.VPotionEffectType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemSet;
import net.Indyuce.mmoitems.api.crafting.CraftingStatus;
import net.Indyuce.mmoitems.api.event.RefreshInventoryEvent;
import net.Indyuce.mmoitems.api.item.ItemReference;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import net.Indyuce.mmoitems.api.player.inventory.InventoryUpdateHandler;
import net.Indyuce.mmoitems.particle.api.ParticleRunnable;
import net.Indyuce.mmoitems.stat.data.AbilityData;
import net.Indyuce.mmoitems.stat.data.AbilityListData;
import net.Indyuce.mmoitems.stat.data.ParticleData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerData extends SynchronizedDataHolder implements Closeable {
   private RPGPlayer rpgPlayer;
   private final InventoryUpdateHandler inventory = new InventoryUpdateHandler(this);
   private final CraftingStatus craftingStatus = new CraftingStatus();
   private final Map<PotionEffectType, PotionEffect> permanentEffects = new HashMap<>();
   private final Set<ParticleRunnable> itemParticles = new HashSet<>();
   private ParticleRunnable overridingItemParticles = null;
   private boolean encumbered = false;
   @Nullable
   private ItemSet.SetBonuses setBonuses = null;
   private final PlayerStats stats;
   private final Set<String> permissions = new HashSet<>();

   public PlayerData(@NotNull MMOPlayerData mmoPlayerData) {
      super(MMOItems.plugin, mmoPlayerData);
      this.rpgPlayer = MMOItems.plugin.getMainRPG().getInfo(this);
      this.stats = new PlayerStats(this);
   }

   public void close() {
      this.cancelRunnables();
   }

   public boolean isOnline() {
      return this.getMMOPlayerData().isOnline();
   }

   public RPGPlayer getRPG() {
      return this.rpgPlayer;
   }

   public void cancelRunnables() {
      this.itemParticles.forEach(BukkitRunnable::cancel);
      if (this.overridingItemParticles != null) {
         this.overridingItemParticles.cancel();
      }
   }

   @Deprecated
   public boolean areHandsFull() {
      return this.isEncumbered();
   }

   public boolean isEncumbered() {
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getPlayer().getInventory().getItemInMainHand());
      NBTItem offHandItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getPlayer().getInventory().getItemInOffHand());
      boolean flag = nBTItem.getBoolean(ItemStats.TWO_HANDED.getNBTPath());
      boolean offHandTwoHanded = offHandItem.getBoolean(ItemStats.TWO_HANDED.getNBTPath());
      boolean mainHandOccupied = nBTItem.getItem() != null && nBTItem.getItem().getType() != Material.AIR && !nBTItem.getBoolean(ItemStats.HANDWORN.getNBTPath());
      boolean offHandOccupied = offHandItem.getItem() != null && offHandItem.getItem().getType() != Material.AIR && !offHandItem.getBoolean(ItemStats.HANDWORN.getNBTPath());
      return flag && offHandOccupied || mainHandOccupied && offHandTwoHanded;
   }

   public void setRPGPlayer(RPGPlayer rpgPlayer) {
      this.rpgPlayer = rpgPlayer;
   }

   public void updateInventory() {
      if (this.isOnline() && this.getMMOPlayerData().hasFullySynchronized()) {
         this.inventory.getEquipped().clear();
         this.permanentEffects.clear();
         this.cancelRunnables();
         this.getMMOPlayerData().getPassiveSkillMap().removeModifiers("MMOItemsItem");
         this.itemParticles.clear();
         this.overridingItemParticles = null;
         if (MMOItems.plugin.hasPermissions()) {
            Permission permissions = MMOItems.plugin.getVault().getPermissions();
            this.permissions.forEach(permission -> {
               if (permissions.has(this.getPlayer(), permission)) {
                  permissions.playerRemove(this.getPlayer(), permission);
               }
            });
         }

         this.permissions.clear();
         this.encumbered = this.isEncumbered();

         for (EquippedItem equippedItem : MMOItems.plugin.getInventory().getInventory(this.getPlayer())) {
            NBTItem nBT = equippedItem.getNBT();
            if (nBT.getItem() != null && nBT.getItem().getType() != Material.AIR && equippedItem.isPlacementLegal() && this.getRPG().canUse(nBT, false, false)) {
               this.inventory.getEquipped().add(equippedItem);
            }
         }

         Bukkit.getPluginManager().callEvent(new RefreshInventoryEvent(this.inventory.getEquipped(), this.getPlayer(), this));

         for (EquippedItem equippedItem : this.inventory.getEquipped()) {
            VolatileMMOItem cached = equippedItem.getCached();
            ModifierSource modifierSource = cached.getType().getModifierSource();
            EquipmentSlot slot = equippedItem.getSlot();
            if (EquipmentSlot.MAIN_HAND.isCompatible(modifierSource, slot)) {
               if (cached.hasData(ItemStats.ABILITIES)) {
                  for (AbilityData ability : ((AbilityListData)cached.getData(ItemStats.ABILITIES)).getAbilities()) {
                     this.getMMOPlayerData().getPassiveSkillMap().addModifier(new PassiveSkill("MMOItemsItem", ability, slot, modifierSource));
                  }
               }

               if (cached.hasData(ItemStats.PERM_EFFECTS)) {
                  ((PotionEffectListData)cached.getData(ItemStats.PERM_EFFECTS)).getEffects().forEach(effect -> {
                     if (this.getPermanentPotionEffectAmplifier(effect.getType()) < effect.getLevel() - 1) {
                        this.permanentEffects.put(effect.getType(), effect.toEffect());
                     }
                  });
               }

               if (cached.hasData(ItemStats.ITEM_PARTICLES)) {
                  ParticleData data = (ParticleData)cached.getData(ItemStats.ITEM_PARTICLES);
                  if (data.getType().hasPriority()) {
                     if (this.overridingItemParticles == null) {
                        this.overridingItemParticles = data.start(this);
                     }
                  } else {
                     this.itemParticles.add(data.start(this));
                  }
               }

               if (MMOItems.plugin.hasPermissions() && cached.hasData(ItemStats.GRANTED_PERMISSIONS)) {
                  Permission permissions = MMOItems.plugin.getVault().getPermissions();
                  this.permissions.addAll(((StringListData)cached.getData(ItemStats.GRANTED_PERMISSIONS)).getList());
                  this.permissions.forEach(permission -> {
                     if (!permissions.has(this.getPlayer(), permission)) {
                        permissions.playerAdd(this.getPlayer(), permission);
                     }
                  });
               }
            }
         }

         HashMap<ItemSet, Integer> map = new HashMap<>();

         for (EquippedItem equippedItem : this.inventory.getEquipped()) {
            String mMOITEMSITEMSET = equippedItem.getCached().getNBT().getString("MMOITEMS_ITEM_SET");
            ItemSet itemSet = MMOItems.plugin.getSets().get(mMOITEMSITEMSET);
            if (itemSet != null) {
               map.put(itemSet, map.getOrDefault(itemSet, 0) + 1);
            }
         }

         this.setBonuses = null;

         for (Entry<ItemSet, Integer> entry : map.entrySet()) {
            if (this.setBonuses == null) {
               this.setBonuses = ((ItemSet)entry.getKey()).getBonuses((Integer)entry.getValue());
            } else {
               this.setBonuses.merge(((ItemSet)entry.getKey()).getBonuses((Integer)entry.getValue()));
            }
         }

         if (this.setBonuses != null) {
            if (MMOItems.plugin.hasPermissions()) {
               Permission permissions = MMOItems.plugin.getVault().getPermissions();
               this.permissions.addAll(this.setBonuses.getPermissions());

               for (String permission : this.setBonuses.getPermissions()) {
                  if (!permissions.has(this.getPlayer(), permission)) {
                     permissions.playerAdd(this.getPlayer(), permission);
                  }
               }
            }

            for (AbilityData ability : this.setBonuses.getAbilities()) {
               this.getMMOPlayerData().getPassiveSkillMap().addModifier(new PassiveSkill("MMOItemsItem", ability, EquipmentSlot.OTHER, ModifierSource.OTHER));
            }

            for (ParticleData particle : this.setBonuses.getParticles()) {
               this.itemParticles.add(particle.start(this));
            }

            for (PotionEffect potionEffect : this.setBonuses.getPotionEffects()) {
               if (this.getPermanentPotionEffectAmplifier(potionEffect.getType()) < potionEffect.getAmplifier()) {
                  this.permanentEffects.put(potionEffect.getType(), potionEffect);
               }
            }
         }

         this.stats.updateStats();
         MMOItems.plugin.getRPGs().forEach(rpg -> rpg.refreshStats(this));
         this.inventory.helmet = this.getPlayer().getInventory().getHelmet();
         this.inventory.chestplate = this.getPlayer().getInventory().getChestplate();
         this.inventory.leggings = this.getPlayer().getInventory().getLeggings();
         this.inventory.boots = this.getPlayer().getInventory().getBoots();
         this.inventory.hand = this.getPlayer().getInventory().getItemInMainHand();
         this.inventory.offhand = this.getPlayer().getInventory().getItemInOffHand();
      }
   }

   public void timedRunnable() {
      this.permanentEffects.values().forEach(potionEffect -> this.getPlayer().addPotionEffect(potionEffect));
      if (this.encumbered) {
         this.getPlayer().addPotionEffect(new PotionEffect(VPotionEffectType.SLOWNESS.get(), 40, 1, true, false));
      }
   }

   public InventoryUpdateHandler getInventory() {
      return this.inventory;
   }

   public ItemSet.SetBonuses getSetBonuses() {
      return this.setBonuses;
   }

   public boolean hasSetBonuses() {
      return this.setBonuses != null;
   }

   public CraftingStatus getCrafting() {
      return this.craftingStatus;
   }

   public int getPermanentPotionEffectAmplifier(PotionEffectType potionEffectType) {
      return this.permanentEffects.containsKey(potionEffectType) ? this.permanentEffects.get(potionEffectType).getAmplifier() : -1;
   }

   public Set<String> getPermissions() {
      return this.permissions;
   }

   public Collection<PotionEffect> getPermanentPotionEffects() {
      return this.permanentEffects.values();
   }

   public PlayerStats getStats() {
      return this.stats;
   }

   @Deprecated
   public void cast(@Nullable AttackMetadata attackMetadata, @Nullable LivingEntity entity, @NotNull AbilityData abilityData) {
      PlayerMetadata playerMetadata = this.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
      abilityData.cast(new TriggerMetadata(playerMetadata, entity, attackMetadata));
   }

   public boolean isOnCooldown(PlayerData.CooldownType cooldownType) {
      return this.getMMOPlayerData().getCooldownMap().isOnCooldown(cooldownType.name());
   }

   public void applyCooldown(PlayerData.CooldownType cooldownType, double value) {
      this.getMMOPlayerData().getCooldownMap().applyCooldown(cooldownType.name(), value);
   }

   @Deprecated
   public boolean isOnCooldown(ItemReference itemReference) {
      return this.getMMOPlayerData().getCooldownMap().isOnCooldown(itemReference);
   }

   @Deprecated
   public void applyItemCooldown(ItemReference itemReference, double value) {
      this.getMMOPlayerData().getCooldownMap().applyCooldown(itemReference, value);
   }

   @Deprecated
   public double getItemCooldown(ItemReference itemReference) {
      return this.getMMOPlayerData().getCooldownMap().getInfo(itemReference).getRemaining() / 1000.0;
   }

   @NotNull
   public static PlayerData get(@NotNull MMOPlayerData mmoPlayerData) {
      return get(mmoPlayerData.getPlayer());
   }

   @NotNull
   public static PlayerData get(@NotNull OfflinePlayer offlinePlayer) {
      return get(offlinePlayer.getUniqueId());
   }

   public static boolean has(Player player) {
      return has(player.getUniqueId());
   }

   public static boolean has(UUID uuid) {
      return MMOItems.plugin.getPlayerDataManager().isLoaded(uuid);
   }

   @NotNull
   public static PlayerData get(UUID uuid) {
      return (PlayerData)MMOItems.plugin.getPlayerDataManager().get(uuid);
   }

   @Deprecated
   @NotNull
   public static Collection<PlayerData> getLoaded() {
      return MMOItems.plugin.getPlayerDataManager().getLoaded();
   }

   public enum CooldownType {
      ELEMENTAL_ATTACK,
      SPECIAL_ATTACK,
      BOUNCING_CRACK;
   }
}
