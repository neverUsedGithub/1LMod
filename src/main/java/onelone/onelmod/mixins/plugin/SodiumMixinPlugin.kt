package onelone.onelmod.mixins.plugin;

import net.fabricmc.loader.api.FabricLoader
import onelone.onelmod.client.OneLClient
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class SodiumMixinPlugin : IMixinConfigPlugin {
    private var hasSodium = false

    override fun onLoad(mixinPackage: String?) {
        hasSodium = FabricLoader.getInstance().isModLoaded("sodium")
    }

    override fun getRefMapperConfig(): String? { return null }
    override fun shouldApplyMixin(targetClass: String?, mixinClass: String?): Boolean { return hasSodium }
    override fun acceptTargets(p0: MutableSet<String>?, p1: MutableSet<String>?) {}
    override fun getMixins(): MutableList<String>? { return null }
    override fun preApply(p0: String?, p1: ClassNode?, p2: String?, p3: IMixinInfo?) {}
    override fun postApply(p0: String?, p1: ClassNode?, p2: String?, p3: IMixinInfo?) {}
}
