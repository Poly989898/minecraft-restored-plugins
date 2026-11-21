package com.nexomc.nexo.nms.v1_21_R8.packets.transformers;

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
import kotlin.jvm.internal.Intrinsics;
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
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R8/packets/transformers/ComponentTransformer;", "Lcom/nexomc/nexo/nms/Transformer;", "Lnet/minecraft/network/chat/Component;", "player", "Lkotlin/Function0;", "Lorg/bukkit/entity/Player;", "locale", "Ljava/util/Locale;", "<init>", "(Lkotlin/jvm/functions/Function0;Ljava/util/Locale;)V", "getLocale$com_nexomc_v1_21_R8", "()Ljava/util/Locale;", "setLocale$com_nexomc_v1_21_R8", "(Ljava/util/Locale;)V", "transform", "input", "formatPlainContents", "", "checkPapiPermission", "transformKyori", "Lnet/kyori/adventure/text/Component;", "Lcom/nexomc/nexo/utils/KyoriComponent;", "Lnet/kyori/adventure/text/event/HoverEvent;", "", "Lnet/kyori/adventure/key/Key;", "Lnet/kyori/adventure/text/event/DataComponentValue;", "com.nexomc:v1_21_R8"}
)
public final class ComponentTransformer implements Transformer<Component> {
   @NotNull
   private final Function0<Player> player;
   @NotNull
   private Locale locale;

