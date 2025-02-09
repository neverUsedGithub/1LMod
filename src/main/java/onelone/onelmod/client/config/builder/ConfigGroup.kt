package onelone.onelmod.client.config.builder

@Repeatable
@Target(AnnotationTarget.CLASS)
annotation class ConfigGroup(
    val name: String,

    val image: String = "",
    val gifImage: String = "",
    val webpImage: String = "",

    val imageWidth: Int = 0,
    val imageHeight: Int = 0
)
