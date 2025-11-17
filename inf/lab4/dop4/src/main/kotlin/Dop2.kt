import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import element.ElementUtils
import impl.JsonSerializer
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val schedule = readScheduleIni(SCHEDULE_INI_PATH)

    val myJson = JsonSerializer()
    val gson = GsonBuilder().setPrettyPrinting().create()
    val jackson = ObjectMapper().writerWithDefaultPrettyPrinter()

    Files.writeString(Path.of("data/schedule_my.json"), myJson.serialize(ElementUtils.toElement(schedule)!!))
    Files.writeString(Path.of("data/schedule_gson.json"), gson.toJson(schedule))
    Files.writeString(Path.of("data/schedule_jackson.json"), jackson.writeValueAsString(schedule))
}