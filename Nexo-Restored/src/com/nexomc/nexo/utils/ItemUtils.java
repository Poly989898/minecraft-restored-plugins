package com.nexomc.nexo.utils;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.misc.misc.MiscMechanic;
import com.nexomc.nexo.mechanics.misc.misc.MiscMechanicFactory;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseCooldown;
import java.util.Iterator;
import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007J6\u0010\b\u001a\u00020\t\"\n\b\u0000\u0010\n\u0018\u0001*\u00020\u0001*\u00020\u00072\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\n0\f2\b\u0010\r\u001a\u0004\u0018\u0001H\nH\u0086\b¢\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0007¨\u0006\u0013"},
   d2 = {"Lcom/nexomc/nexo/utils/ItemUtils;", "", "<init>", "()V", "isAllowedInVanillaRecipes", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "setOrReset", "", "T", "type", "Lio/papermc/paper/datacomponent/DataComponentType$Valued;", "value", "(Lorg/bukkit/inventory/ItemStack;Lio/papermc/paper/datacomponent/DataComponentType$Valued;Ljava/lang/Object;)V", "triggerCooldown", "player", "Lorg/bukkit/entity/Player;", "item", "com.nexomc:core"}
)
public final class ItemUtils {
   @NotNull
   public static final ItemUtils INSTANCE = new ItemUtils();

   private ItemUtils() {
   }

   public final boolean isAllowedInVanillaRecipes(@Nullable ItemStack itemStack) {
      if (!NexoItems.exists(itemStack)) {
         return true;
      } else {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory;
         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory it = (MechanicFactory)iterator.next();
               int value = 0;
               mechanicFactory = it;
               if (!(it instanceof MiscMechanicFactory)) {
                  mechanicFactory = null;
               }

               it = (MiscMechanicFactory)mechanicFactory;
               if (it == null) {
                  continue;
               }

               mechanicFactory = it;
               break;
            }

            mechanicFactory = null;
            break;
         }

         mechanicFactory = mechanicFactory;
         boolean flag;
         if (mechanicFactory != null) {
            MiscMechanic mechanic = (MiscMechanic)(mechanicFactory.getMechanic(itemStack));
            if (mechanic != null) {
               flag = mechanic.isAllowedInVanillaRecipes();
               return flag;
            }
         }

         flag = Settings.DEFAULT_ALLOW_IN_VANILLA_RECIPES.toBool();
         return flag;
      }
   }

   public final void triggerCooldown(@NotNull Player player, @NotNull ItemStack item) {
      UseCooldown data = (UseCooldown)item.getData(DataComponentTypes.USE_COOLDOWN);
      if (data != null) {
         UseCooldown cooldown = data;
         int cooldownTime = (int)(cooldown.seconds() * (float)20);
         player.setCooldown(item, cooldownTime);
      }
   }
}
