package com.nexomc.nexo.mechanics.furniture.jukebox;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.JukeboxSong;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\f\u001a\u00020\u0005*\u00020\u0006H\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J&\u0010\r\u001a\u00020\u0005*\u00020\u000eH\u0007b\u0018\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n\u0012\b\b\u000b\u0012\u0004\b\u0007\u0010\u0002J\"\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0002J\u001a\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0002J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\u0013\u001a\u00020\u0014H\u0002¨\u0006\u001a"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/jukebox/JukeboxListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onInsertDisc", "", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "ignoreCancelled", "onEjectDisc", "onJukeboxBreak", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureBreakEvent;", "insertAndPlayDisc", "", "baseEntity", "Lorg/bukkit/entity/Entity;", "disc", "Lorg/bukkit/inventory/ItemStack;", "player", "Lorg/bukkit/entity/Player;", "ejectAndStopDisc", "songFromDisc", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
)
public final class JukeboxListener implements Listener {
   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onInsertDisc(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      net.kyori.adventure.text.Component component;
      ItemStack itemInMainHand = nexoFurnitureInteractEvent.getPlayer().getInventory().getItemInMainHand();
      ItemStack itemStack = itemInMainHand;
      if (nexoFurnitureInteractEvent.getHand() == EquipmentSlot.HAND && this.insertAndPlayDisc((Entity)nexoFurnitureInteractEvent.getBaseEntity(), itemStack, nexoFurnitureInteractEvent.getPlayer())) {
         block: {
            nexoFurnitureInteractEvent.getPlayer().swingMainHand();
            ItemLore data = (ItemLore)itemStack.getData(DataComponentTypes.LORE);
            if (data != null) {
               List lines = data.lines();
               if (lines != null) {
                  component = (Component)CollectionsKt.firstOrNull(lines);
                  if (component != null) {
                     break block;
                  }
               }
            }

            component = (Component)itemStack.getData(DataComponentTypes.CUSTOM_NAME);
         }

         if (component != null) {
            String str = AdventureUtilsKt.serialize(component);
            if (str != null) {
               String it = str;
               int value = 0;
               Component component2 = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(Message.MECHANICS_JUKEBOX_NOW_PLAYING.toString(), AdventureUtils.tagResolver("disc", it));
               Component message = component2;
               nexoFurnitureInteractEvent.getPlayer().sendActionBar(message);
            }
         }

         nexoFurnitureInteractEvent.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onEjectDisc(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      if (this.ejectAndStopDisc((Entity)nexoFurnitureInteractEvent.getBaseEntity(), nexoFurnitureInteractEvent.getPlayer())) {
         nexoFurnitureInteractEvent.getPlayer().swingMainHand();
         nexoFurnitureInteractEvent.setCancelled(true);
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onJukeboxBreak(@NotNull NexoFurnitureBreakEvent nexoFurnitureBreakEvent) {
      this.ejectAndStopDisc((Entity)nexoFurnitureBreakEvent.getBaseEntity(), nexoFurnitureBreakEvent.getPlayer());
   }

   private final boolean insertAndPlayDisc(Entity baseEntity, ItemStack disc, Player player) {
      PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
      PersistentDataContainer pdc = persistentDataContainer;
      ItemStack itemStack = disc.asQuantity(1);
      ItemStack insertedDisc = itemStack;
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(baseEntity);
      if (furnitureMechanic != null) {
         JukeboxBlock jukeboxBlock = furnitureMechanic.getJukebox();
         if (jukeboxBlock != null) {
            JukeboxBlock it = jukeboxBlock;
            int value = 0;
            jukeboxBlock = it.hasPermission(player) ? it : null;
            if (jukeboxBlock != null) {
               JukeboxBlock jukebox = jukeboxBlock;
               if (!pdc.has(JukeboxBlock.Companion.getMUSIC_DISC_KEY(), DataType.ITEM_STACK) && disc.hasData((DataComponentType)DataComponentTypes.JUKEBOX_PLAYABLE)) {
                  if (player != null && player.getGameMode() != GameMode.CREATIVE) {
                     disc.subtract();
                  }

                  pdc.set(JukeboxBlock.Companion.getMUSIC_DISC_KEY(), DataType.ITEM_STACK, insertedDisc);
                  String str = jukebox.playingSong(baseEntity);
                  if (str == null) {
                     return false;
                  }

                  String sound = str;
                  baseEntity.getWorld().playSound(baseEntity.getLocation().toCenterLocation(), sound, SoundCategory.RECORDS, jukebox.getVolume(), jukebox.getPitch());
                  return true;
               }

               return false;
            }
         }
      }

      return false;
   }

   private final boolean ejectAndStopDisc(Entity baseEntity, Player player) {
      FurnitureMechanic furnitureMechanic2 = NexoFurniture.furnitureMechanic(baseEntity);
      if (furnitureMechanic2 == null) {
         return false;
      } else {
         FurnitureMechanic furnitureMechanic = furnitureMechanic2;
         PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
         PersistentDataContainer pdc = persistentDataContainer;
         ItemStack itemStack = (ItemStack)pdc.get(JukeboxBlock.Companion.getMUSIC_DISC_KEY(), DataType.ITEM_STACK);
         if (itemStack == null) {
            return false;
         } else {
            ItemStack discItem = itemStack;
            JukeboxBlock jukeboxBlock = furnitureMechanic.getJukebox();
            if (jukeboxBlock != null) {
               JukeboxBlock it = jukeboxBlock;
               int value = 0;
               jukeboxBlock = it.hasPermission(player) ? it : null;
               if (jukeboxBlock != null) {
                  JukeboxBlock jukebox = jukeboxBlock;
                  if (pdc.has(JukeboxBlock.Companion.getMUSIC_DISC_KEY(), DataType.ITEM_STACK) && discItem.hasData((DataComponentType)DataComponentTypes.JUKEBOX_PLAYABLE)) {
                     Key songKey = this.songFromDisc(discItem);
                     if (songKey != null) {
                        Set trackedBy = baseEntity.getTrackedBy();
                        Iterable iterable = (Iterable)trackedBy;

                        for(Object element : iterable) {
                           Player p = (Player)element;
                           int value2 = 0;
                           p.stopSound(Sound.sound(songKey, Source.RECORD, jukebox.getVolume(), jukebox.getPitch()));
                        }
                     }

                     baseEntity.getWorld().dropItemNaturally(baseEntity.getLocation().toCenterLocation(), discItem);
                     pdc.remove(JukeboxBlock.Companion.getMUSIC_DISC_KEY());
                     return true;
                  }

                  return false;
               }
            }

            return false;
         }
      }
   }

   private final Key songFromDisc(ItemStack disc) {
      Key key;
      if (disc.getType().isRecord()) {
         String str = disc.getType().name().toLowerCase(Locale.ROOT);
         key = Key.key("minecraft", "music_disc." + StringsKt.substringAfter(str, "music_disc_", str));
      } else {
         JukeboxPlayable data = (JukeboxPlayable)disc.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
         if (data != null) {
            JukeboxSong jukeboxSong = data.jukeboxSong();
            if (jukeboxSong != null) {
               key = jukeboxSong.key();
               return key;
            }
         }

         key = null;
      }

      return key;
   }
}
