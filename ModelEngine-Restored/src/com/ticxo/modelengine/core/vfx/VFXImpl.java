package com.ticxo.modelengine.core.vfx;

import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.vfx.VFX;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.core.vfx.render.VFXDisplayRendererImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Getter
public class VFXImpl implements VFX {
   private final BaseEntity<?> base;
   private final VFXRenderer renderer;
   private final Vector3f modelScale = new Vector3f();
   private final boolean initialized;
   private final List<Runnable> queuedTask = new ArrayList<>();
   private final DataTracker<ItemStack> modelTracker = new DataTracker<>(new ItemStack(Material.AIR));
   private boolean isBaseEntityVisible = true;
   private boolean destroyed;
   private boolean removed;
   @Setter
   private float yaw;
   @Setter
   private float pitch;
   @Setter
   private Vector origin = new Vector();
   @Setter
   private Vector3f position = new Vector3f();
   @Setter
   private Vector3f rotation = new Vector3f();
   @Setter
   private Vector3f scale = new Vector3f(1.0F);
   private Color color;
   private boolean enchanted;
   @Setter
   private int blockLight = -1;
   @Setter
   private int skyLight = -1;
   @Setter
   private boolean visible = true;

   public VFXImpl(@NotNull BaseEntity<?> base, @Nullable Function<VFX, VFXRenderer> rendererSupplier, @Nullable Consumer<VFX> consumer) {
      this.base = base;
      this.setOrigin(base.getLocation().toVector());
      VFXRenderer renderer = rendererSupplier == null ? new VFXDisplayRendererImpl(this) : rendererSupplier.apply(this);
      this.renderer = renderer == null ? new VFXDisplayRendererImpl(this) : renderer;
      if (consumer != null) {
         consumer.accept(this);
      }

      this.renderer.initialize();
      synchronized (this.queuedTask) {
         this.queuedTask.forEach(Runnable::run);
         this.initialized = true;
      }

      this.registerSelf();
   }

   @Override
   public boolean tick() {
      if (!this.initialized) {
         return true;
      }

      this.setOrigin(this.base.getLocation().toVector());
      this.setYaw(this.base.getYHeadRot());
      this.setPitch(this.base.getXHeadRot());
      this.renderer.readVFXData();
      return !this.removed && !this.base.isRemoved();
   }

   @Override
   public void destroy() {
      this.destroyed = true;
   }

   @Override
   public void markRemoved() {
      this.removed = true;
   }

   @Override
   public void queuePostInitTask(Runnable runnable) {
      synchronized (this.queuedTask) {
         if (this.initialized) {
            runnable.run();
         } else {
            this.queuedTask.add(runnable);
         }
      }
   }

   @Override
   public void setBaseEntityVisible(boolean flag) {
      if (this.isBaseEntityVisible() != flag) {
         this.isBaseEntityVisible = flag;
         this.base.setVisible(flag);
      }
   }

   @Override
   public void setModelScale(int scale) {
      this.modelScale.set(scale);
   }

   @Override
   public ItemStack getModel() {
      return this.modelTracker.get();
   }

   @Override
   public void setModel(ItemStack stack) {
      this.modelTracker.set(stack);
   }

   @Override
   public void setColor(Color color) {
      this.color = color;
      ItemStack model = this.modelTracker.get();
      BaseItemEnum base = BaseItemEnum.fromMaterial(model.getType());
      if (base != null) {
         ItemMeta meta = model.getItemMeta();
         base.color(meta, color);
         model.setItemMeta(meta);
         this.modelTracker.markDirty();
      }
   }

   @Override
   public void setEnchanted(boolean flag) {
      if (this.isEnchanted() != flag) {
         this.enchanted = flag;
         ItemStack model = this.modelTracker.get();
         if (flag) {
            model.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
         } else {
            model.removeEnchantment(Enchantment.VANISHING_CURSE);
         }

         this.modelTracker.markDirty();
      }
   }
}
