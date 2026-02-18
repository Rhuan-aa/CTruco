package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.Round;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.PlayedCardDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.bueno.domain.usecases.hand.converter.IncreasedPointsConverter.getPlayedCards;

public class PlayedCardConverter {

    public static List<PlayedCardDto> of(Game game) {
        Hand hand = game.currentHand();
        List<PlayedCardDto> playedCards = new ArrayList<>();
        List<Round> rounds = hand.getRoundsPlayed();
        Card vira = hand.getVira();
        Player p1 = hand.getFirstToPlay();
        Player p2 = hand.getLastToPlay();

        List<Card> p1Dealt = getReconstructedHand(p1, hand, vira);
        List<Card> p2Dealt = getReconstructedHand(p2, hand, vira);
        Map<Player, List<Card>> hands = Map.of(p1, p1Dealt, p2, p2Dealt);
        int deckPileSize = p1Dealt.size() + p2Dealt.size() + 1;

        for (int i = 0; i < rounds.size(); i++) {
            Round round = rounds.get(i);
            processMove(playedCards, round.getFirstToPlay(), round.getFirstCard(), hands, hand, game, rounds, i, deckPileSize);
            processMove(playedCards, round.getLastToPlay(), round.getLastCard(), hands, hand, game, rounds, i, deckPileSize);
        }

        return playedCards;
    }

    private static void processMove(List<PlayedCardDto> resultList, Player player, Card card, Map<Player, List<Card>> decks, Hand hand, Game game, List<Round> rounds, int roundIndex, int deckPileSize) {
        if (card == null || player == null) return;

        List<Card> dealt = decks.getOrDefault(player, List.of());
        if (dealt.isEmpty()) return;

        resultList.add(createDto(
                hand, game, player, hand.getVira(), dealt, rounds, roundIndex, card, deckPileSize
        ));
    }

    private static List<Card> getReconstructedHand(Player player, Hand hand, Card vira) {
        return getPlayedCards(player, hand, vira);
    }

    private static PlayedCardDto createDto(Hand hand, Game game, Player player, Card vira,
                                           List<Card> dealt, List<Round> rounds,
                                           int roundIndex, Card playedCard, int deckPileSize) {

        List<Integer> playedValuesPool = new ArrayList<>();
        for (int j = 0; j < roundIndex; j++) {
            IncreasedPointsConverter.getPlayedValuesPool(player, vira, rounds, playedValuesPool, j);
        }

        int weakVal = !dealt.isEmpty() ? getDisplayValue(dealt.get(0), playedValuesPool, vira) : 0;
        int medVal = dealt.size() > 1 ? getDisplayValue(dealt.get(1), playedValuesPool, vira) : 0;
        int strongVal = dealt.size() > 2 ? getDisplayValue(dealt.get(2), playedValuesPool, vira) : 0;

        boolean isClosed = playedCard.equals(Card.closed());
        int choiceValue = isClosed ? 0 : playedCard.getRelativeValue(vira);

        boolean isFirstToPlay = hand.getFirstToPlay().equals(player);

        boolean isFinalRound = (roundIndex == rounds.size() - 1);
        boolean isHandWinner = isFinalRound && hand.getResult()
                .flatMap(res -> res.getWinner())
                .map(winner -> winner.equals(player))
                .orElse(false);

        return new PlayedCardDto(
                game.getUuid(),
                weakVal,
                medVal,
                strongVal,
                player.getClass().getSimpleName(),
                isFirstToPlay,
                deckPileSize,
                roundIndex + 1,
                getRoundResultForPlayer(rounds.get(0), player),
                (rounds.size() > 1 && roundIndex >= 1) ? getRoundResultForPlayer(rounds.get(1), player) : -1.0,
                (rounds.size() > 2 && roundIndex >= 2) ? getRoundResultForPlayer(rounds.get(2), player) : -1.0,
                isHandWinner,
                choiceValue,
                isClosed
        );
    }

    private static int getDisplayValue(Card target, List<Integer> playedValuesPool, Card vira) {
        int targetVal = target.getRelativeValue(vira);

        if (playedValuesPool.contains(targetVal)) {
            playedValuesPool.remove(Integer.valueOf(targetVal));
            return -1;
        }

        return targetVal;
    }

    private static double getRoundResultForPlayer(Round round, Player player) {
        return round.getWinner()
                .map(winner -> winner.equals(player) ? 1.0 : 0.0)
                .orElse(0.5);
    }
}