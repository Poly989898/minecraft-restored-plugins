package com.nexomc.nexo.items;

import com.nexomc.nexo.items.customarmor.CustomArmorTextures;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.nexomc.nexo.items.customarmor.CustomArmorTextures;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.tint.TintSource;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\bN\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b#\b\u0086\b\u0018\u0000 ¯\u00012\u00020\u0001:\u0002¯\u0001BÑ\u0002\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\f\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\f\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\f\u0012\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\f\u0012\u0014\b\u0002\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00150\u0017\u0012\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001a\u0012\b\b\u0002\u0010\u001b\u001a\u00020\u001c\u0012\b\b\u0002\u0010\u001d\u001a\u00020\u001e\u0012\b\b\u0002\u0010\u001f\u001a\u00020\u001e\u0012\b\b\u0002\u0010 \u001a\u00020\u001c\u0012\b\b\u0002\u0010!\u001a\u00020\u001c\u0012\b\b\u0002\u0010\"\u001a\u00020\u001e\u0012\n\b\u0002\u0010#\u001a\u0004\u0018\u00010$\u0012\b\b\u0002\u0010%\u001a\u00020\u001c\u0012\b\b\u0002\u0010&\u001a\u00020\u001c\u0012\b\b\u0002\u0010'\u001a\u00020(¢\u0006\u0004\b)\u0010*J\u000e\u0010v\u001a\u00020w2\u0006\u0010x\u001a\u00020yJ\u001a\u0010z\u001a\u0004\u0018\u00010\u00052\u0006\u0010{\u001a\u00020y2\u0006\u0010|\u001a\u00020\u0018H\u0002J\u000e\u0010}\u001a\u00020w2\u0006\u0010~\u001a\u00020yJ\u0006\u0010\u007f\u001a\u00020\u001cJ\u0007\u0010\u0080\u0001\u001a\u00020\u001cJ\u0007\u0010\u0081\u0001\u001a\u00020\u001cJ\u0007\u0010\u0082\u0001\u001a\u00020\u001cJ\u0007\u0010\u0083\u0001\u001a\u00020\u001cJ\u001d\u0010\t\u001a\u0005\u0018\u00010\u0084\u00012\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u0001J\u001d\u0010\n\u001a\u0005\u0018\u00010\u0084\u00012\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u0001J\u001d\u0010\u0012\u001a\u0005\u0018\u00010\u0084\u00012\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u0001J\u001d\u0010\r\u001a\u0005\u0018\u00010\u0084\u00012\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u0001J\u001d\u0010\u000e\u001a\u0005\u0018\u00010\u0084\u00012\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u0001J3\u0010\u000f\u001a\t\u0012\u0005\u0012\u00030\u0088\u00010\f2\b\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\u0010\u0087\u0001\u001a\u00030\u0084\u00012\u0010\b\u0002\u0010\u0089\u0001\u001a\t\u0012\u0005\u0012\u00030\u008a\u00010\fJ \u0010\u008b\u0001\u001a\t\u0012\u0005\u0012\u00030\u008c\u00010\f2\u0010\b\u0002\u0010\u0089\u0001\u001a\t\u0012\u0005\u0012\u00030\u008a\u00010\fJ\u0017\u0010\u000b\u001a\t\u0012\u0005\u0012\u00030\u008d\u00010\f2\b\u0010\u0085\u0001\u001a\u00030\u0086\u0001J\u0011\u0010\u008e\u0001\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010,J\f\u0010\u008f\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\n\u0010\u0090\u0001\u001a\u00020\u0005HÆ\u0003J\f\u0010\u0091\u0001\u001a\u0004\u0018\u00010\bHÆ\u0003J\f\u0010\u0092\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\f\u0010\u0093\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0010\u0010\u0094\u0001\u001a\b\u0012\u0004\u0012\u00020\u00050\fHÆ\u0003J\f\u0010\u0095\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\f\u0010\u0096\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0010\u0010\u0097\u0001\u001a\b\u0012\u0004\u0012\u00020\u00050\fHÆ\u0003J\f\u0010\u0098\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\f\u0010\u0099\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\f\u0010\u009a\u0001\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u0010\u0010\u009b\u0001\u001a\b\u0012\u0004\u0012\u00020\u00050\fHÆ\u0003J\u0010\u0010\u009c\u0001\u001a\b\u0012\u0004\u0012\u00020\u00150\fHÆ\u0003J\u0016\u0010\u009d\u0001\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00150\u0017HÆ\u0003J\f\u0010\u009e\u0001\u001a\u0004\u0018\u00010\u001aHÆ\u0003J\n\u0010\u009f\u0001\u001a\u00020\u001cHÆ\u0003J\n\u0010 \u0001\u001a\u00020\u001eHÆ\u0003J\n\u0010¡\u0001\u001a\u00020\u001eHÆ\u0003J\n\u0010¢\u0001\u001a\u00020\u001cHÆ\u0003J\n\u0010£\u0001\u001a\u00020\u001cHÆ\u0003J\n\u0010¤\u0001\u001a\u00020\u001eHÆ\u0003J\f\u0010¥\u0001\u001a\u0004\u0018\u00010$HÆ\u0003J\n\u0010¦\u0001\u001a\u00020\u001cHÆ\u0003J\n\u0010§\u0001\u001a\u00020\u001cHÆ\u0003J\n\u0010¨\u0001\u001a\u00020(HÆ\u0003JÚ\u0002\u0010©\u0001\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\f2\u0014\b\u0002\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00150\u00172\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001f\u001a\u00020\u001e2\b\b\u0002\u0010 \u001a\u00020\u001c2\b\b\u0002\u0010!\u001a\u00020\u001c2\b\b\u0002\u0010\"\u001a\u00020\u001e2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010$2\b\b\u0002\u0010%\u001a\u00020\u001c2\b\b\u0002\u0010&\u001a\u00020\u001c2\b\b\u0002\u0010'\u001a\u00020(HÆ\u0001¢\u0006\u0003\u0010ª\u0001J\u0016\u0010«\u0001\u001a\u00020\u001c2\t\u0010¬\u0001\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\u000b\u0010\u00ad\u0001\u001a\u00020\u0003HÖ\u0081\u0004J\u000b\u0010®\u0001\u001a\u00020\u0018HÖ\u0081\u0004R\u001e\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u0010\n\u0002\u0010/\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u00101\"\u0004\b2\u00103R\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b4\u00101\"\u0004\b5\u00103R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b6\u00107\"\u0004\b8\u00109R\u001c\u0010\t\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b:\u00101\"\u0004\b;\u00103R\u001c\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b<\u00101\"\u0004\b=\u00103R \u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b>\u0010?\"\u0004\b@\u0010AR\u001c\u0010\r\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bB\u00101\"\u0004\bC\u00103R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bD\u00101\"\u0004\bE\u00103R \u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bF\u0010?\"\u0004\bG\u0010AR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bH\u00101\"\u0004\bI\u00103R\u001c\u0010\u0011\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bJ\u00101\"\u0004\bK\u00103R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bL\u00101\"\u0004\bM\u00103R \u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00050\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bN\u0010?\"\u0004\bO\u0010AR \u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bP\u0010?\"\u0004\bQ\u0010AR&\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00150\u0017X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bR\u0010S\"\u0004\bT\u0010UR\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bV\u0010W\"\u0004\bX\u0010YR\u001a\u0010\u001b\u001a\u00020\u001cX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bZ\u0010[\"\u0004\b\\\u0010]R\u001a\u0010\u001d\u001a\u00020\u001eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b^\u0010_\"\u0004\b`\u0010aR\u001a\u0010\u001f\u001a\u00020\u001eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bb\u0010_\"\u0004\bc\u0010aR\u001a\u0010 \u001a\u00020\u001cX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bd\u0010[\"\u0004\be\u0010]R\u001a\u0010!\u001a\u00020\u001cX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bf\u0010[\"\u0004\bg\u0010]R\u001a\u0010\"\u001a\u00020\u001eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bh\u0010_\"\u0004\bi\u0010aR\u001c\u0010#\u001a\u0004\u0018\u00010$X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bj\u0010k\"\u0004\bl\u0010mR\u001a\u0010%\u001a\u00020\u001cX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bn\u0010[\"\u0004\bo\u0010]R\u001a\u0010&\u001a\u00020\u001cX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bp\u0010[\"\u0004\bq\u0010]R\u001a\u0010'\u001a\u00020(X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\br\u0010s\"\u0004\bt\u0010u¨\u0006°\u0001"},
   d2 = {"Lcom/nexomc/nexo/items/NexoMeta;", "", "customModelData", "", "model", "Lnet/kyori/adventure/key/Key;", "parentModel", "itemModel", "Lcom/nexomc/nexo/items/ItemModelBuilder;", "dyeableModel", "throwingModel", "compositeModels", "", "brokenModel", "blockingModel", "pullingModels", "chargedModel", "fireworkModel", "castModel", "damagedModels", "textureLayers", "Lteam/unnamed/creative/model/ModelTexture;", "textureVariables", "", "", "modelTextures", "Lteam/unnamed/creative/model/ModelTextures;", "containsPackInfo", "", "excludedFromInventory", "Lcom/nexomc/nexo/utils/TriState;", "excludedFromCommands", "noUpdate", "disableEnchanting", "generateModel", "customArmorTextures", "Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;", "handSwapAnimation", "oversizedInGui", "swapAnimationScale", "", "<init>", "(Ljava/lang/Integer;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lcom/nexomc/nexo/items/ItemModelBuilder;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Ljava/util/List;Ljava/util/Map;Lteam/unnamed/creative/model/ModelTextures;ZLcom/nexomc/nexo/utils/TriState;Lcom/nexomc/nexo/utils/TriState;ZZLcom/nexomc/nexo/utils/TriState;Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;ZZF)V", "getCustomModelData", "()Ljava/lang/Integer;", "setCustomModelData", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "getModel", "()Lnet/kyori/adventure/key/Key;", "setModel", "(Lnet/kyori/adventure/key/Key;)V", "getParentModel", "setParentModel", "getItemModel", "()Lcom/nexomc/nexo/items/ItemModelBuilder;", "setItemModel", "(Lcom/nexomc/nexo/items/ItemModelBuilder;)V", "getDyeableModel", "setDyeableModel", "getThrowingModel", "setThrowingModel", "getCompositeModels", "()Ljava/util/List;", "setCompositeModels", "(Ljava/util/List;)V", "getBrokenModel", "setBrokenModel", "getBlockingModel", "setBlockingModel", "getPullingModels", "setPullingModels", "getChargedModel", "setChargedModel", "getFireworkModel", "setFireworkModel", "getCastModel", "setCastModel", "getDamagedModels", "setDamagedModels", "getTextureLayers", "setTextureLayers", "getTextureVariables", "()Ljava/util/Map;", "setTextureVariables", "(Ljava/util/Map;)V", "getModelTextures", "()Lteam/unnamed/creative/model/ModelTextures;", "setModelTextures", "(Lteam/unnamed/creative/model/ModelTextures;)V", "getContainsPackInfo", "()Z", "setContainsPackInfo", "(Z)V", "getExcludedFromInventory", "()Lcom/nexomc/nexo/utils/TriState;", "setExcludedFromInventory", "(Lcom/nexomc/nexo/utils/TriState;)V", "getExcludedFromCommands", "setExcludedFromCommands", "getNoUpdate", "setNoUpdate", "getDisableEnchanting", "setDisableEnchanting", "getGenerateModel", "setGenerateModel", "getCustomArmorTextures", "()Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;", "setCustomArmorTextures", "(Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;)V", "getHandSwapAnimation", "setHandSwapAnimation", "getOversizedInGui", "setOversizedInGui", "getSwapAnimationScale", "()F", "setSwapAnimationScale", "(F)V", "packInfo", "", "packSection", "Lorg/bukkit/configuration/ConfigurationSection;", "parseModelKey", "configSection", "configString", "ensureCustomArmorTextures", "section", "hasBlockingModel", "hasCastModel", "hasBrokenModel", "hasChargedModel", "hasFireworkModel", "Lteam/unnamed/creative/model/Model;", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "reference", "Lteam/unnamed/creative/item/RangeDispatchItemModel$Entry;", "tints", "Lteam/unnamed/creative/item/tint/TintSource;", "chargedArrowCases", "Lteam/unnamed/creative/item/SelectItemModel$Case;", "Lteam/unnamed/creative/item/ItemModel;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "copy", "(Ljava/lang/Integer;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lcom/nexomc/nexo/items/ItemModelBuilder;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Ljava/util/List;Ljava/util/List;Ljava/util/Map;Lteam/unnamed/creative/model/ModelTextures;ZLcom/nexomc/nexo/utils/TriState;Lcom/nexomc/nexo/utils/TriState;ZZLcom/nexomc/nexo/utils/TriState;Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;ZZF)Lcom/nexomc/nexo/items/NexoMeta;", "equals", "other", "hashCode", "toString", "Companion", "com.nexomc:core"}
)
public final class NexoMeta {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private Integer customModelData;
   @Nullable
   private Key model;
   @NotNull
   private Key parentModel;
   @Nullable
   private ItemModelBuilder itemModel;
   @Nullable
   private Key dyeableModel;
   @Nullable
   private Key throwingModel;
   @NotNull
   private List<Key> compositeModels;
   @Nullable
   private Key brokenModel;
   @Nullable
   private Key blockingModel;
   @NotNull
   private List<Key> pullingModels;
   @Nullable
   private Key chargedModel;
   @Nullable
   private Key fireworkModel;
   @Nullable
   private Key castModel;
   @NotNull
   private List<Key> damagedModels;
   @NotNull
   private List<ModelTexture> textureLayers;
   @NotNull
   private Map<String, ModelTexture> textureVariables;
   @Nullable
   private ModelTextures modelTextures;
   private boolean containsPackInfo;
   @NotNull
   private TriState excludedFromInventory;
   @NotNull
   private TriState excludedFromCommands;
   private boolean noUpdate;
   private boolean disableEnchanting;
   @NotNull
   private TriState generateModel;
   @Nullable
   private CustomArmorTextures customArmorTextures;
   private boolean handSwapAnimation;
   private boolean oversizedInGui;
   private float swapAnimationScale;

