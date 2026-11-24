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

   public PlayerData(@NotNull MMOPlayerData var1) {
      super(MMOItems.plugin, var1);
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
      NBTItem var1 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getPlayer().getInventory().getItemInMainHand());
      NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(this.getPlayer().getInventory().getItemInOffHand());
      boolean var3 = var1.getBoolean(ItemStats.TWO_HANDED.getNBTPath());
      boolean var4 = var2.getBoolean(ItemStats.TWO_HANDED.getNBTPath());
      boolean var5 = var1.getItem() != null && var1.getItem().getType() != Material.AIR && !var1.getBoolean(ItemStats.HANDWORN.getNBTPath());
      boolean var6 = var2.getItem() != null && var2.getItem().getType() != Material.AIR && !var2.getBoolean(ItemStats.HANDWORN.getNBTPath());
      return var3 && var6 || var5 && var4;
   }

   public void setRPGPlayer(RPGPlayer var1) {
      this.rpgPlayer = var1;
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
            Permission var1 = MMOItems.plugin.getVault().getPermissions();
            this.permissions.forEach(var2x -> {
               if (var1.has(this.getPlayer(), var2x)) {
                  var1.playerRemove(this.getPlayer(), var2x);
               }
            });
         }

         this.permissions.clear();
         this.encumbered = this.isEncumbered();

         for (EquippedItem var2 : MMOItems.plugin.getInventory().getInventory(this.getPlayer())) {
            NBTItem var3 = var2.getNBT();
            if (var3.getItem() != null && var3.getItem().getType() != Material.AIR && var2.isPlacementLegal() && this.getRPG().canUse(var3, false, false)) {
               this.inventory.getEquipped().add(var2);
            }
         }

         Bukkit.getPluginManager().callEvent(new RefreshInventoryEvent(this.inventory.getEquipped(), this.getPlayer(), this));

         for (EquippedItem var11 : this.inventory.getEquipped()) {
            VolatileMMOItem var18 = var11.getCached();
            ModifierSource var4 = var18.getType().getModifierSource();
            EquipmentSlot var5 = var11.getSlot();
            if (EquipmentSlot.MAIN_HAND.isCompatible(var4, var5)) {
               if (var18.hasData(ItemStats.ABILITIES)) {
                  for (AbilityData var7 : ((AbilityListData)var18.getData(ItemStats.ABILITIES)).getAbilities()) {
                     this.getMMOPlayerData().getPassiveSkillMap().addModifier(new PassiveSkill("MMOItemsItem", var7, var5, var4));
                  }
               }

               if (var18.hasData(ItemStats.PERM_EFFECTS)) {
                  ((PotionEffectListData)var18.getData(ItemStats.PERM_EFFECTS)).getEffects().forEach(var1x -> {
                     if (this.getPermanentPotionEffectAmplifier(var1x.getType()) < var1x.getLevel() - 1) {
                        this.permanentEffects.put(var1x.getType(), var1x.toEffect());
                     }
                  });
               }

               if (var18.hasData(ItemStats.ITEM_PARTICLES)) {
                  ParticleData var28 = (ParticleData)var18.getData(ItemStats.ITEM_PARTICLES);
                  if (var28.getType().hasPriority()) {
                     if (this.overridingItemParticles == null) {
                        this.overridingItemParticles = var28.start(this);
                     }
                  } else {
                     this.itemParticles.add(var28.start(this));
                  }
               }

               if (MMOItems.plugin.hasPermissions() && var18.hasData(ItemStats.GRANTED_PERMISSIONS)) {
                  Permission var29 = MMOItems.plugin.getVault().getPermissions();
                  this.permissions.addAll(((StringListData)var18.getData(ItemStats.GRANTED_PERMISSIONS)).getList());
                  this.permissions.forEach(var2x -> {
                     if (!var29.has(this.getPlayer(), var2x)) {
                        var29.playerAdd(this.getPlayer(), var2x);
                     }
                  });
               }
            }
         }

         HashMap<ItemSet, Integer> var10 = new HashMap<>();

         for (EquippedItem var19 : this.inventory.getEquipped()) {
            String var25 = var19.getCached().getNBT().getString("MMOITEMS_ITEM_SET");
            ItemSet var27 = MMOItems.plugin.getSets().get(var25);
            if (var27 != null) {
               var10.put(var27, var10.getOrDefault(var27, 0) + 1);
            }
         }

         this.setBonuses = null;

         for (Entry<ItemSet, Integer> var20 : var10.entrySet()) {
            if (this.setBonuses == null) {
               this.setBonuses = ((ItemSet)var20.getKey()).getBonuses((Integer)var20.getValue());
            } else {
               this.setBonuses.merge(((ItemSet)var20.getKey()).getBonuses((Integer)var20.getValue()));
            }
         }

         if (this.setBonuses != null) {
            if (MMOItems.plugin.hasPermissions()) {
               Permission var14 = MMOItems.plugin.getVault().getPermissions();
               this.permissions.addAll(this.setBonuses.getPermissions());

               for (String var26 : this.setBonuses.getPermissions()) {
                  if (!var14.has(this.getPlayer(), var26)) {
                     var14.playerAdd(this.getPlayer(), var26);
                  }
               }
            }

            for (AbilityData var22 : this.setBonuses.getAbilities()) {
               this.getMMOPlayerData().getPassiveSkillMap().addModifier(new PassiveSkill("MMOItemsItem", var22, EquipmentSlot.OTHER, ModifierSource.OTHER));
            }

            for (ParticleData var23 : this.setBonuses.getParticles()) {
               this.itemParticles.add(var23.start(this));
            }

            for (PotionEffect var24 : this.setBonuses.getPotionEffects()) {
               if (this.getPermanentPotionEffectAmplifier(var24.getType()) < var24.getAmplifier()) {
                  this.permanentEffects.put(var24.getType(), var24);
               }
            }
         }

         this.stats.updateStats();
         MMOItems.plugin.getRPGs().forEach(var1x -> var1x.refreshStats(this));
         this.inventory.helmet = this.getPlayer().getInventory().getHelmet();
         this.inventory.chestplate = this.getPlayer().getInventory().getChestplate();
         this.inventory.leggings = this.getPlayer().getInventory().getLeggings();
         this.inventory.boots = this.getPlayer().getInventory().getBoots();
         this.inventory.hand = this.getPlayer().getInventory().getItemInMainHand();
         this.inventory.offhand = this.getPlayer().getInventory().getItemInOffHand();
      }
   }

   public void timedRunnable() {
      this.permanentEffects.values().forEach(var1 -> this.getPlayer().addPotionEffect(var1));
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

   public int getPermanentPotionEffectAmplifier(PotionEffectType var1) {
      return this.permanentEffects.containsKey(var1) ? this.permanentEffects.get(var1).getAmplifier() : -1;
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
   public void cast(@Nullable AttackMetadata var1, @Nullable LivingEntity var2, @NotNull AbilityData var3) {
      PlayerMetadata var4 = this.getMMOPlayerData().getStatMap().cache(EquipmentSlot.MAIN_HAND);
      var3.cast(new TriggerMetadata(var4, var2, var1));
   }

   public boolean isOnCooldown(PlayerData.CooldownType var1) {
      return this.getMMOPlayerData().getCooldownMap().isOnCooldown(var1.name());
   }

   public void applyCooldown(PlayerData.CooldownType var1, double var2) {
      this.getMMOPlayerData().getCooldownMap().applyCooldown(var1.name(), var2);
   }

   @Deprecated
   public boolean isOnCooldown(ItemReference var1) {
      return this.getMMOPlayerData().getCooldownMap().isOnCooldown(var1);
   }

   @Deprecated
   public void applyItemCooldown(ItemReference var1, double var2) {
      this.getMMOPlayerData().getCooldownMap().applyCooldown(var1, var2);
   }

   @Deprecated
   public double getItemCooldown(ItemReference var1) {
      return this.getMMOPlayerData().getCooldownMap().getInfo(var1).getRemaining() / 1000.0;
   }

   @NotNull
   public static PlayerData get(@NotNull MMOPlayerData var0) {
      return get(var0.getPlayer());
   }

   @NotNull
   public static PlayerData get(@NotNull OfflinePlayer var0) {
      return get(var0.getUniqueId());
   }

   public static boolean has(Player var0) {
      return has(var0.getUniqueId());
   }

   public static boolean has(UUID var0) {
      return MMOItems.plugin.getPlayerDataManager().isLoaded(var0);
   }

   @NotNull
   public static PlayerData get(UUID var0) {
      return (PlayerData)MMOItems.plugin.getPlayerDataManager().get(var0);
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
