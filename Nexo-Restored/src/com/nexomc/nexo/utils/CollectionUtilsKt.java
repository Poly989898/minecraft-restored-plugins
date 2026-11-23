package com.nexomc.nexo.utils;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import java.util.Arrays;
import java.util.function.Predicate;
import kotlin.TuplesKt;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000¸\u0001\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010&\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0013\n\u0002\u0010\"\n\u0002\b\r\n\u0002\u0010\u001f\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\b\u0003\u001a=\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u00032\u0006\u0010\u0004\u001a\u0002H\u00022\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0006¢\u0006\u0002\u0010\u0007\u001a1\u0010\b\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u0002H\u0002¢\u0006\u0002\u0010\r\u001a\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0010\u001a\u001c\u0010\u0011\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0012\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0010\u001aB\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0018\u0010\u0015\u001a\u0014\u0012\u0004\u0012\u0002H\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00010\u00140\u0016H\u0086\bø\u0001\u0000\u001aD\u0010\u0017\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u001a\u0010\u0015\u001a\u0016\u0012\u0004\u0012\u0002H\u0002\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u00140\u0016H\u0086\bø\u0001\u0000\u001aB\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0012\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0018\u0010\u0015\u001a\u0014\u0012\u0004\u0012\u0002H\u0002\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00010\u00140\u0016H\u0086\bø\u0001\u0000\u001a<\u0010\u0019\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000\u001aB\u0010\u001a\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0004\b\u0000\u0010\u0002\"\b\b\u0001\u0010\u0001*\u00020\u001b*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0014\u0010\u0015\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u0016H\u0086\bø\u0001\u0000\u001a<\u0010\u001c\u001a\b\u0012\u0004\u0012\u0002H\u00010\u001d\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000\u001a>\u0010\u001e\u001a\b\u0012\u0004\u0012\u0002H\u00010\u001d\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0014\u0010\u0015\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u0016H\u0086\bø\u0001\u0000\u001aI\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H\u00010 \"\u0004\b\u0000\u0010\u0002\"\u0006\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0018\u0010\u0015\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010!H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\"\u001aC\u0010#\u001a\b\u0012\u0004\u0012\u0002H\u00010 \"\u0004\b\u0000\u0010\u0002\"\u0006\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010$\u001aC\u0010#\u001a\b\u0012\u0004\u0012\u0002H\u00010 \"\u0004\b\u0000\u0010\u0002\"\u0006\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020 2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010%\u001aI\u0010&\u001a\b\u0012\u0004\u0012\u0002H\u00010 \"\u0004\b\u0000\u0010\u0002\"\n\b\u0001\u0010\u0001\u0018\u0001*\u00020\u001b*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0014\u0010\u0015\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010$\u001aT\u0010'\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u00142\u001e\u0010\u0015\u001a\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0+0\u0016H\u0086\bø\u0001\u0000\u001aY\u0010'\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020 2\u001e\u0010\u0015\u001a\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0+0\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010,\u001aB\u0010-\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H\u00020(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H)0\u0016H\u0086\bø\u0001\u0000\u001aB\u0010/\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\b\u0012\u0004\u0012\u0002H)0\u00142\u0012\u00100\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0\u0016H\u0086\bø\u0001\u0000\u001aT\u00101\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*02\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u00142\u001e\u00103\u001a\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0+0\u0016H\u0086\bø\u0001\u0000\u001aB\u00104\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*02\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\b\u0012\u0004\u0012\u0002H)0\u00142\u0012\u00100\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0\u0016H\u0086\bø\u0001\u0000\u001aT\u00104\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H\u000102\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*\"\u0004\b\u0002\u0010\u0001*\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*052\u0018\u0010.\u001a\u0014\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H\u00010!H\u0086\bø\u0001\u0000\u001aV\u00106\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H\u000102\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*\"\u0004\b\u0002\u0010\u0001*\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*052\u001a\u0010.\u001a\u0016\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*\u0012\u0006\u0012\u0004\u0018\u0001H\u00010!H\u0086\bø\u0001\u0000\u001ak\u00107\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u001425\u0010\u0015\u001a1\u0012\u0013\u0012\u00110\u000b¢\u0006\f\b8\u0012\b\b9\u0012\u0004\b\b(:\u0012\u0004\u0012\u0002H\u0002\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*\u0018\u00010+0!H\u0086\bø\u0001\u0000\u001aZ\u0010;\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u00142$\u00103\u001a \u0012\u0004\u0012\u0002H\u0002\u0012\u0016\u0012\u0014\u0012\u0006\u0012\u0004\u0018\u0001H)\u0012\u0006\u0012\u0004\u0018\u0001H*\u0018\u00010+0\u0016H\u0086\bø\u0001\u0000\u001aZ\u0010<\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*02\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u00142$\u00103\u001a \u0012\u0004\u0012\u0002H\u0002\u0012\u0016\u0012\u0014\u0012\u0006\u0012\u0004\u0018\u0001H)\u0012\u0006\u0012\u0004\u0018\u0001H*\u0018\u00010+0\u0016H\u0086\bø\u0001\u0000\u001a`\u0010=\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)\"\u0004\b\u0002\u0010**\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0014\u0010.\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H)0\u00162\u0014\u00100\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H*0\u0016H\u0086\bø\u0001\u0000\u001aF\u0010>\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\b\b\u0000\u0010\u0002*\u00020\u001b\"\n\b\u0001\u0010\u0001\u0018\u0001*\u00020\u001b*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010>\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0016H\u0086\bø\u0001\u0000\u001a1\u0010?\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\n\b\u0000\u0010\u0002\u0018\u0001*\u00020\u001b\"\n\b\u0001\u0010\u0001\u0018\u0001*\u0002H\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0014H\u0086\b\u001a\u001f\u0010@\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\u0014H\u0086\b\u001a6\u0010@\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\u00142\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010C\u001a\b\u0012\u0004\u0012\u0002H\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010D\u001a\b\u0012\u0004\u0012\u0002H\u00020\u001d\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010E\u001a\b\u0012\u0004\u0012\u0002H\u00020\u001d\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a\u001f\u0010F\u001a\b\u0012\u0004\u0012\u0002H\u00010\u001d\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\u0014H\u0086\b\u001a<\u0010F\u001a\b\u0012\u0004\u0012\u0002HG0\u001d\"\u0006\b\u0000\u0010\u0001\u0018\u0001\"\u0004\b\u0001\u0010G*\u0006\u0012\u0002\b\u00030\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002HG0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010@\u001a\b\u0012\u0004\u0012\u0002H\u00010\u000f\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\u00102\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010C\u001a\b\u0012\u0004\u0012\u0002H\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a6\u0010D\u001a\b\u0012\u0004\u0012\u0002H\u00020\u001d\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001aV\u0010C\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\u0010\u0012\u0006\b\u0001\u0012\u0002H)\u0012\u0004\u0012\u0002H*052\u001e\u0010A\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0H\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a+\u0010I\u001a\u000e\u0012\u0004\u0012\u00020J\u0012\u0004\u0012\u0002H*05\"\u0006\b\u0000\u0010*\u0018\u0001*\f\u0012\u0004\u0012\u00020J\u0012\u0002\b\u000305H\u0086\b\u001a0\u0010K\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002HL05\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010L*\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001HL05\u001aD\u0010M\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\b\u0012\u0004\u0012\u0002H)0\u00142\u0014\u00100\u001a\u0010\u0012\u0004\u0012\u0002H)\u0012\u0006\u0012\u0004\u0018\u0001H*0\u0016H\u0086\bø\u0001\u0000\u001aI\u0010M\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\b\u0012\u0004\u0012\u0002H)0 2\u0014\u00100\u001a\u0010\u0012\u0004\u0012\u0002H)\u0012\u0006\u0012\u0004\u0018\u0001H*0\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010N\u001aL\u0010O\u001a\u00020P\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0014\u0010\u0015\u001a\u0010\u0012\u0004\u0012\u0002H\u0002\u0012\u0006\u0012\u0004\u0018\u0001H\u00010\u00162\u0012\u0010Q\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020P0\u0016H\u0086\bø\u0001\u0000\u001a4\u0010R\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0+0\u0014\u001a2\u0010S\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0(\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*2\u0012\u0010T\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*0+\u001a\u000e\u0010U\u001a\u00020\u000b2\u0006\u0010V\u001a\u00020\u000b\u001aG\u0010W\u001a\"\u0012\u000e\u0012\f\b\u0002 X*\u0004\u0018\u0001H)H)\u0012\u000e\u0012\f\b\u0002 X*\u0004\u0018\u0001H*H*0(\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010**\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05H\u0000ø\u0001\u0001\u001a0\u0010Z\u001a\u00020P\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\t2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020P0\u0016H\u0086\bø\u0001\u0000\u001a>\u0010\\\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\u0004\b\u0000\u0010\u0002\"\u0006\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020\t2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u00020P0\u0016H\u0086\bø\u0001\u0000\u001a \u0010]\u001a\u0004\u0018\u0001H\u0001\"\u0006\b\u0000\u0010\u0001\u0018\u0001*\u0006\u0012\u0002\b\u00030\tH\u0086\b¢\u0006\u0002\u0010^\u001a)\u0010_\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\t2\u0006\u0010`\u001a\u0002H\u0002¢\u0006\u0002\u0010a\u001a*\u0010b\u001a\b\u0012\u0004\u0012\u0002H\u00020\u000f\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\t2\f\u0010c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0014\u001a*\u0010b\u001a\b\u0012\u0004\u0012\u0002H\u00020\u001d\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020d2\f\u0010c\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0014\u001aH\u0010e\u001a\u0014\u0012\u0004\u0012\u0002H)\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u000f02\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H)0\u0016H\u0086\bø\u0001\u0000\u001aH\u0010f\u001a\u0014\u0012\u0004\u0012\u0002H)\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u001d02\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u00142\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H)0\u0016H\u0086\bø\u0001\u0000\u001a?\u0010g\u001a\u00020P\"\u0004\b\u0000\u0010\u0002\"\u000e\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u0014*\u0002H)2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u00020P0\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010h\u001aM\u0010g\u001a\u00020P\"\u0004\b\u0000\u0010\u0002\"\u000e\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u0014*\u0002H)2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u00020P0\u00162\f\u0010i\u001a\b\u0012\u0004\u0012\u00020P0\u0006H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010j\u001aK\u0010g\u001a\u0002Hk\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*\"\u0014\b\u0002\u0010k*\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05*\u0002Hk2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002Hk\u0012\u0004\u0012\u00020P0\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010l\u001aY\u0010g\u001a\u00020P\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*\"\u0014\b\u0002\u0010k*\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05*\u0002Hk2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002Hk\u0012\u0004\u0012\u00020P0\u00162\f\u0010i\u001a\b\u0012\u0004\u0012\u00020P0\u0006H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010m\u001aG\u0010n\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010\u0002\"\u000e\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020\u0014\"\u0004\b\u0002\u0010\u0001*\u0002H)2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010o\u001aS\u0010n\u001a\u0004\u0018\u0001H\u0001\"\u0004\b\u0000\u0010)\"\u0004\b\u0001\u0010*\"\u0014\b\u0002\u0010k*\u000e\u0012\u0004\u0012\u0002H)\u0012\u0004\u0012\u0002H*05\"\u0004\b\u0003\u0010\u0001*\u0002Hk2\u0012\u0010[\u001a\u000e\u0012\u0004\u0012\u0002Hk\u0012\u0004\u0012\u0002H\u00010\u0016H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010p\u001a5\u0010q\u001a\u0002H)\"\u0004\b\u0000\u0010\u0002\"\u000e\b\u0001\u0010)*\b\u0012\u0004\u0012\u0002H\u00020r*\u0002H)2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010s\u001a\u0002H\u0002¢\u0006\u0002\u0010t\u001a4\u0010u\u001a\u00020B\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020r2\u0014\b\u0004\u0010A\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u00020B0\u0016H\u0086\bø\u0001\u0000\u001a*\u0010v\u001a\u0004\u0018\u00010\u000b\"\u0006\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00020w2\u0006\u0010`\u001a\u0002H\u0002H\u0086\b¢\u0006\u0002\u0010x\u001a6\u0010y\u001a\b\u0012\u0004\u0012\u0002H\u00020\t\"\u0004\b\u0000\u0010\u00022\u001c\b\u0004\u0010[\u001a\u0016\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001H\u00020\t\u0012\u0004\u0012\u0002H\u00020\u0016H\u0086\bø\u0001\u0000\"\u000e\u0010Y\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0005\b\u009920\u0001\n\u0002\b9¨\u0006z"},
   d2 = {"foliaGetOrPut", "R", "T", "", "key", "value", "Lkotlin/Function0;", "(Ljava/util/Map;Ljava/lang/Object;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "ensureSize", "", "size", "", "default", "(Ljava/util/List;ILjava/lang/Object;)Ljava/util/List;", "toFastList", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "Lkotlin/sequences/Sequence;", "toFastSet", "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;", "flatMapFast", "", "transform", "Lkotlin/Function1;", "flatMapFastNotNull", "flatMapSetFast", "mapFast", "mapNotNullFast", "", "mapFastSet", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "mapFastSetNotNull", "toTypedArrayIndexed", "", "Lkotlin/Function2;", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function2;)[Ljava/lang/Object;", "toTypedArray", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;)[Ljava/lang/Object;", "([Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)[Ljava/lang/Object;", "toTypedArrayNotNull", "associateFast", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "K", "V", "Lkotlin/Pair;", "([Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "associateFastBy", "keySelector", "associateFastWith", "valueSelector", "associateFastLinked", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "pairSelector", "associateFastLinkedWith", "", "associateFastLinkedWithNotNull", "associateFastIndexedNotNull", "Lkotlin/ParameterName;", "name", "index", "associateFastNotNull", "associateFastLinkedNotNull", "associateFastByNotNull", "remap", "filterIsNotInstance", "filterFastIsInstance", "predicate", "", "filterFast", "filterFastSet", "filterFastSetNot", "filterFastSetIsInstance", "S", "", "filterValuesInstanceOf", "", "filterValuesNotNull", "Z", "associateWithNotNull", "([Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Ljava/util/Map;", "forEachWith", "", "action", "toFastMap", "fastMapOf", "pair", "mapCapacity", "expectedSize", "optimizeReadOnlyMap", "kotlin.jvm.PlatformType", "INT_MAX_POWER_OF_TWO", "forEachInstanceOf", "block", "onEachInstanceOf", "firstInstanceOfOrNull", "(Ljava/util/List;)Ljava/lang/Object;", "plusFirst", "element", "(Ljava/util/List;Ljava/lang/Object;)Ljava/util/List;", "plusFast", "elements", "", "groupByFast", "groupByFastSet", "ifNotEmpty", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;)V", "elseBlock", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "M", "(Ljava/util/Map;Lkotlin/jvm/functions/Function1;)Ljava/util/Map;", "(Ljava/util/Map;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "ifNotEmptyOrNull", "(Ljava/lang/Iterable;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "(Ljava/util/Map;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "ensureAtleast", "", "filler", "(Ljava/util/Collection;ILjava/lang/Object;)Ljava/util/Collection;", "removeUnless", "indexOfOrNull", "", "(Ljava/util/Collection;Ljava/lang/Object;)Ljava/lang/Integer;", "nonNullList", "com.nexomc:core"}
)
public final class CollectionUtilsKt {
   private static final int INT_MAX_POWER_OF_TWO = 1073741824;

   public static final <T, R> R foliaGetOrPut(@NotNull Map<T, R> map, T key, @NotNull Function0<? extends R> value) {
      Object obj;
      if (VersionUtil.INSTANCE.isFoliaServer()) {
         obj = map.computeIfAbsent(key, k -> value.invoke());
      } else {
         Object obj2 = map.get(key);
         if (obj2 == null) {
            int value2 = 0;
            Object answer = value.invoke();
            map.put(key, (R)answer);
            obj = answer;
         } else {
            obj = obj2;
         }
      }

      return (R)obj;
   }

   @NotNull
   public static final <T> List<T> ensureSize(@NotNull List<? extends T> list3, int size, T t) {
      List list;
      if (size == list3.size()) {
         list = list3;
      } else if (size > list3.size()) {
         list = CollectionsKt.take((Iterable)list3, size);
      } else {
         List list2 = CollectionsKt.toMutableList((Collection)list3);
         List list4 = list2;
         int value = 0;

         while(list4.size() < size) {
            list4.add(t);
         }

         list = list2;
      }

      return list;
   }

   @NotNull
   public static final <T> ObjectArrayList<T> toFastList(@NotNull Sequence<? extends T> sequence) {
      Iterator it = sequence.iterator();
      if (!it.hasNext()) {
         return new ObjectArrayList();
      } else {
         Object element = it.next();
         if (!it.hasNext()) {
            Object[] objs = new Object[]{element};
            ObjectArrayList objectArrayList = ObjectArrayList.of(objs);
            return objectArrayList;
         } else {
            ObjectArrayList dst = new ObjectArrayList();
            dst.add(element);

            while(it.hasNext()) {
               dst.add(it.next());
            }

            return dst;
         }
      }
   }

   @NotNull
   public static final <T> ObjectLinkedOpenHashSet<T> toFastSet(@NotNull Sequence<? extends T> sequence) {
      Iterator it = sequence.iterator();
      if (!it.hasNext()) {
         return new ObjectLinkedOpenHashSet();
      } else {
         Object element = it.next();
         if (!it.hasNext()) {
            ObjectLinkedOpenHashSet objectLinkedOpenHashSet = ObjectLinkedOpenHashSet.of(element);
            return objectLinkedOpenHashSet;
         } else {
            ObjectLinkedOpenHashSet dst = new ObjectLinkedOpenHashSet();
            dst.add(element);

            while(it.hasNext()) {
               dst.add(it.next());
            }

            return dst;
         }
      }
   }

   @NotNull
   public static final <T, R> ObjectArrayList<R> flatMapFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Iterable<? extends R>> transform) {
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         Iterable list = (Iterable)((Function1)transform).invoke(element);
         CollectionsKt.addAll(destination, list);
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public static final <T, R> ObjectArrayList<R> flatMapFastNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Iterable<? extends R>> transform2) {
        Intrinsics.checkNotNullParameter(iterable, (String)"<this>");
        Intrinsics.checkNotNullParameter(transform2, (String)"transform");
        ObjectArrayList result = new ObjectArrayList();
        for (T element : iterable) {
            for (Object item : (Iterable)transform2.invoke(element)) {
                if (item == null) continue;
                result.add(item);
            }
        }
        return result;
    }

   @NotNull
   public static final <T, R> ObjectLinkedOpenHashSet<R> flatMapSetFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Iterable<? extends R>> transform) {
      Collection destination = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object element : iterable) {
         Iterable list = (Iterable)((Function1)transform).invoke(element);
         CollectionsKt.addAll(destination, list);
      }

      return (ObjectLinkedOpenHashSet)destination;
   }

   @NotNull
   public static final <T, R> ObjectArrayList<R> mapFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends R> transform) {
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         destination.add(((Function1)transform).invoke(item));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public static final <T, R> ObjectArrayList<R> mapNotNullFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends R> transform) {
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         int value = 0;
         Object obj = ((Function1)transform).invoke(element);
         if (obj != null) {
            Object it = obj;
            int value2 = 0;
            destination.add(it);
         }
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public static final <T, R> ObjectOpenHashSet<R> mapFastSet(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends R> transform) {
      Collection destination = (Collection)(new ObjectOpenHashSet((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         destination.add(((Function1)transform).invoke(item));
      }

      return (ObjectOpenHashSet)destination;
   }

   @NotNull
   public static final <T, R> ObjectOpenHashSet<R> mapFastSetNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends R> transform) {
      Collection destination = (Collection)(new ObjectOpenHashSet((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object element : iterable) {
         int value = 0;
         Object obj = ((Function1)transform).invoke(element);
         if (obj != null) {
            Object it = obj;
            int value2 = 0;
            destination.add(it);
         }
      }

      return (ObjectOpenHashSet)destination;
   }

   @NotNull
   public static final <T, K, V> Object2ObjectOpenHashMap<K, V> associateFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Pair<? extends K, ? extends V>> transform) {
      int capacity = RangesKt.coerceAtLeast(mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16);
      Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));

      for(Object element : iterable) {
         Pair pair = (Pair)((Function1)transform).invoke(element);
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return (Object2ObjectOpenHashMap)destination;
   }

   @NotNull
   public static final <T, K, V> Object2ObjectOpenHashMap<K, V> associateFast(@NotNull T[] ts, @NotNull Function1<? super T, ? extends Pair<? extends K, ? extends V>> transform) {
      int capacity = RangesKt.coerceAtLeast(mapCapacity(ts.length), 16);
      Object[] objs = ts;
      Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));
      int value = 0;

      for(int i = ts.length; value < i; ++value) {
         Object element = objs[value];
         Pair pair = (Pair)((Function1)transform).invoke(element);
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return (Object2ObjectOpenHashMap)destination;
   }

   @NotNull
   public static final <T, K> Object2ObjectOpenHashMap<K, T> associateFastBy(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends K> keySelector) {
      Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         destination.put(((Function1)keySelector).invoke(element), element);
      }

      return (Object2ObjectOpenHashMap)destination;
   }

   @NotNull
   public static final <K, V> Object2ObjectOpenHashMap<K, V> associateFastWith(@NotNull Iterable<? extends K> iterable, @NotNull Function1<? super K, ? extends V> valueSelector) {
      Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         destination.put(element, ((Function1)valueSelector).invoke(element));
      }

      return (Object2ObjectOpenHashMap)destination;
   }

   @NotNull
   public static final <T, K, V> Object2ObjectLinkedOpenHashMap<K, V> associateFastLinked(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Pair<? extends K, ? extends V>> pairSelector) {
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast(mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         Pair pair = (Pair)((Function1)pairSelector).invoke(element);
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return (Object2ObjectLinkedOpenHashMap)destination;
   }

   @NotNull
   public static final <K, V> Object2ObjectLinkedOpenHashMap<K, V> associateFastLinkedWith(@NotNull Iterable<? extends K> iterable, @NotNull Function1<? super K, ? extends V> valueSelector) {
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast(mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         destination.put(element, ((Function1)valueSelector).invoke(element));
      }

      return (Object2ObjectLinkedOpenHashMap)destination;
   }

   @NotNull
   public static final <K, V, R> Object2ObjectLinkedOpenHashMap<K, R> associateFastLinkedWith(@NotNull Map<K, ? extends V> map, @NotNull Function2<? super K, ? super V, ? extends R> keySelector) {
      int capacity = RangesKt.coerceAtLeast(mapCapacity(map.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : map.entrySet()) {
         Object k = entry.getKey();
         Object v = entry.getValue();
         ((Map)result).put(k, ((Function2)keySelector).invoke(k, v));
      }

      return result;
   }

   @NotNull
   public static final <K, V, R> Object2ObjectLinkedOpenHashMap<K, R> associateFastLinkedWithNotNull(@NotNull Map<K, ? extends V> map2, @NotNull Function2<? super K, ? super V, ? extends R> keySelector) {
      int capacity = RangesKt.coerceAtLeast(mapCapacity(map2.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : map2.entrySet()) {
         Object k = entry.getKey();
         Object v = entry.getValue();
         Map map = (Map)result;
         Object obj = ((Function2)keySelector).invoke(k, v);
         if (obj != null) {
            Object obj2 = obj;
            map.put(k, obj2);
         }
      }

      return result;
   }

   @NotNull
   public static final <T, K, V> Object2ObjectOpenHashMap<K, V> associateFastIndexedNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function2<? super Integer, ? super T, ? extends Pair<? extends K, ? extends V>> transform) {
      Object2ObjectOpenHashMap map = new Object2ObjectOpenHashMap();
      int index = 0;

      for(Object element : iterable) {
         Pair pair2 = (Pair)((Function2)transform).invoke(index++, element);
         if (pair2 != null) {
            Pair pair = pair2;
            ((Map)map).put(pair.getFirst(), pair.getSecond());
         }
      }

      return map;
   }

   @NotNull
   public static final <T, K, V> Object2ObjectOpenHashMap<K, V> associateFastNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Pair<? extends K, ? extends V>> pairSelector) {
        Intrinsics.checkNotNullParameter(iterable, (String)"<this>");
        Intrinsics.checkNotNullParameter(pairSelector, (String)"pairSelector");
        Collection collection = iterable instanceof Collection ? (Collection)iterable : null;
        Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast((int)CollectionUtilsKt.mapCapacity(collection != null ? collection.size() : 10), (int)16));
        for (T element : iterable) {
            Pair pair = (Pair)pairSelector.invoke(element);
            if (pair == null) continue;
            Pair it = pair;
            boolean bl = false;
            Object object = it.getFirst();
            if (object == null) continue;
            Object object2 = it.getSecond();
            if (object2 == null) continue;
            Pair pair2 = TuplesKt.to((Object)object, (Object)object2);
            pair = pair2;
            if (pair2 == null) {
                continue;
            }
            Pair pair3 = pair;
            Object key = pair3.component1();
            Object value = pair3.component2();
            ((Map)result).put(key, value);
        }
        return result;
    }

   @NotNull
   public static final <T, K, V> Object2ObjectLinkedOpenHashMap<K, V> associateFastLinkedNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends Pair<? extends K, ? extends V>> pairSelector) {
        Intrinsics.checkNotNullParameter(iterable, (String)"<this>");
        Intrinsics.checkNotNullParameter(pairSelector, (String)"pairSelector");
        Collection collection = iterable instanceof Collection ? (Collection)iterable : null;
        Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast((int)CollectionUtilsKt.mapCapacity(collection != null ? collection.size() : 10), (int)16));
        for (T element : iterable) {
            Pair pair = (Pair)pairSelector.invoke(element);
            if (pair == null) continue;
            Pair it = pair;
            boolean bl = false;
            Object object = it.getFirst();
            if (object == null) continue;
            Object object2 = it.getSecond();
            if (object2 == null) continue;
            Pair pair2 = TuplesKt.to((Object)object, (Object)object2);
            pair = pair2;
            if (pair2 == null) {
                continue;
            }
            Pair pair3 = pair;
            Object key = pair3.component1();
            Object value = pair3.component2();
            ((Map)result).put(key, value);
        }
        return result;
    }

   @NotNull
   public static final <T, K, V> Object2ObjectOpenHashMap<K, V> associateFastByNotNull(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends K> keySelector, @NotNull Function1<? super T, ? extends V> valueSelector) {
        Intrinsics.checkNotNullParameter(iterable, (String)"<this>");
        Intrinsics.checkNotNullParameter(keySelector, (String)"keySelector");
        Intrinsics.checkNotNullParameter(valueSelector, (String)"valueSelector");
        Collection collection = iterable instanceof Collection ? (Collection)iterable : null;
        Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast((int)CollectionUtilsKt.mapCapacity(collection != null ? collection.size() : 10), (int)16));
        for (T element : iterable) {
            Object key = keySelector.invoke(element);
            Object value = valueSelector.invoke(element);
            if (key == null || value == null) continue;
            ((Map)result).put(key, value);
        }
        return result;
    }

   @NotNull
   public static final <T> ObjectArrayList<T> filterFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, Boolean> predicate) {
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         if ((Boolean)((Function1)predicate).invoke(element)) {
            destination.add(element);
         }
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public static final <T> ObjectOpenHashSet<T> filterFastSet(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, Boolean> predicate) {
      Collection destination = (Collection)(new ObjectOpenHashSet());

      for(Object element : iterable) {
         if ((Boolean)((Function1)predicate).invoke(element)) {
            destination.add(element);
         }
      }

      return (ObjectOpenHashSet)destination;
   }

   @NotNull
   public static final <T> ObjectOpenHashSet<T> filterFastSetNot(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, Boolean> predicate) {
      Collection destination = (Collection)(new ObjectOpenHashSet());

      for(Object element : iterable) {
         if (!(Boolean)((Function1)predicate).invoke(element)) {
            destination.add(element);
         }
      }

      return (ObjectOpenHashSet)destination;
   }

   @NotNull
   public static final <T> ObjectArrayList<T> filterFast(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, Boolean> predicate) {
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         if ((Boolean)((Function1)predicate).invoke(element)) {
            destination.add(element);
         }
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public static final <T> ObjectOpenHashSet<T> filterFastSet(@NotNull Sequence<? extends T> sequence, @NotNull Function1<? super T, Boolean> predicate) {
      Collection destination = (Collection)(new ObjectOpenHashSet());

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         if ((Boolean)((Function1)predicate).invoke(element)) {
            destination.add(element);
         }
      }

      return (ObjectOpenHashSet)destination;
   }

   @NotNull
   public static final <K, V> Object2ObjectOpenHashMap<K, V> filterFast(@NotNull Map<? extends K, ? extends V> map, @NotNull Function1<? super Map.Entry<? extends K, ? extends V>, Boolean> predicate) {
      Map destination = (Map)(new Object2ObjectOpenHashMap());

      for(Map.Entry element : map.entrySet()) {
         if ((Boolean)((Function1)predicate).invoke(element)) {
            destination.put(element.getKey(), element.getValue());
         }
      }

      return (Object2ObjectOpenHashMap)destination;
   }

   @NotNull
   public static final <T, Z> Map<T, Z> filterValuesNotNull(@NotNull Map<T, ? extends Z> map) {
      LinkedHashMap result = new LinkedHashMap();

      for(Map.Entry entry : map.entrySet()) {
         Object it = entry.getValue();
         int value = 0;
         if (it != null) {
            result.put(entry.getKey(), entry.getValue());
         }
      }

      Map map2 = (Map)result;
      return map2;
   }

   @NotNull
   public static final <K, V> Map<K, V> associateWithNotNull(@NotNull Iterable<? extends K> iterable, @NotNull Function1<? super K, ? extends V> valueSelector) {
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map destination = (Map)result;

      for(Object element : iterable) {
         destination.put(element, ((Function1)valueSelector).invoke(element));
      }

      return filterValuesNotNull(destination);
   }

   @NotNull
   public static final <K, V> Map<K, V> associateWithNotNull(@NotNull K[] ks, @NotNull Function1<? super K, ? extends V> valueSelector) {
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(ks.length), 16));
      Object[] objs = ks;
      Map destination = (Map)result;
      int value = 0;

      for(int i = ks.length; value < i; ++value) {
         Object element = objs[value];
         destination.put(element, ((Function1)valueSelector).invoke(element));
      }

      return filterValuesNotNull(destination);
   }

   public static final <T, R> void forEachWith(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends R> transform, @NotNull Function1<? super R, Unit> action) {

      for(Object element : iterable) {
         Object obj = ((Function1)transform).invoke(element);
         if (obj != null) {
            Object it = obj;
            int value = 0;
            ((Function1)action).invoke(it);
         }
      }

   }

   @NotNull
   public static final <K, V> Object2ObjectOpenHashMap<K, V> toFastMap(@NotNull Iterable<? extends Pair<? extends K, ? extends V>> iterable) {
      if (iterable instanceof Collection) {
         Object2ObjectOpenHashMap object2ObjectOpenHashMap;
         switch (((Collection)iterable).size()) {
            case 0 -> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
            case 1 -> object2ObjectOpenHashMap = fastMapOf(iterable instanceof List ? (Pair)((List)iterable).get(0) : (Pair)((Collection)iterable).iterator().next());
            default -> object2ObjectOpenHashMap = (Object2ObjectOpenHashMap)MapsKt.toMap(iterable, (Map)(new Object2ObjectOpenHashMap(mapCapacity(((Collection)iterable).size()))));
         }

         return object2ObjectOpenHashMap;
      } else {
         return optimizeReadOnlyMap(MapsKt.toMap(iterable, (Map)(new LinkedHashMap())));
      }
   }

   @NotNull
   public static final <K, V> Object2ObjectOpenHashMap<K, V> fastMapOf(@NotNull Pair<? extends K, ? extends V> pair) {
      Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
      int value = 0;
      object2ObjectOpenHashMap.put(pair.getFirst(), pair.getSecond());
      return object2ObjectOpenHashMap;
   }

   public static final int mapCapacity(int expectedSize) {
      return expectedSize < 0 ? expectedSize : (expectedSize < 3 ? expectedSize + 1 : (expectedSize < 1073741824 ? (int)((float)expectedSize / 0.75F + 1.0F) : Integer.MAX_VALUE));
   }

   @NotNull
   public static final <K, V> Object2ObjectOpenHashMap<K, V> optimizeReadOnlyMap(@NotNull Map<K, ? extends V> map2) {
      Object2ObjectOpenHashMap object2ObjectOpenHashMap;
      switch (map2.size()) {
         case 0:
            object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
            break;
         case 1:
            Map.Entry entry = (Map.Entry)map2.entrySet().iterator().next();
            int value = 0;
            Map map = Collections.singletonMap(entry.getKey(), entry.getValue());
            object2ObjectOpenHashMap = new Object2ObjectOpenHashMap(map);
            break;
         default:
            object2ObjectOpenHashMap = new Object2ObjectOpenHashMap(map2);
      }

      return object2ObjectOpenHashMap;
   }

   @NotNull
   public static final <T> List<T> plusFirst(@NotNull List<? extends T> list2, T element) {
      List list = TypeIntrinsics.isMutableList(list2) ? list2 : null;
      if (list != null) {
         List list3 = list;
         int value = 0;
         list3.addFirst(element);
         list = list3;
      } else {
         List createListBuilder = CollectionsKt.createListBuilder();
         int value2 = 0;
         createListBuilder.add(element);
         createListBuilder.addAll((Collection)createListBuilder);
         list = CollectionsKt.build(createListBuilder);
      }

      return list;
   }

   @NotNull
   public static final <T> ObjectArrayList<T> plusFast(@NotNull List<? extends T> list, @NotNull Iterable<? extends T> elements) {
      if (elements instanceof Collection) {
         ObjectArrayList result = new ObjectArrayList(list.size() + ((Collection)elements).size());
         result.addAll((Collection)list);
         result.addAll((Collection)elements);
         return result;
      } else {
         ObjectArrayList result = new ObjectArrayList((Collection)list);
         CollectionsKt.addAll((Collection)result, elements);
         return result;
      }
   }

   @NotNull
   public static final <T> ObjectOpenHashSet<T> plusFast(@NotNull Set<? extends T> set, @NotNull Iterable<? extends T> elements) {
      if (elements instanceof Collection) {
         ObjectOpenHashSet result = new ObjectOpenHashSet(set.size() + ((Collection)elements).size());
         result.addAll((Collection)set);
         result.addAll((Collection)elements);
         return result;
      } else {
         ObjectOpenHashSet result = new ObjectOpenHashSet((Collection)set);
         CollectionsKt.addAll((Collection)result, elements);
         return result;
      }
   }

   @NotNull
   public static final <T, K> Object2ObjectLinkedOpenHashMap<K, ObjectArrayList<T>> groupByFast(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends K> keySelector) {
      Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();

      for(Object element : iterable) {
         Object key = ((Function1)keySelector).invoke(element);
ObjectArrayList list = (ObjectArrayList)map.get(key);
         if (list == null) {
            list = new ObjectArrayList();
            map.put(key, list);
         }
         list.add(element);
      }

      return map;
   }

   @NotNull
   public static final <T, K> Object2ObjectLinkedOpenHashMap<K, ObjectOpenHashSet<T>> groupByFastSet(@NotNull Iterable<? extends T> iterable, @NotNull Function1<? super T, ? extends K> keySelector) {
      Object2ObjectLinkedOpenHashMap map = new Object2ObjectLinkedOpenHashMap();

      for(Object element : iterable) {
         Object key = ((Function1)keySelector).invoke(element);
ObjectOpenHashSet set = (ObjectOpenHashSet)map.get(key);
         if (set == null) {
            set = new ObjectOpenHashSet();
            map.put(key, set);
         }
         set.add(element);
      }

      return map;
   }

   public static final <T, K extends Iterable<? extends T>> void ifNotEmpty(@NotNull K k, @NotNull Function1<? super K, Unit> block) {
      if (CollectionsKt.count(k) > 0) {
         block.invoke(k);
      }

   }

   public static final <T, K extends Iterable<? extends T>> void ifNotEmpty(@NotNull K k, @NotNull Function1<? super K, Unit> block, @NotNull Function0<Unit> elseBlock) {
      if (CollectionsKt.count(k) > 0) {
         block.invoke(k);
      } else {
         elseBlock.invoke();
      }

   }

   @NotNull
   public static final <K, V, M extends Map<K, ? extends V>> M ifNotEmpty(@NotNull M m, @NotNull Function1<? super M, Unit> block) {
      if (!m.isEmpty()) {
         block.invoke(m);
      }

      return (M)m;
   }

   public static final <K, V, M extends Map<K, ? extends V>> void ifNotEmpty(@NotNull M m, @NotNull Function1<? super M, Unit> block, @NotNull Function0<Unit> elseBlock) {
      if (!m.isEmpty()) {
         block.invoke(m);
      } else {
         elseBlock.invoke();
      }

   }

   @Nullable
   public static final <T, K extends Iterable<? extends T>, R> R ifNotEmptyOrNull(@NotNull K k, @NotNull Function1<? super K, ? extends R> block) {
      return (R)(CollectionsKt.count(k) > 0 ? block.invoke(k) : null);
   }

   @Nullable
   public static final <K, V, M extends Map<K, ? extends V>, R> R ifNotEmptyOrNull(@NotNull M m, @NotNull Function1<? super M, ? extends R> block) {
      return (R)(!m.isEmpty() ? block.invoke(m) : null);
   }

   @NotNull
   public static final <T, K extends Collection<T>> K ensureAtleast(@NotNull K k, int size, T filler) {

      while(k.size() < size) {
         k.add(filler);
      }

      return (K)k;
   }

   @NotNull
   public static final <T> List<T> nonNullList(@NotNull Function1<? super List<? extends T>, ? extends T> block) {
      List result = (List)(new ArrayList());
      ((Function1)block).invoke(CollectionsKt.toList((Iterable)result));
      return result;
   }

   private static final Object foliaGetOrPut$lambda$0(Function0 function0, Object it) {
      return function0.invoke();
   }

   private static final Object foliaGetOrPut$lambda$1(Function1 tmp0, Object p0) {
      return tmp0.invoke(p0);
   }
}
