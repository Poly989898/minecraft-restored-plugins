package com.nexomc.nexo.items;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.compatibilities.mmoitems.WrappedMMOItem;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.nms.IItemUtils;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.Colorable;
import com.nexomc.nexo.utils.ColorableKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import com.nexomc.nexo.utils.wrappers.AttributeWrapper;
import com.nexomc.nexo.utils.wrappers.AttributeWrappers;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.JukeboxPlayable;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.datacomponent.item.UseEffects;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000â\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b1\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u001e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u0000 ø\u00022\u00020\u0001:\u0002ø\u0002B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0011\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0004\u0010\u000bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\f¢\u0006\u0004\b\u0004\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u0000¢\u0006\u0004\b\u0004\u0010\u000fJ\u000f\u0010î\u0001\u001a\u00020\u00002\u0006\u0010)\u001a\u00020\u0007J\u000f\u0010ï\u0001\u001a\u00020\u00002\u0006\u0010,\u001a\u00020\"J\u0010\u0010M\u001a\u00020\u00002\b\u0010M\u001a\u0004\u0018\u00010LJ%\u0010ð\u0001\u001a\u00020\u00002\t\u0010ð\u0001\u001a\u0004\u0018\u00010LH\u0007b\u000f\bñ\u0001\u0012\n\bò\u0001\u0012\u0005\b\b(ó\u0001J\u0007\u0010ô\u0001\u001a\u00020DJ\u0010\u0010n\u001a\u00020\u00002\b\u0010n\u001a\u0004\u0018\u00010LJ\u0007\u0010õ\u0001\u001a\u00020DJ\f\u0010P\u001a\b\u0012\u0004\u0012\u00020L0?J\u0016\u0010P\u001a\u00020\u00002\u000e\u0010P\u001a\n\u0012\u0004\u0012\u00020L\u0018\u00010?J!\u0010P\u001a\u00020\u00002\u0013\u0010P\u001a\u000b\u0012\u0006\b\u0001\u0012\u00020L0ö\u0001\"\u00020L¢\u0006\u0003\u0010÷\u0001J\u000f\u0010ø\u0001\u001a\u00020\u00002\u0006\u0010E\u001a\u00020DJ\u0017\u0010ù\u0001\u001a\u00020\u00002\b\u0010t\u001a\u0004\u0018\u00010\"¢\u0006\u0003\u0010ú\u0001J\u0007\u0010û\u0001\u001a\u00020DJ\u0011\u0010ü\u0001\u001a\u00020\u00002\b\u00100\u001a\u0004\u0018\u00010/J\u0007\u0010ý\u0001\u001a\u00020DJ\u0012\u0010\u0084\u0002\u001a\u00020\u00002\t\u0010\u0085\u0002\u001a\u0004\u0018\u00010@J\u0007\u0010\u0086\u0002\u001a\u00020DJ\u0012\u0010\u0089\u0002\u001a\u00020\u00002\t\u0010\u0085\u0002\u001a\u0004\u0018\u00010@J\u0012\u0010\u008a\u0002\u001a\u00020\u00002\t\u0010\u008b\u0002\u001a\u0004\u0018\u00010\u0001J\u0012\u0010\u008c\u0002\u001a\u00020\u00002\t\u0010\u008b\u0002\u001a\u0004\u0018\u00010\u0001J\u0007\u0010\u008d\u0002\u001a\u00020DJ\u0012\u0010\u008e\u0002\u001a\u00020\u00002\t\u0010\u0096\u0001\u001a\u0004\u0018\u00010@J\u0007\u0010\u008f\u0002\u001a\u00020DJ\u001d\u0010\u0090\u0002\u001a\u00020\u00002\u0014\u0010\u0099\u0001\u001a\u000f\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120\u0098\u0001J\u0007\u0010\u0091\u0002\u001a\u00020DJ\u0012\u0010\u0092\u0002\u001a\u00020\u00002\t\u0010\u0093\u0001\u001a\u0004\u0018\u00010@J\u0007\u0010\u0093\u0002\u001a\u00020DJ\u0013\u0010\u0094\u0002\u001a\u00020\u00002\n\u0010´\u0001\u001a\u0005\u0018\u00010³\u0001J\u0007\u0010\u0095\u0002\u001a\u00020DJ\u0011\u0010\u0096\u0002\u001a\u00020\u00002\b\u0010{\u001a\u0004\u0018\u00010@J\u0007\u0010\u0097\u0002\u001a\u00020DJ\u0013\u0010\u0098\u0002\u001a\u00020\u00002\n\u0010\u009c\u0001\u001a\u0005\u0018\u00010\u009b\u0001J\u0007\u0010\u0099\u0002\u001a\u00020DJ\u0013\u0010\u009a\u0002\u001a\u00020\u00002\n\u0010\u0090\u0001\u001a\u0005\u0018\u00010\u008f\u0001J\u0010\u0010\u009b\u0002\u001a\u00020\u00002\u0007\u0010\u009c\u0002\u001a\u00020DJ\u0007\u0010\u009d\u0002\u001a\u00020DJ\u0013\u0010\u009e\u0002\u001a\u00020\u00002\n\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008b\u0001J\u0007\u0010\u009f\u0002\u001a\u00020DJ\u0013\u0010 \u0002\u001a\u00020\u00002\n\u0010\u0088\u0001\u001a\u0005\u0018\u00010\u0087\u0001J\u0007\u0010¡\u0002\u001a\u00020DJ\u0013\u0010¢\u0002\u001a\u00020\u00002\n\u0010\u0083\u0001\u001a\u0005\u0018\u00010\u0082\u0001J\u0007\u0010£\u0002\u001a\u00020DJ\u0011\u0010¤\u0002\u001a\u00020\u00002\b\u0010_\u001a\u0004\u0018\u00010^J\u0007\u0010¥\u0002\u001a\u00020DJ\u0012\u0010¦\u0002\u001a\u00020\u00002\t\u0010\u009f\u0001\u001a\u0004\u0018\u00010\u0001J\u0007\u0010§\u0002\u001a\u00020DJ\u0012\u0010¨\u0002\u001a\u00020\u00002\t\u0010¡\u0001\u001a\u0004\u0018\u00010\u0001J\u0007\u0010©\u0002\u001a\u00020DJ\u0012\u0010ª\u0002\u001a\u00020\u00002\t\u0010£\u0001\u001a\u0004\u0018\u00010\u0001J\u0007\u0010«\u0002\u001a\u00020DJ\u0011\u0010¬\u0002\u001a\u00020\u00002\b\u0010c\u001a\u0004\u0018\u00010bJ\u0007\u0010\u00ad\u0002\u001a\u00020DJ\u0013\u0010®\u0002\u001a\u00020\u00002\n\u0010¯\u0002\u001a\u0005\u0018\u00010·\u0001J\u0007\u0010°\u0002\u001a\u00020DJ\u0013\u0010±\u0002\u001a\u00020\u00002\n\u0010²\u0002\u001a\u0005\u0018\u00010»\u0001J\u0011\u0010³\u0002\u001a\u00020\u00002\b\u0010w\u001a\u0004\u0018\u00010vJ\u0013\u0010³\u0002\u001a\u00020\u00002\n\u0010´\u0002\u001a\u0005\u0018\u00010µ\u0002J\u0007\u0010¶\u0002\u001a\u00020DJ\u0010\u0010·\u0002\u001a\u00020\u00002\u0007\u0010¸\u0002\u001a\u00020fJN\u0010·\u0002\u001a\u00020\u00002\t\u0010¸\u0002\u001a\u0004\u0018\u00010DH\u0007b2\bñ\u0001\u0012\n\bò\u0001\u0012\u0005\b\b(º\u0002\u0012!\b»\u0002\u0012\u001c\b\u000bB\u0018\b¼\u0002\u0012\n\b½\u0002\u0012\u0005\b\b(¾\u0002\u0012\u0007\b¿\u0002\u0012\u0002\b\f¢\u0006\u0003\u0010¹\u0002J\u0007\u0010À\u0002\u001a\u00020DJ\u0011\u0010Á\u0002\u001a\u00020\u00002\b\u0010q\u001a\u0004\u0018\u00010pJ\u0007\u0010Â\u0002\u001a\u00020DJ\u000e\u0010j\u001a\u00020\u00002\u0006\u0010j\u001a\u00020\"J\u0010\u0010\u007f\u001a\u00020\u00002\b\u0010\u007f\u001a\u0004\u0018\u00010~J\n\u0010Ã\u0002\u001a\u00030Ä\u0002H\u0002J\u0013\u0010Å\u0002\u001a\u00020\u00002\n\u0010Æ\u0002\u001a\u0005\u0018\u00010Ç\u0002J\u0011\u0010È\u0002\u001a\u00020\u00002\b\u0010É\u0002\u001a\u00030Ê\u0002J\u0007\u0010Ë\u0002\u001a\u00020DJK\u0010Ì\u0002\u001a\u00020\u0000\"\u0005\b\u0000\u0010Í\u0002\"\t\b\u0001\u0010Î\u0002*\u00020\u00012\b\u0010Ï\u0002\u001a\u00030\u0082\u00022\u0016\u0010Ð\u0002\u001a\u0011\u0012\u0005\u0012\u0003HÍ\u0002\u0012\u0005\u0012\u0003HÎ\u00020Ñ\u00022\b\u0010Ò\u0002\u001a\u0003HÎ\u0002¢\u0006\u0003\u0010Ó\u0002J@\u0010Ì\u0002\u001a\u0005\u0018\u0001HÎ\u0002\"\u0005\b\u0000\u0010Í\u0002\"\u0005\b\u0001\u0010Î\u00022\b\u0010Ï\u0002\u001a\u00030\u0082\u00022\u0016\u0010Ð\u0002\u001a\u0011\u0012\u0005\u0012\u0003HÍ\u0002\u0012\u0005\u0012\u0003HÎ\u00020Ñ\u0002¢\u0006\u0003\u0010Ô\u0002J\u0011\u0010Õ\u0002\u001a\u00020\u00002\b\u0010Ö\u0002\u001a\u00030\u0082\u0002J\u0007\u0010×\u0002\u001a\u00020DJ\u0010\u0010¦\u0001\u001a\u00020\u00002\u0007\u0010¦\u0001\u001a\u00020\"J\u0013\u0010Ø\u0002\u001a\u00020\u00002\n\u0010¦\u0001\u001a\u0005\u0018\u00010¥\u0001J\u0016\u0010Ù\u0002\u001a\u00020\u00002\r\u0010Ú\u0002\u001a\b\u0012\u0004\u0012\u00020@0?J#\u0010Û\u0002\u001a\u00020\u00002\u0014\u0010Ú\u0002\u001a\u000b\u0012\u0006\b\u0001\u0012\u00020@0ö\u0001\"\u00020@¢\u0006\u0003\u0010Ü\u0002J\r\u0010Ý\u0002\u001a\b\u0012\u0004\u0012\u00020@0\u0019J\u0011\u0010Þ\u0002\u001a\u00020\u00002\b\u0010ß\u0002\u001a\u00030à\u0002J\u0019\u0010á\u0002\u001a\u00020\u00002\u0007\u0010â\u0002\u001a\u00020!2\u0007\u0010ã\u0002\u001a\u00020\"J\u001d\u0010ä\u0002\u001a\u00020\u00002\u0014\u0010å\u0002\u001a\u000f\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0\u0098\u0001J\u0010\u0010æ\u0002\u001a\u00020D2\u0007\u0010ç\u0002\u001a\u00020\u0012J\u0010\u0010è\u0002\u001a\u00020\u00002\u0007\u0010ç\u0002\u001a\u00020\u0012J#\u0010é\u0002\u001a\u00020\u00002\u0014\u0010ê\u0002\u001a\u000b\u0012\u0006\b\u0001\u0012\u00020\u00120ö\u0001\"\u00020\u0012¢\u0006\u0003\u0010ë\u0002J\u0017\u0010é\u0002\u001a\u00020\u00002\u000e\u0010ê\u0002\u001a\t\u0012\u0004\u0012\u00020\u00120ì\u0002J\u0007\u0010í\u0002\u001a\u00020DJ\u000f\u0010&\u001a\u00020\u00002\u0007\u0010î\u0002\u001a\u00020%J\r\u0010ï\u0002\u001a\b0\u0003¢\u0006\u0003\bð\u0002J\u0007\u0010ñ\u0002\u001a\u00020\u0000J\u0007\u0010ò\u0002\u001a\u00020\u0000J\b\u0010ó\u0002\u001a\u00030ô\u0002J\u001e\u0010õ\u0002\u001a\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u00030ö\u00012\u0006\u0010,\u001a\u00020\"¢\u0006\u0003\u0010ö\u0002J\u0007\u0010÷\u0002\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001f\u0010\u0010\u001a\u0010\u0012\u0004\u0012\u00020\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R%\u0010\u0015\u001a\u0016\u0012\f\u0012\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u0016\u0012\u0004\u0012\u00020\u00010\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00120\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u001c\u001a\u00020\u001d¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u001d\u0010 \u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0\u0011¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0014R\u001e\u0010&\u001a\u00020%2\u0006\u0010$\u001a\u00020%@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u001e\u0010)\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u0007@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u001e\u0010,\u001a\u00020\"2\u0006\u0010$\u001a\u00020\"@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b-\u0010.R\"\u00100\u001a\u0004\u0018\u00010/2\b\u0010$\u001a\u0004\u0018\u00010/@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b1\u00102R\"\u00104\u001a\u0004\u0018\u0001032\b\u0010$\u001a\u0004\u0018\u000103@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b5\u00106R\"\u00108\u001a\u0004\u0018\u0001072\b\u0010$\u001a\u0004\u0018\u000107@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b9\u0010:R\"\u0010<\u001a\u0004\u0018\u00010;2\b\u0010$\u001a\u0004\u0018\u00010;@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b=\u0010>R*\u0010A\u001a\b\u0012\u0004\u0012\u00020@0?2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020@0?@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bB\u0010CR\u001e\u0010E\u001a\u00020D2\u0006\u0010$\u001a\u00020D@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bF\u0010GR\u001e\u0010I\u001a\u00020H2\u0006\u0010$\u001a\u00020H@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bJ\u0010KR\"\u0010M\u001a\u0004\u0018\u00010L2\b\u0010$\u001a\u0004\u0018\u00010L@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bN\u0010OR.\u0010P\u001a\n\u0012\u0004\u0012\u00020L\u0018\u00010?2\u000e\u0010$\u001a\n\u0012\u0004\u0012\u00020L\u0018\u00010?@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bQ\u0010CR\"\u0010R\u001a\u0004\u0018\u00010\u00032\b\u0010$\u001a\u0004\u0018\u00010\u0003@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bS\u0010TR0\u0010U\u001a\b\u0012\u0004\u0012\u00020@0?2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020@0?@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bV\u0010C\"\u0004\bW\u0010XR\"\u0010Y\u001a\u0004\u0018\u00010\u00012\b\u0010$\u001a\u0004\u0018\u00010\u0001@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bZ\u0010[R\"\u0010\\\u001a\u0004\u0018\u00010\u00012\b\u0010$\u001a\u0004\u0018\u00010\u0001@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b]\u0010[R\"\u0010_\u001a\u0004\u0018\u00010^2\b\u0010$\u001a\u0004\u0018\u00010^@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b`\u0010aR\"\u0010c\u001a\u0004\u0018\u00010b2\b\u0010$\u001a\u0004\u0018\u00010b@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bd\u0010eR\u001e\u0010g\u001a\u00020f2\u0006\u0010$\u001a\u00020f@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bh\u0010iR$\u0010j\u001a\u0004\u0018\u00010\"2\b\u0010$\u001a\u0004\u0018\u00010\"@BX\u0086\u000e¢\u0006\n\n\u0002\u0010m\u001a\u0004\bk\u0010lR\"\u0010n\u001a\u0004\u0018\u00010L2\b\u0010$\u001a\u0004\u0018\u00010L@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bo\u0010OR\"\u0010q\u001a\u0004\u0018\u00010p2\b\u0010$\u001a\u0004\u0018\u00010p@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\br\u0010sR$\u0010t\u001a\u0004\u0018\u00010\"2\b\u0010$\u001a\u0004\u0018\u00010\"@BX\u0086\u000e¢\u0006\n\n\u0002\u0010m\u001a\u0004\bu\u0010lR\"\u0010w\u001a\u0004\u0018\u00010v2\b\u0010$\u001a\u0004\u0018\u00010v@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\bx\u0010yR\"\u0010{\u001a\u0004\u0018\u00010z2\b\u0010$\u001a\u0004\u0018\u00010z@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b|\u0010}R$\u0010\u007f\u001a\u0004\u0018\u00010~2\b\u0010$\u001a\u0004\u0018\u00010~@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0080\u0001\u0010\u0081\u0001R'\u0010\u0083\u0001\u001a\u0005\u0018\u00010\u0082\u00012\t\u0010$\u001a\u0005\u0018\u00010\u0082\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0084\u0001\u0010\u0085\u0001R \u0010\u0086\u0001\u001a\u00020f2\u0006\u0010$\u001a\u00020f@BX\u0086\u000e¢\u0006\t\n\u0000\u001a\u0005\b\u0086\u0001\u0010iR'\u0010\u0088\u0001\u001a\u0005\u0018\u00010\u0087\u00012\t\u0010$\u001a\u0005\u0018\u00010\u0087\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0089\u0001\u0010\u008a\u0001R'\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008b\u00012\t\u0010$\u001a\u0005\u0018\u00010\u008b\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u008d\u0001\u0010\u008e\u0001R'\u0010\u0090\u0001\u001a\u0005\u0018\u00010\u008f\u00012\t\u0010$\u001a\u0005\u0018\u00010\u008f\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0091\u0001\u0010\u0092\u0001R%\u0010\u0093\u0001\u001a\u0004\u0018\u00010@2\b\u0010$\u001a\u0004\u0018\u00010@@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0094\u0001\u0010\u0095\u0001R%\u0010\u0096\u0001\u001a\u0004\u0018\u00010@2\b\u0010$\u001a\u0004\u0018\u00010@@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u0097\u0001\u0010\u0095\u0001R>\u0010\u0099\u0001\u001a\u0011\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0098\u00012\u0015\u0010$\u001a\u0011\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0098\u0001@BX\u0086\u000e¢\u0006\t\n\u0000\u001a\u0005\b\u009a\u0001\u0010\u0014R'\u0010\u009c\u0001\u001a\u0005\u0018\u00010\u009b\u00012\t\u0010$\u001a\u0005\u0018\u00010\u009b\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b\u009d\u0001\u0010\u009e\u0001R$\u0010\u009f\u0001\u001a\u0004\u0018\u00010\u00012\b\u0010$\u001a\u0004\u0018\u00010\u0001@BX\u0086\u000e¢\u0006\t\n\u0000\u001a\u0005\b \u0001\u0010[R$\u0010¡\u0001\u001a\u0004\u0018\u00010\u00012\b\u0010$\u001a\u0004\u0018\u00010\u0001@BX\u0086\u000e¢\u0006\t\n\u0000\u001a\u0005\b¢\u0001\u0010[R$\u0010£\u0001\u001a\u0004\u0018\u00010\u00012\b\u0010$\u001a\u0004\u0018\u00010\u0001@BX\u0086\u000e¢\u0006\t\n\u0000\u001a\u0005\b¤\u0001\u0010[R'\u0010¦\u0001\u001a\u0005\u0018\u00010¥\u00012\t\u0010$\u001a\u0005\u0018\u00010¥\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b§\u0001\u0010¨\u0001R/\u0010ª\u0001\u001a\u0005\u0018\u00010©\u00012\t\u0010$\u001a\u0005\u0018\u00010©\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\b«\u0001\u0010¬\u0001\"\u0006\b\u00ad\u0001\u0010®\u0001R\u001e\u0010¯\u0001\u001a\u00020DX\u0086\u000e¢\u0006\u0011\n\u0000\u001a\u0005\b°\u0001\u0010G\"\u0006\b±\u0001\u0010²\u0001R'\u0010´\u0001\u001a\u0005\u0018\u00010³\u00012\t\u0010$\u001a\u0005\u0018\u00010³\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\bµ\u0001\u0010¶\u0001R'\u0010¸\u0001\u001a\u0005\u0018\u00010·\u00012\t\u0010$\u001a\u0005\u0018\u00010·\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b¹\u0001\u0010º\u0001R'\u0010¼\u0001\u001a\u0005\u0018\u00010»\u00012\t\u0010$\u001a\u0005\u0018\u00010»\u0001@BX\u0086\u000e¢\u0006\n\n\u0000\u001a\u0006\b½\u0001\u0010¾\u0001R-\u0010¿\u0001\u001a\u0004\u0018\u00010@2\b\u0010$\u001a\u0004\u0018\u00010@@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bÀ\u0001\u0010\u0095\u0001\"\u0006\bÁ\u0001\u0010Â\u0001R/\u0010Ä\u0001\u001a\u0005\u0018\u00010Ã\u00012\t\u0010$\u001a\u0005\u0018\u00010Ã\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bÅ\u0001\u0010Æ\u0001\"\u0006\bÇ\u0001\u0010È\u0001R/\u0010Ê\u0001\u001a\u0005\u0018\u00010É\u00012\t\u0010$\u001a\u0005\u0018\u00010É\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bË\u0001\u0010Ì\u0001\"\u0006\bÍ\u0001\u0010Î\u0001R/\u0010Ð\u0001\u001a\u0005\u0018\u00010Ï\u00012\t\u0010$\u001a\u0005\u0018\u00010Ï\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bÑ\u0001\u0010Ò\u0001\"\u0006\bÓ\u0001\u0010Ô\u0001R2\u0010Ö\u0001\u001a\u0005\u0018\u00010Õ\u00012\t\u0010$\u001a\u0005\u0018\u00010Õ\u0001@@X\u0086\u000e¢\u0006\u0015\n\u0003\u0010Û\u0001\u001a\u0006\b×\u0001\u0010Ø\u0001\"\u0006\bÙ\u0001\u0010Ú\u0001R/\u0010Ý\u0001\u001a\u0005\u0018\u00010Ü\u00012\t\u0010$\u001a\u0005\u0018\u00010Ü\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bÞ\u0001\u0010ß\u0001\"\u0006\bà\u0001\u0010á\u0001R/\u0010ã\u0001\u001a\u0005\u0018\u00010â\u00012\t\u0010$\u001a\u0005\u0018\u00010â\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bä\u0001\u0010å\u0001\"\u0006\bæ\u0001\u0010ç\u0001R/\u0010é\u0001\u001a\u0005\u0018\u00010è\u00012\t\u0010$\u001a\u0005\u0018\u00010è\u0001@@X\u0086\u000e¢\u0006\u0012\n\u0000\u001a\u0006\bê\u0001\u0010ë\u0001\"\u0006\bì\u0001\u0010í\u0001R\u001c\u0010þ\u0001\u001a\u000f\u0012\n\u0012\b03¢\u0006\u0003\b\u0080\u00020ÿ\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0081\u0002\u001a\u000b\u0018\u00010\u0082\u0002¢\u0006\u0003\b\u0083\u0002X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0087\u0002\u001a\u000f\u0012\n\u0012\b07¢\u0006\u0003\b\u0080\u00020ÿ\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0088\u0002\u001a\u000b\u0018\u00010\u0082\u0002¢\u0006\u0003\b\u0083\u0002X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006ù\u0002"},
   d2 = {"Lcom/nexomc/nexo/items/ItemBuilder;", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "<init>", "(Lorg/bukkit/inventory/ItemStack;)V", "material", "Lorg/bukkit/Material;", "(Lorg/bukkit/Material;)V", "wrapped", "Lcom/nexomc/nexo/compatibilities/mmoitems/WrappedMMOItem;", "(Lcom/nexomc/nexo/compatibilities/mmoitems/WrappedMMOItem;)V", "Lcom/nexomc/nexo/compatibilities/mythiccrucible/WrappedCrucibleItem;", "(Lcom/nexomc/nexo/compatibilities/mythiccrucible/WrappedCrucibleItem;)V", "itemBuilder", "(Lcom/nexomc/nexo/items/ItemBuilder;)V", "customDataMap", "", "", "getCustomDataMap", "()Ljava/util/Map;", "persistentDataMap", "Lcom/nexomc/nexo/items/ItemBuilder$Companion$PersistentDataSpace;", "getPersistentDataMap", "nexoTags", "", "getNexoTags", "()Ljava/util/Set;", "persistentDataContainer", "Lorg/bukkit/persistence/PersistentDataContainer;", "getPersistentDataContainer", "()Lorg/bukkit/persistence/PersistentDataContainer;", "enchantments", "Lorg/bukkit/enchantments/Enchantment;", "", "getEnchantments", "value", "Lcom/nexomc/nexo/items/NexoMeta;", "nexoMeta", "getNexoMeta", "()Lcom/nexomc/nexo/items/NexoMeta;", "type", "getType", "()Lorg/bukkit/Material;", "amount", "getAmount", "()I", "Lorg/bukkit/Color;", "color", "getColor", "()Lorg/bukkit/Color;", "Lorg/bukkit/inventory/meta/trim/TrimPattern;", "trimPattern", "getTrimPattern", "()Lorg/bukkit/inventory/meta/trim/TrimPattern;", "Lorg/bukkit/inventory/meta/trim/TrimMaterial;", "trimMaterial", "getTrimMaterial", "()Lorg/bukkit/inventory/meta/trim/TrimMaterial;", "Lio/papermc/paper/datacomponent/item/PotionContents;", "potionContents", "getPotionContents", "()Lio/papermc/paper/datacomponent/item/PotionContents;", "", "Lnet/kyori/adventure/key/Key;", "hiddenComponents", "getHiddenComponents", "()Ljava/util/List;", "", "unbreakable", "getUnbreakable", "()Z", "Lcom/nexomc/nexo/utils/wrappers/AttributeWrappers;", "attributeModifiers", "getAttributeModifiers", "()Lcom/nexomc/nexo/utils/wrappers/AttributeWrappers;", "Lnet/kyori/adventure/text/Component;", "customName", "getCustomName", "()Lnet/kyori/adventure/text/Component;", "lore", "getLore", "finalItemStack", "getFinalItemStack", "()Lorg/bukkit/inventory/ItemStack;", "unsetComponents", "getUnsetComponents", "setUnsetComponents$com_nexomc_core", "(Ljava/util/List;)V", "canPlaceOn", "getCanPlaceOn", "()Ljava/lang/Object;", "canBreak", "getCanBreak", "Lio/papermc/paper/datacomponent/item/FoodProperties;", "foodComponent", "getFoodComponent", "()Lio/papermc/paper/datacomponent/item/FoodProperties;", "Lio/papermc/paper/datacomponent/item/Tool;", "toolComponent", "getToolComponent", "()Lio/papermc/paper/datacomponent/item/Tool;", "Lcom/nexomc/nexo/utils/TriState;", "enchantmentGlintOverride", "getEnchantmentGlintOverride", "()Lcom/nexomc/nexo/utils/TriState;", "maxStackSize", "getMaxStackSize", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "itemName", "getItemName", "Lorg/bukkit/inventory/ItemRarity;", "rarity", "getRarity", "()Lorg/bukkit/inventory/ItemRarity;", "maxDamage", "getMaxDamage", "Lio/papermc/paper/datacomponent/item/JukeboxPlayable;", "jukeboxPlayable", "getJukeboxPlayable", "()Lio/papermc/paper/datacomponent/item/JukeboxPlayable;", "Lorg/bukkit/Art;", "paintingVariant", "getPaintingVariant", "()Lorg/bukkit/Art;", "Lorg/bukkit/MusicInstrument;", "instrument", "getInstrument", "()Lorg/bukkit/MusicInstrument;", "Lio/papermc/paper/datacomponent/item/Equippable;", "equippable", "getEquippable", "()Lio/papermc/paper/datacomponent/item/Equippable;", "isGlider", "Lio/papermc/paper/datacomponent/item/UseCooldown;", "useCooldown", "getUseCooldown", "()Lio/papermc/paper/datacomponent/item/UseCooldown;", "Lio/papermc/paper/datacomponent/item/UseRemainder;", "useRemainder", "getUseRemainder", "()Lio/papermc/paper/datacomponent/item/UseRemainder;", "Lio/papermc/paper/datacomponent/item/DamageResistant;", "damageResistant", "getDamageResistant", "()Lio/papermc/paper/datacomponent/item/DamageResistant;", "tooltipStyle", "getTooltipStyle", "()Lnet/kyori/adventure/key/Key;", "itemModel", "getItemModel", "", "blockStates", "getBlockStates", "Lio/papermc/paper/datacomponent/item/Enchantable;", "enchantable", "getEnchantable", "()Lio/papermc/paper/datacomponent/item/Enchantable;", "consumableComponent", "getConsumableComponent", "deathProtectionComponent", "getDeathProtectionComponent", "repairableComponent", "getRepairableComponent", "Lio/papermc/paper/datacomponent/item/CustomModelData;", "customModelData", "getCustomModelData", "()Lio/papermc/paper/datacomponent/item/CustomModelData;", "Lio/papermc/paper/datacomponent/item/ResolvableProfile;", "profile", "getProfile", "()Lio/papermc/paper/datacomponent/item/ResolvableProfile;", "setProfile$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/ResolvableProfile;)V", "cache", "getCache", "setCache", "(Z)V", "Lio/papermc/paper/datacomponent/item/TooltipDisplay;", "tooltipDisplay", "getTooltipDisplay", "()Lio/papermc/paper/datacomponent/item/TooltipDisplay;", "Lio/papermc/paper/datacomponent/item/Weapon;", "weapon", "getWeapon", "()Lio/papermc/paper/datacomponent/item/Weapon;", "Lio/papermc/paper/datacomponent/item/BlocksAttacks;", "blocksAttacks", "getBlocksAttacks", "()Lio/papermc/paper/datacomponent/item/BlocksAttacks;", "breakSound", "getBreakSound", "setBreakSound$com_nexomc_core", "(Lnet/kyori/adventure/key/Key;)V", "Lio/papermc/paper/datacomponent/item/AttackRange;", "attackRange", "getAttackRange", "()Lio/papermc/paper/datacomponent/item/AttackRange;", "setAttackRange$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/AttackRange;)V", "Lio/papermc/paper/datacomponent/item/KineticWeapon;", "kineticWeapon", "getKineticWeapon", "()Lio/papermc/paper/datacomponent/item/KineticWeapon;", "setKineticWeapon$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/KineticWeapon;)V", "Lio/papermc/paper/datacomponent/item/PiercingWeapon;", "piercingWeapon", "getPiercingWeapon", "()Lio/papermc/paper/datacomponent/item/PiercingWeapon;", "setPiercingWeapon$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/PiercingWeapon;)V", "", "minimumAttackCharge", "getMinimumAttackCharge", "()Ljava/lang/Float;", "setMinimumAttackCharge$com_nexomc_core", "(Ljava/lang/Float;)V", "Ljava/lang/Float;", "Lio/papermc/paper/datacomponent/item/SwingAnimation;", "swingAnimation", "getSwingAnimation", "()Lio/papermc/paper/datacomponent/item/SwingAnimation;", "setSwingAnimation$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/SwingAnimation;)V", "Lio/papermc/paper/datacomponent/item/UseEffects;", "useEffects", "getUseEffects", "()Lio/papermc/paper/datacomponent/item/UseEffects;", "setUseEffects$com_nexomc_core", "(Lio/papermc/paper/datacomponent/item/UseEffects;)V", "Lorg/bukkit/damage/DamageType;", "damageType", "getDamageType", "()Lorg/bukkit/damage/DamageType;", "setDamageType$com_nexomc_core", "(Lorg/bukkit/damage/DamageType;)V", "setType", "setAmount", "displayName", "Lkotlin/Deprecated;", "message", "Use customName instead", "hasItemName", "hasLores", "", "([Lnet/kyori/adventure/text/Component;)Lcom/nexomc/nexo/items/ItemBuilder;", "setUnbreakable", "setMaxDamage", "(Ljava/lang/Integer;)Lcom/nexomc/nexo/items/ItemBuilder;", "hasColor", "setColor", "hasTrimPattern", "trimPatReg", "Lorg/bukkit/Registry;", "Lkotlin/jvm/internal/EnhancedNullability;", "trimPatternKey", "Lorg/bukkit/NamespacedKey;", "Lorg/jspecify/annotations/Nullable;", "setTrimPattern", "trimKey", "hasTrimMaterial", "trimMatReg", "trimMaterialKey", "setTrimMaterial", "setCanPlaceOn", "predicate", "setCanBreak", "hasItemModel", "setItemModel", "hasBlockStates", "setBlockStates", "hasTooltipStyle", "setTooltipStyle", "hasTooltipDisplay", "setTooltipDisplay", "hasPaintingVariant", "setPaintingVariant", "hasEnchantable", "setEnchantable", "hasDamageResistant", "setDamageResistant", "setGlider", "glider", "hasUseRemainder", "setUseRemainder", "hasUseCooldownComponent", "setUseCooldownComponent", "hasEquippableComponent", "setEquippableComponent", "hasFoodComponent", "setFoodComponent", "hasConsumableComponent", "setConsumableComponent", "hasDeathProtectionComponent", "setDeathProtectionComponent", "hasRepairableComponent", "setRepairableComponent", "hasToolComponent", "setToolComponent", "hasWeaponComponent", "setWeaponComponent", "weaponComponent", "hasBlocksAttacksComponent", "setBlocksAttacksComponent", "blocksAttacksComponent", "setJukeboxPlayable", "jukeboxSong", "Lorg/bukkit/JukeboxSong;", "hasEnchantmentGlintOverride", "setEnchantmentGlintOverride", "glintOverride", "(Ljava/lang/Boolean;)Lcom/nexomc/nexo/items/ItemBuilder;", "Use the TriState-based overload instead", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "setEnchantmentGlintOverride(TriState.byBoolean(glintOverride))", "imports", "hasRarity", "setRarity", "hasMaxStackSize", "potionContentsBuilder", "Lio/papermc/paper/datacomponent/item/PotionContents$Builder;", "basePotionType", "potionType", "Lorg/bukkit/potion/PotionType;", "addPotionEffect", "potionEffect", "Lorg/bukkit/potion/PotionEffect;", "hasCustomTag", "customTag", "T", "Z", "namespacedKey", "dataType", "Lorg/bukkit/persistence/PersistentDataType;", "data", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)Lcom/nexomc/nexo/items/ItemBuilder;", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;)Ljava/lang/Object;", "removeCustomTag", "key", "hasCustomModelData", "setCustomModelData", "setHiddenComponents", "components", "hideComponents", "([Lnet/kyori/adventure/key/Key;)Lcom/nexomc/nexo/items/ItemBuilder;", "hiddenComponentKeys", "addAttributeModifier", "wrapper", "Lcom/nexomc/nexo/utils/wrappers/AttributeWrapper;", "addEnchant", "enchant", "level", "addEnchants", "enchants", "hasNexoTag", "tag", "addNexoTag", "addNexoTags", "tags", "([Ljava/lang/String;)Lcom/nexomc/nexo/items/ItemBuilder;", "", "hasNexoMeta", "itemResources", "referenceCopy", "Lorg/jetbrains/annotations/NotNull;", "clone", "regenerateItem", "save", "", "buildArray", "(I)[Lorg/bukkit/inventory/ItemStack;", "build", "Companion", "com.nexomc:core"}
)
public final class ItemBuilder {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ItemStack itemStack;
   @NotNull
   private final Map<String, Object> customDataMap;
   @NotNull
   private final Map<Companion.PersistentDataSpace<?, ?>, Object> persistentDataMap;
   @NotNull
   private final Set<String> nexoTags;
   @NotNull
   private final PersistentDataContainer persistentDataContainer;
   @NotNull
   private final Map<Enchantment, Integer> enchantments;
   @NotNull
   private NexoMeta nexoMeta;
   @NotNull
   private Material type;
   private int amount;
   @Nullable
   private Color color;
   @Nullable
   private TrimPattern trimPattern;
   @Nullable
   private TrimMaterial trimMaterial;
   @Nullable
   private PotionContents potionContents;
   @NotNull
   private List<Key> hiddenComponents;
   private boolean unbreakable;
   @NotNull
   private AttributeWrappers attributeModifiers;
   @Nullable
   private Component customName;
   @Nullable
   private List<Component> lore;
   @Nullable
   private ItemStack finalItemStack;
   @NotNull
   private List<Key> unsetComponents;
   @Nullable
   private Object canPlaceOn;
   @Nullable
   private Object canBreak;
   @Nullable
   private FoodProperties foodComponent;
   @Nullable
   private Tool toolComponent;
   @NotNull
   private TriState enchantmentGlintOverride;
   @Nullable
   private Integer maxStackSize;
   @Nullable
   private Component itemName;
   @Nullable
   private ItemRarity rarity;
   @Nullable
   private Integer maxDamage;
   @Nullable
   private JukeboxPlayable jukeboxPlayable;
   @Nullable
   private Art paintingVariant;
   @Nullable
   private MusicInstrument instrument;
   @Nullable
   private Equippable equippable;
   @NotNull
   private TriState isGlider;
   @Nullable
   private UseCooldown useCooldown;
   @Nullable
   private UseRemainder useRemainder;
   @Nullable
   private DamageResistant damageResistant;
   @Nullable
   private Key tooltipStyle;
   @Nullable
   private Key itemModel;
   @Nullable
   private Map<String, String> blockStates;
   @Nullable
   private Enchantable enchantable;
   @Nullable
   private Object consumableComponent;
   @Nullable
   private Object deathProtectionComponent;
   @Nullable
   private Object repairableComponent;
   @Nullable
   private io.papermc.paper.datacomponent.item.CustomModelData customModelData;
   @Nullable
   private ResolvableProfile profile;
   private boolean cache;
   @Nullable
   private TooltipDisplay tooltipDisplay;
   @Nullable
   private Weapon weapon;
   @Nullable
   private BlocksAttacks blocksAttacks;
   @Nullable
   private Key breakSound;
   @Nullable
   private AttackRange attackRange;
   @Nullable
   private KineticWeapon kineticWeapon;
   @Nullable
   private PiercingWeapon piercingWeapon;
   @Nullable
   private Float minimumAttackCharge;
   @Nullable
   private SwingAnimation swingAnimation;
   @Nullable
   private UseEffects useEffects;
   @Nullable
   private DamageType damageType;
   @NotNull
   private final Registry<TrimPattern> trimPatReg;
   @Nullable
   private final NamespacedKey trimPatternKey;
   @NotNull
   private final Registry<TrimMaterial> trimMatReg;
   @Nullable
   private final NamespacedKey trimMaterialKey;
   @NotNull
   private static final NamespacedKey ORIGINAL_NAME_KEY;

