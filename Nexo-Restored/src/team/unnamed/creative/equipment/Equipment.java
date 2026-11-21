package team.unnamed.creative.equipment;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

public interface Equipment extends ResourcePackPart, Keyed {
   @NotNull Key key();

   @NotNull Map<EquipmentLayerType, List<EquipmentLayer>> layers();

   @Contract("_ -> new")
   default @NotNull Equipment layers(@NotNull Map<EquipmentLayerType, List<EquipmentLayer>> layers) {
      return equipment(this.key(), layers);
   }

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.equipment(this);
   }

   @Contract("_, _ -> new")
   static @NotNull Equipment equipment(@NotNull Key key, @NotNull Map<EquipmentLayerType, List<EquipmentLayer>> layers) {
      return new EquipmentImpl(key, layers);
   }

   @Contract("-> new")
   static @NotNull Builder equipment() {
      return new EquipmentImpl.BuilderImpl();
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder key(@NotNull Key var1);

      @Contract("_, _ -> this")
      @NotNull Builder addLayer(@NotNull EquipmentLayerType var1, @NotNull EquipmentLayer var2);

      @Contract("_ -> this")
      default @NotNull Builder addHumanoidLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HUMANOID, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addHumanoidLeggingsLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HUMANOID_LEGGINGS, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addWingsLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.WINGS, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addWolfBodyLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.WOLF_BODY, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addHorseBodyLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HORSE_BODY, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addLlamaBodyLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.LLAMA_BODY, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addCamelSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.CAMEL_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addDonkeySaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.DONKEY_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addHorseSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HORSE_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addMuleSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.MULE_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addPigSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.PIG_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addSkeletonHorseSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.SKELETON_HORSE_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addStriderSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.STRIDER_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addZombieHorseSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.ZOMBIE_HORSE_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addHappyGhastHarness(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HAPPY_GHAST_BODY, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addNautilusBodyLayer(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.NAUTILUS_BODY, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addNautilusSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.NAUTILUS_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addCamelHuskSaddle(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.CAMEL_HUSK_SADDLE, layer);
      }

      @Contract("_ -> this")
      default @NotNull Builder addHumanoidBaby(@NotNull EquipmentLayer layer) {
         return this.addLayer(EquipmentLayerType.HUMANOID_BABY, layer);
      }

      @Contract("-> new")
      @NotNull Equipment build();
   }
}
