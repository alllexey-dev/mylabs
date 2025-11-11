import element.ElementUtils
import impl.IniDeserializer
import model.Schedule
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

val SCHEDULE_INI_PATH = Path("data/schedule.ini")

// 502587 % 132 = 63
// INI -> RON
// wednesday, saturday
fun main() {
    val schedule = readScheduleIni(SCHEDULE_INI_PATH)
    println(schedule)
}

fun readScheduleIni(path: Path): Schedule {
    val inputIni = Files.readString(path)
    val iniDeserializer = IniDeserializer()
    val element = iniDeserializer.deserialize(inputIni)
    val schedule = ElementUtils.toObject(element, Schedule::class.java)
    return schedule
}