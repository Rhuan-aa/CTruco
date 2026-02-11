package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.HandPoints;
import com.bueno.domain.entities.hand.HandResult;
import com.bueno.domain.entities.hand.Round;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.IncreasedPointsDto;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IncreasedPointsConverter {

    public static List<IncreasedPointsDto> of(Game game) {
        Hand hand = game.currentHand();
        List<IncreasedPointsDto> roundDtos = new ArrayList<>();
        List<Round> rounds = hand.getRoundsPlayed();

        Player p1 = hand.getFirstToPlay();
        Player p2 = hand.getLastToPlay();
        Card vira = hand.getVira();

        List<Card> p1DealtCards = getReconstructedHand(p1, hand, vira);
        List<Card> p2DealtCards = getReconstructedHand(p2, hand, vira);

        if (!p1DealtCards.isEmpty()) {
            roundDtos.add(createInitialDto(hand, p1, p2, vira, p1DealtCards));
        }
        if (!p2DealtCards.isEmpty()) {
            roundDtos.add(createInitialDto(hand, p2, p1, vira, p2DealtCards));
        }

        for (int i = 0; i < rounds.size(); i++) {
            roundDtos.add(createDtoForRound(hand, p1, p2, vira, p1DealtCards, rounds, i));
            roundDtos.add(createDtoForRound(hand, p2, p1, vira, p2DealtCards, rounds, i));
        }

        return roundDtos;
    }

    private static List<Card> getReconstructedHand(Player player, Hand hand, Card vira) {
        return getPlayedCards(player, hand, vira);
    }

    @NotNull
    static List<Card> getPlayedCards(Player player, Hand hand, Card vira) {
        List<Card> originalHand = new ArrayList<>();

        if (player.getCards() != null) {
            originalHand.addAll(player.getCards());
        }

        for (Round r : hand.getRoundsPlayed()) {
            if (r.getFirstToPlay().equals(player)) {
                originalHand.add(r.getFirstCard());
            } else if (r.getLastToPlay().equals(player) && r.getLastCard() != null) {
                originalHand.add(r.getLastCard());
            }
        }

        return originalHand.stream()
                .filter(c -> !c.equals(vira))
                .distinct()
                .sorted(Comparator.comparingInt(c -> c.getRelativeValue(vira)))
                .collect(Collectors.toList());
    }

    private static IncreasedPointsDto createInitialDto(Hand hand, Player player, Player opponent, Card vira, List<Card> dealtCards) {
        int weakVal = !dealtCards.isEmpty() ? dealtCards.get(0).getRelativeValue(vira) : 0;
        int medVal = dealtCards.size() > 1 ? dealtCards.get(1).getRelativeValue(vira) : 0;
        int strongVal = dealtCards.size() > 2 ? dealtCards.get(2).getRelativeValue(vira) : 0;

        return new IncreasedPointsDto(
                hand.getGameId(),
                weakVal,
                medVal,
                strongVal,
                player.getClass().getSimpleName(),
                hand.isMaoDeOnze(),
                getPile(hand, vira),
                -1.0, -1.0, -1.0,
                player.getScore(),
                opponent.getScore(),
                hand.getPoints().get(),
                determineOpponentAcceptance(hand),
                getRelativeScoreImpact(hand, player)
        );
    }

    private static IncreasedPointsDto createDtoForRound(Hand hand, Player player, Player opponent, Card vira,
                                                        List<Card> playerDealtCards, List<Round> rounds, int currentRoundIndex) {
        List<Integer> playedValuesPool = new ArrayList<>();

        for (int j = 0; j < currentRoundIndex; j++) {
            getPlayedValuesPool(player, vira, rounds, playedValuesPool, j);
        }

        double r1 = getRoundResultForPlayer(rounds.get(0), player);
        double r2 = (rounds.size() > 1 && currentRoundIndex >= 1)
                ? getRoundResultForPlayer(rounds.get(1), player)
                : -1.0;
        double r3 = (rounds.size() > 2 && currentRoundIndex >= 2)
                ? getRoundResultForPlayer(rounds.get(2), player)
                : -1.0;

        int weakVal = !playerDealtCards.isEmpty() ? getDisplayValue(playerDealtCards.get(0), playedValuesPool, vira) : 0;
        int medVal = playerDealtCards.size() > 1 ? getDisplayValue(playerDealtCards.get(1), playedValuesPool, vira) : 0;
        int strongVal = playerDealtCards.size() > 2 ? getDisplayValue(playerDealtCards.get(2), playedValuesPool, vira) : 0;

        return new IncreasedPointsDto(
                hand.getGameId(),
                weakVal,
                medVal,
                strongVal,
                player.getClass().getSimpleName(),
                hand.isMaoDeOnze(),
                getPile(hand, vira),
                r1,
                r2,
                r3,
                player.getScore(),
                opponent.getScore(),
                hand.getPoints().get(),
                determineOpponentAcceptance(hand),
                getRelativeScoreImpact(hand, player)
        );
    }

    static void getPlayedValuesPool(Player player, Card vira, List<Round> rounds, List<Integer> playedValuesPool, int j) {
        Round r = rounds.get(j);
        Card c = null;

        if (r.getFirstToPlay().equals(player)) {
            c = r.getFirstCard();
        } else if (r.getLastToPlay().equals(player) && r.getLastCard() != null) {
            c = r.getLastCard();
        }

        if (c != null && !c.equals(Card.closed())) {
            playedValuesPool.add(c.getRelativeValue(vira));
        }
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

    private static List<Integer> getPile(Hand hand, Card vira) {
        return hand.getDealtCards().stream()
                .map(c -> c.getRelativeValue(vira))
                .collect(Collectors.toList());
    }

    private static Integer getRelativeScoreImpact(Hand hand, Player player) {
        int points = hand.getResult().map(HandResult::getPoints).map(HandPoints::get).orElse(0);

        return hand.getResult()
                .flatMap(HandResult::getWinner)
                .map(winner -> winner.equals(player) ? points : -points)
                .orElse(0);
    }

    private static int determineOpponentAcceptance(Hand hand) {
        if (hand.getResult().isPresent()) {
            return hand.getPoints().get() > 1 ? 1 : -1;
        }
        return 0;
    }
}