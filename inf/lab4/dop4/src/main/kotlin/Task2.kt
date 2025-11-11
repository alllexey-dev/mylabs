import element.ElementUtils
import impl.RonSerializer
import model.Schedule
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

val SCHEDULE_RON_PATH = Path("data/schedule.ron")

fun main() {
    val schedule = readScheduleIni(SCHEDULE_INI_PATH)
    writeScheduleRon(SCHEDULE_RON_PATH, schedule)
}

fun writeScheduleRon(path: Path, schedule: Schedule) {
    val element = ElementUtils.toElement(schedule)!!
    val ronSerializer = RonSerializer()
    val outputRon = ronSerializer.serialize(element)
    Files.writeString(path, outputRon)
}