   public NexoMeta(@Nullable Integer customModelData, @Nullable Key model, @NotNull Key parentModel, @Nullable ItemModelBuilder itemModel, @Nullable Key dyeableModel, @Nullable Key throwingModel, @NotNull List<Key> compositeModels, @Nullable Key brokenModel, @Nullable Key blockingModel, @NotNull List<Key> pullingModels, @Nullable Key chargedModel, @Nullable Key fireworkModel, @Nullable Key castModel, @NotNull List<Key> damagedModels, @NotNull List<ModelTexture> textureLayers, @NotNull Map<String, ModelTexture> textureVariables, @Nullable ModelTextures modelTextures, boolean containsPackInfo, @NotNull TriState excludedFromInventory, @NotNull TriState excludedFromCommands, boolean noUpdate, boolean disableEnchanting, @NotNull TriState generateModel, @Nullable CustomArmorTextures customArmorTextures, boolean handSwapAnimation, boolean oversizedInGui, float swapAnimationScale) {
      super();
      this.customModelData = customModelData;
      this.model = model;
      this.parentModel = parentModel;
      this.itemModel = itemModel;
      this.dyeableModel = dyeableModel;
      this.throwingModel = throwingModel;
      this.compositeModels = compositeModels;
      this.brokenModel = brokenModel;
      this.blockingModel = blockingModel;
      this.pullingModels = pullingModels;
      this.chargedModel = chargedModel;
      this.fireworkModel = fireworkModel;
      this.castModel = castModel;
      this.damagedModels = damagedModels;
      this.textureLayers = textureLayers;
      this.textureVariables = textureVariables;
      this.modelTextures = modelTextures;
      this.containsPackInfo = containsPackInfo;
      this.excludedFromInventory = excludedFromInventory;
      this.excludedFromCommands = excludedFromCommands;
      this.noUpdate = noUpdate;
      this.disableEnchanting = disableEnchanting;
      this.generateModel = generateModel;
      this.customArmorTextures = customArmorTextures;
      this.handSwapAnimation = handSwapAnimation;
      this.oversizedInGui = oversizedInGui;
      this.swapAnimationScale = swapAnimationScale;
   }

