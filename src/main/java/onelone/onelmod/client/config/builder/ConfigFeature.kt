package onelone.onelmod.client.config.builder

import onelone.onelmod.client.features.Feature
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class ConfigFeature<T : Feature>(
    val feature: KClass<T>
)
