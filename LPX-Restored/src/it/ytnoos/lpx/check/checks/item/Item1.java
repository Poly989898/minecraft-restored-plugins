package it.ytnoos.lpx.check.checks.item;

import com.github.retrooper.packetevents.event.simple.ItemStackDecodeEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import it.ytnoos.lpx.q_;
import it.ytnoos.lpx.api.check.CheckResult;
import it.ytnoos.lpx.api.check.options.CheckOptions;
import it.ytnoos.lpx.api.player.ProtocolPlayer;
import it.ytnoos.lpx.common.check.PlayerCheck;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Item1 extends PlayerCheck<ItemStackDecodeEvent> {
   private static final String[] DEFAULT_COMPONENTS = new String[]{"custom_name", "lore", "enchantments"};
   private DisableDecodingType disableItemDecoding;
   private NbtSizeLimit nbtSizeLimit;
   private Set<String> creativeExempts;

   public Item1(ProtocolPlayer protocolPlayer, CheckOptions checkOptions) {
      super(protocolPlayer, checkOptions);
   }

   @Override
   public void loadOptions() {
      this.disableItemDecoding = DisableDecodingType.CD(
         this.getOptions().getCheckOptions().getConfig().getString("options.disable-item-decoding", DisableDecodingType.DEFAULT.name())
      );
      this.nbtSizeLimit = NbtSizeLimit.defaults();
      this.creativeExempts = new HashSet<>();
      this.creativeExempts
         .addAll(
            this.getOptions().getCheckOptions().getConfig().isSet("options.creative-exempts")
               ? this.getOptions().getCheckOptions().getConfig().getStringList("options.creative-exempts")
               : Arrays.asList(DEFAULT_COMPONENTS)
         );
   }

   public CheckResult check(ProtocolPlayer protocolPlayer, ItemStackDecodeEvent itemStackDecodeEvent) {
      Player player2 = protocolPlayer.getPlayer().orElse(null);
      if (player2 != null && !protocolPlayer.isProcessingPayload()) {
         boolean flag = player2.getGameMode() == GameMode.CREATIVE;
         boolean flag2 = itemStackDecodeEvent.getItemStack().getDecodedSizeBytes() >= this.nbtSizeLimit.getLimit(protocolPlayer);
         switch (this.disableItemDecoding) {
            case NONE:
               return CheckResult.ALLOW;
            case SMART:
               if (!flag2 || flag) {
                  return CheckResult.ALLOW;
               }
               break;
            case ALL:
               if (flag) {
                  return CheckResult.ALLOW;
               }
            case CREATIVE:
         }

         ItemStack itemStack = itemStackDecodeEvent.getItemStack();
         itemStack.setNBT(null);
         if (flag && this.disableItemDecoding == DisableDecodingType.CREATIVE) {
            itemStack.getComponents().getPatches().keySet().removeIf(componentType -> !this.creativeExempts.contains(componentType.getName().getKey()));
         } else {
            itemStack.setComponents(null);
         }

         itemStackDecodeEvent.setItemStack(itemStack);
         return CheckResult.CHANGE;
      } else {
         return CheckResult.ALLOW;
      }
   }

   @Override
   public boolean isPreVia() {
      return true;
   }

}
