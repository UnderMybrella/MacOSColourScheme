package org.abimon.macOSColourScheme

import javafx.scene.Scene
import java.awt.Color
import java.io.File
import java.io.InputStream
import java.text.DecimalFormat
import java.util.*

private val decimalFormat = DecimalFormat("#.##")

fun Color.toRGBAString(): String = "rgba($red, $green, $blue, ${decimalFormat.format(alpha.toFloat() / 255)})"

fun MacOSColourScheme.adjustSlider(): MacOSColourScheme {
    if (isDarkTheme) {
        knobColor = Color("6b6b6b".toInt(16))
        selectedKnobColor = Color("959595".toInt(16))
        scrollBarColor = Color("2b2b2b".toInt(16))
    }

    return this
}

fun Scene.applyScheme(scheme: MacOSColourScheme) {
    val resource =
        MacOSColourScheme::class.java.classLoader.getResourceAsStream("theme.css")?.use(InputStream::readBytes)
            ?.let { bytes -> String(bytes) } ?: return

    val tmp = File.createTempFile(UUID.randomUUID().toString(), ".css")
    tmp.writeText(
        resource
            .replace("%labelColor", scheme.labelColor!!.toRGBAString())
            .replace("%secondaryLabelColor", scheme.secondaryLabelColor!!.toRGBAString())
            .replace("%tertiaryLabelColor", scheme.tertiaryLabelColor!!.toRGBAString())
            .replace("%quaternaryLabelColor", scheme.quaternaryLabelColor!!.toRGBAString())

            .replace("%textColor", scheme.textColor!!.toRGBAString())

            .replace("%separatorColor", scheme.separatorColor!!.toRGBAString())

            .replace("%selectedContentBackgroundColor", scheme.selectedContentBackgroundColor!!.toRGBAString())

            .replace("%selectedMenuItemTextColor", scheme.selectedMenuItemTextColor!!.toRGBAString())

            .replace(
                "%alternatingContentBackgroundColor",
                scheme.alternatingContentBackgroundColors.first().toRGBAString()
            )

            .replace("%controlAccentColor", scheme.controlAccentColor!!.toRGBAString())
            .replace("%controlColor", scheme.controlColor!!.toRGBAString())
            .replace("%controlBackgroundColor", scheme.controlBackgroundColor!!.toRGBAString())
            .replace("%controlTextColor", scheme.controlTextColor!!.toRGBAString())
            .replace("%selectedControlColor", scheme.selectedControlColor!!.toRGBAString())

            .replace("%windowBackgroundColor", scheme.windowBackgroundColor!!.toRGBAString())
            .replace("%windowFrameTextColor", scheme.windowFrameTextColor!!.toRGBAString())
            .replace("%underPageBackgroundColor", scheme.underPageBackgroundColor!!.toRGBAString())

            .replace("%knobColor", scheme.knobColor!!.toRGBAString())
            .replace("%selectedKnobColor", scheme.selectedKnobColor!!.toRGBAString())
            .replace("%scrollBarColor", scheme.scrollBarColor!!.toRGBAString())

            .replace("%systemRed", scheme.systemRed!!.toRGBAString())
            .replace("%systemGreen", scheme.systemGreen!!.toRGBAString())
            .replace("%systemBlue", scheme.systemBlue!!.toRGBAString())
            .replace("%systemOrange", scheme.systemOrange!!.toRGBAString())
            .replace("%systemYellow", scheme.systemYellow!!.toRGBAString())
            .replace("%systemBrown", scheme.systemBrown!!.toRGBAString())
            .replace("%systemPink", scheme.systemPink!!.toRGBAString())
            .replace("%systemPurple", scheme.systemPurple!!.toRGBAString())
            .replace("%systemGray", scheme.systemGray!!.toRGBAString())
    )

    stylesheets.add(tmp.toURI().toURL().toExternalForm())
}