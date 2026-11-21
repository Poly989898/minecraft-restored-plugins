package io.lumine.mythic.core.holograms;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.tasks.Task;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collection;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;

public class DisplayHologram implements IHologram {
   private final HologramManager manager;
   private final PacketTextDisplay hologramEntity;
   private final AbstractEntity attached;
   private final Task renderUpdater;

   public DisplayHologram(HologramManager manager, AbstractLocation location, PacketTextDisplay.PacketTextDisplayBuilder options) {
      this(manager, () -> location.getWorld().getPlayers(), location, options);
   }

   public DisplayHologram(HologramManager manager, AbstractEntity base, PacketTextDisplay.PacketTextDisplayBuilder options) {
      this(manager, () -> ((HologramExecutor)manager).getPlugin().getVolatileCodeHandler().getEntityHandler().getTrackedPlayers(base), base, options);
   }

   public DisplayHologram(
      HologramManager manager, Supplier<Collection<AbstractPlayer>> audience, AbstractLocation location, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      this.manager = manager;
      this.hologramEntity = new PacketTextDisplay(location, options);
      this.hologramEntity.getRenderer().spawn(audience);
      this.attached = null;
      this.renderUpdater = Schedulers.async().runRepeating(task -> this.hologramEntity.getRenderer().updateRenderedPlayers(), 10L, 10L);
   }

   public DisplayHologram(
      HologramManager manager, Supplier<Collection<AbstractPlayer>> audience, AbstractEntity base, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      this.manager = manager;
      this.hologramEntity = new PacketTextDisplay(base.getLocation(), options);
      this.hologramEntity.getRenderer().spawn(audience);
      this.attached = base;
      this.manager.registerAttachedHologram(base.getBukkitEntity().getEntityId(), this.hologramEntity.getRenderer().getEntityId());
      Schedulers.async().runLater(() -> this.hologramEntity.getRenderer().mountEntity(base), 1L);
      this.renderUpdater = Schedulers.async().runRepeating(task -> this.hologramEntity.getRenderer().updateRenderedPlayers(), 10L, 10L);
   }

   @Override
   public void teleport(AbstractLocation location) {
      if (this.attached == null) {
         this.hologramEntity.teleport(location);
      }

      this.hologramEntity.update();
   }

   @Override
   public void setText(String text) {
      this.hologramEntity.setText(text);
   }

   @Override
   public void setText(String[] text, String linePrefix) {
      if (linePrefix == null) {
         this.hologramEntity.setText(text);
      } else {
         Collection<Component> collection = Lists.newArrayList();
         Builder mergedComponent = Component.text();
         int i = 0;
         int size = text.length;

         for (String line : text) {
            mergedComponent = (Builder)mergedComponent.append(Text.parse(linePrefix + line));
            if (i < size - 1) {
               mergedComponent = (Builder)mergedComponent.appendNewline();
            }

            i++;
         }

         this.hologramEntity.setTextComponent(((net.kyori.adventure.text.ComponentBuilder)mergedComponent).build());
      }
   }

   @Override
   public void close() throws Exception {
      if (this.attached != null) {
         this.manager.unregisterAttachedHologram(this.attached.getBukkitEntity().getEntityId(), this.hologramEntity.getEntityId());
      }

      this.renderUpdater.terminate();
      this.hologramEntity.destroy();
   }
}
