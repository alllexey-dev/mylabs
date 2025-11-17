import element.ElementUtils
import impl.XmlSerializer
import model.Schedule
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path

val SCHEDULE_XML_PATH = Path("data/schedule.xml")

fun main() {
    val schedule = readScheduleIni(SCHEDULE_INI_PATH)
    writeScheduleXml(SCHEDULE_XML_PATH, schedule)
}

fun writeScheduleXml(path: Path, schedule: Schedule) {
    val element = ElementUtils.toElement(schedule)!!
    val xmlSerializer = XmlSerializer()
    val outputRon = xmlSerializer.serialize(element)
    Files.writeString(path, outputRon)
}