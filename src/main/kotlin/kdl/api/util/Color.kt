@file:Suppress("SpellCheckingInspection", "ReplaceJavaStaticMethodWithKotlinAnalog")

package kdl.api.util

import java.beans.ConstructorProperties
import java.io.Serializable

fun colorOf(rgb: Int): Color = Color(rgb)

fun colorOf(rgb: Int, alpha: Float): Color = Color(
    rgb ushr 16 and 0xFF,
    rgb ushr 8 and 0xFF,
    rgb and 0xFF,
    (alpha * 255).toInt()
)

val Color.r: Int get() = red
val Color.g: Int get() = green
val Color.b: Int get() = blue
val Color.a: Int get() = alpha

val Color.rf: Float get() = red / 255f
val Color.gf: Float get() = green / 255f
val Color.bf: Float get() = blue / 255f
val Color.af: Float get() = alpha / 255f

/**
 * Copy of java.awt.Color
 *
 * This is needed as the package java.awt has different behaviour in different systems
 * The fabric wiki says:
 *
 * "Avoid using the java.awt package and its subpackages. AWT does not work well on all systems.
 *  Several users have reported that it tends to hang Minecraft."
 */
open class Color : Serializable {
    /**
     * Returns the RGB value representing the color in the default sRGB
     * [ColorModel].
     * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
     * blue).
     * @return the RGB value of the color in the default sRGB
     * `ColorModel`.
     * @see java.awt.image.ColorModel.getRGBdefault
     *
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @since JDK1.0
     */
    /**
     * The color value.
     * @serial
     */
    open var rgb = 0

    // Same as rgb, note both store alpha
    val rgba: Int get() = rgb

    /**
     * The color value in the default sRGB `ColorSpace` as
     * `float` components (no alpha).
     * If `null` after object construction, this must be an
     * sRGB color constructed with 8-bit precision, so compute from the
     * `int` color value.
     * @serial
     * @see .getRGBColorComponents
     *
     * @see .getRGBComponents
     */
    private var frgbvalue: FloatArray? = null

    /**
     * The color value in the native `ColorSpace` as
     * `float` components (no alpha).
     * If `null` after object construction, this must be an
     * sRGB color constructed with 8-bit precision, so compute from the
     * `int` color value.
     * @serial
     * @see .getRGBColorComponents
     *
     * @see .getRGBComponents
     */
    private var fvalue: FloatArray? = null

    /**
     * The alpha value as a `float` component.
     * If `frgbvalue` is `null`, this is not valid
     * data, so compute from the `int` color value.
     * @serial
     * @see .getRGBComponents
     *
     * @see .getComponents
     */
    private var falpha = 0.0f