   @Nullable
   public final Integer getCustomModelData() {
      return this.customModelData;
   }

   public final void setCustomModelData(@Nullable Integer number) {
      this.customModelData = number;
   }

   @Nullable
   public final Key getModel() {
      return this.model;
   }

   public final void setModel(@Nullable Key key) {
      this.model = key;
   }

   @NotNull
   public final Key getParentModel() {
      return this.parentModel;
   }

   public final void setParentModel(@NotNull Key key) {
      this.parentModel = key;
   }

   @Nullable
   public final ItemModelBuilder getItemModel() {
      return this.itemModel;
   }

   public final void setItemModel(@Nullable ItemModelBuilder itemModelBuilder) {
      this.itemModel = itemModelBuilder;
   }

   @Nullable
   public final Key getDyeableModel() {
      return this.dyeableModel;
   }

   public final void setDyeableModel(@Nullable Key key) {
      this.dyeableModel = key;
   }

   @Nullable
   public final Key getThrowingModel() {
      return this.throwingModel;
   }

   public final void setThrowingModel(@Nullable Key key) {
      this.throwingModel = key;
   }

   @NotNull
   public final List<Key> getCompositeModels() {
      return this.compositeModels;
   }

   public final void setCompositeModels(@NotNull List<Key> keys) {
      this.compositeModels = keys;
   }

   @Nullable
   public final Key getBrokenModel() {
      return this.brokenModel;
   }

   public final void setBrokenModel(@Nullable Key key) {
      this.brokenModel = key;
   }

   @Nullable
   public final Key getBlockingModel() {
      return this.blockingModel;
   }

   public final void setBlockingModel(@Nullable Key key) {
      this.blockingModel = key;
   }

   @NotNull
   public final List<Key> getPullingModels() {
      return this.pullingModels;
   }

   public final void setPullingModels(@NotNull List<Key> keys) {
      this.pullingModels = keys;
   }

   @Nullable
   public final Key getChargedModel() {
      return this.chargedModel;
   }

