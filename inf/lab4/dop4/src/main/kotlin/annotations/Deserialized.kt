package annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Deserialized(val name: String, val exclude: Boolean = false)
