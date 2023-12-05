package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.splitByLines

object Day04 : Solution() {
    private val numbersPattern = Regex("\\b\\d{1,2}\\b")

    override fun solvePart1(input: String): Any {
        return parseScratchCards(input)
            .map { card ->
                card.matchingNumbers.fold(0) { accumulator, _ ->
                    if (accumulator == 0) 1 else accumulator * 2
                }
            }
            .sum()
    }

    override fun solvePart2(input: String): Any {
        val cards: Map<Int, ScratchCard> = parseScratchCards(input).associateBy { it.index }

        val cardsToEvaluate: ArrayDeque<ScratchCard> = ArrayDeque<ScratchCard>().also { deque ->
            deque.addAll(cards.values)
        }

        tailrec fun evaluateCards(evaluatedCount: Int = 0): Int {
            if (cardsToEvaluate.isEmpty()) return evaluatedCount

            val card: ScratchCard = cardsToEvaluate.removeFirst()
            val matchingNumbers: Set<Int> = card.matchingNumbers
            val newCards: List<ScratchCard> = if (matchingNumbers.isNotEmpty()) {
                ((card.index + 1)..(card.index + matchingNumbers.count())).mapNotNull { newCardIndex ->
                    cards[newCardIndex]
                }
            } else emptyList()

            cardsToEvaluate.addAll(newCards)

            return evaluateCards(evaluatedCount + 1)
        }

        return evaluateCards()
    }

    private fun parseScratchCards(input: String): List<ScratchCard> {
        return input.splitByLines()
            .mapIndexed { index, line ->
                val numbers = line.substringAfter(": ")
                val (winningSequence, ownSequence) = numbers.split(" | ")
                val winningNumbers = numbersPattern.findAll(winningSequence).map { it.value.toInt() }.toList()
                val ownNumbers = numbersPattern.findAll(ownSequence).map { it.value.toInt() }.toList()
                ScratchCard(index + 1, winningNumbers, ownNumbers)
            }
    }

    data class ScratchCard(
        val index: Int,
        val winningNumbers: List<Int>,
        val ownNumbers: List<Int>,
    ) {
        val matchingNumbers: Set<Int> = this.winningNumbers.intersect(this.ownNumbers)
    }
}

fun main() {
    Day04.solve()
}