   public final void setChargedModel(@Nullable Key key) {
      this.chargedModel = key;
   }

   @Nullable
   public final Key getFireworkModel() {
      return this.fireworkModel;
   }

   public final void setFireworkModel(@Nullable Key key) {
      this.fireworkModel = key;
   }

   @Nullable
   public final Key getCastModel() {
      return this.castModel;
   }

   public final void setCastModel(@Nullable Key key) {
      this.castModel = key;
   }

   @NotNull
   public final List<Key> getDamagedModels() {
      return this.damagedModels;
   }

   public final void setDamagedModels(@NotNull List<Key> keys) {
      this.damagedModels = keys;
   }

   @NotNull
   public final List<ModelTexture> getTextureLayers() {
      return this.textureLayers;
   }

   public final void setTextureLayers(@NotNull List<ModelTexture> modelTextures2) {
      this.textureLayers = modelTextures2;
   }

   @NotNull
   public final Map<String, ModelTexture> getTextureVariables() {
      return this.textureVariables;
   }

   public final void setTextureVariables(@NotNull Map<String, ModelTexture> map) {
      this.textureVariables = map;
   }

   @Nullable
   public final ModelTextures getModelTextures() {
      return this.modelTextures;
   }

   public final void setModelTextures(@Nullable ModelTextures modelTextures2) {
      this.modelTextures = modelTextures2;
   }

   public final boolean getContainsPackInfo() {
      return this.containsPackInfo;
   }

   public final void setContainsPackInfo(boolean flag) {
      this.containsPackInfo = flag;
   }

   @NotNull
   public final TriState getExcludedFromInventory() {
      return this.excludedFromInventory;
   }

   public final void setExcludedFromInventory(@NotNull TriState triState) {
      this.excludedFromInventory = triState;
   }

   @NotNull
   public final TriState getExcludedFromCommands() {
      return this.excludedFromCommands;
   }

   public final void setExcludedFromCommands(@NotNull TriState triState) {
      this.excludedFromCommands = triState;
   }

   public final boolean getNoUpdate() {
      return this.noUpdate;
   }

   public final void setNoUpdate(boolean flag) {
      this.noUpdate = flag;
   }

   public final boolean getDisableEnchanting() {
      return this.disableEnchanting;
   }

   public final void setDisableEnchanting(boolean flag) {
      this.disableEnchanting = flag;
   }

   @NotNull
   public final TriState getGenerateModel() {
      return this.generateModel;
   }

   public final void setGenerateModel(@NotNull TriState triState) {
      this.generateModel = triState;
   }

   @Nullable
   public final CustomArmorTextures getCustomArmorTextures() {
      return this.customArmorTextures;
   }

   public final void setCustomArmorTextures(@Nullable CustomArmorTextures customArmorTextures2) {
      this.customArmorTextures = customArmorTextures2;
   }

   public final boolean getHandSwapAnimation() {
      return this.handSwapAnimation;
   }

   public final void setHandSwapAnimation(boolean flag) {
      this.handSwapAnimation = flag;
   }

   public final boolean getOversizedInGui() {
      return this.oversizedInGui;
   }

   public final void setOversizedInGui(boolean flag) {
      this.oversizedInGui = flag;
   }

   public final float getSwapAnimationScale() {
      return this.swapAnimationScale;
   }

   public final void setSwapAnimationScale(float value) {
      this.swapAnimationScale = value;
   }

   public final void packInfo(@NotNull ConfigurationSection packSection) {
      this.containsPackInfo = true;
      Key key = Model.ITEM_GENERATED;
      this.parentModel = NexoYamlKt.key(packSection, "parent_model", NexoYamlKt.key(packSection, "parent", key));
      Key key2 = this.parseModelKey(packSection, "bbmodel");
      if (key2 == null) {
         key2 = this.parseModelKey(packSection, "model");
      }

      this.model = key2;
      key2 = this.parseModelKey(packSection, "broken_model");
      if (key2 == null) {
         key2 = NexoYamlKt.key(packSection, "broken_texture");
         key2 = key2 != null ? KeyUtils.dropExtension(key2) : null;
      }

      this.brokenModel = key2;
      key2 = this.parseModelKey(packSection, "blocking_model");
      if (key2 == null) {
         key2 = NexoYamlKt.key(packSection, "blocking_texture");
         key2 = key2 != null ? KeyUtils.dropExtension(key2) : null;
      }

      this.blockingModel = key2;
      key2 = this.parseModelKey(packSection, "cast_model");
      if (key2 == null) {
         key2 = NexoYamlKt.key(packSection, "cast_texture");
         key2 = key2 != null ? KeyUtils.dropExtension(key2) : null;
      }

      this.castModel = key2;
      key2 = this.parseModelKey(packSection, "charged_model");
      if (key2 == null) {
         key2 = NexoYamlKt.key(packSection, "charged_texture");
         key2 = key2 != null ? KeyUtils.dropExtension(key2) : null;
      }

      this.chargedModel = key2;
      key2 = this.parseModelKey(packSection, "firework_model");
      if (key2 == null) {
         key2 = NexoYamlKt.key(packSection, "firework_texture");
         key2 = key2 != null ? KeyUtils.dropExtension(key2) : null;
      }

      this.fireworkModel = key2;
      NexoMeta nexoMeta = this;
      List list = NexoYamlKt.keyListOrNull(packSection, "pulling_models");
      if (list == null) {
         Iterable iterable = (Iterable)NexoYamlKt.keyList(packSection, "pulling_textures");
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            Key it = (Key)item;
            int value = 0;
            destination.add(KeyUtils.dropExtension(it));
         }

         list = (List)destination;
         nexoMeta = this;
      }

      nexoMeta.pullingModels = list;
      nexoMeta = this;
      list = NexoYamlKt.keyListOrNull(packSection, "damaged_models");
      if (list == null) {
         Iterable iterable2 = (Iterable)NexoYamlKt.keyList(packSection, "damaged_textures");
         Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object obj : iterable2) {
            Key it = (Key)obj;
            int value2 = 0;
            collection.add(KeyUtils.dropExtension(it));
         }

