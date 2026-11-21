package com.nexomc.nexo.nms.v26_R2.packets.transformers;

import com.mojang.serialization.Codec;
import com.nexomc.nexo.api.ComponentProjections;
import com.nexomc.nexo.compatibilities.placeholderapi.PapiTag;
import com.nexomc.nexo.nms.GlyphHandlers;
import com.nexomc.nexo.nms.Transformer;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SpreadBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.component.ItemLore;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001f\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u0002H\u0016J \u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0011J$\u0010\u0013\u001a\u00060\u0014j\u0002`\u00152\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J\u0014\u0010\u000e\u001a\u0006\u0012\u0002\b\u00030\u0016*\u0006\u0012\u0002\b\u00030\u0016H\u0002J(\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00190\u00172\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00190\u0017H\u0002R\u0016\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r¨\u0006\u001a"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/packets/transformers/ComponentTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/network/chat/Component;", "player", "Lkotlin/Function0;", "Lorg/bukkit/entity/Player;", "locale", "Ljava/util/Locale;", "<init>", "(Lkotlin/jvm/functions/Function0;Ljava/util/Locale;)V", "getLocale$com_nexomc_v26_R2", "()Ljava/util/Locale;", "setLocale$com_nexomc_v26_R2", "(Ljava/util/Locale;)V", "transform", "input", "formatPlainContents", "", "checkPapiPermission", "transformKyori", "Lnet/kyori/adventure/text/Component;", "Lcom/nexomc/nexo/utils/KyoriComponent;", "Lnet/kyori/adventure/text/event/HoverEvent;", "", "Lnet/kyori/adventure/key/Key;", "Lnet/kyori/adventure/text/event/DataComponentValue;", "com.nexomc:v26_R2"}
)
public final class ComponentTransformer implements Transformer<Component> {
   @NotNull
   private final Function0<Player> player;
   @NotNull
   private Locale locale;

   public ComponentTransformer(@NotNull Function0<Player> player, @NotNull Locale locale) {
      super();
      this.player = player;
      this.locale = locale;
   }

   @NotNull
   public final Locale getLocale$com_nexomc_v26_R2() {
      return this.locale;
   }

   public final void setLocale$com_nexomc_v26_R2(@NotNull Locale locale2) {
      this.locale = locale2;
   }

   @NotNull
   public Component transform(@NotNull Component input) {
      return this.transform(input, true, true);
   }

   @NotNull
   public final Component transform(@NotNull Component input, boolean formatPlainContents, boolean checkPapiPermission) {
      net.kyori.adventure.text.Component transformed = this.transformKyori(input, formatPlainContents, checkPapiPermission);
      net.kyori.adventure.text.Component projected = ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), transformed);
      Component component = PaperAdventure.asVanilla(projected);
      return component;
   }

   private final net.kyori.adventure.text.Component transformKyori(Component input, boolean formatPlainContents, boolean checkPapiPermission) {
      net.kyori.adventure.text.Component component2;
      if (input instanceof AdventureComponent) {
         component2 = ((AdventureComponent)input).adventure$component().children(CollectionsKt.emptyList());
      } else {
         ComponentContents componentContents = input.getContents();
         ComponentContents contents = componentContents;
         if (contents instanceof PlainTextContents) {
            block: {
               if (formatPlainContents) {
                  AdventureUtils adventureUtils = AdventureUtils.INSTANCE;
                  String text = ((PlainTextContents)contents).text();
                  if (!adventureUtils.containsLegacyCodes(text)) {
                     String str = ((PlainTextContents)contents).text();
                     component2 = AdventureUtilsKt.deserialize(str);
                     break block;
                  }
               }

               TextComponent textComponent = net.kyori.adventure.text.Component.text(((PlainTextContents)contents).text());
               component2 = (net.kyori.adventure.text.Component)textComponent;
            }
         } else if (contents instanceof TranslatableContents) {
            Object[] objs = ((TranslatableContents)contents).getArgs();
            net.kyori.adventure.text.Component component3;
            Object[] result = new net.kyori.adventure.text.Component[objs.length];
            int index = 0;
            int value = 0;

            for(int i = objs.length; value < i; ++value) {
               Object item = objs[value];
               int value2 = index++;
               int value3 = 0;
               if (item instanceof AdventureComponent) {
                  component3 = ((AdventureComponent)item).adventure$component();
                  component3 = GlyphHandlers.transformGlyphs(component3, this.locale);
               } else if (item instanceof Component) {
                  component3 = this.transformKyori((Component)item, formatPlainContents, checkPapiPermission);
               } else if (item instanceof net.kyori.adventure.text.Component) {
                  component3 = GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)item, this.locale);
               } else {
                  TextComponent textComponent2 = net.kyori.adventure.text.Component.text(item.toString());
                  component3 = (net.kyori.adventure.text.Component)textComponent2;
               }

               net.kyori.adventure.text.Component component4 = component3;
               result[value2] = component4;
            }

            net.kyori.adventure.text.Component[] args = (net.kyori.adventure.text.Component[])(result);
            TranslatableComponent translatableComponent = net.kyori.adventure.text.Component.translatable(((TranslatableContents)contents).getKey(), ((TranslatableContents)contents).getFallback(), PaperAdventure.asAdventure(input.getStyle()), (ComponentLike[])Arrays.copyOf(args, args.length));
            component2 = (net.kyori.adventure.text.Component)translatableComponent;
         } else {
            component2 = PaperAdventure.asAdventure(input).children(CollectionsKt.emptyList());
         }
      }

      net.kyori.adventure.text.Component component = component2;
      boolean condition = !input.getStyle().isEmpty();
      if (condition) {
         int value4 = 0;
         component2 = component.applyFallbackStyle(PaperAdventure.asAdventure(input.getStyle()));
      } else {
         component2 = component;
      }

      net.kyori.adventure.text.Component base = GlyphHandlers.transformGlyphs(component2, this.locale);
      List list = input.getSiblings();
      net.kyori.adventure.text.Component component5;
      if (!((Collection)list).isEmpty()) {
         Style inputStyle = PaperAdventure.asAdventure(input.getStyle());
         List list2 = input.getSiblings();
         Iterable iterable = (Iterable)list2;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object obj : iterable) {
            Component it = (Component)obj;
            int value5 = 0;
            net.kyori.adventure.text.Component component8 = this.transformKyori(it, formatPlainContents, checkPapiPermission);
            boolean flag = !input.getStyle().isEmpty();
            net.kyori.adventure.text.Component component6;
            if (flag) {
               int value6 = 0;
               component6 = component8.applyFallbackStyle(inputStyle);
            } else {
               component6 = component8;
            }

            destination.add(component6);
         }

         List siblings = (List)destination;
         SpreadBuilder spreadBuilder = new SpreadBuilder(2);
         spreadBuilder.add(base);
         Collection collection = (Collection)siblings;
         spreadBuilder.addSpread(collection.toArray(new net.kyori.adventure.text.Component[0]));
         TextComponent textComponent3 = net.kyori.adventure.text.Component.textOfChildren((ComponentLike[])spreadBuilder.toArray(new ComponentLike[spreadBuilder.size()]));
         component5 = (net.kyori.adventure.text.Component)textComponent3;
      } else {
         component5 = base;
      }

      base = component5;
      Iterable iterable2 = (Iterable)base.children();
      Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

      for(Object obj2 : iterable2) {
         net.kyori.adventure.text.Component component9 = (net.kyori.adventure.text.Component)obj2;
         int value7 = 0;
         HoverEvent hoverEvent = component9.hoverEvent();
         collection2.add(component9.hoverEvent((HoverEventSource)(hoverEvent != null ? this.transform(hoverEvent) : null)));
      }

      List children = (List)collection2;
      HoverEvent hoverEvent2 = base.hoverEvent();
      net.kyori.adventure.text.Component component10 = base.hoverEvent((HoverEventSource)(hoverEvent2 != null ? this.transform(hoverEvent2) : null));
      boolean hasChildren = !((Collection)children).isEmpty();
      if (hasChildren) {
         component5 = component10.children(children);
      } else {
         component5 = component10;
      }

      net.kyori.adventure.text.Component component11 = component5;
      net.kyori.adventure.text.Component component12 = component11;
      PapiTag papiTag = PapiTag.INSTANCE;
      AdventureUtils adventureUtils2 = AdventureUtils.INSTANCE;
      net.kyori.adventure.text.Component component7;
      if (papiTag.containsTag(adventureUtils2.asFlatTextContent(component12))) {
         PapiTag papiTag2 = PapiTag.INSTANCE;
         component7 = papiTag2.replacePlaceholders(component12, (Player)this.player.invoke(), checkPapiPermission);
      } else {
         component7 = component12;
      }

      return component7;
   }

   private final HoverEvent<?> transform(HoverEvent<?> hoverEvent2) {
      Object obj = hoverEvent2.value();
      Object value = obj;
      HoverEvent hoverEvent;
      if (value instanceof HoverEvent.ShowItem var3) {
         Map dataComponents = ((HoverEvent.ShowItem)value).dataComponents();
         hoverEvent = HoverEvent.showItem(var3.dataComponents(this.transform(dataComponents)));
      } else if (value instanceof net.kyori.adventure.text.Component) {
         hoverEvent = HoverEvent.showText(GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)value, this.locale));
      } else {
         hoverEvent = hoverEvent2;
      }

      return hoverEvent;
   }

   private final Map<Key, DataComponentValue> transform(Map<Key, ? extends DataComponentValue> input) {
      Map destination = (Map)(new LinkedHashMap(MapsKt.mapCapacity(input.size())));
      Iterable entrySet = (Iterable)input.entrySet();
      Map map = destination;

      for(Object element : entrySet) {
         Map.Entry it = (Map.Entry)element;
         int value2 = 0;
         Object key = it.getKey();
         Map.Entry entry = (Map.Entry)element;
         Object obj = key;
         int value3 = 0;
         DataComponentValue componentValue = (DataComponentValue)entry.getValue();
         DataComponentValue dataComponentValue;
         if (!(componentValue instanceof PaperAdventure.DataComponentValueImpl)) {
            dataComponentValue = componentValue;
         } else {
            Object value = ((PaperAdventure.DataComponentValueImpl)componentValue).value();
            PaperAdventure.DataComponentValueImpl dataComponentValueImpl;
            if (value instanceof net.kyori.adventure.text.Component) {
               dataComponentValueImpl = new PaperAdventure.DataComponentValueImpl(ComponentSerialization.CODEC, PaperAdventure.asVanilla(GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)value, this.locale)));
            } else if (value instanceof Component) {
               dataComponentValueImpl = new PaperAdventure.DataComponentValueImpl(ComponentSerialization.CODEC, this.transform((Component)value));
            } else if (value instanceof ItemLore) {
               Codec codec = ItemLore.CODEC;
               List lines = ((ItemLore)value).lines();
               Iterable iterable = (Iterable)lines;
               Codec codec2 = codec;
               Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object item : iterable) {
                  Component p0 = (Component)item;
                  int value4 = 0;
                  collection.add(this.transform(p0));
               }

               lines = (List)collection;
               List styledLines = ((ItemLore)value).styledLines();
               iterable = (Iterable)styledLines;
               List list = lines;
               collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object obj2 : iterable) {
                  Component p0 = (Component)obj2;
                  int value5 = 0;
                  collection.add(this.transform(p0));
               }

               List list2 = (List)collection;
               ItemLore itemLore = new ItemLore(list, list2);
               dataComponentValueImpl = new PaperAdventure.DataComponentValueImpl(codec2, itemLore);
            } else {
               dataComponentValueImpl = (PaperAdventure.DataComponentValueImpl)componentValue;
            }

            dataComponentValue = (DataComponentValue)dataComponentValueImpl;
         }

         DataComponentValue dataComponentValue2 = dataComponentValue;
         map.put(obj, dataComponentValue2);
      }

      return map;
   }
}
