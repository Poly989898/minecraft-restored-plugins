package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractBlock;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.items.ItemSupplier;
import io.lumine.mythic.api.volatilecode.virtual.PacketFallingBlock;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.drops.droppables.ItemDrop;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import lombok.Getter;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import net.Indyuce.mmoitems.api.droptable.item.MMOItemDropItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.MushroomState;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MMOItemsSupport extends ReloadableModule<MythicBukkit> implements ItemSupplier {
   public MMOItemsSupport(MythicBukkit plugin) {
      super(plugin);
   }

   public void load(MythicBukkit plugin) {
      Events.subscribe(MythicDropLoadEvent.class).handler(event -> {
         if (event.getDropName().equalsIgnoreCase("mmoitems") || event.getDropName().equalsIgnoreCase("mmoitem")) {
            event.register(new MMOItemsSupport.MMOItemsDrop(event.getDropName(), event.getConfig()));
         }
      }).bindWith(this);
   }

   @Override
   public void unload() {
   }

   @Override
   public String getNamespace() {
      return "mmoitems";
   }

   @Override
   public ItemStack getItem(String name) {
      return this.getCustomItem(name);
   }

   @Override
   public boolean isSimilar(String name, ItemStack itemStack) {
      return false;
   }

   @Override
   public Collection<String> getAvailableItemNames() {
      return Collections.emptyList();
   }

   public boolean isMMOItem(ItemStack itemStack) {
      return this.getPlugin().getVolatileCodeHandler().getItemHandler().getNBTData(itemStack).containsKey("MMOITEMS_ITEM_ID");
   }

   public boolean checkCustomBlock(Block block, int id) {
      Optional<CustomBlock> optional = MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData());
      if (optional.isPresent()) {
         CustomBlock custom = optional.get();
         return id == custom.getId();
      } else {
         return false;
      }
   }

   public Material getCustomBlockMaterial(int id) {
      return MMOItems.plugin.getCustomBlocks().getBlock(id).getState().getType();
   }

   public MushroomState getCustomBlockState(int id) {
      return MMOItems.plugin.getCustomBlocks().getBlock(id).getState();
   }

   public AbstractBlock getMMOItemsBlock(int id) {
      return new MMOItemsSupport.MMOItemsBlock(id);
   }

   public ItemStack getCustomItem(String string) {
      try {
         String[] split = string.split("\\.");
         String typeFormat = split[0].toUpperCase().replace("-", "_");
         Type type = MMOItems.plugin.getTypes().get(typeFormat);
         String id = split[1];
         return MMOItems.plugin.getItems().getItem(type, id);
      } catch (IllegalArgumentException e) {
         MythicLogger.error("Could not load drop item: " + e.getMessage());
         return new ItemStack(Material.ZOMBIE_HORSE_SPAWN_EGG);
      }
   }

   public AbstractBlock getCustomMMOBlock(int id) {
      return new MMOItemsSupport.MMOItemsBlock(id);
   }

   @Getter
   public class MMOItemsBlock extends AbstractBlock {
      private final CustomBlock block;

      public MMOItemsBlock(int id) {
         this.block = MMOItems.plugin.getCustomBlocks().getBlock(id);
      }

      @Override
      public void set(AbstractLocation location) {
         this.set(location, true);
      }

      @Override
      public void set(AbstractLocation location, boolean physics) {
         if (this.block != null) {
            Location loc = BukkitAdapter.adapt(location);
            loc.getBlock().setBlockData(this.block.getState().getBlockData(), physics);
         }
      }

      @Override
      public boolean matches(AbstractLocation location) {
         Block block = BukkitAdapter.adapt(location).getBlock();
         return MMOItemsSupport.this.getPlugin().getCompatibility().getMMOItems().get().checkCustomBlock(block, this.block.getId());
      }

      @Override
      public PacketFallingBlock createFakeEntity(AbstractLocation location) {
         return new PacketFallingBlock(location, this.block.getState().getBlockData());
      }

      @Nullable
      @Override
      public BlockData getBlockData() {
         return this.block.getState().getBlockData();
      }

      @Override
      public boolean equals(Object o) {
         return o instanceof MMOItemsSupport.MMOItemsBlock block ? block.getBlock().equals(this.block) : false;
      }

      @Override
      public int hashCode() {
         return this.block.hashCode();
      }
   }

   @Getter
   public class MMOItemsDrop extends ItemDrop implements IItemDrop {
      private Type type;
      private String id;
      private MMOItemDropItem dropItem;

      public MMOItemsDrop(String line, MythicLineConfig config) {
         super(line, config);

         try {
            String typeFormat = config.getString("type").toUpperCase().replace("-", "_");
            Validate.isTrue(MMOItems.plugin.getTypes().has(typeFormat), "Could not find type with ID " + typeFormat, new Object[0]);
            this.type = MMOItems.plugin.getTypes().get(typeFormat);
            this.id = config.getString("id");
            Validate.notNull(this.id, "MMOItems ID cannot be null", new Object[0]);
            this.dropItem = new MMOItemDropItem(this.type, this.id, 1.0, config.getDouble("unidentified", 0.0), 1, 1);
         } catch (IllegalArgumentException e) {
            MMOItems.plugin.getLogger().log(Level.WARNING, "Could not load drop item: " + e.getMessage());
         }
      }

      @Override
      public AbstractItemStack getDrop(DropMetadata metadata, double amount) {
         if (this.dropItem == null) {
            return BukkitAdapter.adapt(Material.STONE);
         } else {
            PlayerData playerData;
            if (metadata.getTrigger() != null && metadata.getTrigger().isPlayer()) {
               playerData = PlayerData.get((Player)metadata.getTrigger().getBukkitEntity());
            } else {
               playerData = null;
            }

            ItemStack itemStack = this.dropItem.getItem(playerData, Numbers.round(amount));
            if (itemStack == null) {
               MythicLogger.errorCompatibility("MMOITEMS", "Item type " + this.type.getId() + "." + this.id + " not found");
               return BukkitAdapter.adapt(Material.STONE);
            } else {
               return BukkitAdapter.adapt(itemStack);
            }
         }
      }
   }
}
