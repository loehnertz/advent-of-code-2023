package codes.jakob.aoc.solution

import codes.jakob.aoc.shared.*
import codes.jakob.aoc.solution.Day07.HandType.*

object Day07 : Solution() {
    private val handComparator: Comparator<Hand> = compareBy(
        { it.type },
        { it.cards[0] },
        { it.cards[1] },
        { it.cards[2] },
        { it.cards[3] },
        { it.cards[4] },
    )

    override fun solvePart1(input: String): Any {
        return calculateWinnings(input, jIsJoker = false)
    }

    override fun solvePart2(input: String): Any {
        return calculateWinnings(input, jIsJoker = true)
    }

    private fun calculateWinnings(input: String, jIsJoker: Boolean): Int {
        return parseHands(input, jIsJoker)
            .sortedWith(handComparator)
            .associateByIndex()
            .mapKeys { (index, _) -> index + 1 }
            .entries
            .fold(0) { winnings, (index, hand) ->
                winnings + hand.bid * index
            }
    }

    private fun parseHands(input: String, jIsJoker: Boolean = false): List<Hand> {
        return input.splitByLines().map { line ->
            val (handString, bidString) = line.split(" ")
            Hand(handString.splitByCharacter().map { Card.fromSymbol(it, jIsJoker) }, bidString.toInt())
        }
    }

    private data class Hand(
        val cards: List<Card>,
        val bid: Int,
    ) {
        init {
            require(cards.count() == 5) { "A hand must consist of exactly 5 cards" }
        }

        val type: HandType = determineType()

        private fun determineType(): HandType {
            val valueCounts: Map<Int, Int> = cards
                .groupBy { it.value }
                .mapValues { (_, cards) -> cards.count() }
            val bestNaturalType: HandType = when (valueCounts.values.maxOrNull()) {
                5 -> FIVE_OF_A_KIND
                4 -> FOUR_OF_A_KIND
                3 -> if (valueCounts.values.containsTimes(2, 1)) FULL_HOUSE else THREE_OF_A_KIND
                2 -> if (valueCounts.values.containsTimes(2, 2)) TWO_PAIRS else ONE_PAIR
                1 -> HIGH_CARD
                else -> error("A hand must have at least one card")
            }
            val jokerCount: Int = cards.count { it.isJoker }
            return if (jokerCount > 0) {
                when (bestNaturalType) {
                    HIGH_CARD -> {
                        when (jokerCount) {
                            1 -> ONE_PAIR
                            2 -> THREE_OF_A_KIND
                            3 -> FOUR_OF_A_KIND
                            else -> FIVE_OF_A_KIND
                        }
                    }

                    // If the pair are jokers, the highest possible hand is a full house
                    ONE_PAIR -> {
                        when (jokerCount) {
                            1 -> THREE_OF_A_KIND
                            2 -> THREE_OF_A_KIND
                            else -> FIVE_OF_A_KIND
                        }
                    }

                    TWO_PAIRS -> {
                        when (jokerCount) {
                            1 -> FULL_HOUSE
                            2 -> FOUR_OF_A_KIND
                            else -> FIVE_OF_A_KIND
                        }
                    }

                    THREE_OF_A_KIND -> {
                        when (jokerCount) {
                            1 -> FOUR_OF_A_KIND
                            3 -> FOUR_OF_A_KIND
                            else -> FIVE_OF_A_KIND
                        }
                    }

                    FULL_HOUSE -> {
                        when (jokerCount) {
                            1 -> FOUR_OF_A_KIND
                            else -> FIVE_OF_A_KIND
                        }
                    }

                    FOUR_OF_A_KIND -> FIVE_OF_A_KIND
                    FIVE_OF_A_KIND -> FIVE_OF_A_KIND
                }
            } else bestNaturalType
        }

        override fun toString(): String {
            return "Hand(cards=${cards.joinToString("")}, bid=$bid)"
        }
    }

    private data class Card(
        val value: Int,
    ) : Comparable<Card> {
        val isJoker: Boolean = value == 1

        init {
            require(value in VALUE_RANGE || isJoker) { "A card must have a value between 2 and 14 or be a joker" }
        }

        private fun convertToSymbol(): Char {
            return when (value) {
                14 -> 'A'
                13 -> 'K'
                12 -> 'Q'
                11 -> 'J'
                10 -> 'T'
                1 -> 'J'
                else -> value.digitToChar()
            }
        }

        override fun compareTo(other: Card): Int = this.value.compareTo(other.value)

        override fun toString(): String = convertToSymbol().toString()

        companion object {
            private val VALUE_RANGE: IntRange = 2..14

            fun fromSymbol(symbol: Char, jIsJoker: Boolean = false): Card {
                return when (symbol) {
                    'A' -> Card(14)
                    'K' -> Card(13)
                    'Q' -> Card(12)
                    'J' -> if (!jIsJoker) Card(11) else Card(1)
                    'T' -> Card(10)
                    else -> Card(symbol.parseInt())
                }
            }
        }
    }

    private enum class HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIRS,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND;
    }
}

fun main() = Day07.solve()
