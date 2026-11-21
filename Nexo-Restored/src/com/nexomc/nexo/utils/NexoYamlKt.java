package com.nexomc.nexo.utils;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.commands.DyeCommandKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000Þ\u0001\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010$\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0010\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\n\u0010\b\u001a\u00020\t*\u00020\u0002\u001a\u0012\u0010\n\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0001\u001a\n\u0010\f\u001a\u00020\u0002*\u00020\u0002\u001a\n\u0010\r\u001a\u00020\u000e*\u00020\u0002\u001a\u0014\u0010\u000f\u001a\u00020\u0002*\u00020\u00022\b\u0010\u0010\u001a\u0004\u0018\u00010\u0002\u001a\u0012\u0010\u0011\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u0002\u001a\u0012\u0010\u0012\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u0002\u001a\u0016\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00020\u0014*\u00020\u0002\u001a\u0018\u0010\u0015\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0014*\u00020\u0002\u001a\u0018\u0010\u0017\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0014*\u00020\u0002\u001a&\u0010\u0018\u001a\u001e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0019j\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0016`\u001a*\u00020\u0002\u001a\u001e\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u0002H\u001c0\u0014\"\u0004\b\u0000\u0010\u001c*\u00020\u0002H\u0000\u001a2\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u0002H\u001c0\u0014\"\u0004\b\u0000\u0010\u001c*\u00020\u00022\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u0002H\u001c0\u001eH\u0000\u001a`\u0010\u001f\u001a$\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0019j\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0016`\u001a0 *\u00020\u00022\u0006\u0010!\u001a\u00020\u00012*\b\u0002\u0010\"\u001a$\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0019j\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0016`\u001a0 \u001a6\u0010#\u001a&\u0012 \u0012\u001e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00160\u0019j\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u0016`\u001a\u0018\u00010 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a*\u0010$\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u0002H\u001c0\u00140 \"\u0004\b\u0000\u0010\u001c*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0014\u0010%\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0012\u0010&\u001a\u00020\u0001*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001a\u0010'\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a$\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00010 *\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0001\u001a\u0014\u0010*\u001a\u0004\u0018\u00010+*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0019\u0010,\u001a\u0004\u0018\u00010-*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001¢\u0006\u0002\u0010.\u001a\u0014\u0010/\u001a\u0004\u0018\u000100*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a(\u0010/\u001a\u0004\u0018\u000100*\u00020\u00022\u0006\u00101\u001a\u00020\u00012\u0006\u00102\u001a\u00020\u00012\n\b\u0002\u00103\u001a\u0004\u0018\u00010\u0001\u001a\u0019\u00104\u001a\u0004\u0018\u000105*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001¢\u0006\u0002\u00106\u001a\u001a\u00107\u001a\n\u0012\u0004\u0012\u000209\u0018\u000108*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a.\u00107\u001a\n\u0012\u0004\u0012\u000209\u0018\u000108*\u00020\u00022\u0006\u00101\u001a\u00020\u00012\u0006\u00102\u001a\u00020\u00012\n\b\u0002\u00103\u001a\u0004\u0018\u00010\u0001\u001a\u0014\u0010!\u001a\u0004\u0018\u00010:*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001c\u0010!\u001a\u0004\u0018\u00010:*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\u0001\u001a\u001a\u0010!\u001a\u00020:*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020:\u001a\u0018\u0010;\u001a\b\u0012\u0004\u0012\u00020:0 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001a\u0010<\u001a\n\u0012\u0004\u0012\u00020:\u0018\u00010 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a$\u0010=\u001a\b\u0012\u0004\u0012\u00020:0 *\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0001\u001a \u0010>\u001a\u0010\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020:\u0018\u00010\u0014*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a*\u0010?\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020:0\u0014*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0001\u001a\u001a\u0010@\u001a\u00020A*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\u0001\u001a\u001a\u0010@\u001a\u00020A*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020A\u001a\u0014\u0010@\u001a\u0004\u0018\u00010A*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0018\u0010B\u001a\b\u0012\u0004\u0012\u00020A0 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001a\u0010C\u001a\n\u0012\u0004\u0012\u00020A\u0018\u00010 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a$\u0010D\u001a\b\u0012\u0004\u0012\u00020A0 *\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0001\u001a.\u0010E\u001a\u0004\u0018\u0001H\u001c\"\u0010\b\u0000\u0010\u001c\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u001c0F*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001H\u0086\b¢\u0006\u0002\u0010G\u001a4\u0010E\u001a\u0002H\u001c\"\u0010\b\u0000\u0010\u001c\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u001c0F*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u0002H\u001cH\u0086\b¢\u0006\u0002\u0010H\u001a6\u0010I\u001a\b\u0012\u0004\u0012\u0002H\u001c0 \"\u000e\b\u0000\u0010\u001c*\b\u0012\u0004\u0012\u0002H\u001c0F*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\f\u0010E\u001a\b\u0012\u0004\u0012\u0002H\u001c0J\u001aB\u0010K\u001a\b\u0012\u0004\u0012\u0002H\u001c0 \"\u000e\b\u0000\u0010\u001c*\b\u0012\u0004\u0012\u0002H\u001c0F*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\f\u0010E\u001a\b\u0012\u0004\u0012\u0002H\u001c0J2\n\b\u0002\u0010)\u001a\u0004\u0018\u00010\u0001\u001a\u0014\u0010L\u001a\u0004\u0018\u00010M*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0012\u0010N\u001a\u00020O*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0012\u0010P\u001a\u00020Q*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001a\u0010P\u001a\u00020Q*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u000209\u001a\u001c\u0010R\u001a\u00020S*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\b\b\u0002\u0010\"\u001a\u00020-\u001a\u001c\u0010T\u001a\u00020U*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\b\b\u0002\u0010\"\u001a\u000209\u001a\u0012\u0010V\u001a\u00020W*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001c\u0010X\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\b\u0010Y\u001a\u0004\u0018\u00010\u0016\u001a\u001a\u0010Z\u001a\u00020\u0002*\u00020\u00022\u0006\u0010[\u001a\u00020\u00012\u0006\u0010\\\u001a\u00020\u0001\u001a\u0012\u0010]\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u001a\u0010^\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010_\u001a\u00020\t\u001a \u0010^\u001a\u00020\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\f\u0010_\u001a\b\u0012\u0004\u0012\u00020\t0`\u001a8\u0010^\u001a\u00020\u0002\"\n\b\u0000\u0010\u001c\u0018\u0001*\u00020\u0016*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0012\u0010_\u001a\u000e\u0012\u0004\u0012\u0002H\u001c\u0012\u0004\u0012\u00020\t0\u001eH\u0086\bø\u0001\u0000\u001a\u001a\u0010a\u001a\u000209*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u000209\u001a\u0019\u0010b\u001a\u0004\u0018\u000109*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001¢\u0006\u0002\u0010c\u001a \u0010d\u001a\u0004\u0018\u00010e*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010f\u001a\u0004\u0018\u00010g\u001a#\u0010h\u001a\u00020e*\u00020\u00022\u0006\u0010!\u001a\u00020\u00012\n\b\u0002\u0010f\u001a\u0004\u0018\u00010g¢\u0006\u0002\u0010i\u001a\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u0002*\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0018\u0010j\u001a\b\u0012\u0004\u0012\u00020\u00020 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a\u0018\u0010k\u001a\b\u0012\u0004\u0012\u00020\u00020 *\u00020\u00022\u0006\u0010!\u001a\u00020\u0001\u001a&\u0010l\u001a\u00020\u000e*\u00020\u00022\u001a\u0010m\u001a\u0016\u0012\u0004\u0012\u00020\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u0016\u0012\u0004\u0012\u00020\u000e0n\u001a`\u0010o\u001a\u000e\u0012\u0004\u0012\u0002H\u001c\u0012\u0004\u0012\u0002Hq0p\"\b\b\u0000\u0010\u001c*\u00020\u0016\"\b\b\u0001\u0010q*\u00020\u0016*\u00020\u00022\u0019\u0010r\u001a\u0015\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u001c0\u001e¢\u0006\u0002\bs2\u0019\u0010t\u001a\u0015\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u0001Hq0\u001e¢\u0006\u0002\bs\u001ab\u0010u\u001a\u0010\u0012\u0004\u0012\u0002H\u001c\u0012\u0004\u0012\u0002Hq\u0018\u00010p\"\b\b\u0000\u0010\u001c*\u00020\u0016\"\b\b\u0001\u0010q*\u00020\u0016*\u00020\u00022\u0019\u0010r\u001a\u0015\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u001c0\u001e¢\u0006\u0002\bs2\u0019\u0010t\u001a\u0015\u0012\u0004\u0012\u00020\u0002\u0012\u0006\u0012\u0004\u0018\u0001Hq0\u001e¢\u0006\u0002\bs\"\u0015\u0010\u0000\u001a\u00020\u0001*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004\"\u0015\u0010\u0005\u001a\u00020\u0002*\u00020\u00028F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006v"},
   d2 = {"rootId", "", "Lorg/bukkit/configuration/ConfigurationSection;", "getRootId", "(Lorg/bukkit/configuration/ConfigurationSection;)Ljava/lang/String;", "rootSection", "getRootSection", "(Lorg/bukkit/configuration/ConfigurationSection;)Lorg/bukkit/configuration/ConfigurationSection;", "isEmpty", "", "copyWithParent", "rootName", "clone", "clear", "", "copyFrom", "section", "copyTo", "moveTo", "childSections", "", "toMap", "", "toSerializableMap", "toLinkedMap", "Ljava/util/LinkedHashMap;", "Lkotlin/collections/LinkedHashMap;", "toTypedMap", "T", "block", "Lkotlin/Function1;", "linkedMapList", "", "key", "default", "linkedMapListOrNull", "typedMapList", "stringOrNull", "stringOrEmpty", "stringListOrNull", "stringListOrSingle", "altKey", "color", "Lorg/bukkit/Color;", "intOrNull", "", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)Ljava/lang/Integer;", "intRangeOrNull", "Lkotlin/ranges/IntRange;", "minKey", "maxKey", "rangeKey", "doubleOrNull", "", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)Ljava/lang/Double;", "floatRangeOrNull", "Lkotlin/ranges/ClosedFloatingPointRange;", "", "Lnet/kyori/adventure/key/Key;", "keyList", "keyListOrNull", "keyListOrSingle", "keyMapOrNull", "keyMapOrSingle", "namespacedKey", "Lorg/bukkit/NamespacedKey;", "namespacedKeyList", "namespacedKeyListOrNull", "namespacedKeyListOrSingle", "enum", "", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)Ljava/lang/Enum;", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;Ljava/lang/Enum;)Ljava/lang/Enum;", "enumList", "Ljava/lang/Class;", "enumListOrSingle", "quaternionf", "Lorg/joml/Quaternionf;", "vector", "Lorg/bukkit/util/Vector;", "vector3f", "Lorg/joml/Vector3f;", "vector2i", "Lorg/joml/Vector2i;", "vector2f", "Lorg/joml/Vector2f;", "triState", "Lcom/nexomc/nexo/utils/TriState;", "plus", "value", "rename", "oldKey", "newKey", "remove", "removeIf", "bool", "Lkotlin/Function0;", "float", "floatOrNull", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;)Ljava/lang/Float;", "durationOrNull", "Lkotlin/time/Duration;", "defaultUnit", "Lkotlin/time/DurationUnit;", "duration", "(Lorg/bukkit/configuration/ConfigurationSection;Ljava/lang/String;Lkotlin/time/DurationUnit;)J", "sectionList", "sectionListOrSingle", "forEach", "action", "Lkotlin/Function2;", "getEither", "Lcom/nexomc/nexo/utils/Either;", "K", "left", "Lkotlin/ExtensionFunctionType;", "right", "getEitherOrNull", "com.nexomc:core"}
)
public final class NexoYamlKt {
   @NotNull
   public static final String getRootId(@NotNull ConfigurationSection configurationSection) {
      String name = getRootSection(configurationSection).getName();
      return name;
   }

