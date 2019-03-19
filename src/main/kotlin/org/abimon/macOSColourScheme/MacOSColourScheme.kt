package org.abimon.macOSColourScheme

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

class MacOSColourScheme {
    companion object {
        val LIGHT: MacOSColourScheme by lazy {
            MacOSColourScheme::class.java.classLoader.getResourceAsStream("light.json")
                .use { stream -> MAPPER.readValue(stream, MacOSColourScheme::class.java) }
        }

        val DARK: MacOSColourScheme by lazy {
            MacOSColourScheme::class.java.classLoader.getResourceAsStream("dark.json")
                .use { stream -> MAPPER.readValue(stream, MacOSColourScheme::class.java) }
        }

        val rgbaSerialiser: JsonSerializer<Color> = object : JsonSerializer<Color>() {
            override fun serialize(value: Color?, gen: JsonGenerator, serializers: SerializerProvider?) {
                if (value == null) {
                    gen.writeString("nil")
                } else {
                    gen.writeString("rgba(${value.red},${value.green},${value.blue},${value.alpha})")
                }
            }

            override fun handledType(): Class<Color> = Color::class.java
        }
        val rgbaDeserialiser: JsonDeserializer<Color> = object : JsonDeserializer<Color>() {
            override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Color? {
                if (p.valueAsString == "nil")
                    return null

                if (!p.valueAsString.startsWith("rgba"))
                    return null

                val rgba = p.valueAsString.substringAfter('(').substringBefore(')').split(',').map(String::trim)
                    .mapNotNull(String::toIntOrNull)
                if (rgba.size != 4)
                    return null

                return Color(rgba[0], rgba[1], rgba[2], rgba[3])
            }
        }
        val rgbaModule = SimpleModule(
            "rgba() deserialiser",
            Version.unknownVersion(),
            mapOf(Color::class.java to rgbaDeserialiser),
            listOf(rgbaSerialiser)
        )

        val MAPPER = ObjectMapper()
            .registerModule(rgbaModule)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        fun loadFromSystem(): MacOSColourScheme? {
            val os = System.getProperty("os.name").toLowerCase()
            if ("mac" !in os)
                return null

            val osVersion = System.getProperty("os.version")
            val major = osVersion.substringBefore('.').toIntOrNull() ?: 0
            val minor = osVersion.substringAfter('.').toIntOrNull() ?: 0

            if (major < 10 || minor < 14)
                return null

            val file = File.createTempFile(UUID.randomUUID().toString(), "")

            try {
                file.deleteOnExit()
                file.setExecutable(true)

                FileOutputStream(file).use { out ->
                    MacOSColourScheme::class.java.classLoader.getResourceAsStream("ColourGetter")
                        .use { stream -> stream.copyTo(out) }
                }

                val process = ProcessBuilder(listOf(file.absolutePath)).start()
                process.waitFor(5, TimeUnit.SECONDS)

                val scheme = MAPPER.readValue(process.inputStream, MacOSColourScheme::class.java)
                if (scheme._isDarkTheme == null) {
                    val styleProcess = ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle").start()
                    styleProcess.waitFor(1, TimeUnit.SECONDS)
                    val name = String(styleProcess.inputStream.use(InputStream::readBytes)).toLowerCase().trim()
                    scheme._isDarkTheme = name.equals("dark", true)
                }

                return scheme
            } catch (json: JsonMappingException) {
                return null
            } finally {
                file.delete()
            }
        }
    }

    var _isDarkTheme: Boolean? = null
    var isDarkTheme: Boolean
        get() = _isDarkTheme ?: false
        set(value) {
            _isDarkTheme = value
        }

    //Label Colors


    /**
     * The primary color to use for text labels
     */
    var labelColor: Color? = null

    /**
     * The secondary color to use for text labels
     */
    var secondaryLabelColor: Color? = null

    /**
     * The tertiary color to use for text labels
     */
    var tertiaryLabelColor: Color? = null

    /**
     * The quaternary color to use for text labels
     */
    var quaternaryLabelColor: Color? = null


    //Text Colors

    /**
     * The color to use for text
     */
    var textColor: Color? = null

    /**
     * The color to use for placeholder text in controls or text views
     */
    var placeholderTextColor: Color? = null

    /**
     * The color to use for selected text
     */
    var selectedTextColor: Color? = null

    /**
     * The color to use for the background area behind text
     */
    var textBackgroundColor: Color? = null

    /**
     * The color to use for the background of selected text
     */
    var selectedTextBackgroundColor: Color? = null

    /**
     * The color to use for the keyboard focus ring around controls
     */
    var keyboardFocusIndicatorColor: Color? = null

