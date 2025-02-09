package onelone.onelmod.client.config.builder

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import onelone.onelmod.client.OneLClient
import onelone.onelmod.client.features.Feature
import java.awt.Color
import java.io.File
import java.io.FileNotFoundException
import java.nio.charset.Charset
import kotlin.concurrent.thread
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

open class AutoConfig<T : Any>(
    private val clazz: KClass<T>,
    private val onChange: (setting: String, value: Any) -> Unit,
) {
    private var namespace = ""
    private var categoryOrder: Array<String> = arrayOf()

    private val properties = clazz.memberProperties
        .filterIsInstance<KMutableProperty<*>>()
        .map { v -> Pair(v, v.findAnnotation<ConfigField>()) }
        .filter { (_, ann) -> ann != null }

    private val groupAnnotations = clazz.annotations
        .filterIsInstance<ConfigGroup>()

    private fun getConfigFile(): File {
        return File(FabricLoader.getInstance().configDir.toFile(), "$namespace.json")
    }

    private fun save() {
        val gson = JsonObject()

        for ((prop, _) in properties) {
            when (val value = prop.getter.call(clazz.objectInstance)) {
                is Boolean -> gson.addProperty(prop.name, value)
                is String -> gson.addProperty(prop.name, value)
                is Color -> gson.addProperty(prop.name, value.rgb)
            }
        }

        val out = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(gson)

        getConfigFile().writeText(out)
    }

    private fun loadConfig() {
        try {
            val source = getConfigFile().readText(Charset.forName("utf-8"))
            val gson = Gson().fromJson(source, JsonElement::class.java)

            if (!gson.isJsonObject) return
            val gsonObject = gson.asJsonObject

            for ((prop, _) in properties) {
                if (!gsonObject.has(prop.name)) continue
                val configValue = gsonObject.get(prop.name)

                try {
                    when (prop.returnType.classifier) {
                        Boolean::class -> prop.setter.call(clazz.objectInstance, configValue.asBoolean)
                        String::class -> prop.setter.call(clazz.objectInstance, configValue.asString)
                        Color::class -> prop.setter.call(clazz.objectInstance, Color.getColor("imported", configValue.asInt))
                    }
                } catch (_: UnsupportedOperationException) {}
                  catch (_: IllegalStateException) {}
            }
        } catch (_: FileNotFoundException) {}
    }

    fun load() {
        val namespace = clazz.findAnnotation<ConfigProvider>()
            ?: throw Exception("config must be annotated with @ConfigProvider")

        this.namespace = namespace.namespace
        this.categoryOrder = namespace.categoryOrder

        thread {
            // game lags on first gui open without this 🤔
            generate(null)
        }

        loadConfig()

        for ((prop, _) in properties) {
            val ann = prop.findAnnotation<ConfigFeature<Feature>>() ?: continue
            val value = prop.getter.call(clazz.objectInstance)
            if (value is Boolean) ann.feature.objectInstance!!.setState(value)
        }
    }

    fun generate(parent: Screen?): Screen {
        val categories: MutableMap<String, MutableMap<String, MutableList<Option<Any>>>> = mutableMapOf()
        val language = Language.getInstance()
        val defaultGroup = "\$default"

        val builder = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("$namespace.config.title"))

        for (pair in properties) {
            val (prop, ann) = pair

            if (ann == null) continue
            if (ann.category !in categories) categories[ann.category] = mutableMapOf()

            val desc = OptionDescription.createBuilder()
            val descTranslation = "$namespace.config.option.${prop.name}.description"

            if (language.hasTranslation(descTranslation)) desc.text(Text.translatable(descTranslation))

            if (ann.image.isNotEmpty()) desc.image(
                Identifier.of(namespace, ann.image),
                ann.imageWidth,
                ann.imageHeight
            )
            if (ann.webpImage.isNotEmpty()) desc.webpImage(Identifier.of(namespace, ann.webpImage))
            if (ann.gifImage.isNotEmpty()) desc.gifImage(Identifier.of(namespace, ann.gifImage))

            val option = Option.createBuilder<Any>()
                .name(Text.translatable("$namespace.config.option.${prop.name}"))
                .binding(Binding.generic(
                    prop.getter.call(clazz.objectInstance),
                    { prop.getter.call(clazz.objectInstance) },
                    { v ->
                        prop.setter.call(clazz.objectInstance, v)
                        onChange(prop.name, v)

                        if (v !is Boolean) return@generic
                        val callback = prop.findAnnotation<ConfigFeature<Feature>>() ?: return@generic
                        callback.feature.objectInstance!!.setState(v)
                    }
                ))
                .description(desc.build())
                .controller(
                    when (prop.returnType.classifier) {
                        Boolean::class -> TickBoxControllerBuilder::create as (Option<Any>) -> ControllerBuilder<Any>
                        String::class -> StringControllerBuilder::create as (Option<Any>) -> ControllerBuilder<Any>
                        Color::class -> ColorControllerBuilder::create as (Option<Any>) -> ControllerBuilder<Any>
                        else -> throw Exception("couldn't infer controller")
                    }
                )
                .build()

            val group = ann.group.ifEmpty { defaultGroup }

            if (group !in categories[ann.category]!!) categories[ann.category]!![group] = mutableListOf()
            categories[ann.category]!![group]!!.add(option)
        }

        val categoryNames = mutableSetOf(*categoryOrder)
        categoryNames.addAll(categories.keys)

        for (category in categoryNames) {
            val groups = categories[category]!!
            val categoryBuilder = ConfigCategory.createBuilder()
                .name(Text.translatable("$namespace.config.category.$category"))

            val tooltipTranslation = "$namespace.config.category.$category.tooltip"
            if (language.hasTranslation(tooltipTranslation)) categoryBuilder.tooltip(
                Text.translatable(
                    tooltipTranslation
                )
            )

            for ((group, options) in groups) {
                if (group == defaultGroup) categoryBuilder.options(options)
                else {
                    val ann = groupAnnotations.find { v -> v.name == group }
                    val groupBuilder = OptionGroup.createBuilder()
                    val desc = OptionDescription.createBuilder()

                    val nameTranslation = "$namespace.config.group.$group"
                    if (language.hasTranslation(nameTranslation)) groupBuilder.name(Text.translatable(nameTranslation))

                    val descTranslation = "$namespace.config.group.$group.description"
                    if (language.hasTranslation(descTranslation)) desc.text(Text.translatable(descTranslation))

                    if (ann != null) {
                        if (ann.image.isNotEmpty()) desc.image(
                            Identifier.of(namespace, ann.image),
                            ann.imageWidth,
                            ann.imageHeight
                        )

                        if (ann.webpImage.isNotEmpty()) desc.webpImage(Identifier.of(namespace, ann.webpImage))
                        if (ann.gifImage.isNotEmpty()) desc.gifImage(Identifier.of(namespace, ann.gifImage))
                    }

                    categoryBuilder.group(groupBuilder.description(desc.build())
                            .options(options)
                            .build()
                    )
                }
            }

            builder.category(categoryBuilder.build())
        }

        return builder
            .save(this::save)
            .build()
            .generateScreen(parent)
    }
}