   @NotNull
   public static final ConfigurationSection getRootSection(@NotNull ConfigurationSection configurationSection2) {

      ConfigurationSection current;
      ConfigurationSection configurationSection;
      for(current = configurationSection2; current.getParent() != null; current = configurationSection) {
         configurationSection = current.getParent();
         if (configurationSection.getParent() == null) {
            break;
         }

         configurationSection = current.getParent();
      }

      return current;
   }

   public static final boolean isEmpty(@NotNull ConfigurationSection configurationSection) {
      return configurationSection.getKeys(false).isEmpty();
   }

   @NotNull
   public static final ConfigurationSection copyWithParent(@NotNull ConfigurationSection configurationSection2, @NotNull String rootName) {
      ConfigurationSection configurationSection = (new YamlConfiguration()).createSection(rootName);
      ConfigurationSection parent = configurationSection;
      configurationSection = parent.createSection(configurationSection2.getName());
      return copyFrom(configurationSection, configurationSection2);
   }

   @NotNull
   public static final ConfigurationSection clone(@NotNull ConfigurationSection configurationSection2) {
      ConfigurationSection configurationSection = (new YamlConfiguration()).createSection(configurationSection2.getName());
      return copyFrom(configurationSection, configurationSection2);
   }

   public static final void clear(@NotNull ConfigurationSection configurationSection) {
      Set keys = configurationSection.getKeys(false);
      Iterable iterable = (Iterable)keys;

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         remove(configurationSection, it);
      }

   }

   @NotNull
   public static final ConfigurationSection copyFrom(@NotNull ConfigurationSection configurationSection, @Nullable ConfigurationSection section) {
      if (section != null) {
         int value = 0;
         NexoYaml.Companion.copyConfigurationSection(section, configurationSection);
      }

      return configurationSection;
   }

   @NotNull
   public static final ConfigurationSection copyTo(@NotNull ConfigurationSection configurationSection, @NotNull ConfigurationSection section) {
      NexoYaml.Companion.copyConfigurationSection(configurationSection, section);
      return section;
   }

   @NotNull
   public static final ConfigurationSection moveTo(@NotNull ConfigurationSection configurationSection, @NotNull ConfigurationSection section) {
      copyTo(configurationSection, section);
      ConfigurationSection parent = configurationSection.getParent();
      if (parent != null) {
         parent.set(configurationSection.getName(), (Object)null);
      } else {
         Configuration root = configurationSection.getRoot();
         if (root != null) {
            root.set(configurationSection.getName(), (Object)null);
         }
      }

      return section;
   }

   @NotNull
   public static final Map<String, ConfigurationSection> childSections(@NotNull ConfigurationSection configurationSection) {
      Map values = configurationSection.getValues(false);
      Map map = values;
      Collection destination = (Collection)(new ArrayList());

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value = 0;
         int value2 = 0;
         String key = (String)element.getKey();
         Object obj = element.getValue();
         Pair pair = obj instanceof ConfigurationSection ? kotlin.TuplesKt.to(key, obj) : null;
         if (pair != null) {
            Object it = pair;
            int value3 = 0;
            destination.add(it);
         }
      }

      return MapsKt.toMap((Iterable)((List)destination));
   }

   @NotNull
   public static final Map<String, Object> toMap(@NotNull ConfigurationSection configurationSection2) {
      Set keys = configurationSection2.getKeys(false);
      Iterable iterable = (Iterable)keys;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         Object obj = configurationSection2.get(it);
         if (obj != null) {
            block: {
               Object v = obj;
               int value2 = 0;
               ConfigurationSection configurationSection = v instanceof ConfigurationSection ? (ConfigurationSection)v : null;
               if ((v instanceof ConfigurationSection ? (ConfigurationSection)v : null) != null) {
                  obj = toMap(configurationSection);
                  if (obj != null) {
                     break block;
                  }
               }

               obj = v;
            }
         } else {
            obj = null;
         }

         Object obj2 = obj;
         destination.put(element, obj2);
      }

      return destination;
   }

   @NotNull
   public static final Map<String, Object> toSerializableMap(@NotNull ConfigurationSection configurationSection) {
      Map values = configurationSection.getValues(false);
      Map map = values;
      Map destination = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map.size())));
      Iterable entrySet = (Iterable)map.entrySet();
      Map map2 = destination;

      for(Object element : entrySet) {
         Map.Entry it = (Map.Entry)element;
         int value2 = 0;
         Object key = it.getKey();
         Map.Entry entry = (Map.Entry)element;
         Object obj = key;
         int value3 = 0;
         Object value = entry.getValue();
         Object obj2;
         if (value instanceof ConfigurationSection) {
            obj2 = toSerializableMap((ConfigurationSection)value);
         } else if (!(value instanceof List)) {
            obj2 = value;
         } else {
            Iterable iterable = (Iterable)value;
            Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               int value4 = 0;
               collection.add(item instanceof ConfigurationSection ? toSerializableMap((ConfigurationSection)item) : item);
            }

            obj2 = (List)collection;
         }

         Object obj3 = obj2;
         map2.put(obj, obj3);
      }

      return map2;
   }

   @NotNull
   public static final LinkedHashMap<String, Object> toLinkedMap(@NotNull ConfigurationSection configurationSection) {
      Set keys = configurationSection.getKeys(false);
      Iterable iterable = (Iterable)keys;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         String p0 = (String)element;
         int value = 0;
         Object obj = configurationSection.get(p0);
         destination.put(element, obj);
      }

      return (LinkedHashMap)MapsKt.toMap(CollectionUtilsKt.filterValuesNotNull(destination), (Map)(new LinkedHashMap()));
   }

   @NotNull
   public static final <T> Map<String, T> toTypedMap(@NotNull ConfigurationSection configurationSection) {
      Set keys = configurationSection.getKeys(false);
      Iterable iterable = (Iterable)keys;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         Object obj = configurationSection.get(it);
         if (obj == null) {
            obj = null;
         }

         Object obj2 = obj;
         destination.put(element, obj2);
      }

      return CollectionUtilsKt.<String, T>filterValuesNotNull(destination);
   }

   @NotNull
   public static final <T> Map<String, T> toTypedMap(@NotNull ConfigurationSection configurationSection, @NotNull Function1<Object, ? extends T> block) {
      Set keys = configurationSection.getKeys(false);
      Iterable iterable = (Iterable)keys;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         Object obj = configurationSection.get(it);
         if (obj != null) {
            Object a = obj;
            int value2 = 0;
            obj = block.invoke(a);
         } else {
            obj = null;
         }

         Object obj2 = obj;
         destination.put(element, obj2);
      }

      return CollectionUtilsKt.<String, T>filterValuesNotNull(destination);
   }

   @NotNull
   public static final List<LinkedHashMap<String, Object>> linkedMapList(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull List<? extends LinkedHashMap<String, Object>> list) {
      List list2 = configurationSection.getList(key);
      if (list2 != null) {
         Iterable iterable = (Iterable)list2;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            if (element instanceof LinkedHashMap) {
               destination.add(element);
            }
         }

         list2 = (List)destination;
      } else {
         list2 = list;
      }

      return list2;
   }

   @Nullable
   public static final List<LinkedHashMap<String, Object>> linkedMapListOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      List list = configurationSection.getList(key);
      if (list != null) {
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            if (element instanceof LinkedHashMap) {
               destination.add(element);
            }
         }

         list = (List)destination;
      } else {
         list = null;
      }

      return list;
   }

   @NotNull
   public static final <T> List<Map<String, T>> typedMapList(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      List mapList = configurationSection.getMapList(key);
      Iterable iterable = (Iterable)mapList;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         if (element instanceof Map) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   @Nullable
   public static final String stringOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      Object obj = configurationSection.get(key);
      String str = obj instanceof String ? (String)obj : null;
      if ((obj instanceof String ? (String)obj : null) != null) {
         CharSequence charSequence = (CharSequence)str;
         CharSequence charSequence2;
         if (charSequence.length() == 0) {
            int value = 0;
            charSequence2 = null;
         } else {
            charSequence2 = charSequence;
         }

         str = (String)charSequence2;
      } else {
         str = null;
      }

      return str;
   }

   @NotNull
   public static final String stringOrEmpty(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String string = configurationSection.getString(key);
      if (string == null) {
         string = "";
      }

      return string;
   }

   @Nullable
   public static final List<String> stringListOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      List stringList = configurationSection.getStringList(key);
      Iterable iterable = (Iterable)stringList;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         String it = (String)element;
         int value = 0;
         CharSequence charSequence = (CharSequence)it;
         if (charSequence != null && charSequence.length() != 0) {
            destination.add(element);
         }
      }

      Collection collection = (Collection)((List)destination);
      Collection collection2;
      if (collection.isEmpty()) {
         collection2 = null;
      } else {
         collection2 = collection;
      }

      return (List)collection2;
   }

   @NotNull
   public static final List<String> stringListOrSingle(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable String altKey) {
      List list = stringListOrNull(configurationSection, key);
      if (list != null) {
         Iterable iterable = (Iterable)list;
         if (CollectionsKt.count(iterable) > 0) {
            List it = (List)iterable;
            int value = 0;
            return it;
         }
      }

      String fallbackKey = stringOrNull(configurationSection, key);
      if (fallbackKey != null) {
         return CollectionsKt.listOf(fallbackKey);
      } else {
         fallbackKey = StringUtilsKt.ifNullOrEmpty(altKey, StringsKt.endsWith(key, "s", false) ? StringsKt.removeSuffix(key, (CharSequence)"s") : key + "s");
         if (Intrinsics.areEqual(fallbackKey, key)) {
            return CollectionsKt.emptyList();
         } else {
            list = stringListOrNull(configurationSection, fallbackKey);
            if (list != null) {
               Iterable iterable2 = (Iterable)list;
               if (CollectionsKt.count(iterable2) > 0) {
                  List it = (List)iterable2;
                  int value2 = 0;
                  return it;
               }
            }

            String it = stringOrNull(configurationSection, fallbackKey);
            if (it != null) {
               int value3 = 0;
               return CollectionsKt.listOf(it);
            } else {
               return CollectionsKt.emptyList();
            }
         }
      }
   }

   @Nullable
   public static final Color color(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      return str != null ? DyeCommandKt.toColor(str) : null;
   }

   @Nullable
   public static final Integer intOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      return configurationSection.isInt(key) ? configurationSection.getInt(key) : null;
   }

   @Nullable
   public static final IntRange intRangeOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      return str != null ? RangeUtilsKt.toIntRangeOrNull(str) : null;
   }

   @Nullable
   public static final IntRange intRangeOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String minKey, @NotNull String maxKey, @Nullable String rangeKey) {
      IntRange intRange;
      if (rangeKey != null) {
         int value = 0;
         intRange = intRangeOrNull(configurationSection, rangeKey);
      } else {
         intRange = null;
      }

      IntRange rangeKey_16 = intRange;
      Integer number = intOrNull(configurationSection, minKey);
      if (number != null) {
         int min = number;
         number = intOrNull(configurationSection, maxKey);
         if (number != null) {
            int p0 = number;
            return new IntRange(min, p0);
         } else {
            return rangeKey_16;
         }
      } else {
         return rangeKey_16;
      }
   }

   @Nullable
   public static final Double doubleOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      return configurationSection.isDouble(key) ? configurationSection.getDouble(key) : (configurationSection.isInt(key) ? (double)configurationSection.getInt(key) : null);
   }

   @Nullable
   public static final ClosedFloatingPointRange<Float> floatRangeOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      return str != null ? RangeUtilsKt.toFloatRangeOrNull(str) : null;
   }

   @Nullable
   public static final ClosedFloatingPointRange<Float> floatRangeOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String minKey, @NotNull String maxKey, @Nullable String rangeKey) {
      ClosedFloatingPointRange closedFloatingPointRange;
      if (rangeKey != null) {
         int value = 0;
         closedFloatingPointRange = floatRangeOrNull(configurationSection, rangeKey);
      } else {
         closedFloatingPointRange = null;
      }

      ClosedFloatingPointRange rangeKey_15 = closedFloatingPointRange;
      Float number = floatOrNull(configurationSection, minKey);
      if (number != null) {
         float min = number;
         number = floatOrNull(configurationSection, maxKey);
         if (number != null) {
            float p0 = number;
            return RangesKt.rangeTo(min, p0);
         } else {
            return rangeKey_15;
         }
      } else {
         return rangeKey_15;
      }
   }

   @Nullable
   public static final Key key(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key);
         Key key2;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            key2 = Key.key(p0);
         } else {
            key2 = null;
         }

         obj = ResultShim.ok(key2);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (Key)(ResultShim.isFailure(obj) ? null : obj);
   }

   @Nullable
   public static final Key key(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull String str) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key, str);
         Key key2;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            key2 = Key.key(p0);
         } else {
            key2 = null;
         }

         obj = ResultShim.ok(key2);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (Key)(ResultShim.isFailure(obj) ? null : obj);
   }

   @NotNull
   public static final Key key(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull Key key2) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key);
         Key key3;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            key3 = Key.key(p0);
         } else {
            key3 = null;
         }

         obj = ResultShim.ok(key3);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Key key4 = (Key)(ResultShim.isFailure(obj) ? null : obj);
      if (key4 == null) {
         key4 = key2;
      }

      return key4;
   }

   @NotNull
   public static final List<Key> keyList(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         int value = 0;
         List stringList = configurationSection.getStringList(key);
         Iterable iterable = (Iterable)stringList;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            String it = (String)element;
            int value3 = 0;

            Object obj3;
            try {
               int value4 = 0;
               obj3 = ResultShim.ok(Key.key(it));
            } catch (Throwable e) {
               obj3 = ResultShim.ok(ResultKt.createFailure(e));
            }

            Key key2 = (Key)(ResultShim.isFailure(obj3) ? null : obj3);
            if (key2 != null) {
               Object obj4 = key2;
               int value5 = 0;
               destination.add(obj4);
            }
         }

         obj2 = ResultShim.ok((List)destination);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      return (List)(ResultShim.isFailure(obj) ? emptyList : obj);
   }

   @Nullable
   public static final List<Key> keyListOrNull(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         int value = 0;
         List stringList = configurationSection.getStringList(key);
         Iterable iterable = (Iterable)stringList;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            String it = (String)element;
            int value3 = 0;

            Object obj3;
            try {
               int value4 = 0;
               obj3 = ResultShim.ok(Key.key(it));
            } catch (Throwable e) {
               obj3 = ResultShim.ok(ResultKt.createFailure(e));
            }

            Key key2 = (Key)(ResultShim.isFailure(obj3) ? null : obj3);
            if (key2 != null) {
               Object obj4 = key2;
               int value5 = 0;
               destination.add(obj4);
            }
         }

         obj2 = ResultShim.ok((List)destination);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      Collection collection = (Collection)(ResultShim.isFailure(obj) ? emptyList : obj);
      Collection collection2;
      if (collection.isEmpty()) {
         int value6 = 0;
         collection2 = null;
      } else {
         collection2 = collection;
      }

      return (List)collection2;
   }

   @NotNull
   public static final List<Key> keyListOrSingle(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable String altKey) {
      List it = keyListOrNull(configurationSection, key);
      if (it != null) {
         int value = 0;
         List it_11 = !((Collection)it).isEmpty() ? it : null;
         if (it_11 != null) {
            value = 0;
            return it_11;
         }
      }

      Key it_10 = key(configurationSection, key);
      if (it_10 != null) {
         int it_9 = 0;
         return CollectionsKt.listOf(it_10);
      } else {
         String fallbackKey = StringUtilsKt.ifNullOrEmpty(altKey, StringsKt.endsWith(key, "s", false) ? StringsKt.removeSuffix(key, (CharSequence)"s") : key + "s");
         if (Intrinsics.areEqual(fallbackKey, key)) {
            return CollectionsKt.emptyList();
         } else {
            List it_8 = keyListOrNull(configurationSection, fallbackKey);
            if (it_8 != null) {
               int value2 = 0;
               List it_7 = !((Collection)it_8).isEmpty() ? it_8 : null;
               if (it_7 != null) {
                  value2 = 0;
                  return it_7;
               }
            }

            Key it_6 = key(configurationSection, fallbackKey);
            if (it_6 != null) {
               int value3 = 0;
               return CollectionsKt.listOf(it_6);
            } else {
               return CollectionsKt.emptyList();
            }
         }
      }
   }

   @Nullable
   public static final Map<String, Key> keyMapOrNull(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         ConfigurationSection configurationSection3 = configurationSection;
         int value = 0;
         Set keys = configurationSection.getKeys(false);
         Iterable iterable = (Iterable)keys;
         LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
         Map destination = (Map)result;

         for(Object element : iterable) {
            String it = (String)element;
            int value2 = 0;
            Key key2 = key(configurationSection3, it);
            destination.put(element, key2);
         }

         obj2 = ResultShim.ok(CollectionUtilsKt.filterValuesNotNull(destination));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Map emptyMap = MapsKt.emptyMap();
      Map map = (Map)(ResultShim.isFailure(obj) ? emptyMap : obj);
      Map map2;
      if (map.isEmpty()) {
         int value3 = 0;
         map2 = null;
      } else {
         map2 = map;
      }

      return map2;
   }

   @NotNull
   public static final Map<String, Key> keyMapOrSingle(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable String altKey) {
      Map map = keyMapOrNull(configurationSection, key);
      if (map == null) {
         Key key2 = key(configurationSection, key);
         if (key2 != null) {
            Key it = key2;
            int value = 0;
            map = MapsKt.mapOf(kotlin.TuplesKt.to(key, it));
         } else {
            map = null;
         }

         if (map == null) {
            map = MapsKt.emptyMap();
         }
      }

      Map map2 = map;
      if (map2.isEmpty()) {
         int value2 = 0;
         map = altKey != null ? keyMapOrSingle(configurationSection, altKey, (String)null) : MapsKt.emptyMap();
      } else {
         map = map2;
      }

      return map;
   }

   @NotNull
   public static final NamespacedKey namespacedKey(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull String str) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key);
         NamespacedKey namespacedKey;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            namespacedKey = NamespacedKey.fromString(p0);
         } else {
            namespacedKey = null;
         }

         obj = ResultShim.ok(namespacedKey);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      NamespacedKey namespacedKey2 = (NamespacedKey)(ResultShim.isFailure(obj) ? null : obj);
      if (namespacedKey2 == null) {
         namespacedKey2 = NamespacedKey.fromString(str);
      }

      return namespacedKey2;
   }

   @NotNull
   public static final NamespacedKey namespacedKey(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull NamespacedKey namespacedKey) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key);
         NamespacedKey namespacedKey2;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            namespacedKey2 = NamespacedKey.fromString(p0);
         } else {
            namespacedKey2 = null;
         }

         obj = ResultShim.ok(namespacedKey2);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      NamespacedKey namespacedKey3 = (NamespacedKey)(ResultShim.isFailure(obj) ? null : obj);
      if (namespacedKey3 == null) {
         namespacedKey3 = namespacedKey;
      }

      return namespacedKey3;
   }

   @Nullable
   public static final NamespacedKey namespacedKey(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      ConfigurationSection configurationSection2 = configurationSection;

      Object obj;
      try {
         int value = 0;
         String string = configurationSection2.getString(key);
         NamespacedKey namespacedKey;
         if (string != null) {
            String p0 = string;
            int value2 = 0;
            namespacedKey = NamespacedKey.fromString(p0);
         } else {
            namespacedKey = null;
         }

         obj = ResultShim.ok(namespacedKey);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      return (NamespacedKey)(ResultShim.isFailure(obj) ? null : obj);
   }

   @NotNull
   public static final List<NamespacedKey> namespacedKeyList(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         int value = 0;
         List stringList = configurationSection.getStringList(key);
         Iterable iterable = (Iterable)stringList;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            String it = (String)element;
            int value3 = 0;

            Object obj3;
            try {
               int value4 = 0;
               obj3 = ResultShim.ok(NamespacedKey.fromString(it));
            } catch (Throwable e) {
               obj3 = ResultShim.ok(ResultKt.createFailure(e));
            }

            NamespacedKey namespacedKey = (NamespacedKey)(ResultShim.isFailure(obj3) ? null : obj3);
            if (namespacedKey != null) {
               Object obj4 = namespacedKey;
               int value5 = 0;
               destination.add(obj4);
            }
         }

         obj2 = ResultShim.ok((List)destination);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      return (List)(ResultShim.isFailure(obj) ? emptyList : obj);
   }

   @Nullable
   public static final List<NamespacedKey> namespacedKeyListOrNull(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         int value = 0;
         List stringList = configurationSection.getStringList(key);
         Iterable iterable = (Iterable)stringList;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            String it = (String)element;
            int value3 = 0;

            Object obj3;
            try {
               int value4 = 0;
               obj3 = ResultShim.ok(NamespacedKey.fromString(it));
            } catch (Throwable e) {
               obj3 = ResultShim.ok(ResultKt.createFailure(e));
            }

            NamespacedKey namespacedKey = (NamespacedKey)(ResultShim.isFailure(obj3) ? null : obj3);
            if (namespacedKey != null) {
               Object obj4 = namespacedKey;
               int value5 = 0;
               destination.add(obj4);
            }
         }

         obj2 = ResultShim.ok((List)destination);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      Collection collection = (Collection)(ResultShim.isFailure(obj) ? emptyList : obj);
      Collection collection2;
      if (collection.isEmpty()) {
         int value6 = 0;
         collection2 = null;
      } else {
         collection2 = collection;
      }

      return (List)collection2;
   }

   @NotNull
   public static final List<NamespacedKey> namespacedKeyListOrSingle(@NotNull ConfigurationSection configurationSection2, @NotNull String key, @Nullable String altKey) {
      ConfigurationSection configurationSection = configurationSection2;

      Object obj2;
      try {
         int value = 0;
         Iterable iterable = (Iterable)stringListOrSingle(configurationSection, key, altKey);
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value2 = 0;
            String it = (String)element;
            int value3 = 0;

            Object obj3;
            try {
               int value4 = 0;
               obj3 = ResultShim.ok(NamespacedKey.fromString(it));
            } catch (Throwable e) {
               obj3 = ResultShim.ok(ResultKt.createFailure(e));
            }

            NamespacedKey namespacedKey = (NamespacedKey)(ResultShim.isFailure(obj3) ? null : obj3);
            if (namespacedKey != null) {
               Object obj4 = namespacedKey;
               int value5 = 0;
               destination.add(obj4);
            }
         }

         obj2 = ResultShim.ok((List)destination);
      } catch (Throwable ex) {
         obj2 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj = obj2;
      List emptyList = CollectionsKt.emptyList();
      return (List)(ResultShim.isFailure(obj) ? emptyList : obj);
   }

   @NotNull
   public static final <T extends Enum<T>> List<T> enumList(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull Class<T> clazz) {
      List stringList = configurationSection.getStringList(key);
      Iterable iterable = (Iterable)stringList;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String it = (String)element;
         int value2 = 0;
         Enum enumValue2 = org.apache.commons.lang3.EnumUtils.getEnum(clazz, it);
         if (enumValue2 != null) {
            Object obj = enumValue2;
            int value3 = 0;
            destination.add(obj);
         }
      }

      return (List)destination;
   }

   @NotNull
   public static final <T extends Enum<T>> List<T> enumListOrSingle(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull Class<T> clazz, @Nullable String altKey) {
      Iterable iterable = (Iterable)stringListOrSingle(configurationSection, key, altKey);
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String it = (String)element;
         int value2 = 0;
         Enum enumValue2 = org.apache.commons.lang3.EnumUtils.getEnum(clazz, it);
         if (enumValue2 != null) {
            Object obj = enumValue2;
            int value3 = 0;
            destination.add(obj);
         }
      }

      return (List)destination;
   }

   @Nullable
   public static final Quaternionf quaternionf(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 4);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
            int index = 0;

            for(Object item : iterable) {
               int value = index++;
               if (value < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               String c = (String)item;
               int value2 = 0;
               Float number = StringsKt.toFloatOrNull(c);
               destination.add(number != null ? number : (value == 3 ? 1.0F : 0.0F));
            }

            List list2 = (List)destination;
            float x = ((Number)list2.get(0)).floatValue();
            float y = ((Number)list2.get(1)).floatValue();
            float floatValue = ((Number)list2.get(2)).floatValue();
            float w = ((Number)list2.get(3)).floatValue();
            return new Quaternionf(x, y, floatValue, w);
         }
      }

      return null;
   }

   @NotNull
   public static final Vector vector(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 3);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String it = (String)item;
               int value = 0;
               Float number = StringsKt.toFloatOrNull(it);
               destination.add(number != null ? number : 0.0F);
            }

            list = CollectionUtilsKt.ensureSize((List)destination, 3, 0.0F);
            if (list != null) {
               List list2 = list;
               float x = ((Number)list2.get(0)).floatValue();
               float y = ((Number)list2.get(1)).floatValue();
               float z = ((Number)list2.get(2)).floatValue();
               return new Vector(x, y, z);
            }
         }
      }

      return new Vector();
   }

   @NotNull
   public static final Vector3f vector3f(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 3);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String it = (String)item;
               int value = 0;
               Float number = StringsKt.toFloatOrNull(it);
               destination.add(number != null ? number : 0.0F);
            }

            list = CollectionUtilsKt.ensureSize((List)destination, 3, 0.0F);
            if (list != null) {
               List list2 = list;
               float x = ((Number)list2.get(0)).floatValue();
               float y = ((Number)list2.get(1)).floatValue();
               float z = ((Number)list2.get(2)).floatValue();
               return new Vector3f(x, y, z);
            }
         }
      }

      return new Vector3f();
   }

   @NotNull
   public static final Vector3f vector3f(@NotNull ConfigurationSection configurationSection, @NotNull String key, float value) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 3);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String it = (String)item;
               int value2 = 0;
               Float number = StringsKt.toFloatOrNull(it);
               destination.add(number != null ? number : value);
            }

            list = CollectionUtilsKt.ensureSize((List)destination, 3, value);
            if (list != null) {
               List list2 = list;
               float x = ((Number)list2.get(0)).floatValue();
               float y = ((Number)list2.get(1)).floatValue();
               float z = ((Number)list2.get(2)).floatValue();
               return new Vector3f(x, y, z);
            }
         }
      }

      return new Vector3f(value, value, value);
   }

   @NotNull
   public static final Vector2i vector2i(@NotNull ConfigurationSection configurationSection, @NotNull String key, int value) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 2);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String it = (String)item;
               int value2 = 0;
               Integer number = StringsKt.toIntOrNull(it);
               destination.add(number != null ? number : value);
            }

            list = CollectionUtilsKt.ensureSize((List)destination, 2, value);
            if (list != null) {
               List list2 = list;
               int x = ((Number)list2.get(0)).intValue();
               int y = ((Number)list2.get(1)).intValue();
               return new Vector2i(x, y);
            }
         }
      }

      return new Vector2i(value, value);
   }

   @NotNull
   public static final Vector2f vector2f(@NotNull ConfigurationSection configurationSection, @NotNull String key, float value) {
      String str = stringOrNull(configurationSection, key);
      if (str != null) {
         CharSequence charSequence = (CharSequence)str;
         String[] strs = new String[]{","};
         List list = StringsKt.split(charSequence, strs, false, 2);
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String it = (String)item;
               int value2 = 0;
               Float number = StringsKt.toFloatOrNull(it);
               destination.add(number != null ? number : value);
            }

            list = CollectionUtilsKt.ensureSize((List)destination, 2, value);
            if (list != null) {
               List list2 = list;
               float x = ((Number)list2.get(0)).floatValue();
               float y = ((Number)list2.get(1)).floatValue();
               return new Vector2f(x, y);
            }
         }
      }

      return new Vector2f(value, value);
   }

   @NotNull
   public static final TriState triState(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      TriState triState;
      if (configurationSection.isBoolean(key)) {
         triState = TriState.Companion.byBoolean(configurationSection.getBoolean(key));
      } else if (configurationSection.isString(key)) {
         triState = (TriState)org.apache.commons.lang3.EnumUtils.getEnum(TriState.class, stringOrNull(configurationSection, key));
      } else {
         triState = TriState.NOT_SET;
      }

      if (triState == null) {
         triState = TriState.NOT_SET;
      }

      return triState;
   }

   @NotNull
   public static final ConfigurationSection plus(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable Object value) {
      configurationSection.set(key, value);
      return configurationSection;
   }

   @NotNull
   public static final ConfigurationSection rename(@NotNull ConfigurationSection configurationSection2, @NotNull String oldKey, @NotNull String newKey) {
      Object obj = configurationSection2.get(oldKey);
      if (obj == null) {
         return configurationSection2;
      } else {
         Object old = obj;
         if (old instanceof ConfigurationSection) {
            ConfigurationSection configurationSection = configurationSection2.getConfigurationSection(newKey);
            if (configurationSection == null) {
               configurationSection = (ConfigurationSection)(new YamlConfiguration());
            }

            ConfigurationSection target = configurationSection;
            NexoYaml.Companion.copyConfigurationSection((ConfigurationSection)old, target);
            configurationSection2.set(newKey, target);
         } else {
            configurationSection2.set(newKey, old);
         }

         configurationSection2.set(oldKey, (Object)null);
         return configurationSection2;
      }
   }

   @NotNull
   public static final ConfigurationSection remove(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      configurationSection.set(key, (Object)null);
      return configurationSection;
   }

   @NotNull
   public static final ConfigurationSection removeIf(@NotNull ConfigurationSection configurationSection, @NotNull String key, boolean bool) {
      if (bool) {
         configurationSection.set(key, (Object)null);
      }

      return configurationSection;
   }

   @NotNull
   public static final ConfigurationSection removeIf(@NotNull ConfigurationSection configurationSection, @NotNull String key, @NotNull Function0<Boolean> bool) {
      if ((Boolean)bool.invoke()) {
         configurationSection.set(key, (Object)null);
      }

      return configurationSection;
   }

   public static final float float_(@NotNull ConfigurationSection configurationSection, @NotNull String key, float value) {
      return (float)configurationSection.getDouble(key, (double)value);
   }

   @Nullable
   public static final Float floatOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      return configurationSection.isDouble(key) ? (float)configurationSection.getDouble(key) : null;
   }

   @Nullable
   public static final Duration durationOrNull(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable DurationUnit defaultUnit) {
      String string = configurationSection.getString(key);
      return string != null ? DurationUtilsKt.toDuration(string, defaultUnit) : null;
   }

   public static final long duration(@NotNull ConfigurationSection configurationSection, @NotNull String key, @Nullable DurationUnit defaultUnit) {
      String string = configurationSection.getString(key);
      long value;
      if (string != null) {
         Duration duration = DurationUtilsKt.toDuration(string, defaultUnit);
         if (duration != null) {
            value = duration.unbox_impl();
            return value;
         }
      }

      value = Duration.Companion.getZERO_UwyO8pc();
      return value;
   }

   @Nullable
   public static final ConfigurationSection section(@NotNull ConfigurationSection configurationSection, @NotNull String key) {
      return configurationSection.getConfigurationSection(key);
   }

   @NotNull
   public static final List<ConfigurationSection> sectionList(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      Iterable iterable = (Iterable)typedMapList(configurationSection2, key);
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Map map = (Map)item;
         int value = 0;
         ConfigurationSection configurationSection = (new YamlConfiguration()).createSection(key);
         ConfigurationSection configurationSection3 = configurationSection;
         int value2 = 0;

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value3 = 0;
            configurationSection3.set((String)element.getKey(), NexoYaml.Companion.convertToConfigurationSection$com_nexomc_core(element.getValue()));
         }

         destination.add(configurationSection);
      }

      Collection collection = (Collection)((List)destination);
      Object obj;
      if (collection.isEmpty()) {
         List list = configurationSection2.getList(key);
         if (list == null) {
            obj = null;
         } else {
            Iterable iterable2 = (Iterable)list;
            Collection collection2 = (Collection)(new ArrayList());

            for(Object obj2 : iterable2) {
               if (obj2 instanceof ConfigurationSection) {
                  collection2.add(obj2);
               }
            }

            obj = (List)collection2;
         }

         if (obj == null) {
            obj = CollectionsKt.emptyList();
         }
      } else {
         obj = collection;
      }

      return (List)obj;
   }

   @NotNull
   public static final List<ConfigurationSection> sectionListOrSingle(@NotNull ConfigurationSection configurationSection2, @NotNull String key) {
      List sections = (List)(new ArrayList());
      CollectionsKt.addAll((Collection)sections, (Iterable)sectionList(configurationSection2, key));
      ConfigurationSection configurationSection = configurationSection2.getConfigurationSection(key);
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         sections.add(p0);
      }

      return sections;
   }

   public static final void forEach(@NotNull ConfigurationSection configurationSection, @NotNull Function2<? super String, Object, Unit> action) {
      Map values = configurationSection.getValues(false);
      Map map = values;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         String key = (String)element.getKey();
         Object value = element.getValue();
         action.invoke(key, value);
      }

   }

   @NotNull
   public static final <T, K> Either<T, K> getEither(@NotNull ConfigurationSection configurationSection, @NotNull Function1<? super ConfigurationSection, ? extends T> left, @NotNull Function1<? super ConfigurationSection, ? extends K> right) {
      Object obj = left.invoke(configurationSection);
      Either either;
      if (obj != null) {
         Object it = obj;
         int value = 0;
         either = new Either.Left(it);
      } else {
         Object obj2 = right.invoke(configurationSection);
         Either.Right right2;
         if (obj2 != null) {
            Object it = obj2;
            int value2 = 0;
            right2 = new Either.Right(it);
         } else {
            right2 = null;
         }

         either = right2;
      }

      return either;
   }

   @Nullable
   public static final <T, K> Either<T, K> getEitherOrNull(@NotNull ConfigurationSection configurationSection, @NotNull Function1<? super ConfigurationSection, ? extends T> left, @NotNull Function1<? super ConfigurationSection, ? extends K> right) {
      Object obj = left.invoke(configurationSection);
      Either either;
      if (obj != null) {
         Object it = obj;
         int value = 0;
         either = new Either.Left(it);
      } else {
         Object obj2 = right.invoke(configurationSection);
         if (obj2 != null) {
            Object it = obj2;
            int value2 = 0;
            either = new Either.Right(it);
         } else {
            either = null;
         }
      }

      return either;
   }

   /** Kotlin default-argument bridge, kept for binary compatibility with classes that were not recompiled. */
   public static kotlin.time.Duration durationOrNull$default(org.bukkit.configuration.ConfigurationSection section, String key, kotlin.time.DurationUnit unit, int mask, Object marker) {
      if ((mask & 2) != 0) unit = null;
      return durationOrNull(section, key, unit);
   }

   public static java.util.List stringListOrSingle$default(org.bukkit.configuration.ConfigurationSection section, String key, String defaultValue, int mask, Object marker) {
      if ((mask & 2) != 0) defaultValue = null;
      return stringListOrSingle(section, key, defaultValue);
   }
}
