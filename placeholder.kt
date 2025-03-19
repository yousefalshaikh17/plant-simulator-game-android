// Looking at open sourcing this project in the future!

import java.util.Date

// A simple function to greet a user
fun greetUser(name: String) {
    println("Hello, $name!")
}

// Main function to start the program (There is no real program at the moment)
fun main() {
    println("Hello, world!")
    
    // Call greetUser with an example name
    greetUser("Kotlin Developer")
    
    // Print some additional information
    val today = Date()
    println("Today's date is: $today")
}