   public ItemBuilder(@NotNull ItemStack itemStack) {
      org.bukkit.inventory.meta.trim.TrimMaterial trimMaterial2;
      org.bukkit.Color color2;
      TrimPattern trimPattern2;
      block: {
         /* implicit super() */
         this.itemStack = itemStack;
         this.customDataMap = (Map)(new LinkedHashMap());
         this.persistentDataMap = (Map)(new LinkedHashMap());
         this.nexoTags = (Set)(new LinkedHashSet());
         this.nexoMeta = NexoMeta.Companion.getDEFAULT();
         this.type = Material.PAPER;
         this.hiddenComponents = CollectionsKt.emptyList();
         this.attributeModifiers = new AttributeWrappers(((java.util.List)new java.util.ArrayList()));
         this.unsetComponents = (List)(new ArrayList());
         this.enchantmentGlintOverride = TriState.NOT_SET;
         this.isGlider = TriState.NOT_SET;
         this.cache = true;
         Material material = this.itemStack.getType();
         this.type = material;
         this.amount = this.itemStack.getAmount();
         this.potionContents = this.itemStack.getData(DataComponentTypes.POTION_CONTENTS);
         ItemArmorTrim data = this.itemStack.getData(DataComponentTypes.TRIM);
         if (data != null) {
            ArmorTrim armorTrim = data.armorTrim();
            if (armorTrim != null) {
               trimPattern2 = armorTrim.getPattern();
               break block;
            }
         }

         trimPattern2 = null;
      }

      block: {
         this.trimPattern = trimPattern2;
         ItemArmorTrim itemArmorTrim = this.itemStack.getData(DataComponentTypes.TRIM);
         if (itemArmorTrim != null) {
            ArmorTrim armorTrim2 = itemArmorTrim.armorTrim();
            if (armorTrim2 != null) {
               trimMaterial2 = armorTrim2.getMaterial();
               break block;
            }
         }

         trimMaterial2 = null;
      }

      block: {
         this.trimMaterial = trimMaterial2;
         this.customName = this.itemStack.getData(DataComponentTypes.CUSTOM_NAME);
         ItemLore itemLore = this.itemStack.getData(DataComponentTypes.LORE);
         this.lore = itemLore != null ? itemLore.lines() : null;
         this.unbreakable = NMSHandlers.handler().itemUtils().unbreakable(this.itemStack);
         this.customModelData = this.itemStack.getData(DataComponentTypes.CUSTOM_MODEL_DATA);
         PersistentDataContainerView it = this.itemStack.getPersistentDataContainer();
         int value = 0;
         PersistentDataContainer persistentDataContainer2 = it.getAdapterContext().newPersistentDataContainer();
         int value2 = 0;
         it.copyTo(persistentDataContainer2, true);
         this.persistentDataContainer = persistentDataContainer2;
         this.enchantments = (Map)(new HashMap());
         this.itemName = this.itemStack.getData(DataComponentTypes.ITEM_NAME);
         this.maxDamage = this.itemStack.getData(DataComponentTypes.MAX_DAMAGE);
         this.foodComponent = this.itemStack.getData(DataComponentTypes.FOOD);
         this.toolComponent = this.itemStack.getData(DataComponentTypes.TOOL);
         this.enchantmentGlintOverride = TriState.Companion.byBoolean(this.itemStack.getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
         this.rarity = this.itemStack.getData(DataComponentTypes.RARITY);
         this.maxStackSize = this.itemStack.getData(DataComponentTypes.MAX_STACK_SIZE);
         this.jukeboxPlayable = this.itemStack.getData(DataComponentTypes.JUKEBOX_PLAYABLE);
         this.paintingVariant = NMSHandlers.handler().itemUtils().paintingVariant(this.itemStack);
         this.useCooldown = this.itemStack.getData(DataComponentTypes.USE_COOLDOWN);
         this.useRemainder = this.itemStack.getData(DataComponentTypes.USE_REMAINDER);
         this.damageResistant = this.itemStack.getData(DataComponentTypes.DAMAGE_RESISTANT);
         this.itemModel = UtilsKt.takeIf(this.itemStack.getData(DataComponentTypes.ITEM_MODEL), this.itemStack.isDataOverridden((DataComponentType)DataComponentTypes.ITEM_MODEL));
         this.enchantable = this.itemStack.getData(DataComponentTypes.ENCHANTABLE);
         this.isGlider = TriState.Companion.byBoolean(this.itemStack.hasData((DataComponentType)DataComponentTypes.GLIDER));
         this.equippable = this.itemStack.getData(DataComponentTypes.EQUIPPABLE);
         DyedItemColor dyedItemColor = this.itemStack.getData(DataComponentTypes.DYED_COLOR);
         if (dyedItemColor != null) {
            color2 = dyedItemColor.color();
            if (color2 != null) {
               break block;
            }
         }

         DyeColor dyeColor = this.itemStack.getData(DataComponentTypes.BASE_COLOR);
         color2 = dyeColor != null ? dyeColor.getColor() : null;
      }

      this.color = color2;
      if (VersionUtil.atleast("1.21.5")) {
         this.tooltipDisplay = this.itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY);
         this.weapon = this.itemStack.getData(DataComponentTypes.WEAPON);
         this.blocksAttacks = this.itemStack.getData(DataComponentTypes.BLOCKS_ATTACKS);
      }

      RegistryKey registryKey = RegistryKey.TRIM_PATTERN;
      this.trimPatReg = RegistryUtilsKt.<TrimPattern>toRegistry(registryKey);
      /* this-alias removed */
      TrimPattern trimPattern3 = this.trimPattern;
      NamespacedKey namespacedKey;
      if (trimPattern3 != null) {
         TrimPattern trimPattern4 = trimPattern3;
         Registry registry = this.trimPatReg;
         int value3 = 0;
         namespacedKey = registry.getKey((Keyed)trimPattern4);
         /* this-alias removed */
      } else {
         namespacedKey = null;
      }

      this.trimPatternKey = namespacedKey;
      RegistryKey registryKey2 = RegistryKey.TRIM_MATERIAL;
      this.trimMatReg = RegistryUtilsKt.<TrimMaterial>toRegistry(registryKey2);
      /* this-alias removed */
      TrimMaterial trimMaterial3 = this.trimMaterial;
      NamespacedKey namespacedKey2;
      if (trimMaterial3 != null) {
         TrimMaterial trimMaterial4 = trimMaterial3;
         Registry registry2 = this.trimMatReg;
         int value4 = 0;
         namespacedKey2 = registry2.getKey((Keyed)trimMaterial4);
         /* this-alias removed */
      } else {
         namespacedKey2 = null;
      }

      this.trimMaterialKey = namespacedKey2;
   }

