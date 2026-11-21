package com.ticxo.modelengine.api.nms;

import com.ticxo.modelengine.api.generator.BaseItemEnum;
import com.ticxo.modelengine.api.generator.assets.ItemModelData;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRenderer;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.render.ModelRenderer;
import com.ticxo.modelengine.api.model.render.ModelRendererParser;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.nms.network.NetworkHandler;
import com.ticxo.modelengine.api.nms.ui.AnvilHandler;
import com.ticxo.modelengine.api.utils.config.ConfigProperty;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import com.ticxo.modelengine.api.vfx.render.VFXRendererParser;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.Matrix4f;

public interface NMSHandler {
   EntityHandler getEntityHandler();

   NetworkHandler getNetworkHandler();

   default AnvilHandler getAnvilHandler() {
      return null;
   }

   RenderParsers getGlobalParsers();

   RenderParsers createParsers();

   void sendPivotOverrides();

   default Set<ItemStack> createStack(ItemModelData data, ItemModelData.Context context) {
      Collection<ItemModelData.SubModel> subModels = data.getMultiModels().getSubModels();
      return subModels.stream().map(subModel -> subModel.getItem().create(context.color(), subModel.getData())).collect(Collectors.toSet());
   }

   default boolean colorStack(ItemStack stack, Color color) {
      BaseItemEnum base = BaseItemEnum.fromMaterial(stack.getType());
      if (base == null) {
         return false;
      }

      ItemMeta meta = stack.getItemMeta();
      base.color(meta, color);
      stack.setItemMeta(meta);
      return true;
   }

   default NamespacedKey getPlayerHeadModelKey() {
      return new NamespacedKey(ConfigProperty.NAMESPACE.getString().toLowerCase(Locale.ENGLISH), "player_head");
   }

   default void applyPlayerHeadItemModel(ItemStack stack) {
   }

   <T extends ModelRenderer> ModelRendererParser<T> getModelRendererParser(T renderer);

   <T extends BehaviorRenderer> BehaviorRendererParser<T> getBehaviorRendererParser(T renderer);

   <T extends VFXRenderer> VFXRendererParser<T> getVFXRendererParser(T renderer);

   Transformation decompose(Matrix4f transform);

   default void sendMountPassengerUpdate(Set<UUID> targets, int mountId, Collection<Integer> passengerIds) {
   }
}