    companion object {
        /**
         * The color white.  In the default sRGB space.
         */
        val white = Color(255, 255, 255)

        /**
         * The color white.  In the default sRGB space.
         * @since 1.4
         */
        val WHITE: Color = white

        /**
         * The color light gray.  In the default sRGB space.
         */
        val lightGray = Color(192, 192, 192)

        /**
         * The color light gray.  In the default sRGB space.
         * @since 1.4
         */
        val LIGHT_GRAY: Color = lightGray

        /**
         * The color gray.  In the default sRGB space.
         */
        val gray = Color(128, 128, 128)

        /**
         * The color gray.  In the default sRGB space.
         * @since 1.4
         */
        val GRAY: Color = gray

        /**
         * The color dark gray.  In the default sRGB space.
         */
        val darkGray = Color(64, 64, 64)

        /**
         * The color dark gray.  In the default sRGB space.
         * @since 1.4
         */
        val DARK_GRAY: Color = darkGray

        /**
         * The color black.  In the default sRGB space.
         */
        val black = Color(0, 0, 0)

        /**
         * The color black.  In the default sRGB space.
         * @since 1.4
         */
        val BLACK: Color = black

        /**
         * The color red.  In the default sRGB space.
         */
        val red = Color(255, 0, 0)

        /**
         * The color red.  In the default sRGB space.
         * @since 1.4
         */
        val RED: Color = red

        /**
         * The color pink.  In the default sRGB space.
         */
        val pink = Color(255, 175, 175)

        /**
         * The color pink.  In the default sRGB space.
         * @since 1.4
         */
        val PINK: Color = pink

        /**
         * The color orange.  In the default sRGB space.
         */
        val orange = Color(255, 200, 0)

        /**
         * The color orange.  In the default sRGB space.
         * @since 1.4
         */
        val ORANGE: Color = orange

        /**
         * The color yellow.  In the default sRGB space.
         */
        val yellow = Color(255, 255, 0)

        /**
         * The color yellow.  In the default sRGB space.
         * @since 1.4
         */
        val YELLOW: Color = yellow

        /**
         * The color green.  In the default sRGB space.
         */
        val green = Color(0, 255, 0)

        /**
         * The color green.  In the default sRGB space.
         * @since 1.4
         */
        val GREEN: Color = green

        /**
         * The color magenta.  In the default sRGB space.
         */
        val magenta = Color(255, 0, 255)

        /**
         * The color magenta.  In the default sRGB space.
         * @since 1.4
         */
        val MAGENTA: Color = magenta

        /**
         * The color cyan.  In the default sRGB space.
         */
        val cyan = Color(0, 255, 255)

        /**
         * The color cyan.  In the default sRGB space.
         * @since 1.4
         */
        val CYAN: Color = cyan

        /**
         * The color blue.  In the default sRGB space.
         */
        val blue = Color(0, 0, 255)

        /**
         * The color blue.  In the default sRGB space.
         * @since 1.4
         */
        val BLUE: Color = blue

        /*
         * JDK 1.1 serialVersionUID
         */
        private const val serialVersionUID = 118526816881161077L

        /**
         * Checks the color integer components supplied for validity.
         * Throws an [IllegalArgumentException] if the value is out of
         * range.
         * @param r the Red component
         * @param g the Green component
         * @param b the Blue component
         */
        private fun testColorValueRange(r: Int, g: Int, b: Int, a: Int) {
            var rangeError = false
            var badComponentString = ""
            if (a < 0 || a > 255) {
                rangeError = true
                badComponentString = "$badComponentString Alpha"
            }
            if (r < 0 || r > 255) {
                rangeError = true
                badComponentString = "$badComponentString Red"
            }
            if (g < 0 || g > 255) {
                rangeError = true
                badComponentString = "$badComponentString Green"
            }
            if (b < 0 || b > 255) {
                rangeError = true
                badComponentString = "$badComponentString Blue"
            }
            require(rangeError != true) {
                ("Color parameter outside of expected range:"
                        + badComponentString)
            }
        }

        /**
         * Checks the color `float` components supplied for
         * validity.
         * Throws an `IllegalArgumentException` if the value is out
         * of range.
         * @param r the Red component
         * @param g the Green component
         * @param b the Blue component
         */
        private fun testColorValueRange(
            r: Float,
            g: Float,
            b: Float,
            a: Float
        ) {
            var rangeError = false
            var badComponentString = ""
            if (a < 0.0 || a > 1.0) {
                rangeError = true
                badComponentString = "$badComponentString Alpha"
            }
            if (r < 0.0 || r > 1.0) {
                rangeError = true
                badComponentString = "$badComponentString Red"
            }
            if (g < 0.0 || g > 1.0) {
                rangeError = true
                badComponentString = "$badComponentString Green"
            }
            if (b < 0.0 || b > 1.0) {
                rangeError = true
                badComponentString = "$badComponentString Blue"
            }
            require(rangeError != true) {
                ("Color parameter outside of expected range:"
                        + badComponentString)
            }
        }

        private const val FACTOR = 0.7

        /**
         * Converts a `String` to an integer and returns the
         * specified opaque `Color`. This method handles string
         * formats that are used to represent octal and hexadecimal numbers.
         * @param      nm a `String` that represents
         * an opaque color as a 24-bit integer
         * @return     the new `Color` object.
         * @see java.lang.Integer.decode
         *
         * @exception  NumberFormatException  if the specified string cannot
         * be interpreted as a decimal,
         * octal, or hexadecimal integer.
         * @since      JDK1.1
         */
        @Throws(NumberFormatException::class)
        fun decode(nm: String?): Color {
            val intval = Integer.decode(nm)
            val i = intval.toInt()
            return Color(i shr 16 and 0xFF, i shr 8 and 0xFF, i and 0xFF)
        }

        /**
         * Finds a color in the system properties.
         *
         *
         * The argument is treated as the name of a system property to
         * be obtained. The string value of this property is then interpreted
         * as an integer which is then converted to a `Color`
         * object.
         *
         *
         * If the specified property is not found or could not be parsed as
         * an integer then `null` is returned.
         * @param    nm the name of the color property
         * @return   the `Color` converted from the system
         * property.
         * @see java.lang.System.getProperty
         * @see java.lang.Integer.getInteger
         * @see java.awt.Color.Color
         * @since    JDK1.0
         */
        fun getColor(nm: String): Color? {
            val intval = Integer.getInteger(nm) ?: return null
            return Color(intval shr 16 and 0xFF, intval shr 8 and 0xFF, intval and 0xFF)
        }

        /**
         * Finds a color in the system properties.
         *
         *
         * The first argument is treated as the name of a system property to
         * be obtained. The string value of this property is then interpreted
         * as an integer which is then converted to a `Color`
         * object.
         *
         *
         * If the specified property is not found or cannot be parsed as
         * an integer then the `Color` specified by the second
         * argument is returned instead.
         * @param    nm the name of the color property
         * @param    v    the default `Color`
         * @return   the `Color` converted from the system
         * property, or the specified `Color`.
         * @see java.lang.System.getProperty
         * @see java.lang.Integer.getInteger
         * @see java.awt.Color.Color
         * @since    JDK1.0
         */
        fun getColor(nm: String, v: Color): Color {
            val intval = Integer.getInteger(nm) ?: return v
            return Color(intval shr 16 and 0xFF, intval shr 8 and 0xFF, intval and 0xFF)
        }

        /**
         * Finds a color in the system properties.
         *
         *
         * The first argument is treated as the name of a system property to
         * be obtained. The string value of this property is then interpreted
         * as an integer which is then converted to a `Color`
         * object.
         *
         *
         * If the specified property is not found or could not be parsed as
         * an integer then the integer value `v` is used instead,
         * and is converted to a `Color` object.
         * @param    nm  the name of the color property
         * @param    defaultRgb   the default color value, as an integer
         * @return   the `Color` converted from the system
         * property or the `Color` converted from
         * the specified integer.
         * @see java.lang.System.getProperty
         * @see java.lang.Integer.getInteger
         * @see java.awt.Color.Color
         * @since    JDK1.0
         */
        fun getColor(nm: String, defaultRgb: Int): Color {
            val intval = Integer.getInteger(nm)
            val i = intval?.toInt() ?: defaultRgb
            return Color(i shr 16 and 0xFF, i shr 8 and 0xFF, i shr 0 and 0xFF)
        }

        /**
         * Converts the components of a color, as specified by the HSB
         * model, to an equivalent set of values for the default RGB model.
         *
         *
         * The `saturation` and `brightness` components
         * should be floating-point values between zero and one
         * (numbers in the range 0.0-1.0).  The `hue` component
         * can be any floating-point number.  The floor of this number is
         * subtracted from it to create a fraction between 0 and 1.  This
         * fractional number is then multiplied by 360 to produce the hue
         * angle in the HSB color model.
         *
         *
         * The integer that is returned by `HSBtoRGB` encodes the
         * value of a color in bits 0-23 of an integer value that is the same
         * format used by the method [getRGB][.getRGB].
         * This integer can be supplied as an argument to the
         * `Color` constructor that takes a single integer argument.
         * @param     hue   the hue component of the color
         * @param     saturation   the saturation of the color
         * @param     brightness   the brightness of the color
         * @return    the RGB value of the color with the indicated hue,
         * saturation, and brightness.
         * @see java.awt.Color.getRGB
         * @see java.awt.Color.Color
         * @see java.awt.image.ColorModel.getRGBdefault
         * @since     JDK1.0
         */
        fun HSBtoRGB(hue: Float, saturation: Float, brightness: Float): Int {
            var r = 0
            var g = 0
            var b = 0
            if (saturation == 0f) {
                b = (brightness * 255.0f + 0.5f).toInt()
                g = b
                r = g
            } else {
                val h = (hue - Math.floor(hue.toDouble()).toFloat()) * 6.0f
                val f = h - Math.floor(h.toDouble()).toFloat()
                val p = brightness * (1.0f - saturation)
                val q = brightness * (1.0f - saturation * f)
                val t = brightness * (1.0f - saturation * (1.0f - f))
                when (h.toInt()) {
                    0 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (t * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    1 -> {
                        r = (q * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    2 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (t * 255.0f + 0.5f).toInt()
                    }
                    3 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (q * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    4 -> {
                        r = (t * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    5 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (q * 255.0f + 0.5f).toInt()
                    }
                }
            }
            return -0x1000000 or (r shl 16) or (g shl 8) or (b shl 0)
        }

        /**
         * Converts the components of a color, as specified by the default RGB
         * model, to an equivalent set of values for hue, saturation, and
         * brightness that are the three components of the HSB model.
         *
         *
         * If the `hsbvals` argument is `null`, then a
         * new array is allocated to return the result. Otherwise, the method
         * returns the array `hsbvals`, with the values put into
         * that array.
         * @param     r   the red component of the color
         * @param     g   the green component of the color
         * @param     b   the blue component of the color
         * @param     hsbvals  the array used to return the
         * three HSB values, or `null`
         * @return    an array of three elements containing the hue, saturation,
         * and brightness (in that order), of the color with
         * the indicated red, green, and blue components.
         * @see java.awt.Color.getRGB
         * @see java.awt.Color.Color
         * @see java.awt.image.ColorModel.getRGBdefault
         * @since     JDK1.0
         */
        fun RGBtoHSB(r: Int, g: Int, b: Int, hsbvals: FloatArray?): FloatArray {
            var hsbvals = hsbvals
            var hue: Float
            val saturation: Float
            val brightness: Float
            if (hsbvals == null) {
                hsbvals = FloatArray(3)
            }
            var cmax = if (r > g) r else g
            if (b > cmax) cmax = b
            var cmin = if (r < g) r else g
            if (b < cmin) cmin = b
            brightness = cmax.toFloat() / 255.0f
            saturation = if (cmax != 0) (cmax - cmin) as Float / cmax.toFloat() else 0f
            if (saturation == 0f) hue = 0f else {
                val redc = (cmax - r) as Float / (cmax - cmin) as Float
                val greenc = (cmax - g) as Float / (cmax - cmin) as Float
                val bluec = (cmax - b) as Float / (cmax - cmin) as Float
                hue = if (r == cmax) bluec - greenc else if (g == cmax) 2.0f + redc - bluec else 4.0f + greenc - redc
                hue = hue / 6.0f
                if (hue < 0) hue = hue + 1.0f
            }
            hsbvals[0] = hue
            hsbvals[1] = saturation
            hsbvals[2] = brightness
            return hsbvals
        }

        /**
         * Creates a `Color` object based on the specified values
         * for the HSB color model.
         *
         *
         * The `s` and `b` components should be
         * floating-point values between zero and one
         * (numbers in the range 0.0-1.0).  The `h` component
         * can be any floating-point number.  The floor of this number is
         * subtracted from it to create a fraction between 0 and 1.  This
         * fractional number is then multiplied by 360 to produce the hue
         * angle in the HSB color model.
         * @param  h   the hue component
         * @param  s   the saturation of the color
         * @param  b   the brightness of the color
         * @return  a `Color` object with the specified hue,
         * saturation, and brightness.
         * @since   JDK1.0
         */
        fun getHSBColor(h: Float, s: Float, b: Float): Color {
            return Color(HSBtoRGB(h, s, b))
        }
    }

    /**
     * Creates an opaque sRGB color with the specified red, green,
     * and blue values in the range (0 - 255).
     * The actual color used in rendering depends
     * on finding the best match given the color space
     * available for a given output device.
     * Alpha is defaulted to 255.
     *
     * @throws IllegalArgumentException if `r`, `g`
     * or `b` are outside of the range
     * 0 to 255, inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getRGB
     */
    constructor(r: Int, g: Int, b: Int) : this(r, g, b, 255) {}

    /**
     * Creates an sRGB color with the specified red, green, blue, and alpha
     * values in the range (0 - 255).
     *
     * @throws IllegalArgumentException if `r`, `g`,
     * `b` or `a` are outside of the range
     * 0 to 255, inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getAlpha
     *
     * @see .getRGB
     */
    @ConstructorProperties("red", "green", "blue", "alpha")
    constructor(r: Int, g: Int, b: Int, a: Int) {
        rgb = a and 0xFF shl 24 or
                (r and 0xFF shl 16) or
                (g and 0xFF shl 8) or
                (b and 0xFF shl 0)
        testColorValueRange(r, g, b, a)
    }

    /**
     * Creates an opaque sRGB color with the specified combined RGB value
     * consisting of the red component in bits 16-23, the green component
     * in bits 8-15, and the blue component in bits 0-7.  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.  Alpha is
     * defaulted to 255.
     *
     * @param rgb the combined RGB components
     * @see java.awt.image.ColorModel.getRGBdefault
     *
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getRGB
     */
    constructor(rgb: Int) {
        this.rgb = -0x1000000 or rgb
    }

    /**
     * Creates an sRGB color with the specified combined RGBA value consisting
     * of the alpha component in bits 24-31, the red component in bits 16-23,
     * the green component in bits 8-15, and the blue component in bits 0-7.
     * If the `hasalpha` argument is `false`, alpha
     * is defaulted to 255.
     *
     * @param rgba the combined RGBA components
     * @param hasalpha `true` if the alpha bits are valid;
     * `false` otherwise
     * @see java.awt.image.ColorModel.getRGBdefault
     *
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getAlpha
     *
     * @see .getRGB
     */
    constructor(rgba: Int, hasalpha: Boolean) {
        if (hasalpha) {
            rgb = rgba
        } else {
            rgb = -0x1000000 or rgba
        }
    }

    /**
     * Creates an opaque sRGB color with the specified red, green, and blue
     * values in the range (0.0 - 1.0).  Alpha is defaulted to 1.0.  The
     * actual color used in rendering depends on finding the best
     * match given the color space available for a particular output
     * device.
     *
     * @throws IllegalArgumentException if `r`, `g`
     * or `b` are outside of the range
     * 0.0 to 1.0, inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getRGB
     */
    constructor(r: Float, g: Float, b: Float) : this(
        (r * 255 + 0.5).toInt(),
        (g * 255 + 0.5).toInt(),
        (b * 255 + 0.5).toInt()
    ) {
        testColorValueRange(r, g, b, 1.0f)
        frgbvalue = FloatArray(3)
        frgbvalue!![0] = r
        frgbvalue!![1] = g
        frgbvalue!![2] = b
        falpha = 1.0f
        fvalue = frgbvalue
    }

    /**
     * Creates an sRGB color with the specified red, green, blue, and
     * alpha values in the range (0.0 - 1.0).  The actual color
     * used in rendering depends on finding the best match given the
     * color space available for a particular output device.
     * @throws IllegalArgumentException if `r`, `g`
     * `b` or `a` are outside of the range
     * 0.0 to 1.0, inclusive
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     * @see .getRed
     *
     * @see .getGreen
     *
     * @see .getBlue
     *
     * @see .getAlpha
     *
     * @see .getRGB
     */
    constructor(r: Float, g: Float, b: Float, a: Float) : this(
        (r * 255 + 0.5).toInt(),
        (g * 255 + 0.5).toInt(),
        (b * 255 + 0.5).toInt(),
        (a * 255 + 0.5).toInt()
    ) {
        frgbvalue = FloatArray(3)
        frgbvalue!![0] = r
        frgbvalue!![1] = g
        frgbvalue!![2] = b
        falpha = a
        fvalue = frgbvalue
    }

    /**
     * Returns the red component in the range 0-255 in the default sRGB
     * space.
     * @return the red component.
     * @see .getRGB
     */
    val red: Int
        get() = rgb shr 16 and 0xFF

    /**
     * Returns the green component in the range 0-255 in the default sRGB
     * space.
     * @return the green component.
     * @see .getRGB
     */
    val green: Int
        get() = rgb shr 8 and 0xFF

    /**
     * Returns the blue component in the range 0-255 in the default sRGB
     * space.
     * @return the blue component.
     * @see .getRGB
     */
    val blue: Int
        get() = rgb shr 0 and 0xFF

    /**
     * Returns the alpha component in the range 0-255.
     * @return the alpha component.
     * @see .getRGB
     */
    val alpha: Int
        get() = rgb shr 24 and 0xff

    /**
     * Creates a new `Color` that is a brighter version of this
     * `Color`.
     *
     *
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this `Color` to create a brighter version
     * of this `Color`.
     * The `alpha` value is preserved.
     * Although `brighter` and
     * `darker` are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors.
     * @return     a new `Color` object that is
     * a brighter version of this `Color`
     * with the same `alpha` value.
     * @see Color.darker
     *
     * @since      JDK1.0
     */
    fun brighter(): Color {
        var r = red
        var g = green
        var b = blue
        val alpha = alpha

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        val i = (1.0 / (1.0 - FACTOR)).toInt()
        if (r == 0 && g == 0 && b == 0) {
            return Color(i, i, i, alpha)
        }
        if (r > 0 && r < i) r = i
        if (g > 0 && g < i) g = i
        if (b > 0 && b < i) b = i
        return Color(
            Math.min((r / FACTOR).toInt(), 255),
            Math.min((g / FACTOR).toInt(), 255),
            Math.min((b / FACTOR).toInt(), 255),
            alpha
        )
    }

    /**
     * Creates a new `Color` that is a darker version of this
     * `Color`.
     *
     *
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this `Color` to create a darker version of
     * this `Color`.
     * The `alpha` value is preserved.
     * Although `brighter` and
     * `darker` are inverse operations, the results of a series
     * of invocations of these two methods might be inconsistent because
     * of rounding errors.
     * @return  a new `Color` object that is
     * a darker version of this `Color`
     * with the same `alpha` value.
     * @see java.awt.Color.brighter
     *
     * @since      JDK1.0
     */
    fun darker(): Color {
        return Color(
            Math.max((red * FACTOR).toInt(), 0),
            Math.max((green * FACTOR).toInt(), 0),
            Math.max((blue * FACTOR).toInt(), 0),
            alpha
        )
    }

    /**
     * Computes the hash code for this `Color`.
     * @return     a hash code value for this object.
     * @since      JDK1.0
     */
    override fun hashCode(): Int {
        return rgb
    }

    /**
     * Determines whether another object is equal to this
     * `Color`.
     *
     *
     * The result is `true` if and only if the argument is not
     * `null` and is a `Color` object that has the same
     * red, green, blue, and alpha values as this object.
     * @param       obj   the object to test for equality with this
     * `Color`
     * @return      `true` if the objects are the same;
     * `false` otherwise.
     * @since   JDK1.0
     */
    override fun equals(obj: Any?): Boolean {
        return obj is Color && obj.rgb == rgb
    }

    /**
     * Returns a string representation of this `Color`. This
     * method is intended to be used only for debugging purposes.  The
     * content and format of the returned string might vary between
     * implementations. The returned string might be empty but cannot
     * be `null`.
     *
     * @return  a string representation of this `Color`.
     */
    override fun toString(): String {
        return "Color[r=$red,g=$green,b=$blue,a=$alpha,hex=${"#%08x".format(rgba)}]"
    }

    /**
     * Returns a `float` array containing the color and alpha
     * components of the `Color`, as represented in the default
     * sRGB color space.
     * If `compArray` is `null`, an array of length
     * 4 is created for the return value.  Otherwise,
     * `compArray` must have length 4 or greater,
     * and it is filled in with the components and returned.
     * @param compArray an array that this method fills with
     * color and alpha components and returns
     * @return the RGBA components in a `float` array.
     */
    fun getRGBComponents(compArray: FloatArray?): FloatArray {
        val f: FloatArray
        f = compArray ?: FloatArray(4)
        if (frgbvalue == null) {
            f[0] = red.toFloat() / 255f
            f[1] = green.toFloat() / 255f
            f[2] = blue.toFloat() / 255f
            f[3] = alpha.toFloat() / 255f
        } else {
            f[0] = frgbvalue!![0]
            f[1] = frgbvalue!![1]
            f[2] = frgbvalue!![2]
            f[3] = falpha
        }
        return f
    }

    /**
     * Returns a `float` array containing only the color
     * components of the `Color`, in the default sRGB color
     * space.  If `compArray` is `null`, an array of
     * length 3 is created for the return value.  Otherwise,
     * `compArray` must have length 3 or greater, and it is
     * filled in with the components and returned.
     * @param compArray an array that this method fills with color
     * components and returns
     * @return the RGB components in a `float` array.
     */
    fun getRGBColorComponents(compArray: FloatArray?): FloatArray {
        val f: FloatArray
        f = compArray ?: FloatArray(3)
        if (frgbvalue == null) {
            f[0] = red.toFloat() / 255f
            f[1] = green.toFloat() / 255f
            f[2] = blue.toFloat() / 255f
        } else {
            f[0] = frgbvalue!![0]
            f[1] = frgbvalue!![1]
            f[2] = frgbvalue!![2]
        }
        return f
    }

    /**
     * Returns a `float` array containing the color and alpha
     * components of the `Color`, in the
     * `ColorSpace` of the `Color`.
     * If `compArray` is `null`, an array with
     * length equal to the number of components in the associated
     * `ColorSpace` plus one is created for
     * the return value.  Otherwise, `compArray` must have at
     * least this length and it is filled in with the components and
     * returned.
     * @param compArray an array that this method fills with the color and
     * alpha components of this `Color` in its
     * `ColorSpace` and returns
     * @return the color and alpha components in a `float`
     * array.
     */
    fun getComponents(compArray: FloatArray?): FloatArray {
        if (fvalue == null) return getRGBComponents(compArray)
        val f: FloatArray
        val n = fvalue!!.size
        f = compArray ?: FloatArray(n + 1)
        for (i in 0 until n) {
            f[i] = fvalue!![i]
        }
        f[n] = falpha
        return f
    }

    /**
     * Returns a `float` array containing only the color
     * components of the `Color`, in the
     * `ColorSpace` of the `Color`.
     * If `compArray` is `null`, an array with
     * length equal to the number of components in the associated
     * `ColorSpace` is created for
     * the return value.  Otherwise, `compArray` must have at
     * least this length and it is filled in with the components and
     * returned.
     * @param compArray an array that this method fills with the color
     * components of this `Color` in its
     * `ColorSpace` and returns
     * @return the color components in a `float` array.
     */
    fun getColorComponents(compArray: FloatArray?): FloatArray {
        if (fvalue == null) return getRGBColorComponents(compArray)
        val f: FloatArray
        val n = fvalue!!.size
        f = compArray ?: FloatArray(n)
        for (i in 0 until n) {
            f[i] = fvalue!![i]
        }
        return f
    }


}