   @NotNull
   public final Map<String, Object> getCustomDataMap() {
      return this.customDataMap;
   }

   @NotNull
   public final Map<Companion.PersistentDataSpace<?, ?>, Object> getPersistentDataMap() {
      return this.persistentDataMap;
   }

   @NotNull
   public final Set<String> getNexoTags() {
      return this.nexoTags;
   }

   @NotNull
   public final PersistentDataContainer getPersistentDataContainer() {
      return this.persistentDataContainer;
   }

   @NotNull
   public final Map<Enchantment, Integer> getEnchantments() {
      return this.enchantments;
   }

   @NotNull
   public final NexoMeta getNexoMeta() {
      return this.nexoMeta;
   }

   @NotNull
   public final Material getType() {
      return this.type;
   }

   public final int getAmount() {
      return this.amount;
   }

   @Nullable
   public final Color getColor() {
      return this.color;
   }

   @Nullable
   public final TrimPattern getTrimPattern() {
      return this.trimPattern;
   }

   @Nullable
   public final TrimMaterial getTrimMaterial() {
      return this.trimMaterial;
   }

   @Nullable
   public final PotionContents getPotionContents() {
      return this.potionContents;
   }

   @NotNull
   public final List<Key> getHiddenComponents() {
      return this.hiddenComponents;
   }

