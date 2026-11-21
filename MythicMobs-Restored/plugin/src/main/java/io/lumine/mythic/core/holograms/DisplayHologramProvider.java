package io.lumine.mythic.core.holograms;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.holograms.HologramManager;
import io.lumine.mythic.api.holograms.HologramProvider;
import io.lumine.mythic.api.holograms.IHologram;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.utils.text.Text;
import java.util.Collection;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;

public class DisplayHologramProvider implements HologramProvider {
   private final HologramManager manager;

   public DisplayHologramProvider(HologramManager manager) {
      this.manager = manager;
   }

   @Override
   public IHologram createHologram(String name, AbstractLocation location, String text) {
      Component line = parseLines(text);
      PacketTextDisplay.PacketTextDisplayBuilder options = PacketTextDisplay.create()
         .lineWidth(200)
         .scale(new AbstractVector(1, 1, 1))
         .brightnessBitshifted(-1)
         .yOffset(2.0F)
         .textComponent(line);
      return new DisplayHologram(this.manager, location, options);
   }

   @Override
   public IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractEntity entity, String text, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      Component line = parseLines(text);
      options.textComponent(line);
      return new DisplayHologram(this.manager, audience, entity, options);
   }

   @Override
   public IHologram createHologram(AbstractEntity entity, String text, PacketTextDisplay.PacketTextDisplayBuilder options) {
      Component line = parseLines(text);
      options.textComponent(line);
      return new DisplayHologram(this.manager, entity, options);
   }

   @Override
   public IHologram createHologram(AbstractLocation location, String text, PacketTextDisplay.PacketTextDisplayBuilder options) {
      Component line = parseLines(text);
      options.textComponent(line);
      return new DisplayHologram(this.manager, location, options);
   }

   @Override
   public IHologram createHologram(
      Supplier<Collection<AbstractPlayer>> audience, AbstractLocation location, String text, PacketTextDisplay.PacketTextDisplayBuilder options
   ) {
      Component line = parseLines(text);
      options.textComponent(line);
      return new DisplayHologram(this.manager, audience, location, options);
   }

   static Component parseLines(String text) {
      if (!text.contains("\\n")) {
         return Text.parse(text);
      } else {
         String[] splits = text.split("\\\\n");
         Builder builder = Component.text();

         for (int i = 0; i < splits.length; i++) {
            if (i > 0) {
               builder.appendNewline();
            }

            builder.append(Text.parse(splits[i]));
         }

         return ((net.kyori.adventure.text.ComponentBuilder)builder).build();
      }
   }

   @Override
   public void cleanup() {
   }
}
