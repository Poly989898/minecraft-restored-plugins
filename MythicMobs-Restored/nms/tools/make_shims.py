#!/usr/bin/env python3
"""Regenerate shims + build.gradle for the legacy Spigot-mapped NMS modules. Run from nms/."""
import glob, os, subprocess, sys, zipfile
GH = os.environ.get('GRADLE_USER_HOME') or os.path.expanduser('~/.gradle')
NMS = os.getcwd()
ORIG = os.path.join(NMS, '..', 'original', 'MythicMobs-5.14.0.jar')
M2 = os.path.expanduser('~/.m2/repository/org/spigotmc/spigot')
MODS = {'v1_20_R1': '1.20.1', 'v1_20_R3': '1.20.4', 'v1_21_R1': '1.21.1', 'v1_21_R3': '1.21.4',
        'v1_21_R4': '1.21.5', 'v1_21_R5': '1.21.8', 'v1_21_R6': '1.21.10', 'v1_21_R7': '1.21.11'}
GRADLE = '''plugins {{ id 'java' }}
java {{ toolchain {{ languageVersion = JavaLanguageVersion.of(25) }} }}
repositories {{
    mavenLocal()
    mavenCentral()
    maven {{ url 'https://repo.papermc.io/repository/maven-public/' }}
    maven {{ url 'https://libraries.minecraft.net/' }}
    maven {{ url 'https://hub.spigotmc.org/nexus/content/repositories/snapshots/' }}
}}
dependencies {{
    // order matters: shimmed server jar first, then the matching Paper API (no Spigot API: it conflicts with it)
    compileOnly files("$rootDir/shim/{mc}.jar")
    compileOnly('org.spigotmc:spigot:{sv}') {{ exclude group: 'org.spigotmc', module: 'spigot-api' }}
    compileOnly 'io.papermc.paper:paper-api:{mc}-R0.1-SNAPSHOT'
    compileOnly files({spigot_api})   // only classes missing from the Paper API (e.g. org.bukkit.registry.RegistryAware); files() sort before modules, so it must not overlap
    compileOnly files("$rootDir/../plugin/build/classes/java/main", "$rootDir/../plugin/libs/shaded.jar")
    compileOnly 'org.projectlombok:lombok:1.18.34'
    compileOnly 'org.jetbrains:annotations:24.1.0'
    compileOnly 'com.google.code.findbugs:jsr305:3.0.2'
}}
tasks.withType(JavaCompile) {{
    options.encoding = 'UTF-8'
    options.compilerArgs += ['--release', '21', '-Xmaxerrs', '100000', '-proc:none', '-nowarn']
}}
def unrename = tasks.register('unrenameKeywords', Exec) {{
    commandLine 'java', "$rootDir/tools/Unrename.java", "${{layout.buildDirectory.get()}}/classes/java/main"
}}
tasks.named('compileJava') {{ finalizedBy unrename }}
import org.gradle.api.attributes.java.TargetJvmVersion
configurations.matching {{ it.name.toLowerCase().endsWith('compileclasspath') }}.configureEach {{
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}}
'''
def attribute_instance_refs(mod, mc):
    """All abstract methods of the Paper API's AttributeInstance, force-added to CraftAttributeInstance (Paper's patched server implements them)."""
    pa = sorted(glob.glob(os.path.join(GH, f'caches/modules-2/files-2.1/io.papermc.paper/paper-api/{mc}-R0.1-SNAPSHOT/*/paper-api-{mc}-R0.1-SNAPSHOT.jar')))
    if not pa: return ''
    txt = subprocess.run(['javap', '-p', '-s', '-cp', pa[0], 'org.bukkit.attribute.AttributeInstance'], capture_output=True, text=True).stdout.split('\n')
    out = []
    for i, l in enumerate(txt):
        l = l.strip()
        if l.endswith(');') and 'abstract' in l and i + 1 < len(txt) and txt[i + 1].strip().startswith('descriptor:'):
            name = l.split('(')[0].split()[-1]
            desc = txt[i + 1].split('descriptor:')[1].strip()
            out.append(f'X\torg/bukkit/craftbukkit/{mod}/attribute/CraftAttributeInstance\t{name}\t{desc}\n')
    return ''.join(out)

os.makedirs('shim', exist_ok=True)

def extra_api(mc, sv):
    """Spigot-API classes that are NOT in the matching Paper API (types only the server jar refers to)."""
    out = f'shim/{mc}-extra-api.jar'
    sa = os.path.expanduser(f'~/.m2/repository/org/spigotmc/spigot-api/{sv}/spigot-api-{sv}.jar')
    pa = sorted(glob.glob(os.path.join(GH, f'caches/modules-2/files-2.1/io.papermc.paper/paper-api/{mc}-R0.1-SNAPSHOT/*/paper-api-{mc}-R0.1-SNAPSHOT.jar')))
    have = set(zipfile.ZipFile(pa[0]).namelist()) if pa else set()
    n = 0
    with zipfile.ZipFile(sa) as zi, zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as zo:
        for it in zi.infolist():
            if it.filename.endswith('.class') and it.filename not in have:
                zo.writestr(it.filename, zi.read(it.filename)); n += 1
    print(f'  extra-api {mc}: {n} classes not in Paper API (paper-api jar found: {bool(pa)})')
    return out
only = sys.argv[1:] or list(MODS)
for mod in only:
    mc = MODS[mod]
    sv = f'{mc}-R0.2-SNAPSHOT' if os.path.isdir(f'{M2}/{mc}-R0.2-SNAPSHOT') else f'{mc}-R0.1-SNAPSHOT'
    refs = os.path.join(os.environ.get('TMPDIR','D:/Tools/tmp'), f'refs_{mod}.txt')
    subprocess.run([sys.executable, 'tools/extract_refs.py', ORIG, mod, refs], check=True)
    # members the Paper-patched server provides but no module code references directly (e.g. inherited abstract methods)
    with open(refs, 'a', encoding='utf-8') as rf, open('tools/extra_refs.txt', encoding='utf-8') as ef:
        rf.write(ef.read().replace('{MOD}', mod))
        rf.write(attribute_instance_refs(mod, mc))
    jar = f'{M2}/{sv}/spigot-{sv}.jar'
    subprocess.run(['java', 'tools/Shim.java', jar, f'shim/{mc}.jar', refs], check=True)
    extra_api(mc, sv)
    open(f'{mod}/build.gradle', 'w').write(GRADLE.format(mc=mc, sv=sv, spigot_api='"$rootDir/shim/' + mc + '-extra-api.jar"'))
print('done')