   public final boolean getUnbreakable() {
      return this.unbreakable;
   }

   @NotNull
   public final AttributeWrappers getAttributeModifiers() {
      return this.attributeModifiers;
   }

   @Nullable
   public final Component getCustomName() {
      return this.customName;
   }

   @Nullable
   public final List<Component> getLore() {
      return this.lore;
   }

   @Nullable
   public final ItemStack getFinalItemStack() {
      return this.finalItemStack;
   }

   @NotNull
   public final List<Key> getUnsetComponents() {
      return this.unsetComponents;
   }

   public final void setUnsetComponents$com_nexomc_core(@NotNull List<Key> keys) {
      this.unsetComponents = keys;
   }

   @Nullable
   public final Object getCanPlaceOn() {
      return this.canPlaceOn;
   }

   @Nullable
   public final Object getCanBreak() {
      return this.canBreak;
   }

   @Nullable
   public final FoodProperties getFoodComponent() {
      return this.foodComponent;
   }

   @Nullable
   public final Tool getToolComponent() {
      return this.toolComponent;
   }

   @NotNull
   public final TriState getEnchantmentGlintOverride() {
      return this.enchantmentGlintOverride;
   }

   @Nullable
   public final Integer getMaxStackSize() {
      return this.maxStackSize;
   }

