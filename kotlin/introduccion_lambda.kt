/*fun upperCaseString(text: String): String {
    return text.uppercase()
}*/

val upperCaseString = { text: String -> text.uppercase() }

fun main() {
    println(upperCaseString("hello world"))

    val numbers = listOf(1, -2, 3, -4, 5, -6)
    println(numbers)

    val positives = numbers.filter({ x -> x > 0})
    println(positives)

    val negatives = numbers.filter({ x -> x < 0})
    println(negatives)

    val isPositive = { x: Int -> x > 0 }
    val positives2 = numbers.filter(isPositive)
    println(positives2)

    val isNegative = { x: Int -> x < 0 }
    val negatives2 = numbers.filter(isNegative)
    println(negatives2)

    val doubled = numbers.map({ x -> x * 2 })
    println(doubled)

    val isDoubled = { x: Int -> x * 2 }
    val doubled2 = numbers.map(isDoubled)
    println(doubled2)

    val tripled = numbers.map({ x -> x * 3 })
    println(tripled)

    val isTripled = { x: Int -> x * 3 }
    val tripled2 = numbers.map(isTripled)
    println(tripled2)

    // You have a list of actions supported by a web service, a common prefix for all requests, and an ID of a particular resource. To request an action title over the resource with ID: 5, you need to create the following URL: https://example.com/book-info/5/title. Use a lambda expression to create a list of URLs from the list of actions.
    val actions = listOf("title", "year", "author")
    val prefix = "https://example.com/book-info"
    val id = 5
    // https://example.com/book-info/5/title
    // https://example.com/book-info/5/year
    // https://example.com/book-info/5/author
    println(actions)
    val urls = actions.map({ action -> "http://www.example.com/$id/$action" })
    println(urls)
}