         list = (List)collection;
         nexoMeta = this;
      }

      nexoMeta.damagedModels = list;
      Key key3 = this.parseModelKey(packSection, "dyeable_model");
      if (key3 == null) {
         key3 = NexoYamlKt.key(packSection, "dyeable_texture");
         key3 = key3 != null ? KeyUtils.dropExtension(key3) : null;
      }

      this.dyeableModel = key3;
      nexoMeta = this;
      List list2 = NexoYamlKt.keyListOrNull(packSection, "composite_models");
      if (list2 == null) {
         Iterable iterable3 = (Iterable)NexoYamlKt.keyList(packSection, "composite_textures");
         Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

         for(Object obj2 : iterable3) {
            Key it = (Key)obj2;
            int value3 = 0;
            collection2.add(KeyUtils.dropExtension(it));
         }

         list2 = (List)collection2;
         nexoMeta = this;
      }

      nexoMeta.compositeModels = list2;
      ConfigurationSection configurationSection = NexoYamlKt.section(packSection, "textures");
      if (configurationSection != null) {
         ConfigurationSection section = configurationSection;
         int value4 = 0;
         this.textureVariables = NexoYamlKt.toTypedMap(section, NexoMeta::packInfo$lambda$3$0);
      } else {
         NexoMeta nexoMeta2 = this;
         int value5 = 0;
         Iterable iterable4 = (Iterable)NexoYamlKt.keyListOrSingle(packSection, "texture", (String)null);
         Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable4, 10)));

         for(Object obj3 : iterable4) {
            Key it = (Key)obj3;
            int value6 = 0;
            collection3.add(ModelTexture.ofKey(KeyUtils.dropExtension(it)));
         }

         nexoMeta2.textureLayers = (List)collection3;
      }

      ModelTextures.Builder builder = ModelTextures.builder();
      ModelTexture modelTexture = this.textureVariables.get("particle");
      if (modelTexture == null) {
         modelTexture = CollectionsKt.firstOrNull(this.textureLayers);
      }

      this.modelTextures = builder.particle(modelTexture).variables(this.textureVariables).layers(this.textureLayers).build();
      this.customModelData = NexoYamlKt.intOrNull(packSection, "custom_model_data");
      this.handSwapAnimation = packSection.getBoolean("hand_swap_animation", true);
      this.oversizedInGui = packSection.getBoolean("oversized_in_gui", false);
      Float number = NexoYamlKt.floatOrNull(packSection, "swap_animation_scale");
      this.swapAnimationScale = number != null ? number : 1.0F;
      if (packSection.contains("model")) {
         this.generateModel = TriState.FALSE;
      } else if (!packSection.contains("texture") && !packSection.contains("textures")) {
         this.generateModel = TriState.NOT_SET;
      } else {
         this.generateModel = TriState.TRUE;
      }

   }

   private final Key parseModelKey(ConfigurationSection configSection, String configString) {
      String modelName = NexoYamlKt.stringOrNull(configSection, configString);
      String str = NexoYamlKt.getRootId(configSection).toLowerCase(Locale.ROOT);
      String parent = StringsKt.replace(str, " ", "_", false);
      Key key;
      if (modelName == null && Intrinsics.areEqual(configString, "model") && Key.parseable(parent)) {
         key = Key.key(parent);
      } else if (Key.parseable(modelName)) {
         key = KeyUtils.dropExtension(modelName);
      } else {
         key = null;
      }

      return key;
   }

   public final void ensureCustomArmorTextures(@NotNull ConfigurationSection section) {
      if (this.customArmorTextures == null) {
         NexoMeta nexoMeta = this;

         Object obj2;
         try {
            NexoMeta nexoMeta2 = nexoMeta;
            int value = 0;
            ConfigurationSection configurationSection = NexoYamlKt.section(section, "Pack.CustomArmor");
            CustomArmorTextures customArmorTextures2;
            if (configurationSection != null) {
               ConfigurationSection p0 = configurationSection;
               int value2 = 0;
               customArmorTextures2 = new CustomArmorTextures(p0);
            } else {
               customArmorTextures2 = null;
            }

            obj2 = ResultShim.ok(customArmorTextures2);
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = UtilsKt.printOnFailure(obj2, false);
         CustomArmorTextures customArmorTextures3 = (CustomArmorTextures)(ResultShim.isFailure(obj) ? null : obj);
         if (customArmorTextures3 == null) {
            block: {
               Regex regex = UtilsKt.takeUnless(CustomArmorType.Companion.getItemIdRegex(), this.generateModel.isFalse());
               if (regex != null) {
                  MatchResult matchResult = regex.find((CharSequence)NexoYamlKt.getRootId(section), 0);
                  if (matchResult != null) {
                     String str = matchResult.getValue();
                     if (str != null) {
                        customArmorTextures3 = StringUtilsKt.ifNotEmpty(str, s -> new CustomArmorTextures(s));
                        break block;
                     }
                  }
               }

               customArmorTextures3 = null;
            }
         }

         this.customArmorTextures = customArmorTextures3;
      }
   }

   public final boolean hasBlockingModel() {
      Key key = this.blockingModel;
      boolean flag;
      if (key != null) {
         String str = key.value();
         if (str != null) {
            flag = ((CharSequence)str).length() > 0;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   public final boolean hasCastModel() {
      Key key = this.castModel;
      boolean flag;
      if (key != null) {
         String str = key.value();
         if (str != null) {
            flag = ((CharSequence)str).length() > 0;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   public final boolean hasBrokenModel() {
      Key key = this.brokenModel;
      boolean flag;
      if (key != null) {
         String str = key.value();
         if (str != null) {
            flag = ((CharSequence)str).length() > 0;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   public final boolean hasChargedModel() {
      Key key = this.chargedModel;
      boolean flag;
      if (key != null) {
         String str = key.value();
         if (str != null) {
            flag = ((CharSequence)str).length() > 0;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   public final boolean hasFireworkModel() {
      Key key = this.fireworkModel;
      boolean flag;
      if (key != null) {
         String str = key.value();
         if (str != null) {
            flag = ((CharSequence)str).length() > 0;
            return flag;
         }
      }

      flag = false;
      return flag;
   }

   @Nullable
   public final Model dyeableModel(@NotNull ResourcePack resourcePack, @NotNull Model reference) {
      Key key = this.dyeableModel;
      Model model2;
      if (key != null) {
         Key it = key;
         int value = 0;
         model2 = resourcePack.model(it);
         if (model2 == null) {
            Model.Builder toBuilder = reference.toBuilder();
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            Key key2 = reference.key();
            toBuilder = toBuilder.key(keyUtils.appendSuffix(key2, "_dyeable"));
            ModelTextures.Builder builder = ModelTextures.builder();
            ModelTexture[] p0 = new ModelTexture[]{ModelTexture.ofKey(it.key())};
            Model p0_8 = toBuilder.textures(builder.layers(p0).build()).build();
            int value2 = 0;
            resourcePack.model(p0_8);
            model2 = p0_8;
         }
      } else {
         model2 = null;
      }

      return model2;
   }

   @Nullable
   public final Model throwingModel(@NotNull ResourcePack resourcePack, @NotNull Model reference) {
      Key key = this.throwingModel;
      Model model2;
      if (key != null) {
         Key it = key;
         int value = 0;
         model2 = resourcePack.model(it);
         if (model2 == null) {
            Model.Builder toBuilder = reference.toBuilder();
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            Key key2 = reference.key();
            toBuilder = toBuilder.key(keyUtils.appendSuffix(key2, "_throwing"));
            ModelTextures.Builder builder = ModelTextures.builder();
            ModelTexture[] p0 = new ModelTexture[]{ModelTexture.ofKey(it.key())};
            Model p0_7 = toBuilder.textures(builder.layers(p0).build()).build();
            int value2 = 0;
            resourcePack.model(p0_7);
            model2 = p0_7;
         }
      } else {
         model2 = null;
      }

      return model2;
   }

   @Nullable
   public final Model castModel(@NotNull ResourcePack resourcePack, @NotNull Model reference) {
      Key key = this.castModel;
      Model model2;
      if (key != null) {
         Key it = key;
         int value = 0;
         model2 = resourcePack.model(it);
         if (model2 == null) {
            Model.Builder toBuilder = reference.toBuilder();
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            Key key2 = reference.key();
            toBuilder = toBuilder.key(keyUtils.appendSuffix(key2, "_cast"));
            ModelTextures.Builder builder = ModelTextures.builder();
            ModelTexture[] p0 = new ModelTexture[]{ModelTexture.ofKey(it.key())};
            Model p0_6 = toBuilder.textures(builder.layers(p0).build()).build();
            int value2 = 0;
            resourcePack.model(p0_6);
            model2 = p0_6;
         }
      } else {
         model2 = null;
      }

      return model2;
   }

   @Nullable
   public final Model brokenModel(@NotNull ResourcePack resourcePack, @NotNull Model reference) {
      Key key = this.brokenModel;
      Model model2;
      if (key != null) {
         Key it = key;
         int value = 0;
         model2 = resourcePack.model(it);
         if (model2 == null) {
            Model.Builder toBuilder = reference.toBuilder();
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            Key key2 = reference.key();
            toBuilder = toBuilder.key(keyUtils.appendSuffix(key2, "_broken"));
            ModelTextures.Builder builder = ModelTextures.builder();
            ModelTexture[] p0 = new ModelTexture[]{ModelTexture.ofKey(it.key())};
            Model p0_5 = toBuilder.textures(builder.layers(p0).build()).build();
            int value2 = 0;
            resourcePack.model(p0_5);
            model2 = p0_5;
         }
      } else {
         model2 = null;
      }

      return model2;
   }

   @Nullable
   public final Model blockingModel(@NotNull ResourcePack resourcePack, @NotNull Model reference) {
      Key key = this.blockingModel;
      Model model2;
      if (key != null) {
         Key it = key;
         int value = 0;
         model2 = resourcePack.model(it);
         if (model2 == null) {
            Model.Builder toBuilder = reference.toBuilder();
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            Key key2 = reference.key();
            toBuilder = toBuilder.key(keyUtils.appendSuffix(key2, "_blocking"));
            ModelTextures.Builder builder = ModelTextures.builder();
            ModelTexture[] p0 = new ModelTexture[]{ModelTexture.ofKey(it.key())};
            Model p0_4 = toBuilder.textures(builder.layers(p0).build()).build();
            int value2 = 0;
            resourcePack.model(p0_4);
            model2 = p0_4;
         }
      } else {
         model2 = null;
      }

      return model2;
   }

   @NotNull
   public final List<RangeDispatchItemModel.Entry> pullingModels(@NotNull ResourcePack resourcePack, @NotNull Model reference, @NotNull List<TintSource> tints) {
      Iterable iterable = (Iterable)this.pullingModels;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));
      int index = 0;

      for(Object item : iterable) {
         int value = index++;
         if (value < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Key pulling = (Key)item;
         int value2 = 0;
         float pull = value == 0 ? 0.0F : Math.min(UtilsKt.roundPredicate((double)(((float)value + 1.0F) / (float)this.pullingModels.size()), 0.05f), 0.9F);
         if (resourcePack.model(pulling) == null) {
            Model.Builder builder = reference.toBuilder().key(pulling);
            ModelTextures.Builder builder2 = ModelTextures.builder();
            ModelTexture[] modelTextures2 = new ModelTexture[]{ModelTexture.ofKey(pulling)};
            builder.textures(builder2.layers(modelTextures2).build()).build().addTo(resourcePack);
         }

         destination.add(RangeDispatchItemModel.Entry.entry(pull, ItemModel.reference(pulling, tints)));
      }

      return (List)destination;
   }

   @NotNull
   public final List<SelectItemModel.Case> chargedArrowCases(@NotNull List<TintSource> tints) {
      List createListBuilder = CollectionsKt.createListBuilder();
      int value = 0;
      Key key = this.chargedModel;
      if (key != null) {
         Key it = key;
         int value2 = 0;
         ItemModel itemModel2 = ItemModel.reference(it, tints);
         JsonElement[] jsonElements = new JsonElement[]{new JsonPrimitive("arrow")};
         SelectItemModel.Case caseValue2 = SelectItemModel.Case._case(itemModel2, jsonElements);
         createListBuilder.add(caseValue2);
      }

      key = this.fireworkModel;
      if (key != null) {
         Key it = key;
         int value3 = 0;
         ItemModel itemModel3 = ItemModel.reference(it, tints);
         JsonElement[] jsonElements2 = new JsonElement[]{new JsonPrimitive("rocket")};
         SelectItemModel.Case caseValue3 = SelectItemModel.Case._case(itemModel3, jsonElements2);
         createListBuilder.add(caseValue3);
      }

      return CollectionsKt.build(createListBuilder);
   }

   @NotNull
   public final List<ItemModel> compositeModels(@NotNull ResourcePack resourcePack) {
      Iterable iterable = (Iterable)this.compositeModels;
      ArrayList first = new ArrayList();
      ArrayList second = new ArrayList();

      for(Object element : iterable) {
         Key it = (Key)element;
         int value = 0;
         if (resourcePack.model(it) != null) {
            first.add(element);
         } else {
            second.add(element);
         }
      }

      Pair pair = new Pair(first, second);
      int value2 = 0;
      List models = (List)pair.component1();
      List textures = (List)pair.component2();
      Iterable iterable2 = (Iterable)textures;
      int value3 = 0;

      for(Object obj : iterable2) {
         Key it = (Key)obj;
         int value4 = 0;
         resourcePack.model(Model.model().key(it).build());
      }

      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));
      value3 = 0;

      for(Object item : iterable2) {
         Key p0 = (Key)item;
         int value5 = 0;
         destination.add(ItemModel.reference(p0));
      }

      List textures_3 = (List)destination;
      Iterable iterable3 = (Iterable)models;
      Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

      for(Object obj2 : iterable3) {
         Key p0 = (Key)obj2;
         int value6 = 0;
         collection.add(ItemModel.reference(p0));
      }

      List models_1 = (List)collection;
      return CollectionsKt.plus((Collection)textures_3, (Iterable)models_1);
   }

   @Nullable
   public final Integer component1() {
      return this.customModelData;
   }

   @Nullable
   public final Key component2() {
      return this.model;
   }

   @NotNull
   public final Key component3() {
      return this.parentModel;
   }

   @Nullable
   public final ItemModelBuilder component4() {
      return this.itemModel;
   }

   @Nullable
   public final Key component5() {
      return this.dyeableModel;
   }

   @Nullable
   public final Key component6() {
      return this.throwingModel;
   }

   @NotNull
   public final List<Key> component7() {
      return this.compositeModels;
   }

   @Nullable
   public final Key component8() {
      return this.brokenModel;
   }

   @Nullable
   public final Key component9() {
      return this.blockingModel;
   }

   @NotNull
   public final List<Key> component10() {
      return this.pullingModels;
   }

   @Nullable
   public final Key component11() {
      return this.chargedModel;
   }

   @Nullable
   public final Key component12() {
      return this.fireworkModel;
   }

   @Nullable
   public final Key component13() {
      return this.castModel;
   }

   @NotNull
   public final List<Key> component14() {
      return this.damagedModels;
   }

   @NotNull
   public final List<ModelTexture> component15() {
      return this.textureLayers;
   }

   @NotNull
   public final Map<String, ModelTexture> component16() {
      return this.textureVariables;
   }

   @Nullable
   public final ModelTextures component17() {
      return this.modelTextures;
   }

   public final boolean component18() {
      return this.containsPackInfo;
   }

   @NotNull
   public final TriState component19() {
      return this.excludedFromInventory;
   }

   @NotNull
   public final TriState component20() {
      return this.excludedFromCommands;
   }

   public final boolean component21() {
      return this.noUpdate;
   }

   public final boolean component22() {
      return this.disableEnchanting;
   }

   @NotNull
   public final TriState component23() {
      return this.generateModel;
   }

   @Nullable
   public final CustomArmorTextures component24() {
      return this.customArmorTextures;
   }

   public final boolean component25() {
      return this.handSwapAnimation;
   }

   public final boolean component26() {
      return this.oversizedInGui;
   }

   public final float component27() {
      return this.swapAnimationScale;
   }

   @NotNull
   public final NexoMeta copy(@Nullable Integer customModelData, @Nullable Key model, @NotNull Key parentModel, @Nullable ItemModelBuilder itemModel, @Nullable Key dyeableModel, @Nullable Key throwingModel, @NotNull List<Key> compositeModels, @Nullable Key brokenModel, @Nullable Key blockingModel, @NotNull List<Key> pullingModels, @Nullable Key chargedModel, @Nullable Key fireworkModel, @Nullable Key castModel, @NotNull List<Key> damagedModels, @NotNull List<ModelTexture> textureLayers, @NotNull Map<String, ModelTexture> textureVariables, @Nullable ModelTextures modelTextures, boolean containsPackInfo, @NotNull TriState excludedFromInventory, @NotNull TriState excludedFromCommands, boolean noUpdate, boolean disableEnchanting, @NotNull TriState generateModel, @Nullable CustomArmorTextures customArmorTextures, boolean handSwapAnimation, boolean oversizedInGui, float swapAnimationScale) {
      return new NexoMeta(customModelData, model, parentModel, itemModel, dyeableModel, throwingModel, compositeModels, brokenModel, blockingModel, pullingModels, chargedModel, fireworkModel, castModel, damagedModels, textureLayers, textureVariables, modelTextures, containsPackInfo, excludedFromInventory, excludedFromCommands, noUpdate, disableEnchanting, generateModel, customArmorTextures, handSwapAnimation, oversizedInGui, swapAnimationScale);
   }

   @NotNull
   public String toString() {
      return "NexoMeta(customModelData=" + this.customModelData + ", model=" + this.model + ", parentModel=" + this.parentModel + ", itemModel=" + this.itemModel + ", dyeableModel=" + this.dyeableModel + ", throwingModel=" + this.throwingModel + ", compositeModels=" + this.compositeModels + ", brokenModel=" + this.brokenModel + ", blockingModel=" + this.blockingModel + ", pullingModels=" + this.pullingModels + ", chargedModel=" + this.chargedModel + ", fireworkModel=" + this.fireworkModel + ", castModel=" + this.castModel + ", damagedModels=" + this.damagedModels + ", textureLayers=" + this.textureLayers + ", textureVariables=" + this.textureVariables + ", modelTextures=" + this.modelTextures + ", containsPackInfo=" + this.containsPackInfo + ", excludedFromInventory=" + this.excludedFromInventory + ", excludedFromCommands=" + this.excludedFromCommands + ", noUpdate=" + this.noUpdate + ", disableEnchanting=" + this.disableEnchanting + ", generateModel=" + this.generateModel + ", customArmorTextures=" + this.customArmorTextures + ", handSwapAnimation=" + this.handSwapAnimation + ", oversizedInGui=" + this.oversizedInGui + ", swapAnimationScale=" + this.swapAnimationScale + ")";
   }

   public int hashCode() {
      int result = this.customModelData == null ? 0 : this.customModelData.hashCode();
      result = result * 31 + (this.model == null ? 0 : this.model.hashCode());
      result = result * 31 + this.parentModel.hashCode();
      result = result * 31 + (this.itemModel == null ? 0 : this.itemModel.hashCode());
      result = result * 31 + (this.dyeableModel == null ? 0 : this.dyeableModel.hashCode());
      result = result * 31 + (this.throwingModel == null ? 0 : this.throwingModel.hashCode());
      result = result * 31 + this.compositeModels.hashCode();
      result = result * 31 + (this.brokenModel == null ? 0 : this.brokenModel.hashCode());
      result = result * 31 + (this.blockingModel == null ? 0 : this.blockingModel.hashCode());
      result = result * 31 + this.pullingModels.hashCode();
      result = result * 31 + (this.chargedModel == null ? 0 : this.chargedModel.hashCode());
      result = result * 31 + (this.fireworkModel == null ? 0 : this.fireworkModel.hashCode());
      result = result * 31 + (this.castModel == null ? 0 : this.castModel.hashCode());
      result = result * 31 + this.damagedModels.hashCode();
      result = result * 31 + this.textureLayers.hashCode();
      result = result * 31 + this.textureVariables.hashCode();
      result = result * 31 + (this.modelTextures == null ? 0 : this.modelTextures.hashCode());
      result = result * 31 + Boolean.hashCode(this.containsPackInfo);
      result = result * 31 + this.excludedFromInventory.hashCode();
      result = result * 31 + this.excludedFromCommands.hashCode();
      result = result * 31 + Boolean.hashCode(this.noUpdate);
      result = result * 31 + Boolean.hashCode(this.disableEnchanting);
      result = result * 31 + this.generateModel.hashCode();
      result = result * 31 + (this.customArmorTextures == null ? 0 : this.customArmorTextures.hashCode());
      result = result * 31 + Boolean.hashCode(this.handSwapAnimation);
      result = result * 31 + Boolean.hashCode(this.oversizedInGui);
      result = result * 31 + Float.hashCode(this.swapAnimationScale);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof NexoMeta)) {
         return false;
      } else {
         NexoMeta nexoMeta = (NexoMeta)other;
         if (!Intrinsics.areEqual(this.customModelData, nexoMeta.customModelData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.model, nexoMeta.model)) {
            return false;
         } else if (!Intrinsics.areEqual(this.parentModel, nexoMeta.parentModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.itemModel, nexoMeta.itemModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.dyeableModel, nexoMeta.dyeableModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.throwingModel, nexoMeta.throwingModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.compositeModels, nexoMeta.compositeModels)) {
            return false;
         } else if (!Intrinsics.areEqual(this.brokenModel, nexoMeta.brokenModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.blockingModel, nexoMeta.blockingModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pullingModels, nexoMeta.pullingModels)) {
            return false;
         } else if (!Intrinsics.areEqual(this.chargedModel, nexoMeta.chargedModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fireworkModel, nexoMeta.fireworkModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.castModel, nexoMeta.castModel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.damagedModels, nexoMeta.damagedModels)) {
            return false;
         } else if (!Intrinsics.areEqual(this.textureLayers, nexoMeta.textureLayers)) {
            return false;
         } else if (!Intrinsics.areEqual(this.textureVariables, nexoMeta.textureVariables)) {
            return false;
         } else if (!Intrinsics.areEqual(this.modelTextures, nexoMeta.modelTextures)) {
            return false;
         } else if (this.containsPackInfo != nexoMeta.containsPackInfo) {
            return false;
         } else if (this.excludedFromInventory != nexoMeta.excludedFromInventory) {
            return false;
         } else if (this.excludedFromCommands != nexoMeta.excludedFromCommands) {
            return false;
         } else if (this.noUpdate != nexoMeta.noUpdate) {
            return false;
         } else if (this.disableEnchanting != nexoMeta.disableEnchanting) {
            return false;
         } else if (this.generateModel != nexoMeta.generateModel) {
            return false;
         } else if (!Intrinsics.areEqual(this.customArmorTextures, nexoMeta.customArmorTextures)) {
            return false;
         } else if (this.handSwapAnimation != nexoMeta.handSwapAnimation) {
            return false;
         } else if (this.oversizedInGui != nexoMeta.oversizedInGui) {
            return false;
         } else {
            return Float.compare(this.swapAnimationScale, nexoMeta.swapAnimationScale) == 0;
         }
      }
   }

   public NexoMeta() {
      this(null, null, Model.ITEM_GENERATED, null, null, null, new java.util.ArrayList<Key>(), null, null, new java.util.ArrayList<Key>(), null, null, null, new java.util.ArrayList<Key>(), new java.util.ArrayList<ModelTexture>(), new java.util.HashMap<String, ModelTexture>(), null, false, TriState.NOT_SET, TriState.NOT_SET, false, false, TriState.NOT_SET, null, true, false, 1.0F);
   }

   private static final ModelTexture packInfo$lambda$3$0(Object it) {
      return ModelTexture.ofKey(KeyUtils.dropExtension(KeyUtils.parseKey(it.toString(), false)));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/items/NexoMeta$Companion;", "", "<init>", "()V", "DEFAULT", "Lcom/nexomc/nexo/items/NexoMeta;", "getDEFAULT", "()Lcom/nexomc/nexo/items/NexoMeta;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NexoMeta getDEFAULT() {
         return new NexoMeta();
      }
   }
}
