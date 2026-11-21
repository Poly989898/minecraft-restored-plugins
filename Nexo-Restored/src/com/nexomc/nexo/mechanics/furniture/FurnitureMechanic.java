package com.nexomc.nexo.mechanics.furniture;

import com.google.common.base.Preconditions;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.blocklocker.BlockLockerMechanic;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.ItemModifier;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.breakable.Breakable;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.connectable.ConnectableMechanic;
import com.nexomc.nexo.mechanics.furniture.door.FurnitureDoor;
import com.nexomc.nexo.mechanics.furniture.evolution.EvolvingFurniture;
import com.nexomc.nexo.mechanics.furniture.hitbox.BarrierHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.FurnitureHitbox;
import com.nexomc.nexo.mechanics.furniture.hitbox.InteractionHitbox;
import com.nexomc.nexo.mechanics.furniture.jukebox.JukeboxBlock;
import com.nexomc.nexo.mechanics.furniture.placements.FurniturePlacement;
import com.nexomc.nexo.mechanics.furniture.placements.FurniturePlacementMechanic;
import com.nexomc.nexo.mechanics.furniture.placements.PlacementType;
import com.nexomc.nexo.mechanics.furniture.rotatable.Rotatable;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.mechanics.furniture.states.FurnitureState;
import com.nexomc.nexo.mechanics.furniture.states.FurnitureStates;
import com.nexomc.nexo.mechanics.furniture.text_entity.FurnitureTextEntities;
import com.nexomc.nexo.mechanics.light.LightBlock;
import com.nexomc.nexo.mechanics.light.LightMechanic;
import com.nexomc.nexo.mechanics.limitedplacing.LimitedPlacing;
import com.nexomc.nexo.mechanics.storage.StorageMechanic;
import com.nexomc.nexo.utils.BlockHelpers;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.actions.ClickAction;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import com.nexomc.nexo.utils.logs.Logs;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import nl.rutgerkok.blocklocker.BlockLockerAPIv2;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008a\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\r\b\u0016\u0018\u0000 \u0098\u00012\u00020\u0001:\u0004\u0097\u0001\u0098\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010l\u001a\u00020m2\u0006\u0010n\u001a\u00020oJ\u001b\u0010|\u001a\u00020\u00192\b\u0010}\u001a\u0004\u0018\u00010~2\t\u0010\u007f\u001a\u0005\u0018\u00010\u0080\u0001J\u0013\u0010\u0081\u0001\u001a\u0004\u0018\u00010o2\b\u0010\u0082\u0001\u001a\u00030\u0083\u0001J'\u0010\u0081\u0001\u001a\u0004\u0018\u00010o2\b\u0010\u0082\u0001\u001a\u00030\u0083\u00012\b\u0010\u0084\u0001\u001a\u00030\u0085\u00012\b\u0010\u0086\u0001\u001a\u00030\u0087\u0001J0\u0010\u0081\u0001\u001a\u0004\u0018\u00010o2\b\u0010\u0082\u0001\u001a\u00030\u0083\u00012\b\u0010\u0084\u0001\u001a\u00030\u0085\u00012\b\u0010\u0086\u0001\u001a\u00030\u0087\u00012\u0007\u0010\u0088\u0001\u001a\u00020\u0019J\u001e\u0010\u0089\u0001\u001a\u00030\u0083\u00012\b\u0010\u008a\u0001\u001a\u00030\u0083\u00012\b\u0010\u0086\u0001\u001a\u00030\u0087\u0001H\u0002J&\u0010\u008b\u0001\u001a\u00030\u008c\u00012\u0006\u0010n\u001a\u00020o2\b\u0010\u0084\u0001\u001a\u00030\u0085\u00012\b\u0010\u008d\u0001\u001a\u00030\u0087\u0001H\u0002J\u001c\u0010\u008e\u0001\u001a\u00030\u008c\u00012\u0006\u0010n\u001a\u00020o2\b\u0010\u008d\u0001\u001a\u00030\u0087\u0001H\u0002J\u0011\u0010\u008f\u0001\u001a\u00030\u008c\u00012\u0007\u0010\u0090\u0001\u001a\u00020oJ\u0010\u0010\u0091\u0001\u001a\u00030\u008c\u00012\u0006\u0010n\u001a\u00020oJ\u001b\u0010\u0092\u0001\u001a\u00020\u00192\u0006\u0010n\u001a\u00020o2\n\b\u0002\u0010\u0084\u0001\u001a\u00030\u0085\u0001J\u001b\u0010\u0092\u0001\u001a\u00020\u00192\b\u0010\u0082\u0001\u001a\u00030\u0083\u00012\b\u0010\u0084\u0001\u001a\u00030\u0085\u0001J\u0010\u0010\u0093\u0001\u001a\u00030\u008c\u00012\u0006\u0010}\u001a\u00020~J\u0010\u0010\u0094\u0001\u001a\u00030\u008c\u00012\u0006\u0010n\u001a\u00020oJ\u000f\u0010\u0095\u0001\u001a\u00020\u00002\u0006\u0010n\u001a\u00020oJ\u0007\u0010\u0096\u0001\u001a\u00020\u0000R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u001d¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010 \u001a\u00020!¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0013\u0010$\u001a\u0004\u0018\u00010%¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u000e\u0010(\u001a\u00020%X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010*X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010+\u001a\b\u0012\u0004\u0012\u00020-0,¢\u0006\b\n\u0000\u001a\u0004\b.\u0010/R$\u00102\u001a\u0002012\u0006\u00100\u001a\u000201@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b3\u00104\"\u0004\b5\u00106R\u0011\u00107\u001a\u000208¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\u0013\u0010;\u001a\u0004\u0018\u00010<¢\u0006\b\n\u0000\u001a\u0004\b=\u0010>R\u0011\u0010?\u001a\u00020@¢\u0006\b\n\u0000\u001a\u0004\bA\u0010BR\u0011\u0010C\u001a\u00020D¢\u0006\b\n\u0000\u001a\u0004\bE\u0010FR\u0011\u0010G\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bH\u0010\u001bR\u0017\u0010I\u001a\b\u0012\u0004\u0012\u00020J0,¢\u0006\b\n\u0000\u001a\u0004\bK\u0010/R\u0017\u0010L\u001a\b\u0012\u0004\u0012\u00020M0,¢\u0006\b\n\u0000\u001a\u0004\bN\u0010/R\u0013\u0010O\u001a\u0004\u0018\u00010P¢\u0006\b\n\u0000\u001a\u0004\bQ\u0010RR\u0013\u0010S\u001a\u0004\u0018\u00010T¢\u0006\b\n\u0000\u001a\u0004\bU\u0010VR\u0013\u0010W\u001a\u0004\u0018\u00010X¢\u0006\b\n\u0000\u001a\u0004\bY\u0010ZR\u0011\u0010[\u001a\u00020\\¢\u0006\b\n\u0000\u001a\u0004\b]\u0010^R(\u0010`\u001a\u0004\u0018\u00010_2\b\u00100\u001a\u0004\u0018\u00010_@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\ba\u0010b\"\u0004\bc\u0010dR$\u0010f\u001a\u00020e2\u0006\u00100\u001a\u00020e@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bg\u0010h\"\u0004\bi\u0010jR\u0011\u0010k\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bk\u0010\u001bR\u0011\u0010p\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bq\u0010\u001bR\u0011\u0010r\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\br\u0010\u001bR\u0011\u0010s\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bs\u0010\u001bR\u0011\u0010t\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bu\u0010\u001bR\u0011\u0010v\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\bw\u0010\u001bR\u0011\u0010x\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\by\u0010\u001bR\u0011\u0010z\u001a\u00020\u0019¢\u0006\b\n\u0000\u001a\u0004\b{\u0010\u001b¨\u0006\u0099\u0001"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/MechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "limitedPlacing", "Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing;", "getLimitedPlacing", "()Lcom/nexomc/nexo/mechanics/limitedplacing/LimitedPlacing;", "storage", "Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "getStorage", "()Lcom/nexomc/nexo/mechanics/storage/StorageMechanic;", "blockSounds", "Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "getBlockSounds", "()Lcom/nexomc/nexo/utils/blocksounds/BlockSounds;", "jukebox", "Lcom/nexomc/nexo/mechanics/furniture/jukebox/JukeboxBlock;", "getJukebox", "()Lcom/nexomc/nexo/mechanics/furniture/jukebox/JukeboxBlock;", "farmlandRequired", "", "getFarmlandRequired", "()Z", "evolution", "Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolvingFurniture;", "getEvolution", "()Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolvingFurniture;", "light", "Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "getLight", "()Lcom/nexomc/nexo/mechanics/light/LightMechanic;", "modelEngineID", "", "getModelEngineID", "()Ljava/lang/String;", "placedItemId", "placedItemModel", "Lnet/kyori/adventure/key/Key;", "clickActions", "", "Lcom/nexomc/nexo/utils/actions/ClickAction;", "getClickActions", "()Ljava/util/List;", "value", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "properties", "getProperties", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;", "setProperties$com_nexomc_core", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureProperties;)V", "rotatable", "Lcom/nexomc/nexo/mechanics/furniture/rotatable/Rotatable;", "getRotatable", "()Lcom/nexomc/nexo/mechanics/furniture/rotatable/Rotatable;", "blockLocker", "Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerMechanic;", "getBlockLocker", "()Lcom/nexomc/nexo/compatibilities/blocklocker/BlockLockerMechanic;", "restrictedRotation", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$RestrictedRotation;", "getRestrictedRotation", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$RestrictedRotation;", "breakable", "Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "getBreakable", "()Lcom/nexomc/nexo/mechanics/breakable/Breakable;", "waterloggable", "getWaterloggable", "seats", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "getSeats", "beds", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "getBeds", "connectable", "Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic;", "getConnectable", "()Lcom/nexomc/nexo/mechanics/furniture/connectable/ConnectableMechanic;", "door", "Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoor;", "getDoor", "()Lcom/nexomc/nexo/mechanics/furniture/door/FurnitureDoor;", "states", "Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStates;", "getStates", "()Lcom/nexomc/nexo/mechanics/furniture/states/FurnitureStates;", "textEntities", "Lcom/nexomc/nexo/mechanics/furniture/text_entity/FurnitureTextEntities;", "getTextEntities", "()Lcom/nexomc/nexo/mechanics/furniture/text_entity/FurnitureTextEntities;", "Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacement;", "placements", "getPlacements", "()Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacement;", "setPlacements$com_nexomc_core", "(Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacement;)V", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;", "hitbox", "getHitbox", "()Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;", "setHitbox$com_nexomc_core", "(Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;)V", "isModelEngine", "placedItem", "Lcom/nexomc/nexo/items/ItemBuilder;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "hasLimitedPlacing", "getHasLimitedPlacing", "isStorage", "isJukebox", "hasBlockSounds", "getHasBlockSounds", "hasSeats", "getHasSeats", "hasBeds", "getHasBeds", "hasEvolution", "getHasEvolution", "isInteractable", "player", "Lorg/bukkit/entity/Player;", "block", "Lorg/bukkit/block/Block;", "place", "location", "Lorg/bukkit/Location;", "yaw", "", "facing", "Lorg/bukkit/block/BlockFace;", "checkSpace", "correctedSpawnLocation", "baseLocation", "setBaseFurnitureData", "", "blockFace", "correctFurnitureTranslation", "spawnModelEngineFurniture", "entity", "removeBaseEntity", "notEnoughSpace", "runClickActions", "rotateFurniture", "placementMechanic", "clone", "RestrictedRotation", "Companion", "com.nexomc:core"}
)
public class FurnitureMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final LimitedPlacing limitedPlacing;
   @Nullable
   private final StorageMechanic storage;
   @Nullable
   private final BlockSounds blockSounds;
   @Nullable
   private final JukeboxBlock jukebox;
   private final boolean farmlandRequired;
   @Nullable
   private final EvolvingFurniture evolution;
   @NotNull
   private final LightMechanic light;
   @Nullable
   private final String modelEngineID;
   @NotNull
   private final String placedItemId;
   @Nullable
   private final Key placedItemModel;
   @NotNull
   private final List<ClickAction> clickActions;
   @NotNull
   private FurnitureProperties properties;
   @NotNull
   private final Rotatable rotatable;
   @Nullable
   private final BlockLockerMechanic blockLocker;
   @NotNull
   private final RestrictedRotation restrictedRotation;
   @NotNull
   private final Breakable breakable;
   private final boolean waterloggable;
   @NotNull
   private final List<FurnitureSeat> seats;
   @NotNull
   private final List<FurnitureBed> beds;
   @Nullable
   private final ConnectableMechanic connectable;
   @Nullable
   private final FurnitureDoor door;
   @Nullable
   private final FurnitureStates states;
   @NotNull
   private final FurnitureTextEntities textEntities;
   @Nullable
   private FurniturePlacement placements;
   @NotNull
   private FurnitureHitbox hitbox;
   private final boolean isModelEngine;
   private final boolean hasLimitedPlacing;
   private final boolean isStorage;
   private final boolean isJukebox;
   private final boolean hasBlockSounds;
   private final boolean hasSeats;
   private final boolean hasBeds;
   private final boolean hasEvolution;
   @NotNull
   private static final NamespacedKey FURNITURE_KEY;
   @NotNull
   private static final NamespacedKey INVALID_FURNITURE_KEY;
   @NotNull
   private static final NamespacedKey DISPLAY_NAME_KEY;
   @NotNull
   private static final NamespacedKey FURNITURE_DYE_KEY;
   @NotNull
   private static final NamespacedKey FURNITURE_LIGHT_KEY;
   @NotNull
   private static final NamespacedKey MODELENGINE_KEY;
   @NotNull
   private static final NamespacedKey EVOLUTION_KEY;
   @NotNull
   private static final ConfigurationSection invalidFurnitureSection;
   @NotNull
   private static final Lazy<FurnitureMechanic> INVALID_FURNITURE$delegate;

   public FurnitureMechanic(@NotNull MechanicFactory factory, @NotNull ConfigurationSection section) {
      com.nexomc.nexo.mechanics.furniture.FurnitureMechanic.RestrictedRotation restrictedRotation2;
      ItemModifier[] itemModifiers = new ItemModifier[]{FurnitureMechanic::_init_$lambda$0};
      super(factory, section, itemModifiers);
      FurnitureMechanic furnitureMechanic = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "limited_placing");
      LimitedPlacing limitedPlacing2;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         limitedPlacing2 = new LimitedPlacing(p0);
         furnitureMechanic = this;
      } else {
         limitedPlacing2 = null;
      }

      this.limitedPlacing = limitedPlacing2;
      furnitureMechanic = this;
      ConfigurationSection configurationSection2 = NexoYamlKt.section(section, "storage");
      StorageMechanic storageMechanic;
      if (configurationSection2 != null) {
         ConfigurationSection p0 = configurationSection2;
         int value2 = 0;
         storageMechanic = new StorageMechanic(p0);
         furnitureMechanic = this;
      } else {
         storageMechanic = null;
      }

      this.storage = storageMechanic;
      furnitureMechanic = this;
      ConfigurationSection configurationSection3 = NexoYamlKt.section(section, "block_sounds");
      BlockSounds blockSounds2;
      if (configurationSection3 != null) {
         ConfigurationSection p0 = configurationSection3;
         int value3 = 0;
         blockSounds2 = new BlockSounds(p0);
         furnitureMechanic = this;
      } else {
         blockSounds2 = null;
      }

      this.blockSounds = blockSounds2;
      furnitureMechanic = this;
      ConfigurationSection configurationSection4 = NexoYamlKt.section(section, "jukebox");
      JukeboxBlock jukeboxBlock;
      if (configurationSection4 != null) {
         ConfigurationSection p0 = configurationSection4;
         int value4 = 0;
         jukeboxBlock = new JukeboxBlock(p0);
         furnitureMechanic = this;
      } else {
         jukeboxBlock = null;
      }

      this.jukebox = jukeboxBlock;
      this.farmlandRequired = section.getBoolean("farmland_required", false);
      furnitureMechanic = this;
      ConfigurationSection configurationSection5 = NexoYamlKt.section(section, "evolution");
      EvolvingFurniture evolvingFurniture;
      if (configurationSection5 != null) {
         ConfigurationSection p0 = configurationSection5;
         int value5 = 0;
         EvolvingFurniture evolvingFurniture2 = new EvolvingFurniture(p0);
         value5 = 0;
         MechanicFactory mechanicFactory = this.getFactory();
         ((FurnitureFactory)mechanicFactory).registerEvolution();
         furnitureMechanic = this;
         evolvingFurniture = evolvingFurniture2;
      } else {
         evolvingFurniture = null;
      }

      this.evolution = evolvingFurniture;
      this.modelEngineID = section.getString("modelengine_id", (String)null);
      String str = NexoYamlKt.stringOrNull(section, "item");
      if (str == null) {
         str = this.getItemID();
      }

      this.placedItemId = str;
      this.placedItemModel = NexoYamlKt.key(section, "item_model");
      Iterable iterable = (Iterable)NexoYamlKt.sectionList(section, "clickActions");
      ClickAction.Companion companion = ClickAction.Companion;
      int value17 = 0;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value6 = 0;
         ConfigurationSection p0 = (ConfigurationSection)element;
         int value7 = 0;
         ClickAction clickAction = companion.from(p0);
         if (clickAction != null) {
            Object obj2 = clickAction;
            int value8 = 0;
            destination.add(obj2);
         }
      }

      this.clickActions = (List)destination;
      furnitureMechanic = this;
      ConfigurationSection configurationSection6 = NexoYamlKt.section(section, "properties");
      FurnitureProperties furnitureProperties;
      if (configurationSection6 != null) {
         ConfigurationSection configurationSection14 = configurationSection6;
         int value9 = 0;
         furnitureProperties = new FurnitureProperties(configurationSection14);
         furnitureMechanic = this;
      } else {
         furnitureProperties = new FurnitureProperties(null, null, null, org.bukkit.entity.ItemDisplay.ItemDisplayTransform.NONE, null, null, null, 0.0F, 0.0F, new org.joml.Vector3f(), new org.joml.Quaternionf(), new org.joml.Quaternionf(), kotlin.time.Duration.Companion.getZERO_UwyO8pc(), true, com.nexomc.nexo.utils.TriState.NOT_SET);
      }

      furnitureMechanic.properties = furnitureProperties;
      furnitureMechanic = this;
      Object obj = section.get("rotatable");
      Rotatable rotatable2;
      if (obj != null) {
         Object obj3 = obj;
         int value10 = 0;
         rotatable2 = new Rotatable(obj3);
         furnitureMechanic = this;
      } else {
         rotatable2 = new Rotatable();
      }

      this.rotatable = rotatable2;
      furnitureMechanic = this;
      ConfigurationSection configurationSection7 = NexoYamlKt.section(section, "blocklocker");
      BlockLockerMechanic blockLockerMechanic;
      if (configurationSection7 != null) {
         ConfigurationSection configurationSection15 = configurationSection7;
         int p0 = 0;
         blockLockerMechanic = new BlockLockerMechanic(configurationSection15);
         furnitureMechanic = this;
      } else {
         blockLockerMechanic = null;
      }

      block: {
         this.blockLocker = blockLockerMechanic;
         furnitureMechanic = this;
         String string = section.getString("restricted_rotation");
         if (string != null) {
            String str2 = string;
            RestrictedRotation.Companion p0 = FurnitureMechanic.RestrictedRotation.Companion;
            restrictedRotation2 = p0.fromString(str2);
            furnitureMechanic = this;
            if (restrictedRotation2 != null) {
               break block;
            }
         }

         restrictedRotation2 = FurnitureMechanic.RestrictedRotation.STRICT;
      }

      this.restrictedRotation = restrictedRotation2;
      this.breakable = new Breakable(section);
      this.waterloggable = section.getBoolean("waterloggable");
      iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "seats", (String)null);
      Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String p0 = (String)item;
         int value11 = 0;
         collection3.add(new FurnitureSeat(p0));
      }

      this.seats = (List)collection3;
      iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "beds", (String)null);
      collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj4 : iterable) {
         String p0 = (String)obj4;
         int p0_6 = 0;
         collection3.add(new FurnitureBed(p0));
      }

      this.beds = (List)collection3;
      furnitureMechanic = this;
      ConfigurationSection configurationSection8 = NexoYamlKt.section(section, "connectable");
      ConnectableMechanic connectableMechanic;
      if (configurationSection8 != null) {
         ConfigurationSection p0 = configurationSection8;
         int value18 = 0;
         connectableMechanic = new ConnectableMechanic(p0);
         furnitureMechanic = this;
      } else {
         connectableMechanic = null;
      }

      this.connectable = connectableMechanic;
      furnitureMechanic = this;
      ConfigurationSection configurationSection9 = NexoYamlKt.section(section, "door");
      FurnitureDoor furnitureDoor;
      if (configurationSection9 != null) {
         ConfigurationSection p0 = configurationSection9;
         int value12 = 0;
         furnitureDoor = new FurnitureDoor(p0);
         furnitureMechanic = this;
      } else {
         furnitureDoor = null;
      }

      this.door = furnitureDoor;
      furnitureMechanic = this;
      ConfigurationSection configurationSection10 = NexoYamlKt.section(section, "states");
      FurnitureStates furnitureStates;
      if (configurationSection10 != null) {
         ConfigurationSection p0 = configurationSection10;
         int value13 = 0;
         furnitureStates = new FurnitureStates(p0);
         furnitureMechanic = this;
      } else {
         furnitureStates = null;
      }

      this.states = furnitureStates;
      this.textEntities = new FurnitureTextEntities(section);
      furnitureMechanic = this;
      ConfigurationSection configurationSection11 = NexoYamlKt.section(section, "placements");
      FurniturePlacement furniturePlacement;
      if (configurationSection11 != null) {
         ConfigurationSection it = configurationSection11;
         int value14 = 0;
         furniturePlacement = new FurniturePlacement(section, it, factory);
         furnitureMechanic = this;
      } else {
         furniturePlacement = null;
      }

      furnitureMechanic.placements = furniturePlacement;
      furnitureMechanic = this;
      ConfigurationSection configurationSection12 = NexoYamlKt.section(section, "hitbox");
      FurnitureHitbox furnitureHitbox;
      if (configurationSection12 != null) {
         ConfigurationSection configurationSection13 = configurationSection12;
         furnitureHitbox = new FurnitureHitbox(configurationSection13);
         furnitureMechanic = this;
      } else {
         collection3 = (Collection)NexoYamlKt.stringListOrSingle(section, "hitbox", (String)null);
         Collection collection;
         if (collection3.isEmpty()) {
            collection = null;
            furnitureMechanic = this;
         } else {
            collection = collection3;
         }

         List list = (List)collection;
         if (list != null) {
            Collection collection2 = (Collection)list;
            FurnitureMechanic furnitureMechanic2 = furnitureMechanic;
            furnitureHitbox = new FurnitureHitbox(collection2);
            furnitureMechanic = furnitureMechanic2;
         } else {
            ObjectOpenHashSet objectOpenHashSet = ObjectOpenHashSet.of(new InteractionHitbox(new org.joml.Vector3f(), 1.0F, 1.0F));
            furnitureHitbox = new FurnitureHitbox(new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), objectOpenHashSet, new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());
         }
      }

      furnitureMechanic.hitbox = furnitureHitbox;
      Iterable barriers = (Iterable)this.hitbox.getBarriers();
      value17 = 0;
      Collection collection4 = (Collection)(new ObjectArrayList((barriers instanceof Collection ? (Collection)barriers : null) != null ? (barriers instanceof Collection ? (Collection)barriers : null).size() : 10));

      for(Object obj5 : barriers) {
         BarrierHitbox it = (BarrierHitbox)obj5;
         int value15 = 0;
         collection4.add(it.toVector3f());
      }

      ObjectArrayList barrierHitboxes = (ObjectArrayList)collection4;
      this.light = new LightMechanic(section);
      barriers = (Iterable)this.light.getLightBlocks();
      value17 = 0;
      collection4 = (Collection)(new ObjectArrayList());

      for(Object obj6 : barriers) {
         LightBlock it = (LightBlock)obj6;
         int value16 = 0;
         if (barrierHitboxes.contains(it.toVector3f())) {
            collection4.add(obj6);
         }
      }

      barriers = (Iterable)((ObjectArrayList)collection4);
      value17 = 0;
      if (CollectionsKt.count(barriers) > 0) {
         ObjectArrayList it = (ObjectArrayList)barriers;
         int value19 = 0;
         String itemID = this.getItemID();
         Logs.logError("Furniture " + itemID + " has lights that overlap with the barrierHitboxes at: " + CollectionsKt.joinToString((Iterable)it, (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), false);
         Logs.logWarn("Nexo will ignore any lights that conflict with a barrier...", false);
         CollectionsKt.removeAll((Collection)this.light.getLightBlocks(), (Iterable)it);
      }

      if (!section.contains("hardness")) {
         this.breakable.setHardness((double)0.0F);
      }

      this.isModelEngine = PluginUtils.INSTANCE.isModelEngineEnabled() && this.modelEngineID != null;
      this.hasLimitedPlacing = this.limitedPlacing != null;
      this.isStorage = this.storage != null;
      this.isJukebox = this.jukebox != null;
      this.hasBlockSounds = this.blockSounds != null;
      this.hasSeats = !((Collection)this.seats).isEmpty();
      this.hasBeds = !((Collection)this.beds).isEmpty();
      this.hasEvolution = this.evolution != null;
   }

   @Nullable
   public final LimitedPlacing getLimitedPlacing() {
      return this.limitedPlacing;
   }

   @Nullable
   public final StorageMechanic getStorage() {
      return this.storage;
   }

   @Nullable
   public final BlockSounds getBlockSounds() {
      return this.blockSounds;
   }

   @Nullable
   public final JukeboxBlock getJukebox() {
      return this.jukebox;
   }

   public final boolean getFarmlandRequired() {
      return this.farmlandRequired;
   }

   @Nullable
   public final EvolvingFurniture getEvolution() {
      return this.evolution;
   }

   @NotNull
   public final LightMechanic getLight() {
      return this.light;
   }

   @Nullable
   public final String getModelEngineID() {
      return this.modelEngineID;
   }

   @NotNull
   public final List<ClickAction> getClickActions() {
      return this.clickActions;
   }

   @NotNull
   public final FurnitureProperties getProperties() {
      return this.properties;
   }

   public final void setProperties$com_nexomc_core(@NotNull FurnitureProperties furnitureProperties) {
      this.properties = furnitureProperties;
   }

   @NotNull
   public final Rotatable getRotatable() {
      return this.rotatable;
   }

   @Nullable
   public final BlockLockerMechanic getBlockLocker() {
      return this.blockLocker;
   }

   @NotNull
   public final RestrictedRotation getRestrictedRotation() {
      return this.restrictedRotation;
   }

   @NotNull
   public final Breakable getBreakable() {
      return this.breakable;
   }

   public final boolean getWaterloggable() {
      return this.waterloggable;
   }

   @NotNull
   public final List<FurnitureSeat> getSeats() {
      return this.seats;
   }

   @NotNull
   public final List<FurnitureBed> getBeds() {
      return this.beds;
   }

   @Nullable
   public final ConnectableMechanic getConnectable() {
      return this.connectable;
   }

   @Nullable
   public final FurnitureDoor getDoor() {
      return this.door;
   }

   @Nullable
   public final FurnitureStates getStates() {
      return this.states;
   }

   @NotNull
   public final FurnitureTextEntities getTextEntities() {
      return this.textEntities;
   }

   @Nullable
   public final FurniturePlacement getPlacements() {
      return this.placements;
   }

   public final void setPlacements$com_nexomc_core(@Nullable FurniturePlacement furniturePlacement) {
      this.placements = furniturePlacement;
   }

   @NotNull
   public final FurnitureHitbox getHitbox() {
      return this.hitbox;
   }

   public final void setHitbox$com_nexomc_core(@NotNull FurnitureHitbox furnitureHitbox) {
      this.hitbox = furnitureHitbox;
   }

   public final boolean isModelEngine() {
      return this.isModelEngine;
   }

   @NotNull
   public final ItemBuilder placedItem(@NotNull ItemDisplay baseEntity) {
      ItemBuilder builder = null;
      if (!this.light.isEmpty() && this.light.getToggleable() && NexoFurniture.lightState(baseEntity)) {
         ItemBuilder toggledItemModel = this.light.getToggledItemModel();
         if (toggledItemModel == null) {
            toggledItemModel = this.light.getToggledModel();
         }

         builder = toggledItemModel != null ? toggledItemModel.clone() : null;
      }

      if (this.connectable != null) {
         builder = this.connectable.displayedItem(baseEntity);
      }

      ItemBuilder itemBuilder = builder;
      if (builder == null) {
         Key key = this.placedItemModel;
         if (key != null) {
            Key it = key;
            int value = 0;
            itemBuilder = (new ItemBuilder(Material.LEATHER_HORSE_ARMOR)).setItemModel(it);
         } else {
            itemBuilder = null;
         }

         if (itemBuilder == null) {
            ItemBuilder itemBuilder2 = NexoItems.itemFromId(this.placedItemId);
            itemBuilder = itemBuilder2 != null ? itemBuilder2.clone() : null;
            if (itemBuilder == null) {
               itemBuilder2 = NexoItems.itemFromId(this.getItemID());
               itemBuilder = itemBuilder2 != null ? itemBuilder2.clone() : null;
               if (itemBuilder == null) {
                  itemBuilder = new ItemBuilder(Material.BARRIER);
               }
            }
         }
      }

      builder = itemBuilder;
      FurnitureStates furnitureStates = this.states;
      if (furnitureStates != null) {
         FurnitureState furnitureState = furnitureStates.currentState(baseEntity);
         if (furnitureState != null) {
            furnitureState.stateItem(builder);
         }
      }

      return builder;
   }

   public final boolean getHasLimitedPlacing() {
      return this.hasLimitedPlacing;
   }

   public final boolean isStorage() {
      return this.isStorage;
   }

   public final boolean isJukebox() {
      return this.isJukebox;
   }

   public final boolean getHasBlockSounds() {
      return this.hasBlockSounds;
   }

   public final boolean getHasSeats() {
      return this.hasSeats;
   }

   public final boolean getHasBeds() {
      return this.hasBeds;
   }

   public final boolean getHasEvolution() {
      return this.hasEvolution;
   }

   public final boolean isInteractable(@Nullable Player player, @Nullable Block block) {
      boolean flag;
      if (!this.rotatable.shouldRotate(player) && !this.hasSeats && !this.isStorage && !this.light.getToggleable() && ((Collection)this.clickActions).isEmpty() && this.door == null) {
         if (this.blockLocker != null) {
            int value = 0;
            flag = BlockLockerAPIv2.isAllowed(player, block, true);
         } else {
            flag = false;
         }

         if (!flag) {
            flag = false;
            return flag;
         }
      }

      flag = true;
      return flag;
   }

   @Nullable
   public final ItemDisplay place(@NotNull Location location) {
      return this.place(location, location.getYaw(), BlockFace.UP, true);
   }

   @Nullable
   public final ItemDisplay place(@NotNull Location location, float yaw, @NotNull BlockFace facing) {
      return this.place(location, yaw, facing, true);
   }

   @Nullable
   public final ItemDisplay place(@NotNull Location location, float yaw, @NotNull BlockFace facing, boolean checkSpace) {
      FurnitureMechanic furnitureMechanic;
      if (!location.isWorldLoaded()) {
         return null;
      } else {
         Preconditions.checkNotNull(location.getWorld());
         PlacementType placementType2;
         if (this.placements != null) {
            int value = 0;
            placementType2 = PlacementType.Companion.fromBlockFace(facing);
         } else {
            placementType2 = null;
         }

         PlacementType placementType;
         block: {
            placementType = placementType2;
            FurniturePlacement furniturePlacement = this.placements;
            if (furniturePlacement != null) {
               FurniturePlacementMechanic furniturePlacementMechanic = furniturePlacement.getVariants().get(placementType);
               if (furniturePlacementMechanic != null) {
                  furnitureMechanic = furniturePlacementMechanic;
                  break block;
               }
            }

            furnitureMechanic = this;
         }

         FurnitureMechanic mechanic = furnitureMechanic;
         if (checkSpace && mechanic.notEnoughSpace(location, yaw)) {
            return null;
         } else {
            Entity entity = location.getWorld().spawn(mechanic.correctedSpawnLocation(location, facing), ItemDisplay.class, (ItemDisplay display) -> FurnitureMechanic.place$lambda$1(mechanic, yaw, facing, placementType, display));
            ItemDisplay baseEntity = (ItemDisplay)entity;
            if (mechanic.isModelEngine) {
               mechanic.spawnModelEngineFurniture(baseEntity);
            }

            ConnectableMechanic connectableMechanic = mechanic.connectable;
            if (connectableMechanic != null) {
               connectableMechanic.updateState(baseEntity);
            }

            connectableMechanic = mechanic.connectable;
            if (connectableMechanic != null) {
               connectableMechanic.updateSurrounding(baseEntity);
            }

            return baseEntity;
         }
      }
   }

   private final Location correctedSpawnLocation(Location baseLocation, BlockFace facing) {
      LimitedPlacing limitedPlacing2 = this.limitedPlacing;
      Boolean flag = limitedPlacing2 != null ? limitedPlacing2.isWall() : false;
      LimitedPlacing limitedPlacing3 = this.limitedPlacing;
      Pair pair = TuplesKt.to(flag, limitedPlacing3 != null ? limitedPlacing3.isRoof() : false);
      boolean isWall = (Boolean)pair.component1();
      boolean isRoof = (Boolean)pair.component2();
      Pair pair2 = TuplesKt.to(this.properties.isFixedTransform(), this.properties.isNoneTransform());
      boolean isFixed = (Boolean)pair2.component1();
      boolean isNone = (Boolean)pair2.component2();
      boolean solidBelow = baseLocation.getBlock().getRelative(BlockFace.DOWN).isSolid();
      Float number2 = this.hitbox.hitboxHeight() - (float)1;
      float it = ((Number)number2).floatValue();
      int value = 0;
      Float number = !isRoof || facing != BlockFace.DOWN ? number2 : null;
      float hitboxOffset = number != null ? number : -0.49F;
      Location location;
      if (!isFixed || facing != BlockFace.UP && (facing.getModY() != 0 || !solidBelow || isWall)) {
         location = baseLocation.toCenterLocation();
      } else {
         location = BlockHelpers.toCenterBlockLocation(baseLocation);
      }

      Location location2 = location;
      value = 0;
      if (isRoof && facing == BlockFace.DOWN) {
         location2.setY(location2.getY() + (double)(-hitboxOffset));
      }

      if (isNone && !isWall && !isRoof) {
         return location2;
      } else {
         if (isFixed && isWall && facing.getModY() == 0 && !solidBelow) {
            value = 0;
            double scale = 0.49 * (double)this.properties.getScale().y() * (double)2;
            location2.setX(location2.getX() + (double)(-facing.getModX()) * scale);
            location2.setZ(location2.getZ() + (double)(-facing.getModZ()) * scale);
         }

         return location2;
      }
   }

   private final void setBaseFurnitureData(ItemDisplay baseEntity, float yaw, BlockFace blockFace) {
      Component component;
      block: {
         baseEntity.setPersistent(true);
         baseEntity.setInvulnerable(true);
         baseEntity.setSilent(true);
         baseEntity.setCustomNameVisible(false);
         ItemBuilder item = NexoItems.itemFromId(this.getItemID());
         if (item != null) {
            component = item.getItemName();
            if (component != null) {
               break block;
            }
         }

         component = item != null ? item.getCustomName() : null;
         if (component == null) {
            TextComponent textComponent = Component.text(this.getItemID());
            component = (Component)textComponent;
         }
      }

      Component customName = component;
      baseEntity.customName(customName);
      LimitedPlacing limitedPlacing2 = this.limitedPlacing;
      Boolean flag = limitedPlacing2 != null ? limitedPlacing2.isWall() : false;
      LimitedPlacing limitedPlacing3 = this.limitedPlacing;
      Pair pair = TuplesKt.to(flag, limitedPlacing3 != null ? limitedPlacing3.isFloor() : false);
      boolean isWall = (Boolean)pair.component1();
      boolean isFloor = (Boolean)pair.component2();
      LimitedPlacing limitedPlacing4 = this.limitedPlacing;
      Pair pair2 = TuplesKt.to(limitedPlacing4 != null ? limitedPlacing4.isRoof() : false, this.properties.isFixedTransform());
      boolean isRoof = (Boolean)pair2.component1();
      boolean isFixed = (Boolean)pair2.component2();
      float yaw_1 = isWall && isFixed && blockFace.getModY() == 0 ? 90.0F * (float)blockFace.ordinal() - (float)180 : yaw;
      float pitch = isFixed && isFloor && blockFace == BlockFace.UP ? -90.0F : (isFixed && isFloor && !isWall && blockFace.getModY() == 0 ? -90.0F : (isFixed && isRoof && blockFace == BlockFace.DOWN ? 90.0F : 0.0F));
      baseEntity.setRotation(yaw_1, pitch);
      if (this.properties.getOffsetAgainstBlocks()) {
         this.correctFurnitureTranslation(baseEntity, blockFace);
      }

      PersistentDataContainer persistentDataContainer = baseEntity.getPersistentDataContainer();
      PersistentDataContainer pdc = persistentDataContainer;
      pdc.set(FURNITURE_KEY, PersistentDataType.STRING, this.getItemID());
      if (this.hasEvolution) {
         pdc.set(EVOLUTION_KEY, PersistentDataType.INTEGER, 0);
      }

   }

   private final void correctFurnitureTranslation(ItemDisplay baseEntity, BlockFace blockFace) {
      org.joml.Vector3f vector3f;
      Transformation transformation = baseEntity.getTransformation();
      int value = 0;
      Block relative = baseEntity.getLocation().getBlock().getRelative(blockFace.getOppositeFace());
      Block against = relative;
      if (!against.isReplaceable()) {
         BlockData blockData = against.getBlockData();
         TrapDoor trapDoor = blockData instanceof TrapDoor ? (TrapDoor)blockData : null;
         boolean flag;
         if ((blockData instanceof TrapDoor ? (TrapDoor)blockData : null) == null) {
            flag = false;
         } else {
            TrapDoor it = trapDoor;
            int value2 = 0;
            flag = !it.isOpen() && it.getHalf() == Half.TOP;
         }

         if (!flag) {
            block: {
               block2: {
                  ItemDisplay itemDisplay = Companion.baseEntity(against);
                  if (itemDisplay != null) {
                     Transformation transformation2 = itemDisplay.getTransformation();
                     if (transformation2 != null) {
                        vector3f = transformation2.getTranslation();
                        if (vector3f != null) {
                           break block2;
                        }
                     }
                  }

                  int value3 = 0;
                  BoundingBox boundingBox = against.getBoundingBox();
                  BoundingBox bb = boundingBox;
                  float value4;
                  switch (FurnitureMechanic.WhenMappings.$EnumSwitchMapping$0[blockFace.ordinal()]) {
                     case 1 -> value4 = (float)bb.getHeight() - (this.properties.isFixedTransform() ? 0.99F : 1.01F);
                     case 2 -> value4 = (float)bb.getHeight() - (this.properties.isFixedTransform() ? 0.99F : 0.0F);
                     default -> value4 = 0.0F;
                  }

                  float magnitude = value4;
                  double height = bb.getHeight();
                  if (!(0.01 <= height ? height <= 0.99 : false) || blockFace.getModY() == 0) {
                     break block;
                  }

                  Location location = baseEntity.getLocation();
                  if (bb.contains(LocationUtilsKt.plus(location, 0.0F, magnitude, 0.0F).toVector())) {
                     break block;
                  }

                  vector3f = this.properties.isFixedTransform() ? new Vector3f(0.0F, 0.0F, magnitude) : new Vector3f(0.0F, magnitude, 0.0F);
               }

               Vector3f offset = vector3f;
               transformation.getTranslation().set((Vector3fc)this.properties.getTranslation().add((Vector3fc)offset, new Vector3f()));
            }
         }
      }

      baseEntity.setTransformation(transformation);
   }

   public final void spawnModelEngineFurniture(@NotNull ItemDisplay entity) {
      Location location = entity.getLocation();
      int value = 0;
      location.setY(Math.floor(location.getY()));
      entity.teleport(location);
      ModeledEntity modelEntity = ModelEngineAPI.getOrCreateModeledEntity((Entity)entity);
      ActiveModel activeModel2 = modelEntity.getModels().get(this.modelEngineID);
      if (activeModel2 == null) {
         activeModel2 = ModelEngineAPI.createActiveModel(this.modelEngineID);
      }

      ActiveModel activeModel = activeModel2;
      modelEntity.removeModel(this.modelEngineID);
      modelEntity.addModel(activeModel, true);
      modelEntity.setModelRotationLocked(false);
      modelEntity.setBaseEntityVisible(false);
      modelEntity.setSaved(true);
   }

   public final void removeBaseEntity(@NotNull ItemDisplay baseEntity) {
      if (this.hasBeds) {
         FurnitureBed.Companion.removeBeds(baseEntity);
      }

      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      IFurniturePacketManager packetManager = (FurnitureFactory)mechanicFactory != null ? ((FurnitureFactory)mechanicFactory).packetManager() : null;
      if (packetManager != null) {
         packetManager.removeHitboxEntityPacket(baseEntity, this);
      }

      if (packetManager != null) {
         packetManager.removeBarrierHitboxPacket(baseEntity, this);
      }

      if (packetManager != null) {
         packetManager.removeLightMechanicPacket(baseEntity, this);
      }

      if (packetManager != null) {
         packetManager.removeTextEntityMechanicPacket(baseEntity, this);
      }

      if (!baseEntity.isDead()) {
         baseEntity.remove();
      }

      ConnectableMechanic connectableMechanic = this.connectable;
      if (connectableMechanic != null) {
         connectableMechanic.updateSurrounding(baseEntity);
      }

   }

   public final boolean notEnoughSpace(@NotNull ItemDisplay baseEntity, float yaw) {
      FurnitureHitbox furnitureHitbox = this.hitbox;
      Location location = baseEntity.getLocation();
      Iterable iterable = (Iterable)furnitureHitbox.hitboxLocations(location, yaw);
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         for(Object element : iterable) {
            Location it = (Location)element;
            int value = 0;
            Block block = it.getBlock();
            if (!BlockHelpers.isReplaceable(block, baseEntity.getUniqueId())) {
               flag = true;
               return flag;
            }
         }

         flag = false;
      }

      return flag;
   }

   public final boolean notEnoughSpace(@NotNull Location location, float yaw) {
      Iterable iterable = (Iterable)this.hitbox.hitboxLocations(location, yaw);
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         for(Object element : iterable) {
            Location it = (Location)element;
            int value = 0;
            Block block = it.getBlock();
            if (!BlockHelpers.isReplaceable(block, (UUID)null)) {
               flag = true;
               return flag;
            }
         }

         flag = false;
      }

      return flag;
   }

   public final void runClickActions(@NotNull Player player) {

      for(ClickAction action : this.clickActions) {
         if (action.canRun(player)) {
            action.performActions(player);
         }
      }

   }

   public final void rotateFurniture(@NotNull ItemDisplay baseEntity) {
      float newYaw = baseEntity.getYaw() + (this.restrictedRotation == FurnitureMechanic.RestrictedRotation.VERY_STRICT ? 45.0F : 22.5F);
      if (!this.notEnoughSpace(baseEntity, newYaw)) {
         baseEntity.setRotation(newYaw, baseEntity.getPitch());
         this.hitbox.refreshHitboxes(baseEntity, this);
         this.light.refreshLights(baseEntity, this);
         this.textEntities.refreshTextEntities(baseEntity, this);
         FurnitureSeat.Companion.updateSeats(baseEntity, this);
         FurnitureBed.Companion.updateBeds(baseEntity, this);
      }
   }

   @NotNull
   public final FurnitureMechanic placementMechanic(@NotNull ItemDisplay baseEntity) {
      if (this.placements == null) {
         return this;
      } else {
         PlacementType placementType = FurniturePlacement.Companion.placementType(baseEntity);
         if (placementType == null) {
            return this;
         } else {
            PlacementType type = placementType;
            FurniturePlacement furniturePlacement = this.placements;
            FurnitureMechanic furnitureMechanic = furniturePlacement.mechanicFor(type);
            if (furnitureMechanic == null) {
               furnitureMechanic = this;
            }

            return furnitureMechanic;
         }
      }
   }

   @NotNull
   public final FurnitureMechanic clone() {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      ConfigurationSection section = this.getSection();
      MechanicFactory mechanicFactory2 = mechanicFactory;
      return new FurnitureMechanic(mechanicFactory2, section);
   }

   private static final ItemBuilder _init_$lambda$0(ItemBuilder it) {
      NamespacedKey namespacedKey = FURNITURE_KEY;
      PersistentDataType persistentDataType = PersistentDataType.BYTE;
      return it.customTag(namespacedKey, persistentDataType, (byte)1);
   }

   private static final Unit place$lambda$1(FurnitureMechanic mechanic, float yaw, BlockFace facing, PlacementType placementType, ItemDisplay e) {
      mechanic.setBaseFurnitureData(e, yaw, facing);
      if (placementType == null) {
         return Unit.INSTANCE;
      } else {
         e.getPersistentDataContainer().set(FurniturePlacement.Companion.getPLACEMENT_KEY(), DataType.asEnum(PlacementType.class), placementType);
         return Unit.INSTANCE;
      }
   }

   private static final void place$lambda$2(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   private static final FurnitureMechanic INVALID_FURNITURE_delegate$lambda$0() {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      ConfigurationSection configurationSection = invalidFurnitureSection;
      MechanicFactory mechanicFactory2 = mechanicFactory;
      return new FurnitureMechanic(mechanicFactory2, configurationSection);
   }

   static {
      FURNITURE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "furniture");
      INVALID_FURNITURE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "invalid_furniture");
      DISPLAY_NAME_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "display_name");
      FURNITURE_DYE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "furniture_dye");
      FURNITURE_LIGHT_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "furniture_light");
      MODELENGINE_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "modelengine");
      EVOLUTION_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "evolution");
      ConfigurationSection configurationSection = (new YamlConfiguration()).createSection("invalid_furniture.Mechanics.furniture");
      invalidFurnitureSection = NexoYamlKt.copyFrom(NexoYamlKt.plus(NexoYamlKt.plus(configurationSection, "hitbox", NexoYamlKt.plus((ConfigurationSection)(new YamlConfiguration()), "interactions", CollectionsKt.listOf("0,0,0 1,1"))), "item_model", "minecraft:barrier"), Settings.INVALID_FURNITURE_ITEM.toConfigSection());
      INVALID_FURNITURE$delegate = LazyKt.lazy(FurnitureMechanic::INVALID_FURNITURE_delegate$lambda$0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u0000 \u00072\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$RestrictedRotation;", "", "<init>", "(Ljava/lang/String;I)V", "NONE", "STRICT", "VERY_STRICT", "Companion", "com.nexomc:core"}
   )
   public static enum RestrictedRotation {
      NONE,
      STRICT,
      VERY_STRICT;

      @NotNull
      public static final Companion Companion = new Companion();

      @NotNull
      public static EnumEntries<RestrictedRotation> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }

      @Metadata(
         mv = {2, 4, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
         d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$RestrictedRotation$Companion;", "", "<init>", "()V", "fromString", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$RestrictedRotation;", "string", "", "com.nexomc:core"}
      )
      public static final class Companion {
         private Companion() {
         }

         @NotNull
         public final RestrictedRotation fromString(@Nullable String string) {
            Iterable entries = (Iterable)FurnitureMechanic.RestrictedRotation.getEntries();
            Iterator iterator = entries.iterator();

            Object obj;
            while(true) {
               if (iterator.hasNext()) {
                  Object element = iterator.next();
                  RestrictedRotation it = (RestrictedRotation)element;
                  int value = 0;
                  if (!StringsKt.equals(it.name(), string, true)) {
                     continue;
                  }

                  obj = element;
                  break;
               }

               obj = null;
               break;
            }

            RestrictedRotation restrictedRotation2 = (RestrictedRotation)obj;
            if (restrictedRotation2 == null) {
               RestrictedRotation restrictedRotation3 = FurnitureMechanic.RestrictedRotation.STRICT;
               int value2 = 0;
               Logs.logError("Invalid restricted rotation: " + string, false);
               Logs.logError("Allowed ones are: " + CollectionsKt.joinToString((Iterable)FurnitureMechanic.RestrictedRotation.getEntries(), (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), false);
               Logs.logWarn("Setting to " + restrictedRotation3, false);
               restrictedRotation2 = restrictedRotation3;
            }

            return restrictedRotation2;
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019J\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u001a\u001a\u00020\u001bJ\u0010\u0010$\u001a\u0004\u0018\u00010\u001f2\u0006\u0010\u0014\u001a\u00020\u0015R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007R\u0011\u0010\n\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0007R\u0011\u0010\f\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u0007R\u0011\u0010\u000e\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0007R\u0011\u0010\u0010\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0007R\u0011\u0010\u0012\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0007R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u001e\u001a\u00020\u001f8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\"\u0010#\u001a\u0004\b \u0010!¨\u0006%"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic$Companion;", "", "<init>", "()V", "FURNITURE_KEY", "Lorg/bukkit/NamespacedKey;", "getFURNITURE_KEY", "()Lorg/bukkit/NamespacedKey;", "INVALID_FURNITURE_KEY", "getINVALID_FURNITURE_KEY", "DISPLAY_NAME_KEY", "getDISPLAY_NAME_KEY", "FURNITURE_DYE_KEY", "getFURNITURE_DYE_KEY", "FURNITURE_LIGHT_KEY", "getFURNITURE_LIGHT_KEY", "MODELENGINE_KEY", "getMODELENGINE_KEY", "EVOLUTION_KEY", "getEVOLUTION_KEY", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "block", "Lorg/bukkit/block/Block;", "location", "Lorg/bukkit/Location;", "interactionId", "", "invalidFurnitureSection", "Lorg/bukkit/configuration/ConfigurationSection;", "INVALID_FURNITURE", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getINVALID_FURNITURE", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "INVALID_FURNITURE$delegate", "Lkotlin/Lazy;", "invalidFurniture", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getFURNITURE_KEY() {
         return FurnitureMechanic.FURNITURE_KEY;
      }

      @NotNull
      public final NamespacedKey getINVALID_FURNITURE_KEY() {
         return FurnitureMechanic.INVALID_FURNITURE_KEY;
      }

      @NotNull
      public final NamespacedKey getDISPLAY_NAME_KEY() {
         return FurnitureMechanic.DISPLAY_NAME_KEY;
      }

      @NotNull
      public final NamespacedKey getFURNITURE_DYE_KEY() {
         return FurnitureMechanic.FURNITURE_DYE_KEY;
      }

      @NotNull
      public final NamespacedKey getFURNITURE_LIGHT_KEY() {
         return FurnitureMechanic.FURNITURE_LIGHT_KEY;
      }

      @NotNull
      public final NamespacedKey getMODELENGINE_KEY() {
         return FurnitureMechanic.MODELENGINE_KEY;
      }

      @NotNull
      public final NamespacedKey getEVOLUTION_KEY() {
         return FurnitureMechanic.EVOLUTION_KEY;
      }

      @Nullable
      public final ItemDisplay baseEntity(@Nullable Block block) {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         if (block != null) {
            Location location = block.getLocation();
            if (location != null) {
               return companion.baseEntityFromHitbox(location);
            }
         }

         return null;
      }

      @Nullable
      public final ItemDisplay baseEntity(@Nullable Location location) {
         return location == null ? null : IFurniturePacketManager.Companion.baseEntityFromHitbox(location);
      }

      @Nullable
      public final ItemDisplay baseEntity(int interactionId) {
         return IFurniturePacketManager.Companion.baseEntityFromHitbox(interactionId);
      }

      @NotNull
      public final FurnitureMechanic getINVALID_FURNITURE() {
         Lazy lazy = FurnitureMechanic.INVALID_FURNITURE$delegate;
         return (FurnitureMechanic)lazy.getValue();
      }

      @Nullable
      public final FurnitureMechanic invalidFurniture(@NotNull ItemDisplay baseEntity) {
         return !baseEntity.getPersistentDataContainer().has(this.getINVALID_FURNITURE_KEY()) ? null : this.getINVALID_FURNITURE();
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.block.BlockFace.values().length];
         try { a_0[org.bukkit.block.BlockFace.UP.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.block.BlockFace.DOWN.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
