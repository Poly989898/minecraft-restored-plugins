package net.Indyuce.inventory.manager.data;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.comp.profile.DefaultProfileDataModule;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.SynchronizedDataManager;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.manager.data.yaml.YAMLDataHandler;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class PlayerDataManager extends SynchronizedDataManager<InventoryHandler, DefaultOfflineDataHolder> {
   @NotNull
   private Function<MMOPlayerData, InventoryHandler> load = CustomInventoryHandler::new;

   public PlayerDataManager() {
      super(MMOInventory.plugin, new YAMLDataHandler());
   }

   public void setInventoryProvider(@NotNull Function<MMOPlayerData, InventoryHandler> load) {
      Validate.notNull(load, "Function cannot be null");
      this.load = load;
   }

   public InventoryHandler newPlayerData(@NotNull MMOPlayerData mmoPlayerData) {
      return this.load.apply(mmoPlayerData);
   }

   public Object newProfileDataModule() {
      return new DefaultProfileDataModule(MMOInventory.plugin);
   }

   @NotNull
   public CompletableFuture<Void> loadData(@NotNull InventoryHandler playerData) {
      return !(playerData instanceof CustomInventoryHandler) ? CompletableFuture.completedFuture(null) : super.loadData(playerData);
   }

   public CompletableFuture<Void> saveData(@NotNull InventoryHandler playerData) {
      return !(playerData instanceof CustomInventoryHandler) ? CompletableFuture.completedFuture(null) : super.saveData(playerData);
   }
}
