package annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Serialized(val name: String = "", val exclude: Boolean = false)
