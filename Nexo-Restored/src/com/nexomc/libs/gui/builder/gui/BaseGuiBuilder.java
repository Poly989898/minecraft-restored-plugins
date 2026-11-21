package com.nexomc.libs.gui.builder.gui;

import com.nexomc.libs.gui.components.InteractionModifier;
import com.nexomc.libs.gui.guis.BaseGui;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseGuiBuilder<G extends BaseGui, B extends BaseGuiBuilder<G, B>> {
   private Component title = null;
   private final EnumSet<InteractionModifier> interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
   private Consumer<G> consumer;

   @Contract("_ -> this")
   public @NotNull B title(@NotNull Component title) {
      this.title = title;
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableItemPlace() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableItemTake() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableItemSwap() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableItemDrop() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableOtherActions() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B disableAllInteractions() {
      this.interactionModifiers.addAll(InteractionModifier.VALUES);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableItemPlace() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableItemTake() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableItemSwap() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableItemDrop() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableOtherActions() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return (B)this;
   }

   @Contract(" -> this")
   public @NotNull B enableAllInteractions() {
      this.interactionModifiers.clear();
      return (B)this;
   }

   @Contract("_ -> this")
   public @NotNull B apply(@NotNull Consumer<G> consumer) {
      this.consumer = consumer;
      return (B)this;
   }

   @Contract(" -> new")
   public abstract @NotNull G create();

   protected @NotNull Component getTitle() {
      return this.title;
   }

   protected @Nullable Consumer<G> getConsumer() {
      return this.consumer;
   }

   protected @NotNull Set<InteractionModifier> getModifiers() {
      return this.interactionModifiers;
   }

   protected void consumeBuilder(@NotNull BaseGuiBuilder<?, ?> builder) {
      this.title = builder.title;
      this.interactionModifiers.addAll(builder.interactionModifiers);
   }
}
