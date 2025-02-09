package onelone.onelmod.client.config.builder

@Target(AnnotationTarget.PROPERTY)
annotation class ConfigField(
    val category: String,
    val group: String = "",

    val image: String = "",
    val gifImage: String = "",
    val webpImage: String = "",

    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
)