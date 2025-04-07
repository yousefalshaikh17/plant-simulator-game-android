// Deprecated

package com.ys.mydynamicapp

@Deprecated(message = "Deprecated in favor of Rect class which provides all the same features built in.")
class BoundingBox(var min: Vector2, var max :Vector2) {
    constructor(minX :Int, minY :Int, maxX :Int, maxY :Int) : this(Vector2(minX, minY), Vector2(maxX,maxY))



    init {
        min = min.clone()
        max = max.clone()
    }

    fun checkForIntersection(intersection :Vector2): Boolean {
        return checkForIntersection(intersection.x, intersection.y)
    }

    fun checkForIntersection(x: Int, y: Int): Boolean {
        return (min.x <= x && max.x >= x && min.y <= y && max.y >= y)
    }

    fun checkIfWithinOtherBoundary(boundary: BoundingBox): Boolean
    {
        return checkForIntersection(boundary.min.x, boundary.min.y) ||
                checkForIntersection(boundary.max.x, boundary.min.y) ||
                checkForIntersection(boundary.min.x, boundary.max.y) ||
                checkForIntersection(boundary.max.x, boundary.max.y)
    }

    fun update(min: Vector2, max: Vector2)
    {
        this.min = min.clone()
        this.max = max.clone()
    }

//    fun checkIfWithinOtherBoundary(gameObject: GameObject): Boolean
//    {
//        return checkIfWithinOtherBoundary(gameObject.getBoundingBox())
//    }

    override fun toString(): String {
        return min.toString()+" "+max.toString()
    }

}