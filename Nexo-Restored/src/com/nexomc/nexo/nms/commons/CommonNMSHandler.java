package com.nexomc.nexo.nms.commons;

import com.nexomc.nexo.mods.INexoCreativeInventory;
import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.commons.furniture.FurniturePacketManager;
import com.nexomc.nexo.nms.commons.items.ITridentWrapper;
import com.nexomc.nexo.nms.commons.mods.NexoCreativeInventory;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.InteractionResult;
import io.papermc.paper.configuration.GlobalConfiguration;
import kotlin.Metadata;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.SoundGroup;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH&J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH&J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u001a\u001a\u00020\u001bH\u0016J\b\u0010\u001c\u001a\u00020\u001bH\u0016J\b\u0010\u001d\u001a\u00020\u001bH\u0016J2\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)H\u0016J(\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-2\u0006\u0010 \u001a\u00020\u000f2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)H\u0002R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006."},
   d2 = {"Lcom/nexomc/nexo/nms/commons/CommonNMSHandler;", "Lcom/nexomc/nexo/nms/NMSHandler;", "<init>", "()V", "tridentWrapper", "Lcom/nexomc/nexo/nms/commons/items/ITridentWrapper;", "trident", "Lorg/bukkit/entity/Trident;", "nearestDirection", "Lnet/minecraft/core/Direction;", "vector3f", "Lorg/joml/Vector3f;", "teleportNPCPacket", "Lnet/minecraft/network/protocol/game/ClientboundTeleportEntityPacket;", "playerNpc", "Lnet/minecraft/server/level/ServerPlayer;", "playerUtils", "Lcom/nexomc/nexo/nms/commons/PlayerUtils;", "furniturePacketManager", "Lcom/nexomc/nexo/nms/commons/furniture/FurniturePacketManager;", "customEntityHandler", "Lcom/nexomc/nexo/nms/commons/CustomEntityHandler;", "creativeInventory", "Lcom/nexomc/nexo/mods/INexoCreativeInventory;", "entityUtils", "Lcom/nexomc/nexo/nms/commons/EntityUtils;", "tripwireUpdatesDisabled", "", "noteblockUpdatesDisabled", "chorusplantUpdateDisabled", "correctBlockStates", "Lcom/nexomc/nexo/utils/InteractionResult;", "player", "Lorg/bukkit/entity/Player;", "slot", "Lorg/bukkit/inventory/EquipmentSlot;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "target", "Lorg/bukkit/block/Block;", "blockFace", "Lorg/bukkit/block/BlockFace;", "playerHitResult", "Lnet/minecraft/world/phys/BlockHitResult;", "world", "Lnet/minecraft/world/level/Level;", "com.nexomc:commons"}
)
public abstract class CommonNMSHandler implements NMSHandler {
   @NotNull
   private final PlayerUtils playerUtils = new PlayerUtils();
   @NotNull
   private final FurniturePacketManager furniturePacketManager = new FurniturePacketManager();
   @NotNull
   private final CustomEntityHandler customEntityHandler = new CustomEntityHandler();
   @NotNull
   private final INexoCreativeInventory creativeInventory = new NexoCreativeInventory();
   @NotNull
   private final EntityUtils entityUtils = new EntityUtils();

   @NotNull
   public abstract ITridentWrapper tridentWrapper(@NotNull Trident trident);

   @NotNull
   public abstract Direction nearestDirection(@NotNull Vector3f vector3f);

   @NotNull
   public abstract ClientboundTeleportEntityPacket teleportNPCPacket(@NotNull ServerPlayer serverPlayer);

   @NotNull
   public EntityUtils entityUtils() {
      return this.entityUtils;
   }

   @NotNull
   public PlayerUtils playerUtils() {
      return this.playerUtils;
   }

   @NotNull
   public FurniturePacketManager furniturePacketManager() {
      return this.furniturePacketManager;
   }

   @NotNull
   public CustomEntityHandler customEntityHandler() {
      return this.customEntityHandler;
   }

