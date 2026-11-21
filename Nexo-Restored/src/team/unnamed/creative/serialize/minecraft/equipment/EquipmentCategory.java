package team.unnamed.creative.serialize.minecraft.equipment;

import java.util.Collection;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.serialize.minecraft.ResourceCategory;
import team.unnamed.creative.serialize.minecraft.io.ResourceDeserializer;
import team.unnamed.creative.serialize.minecraft.io.ResourceSerializer;

@Internal
public final class EquipmentCategory implements ResourceCategory<Equipment> {
   public static final ResourceCategory<Equipment> INSTANCE = new EquipmentCategory();

   private EquipmentCategory() {
   }

   public @NotNull String folder(PackFormat packFormat) {
      return packFormat.min().major() < 43 && packFormat.min().major() >= 0 ? "models/equipment" : "equipment";
   }

   public @NotNull String extension(PackFormat packFormat) {
      return ".json";
   }

   public @NotNull ResourceDeserializer<Equipment> deserializer() {
      return EquipmentSerializer.INSTANCE;
   }

   public @NotNull Function<ResourceContainer, Collection<Equipment>> lister() {
      return ResourceContainer::equipment;
   }

   public @NotNull ResourceSerializer<Equipment> serializer() {
      return EquipmentSerializer.INSTANCE;
   }
}