    /**
     * The color to use for selected text in an unemphasized context
     */
    var unemphasizedSelectedTextColor: Color? = null

    /**
     * The color to use for the text background in an unemphasized context
     */
    var unemphasizedSelectedTextBackgroundColor: Color? = null


    //Content Colors


    /**
     * The color to use for links
     */
    var linkColor: Color? = null

    /**
     * The color to use for separators between different sections of content
     */
    var separatorColor: Color? = null

    /**
     * The color to use for the background of selected and emphasized content
     */
    var selectedContentBackgroundColor: Color? = null

    /**
     * The colour to use for selected and unemphasized content
     */
    var unemphasizedContentBackgroundColor: Color? = null


    //Menu Colors


    /**
     * The color to use for the text in menu items
     */
    var selectedMenuItemTextColor: Color? = null


    //Table Colors


    /**
     * The color to use for the optional gridlines, such as those in a table view
     */
    var gridColor: Color? = null

    /**
     * The color to use for text in header cells in table views and outline views
     */
    var headerTextColor: Color? = null

    /**
     * The colors to use for alternating content, typically found in table views and collection views
     */
    var alternatingContentBackgroundColors: Array<Color> = emptyArray()


    //Control Colors


    /**
     * The user's current accent color preference
     */
    var controlAccentColor: Color? = null

    /**
     * The color to use for the flat surfaces of a control
     */
    var controlColor: Color? = null

    /**
     * The color to use for the background of large controls, such as scroll views or table views.
     */
    var controlBackgroundColor: Color? = null

    /**
     * The color to use for text on enabled controls
     */
    var controlTextColor: Color? = null

    /**
     * The color to use for text on disabled controls
     */
    var disabledControlTextColor: Color? = null

    /**
     * The current system control tint color
     */
    var currentControlTint: Int? = null

    /**
     * The color to use for the face of a selected control - that is, a control that has been clicked or is being dragged
     */
    var selectedControlColor: Color? = null

    /**
     * The color to use for text in a selected control - that is, a control being clicked or dragged
     */
    var selectedControlTextColor: Color? = null

    /**
     * The color to use for text in a selected control
     */
    var alternateSelectedControlTextColor: Color? = null

    /**
     * The patterned color to use for the background of a scrubber control
     */
    var scrubberTexturedBackground: Color? = null


    //Window Colors


    /**
     * The color to use for the window background
     */
    var windowBackgroundColor: Color? = null

    /**
     * The color to use for text in a window's frame
     */
    var windowFrameTextColor: Color? = null

    /**
     * The color to use in the area beneath your window's views
     */
    var underPageBackgroundColor: Color? = null


    //Highlights and Shadows


    /**
     * The highlight color to use for the bubble that shows inline search result values
     */
    var findHighlightColor: Color? = null

    /**
     * The color to use as a virtual light source on the screen
     */
    var highlightColor: Color? = null

    /**
     * The color to use for virtual shadows cast by raised objects on the screen
     */
    var shadowColor: Color? = null


    //Deprecated Colors
    //TODO: Find an alternative

    /**
     * The system color used for the flat surface of a slider knob that hasn't been selected
     */
    var knobColor: Color? = null

    /**
     * The system color used for the slider knob when it is selected
     */
    var selectedKnobColor: Color? = null

    /**
     * The system color used for scroll "bars" - that is, for the groove in which a scroller's knob moves
     */
    var scrollBarColor: Color? = null


    //System Colors


    /**
     * Returns a color object for red that automatically adapts to vibrancy and accessibility settings.
     */
    var systemRed: Color? = null

    /**
     * Returns a color object for green that automatically adapts to vibrancy and accessibility settings.
     */
    var systemGreen: Color? = null

    /**
     * Returns a color object for blue that automatically adapts to vibrancy and accessibility settings.
     */
    var systemBlue: Color? = null

    /**
     * Returns a color object for orange that automatically adapts to vibrancy and accessibility settings.
     */
    var systemOrange: Color? = null

    /**
     * Returns a color object for yellow that automatically adapts to vibrancy and accessibility settings.
     */
    var systemYellow: Color? = null

    /**
     * Returns a color object for brown that automatically adapts to vibrancy and accessibility settings.
     */
    var systemBrown: Color? = null

    /**
     * Returns a color object for pink that automatically adapts to vibrancy and accessibility settings.
     */
    var systemPink: Color? = null

    /**
     * Returns a color object for purple that automatically adapts to vibrancy and accessibility settings.
     */
    var systemPurple: Color? = null

    /**
     * Returns a color object for gray that automatically adapts to vibrancy and accessibility settings.
     */
    var systemGray: Color? = null
}