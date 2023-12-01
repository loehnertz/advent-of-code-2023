package codes.jakob.aoc.solution

object Day01 : Solution() {
    private val digitsPattern = Regex("(?=(zero|one|two|three|four|five|six|seven|eight|nine|\\d))")

    override fun solvePart1(input: String): Any {
        return input
            .splitMultiline()
            .asSequence()
            .map { line -> line.splitByCharacter() }
            .map { chars -> chars.first { it.isDigit() } to chars.last { it.isDigit() } }
            .map { (first, last) -> "$first$last" }
            .map { it.toInt() }
            .sum()
    }

    override fun solvePart2(input: String): Any {
        return input
            .splitMultiline()
            .asSequence()
            .map { line -> detectDigitsFromSentence(line) }
            .map { digits -> digits.first() to digits.last() }
            .map { (first, last) -> "$first$last" }
            .map { it.toInt() }
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