   @Nullable
   public final Component getItemName() {
      return this.itemName;
   }

   @Nullable
   public final ItemRarity getRarity() {
      return this.rarity;
   }

   @Nullable
   public final Integer getMaxDamage() {
      return this.maxDamage;
   }

   @Nullable
   public final JukeboxPlayable getJukeboxPlayable() {
      return this.jukeboxPlayable;
   }

   @Nullable
   public final Art getPaintingVariant() {
      return this.paintingVariant;
   }

   @Nullable
   public final MusicInstrument getInstrument() {
      return this.instrument;
   }

   @Nullable
   public final Equippable getEquippable() {
      return this.equippable;
   }

   @NotNull
   public final TriState isGlider() {
      return this.isGlider;
   }

   @Nullable
   public final UseCooldown getUseCooldown() {
      return this.useCooldown;
   }

   @Nullable
   public final UseRemainder getUseRemainder() {
      return this.useRemainder;
   }

   @Nullable
   public final DamageResistant getDamageResistant() {
      return this.damageResistant;
   }

   @Nullable
   public final Key getTooltipStyle() {
      return this.tooltipStyle;
   }

   @Nullable
   public final Key getItemModel() {
      return this.itemModel;
   }

   @Nullable
   public final Map<String, String> getBlockStates() {
      return this.blockStates;
   }

   @Nullable
   public final Enchantable getEnchantable() {
      return this.enchantable;
   }

   @Nullable
   public final Object getConsumableComponent() {
      return this.consumableComponent;
   }

   @Nullable
   public final Object getDeathProtectionComponent() {
      return this.deathProtectionComponent;
   }

   @Nullable
   public final Object getRepairableComponent() {
      return this.repairableComponent;
   }

   @Nullable
   public final io.papermc.paper.datacomponent.item.CustomModelData getCustomModelData() {
      return this.customModelData;
   }

   @Nullable
   public final ResolvableProfile getProfile() {
      return this.profile;
   }

   public final void setProfile$com_nexomc_core(@Nullable ResolvableProfile resolvableProfile) {
      this.profile = resolvableProfile;
   }

   public final boolean getCache() {
      return this.cache;
   }

   public final void setCache(boolean flag) {
      this.cache = flag;
   }

   @Nullable
   public final TooltipDisplay getTooltipDisplay() {
      return this.tooltipDisplay;
   }

   @Nullable
   public final Weapon getWeapon() {
      return this.weapon;
   }

   @Nullable
   public final BlocksAttacks getBlocksAttacks() {
      return this.blocksAttacks;
   }

   @Nullable
   public final Key getBreakSound() {
      return this.breakSound;
   }

   public final void setBreakSound$com_nexomc_core(@Nullable Key key) {
      this.breakSound = key;
   }

   @Nullable
   public final AttackRange getAttackRange() {
      return this.attackRange;
   }

   public final void setAttackRange$com_nexomc_core(@Nullable AttackRange attackRange2) {
      this.attackRange = attackRange2;
   }

   @Nullable
   public final KineticWeapon getKineticWeapon() {
      return this.kineticWeapon;
   }

   public final void setKineticWeapon$com_nexomc_core(@Nullable KineticWeapon kineticWeapon2) {
      this.kineticWeapon = kineticWeapon2;
   }

   @Nullable
   public final PiercingWeapon getPiercingWeapon() {
      return this.piercingWeapon;
   }

   public final void setPiercingWeapon$com_nexomc_core(@Nullable PiercingWeapon piercingWeapon2) {
      this.piercingWeapon = piercingWeapon2;
   }

   @Nullable
   public final Float getMinimumAttackCharge() {
      return this.minimumAttackCharge;
   }

   public final void setMinimumAttackCharge$com_nexomc_core(@Nullable Float number) {
      this.minimumAttackCharge = number;
   }

   @Nullable
   public final SwingAnimation getSwingAnimation() {
      return this.swingAnimation;
   }

   public final void setSwingAnimation$com_nexomc_core(@Nullable SwingAnimation swingAnimation2) {
      this.swingAnimation = swingAnimation2;
   }

   @Nullable
   public final UseEffects getUseEffects() {
      return this.useEffects;
   }

   public final void setUseEffects$com_nexomc_core(@Nullable UseEffects useEffects2) {
      this.useEffects = useEffects2;
   }

   @Nullable
   public final DamageType getDamageType() {
      return this.damageType;
   }

   public final void setDamageType$com_nexomc_core(@Nullable DamageType damageType2) {
      this.damageType = damageType2;
   }

   public ItemBuilder(@NotNull Material material) {
      ItemStack itemStack2 = ItemStack.of(material);
      this(itemStack2);
   }

   public ItemBuilder(@NotNull WrappedMMOItem wrapped) {
      ItemStack itemStack2 = wrapped.build();
      this(itemStack2);
      this.cache = wrapped.getCache();
   }

   public ItemBuilder(@NotNull WrappedCrucibleItem wrapped) {
      ItemStack itemStack2 = wrapped.build();
      this(itemStack2);
      this.cache = wrapped.getCache();
   }

   public ItemBuilder(@NotNull ItemBuilder itemBuilder) {
      this(itemBuilder.build());
   }

   @NotNull
   public final ItemBuilder setType(@NotNull Material type) {
      this.type = type;
      return this;
   }

   @NotNull
   public final ItemBuilder setAmount(int amount) {
      this.amount = RangesKt.coerceIn(amount, 0, this.type.getMaxStackSize());
      return this;
   }

   @NotNull
   public final ItemBuilder customName(@Nullable Component customName) {
      this.customName = customName;
      return this;
   }

   /** @deprecated */
   @Deprecated(
      message = "Use customName instead"
   )
   @NotNull
   public final ItemBuilder displayName(@Nullable Component displayName) {
      this.customName = displayName;
      return this;
   }

   public final boolean hasItemName() {
      return this.itemName != null;
   }

   @NotNull
   public final ItemBuilder itemName(@Nullable Component itemName) {
      this.itemName = itemName;
      return this;
   }

   public final boolean hasLores() {
      Collection collection = (Collection)this.lore;
      return collection != null && !collection.isEmpty();
   }

   @NotNull
   public final List<Component> lore() {
      List list = this.lore;
      if (list == null) {
         list = CollectionsKt.emptyList();
      }

      return list;
   }

   @NotNull
   public final ItemBuilder lore(@Nullable List<Component> lore) {
      this.lore = lore;
      return this;
   }

   @NotNull
   public final ItemBuilder lore(@NotNull Component... lore) {
      this.lore = ArraysKt.toList(lore);
      return this;
   }

   @NotNull
   public final ItemBuilder setUnbreakable(boolean unbreakable) {
      this.unbreakable = unbreakable;
      return this;
   }

   @NotNull
   public final ItemBuilder setMaxDamage(@Nullable Integer maxDamage) {
      this.maxDamage = maxDamage;
      return this;
   }

   public final boolean hasColor() {
      return this.color != null && !Intrinsics.areEqual(this.color, Bukkit.getItemFactory().getDefaultLeatherColor());
   }

   @NotNull
   public final ItemBuilder setColor(@Nullable Color color) {
      this.color = color;
      return this;
   }

   public final boolean hasTrimPattern() {
      return this.trimPattern != null;
   }

   @NotNull
   public final ItemBuilder setTrimPattern(@Nullable Key trimKey) {
      ItemBuilder itemBuilder = this;
      TrimPattern trimPattern2;
      if (trimKey != null) {
         Registry registry = this.trimPatReg;
         int value = 0;
         trimPattern2 = (TrimPattern)registry.get(trimKey);
         itemBuilder = this;
      } else {
         trimPattern2 = null;
      }

      itemBuilder.trimPattern = trimPattern2;
      return this;
   }

   public final boolean hasTrimMaterial() {
      return this.trimMaterial != null;
   }

   @NotNull
   public final ItemBuilder setTrimMaterial(@Nullable Key trimKey) {
      ItemBuilder itemBuilder = this;
      TrimMaterial trimMaterial2;
      if (trimKey != null) {
         Registry registry = this.trimMatReg;
         int value = 0;
         trimMaterial2 = (TrimMaterial)registry.get(trimKey);
         itemBuilder = this;
      } else {
         trimMaterial2 = null;
      }

      itemBuilder.trimMaterial = trimMaterial2;
      return this;
   }

   @NotNull
   public final ItemBuilder setCanPlaceOn(@Nullable Object predicate) {
      this.canPlaceOn = predicate;
      return this;
   }

   @NotNull
   public final ItemBuilder setCanBreak(@Nullable Object predicate) {
      this.canBreak = predicate;
      return this;
   }

   public final boolean hasItemModel() {
      return this.itemModel != null;
   }

   @NotNull
   public final ItemBuilder setItemModel(@Nullable Key itemModel) {
      ItemBuilder itemBuilder = this;
      NamespacedKey namespacedKey;
      if (itemModel != null) {
         int value = 0;
         namespacedKey = NamespacedKey.fromString(itemModel.asString());
         itemBuilder = this;
      } else {
         namespacedKey = null;
      }

      itemBuilder.itemModel = (Key)namespacedKey;
      return this;
   }

   public final boolean hasBlockStates() {
      Map map = this.blockStates;
      return map != null && !map.isEmpty();
   }

   @NotNull
   public final ItemBuilder setBlockStates(@NotNull Map<String, String> blockStates) {
      this.blockStates = blockStates;
      return this;
   }

   public final boolean hasTooltipStyle() {
      return this.tooltipStyle != null;
   }

   @NotNull
   public final ItemBuilder setTooltipStyle(@Nullable Key tooltipStyle) {
      this.tooltipStyle = tooltipStyle;
      return this;
   }

   public final boolean hasTooltipDisplay() {
      return VersionUtil.atleast("1.21.5") && this.tooltipDisplay != null;
   }

   @NotNull
   public final ItemBuilder setTooltipDisplay(@Nullable TooltipDisplay tooltipDisplay) {
      this.tooltipDisplay = tooltipDisplay;
      return this;
   }

   public final boolean hasPaintingVariant() {
      return this.paintingVariant != null;
   }

   @NotNull
   public final ItemBuilder setPaintingVariant(@Nullable Key paintingVariant) {
      Registry registry2 = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT);
      Registry registry = registry2;
      ItemBuilder itemBuilder = this;
      Art art;
      if (paintingVariant != null) {
         int value = 0;
         art = (Art)registry.get(paintingVariant);
         itemBuilder = this;
      } else {
         art = null;
      }

