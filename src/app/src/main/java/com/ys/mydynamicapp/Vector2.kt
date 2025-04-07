package com.ys.mydynamicapp

import kotlin.math.pow
import kotlin.math.sqrt

class Vector2(private var axis0:Int, private var axis1:Int) {

    var x: Int get() = axis0; set(value) {axis0 = value}
    var y: Int get() = axis1; set(value) {axis1 = value}

    init {
    }

    constructor(bothAxis:Int) : this(bothAxis, bothAxis)


    fun getLargestAxis(): Int {
        if (axis0 > axis1)
            return axis0
        return axis1
    }

    fun clone(): Vector2 {
        return Vector2(axis0,axis1)
    }

    fun add(addend: Vector2): Vector2 {
        axis0 += addend.x
        axis1 += addend.y
        return this
    }

    fun update(x:Int, y:Int)
    {
        this.axis0 = x
        this.axis1 = y
    }

    fun update(vector2: Vector2)
    {
        this.axis0 = vector2.x
        this.axis1 = vector2.y
    }

    fun subtract(subtrahend: Vector2): Vector2 {
        axis0 -= subtrahend.x
        axis1 -= subtrahend.y
        return this
    }

    fun divide(dividend: Float): Vector2 {
        axis0 = (axis0.toFloat() / dividend).toInt()
        axis1 = (axis1.toFloat() / dividend).toInt()
        return this
    }

    fun divide(dividend: Vector2): Vector2 {
        axis0 = (axis0.toFloat() / dividend.x.toFloat()).toInt()
        axis1 = (axis1.toFloat() / dividend.y.toFloat()).toInt()
        return this
    }

    fun multiply(multiplier: Float): Vector2 {
        axis0 = (axis0.toFloat() * multiplier).toInt()
        axis1 = (axis1.toFloat() * multiplier).toInt()
        return this
    }

    fun multiply(multiplier: Vector2): Vector2 {
        axis0 = (axis0.toFloat() * multiplier.x).toInt()
        axis1 = (axis1.toFloat() * multiplier.y).toInt()
        return this
    }

    fun equals(vector2: Vector2): Boolean {
        return (vector2.x == axis0) and (vector2.y == axis1)
    }

    override fun toString(): String {
        return axis0.toString()+", "+axis1
    }
}
