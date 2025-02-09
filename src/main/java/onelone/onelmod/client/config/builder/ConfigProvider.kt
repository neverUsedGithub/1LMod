package onelone.onelmod.client.config.builder

@Target(AnnotationTarget.CLASS)
annotation class ConfigProvider(val namespace: String, val categoryOrder: Array<String>)