      itemBuilder.paintingVariant = art;
      return this;
   }

   public final boolean hasEnchantable() {
      return this.enchantable != null;
   }

   @NotNull
   public final ItemBuilder setEnchantable(@Nullable Enchantable enchantable) {
      this.enchantable = enchantable;
      return this;
   }

   public final boolean hasDamageResistant() {
      return this.damageResistant != null;
   }

   @NotNull
   public final ItemBuilder setDamageResistant(@Nullable DamageResistant damageResistant) {
      this.damageResistant = damageResistant;
      return this;
   }

   @NotNull
   public final ItemBuilder setGlider(boolean glider) {
      this.isGlider = TriState.Companion.byBoolean(glider);
      return this;
   }

   public final boolean hasUseRemainder() {
      return this.useRemainder != null;
   }

   @NotNull
   public final ItemBuilder setUseRemainder(@Nullable UseRemainder useRemainder) {
      this.useRemainder = useRemainder;
      return this;
   }

   public final boolean hasUseCooldownComponent() {
      return this.useCooldown != null;
   }

   @NotNull
   public final ItemBuilder setUseCooldownComponent(@Nullable UseCooldown useCooldown) {
      this.useCooldown = useCooldown;
      return this;
   }

   public final boolean hasEquippableComponent() {
      return this.equippable != null;
   }

   @NotNull
   public final ItemBuilder setEquippableComponent(@Nullable Equippable equippable) {
      this.equippable = equippable;
      return this;
   }

   public final boolean hasFoodComponent() {
      return this.foodComponent != null;
   }

   @NotNull
   public final ItemBuilder setFoodComponent(@Nullable FoodProperties foodComponent) {
      this.foodComponent = foodComponent;
      return this;
   }

   public final boolean hasConsumableComponent() {
      return this.consumableComponent != null;
   }

   @NotNull
   public final ItemBuilder setConsumableComponent(@Nullable Object consumableComponent) {
      this.consumableComponent = consumableComponent;
      return this;
   }

   public final boolean hasDeathProtectionComponent() {
      return this.deathProtectionComponent != null;
   }

   @NotNull
   public final ItemBuilder setDeathProtectionComponent(@Nullable Object deathProtectionComponent) {
      this.deathProtectionComponent = deathProtectionComponent;
      return this;
   }

   public final boolean hasRepairableComponent() {
      return this.repairableComponent != null;
   }

   @NotNull
   public final ItemBuilder setRepairableComponent(@Nullable Object repairableComponent) {
      this.repairableComponent = repairableComponent;
      return this;
   }

   public final boolean hasToolComponent() {
      return this.toolComponent != null;
   }

   @NotNull
   public final ItemBuilder setToolComponent(@Nullable Tool toolComponent) {
      this.toolComponent = toolComponent;
      return this;
   }

   public final boolean hasWeaponComponent() {
      return VersionUtil.atleast("1.21.5") && this.weapon != null;
   }

   @NotNull
   public final ItemBuilder setWeaponComponent(@Nullable Weapon weaponComponent) {
      this.weapon = weaponComponent;
      return this;
   }

   public final boolean hasBlocksAttacksComponent() {
      return VersionUtil.atleast("1.21.5") && this.blocksAttacks != null;
   }

   @NotNull
   public final ItemBuilder setBlocksAttacksComponent(@Nullable BlocksAttacks blocksAttacksComponent) {
      this.blocksAttacks = blocksAttacksComponent;
      return this;
   }

   @NotNull
   public final ItemBuilder setJukeboxPlayable(@Nullable JukeboxPlayable jukeboxPlayable) {
      this.jukeboxPlayable = jukeboxPlayable;
      return this;
   }

   @NotNull
   public final ItemBuilder setJukeboxPlayable(@Nullable JukeboxSong jukeboxSong) {
      JukeboxPlayable jukeboxPlayable2;
      ItemBuilder itemBuilder;
      block: {
         itemBuilder = this;
         if (jukeboxSong != null) {
            int value = 0;
            JukeboxPlayable.Builder builder = JukeboxPlayable.jukeboxPlayable(jukeboxSong);
            itemBuilder = this;
            if (builder != null) {
               jukeboxPlayable2 = builder.build();
               break block;
            }
         }

         jukeboxPlayable2 = null;
      }

      itemBuilder.jukeboxPlayable = jukeboxPlayable2;
      return this;
   }

   public final boolean hasEnchantmentGlintOverride() {
      return this.enchantmentGlintOverride.isSet();
   }

   @NotNull
   public final ItemBuilder setEnchantmentGlintOverride(@NotNull TriState glintOverride) {
      this.enchantmentGlintOverride = glintOverride;
      return this;
   }

   /** @deprecated */
   @Deprecated(
      message = "Use the TriState-based overload instead",
      replaceWith = @ReplaceWith(
   expression = "setEnchantmentGlintOverride(TriState.byBoolean(glintOverride))",
   imports = {}
)
   )
   @NotNull
   public final ItemBuilder setEnchantmentGlintOverride(@Nullable Boolean glintOverride) {
      this.enchantmentGlintOverride = TriState.Companion.byBoolean(glintOverride);
      return this;
   }

   public final boolean hasRarity() {
      return this.rarity != null;
   }

   @NotNull
   public final ItemBuilder setRarity(@Nullable ItemRarity rarity) {
      this.rarity = rarity;
      return this;
   }

   public final boolean hasMaxStackSize() {
      return this.maxStackSize != null;
   }

   @NotNull
   public final ItemBuilder maxStackSize(int maxStackSize) {
      this.maxStackSize = RangesKt.coerceIn(maxStackSize, 1, 99);
      return this;
   }

   @NotNull
   public final ItemBuilder instrument(@Nullable MusicInstrument instrument) {
      this.instrument = instrument;
      return this;
   }

   private final PotionContents.Builder potionContentsBuilder() {
      PotionContents.Builder builder2 = PotionContents.potionContents();
      PotionContents.Builder builder = builder2;
      PotionContents potionContents2 = this.potionContents;
      if (potionContents2 != null) {
         PotionContents existing = potionContents2;
         int value = 0;
         builder.potion(existing.potion()).addCustomEffects(existing.customEffects()).customName(existing.customName()).customColor(existing.customColor());
      }

      return builder;
   }

   @NotNull
   public final ItemBuilder basePotionType(@Nullable PotionType potionType) {
      this.potionContents = this.potionContentsBuilder().potion(potionType).build();
      return this;
   }

   @NotNull
   public final ItemBuilder addPotionEffect(@NotNull PotionEffect potionEffect) {
      this.potionContents = this.potionContentsBuilder().addCustomEffects(CollectionsKt.listOf(potionEffect)).build();
      return this;
   }

   public final boolean hasCustomTag() {
      return !this.persistentDataContainer.isEmpty();
   }

   @NotNull
   public final <T, Z> ItemBuilder customTag(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> dataType, @NotNull Z data) {
      this.persistentDataMap.put(new Companion.PersistentDataSpace(namespacedKey, dataType), data);
      return this;
   }

   @Nullable
   public final <T, Z> Z customTag(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> dataType) {
      Object obj = this.persistentDataMap.get(new Companion.PersistentDataSpace(namespacedKey, dataType));
      if (obj == null) {
         obj = null;
      }

      return (Z)obj;
   }

   @NotNull
   public final ItemBuilder removeCustomTag(@NotNull NamespacedKey key) {
      this.persistentDataContainer.remove(key);
      return this;
   }

   public final boolean hasCustomModelData() {
      return this.customModelData != null;
   }

   @NotNull
   public final ItemBuilder customModelData(int customModelData) {
      io.papermc.paper.datacomponent.item.CustomModelData.Builder builder2 = io.papermc.paper.datacomponent.item.CustomModelData.customModelData().addFloat((float)customModelData);
      io.papermc.paper.datacomponent.item.CustomModelData.Builder builder = builder2;
      io.papermc.paper.datacomponent.item.CustomModelData customModelData2 = this.customModelData;
      if (customModelData2 != null) {
         io.papermc.paper.datacomponent.item.CustomModelData existing = customModelData2;
         int value = 0;
         builder.addFloats(existing.floats()).addFlags(existing.flags()).addStrings(existing.strings()).addColors(existing.colors());
      }

      this.customModelData = builder.build();
      return this;
   }

   @NotNull
   public final ItemBuilder setCustomModelData(@Nullable io.papermc.paper.datacomponent.item.CustomModelData customModelData) {
      this.customModelData = customModelData;
      return this;
   }

   @NotNull
   public final ItemBuilder setHiddenComponents(@NotNull List<Key> components) {
      this.hiddenComponents = components;
      return this;
   }

   @NotNull
   public final ItemBuilder hideComponents(@NotNull Key... components) {
      if (VersionUtil.atleast("1.21.5")) {
         RegistryKey registryKey = RegistryKey.DATA_COMPONENT_TYPE;
         Registry registry = RegistryUtilsKt.toRegistry(registryKey);
         TooltipDisplay tooltipDisplay2 = this.tooltipDisplay;
         Set set = tooltipDisplay2 != null ? tooltipDisplay2.hiddenComponents() : null;
         if (set == null) {
            set = SetsKt.emptySet();
         }

         Set existing = set;
         Collection destination = (Collection)(new ArrayList());
         Object[] objs = components;
         int value = 0;

         for(int i = components.length; value < i; ++value) {
            Object element = objs[value];
            int value2 = 0;
            int value3 = 0;
            DataComponentType dataComponentType = (DataComponentType)registry.get((Key)element);
            if (dataComponentType != null) {
               Object it = dataComponentType;
               int value4 = 0;
               destination.add(it);
            }
         }

         List added = (List)destination;
         TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();
         TooltipDisplay tooltipDisplay3 = this.tooltipDisplay;
         builder = builder.hideTooltip(tooltipDisplay3 != null ? tooltipDisplay3.hideTooltip() : false);
         Collection collection = (Collection)CollectionsKt.distinct((Iterable)SetsKt.plus(existing, (Iterable)added));
         DataComponentType[] dataComponentTypes = (DataComponentType[])collection.toArray(new DataComponentType[0]);
         this.tooltipDisplay = builder.addHiddenComponents(Arrays.copyOf(dataComponentTypes, dataComponentTypes.length)).build();
      } else {
         this.hiddenComponents = CollectionsKt.distinct((Iterable)CollectionsKt.plus((Collection)this.hiddenComponents, components));
      }

      return this;
   }

   @NotNull
   public final Set<Key> hiddenComponentKeys() {
      Set set;
      if (VersionUtil.atleast("1.21.5")) {
         TooltipDisplay tooltipDisplay2 = this.tooltipDisplay;
         if (tooltipDisplay2 != null) {
            Set set2 = tooltipDisplay2.hiddenComponents();
            if (set2 != null) {
               Iterable iterable = (Iterable)set2;
               Collection destination = (Collection)((Set)(new LinkedHashSet()));

               for(Object item : iterable) {
                  DataComponentType it = (DataComponentType)item;
                  int value = 0;
                  destination.add(it.key());
               }

               set = (Set)destination;
               return set;
            }
         }

         set = SetsKt.emptySet();
      } else {
         set = CollectionsKt.toSet((Iterable)this.hiddenComponents);
      }

      return set;
   }

   @NotNull
   public final ItemBuilder addAttributeModifier(@NotNull AttributeWrapper wrapper) {
      ((Collection)this.attributeModifiers.getWrappers()).add(wrapper);
      return this;
   }

   @NotNull
   public final ItemBuilder addEnchant(@NotNull Enchantment enchant, int level) {
      this.enchantments.put(enchant, level);
      return this;
   }

   @NotNull
   public final ItemBuilder addEnchants(@NotNull Map<Enchantment, Integer> enchants) {
      enchants.forEach((enchant, level) -> this.addEnchant(enchant, level));
      return this;
   }

   public final boolean hasNexoTag(@NotNull String tag) {
      return this.nexoTags.contains(tag);
   }

   @NotNull
   public final ItemBuilder addNexoTag(@NotNull String tag) {
      ((Collection)this.nexoTags).add(tag);
      return this;
   }

   @NotNull
   public final ItemBuilder addNexoTags(@NotNull String... tags) {
      CollectionsKt.addAll((Collection)this.nexoTags, tags);
      return this;
   }

   @NotNull
   public final ItemBuilder addNexoTags(@NotNull Collection<String> tags) {
      CollectionsKt.addAll((Collection)this.nexoTags, (Iterable)tags);
      return this;
   }

   public final boolean hasNexoMeta() {
      return !Intrinsics.areEqual(this.nexoMeta, NexoMeta.Companion.getDEFAULT());
   }

   @NotNull
   public final ItemBuilder nexoMeta(@NotNull NexoMeta itemResources) {
      this.nexoMeta = itemResources;
      return this;
   }

   @NotNull
   public final ItemStack referenceCopy() {
      ItemStack itemStack2 = this.itemStack.clone();
      return itemStack2;
   }

   @NotNull
   public final ItemBuilder clone() {
      return new ItemBuilder(this.build());
   }

   @NotNull
   public final ItemBuilder regenerateItem() {
      this.itemStack.setType(this.type);
      if (this.amount != this.itemStack.getAmount()) {
         this.itemStack.setAmount(this.amount);
      }

      Integer number = this.maxDamage;
      if (number != null) {
         Integer number2 = number;
         int it = ((Number)number2).intValue();
         int value = 0;
         number = it > 0 ? number2 : null;
         if ((it > 0 ? number2 : null) != null) {
            it = ((Number)number).intValue();
            value = 0;
            this.itemStack.setData(DataComponentTypes.MAX_DAMAGE, it);
            this.itemStack.setData(DataComponentTypes.DAMAGE, 0);
         }
      }

      Component component2 = this.itemName;
      if (component2 != null) {
         Component it = component2;
         int value2 = 0;
         this.itemStack.setData(DataComponentTypes.ITEM_NAME, it);
      }

      Integer number3 = this.maxStackSize;
      if (number3 != null) {
         int it = ((Number)number3).intValue();
         int value3 = 0;
         this.itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, it);
      }

      Boolean toBooleanOrNull = this.enchantmentGlintOverride.toBooleanOrNull();
      if (toBooleanOrNull != null) {
         boolean it = toBooleanOrNull;
         int value4 = 0;
         this.itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, it);
      }

      ItemRarity itemRarity = this.rarity;
      if (itemRarity != null) {
         ItemRarity it = itemRarity;
         int value5 = 0;
         this.itemStack.setData(DataComponentTypes.RARITY, it);
      }

      JukeboxPlayable jukeboxPlayable2 = this.jukeboxPlayable;
      if (jukeboxPlayable2 != null) {
         JukeboxPlayable it = jukeboxPlayable2;
         int value6 = 0;
         this.itemStack.setData(DataComponentTypes.JUKEBOX_PLAYABLE, it);
      }

      UseCooldown useCooldown2 = this.useCooldown;
      if (useCooldown2 != null) {
         UseCooldown it = useCooldown2;
         int value7 = 0;
         this.itemStack.setData(DataComponentTypes.USE_COOLDOWN, it);
      }

      DamageResistant damageResistant2 = this.damageResistant;
      if (damageResistant2 != null) {
         DamageResistant it = damageResistant2;
         int value8 = 0;
         this.itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, it);
      }

      Key key = this.tooltipStyle;
      if (key != null) {
         Key it = key;
         int value9 = 0;
         this.itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, it);
      }

      UseRemainder useRemainder2 = this.useRemainder;
      if (useRemainder2 != null) {
         UseRemainder it = useRemainder2;
         int value10 = 0;
         this.itemStack.setData(DataComponentTypes.USE_REMAINDER, it);
      }

      Enchantable enchantable2 = this.enchantable;
      if (enchantable2 != null) {
         Enchantable it = enchantable2;
         int value11 = 0;
         this.itemStack.setData(DataComponentTypes.ENCHANTABLE, it);
      }

      Key key2 = this.itemModel;
      if (key2 != null) {
         Key it = key2;
         int value12 = 0;
         this.itemStack.setData(DataComponentTypes.ITEM_MODEL, it);
      }

      io.papermc.paper.datacomponent.item.CustomModelData customModelData2 = this.customModelData;
      if (customModelData2 != null) {
         io.papermc.paper.datacomponent.item.CustomModelData it = customModelData2;
         int value13 = 0;
         this.itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, it);
      }

      Component component3 = this.customName;
      if (component3 != null) {
         Component it = component3;
         int value14 = 0;
         this.itemStack.setData(DataComponentTypes.CUSTOM_NAME, AdventureUtils.INSTANCE.setDefaultStyle(it, net.kyori.adventure.text.format.NamedTextColor.WHITE));
      }

      PotionContents potionContents2 = this.potionContents;
      if (potionContents2 != null) {
         PotionContents contents = potionContents2;
         int value15 = 0;
         Color color2 = this.color;
         if (color2 == null) {
            color2 = contents.customColor();
         }

         Color color = color2;
         this.itemStack.setData(DataComponentTypes.POTION_CONTENTS, this.potionContentsBuilder().customColor(color).build());
      }

      if (this.lore != null) {
         ItemStack itemStack2 = this.itemStack;
         DataComponentType.Valued valued = DataComponentTypes.LORE;
         List list = this.lore;
         itemStack2.setData(valued, ItemLore.lore(list));
      } else {
         this.itemStack.unsetData((DataComponentType)DataComponentTypes.LORE);
      }

      if (!this.enchantments.isEmpty()) {
         this.itemStack.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments().addAll(this.enchantments).build());
      }

      if (this.isGlider.isSet()) {
         if (this.isGlider.toBoolean(this.type == Material.ELYTRA)) {
            this.itemStack.setData(DataComponentTypes.GLIDER);
         } else {
            this.itemStack.unsetData((DataComponentType)DataComponentTypes.GLIDER);
         }
      }

      NMSHandlers.handler().itemUtils().setUnbreakable(this.itemStack, this.unbreakable);
      if (!this.itemStack.hasData((DataComponentType)DataComponentTypes.ITEM_MODEL) && (this.nexoMeta.getDyeableModel() != null || this.nexoMeta.getThrowingModel() != null || !((Collection)this.nexoMeta.getCompositeModels()).isEmpty())) {
         NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:" + NexoItems.idFromItem(this));
         if (namespacedKey != null) {
            NamespacedKey it = namespacedKey;
            int value16 = 0;
            this.itemStack.setData(DataComponentTypes.ITEM_MODEL, it);
         }
      }

      Colorable colorable = ColorableKt.asColorable(this.itemStack);
      if (colorable != null) {
         colorable.setColor(this.color);
      }

      TrimPattern trimPattern2 = this.trimPattern;
      if (trimPattern2 != null) {
         TrimPattern it = trimPattern2;
         int value17 = 0;
         ArmorTrim armorTrim2;
         TrimMaterial trimMaterial2 = this.trimMaterial;
         if (trimMaterial2 == null) {
            trimMaterial2 = TrimMaterial.REDSTONE;
         }

         armorTrim2 = new ArmorTrim(trimMaterial2, it);
         ItemArmorTrim.Builder builder = ItemArmorTrim.itemArmorTrim(armorTrim2);
         ItemArmorTrim.Builder armorTrim = builder;
         this.itemStack.setData(DataComponentTypes.TRIM, (DataComponentBuilder)armorTrim);
      }

      this.itemStack.editPersistentDataContainer((a0_ -> ItemBuilder.regenerateItem$lambda$18(this, a0_)));
      IItemUtils itemUtils = NMSHandlers.handler().itemUtils();
      int value18 = 0;
      Colorable colorable2 = itemUtils.asColorable(this.itemStack);
      if (colorable2 != null) {
         colorable2.setColor(this.color);
      }

      itemUtils.paintingVariant(this.itemStack, this.paintingVariant);
      itemUtils.consumableComponent(this.itemStack, this.consumableComponent);
      itemUtils.repairableComponent(this.itemStack, this.repairableComponent);
      itemUtils.canPlaceOnComponent(this.itemStack, this.canPlaceOn);
      itemUtils.canBreakComponent(this.itemStack, this.canBreak);
      itemUtils.blockstateComponent(this.itemStack, this.blockStates);
      itemUtils.deathProtectionComponent(this.itemStack, this.deathProtectionComponent);
      itemUtils.customDataComponent(this.itemStack, this.customDataMap);
      itemUtils.handleTooltipDisplay(this.itemStack, (Collection)this.hiddenComponents);
      ItemBuilder itemBuilder = this;

      Object obj;
      try {
         ItemBuilder itemBuilder2 = itemBuilder;
         value18 = 0;
         Equippable equippable2 = itemBuilder2.equippable;
         Unit unit;
         if (equippable2 != null) {
            Equippable it = equippable2;
            int value19 = 0;
            itemBuilder2.itemStack.setData(DataComponentTypes.EQUIPPABLE, it);
            unit = Unit.INSTANCE;
         } else {
            unit = null;
         }

         obj = ResultShim.ok(unit);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value20 = 0;
         it.printStackTrace();
      }

      FoodProperties foodProperties = this.foodComponent;
      if (foodProperties != null) {
         FoodProperties it = foodProperties;
         int value21 = 0;
         this.itemStack.setData(DataComponentTypes.FOOD, it);
      }

      Tool tool = this.toolComponent;
      if (tool != null) {
         Tool it = tool;
         int value22 = 0;
         this.itemStack.setData(DataComponentTypes.TOOL, it);
      }

      ResolvableProfile resolvableProfile = this.profile;
      if (resolvableProfile != null) {
         ResolvableProfile it = resolvableProfile;
         int value23 = 0;
         this.itemStack.setData(DataComponentTypes.PROFILE, it);
      }

      if (this.attributeModifiers.isNotEmpty()) {
         this.itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.attributeModifiers.build(this.itemStack));
      }

      MusicInstrument musicInstrument = this.instrument;
      if (musicInstrument != null) {
         MusicInstrument it = musicInstrument;
         int value24 = 0;
         this.itemStack.setData(DataComponentTypes.INSTRUMENT, it);
      }

      if (VersionUtil.atleast("1.21.5")) {
         TooltipDisplay tooltipDisplay2 = this.tooltipDisplay;
         if (tooltipDisplay2 != null) {
            TooltipDisplay it = tooltipDisplay2;
            int value25 = 0;
            this.itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, it);
         }

         Weapon weapon2 = this.weapon;
         if (weapon2 != null) {
            Weapon it = weapon2;
            int value26 = 0;
            this.itemStack.setData(DataComponentTypes.WEAPON, it);
         }

         BlocksAttacks blocksAttacks2 = this.blocksAttacks;
         if (blocksAttacks2 != null) {
            BlocksAttacks it = blocksAttacks2;
            int value27 = 0;
            this.itemStack.setData(DataComponentTypes.BLOCKS_ATTACKS, it);
         }

         Key key3 = this.breakSound;
         if (key3 != null) {
            Key it = key3;
            int value28 = 0;
            this.itemStack.setData(DataComponentTypes.BREAK_SOUND, it);
         }
      }

      if (VersionUtil.atleast("1.21.11")) {
         AttackRange attackRange2 = this.attackRange;
         if (attackRange2 != null) {
            AttackRange it = attackRange2;
            int value29 = 0;
            this.itemStack.setData(DataComponentTypes.ATTACK_RANGE, it);
         }

         KineticWeapon kineticWeapon2 = this.kineticWeapon;
         if (kineticWeapon2 != null) {
            KineticWeapon it = kineticWeapon2;
            int value30 = 0;
            this.itemStack.setData(DataComponentTypes.KINETIC_WEAPON, it);
         }

         PiercingWeapon piercingWeapon2 = this.piercingWeapon;
         if (piercingWeapon2 != null) {
            PiercingWeapon it = piercingWeapon2;
            int value31 = 0;
            this.itemStack.setData(DataComponentTypes.PIERCING_WEAPON, it);
         }

         Float number4 = this.minimumAttackCharge;
         if (number4 != null) {
            float it = ((Number)number4).floatValue();
            int value32 = 0;
            this.itemStack.setData(DataComponentTypes.MINIMUM_ATTACK_CHARGE, it);
         }

         SwingAnimation swingAnimation2 = this.swingAnimation;
         if (swingAnimation2 != null) {
            SwingAnimation it = swingAnimation2;
            int value33 = 0;
            this.itemStack.setData(DataComponentTypes.ATTACK_ANIMATION, it);
            this.itemStack.setData(DataComponentTypes.INTERACT_ANIMATION, it);
         }

         UseEffects useEffects2 = this.useEffects;
         if (useEffects2 != null) {
            UseEffects it = useEffects2;
            int value34 = 0;
            this.itemStack.setData(DataComponentTypes.USE_EFFECTS, it);
         }

         DamageType damageType2 = this.damageType;
         if (damageType2 != null) {
            DamageType it = damageType2;
            int value35 = 0;
            this.itemStack.setData(DataComponentTypes.DAMAGE_TYPE, it);
         }
      }

      if (NexoFurniture.isFurniture(this.itemStack)) {
         this.itemStack.editMeta((a0_ -> ItemBuilder.regenerateItem$lambda$38((b0_ -> ItemBuilder.regenerateItem$lambda$37(this, (ItemMeta)b0_)), a0_)));
      }

      for(Key component : this.unsetComponents) {
         ItemStack itemStack3 = this.itemStack;
         RegistryKey registryKey = RegistryKey.DATA_COMPONENT_TYPE;
         DataComponentType dataComponentType = (DataComponentType)RegistryUtilsKt.toRegistry(registryKey).get(component);
         if (dataComponentType != null) {
            itemStack3.unsetData(dataComponentType);
         }
      }

      this.finalItemStack = this.itemStack;
      return this;
   }

   public final void save() {
      org.bukkit.configuration.file.YamlConfiguration yamlConfiguration;
      java.util.List list;
      this.regenerateItem();
      Iterable entrySet = (Iterable)NexoItems.itemMap().entrySet();
      Iterator iterator = entrySet.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            Map.Entry it = (Map.Entry)element;
            int value = 0;
            if (!((Map)it.getValue()).containsValue(this)) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      Map.Entry entry = (Map.Entry)obj;
      if (entry != null) {
         File key = (File)entry.getKey();
         if (key != null) {
            File file = key;
            YamlConfiguration config = NexoYaml.Companion.loadConfiguration(file);
            String itemId = NexoItems.idFromItem(this);
            Color color2 = this.color;
            if (color2 != null) {
               Color color3 = color2;
               int value2 = 0;
               String str = itemId + ".color";
               int red = color3.getRed();
               config.set(str, red + "," + color3.getGreen() + "," + color3.getBlue());
            }

            if (this.hasTrimPattern()) {
               String str2 = itemId + ".trim_pattern";
               NamespacedKey namespacedKey = this.trimPatternKey;
               config.set(str2, namespacedKey.asString());
            }

            if (this.hasTrimMaterial()) {
               String str3 = itemId + ".trim_material";
               NamespacedKey namespacedKey2 = this.trimMaterialKey;
               config.set(str3, namespacedKey2.asString());
            }

            Iterable hiddenComponentKeys = (Iterable)this.hiddenComponentKeys();
            if (CollectionsKt.count(hiddenComponentKeys) > 0) {
               Set it = (Set)hiddenComponentKeys;
               int value3 = 0;
               String str4 = itemId + ".Components.tooltip_display";
               Iterable iterable = (Iterable)it;
               String str5 = str4;
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object item : iterable) {
                  Key p0 = (Key)item;
                  int value4 = 0;
                  destination.add(p0.asString());
               }

               List list2 = (List)destination;
               config.set(str5, list2);
            }

            if (this.hasEquippableComponent()) {
               String str6;
               String str7;
               block: {
                  str6 = itemId + ".Components.equippable.slot";
                  Equippable equippable2 = this.equippable;
                  if (equippable2 != null) {
                     EquipmentSlot slot = equippable2.slot();
                     if (slot != null) {
                        str7 = slot.name();
                        break block;
                     }
                  }

                  str7 = null;
               }

               block: {
                  config.set(str6, str7);
                  str6 = itemId + ".Components.equippable.asset_id";
                  Equippable equippable3 = this.equippable;
                  if (equippable3 != null) {
                     Key assetId = equippable3.assetId();
                     if (assetId != null) {
                        str7 = assetId.toString();
                        break block;
                     }
                  }

                  str7 = null;
               }

               block: {
                  config.set(str6, str7);
                  yamlConfiguration = config;
                  str6 = itemId + ".Components.equippable.allowed_entity_types";
                  Equippable equippable4 = this.equippable;
                  if (equippable4 != null) {
                     RegistryKeySet allowedEntities = equippable4.allowedEntities();
                     if (allowedEntities != null) {
                        RegistryKey registryKey = RegistryKey.ENTITY_TYPE;
                        Collection collection = allowedEntities.resolve(RegistryUtilsKt.toRegistry(registryKey));
                        if (collection != null) {
                           Iterable iterable2 = (Iterable)collection;
                           String str8 = str6;
                           Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

                           for(Object obj2 : iterable2) {
                              EntityType it = (EntityType)obj2;
                              int value5 = 0;
                              collection2.add(it.name());
                           }

                           List list3 = (List)collection2;
                           yamlConfiguration = config;
                           str6 = str8;
                           list = list3;
                           break block;
                        }
                     }
                  }

                  list = null;
               }

               yamlConfiguration.set(str6, list);
            }

            ItemBuilder itemBuilder = this;

            Object result1;
            try {
               ItemBuilder itemBuilder2 = itemBuilder;
               int it = 0;
               config.save(file);
               result1 = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable e) {
               result1 = ResultShim.ok(ResultKt.createFailure(e));
            }

            UtilsKt.printOnFailure(result1, true);
            return;
         }
      }

   }

   @NotNull
   public final ItemStack[] buildArray(int amount) {
      ItemStack built = this.build();
      int value;
      if (this.hasMaxStackSize()) {
         Integer number = this.maxStackSize;
         value = number;
      } else {
         value = this.type.getMaxStackSize();
      }

      int max = value;
      int rest = max == amount ? amount : amount % max;
      int iterations = amount > max ? (amount - rest) / max : 0;
      ItemStack[] output = new ItemStack[iterations + (rest > 0 ? 1 : 0)];

      for(int index = 0; index < iterations; ++index) {
         ItemUpdater.Companion companion = ItemUpdater.Companion;
         ItemStack itemStack2 = built.asQuantity(max);
         output[index] = companion.updateItem(itemStack2);
      }

      if (rest != 0) {
         ItemUpdater.Companion companion2 = ItemUpdater.Companion;
         ItemStack itemStack3 = built.asQuantity(rest);
         output[iterations] = companion2.updateItem(itemStack3);
      }

      return output;
   }

   @NotNull
   public final ItemStack build() {
      if (!this.cache) {
         String str = NexoItems.idFromItem(this);
         if (str != null) {
            String p0 = str;
            NexoItems nexoItems = NexoItems.INSTANCE;
            int value = 0;
            nexoItems.reloadItem(p0);
         }
      }

      if (this.finalItemStack == null) {
         this.regenerateItem();
      }

      ItemStack itemStack2 = this.finalItemStack;
      itemStack2 = itemStack2.clone();
      return itemStack2;
   }

   private static final void addEnchants$lambda$0(Function2 tmp0, Object p0, Object p1) {
      tmp0.invoke(p0, p1);
   }

   private static final void regenerateItem$lambda$18(ItemBuilder itemBuilder, PersistentDataContainer pdc) {
      for(Map.Entry entry : itemBuilder.persistentDataMap.entrySet()) {
         Companion.PersistentDataSpace key = (Companion.PersistentDataSpace)entry.getKey();
         Object value = entry.getValue();
         Companion.PersistentDataSpace persistentDataSpace = key;
         if (!(key instanceof Companion.PersistentDataSpace)) {
            persistentDataSpace = null;
         }

         if (persistentDataSpace != null) {
            Companion.PersistentDataSpace persistentDataSpace2 = persistentDataSpace;
            NamespacedKey dataKey = persistentDataSpace2.component1();
            PersistentDataType dataType = persistentDataSpace2.component2();
            if (!Intrinsics.areEqual(dataType.getComplexType(), value.getClass())) {
               if (Number.class.isAssignableFrom(dataType.getComplexType()) && value instanceof Number) {
                  Number number = (Number)value;
                  Object complexType = dataType.getComplexType();
                  if (complexType == null) {
                     throw new NullPointerException("null cannot be cast to non-null type java.lang.Class<kotlin.Number>");
                  }

                  Number newValue = UtilsKt.convertToTargetType(number, (Class)complexType);
                  pdc.set(dataKey, dataType, newValue);
               } else {
                  Logs.logError("Could not set PersistentData for key " + dataKey, false);
                  Logs.logError("Expected a " + dataType.getComplexType() + " but got " + value.getClass(), false);
               }
            } else {
               pdc.set(dataKey, dataType, value);
            }
         }
      }

   }

   private static final Unit regenerateItem$lambda$37(ItemBuilder itemBuilder, ItemMeta meta) {
      if (meta instanceof PotionMeta) {
         NMSHandlers.handler().itemUtils().consumableComponent(itemBuilder.itemStack, (Object)null);
         ((PotionMeta)meta).setFood((FoodComponent)null);
      } else if (meta instanceof LeatherArmorMeta) {
         LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta)meta;
         EquippableComponent equippableComponent = ((LeatherArmorMeta)meta).getEquippable();
         LeatherArmorMeta leatherArmorMeta2 = leatherArmorMeta;
         int value = 0;
         equippableComponent.setSlot(EquipmentSlot.HAND);
         leatherArmorMeta2.setEquippable(equippableComponent);
      }

      return Unit.INSTANCE;
   }

   private static final void regenerateItem$lambda$38(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   static {
      ORIGINAL_NAME_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "original_name");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\bB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/items/ItemBuilder$Companion;", "", "<init>", "()V", "ORIGINAL_NAME_KEY", "Lorg/bukkit/NamespacedKey;", "getORIGINAL_NAME_KEY", "()Lorg/bukkit/NamespacedKey;", "PersistentDataSpace", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getORIGINAL_NAME_KEY() {
         return ItemBuilder.ORIGINAL_NAME_KEY;
      }

      @Metadata(
         mv = {2, 4, 0},
         k = 1,
         xi = 48,
         d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u00020\u0003B#\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000e\u001a\u00020\u0005HÆ\u0003J\u0015\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0007HÆ\u0003J5\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u00002\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0007HÆ\u0001J\u0014\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÖ\u0083\u0004J\n\u0010\u0014\u001a\u00020\u0015HÖ\u0081\u0004J\n\u0010\u0016\u001a\u00020\u0017HÖ\u0081\u0004R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001d\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0018"},
         d2 = {"Lcom/nexomc/nexo/items/ItemBuilder$Companion$PersistentDataSpace;", "T", "Z", "", "key", "Lorg/bukkit/NamespacedKey;", "dataType", "Lorg/bukkit/persistence/PersistentDataType;", "<init>", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;)V", "getKey", "()Lorg/bukkit/NamespacedKey;", "getDataType", "()Lorg/bukkit/persistence/PersistentDataType;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
      )
      public static final class PersistentDataSpace<T, Z> {
         @NotNull
         private final NamespacedKey key;
         @NotNull
         private final PersistentDataType<T, Z> dataType;

         public PersistentDataSpace(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> dataType) {
            super();
            this.key = key;
            this.dataType = dataType;
         }

         @NotNull
         public final NamespacedKey getKey() {
            return this.key;
         }

         @NotNull
         public final PersistentDataType<T, Z> getDataType() {
            return this.dataType;
         }

         @NotNull
         public final NamespacedKey component1() {
            return this.key;
         }

         @NotNull
         public final PersistentDataType<T, Z> component2() {
            return this.dataType;
         }

         @NotNull
         public final PersistentDataSpace<T, Z> copy(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> dataType) {
            return new PersistentDataSpace<T, Z>(key, dataType);
         }

         @NotNull
         public String toString() {
            return "PersistentDataSpace(key=" + this.key + ", dataType=" + this.dataType + ")";
         }

         public int hashCode() {
            int result = this.key.hashCode();
            result = result * 31 + this.dataType.hashCode();
            return result;
         }

         public boolean equals(@Nullable Object other) {
            if (this == other) {
               return true;
            } else if (!(other instanceof PersistentDataSpace)) {
               return false;
            } else {
               PersistentDataSpace persistentDataSpace = (PersistentDataSpace)other;
               if (!Intrinsics.areEqual(this.key, persistentDataSpace.key)) {
                  return false;
               } else {
                  return Intrinsics.areEqual(this.dataType, persistentDataSpace.dataType);
               }
            }
         }
      }
   }

   public static final void access$addEnchants$addEnchant(ItemBuilder itemBuilder, Enchantment enchant, int level) { itemBuilder.addEnchant(enchant, level); }
}