   @NotNull
   public INexoCreativeInventory creativeInventory() {
      return this.creativeInventory;
   }

   public boolean tripwireUpdatesDisabled() {
      return GlobalConfiguration.get().blockUpdates.disableTripwireUpdates;
   }

   public boolean noteblockUpdatesDisabled() {
      return GlobalConfiguration.get().blockUpdates.disableNoteblockUpdates;
   }

   public boolean chorusplantUpdateDisabled() {
      return GlobalConfiguration.get().blockUpdates.disableChorusPlantUpdates;
   }

   @Nullable
   public InteractionResult correctBlockStates(@NotNull Player player, @NotNull EquipmentSlot slot, @NotNull ItemStack itemStack, @NotNull Block target, @NotNull BlockFace blockFace) {
      InteractionHand hand = slot == EquipmentSlot.HAND ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
      net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
      ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();
      Level level = ((Entity)serverPlayer).level();
      ServerLevel serverLevel = (ServerLevel)level;
      BlockHitResult hitResult = this.playerHitResult((Level)serverLevel, serverPlayer, target, blockFace);
      BlockPlaceContext placeContext = new BlockPlaceContext((Level)serverLevel, (net.minecraft.world.entity.player.Player)serverPlayer, hand, nmsStack, hitResult);
      Item item = nmsStack.getItem();
      BlockItem blockItem2 = item instanceof BlockItem ? (BlockItem)item : null;
      if ((item instanceof BlockItem ? (BlockItem)item : null) == null) {
         CommonNMSHandler commonNMSHandler = this;
         int value = 0;
         nmsStack.getItem().useOn(new UseOnContext((net.minecraft.world.entity.player.Player)serverPlayer, hand, hitResult));
         if (!((CraftPlayer)player).isSneaking()) {
            serverPlayer.gameMode.useItem(serverPlayer, (Level)serverLevel, nmsStack, hand);
         }

         if (((CraftPlayer)player).getGameMode() == GameMode.CREATIVE) {
            ((CraftPlayer)player).getInventory().setItem(slot, CraftItemStack.asBukkitMirror(nmsStack));
         }

         return null;
      } else {
         BlockItem blockItem = blockItem2;
         InteractionResult interactionResult3 = InteractionResult.Companion.fromNms(blockItem.place(placeContext).getClass());
         InteractionResult interactionResult = InteractionResult.FAIL;
         int value2 = 0;
         InteractionResult interactionResult2 = !interactionResult.equals(interactionResult3) ? interactionResult3 : null;
         if (interactionResult2 == null) {
            return null;
         } else {
            InteractionResult result = interactionResult2;
            if (!((CraftPlayer)player).isSneaking()) {
               BlockPos clickPos = placeContext.getClickedPos();
               Block blockAt = ((CraftPlayer)player).getWorld().getBlockAt(clickPos.getX(), clickPos.getY(), clickPos.getZ());
               Block block = blockAt;
               SoundGroup blockSoundGroup = block.getBlockSoundGroup();
               SoundGroup sound = blockSoundGroup;
               World world = ((CraftPlayer)player).getWorld();
               Location location = block.getLocation();
               world.playSound(BlockHelpers.toCenterBlockLocation(location), sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            }

            ((CraftPlayer)player).getInventory().setItem(slot, CraftItemStack.asBukkitMirror(nmsStack));
            return result;
         }
      }
   }

   private final BlockHitResult playerHitResult(Level world, ServerPlayer player, Block target, BlockFace blockFace) {
      Vec3 from = player.getEyePosition();
      Vec3 to = from.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(player.blockInteractionRange()));
      BlockHitResult blockHitResult = world.clip(new ClipContext(from, to, net.minecraft.world.level.ClipContext.Block.OUTLINE, Fluid.NONE, (Entity)player)).withPosition(new BlockPos(target.getX(), target.getY(), target.getZ())).withDirection(Direction.valueOf(blockFace.name()));
      return blockHitResult;
   }
}
