import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import element.ElementUtils
import impl.IniDeserializer
import impl.JsonSerializer
import model.Schedule
import org.ini4j.Wini
import java.io.StringReader
import java.nio.file.Files

fun main() {
    Thread.sleep(1000) // warmup
    val task4 = Task4()
    task4.benchmarkDeserialization()
    println()
    task4.benchmarkSerialization()
}

class Task4 {

    val jsonSerializer = JsonSerializer(prettyPrinting = false)
    val gson = Gson()
    val objectMapper = ObjectMapper()

    val iniDeserializer = IniDeserializer()
    val loops = 10000

    fun benchmarkSerialization() {
        val schedule = readScheduleIni(SCHEDULE_INI_PATH)

        val customResults = mutableListOf<String>()
        val gsonResults = mutableListOf<String>()
        val jacksonResults = mutableListOf<String>()

        val customStart = System.currentTimeMillis()
        for (i in 0 until loops) {
            customResults.add(customToJson(schedule))
        }
        val customEnd = System.currentTimeMillis()
        for (i in 0 until loops) {
            gsonResults.add(gsonToJson(schedule))
        }
        val gsonEnd = System.currentTimeMillis()
        for (i in 0 until loops) {
            jacksonResults.add(jacksonToJson(schedule))
        }

        val jacksonEnd = System.currentTimeMillis()
        println("=== JSON (RON) SERIALIZATION RESULTS ===")
        val customDelta = customEnd - customStart
        println("custom: $customDelta ms (${customDelta / loops.toDouble()} ms/loop)")
        val gsonDelta = gsonEnd - customEnd
        println("gson: $gsonDelta ms (${gsonDelta / loops.toDouble()} ms/loop)")
        val jacksonDelta = jacksonEnd - gsonEnd
        println("jackson: $jacksonDelta ms (${jacksonDelta / loops.toDouble()} ms/loop)")
    }

    fun benchmarkDeserialization() {
        val iniString = Files.readString(SCHEDULE_INI_PATH)

        val customStart = System.currentTimeMillis()
        for (i in 0 until loops) {
            iniCustomTest(iniString)
        }
        val customEnd = System.currentTimeMillis()
        for (i in 0 until loops) {
            iniIni4jTest(iniString)
        }
        val ini4jEnd = System.currentTimeMillis()
        println("=== INI DESERIALIZATION RESULTS ===")
        val customDelta = customEnd - customStart
        println("custom: $customDelta ms (${customDelta / loops.toDouble()} ms/loop)")
        val ini4jDelta = ini4jEnd - customEnd
        println("ini4j: $ini4jDelta ms (${ini4jDelta / loops.toDouble()} ms/loop)")
    }

    fun customToJson(schedule: Schedule): String {
        val element = ElementUtils.toElement(schedule)!!
        return jsonSerializer.serialize(element)
    }

    fun gsonToJson(schedule: Schedule): String {
        return gson.toJson(schedule)
    }

    fun jacksonToJson(schedule: Schedule): String {
        return objectMapper.writeValueAsString(schedule)
    }

    fun iniIni4jTest(iniString: String): Wini {
        val ini4jInstance = Wini()
        ini4jInstance.load(StringReader(iniString))
        return ini4jInstance
    }

    fun iniCustomTest(iniString: String): Schedule {
        return ElementUtils.toObject(iniDeserializer.deserialize(iniString), Schedule::class.java)
    }
}