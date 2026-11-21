package com.ticxo.modelengine.core.menu.screen;

import com.ticxo.modelengine.api.animation.BlueprintAnimation;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.animation.property.IAnimationProperty;
import com.ticxo.modelengine.api.animation.property.SimpleProperty;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.menu.AbstractScreen;
import com.ticxo.modelengine.api.menu.Widget;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.utils.MiscUtils;
import com.ticxo.modelengine.api.utils.data.ComponentUtil;
import com.ticxo.modelengine.api.utils.data.ItemUtils;
import com.ticxo.modelengine.api.utils.ticker.DualTicker;
import com.ticxo.modelengine.core.animation.handler.StateMachineHandler;
import com.ticxo.modelengine.core.listener.ChatListener;
import com.ticxo.modelengine.core.menu.widget.BorderWidget;
import com.ticxo.modelengine.core.menu.widget.CloseWidget;
import com.ticxo.modelengine.core.menu.widget.CompositeWidget;
import com.ticxo.modelengine.core.menu.widget.PaginatorWidget;
import com.ticxo.modelengine.core.menu.widget.TabWidget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class EditModelScreen extends AbstractScreen {
   private final ActiveModel model;
   private final TabWidget tab;

   public EditModelScreen(AbstractScreen rootScreen, Player viewer, ActiveModel model) {
      super(viewer, model.getBlueprint().getName(), 6);
      this.model = model;
      this.addWidget(new BorderWidget());
      this.tab = new TabWidget();
      this.tab.addTab(new EditModelScreen.SettingsTab());
      this.tab.addTab(new EditModelScreen.BoneTab());
      this.tab.addTab(new EditModelScreen.AnimationTab());
      this.addWidget(this.tab);
      this.addWidget(new CloseWidget(rootScreen));
   }

   @Override
   public void onTick() {
      if (this.tab.getSelectedTab() instanceof EditModelScreen.AnimationTab animationTab) {
         animationTab.onTick();
      }

      super.onTick();
   }

   class AnimationTab implements TabWidget.Tab {
      private final ItemStack stack = new ItemStack(Material.ARMOR_STAND);
      private final CompositeWidget composite;
      private final PaginatorWidget page;
      private final EditModelScreen.AnimationTab.PriorityButton priority;

      public AnimationTab() {
         ItemUtils.name(this.stack, Component.text("Animations", ComponentUtil.reset()));
         this.page = new PaginatorWidget();
         if (EditModelScreen.this.model.getAnimationHandler() instanceof StateMachineHandler) {
            this.priority = new EditModelScreen.AnimationTab.PriorityButton();
            this.composite = new CompositeWidget(this.page, this.priority);
         } else {
            this.priority = null;
            this.composite = null;
         }
      }

      @Override
      public ItemStack getItemStack() {
         return this.stack;
      }

      @Override
      public Widget getWidget() {
         return this.composite != null ? this.composite : this.page;
      }

      @Override
      public void onSelect() {
         this.page.clearButtons();
         ModelBlueprint blueprint = EditModelScreen.this.model.getBlueprint();
         Map<String, BlueprintAnimation> pairs = blueprint.getAnimations();

         for (Entry<String, BlueprintAnimation> entry : pairs.entrySet()) {
            this.page.addButton(new EditModelScreen.AnimationTab.AnimationButton(entry.getValue()));
         }
      }

      protected void onTick() {
         int i = 19;

         while (i <= 43) {
            switch (i % 9) {
               default:
                  if (this.page.getButton(i) instanceof EditModelScreen.AnimationTab.AnimationButton button && button.update(false)) {
                     EditModelScreen.this.markSlotDirty(i);
                  }
               case 0:
               case 8:
                  i++;
            }
         }
      }

      class AnimationButton implements PaginatorWidget.PageButton {
         private final BlueprintAnimation animation;
         private ItemStack stack;
         private IAnimationProperty trackedProperty;
         private int lastHash;

         public AnimationButton(BlueprintAnimation animation) {
            this.animation = animation;
            this.update(true);
         }

         @Override
         public ItemStack getItemStack() {
            return this.stack;
         }

         @Override
         public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            AnimationHandler handler = EditModelScreen.this.model.getAnimationHandler();
            SimpleProperty prop = new SimpleProperty(EditModelScreen.this.model, this.animation);
            if (prop.getLoopMode() == BlueprintAnimation.LoopMode.LOOP) {
               prop.setSkipLastFrame(true);
            }

            if (handler instanceof StateMachineHandler stateMachineHandler) {
               if (stateMachineHandler.isPlayingAnimation(AnimationTab.this.priority.priority, this.animation.getName())) {
                  stateMachineHandler.stopAnimation(AnimationTab.this.priority.priority, this.animation.getName());
               } else {
                  stateMachineHandler.playAnimation(AnimationTab.this.priority.priority, prop, true);
               }
            } else if (handler.isPlayingAnimation(this.animation.getName())) {
               handler.stopAnimation(this.animation.getName());
            } else {
               handler.playAnimation(prop, true);
            }
         }

         public boolean update(boolean force) {
            AnimationHandler handler = EditModelScreen.this.model.getAnimationHandler();
            IAnimationProperty property = handler instanceof StateMachineHandler stateMachineHandler
               ? stateMachineHandler.getAnimation(AnimationTab.this.priority.priority, this.animation.getName())
               : handler.getAnimation(this.animation.getName());
            if (property == null) {
               if (!force && this.trackedProperty == null) {
                  return false;
               }

               this.trackedProperty = null;
               this.stack = new ItemStack(Material.RED_CONCRETE);
               ItemUtils.name(this.stack, Component.text(this.animation.getName(), ComponentUtil.color(NamedTextColor.RED)));
               ItemUtils.lore(
                  this.stack,
                  Component.empty(),
                  Component.text("Animation:", ComponentUtil.reset()),
                  Component.text("- Animation ID: " + this.animation.getName(), ComponentUtil.reset()),
                  Component.text("- Loop Mode: " + this.animation.getLoopMode(), ComponentUtil.reset()),
                  Component.text("- Override: " + this.animation.isOverride(), ComponentUtil.reset()),
                  Component.text("- Length: " + MiscUtils.FORMATTER.format(this.animation.getLength()) + "s", ComponentUtil.reset())
               );
            } else {
               if (!force && this.trackedProperty != null) {
                  int hash = this.trackedProperty.hashCode();
                  if (hash == this.lastHash) {
                     return false;
                  }

                  this.lastHash = hash;
               }

               this.trackedProperty = property;
               this.stack = new ItemStack(Material.LIME_CONCRETE);
               ItemUtils.name(this.stack, Component.text(this.animation.getName(), ComponentUtil.color(NamedTextColor.GREEN)));
               ItemUtils.lore(
                  this.stack,
                  Component.empty(),
                  Component.text("Animation:", ComponentUtil.reset()),
                  Component.text("- Animation ID: " + this.animation.getName(), ComponentUtil.reset()),
                  Component.text("- Loop Mode: " + this.animation.getLoopMode(), ComponentUtil.reset()),
                  Component.text("- Override: " + this.animation.isOverride(), ComponentUtil.reset()),
                  Component.text("- Length: " + MiscUtils.FORMATTER.format(this.animation.getLength()) + "s", ComponentUtil.reset()),
                  Component.empty(),
                  Component.text("Property:", ComponentUtil.reset()),
                  Component.text(
                     "- Phase: "
                        + property.getPhase()
                        + (property.getPhase() == IAnimationProperty.Phase.PLAY ? " (" + MiscUtils.FORMATTER.format(property.getTime()) + "s)" : ""),
                     ComponentUtil.reset()
                  ),
                  Component.text("- Forced Loop Mode: " + property.getForceLoopMode(), ComponentUtil.reset()),
                  Component.text("- Forced Override: " + property.getForceOverride(), ComponentUtil.reset()),
                  Component.text("- Speed: " + MiscUtils.FORMATTER.format(property.getSpeed()) + "x", ComponentUtil.reset()),
                  Component.text("- Lerp-In: " + MiscUtils.FORMATTER.format(property.getLerpIn()) + "s", ComponentUtil.reset()),
                  Component.text("- Lerp-Out: " + MiscUtils.FORMATTER.format(property.getLerpOut()) + "s", ComponentUtil.reset())
               );
            }

            return true;
         }
      }

      class PriorityButton implements Widget {
         private static final int SLOT = 51;
         private final ItemStack stack;
         private int priority = 0;

         public PriorityButton() {
            this.stack = new ItemStack(Material.COMPARATOR);
            ItemUtils.name(this.stack, Component.text("Priority: " + this.priority, ComponentUtil.reset()));
            ItemUtils.lore(
               this.stack,
               ((Component.empty().style(ComponentUtil.reset()).append(Component.text("<<< Left Click")))
                     .append(Component.text(" | ")))
                  .append(Component.text("Right Click >>>"))
            );
         }

         @Override
         public ItemStack getItemForSlot(int size, int slot) {
            return 51 == slot ? this.stack : null;
         }

         @Override
         public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
            if (slot == 51) {
               if (event.getClick().isLeftClick()) {
                  this.priority--;
               } else if (event.getClick().isRightClick()) {
                  this.priority++;
               }

               AnimationTab.this.page.refreshPage(screen);
               ItemUtils.name(this.stack, Component.text("Priority: " + this.priority, ComponentUtil.reset()));
               screen.markSlotsDirty(51);
            }
         }
      }
   }

   static class BoneButton implements PaginatorWidget.PageButton {
      private final ModelBone bone;
      private ItemStack stack;

      public BoneButton(ModelBone bone) {
         this.bone = bone;
         this.updateItem();
      }

      @Override
      public ItemStack getItemStack() {
         return this.stack;
      }

      @Override
      public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
         this.bone.setVisible(!this.bone.isVisible());
         this.updateItem();
         screen.markSlotDirty(slot);
      }

      private void updateItem() {
         BlueprintBone blueprintBone = this.bone.getBlueprintBone();
         if (blueprintBone.isRenderer()) {
            Collection<ItemStack> items = blueprintBone.getModelData().createItemStack();
            if (!items.isEmpty()) {
               this.stack = items.iterator().next();
               this.stack.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_DYE});
            }
         }

         if (this.stack == null) {
            this.stack = new ItemStack(Material.BONE);
         }

         if (this.bone.isEnchanted()) {
            this.stack.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
            this.stack.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         ItemUtils.name(this.stack, Component.text(this.bone.getUniqueBoneId(), ComponentUtil.reset()));
         ArrayList<Component> lore = new ArrayList<>();
         lore.add(Component.empty());
         lore.add(Component.text("Bone ID: " + this.bone.getBoneId(), ComponentUtil.reset()));
         if (this.bone.getParent() != null) {
            lore.add(Component.text("Parent: " + this.bone.getParent().getBoneId(), ComponentUtil.reset()));
         }

         lore.add(Component.text("Visible: " + this.bone.isVisible(), ComponentUtil.reset()));
         int defColor = this.bone.getDefaultTint().asRGB();
         int dmgColor = this.bone.getDamageTint().asRGB();
         lore.add(
            Component.text("Default Color: ", ComponentUtil.reset())
               .append(Component.text("#" + Integer.toString(defColor, 16).toUpperCase(Locale.ENGLISH), Style.style(TextColor.color(defColor))))
         );
         lore.add(
            Component.text("Damage Color: ", ComponentUtil.reset())
               .append(Component.text("#" + Integer.toString(dmgColor, 16).toUpperCase(Locale.ENGLISH), Style.style(TextColor.color(dmgColor))))
         );
         lore.add(Component.text("Enchanted: " + this.bone.isEnchanted(), ComponentUtil.reset()));
         boolean hasBehavior = false;

         for (Entry<BoneBehaviorType<?>, BoneBehavior> behavior : this.bone.getImmutableBoneBehaviors().entrySet()) {
            if (!behavior.getValue().isHidden()) {
               if (!hasBehavior) {
                  hasBehavior = true;
                  lore.add(Component.text("Behavior: ", ComponentUtil.reset()));
               }

               lore.add(Component.text("- " + behavior.getKey().getId(), ComponentUtil.reset()));
            }
         }

         ItemUtils.lore(this.stack, lore);
      }
   }

   class BoneTab implements TabWidget.Tab {
      private final ItemStack stack = new ItemStack(Material.BONE);
      private final PaginatorWidget page;

      public BoneTab() {
         ItemUtils.name(this.stack, Component.text("Bones", ComponentUtil.reset()));
         this.page = new PaginatorWidget();
      }

      @Override
      public ItemStack getItemStack() {
         return this.stack;
      }

      @Override
      public Widget getWidget() {
         return this.page;
      }

      @Override
      public void onSelect() {
         this.page.clearButtons();

         for (ModelBone bone : EditModelScreen.this.model.getBones().values()) {
            this.page.addButton(new EditModelScreen.BoneButton(bone));
         }
      }
   }

   class SettingsTab implements TabWidget.Tab {
      private final ItemStack stack = new ItemStack(Material.COMMAND_BLOCK);
      private final EditModelScreen.SettingsWidget widget;

      public SettingsTab() {
         ItemUtils.name(this.stack, Component.text("Model Settings", ComponentUtil.reset()));
         this.widget = EditModelScreen.this.new SettingsWidget();
      }

      @Override
      public ItemStack getItemStack() {
         return this.stack;
      }

      @Override
      public Widget getWidget() {
         return this.widget;
      }

      @Override
      public void onSelect() {
      }
   }

   class SettingsWidget implements Widget {
      private static final int RENDER_SCALE_SLOT = 21;
      private static final int HITBOX_SCALE_SLOT = 22;
      private static final int HITBOX_VISIBLE_SLOT = 23;
      private static final int SHADOW_VISIBLE_SLOT = 30;
      private static final int DEFAULT_TINT_SLOT = 31;
      private static final int DAMAGE_TINT_SLOT = 32;
      private static final int DAMAGE_TINT_VISIBLE_SLOT = 39;
      private static final int PITCH_LOCK_SLOT = 40;
      private static final int YAW_LOCK_SLOT = 41;
      private final ItemStack renderScale = new ItemStack(Material.SLIME_BALL);
      private final ItemStack hitboxScale = new ItemStack(Material.MAGMA_CREAM);
      private final ItemStack hitboxVisible = new ItemStack(Material.SHULKER_SHELL);
      private final ItemStack shadowVisible = new ItemStack(Material.BLACK_DYE);
      private final ItemStack defaultTint = new ItemStack(Material.WHITE_DYE);
      private final ItemStack damageTint = new ItemStack(Material.RED_DYE);
      private final ItemStack damageTintVisible = new ItemStack(Material.DIAMOND_SWORD);
      private final ItemStack pitchLock;
      private final ItemStack yawLock;

      public SettingsWidget() {
         ItemUtils.meta(this.damageTintVisible, meta -> meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES}));
         this.pitchLock = new ItemStack(Material.CLOCK);
         this.yawLock = new ItemStack(Material.COMPASS);
         this.update();
      }

      @Override
      public ItemStack getItemForSlot(int size, int slot) {
         return switch (slot) {
            case 21 -> this.renderScale;
            case 22 -> this.hitboxScale;
            case 23 -> this.hitboxVisible;
            default -> null;
            case 30 -> this.shadowVisible;
            case 31 -> this.defaultTint;
            case 32 -> this.damageTint;
            case 39 -> this.damageTintVisible;
            case 40 -> this.pitchLock;
            case 41 -> this.yawLock;
         };
      }

      @Override
      public void onClick(AbstractScreen screen, Player player, int slot, InventoryClickEvent event) {
         switch (slot) {
            case 21:
               player.closeInventory();
               ComponentUtil.sendMessage(player, Component.text("[ModelEngine] Enter render scale:", ComponentUtil.color(NamedTextColor.AQUA)));
               ChatListener.fetch(
                  player,
                  s -> {
                     try {
                        EditModelScreen.this.model.setScale(Double.parseDouble(s));
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] Set render scale of model to " + s + ".", ComponentUtil.color(NamedTextColor.GREEN))
                        );
                        this.update(slot);
                        DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                        return true;
                     } catch (NumberFormatException e) {
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] \"" + s + "\" is not a number. Try again.", ComponentUtil.color(NamedTextColor.RED))
                        );
                        return false;
                     }
                  }
               );
               break;
            case 22:
               player.closeInventory();
               ComponentUtil.sendMessage(player, Component.text("[ModelEngine] Enter hitbox scale:", ComponentUtil.color(NamedTextColor.AQUA)));
               ChatListener.fetch(
                  player,
                  s -> {
                     try {
                        EditModelScreen.this.model.setHitboxScale(Double.parseDouble(s));
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] Set hitbox scale of model to " + s + ".", ComponentUtil.color(NamedTextColor.GREEN))
                        );
                        this.update(slot);
                        DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                        return true;
                     } catch (NumberFormatException ex) {
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] \"" + s + "\" is not a number. Try again.", ComponentUtil.color(NamedTextColor.RED))
                        );
                        return false;
                     }
                  }
               );
               break;
            case 23:
               EditModelScreen.this.model.setHitboxVisible(!EditModelScreen.this.model.isHitboxVisible());
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            default:
               break;
            case 30:
               EditModelScreen.this.model.setShadowVisible(!EditModelScreen.this.model.isShadowVisible());
               break;
            case 31:
               player.closeInventory();
               ComponentUtil.sendMessage(player, Component.text("[ModelEngine] Enter default color in hex:", ComponentUtil.color(NamedTextColor.AQUA)));
               ChatListener.fetch(
                  player,
                  s -> {
                     try {
                        if (s.startsWith("#")) {
                           s = s.substring(1);
                        }

                        int color = Integer.parseInt(s, 16);
                        EditModelScreen.this.model.setDefaultTint(Color.fromRGB(color));
                        ComponentUtil.sendMessage(
                           player,
                           (Component.text("[ModelEngine] Set default color of model to ", ComponentUtil.color(NamedTextColor.GREEN))
                                 .append(Component.text("#" + s.toUpperCase(Locale.ENGLISH), ComponentUtil.color(TextColor.color(color)))))
                              .append(Component.text("."))
                        );
                        this.update(slot);
                        DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                        return true;
                     } catch (NumberFormatException ex2) {
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] \"" + s + "\" is not a valid color. Try again.", ComponentUtil.color(NamedTextColor.RED))
                        );
                        return false;
                     }
                  }
               );
               break;
            case 32:
               player.closeInventory();
               ComponentUtil.sendMessage(player, Component.text("[ModelEngine] Enter damage color in hex:", ComponentUtil.color(NamedTextColor.AQUA)));
               ChatListener.fetch(
                  player,
                  s -> {
                     try {
                        if (s.startsWith("#")) {
                           s = s.substring(1);
                        }

                        int color = Integer.parseInt(s, 16);
                        EditModelScreen.this.model.setDamageTint(Color.fromRGB(color));
                        ComponentUtil.sendMessage(
                           player,
                           (Component.text("[ModelEngine] Set damage color of model to ", ComponentUtil.color(NamedTextColor.GREEN))
                                 .append(Component.text("#" + s.toUpperCase(Locale.ENGLISH), ComponentUtil.color(TextColor.color(color)))))
                              .append(Component.text("."))
                        );
                        this.update(slot);
                        DualTicker.queueSyncTask(EditModelScreen.this::openScreen);
                        return true;
                     } catch (NumberFormatException ex3) {
                        ComponentUtil.sendMessage(
                           player, Component.text("[ModelEngine] \"" + s + "\" is not a valid color. Try again.", ComponentUtil.color(NamedTextColor.RED))
                        );
                        return false;
                     }
                  }
               );
               break;
            case 39:
               EditModelScreen.this.model.setCanHurt(!EditModelScreen.this.model.canHurt());
               break;
            case 40:
               EditModelScreen.this.model.setLockPitch(!EditModelScreen.this.model.isLockPitch());
               break;
            case 41:
               EditModelScreen.this.model.setLockYaw(!EditModelScreen.this.model.isLockYaw());
         }

         this.update(slot);
         EditModelScreen.this.markSlotDirty(slot);
      }

      public void update() {
         this.update(21);
         this.update(22);
         this.update(23);
         this.update(30);
         this.update(31);
         this.update(32);
         this.update(39);
         this.update(40);
         this.update(41);
      }

      public void update(int slot) {
         switch (slot) {
            case 21:
               ItemUtils.name(
                  this.renderScale,
                  Component.text("Render Scale: " + MiscUtils.FORMATTER.format(EditModelScreen.this.model.getScale().x()), ComponentUtil.reset())
               );
               break;
            case 22:
               ItemUtils.name(
                  this.hitboxScale,
                  Component.text("Hitbox Scale: " + MiscUtils.FORMATTER.format(EditModelScreen.this.model.getHitboxScale().x()), ComponentUtil.reset())
               );
               break;
            case 23:
               ItemUtils.name(
                  this.hitboxVisible,
                  Component.text(
                     "Hitbox Visible: " + EditModelScreen.this.model.isHitboxVisible(),
                     ComponentUtil.color(EditModelScreen.this.model.isHitboxVisible() ? NamedTextColor.GREEN : NamedTextColor.RED)
                  )
               );
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            default:
               break;
            case 30:
               ItemUtils.name(
                  this.shadowVisible,
                  Component.text(
                     "Shadow Visible: " + EditModelScreen.this.model.isShadowVisible(),
                     ComponentUtil.color(EditModelScreen.this.model.isShadowVisible() ? NamedTextColor.GREEN : NamedTextColor.RED)
                  )
               );
               break;
            case 31:
               ItemUtils.name(
                  this.defaultTint,
                  Component.text(
                     "Default Tint: #" + Integer.toHexString(EditModelScreen.this.model.getDefaultTint().asARGB()).toUpperCase(Locale.ENGLISH).substring(2),
                     ComponentUtil.color(TextColor.color(EditModelScreen.this.model.getDefaultTint().asRGB()))
                  )
               );
               break;
            case 32:
               ItemUtils.name(
                  this.damageTint,
                  Component.text(
                     "Damage Tint: #" + Integer.toHexString(EditModelScreen.this.model.getDamageTint().asARGB()).toUpperCase(Locale.ENGLISH).substring(2),
                     ComponentUtil.color(TextColor.color(EditModelScreen.this.model.getDamageTint().asRGB()))
                  )
               );
               break;
            case 39:
               ItemUtils.name(
                  this.damageTintVisible,
                  Component.text(
                     "Damage Tint Visible: " + EditModelScreen.this.model.canHurt(),
                     ComponentUtil.color(EditModelScreen.this.model.canHurt() ? NamedTextColor.GREEN : NamedTextColor.RED)
                  )
               );
               break;
            case 40:
               ItemUtils.name(
                  this.pitchLock,
                  Component.text(
                     "Pitch Lock: " + EditModelScreen.this.model.isLockPitch(),
                     ComponentUtil.color(EditModelScreen.this.model.isLockPitch() ? NamedTextColor.GREEN : NamedTextColor.RED)
                  )
               );
               break;
            case 41:
               ItemUtils.name(
                  this.yawLock,
                  Component.text(
                     "Yaw Lock: " + EditModelScreen.this.model.isLockYaw(),
                     ComponentUtil.color(EditModelScreen.this.model.isLockYaw() ? NamedTextColor.GREEN : NamedTextColor.RED)
                  )
               );
         }
      }
   }
}
