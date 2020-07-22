package kdl.api.model

import net.minecraft.client.util.math.AffineTransformation
import net.minecraft.client.util.math.Vector3f
import net.minecraft.util.math.Quaternion

class Transformation(
    val translation: Vector3f = Vector3f(),
    val rotation: Quaternion = Quaternion(0f, 0f, 0f, 1f),
    val scale: Vector3f = Vector3f(1f, 1f, 1f)
) {
    companion object {
        fun identity() = Transformation()

        fun rotation0() = Transformation()
        fun rotation90Y() = Transformation().apply { rotateDegY(90f) }
        fun rotation180Y() = Transformation().apply { rotateDegY(180f) }
        fun rotation270Y() = Transformation().apply { rotateDegY(270f) }
        fun rotation90X() = Transformation().apply { rotateDegX(90f) }
        fun rotation180X() = Transformation().apply { rotateDegX(180f) }
        fun rotation270X() = Transformation().apply { rotateDegX(270f) }
    }

    /**
     * Resets the transformation to the default state
     */
    fun reset() {
        resetTranslation()
        resetRotation()
        resetScale()
    }

    /**
     * Sets the current translation
     */
    fun moveTo(x: Float, y: Float, z: Float) {
        translation.set(x, y, z)
    }

    /**
     * Adds an offset to the current translation
     */
    fun translate(x: Float, y: Float, z: Float) {
        translation.set(translation.x + x, translation.y + y, translation.z + z)
    }

    /**
     * Sets the translation to 0
     */
    fun resetTranslation() {
        translation.set(0f, 0f, 0f)
    }

    /**
     * Rotates in the X axis by [angle] radians
     */
    fun rotateX(angle: Float) {
        rotate(angle, 1f, 0f, 0f)
    }

    /**
     * Rotates in the Y axis by [angle] radians
     */
    fun rotateY(angle: Float) {
        rotate(angle, 0f, 1f, 0f)
    }

    /**
     * Rotates in the Z axis by [angle] radians
     */
    fun rotateZ(angle: Float) {
        rotate(angle, 0f, 0f, 1f)
    }

    /**
     * Rotates around an arbitrary axis ([x], [y], [z]) by [angle] radians
     */
    fun rotate(angle: Float, x: Float, y: Float, z: Float) {
        rotation.hamiltonProduct(Quaternion(Vector3f(x, y, z), angle, false))
    }

    /**
     * Rotates in 3 axis, using Euler rotations in radians
     */
    fun rotate(x: Float, y: Float, z: Float) {
        rotateX(x)
        rotateY(y)
        rotateZ(z)
    }

    /**
     * Rotates in the X axis by [angle] degrees
     */
    fun rotateDegX(angle: Float) {
        rotate(Math.toRadians(angle.toDouble()).toFloat(), 1f, 0f, 0f)
    }

    /**
     * Rotates in the Y axis by [angle] degrees
     */
    fun rotateDegY(angle: Float) {
        rotate(Math.toRadians(angle.toDouble()).toFloat(), 0f, 1f, 0f)
    }

    /**
     * Rotates in the Z axis by [angle] degrees
     */
    fun rotateDegZ(angle: Float) {
        rotate(Math.toRadians(angle.toDouble()).toFloat(), 0f, 0f, 1f)
    }

    /**
     * Rotates around an arbitrary axis ([x], [y], [z]) by [angle] degrees
     */
    fun rotateDeg(angle: Float, x: Float, y: Float, z: Float) {
        rotation.hamiltonProduct(Quaternion(Vector3f(x, y, z), angle, true))
    }

    /**
     * Rotates in 3 axis, using Euler rotations in degrees
     */
    fun rotateDeg(x: Float, y: Float, z: Float) {
        rotateX(Math.toRadians(x.toDouble()).toFloat())
        rotateY(Math.toRadians(y.toDouble()).toFloat())
        rotateZ(Math.toRadians(z.toDouble()).toFloat())
    }

    /**
     * Sets the rotation to no-rotation
     */
    fun resetRotation() {
        rotation.set(0f, 0f, 0f, 1f)
    }

    /**
     * Sets the scale factor
     */
    fun scale(factor: Float) {
        scale.set(factor, factor, factor)
    }

    /**
     * Sets the scale factor for each axis
     */
    fun scale(x: Float, y: Float, z: Float) {
        scale.set(x, y, z)
    }

    /**
     * Multiplies the scale factor
     */
    fun multiplyScale(factor: Float) {
        scale.set(scale.x * factor, scale.y * factor, scale.z * factor)
    }

    /**
     * Multiplies the scale factor for each axis
     */
    fun multiplyScale(x: Float, y: Float, z: Float) {
        scale.set(scale.x * x, scale.y * y, scale.z * z)
    }

    /**
     * Sets the scale to 1
     */
    fun resetScale() {
        scale.set(1f, 1f, 1f)
    }

    /**
     * Conversion to Rotation3
     */
    fun toAffineTransformation(): AffineTransformation {
        return AffineTransformation(translation.copy(), rotation.copy(), scale.copy(), null)
    }

    /**
     * Applies the translation, rotation and scale to the OpenGLMatrix
     *
     * Equivalent to glTranslate, glRotate and glScale, but all together.
     */
//    fun glMultiply() {
//        auxMatrix.setIdentity()
//
//        // rotation
//        if (rotation.w != 0.0f) {
//            auxQuat.set(rotation)
//            auxQuat.inverse()
//            auxMatrix.setRotation(auxQuat)
//        }
//
//        // translation
//        auxMatrix.m30 = translation.x
//        auxMatrix.m31 = translation.y
//        auxMatrix.m32 = translation.z
//
//        // scale
//        auxMatrix.m00 *= scale.x
//        auxMatrix.m01 *= scale.x
//        auxMatrix.m02 *= scale.x
//        auxMatrix.m10 *= scale.y
//        auxMatrix.m11 *= scale.y
//        auxMatrix.m12 *= scale.y
//        auxMatrix.m20 *= scale.z
//        auxMatrix.m21 *= scale.z
//        auxMatrix.m22 *= scale.z
//
//        auxMatrix.transpose()
//        ForgeHooksClient.multiplyCurrentGlMatrix(auxMatrix)
//    }

    /**
     * Creates a immutable version of this transformation
     */
//    fun toMatrix4f(): Matrix4f {
//        val auxMatrix = Matrix4f()
//        auxMatrix.loadIdentity()
//
//        // rotation
//        if (rotation.d != 0.0f) {
//            auxQuat.set(rotation)
//            auxQuat.inverse()
//            auxMatrix.setRotation(auxQuat)
//        }
//
//        // translation
//        auxMatrix.m30 = translation.x
//        auxMatrix.m31 = translation.y
//        auxMatrix.m32 = translation.z
//
//        // scale
//        auxMatrix.m00 *= scale.x
//        auxMatrix.m01 *= scale.x
//        auxMatrix.m02 *= scale.x
//        auxMatrix.m10 *= scale.y
//        auxMatrix.m11 *= scale.y
//        auxMatrix.m12 *= scale.y
//        auxMatrix.m20 *= scale.z
//        auxMatrix.m21 *= scale.z
//        auxMatrix.m22 *= scale.z
//
//        return auxMatrix
//    }
}