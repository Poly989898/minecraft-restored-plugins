#!/usr/bin/env python3
"""Source repairs for the decompiled legacy (Spigot-mapped) NMS modules. Idempotent. Run from nms/."""
import re, os, glob
MODS = ['v1_20_R1','v1_20_R3','v1_21_R1','v1_21_R3','v1_21_R4','v1_21_R5','v1_21_R6','v1_21_R7']
KW = 'do|if|for|int|new|try|case|else|goto|byte|char|long|void|enum|this|null|true|false|class|final|short|float|double|boolean|break|catch|const|super|while|throw|throws|import|package|return|static|switch|public|private|protected|abstract|interface|extends|implements|native|volatile|transient|synchronized|strictfp|instanceof|assert|default|continue|finally'
total = 0
def rw(path, fn):
    global total
    if not os.path.exists(path): return
    s = open(path, encoding='utf-8').read(); t = fn(s)
    if t != s: open(path, 'w', encoding='utf-8').write(t); total += 1
for mod in MODS:
    base = f'{mod}/src/main/java/io/lumine/mythic/core/volatilecode/{mod}/'
    legacy120 = mod in ('v1_20_R1', 'v1_20_R3')
    # selector lambdas: cast to the Supplier's element type declared by the module's goal class
    goal = open(base + 'ai/pathfinding/AsyncNearestAttackableTargetGoal.java', encoding='utf-8').read()
    if 'Supplier<Predicate<EntityLiving>>' in goal: sel = 'java.util.function.Predicate<net.minecraft.world.entity.EntityLiving>'
    else: sel = 'net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition.a'
    for f in ('OtherFactionMonstersGoal', 'SpecificFactionMonstersGoal'):
        rw(base + f'ai/targeters/{f}.java', lambda s: re.sub(r'return (\(targetEntity, serverLevel\)|targetEntity) -> false;', lambda m: f'return ({sel}){m.group(1)} -> false;', s) if f'return ({sel})' not in s else s)
    rw(base + 'packets/PacketInterceptor.java', lambda s: s.replace('return modifier == null ? original : modifier.modify(original);', 'return modifier == null ? original : (Packet<T>)(Packet)((PacketInterceptor.Modifier)modifier).modify(original);'))
    rw(base + 'virtual/VirtualFallingBlock.java', lambda s: s.replace('Vector3dc location = this.getWrapper().getLocation().get();', 'Vector3dc location = (Vector3dc)this.getWrapper().getLocation().get();'))
    rw(base + 'virtual/VirtualItem.java', lambda s: s.replace(', !bool)', ', !((Boolean)bool))'))
    rw(base + 'ai/pathfinding/MegSupport.java', lambda s: s.replace('mobPos.a(target, 2.0)', 'mobPos.a((net.minecraft.core.IPosition)target, 2.0)'))
    rw(base + 'VolatileAIHandlerImpl.java', lambda s: re.sub(r'\be\.([gh])\(null\);', r'e.\1((net.minecraft.world.entity.EntityLiving)null);', s))
    rw(base + 'VolatilePacketHandlerImpl.java', lambda s: re.sub(r'\bsp\.([cd])\(null\);', r'sp.\1((net.minecraft.world.entity.Entity)null);', s))
    rw(base + 'VolatileEntityHandlerImpl.java', lambda s: s.replace('AttributeDefaults.a(CraftEntityType.bukkitToMinecraft(entityType))', 'AttributeDefaults.a((net.minecraft.world.entity.EntityTypes)CraftEntityType.bukkitToMinecraft(entityType))'))
    # writeCollection(List.of(..), PacketDataSerializer::a): pick the exact overload used by the original bytecode
    writer = '(net.minecraft.network.PacketDataSerializer.b<String>)' if legacy120 else '(net.minecraft.network.codec.StreamEncoder<net.minecraft.network.PacketDataSerializer, String>)'
    def wc(s):
        out = []
        for line in s.split('\n'):
            if 'PacketDataSerializer::a)' in line and 'List.of(' in line and writer not in line:
                line = line.replace('List.of(', 'List.<String>of(').replace('PacketDataSerializer::a)', writer + 'PacketDataSerializer::a)')
            out.append(line)
        return '\n'.join(out)
    rw(base + 'VolatileEntityHandlerImpl.java', wc)
    rw(base + 'VolatileEntityHandlerImpl.java', lambda s: s.replace('List.of(b.a(SHARED_FLAGS, b))', 'List.of(net.minecraft.network.syncher.DataWatcher.b.a(SHARED_FLAGS, b))'))
    # original bytecode: iload; i2d; WorldBorder.a(D)V  (setSize(double)); plain `a(radius)` would bind to a(int)
    rw(base + 'VolatileEntityHandlerImpl.java', lambda s: s.replace('border.a(radius);', 'border.a((double)radius);'))
    rw(base + 'VolatileItemHandlerImpl.java', lambda s: s.replace('List<IChatBaseComponent> components = lore.stream()\n         .map(', 'List<IChatBaseComponent> components = lore.stream()\n         .<IChatBaseComponent>map('))
    # keyword-named obfuscated methods: x.do() -> x.do_() (restored in bytecode by Unrename after compilation)
    for p in glob.glob(base + '**/*.java', recursive=True):
        rw(p, lambda s: re.sub(r'\.(' + KW + r')\(', r'.\1_(', s))
print('files changed:', total)
