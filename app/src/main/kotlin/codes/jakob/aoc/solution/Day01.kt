package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByCharacter
import codes.jakob.aoc.shared.splitByLines

object Day01 : Solution() {
    private val digitsPattern = Regex("(?=(zero|one|two|three|four|five|six|seven|eight|nine|\\d))")

    override fun solvePart1(input: String): Any {
        return input
            // Split the input into a list of lines
            .splitByLines()
            .asSequence()
            // Split each line into a list of words
            .map { line -> line.splitByCharacter() }
            // Filter out all words that do not contain a digit
            .map { chars -> chars.first { it.isDigit() } to chars.last { it.isDigit() } }
            // Join the first and last digit of each line to a string
            .map { (first, last) -> "$first$last" }
            // Convert the string to an integer
            .map { it.toInt() }
            // Sum up all integers
            .sum()
    }

    override fun solvePart2(input: String): Any {
        return input
            // Split the input into a list of lines
            .splitByLines()
            .asSequence()
            // Detect the digits in each line
            .map { line -> detectDigitsFromSentence(line) }
            // Join the first and last digit of each line to a string
            .map { digits -> digits.first() to digits.last() }
            // Join the first and last digit of each line to a string
            .map { (first, last) -> "$first$last" }
            // Convert the string to an integer
            .map { it.toInt() }
            // Sum up all integers
            .sum()
    }

    private fun detectDigitsFromSentence(sentence: String): List<Char> {
        return digitsPattern
            .findAll(sentence)
            .mapNotNull { it.groups[1]?.value }
            .map { detectDigitFromWord(it) }
            .toList()
    }

    private fun detectDigitFromWord(word: String): Char {
        if (word.length == 1 && word.first().isDigit()) return word.first()
        return when (word) {
            "zero" -> '0'
            "one" -> '1'
            "two" -> '2'
            "three" -> '3'
            "four" -> '4'
            "five" -> '5'
            "six" -> '6'
            "seven" -> '7'
            "eight" -> '8'
            "nine" -> '9'
            else -> throw IllegalArgumentException("The given word does not represent a digit")
        }
    }
}

fun main() {
    Day01.solve()
}