   public ComponentTransformer(@NotNull Function0<? extends Player> player, @NotNull Locale locale) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(locale, "locale");
      super();
      this.player = player;
      this.locale = locale;
   }

   @NotNull
   public final Locale getLocale$com_nexomc_v1_21_R8() {
      return this.locale;
   }

   public final void setLocale$com_nexomc_v1_21_R8(@NotNull Locale var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.locale = var1;
   }

   @NotNull
   public Component transform(@NotNull Component input) {
      Intrinsics.checkNotNullParameter(input, "input");
      return this.transform(input, true, true);
   }

   @NotNull
   public final Component transform(@NotNull Component input, boolean formatPlainContents, boolean checkPapiPermission) {
      Intrinsics.checkNotNullParameter(input, "input");
      net.kyori.adventure.text.Component transformed = this.transformKyori(input, formatPlainContents, checkPapiPermission);
      net.kyori.adventure.text.Component projected = ComponentProjections.INSTANCE.apply((Player)this.player.invoke(), transformed);
      Component var10000 = PaperAdventure.asVanilla(projected);
      Intrinsics.checkNotNullExpressionValue(var10000, "asVanilla(...)");
      return var10000;
   }

   private final net.kyori.adventure.text.Component transformKyori(Component input, boolean formatPlainContents, boolean checkPapiPermission) {
      net.kyori.adventure.text.Component var10000;
      if (input instanceof AdventureComponent) {
         var10000 = ((AdventureComponent)input).adventure$component().children(CollectionsKt.emptyList());
      } else {
         ComponentContents contents = input.getContents();
         if (contents instanceof PlainTextContents) {
            block: {
               if (formatPlainContents) {
                  AdventureUtils var64 = AdventureUtils.INSTANCE;
                  String var10001 = ((PlainTextContents)contents).text();
                  Intrinsics.checkNotNullExpressionValue(var10001, "text(...)");
                  if (!var64.containsLegacyCodes(var10001)) {
                     String var66 = ((PlainTextContents)contents).text();
                     Intrinsics.checkNotNullExpressionValue(var66, "text(...)");
                     var10000 = AdventureUtilsKt.deserialize(var66);
                     break block;
                  }
               }

               TextComponent var65 = net.kyori.adventure.text.Component.text(((PlainTextContents)contents).text());
               Intrinsics.checkNotNullExpressionValue(var65, "text(...)");
               var10000 = (net.kyori.adventure.text.Component)var65;
            }
         } else if (contents instanceof TranslatableContents) {
            net.kyori.adventure.text.Component var67 = ((TranslatableContents)contents).getArgs();
            Intrinsics.checkNotNullExpressionValue(var67, "getArgs(...)");
            Object[] $this$toTypedArray$iv = var67;
            Object[] result$iv = new net.kyori.adventure.text.Component[$this$toTypedArray$iv.length];
            int index$iv = 0;
            int var13 = 0;

            for(int var14 = $this$toTypedArray$iv.length; var13 < var14; ++var13) {
               Object item$iv = $this$toTypedArray$iv[var13];
               int var28 = index$iv++;
               int var17 = 0;
               if (item$iv instanceof AdventureComponent) {
                  var67 = ((AdventureComponent)item$iv).adventure$component();
                  Intrinsics.checkNotNullExpressionValue(var67, "adventure$component(...)");
                  var67 = GlyphHandlers.transformGlyphs(var67, this.locale);
               } else if (item$iv instanceof Component) {
                  var67 = this.transformKyori((Component)item$iv, formatPlainContents, checkPapiPermission);
               } else if (item$iv instanceof net.kyori.adventure.text.Component) {
                  var67 = GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)item$iv, this.locale);
               } else {
                  TextComponent var70 = net.kyori.adventure.text.Component.text(item$iv.toString());
                  Intrinsics.checkNotNullExpressionValue(var70, "text(...)");
                  var67 = (net.kyori.adventure.text.Component)var70;
               }

               net.kyori.adventure.text.Component var29 = var67;
               result$iv[var28] = var29;
            }

            net.kyori.adventure.text.Component[] args = (net.kyori.adventure.text.Component[])(result$iv);
            TranslatableComponent var7 = net.kyori.adventure.text.Component.translatable(((TranslatableContents)contents).getKey(), ((TranslatableContents)contents).getFallback(), PaperAdventure.asAdventure(input.getStyle()), (ComponentLike[])Arrays.copyOf(args, args.length));
            Intrinsics.checkNotNull(var7);
            var10000 = (net.kyori.adventure.text.Component)var7;
         } else {
            var10000 = PaperAdventure.asAdventure(input).children(CollectionsKt.emptyList());
            Intrinsics.checkNotNullExpressionValue(var10000, "children(...)");
         }
      }

      net.kyori.adventure.text.Component component = var10000;
      Intrinsics.checkNotNull(component);
      boolean condition$iv = !input.getStyle().isEmpty();
      if (condition$iv) {
         int var43 = 0;
         var10000 = component.applyFallbackStyle(PaperAdventure.asAdventure(input.getStyle()));
         Intrinsics.checkNotNullExpressionValue(var10000, "applyFallbackStyle(...)");
      } else {
         var10000 = component;
      }

      net.kyori.adventure.text.Component base = GlyphHandlers.transformGlyphs(var10000, this.locale);
      List var72 = input.getSiblings();
      Intrinsics.checkNotNullExpressionValue(var72, "getSiblings(...)");
      net.kyori.adventure.text.Component var75;
      if (!((Collection)var72).isEmpty()) {
         Style inputStyle = PaperAdventure.asAdventure(input.getStyle());
         List var73 = input.getSiblings();
         Intrinsics.checkNotNullExpressionValue(var73, "getSiblings(...)");
         Iterable $this$map$iv = (Iterable)var73;
         int $i$f$map = 0;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

         for(Object item$iv$iv : $this$map$iv) {
            Component it = (Component)item$iv$iv;
            int var19 = 0;
            Intrinsics.checkNotNull(it);
            net.kyori.adventure.text.Component $this$applyIf$iv = this.transformKyori(it, formatPlainContents, checkPapiPermission);
            boolean condition$iv = !input.getStyle().isEmpty();
            net.kyori.adventure.text.Component var74;
            if (condition$iv) {
               int var24 = 0;
               var74 = $this$applyIf$iv.applyFallbackStyle(inputStyle);
               Intrinsics.checkNotNullExpressionValue(var74, "applyFallbackStyle(...)");
            } else {
               var74 = $this$applyIf$iv;
            }

            destination$iv$iv.add(var74);
         }

         List siblings = (List)destination$iv$iv;
         SpreadBuilder $i$f$map = new SpreadBuilder(2);
         $i$f$map.add(base);
         Collection $this$toTypedArray$iv = (Collection)siblings;
         $i$f$map.addSpread($this$toTypedArray$iv.toArray(new net.kyori.adventure.text.Component[0]));
         TextComponent var26 = net.kyori.adventure.text.Component.textOfChildren((ComponentLike[])$i$f$map.toArray(new ComponentLike[$i$f$map.size()]));
         Intrinsics.checkNotNull(var26);
         var75 = (net.kyori.adventure.text.Component)var26;
      } else {
         var75 = base;
      }

      base = var75;
      Iterable $this$map$iv = (Iterable)base.children();
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

      for(Object item$iv$iv : $this$map$iv) {
         net.kyori.adventure.text.Component item$iv$iv = (net.kyori.adventure.text.Component)item$iv$iv;
         int var18 = 0;
         HoverEvent var81 = item$iv$iv.hoverEvent();
         destination$iv$iv.add(item$iv$iv.hoverEvent((HoverEventSource)(var81 != null ? this.transform(var81) : null)));
      }

      List children = (List)destination$iv$iv;
      HoverEvent var82 = base.hoverEvent();
      net.kyori.adventure.text.Component $this$map$iv = base.hoverEvent((HoverEventSource)(var82 != null ? this.transform(var82) : null));
      $i$f$map = !((Collection)children).isEmpty();
      if ($i$f$map) {
         var75 = $this$map$iv.children(children);
      } else {
         var75 = $this$map$iv;
      }

      net.kyori.adventure.text.Component $this$applyIf$iv = var75;
      int $this$applyIf$iv = 0;
      PapiTag var77 = PapiTag.INSTANCE;
      AdventureUtils var83 = AdventureUtils.INSTANCE;
      Intrinsics.checkNotNull($this$applyIf$iv);
      net.kyori.adventure.text.Component var79;
      if (var77.containsTag(var83.asFlatTextContent($this$applyIf$iv))) {
         $this$applyIf$iv = 0;
         PapiTag var78 = PapiTag.INSTANCE;
         Intrinsics.checkNotNull($this$applyIf$iv);
         var79 = var78.replacePlaceholders($this$applyIf$iv, (Player)this.player.invoke(), checkPapiPermission);
      } else {
         var79 = $this$applyIf$iv;
      }

      Intrinsics.checkNotNullExpressionValue(var79, "applyIf(...)");
      return var79;
   }

   private final HoverEvent<?> transform(HoverEvent<?> $this$transform) {
      Object var10000 = $this$transform.value();
      Intrinsics.checkNotNullExpressionValue(var10000, "value(...)");
      Object value = var10000;
      HoverEvent var4;
      if (value instanceof HoverEvent.ShowItem var3) {
         Map var10002 = ((HoverEvent.ShowItem)value).dataComponents();
         Intrinsics.checkNotNullExpressionValue(var10002, "dataComponents(...)");
         var4 = HoverEvent.showItem(var3.dataComponents(this.transform(var10002)));
         Intrinsics.checkNotNullExpressionValue(var4, "showItem(...)");
      } else if (value instanceof net.kyori.adventure.text.Component) {
         var4 = HoverEvent.showText(GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)value, this.locale));
         Intrinsics.checkNotNullExpressionValue(var4, "showText(...)");
      } else {
         var4 = $this$transform;
      }

      return var4;
   }

   private final Map<Key, DataComponentValue> transform(Map<Key, ? extends DataComponentValue> input) {
      Map destination$iv$iv = (Map)(new LinkedHashMap(MapsKt.mapCapacity(input.size())));
      Iterable $this$associateByTo$iv$iv$iv = (Iterable)input.entrySet();
      Map destination$iv$iv$iv = destination$iv$iv;

      for(Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
         Map.Entry it$iv$iv = (Map.Entry)element$iv$iv$iv;
         int var14 = 0;
         Object var10001 = it$iv$iv.getKey();
         Map.Entry var15 = (Map.Entry)element$iv$iv$iv;
         Object var32 = var10001;
         int var16 = 0;
         DataComponentValue componentValue = (DataComponentValue)var15.getValue();
         DataComponentValue var47;
         if (!(componentValue instanceof PaperAdventure.DataComponentValueImpl)) {
            var47 = componentValue;
         } else {
            Object value = ((PaperAdventure.DataComponentValueImpl)componentValue).value();
            PaperAdventure.DataComponentValueImpl var10000;
            if (value instanceof net.kyori.adventure.text.Component) {
               var10000 = new PaperAdventure.DataComponentValueImpl(ComponentSerialization.CODEC, PaperAdventure.asVanilla(GlyphHandlers.transformGlyphs((net.kyori.adventure.text.Component)value, this.locale)));
            } else if (value instanceof Component) {
               var10000 = new PaperAdventure.DataComponentValueImpl(ComponentSerialization.CODEC, this.transform((Component)value));
            } else if (value instanceof ItemLore) {
               Codec var46 = ItemLore.CODEC;
               List var48 = ((ItemLore)value).lines();
               Intrinsics.checkNotNullExpressionValue(var48, "lines(...)");
               Iterable $this$map$iv = (Iterable)var48;
               Codec var20 = var46;
               int $i$f$map = 0;
               Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

               for(Object item$iv$iv : $this$map$iv) {
                  Component p0 = (Component)item$iv$iv;
                  int var29 = 0;
                  destination$iv$iv.add(this.transform(p0));
               }

               var48 = (List)destination$iv$iv;
               List var10002 = ((ItemLore)value).styledLines();
               Intrinsics.checkNotNullExpressionValue(var10002, "styledLines(...)");
               $this$map$iv = (Iterable)var10002;
               List var28 = var48;
               $i$f$map = 0;
               destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));

               for(Object item$iv$iv : $this$map$iv) {
                  Component p0 = (Component)item$iv$iv;
                  int var45 = 0;
                  destination$iv$iv.add(this.transform(p0));
               }

               List var30 = (List)destination$iv$iv;
               ItemLore var36 = new ItemLore(var28, var30);
               var10000 = new PaperAdventure.DataComponentValueImpl(var20, var36);
            } else {
               var10000 = (PaperAdventure.DataComponentValueImpl)componentValue;
            }

            var47 = (DataComponentValue)var10000;
         }

         DataComponentValue var33 = var47;
         destination$iv$iv$iv.put(var32, var33);
      }

      return destination$iv$iv$iv;
   }
}
