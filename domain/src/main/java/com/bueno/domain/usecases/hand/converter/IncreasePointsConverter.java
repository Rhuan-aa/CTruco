package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.HandPoints;
import com.bueno.domain.entities.hand.HandResult;
import com.bueno.domain.entities.hand.Round;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.IncreasePointsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IncreasePointsConverter {

    public static List<IncreasePointsDto> of(Game game) {
        Hand hand = game.currentHand();
        List<IncreasePointsDto> roundDtos = new ArrayList<>();
        List<Round> rounds = hand.getRoundsPlayed();

        Player p1 = hand.getFirstToPlay();
        Player p2 = hand.getLastToPlay();
        Card vira = hand.getVira();

        List<Card> p1DealtCards = hand.getDealtCards().stream()
                .filter(c -> !c.equals(vira))
                .sorted((c1, c2) -> c1.compareValueTo(c2, vira))
                .collect(Collectors.toList());

        for (int i = 0; i < rounds.size(); i++) {
            roundDtos.add(createDtoForRound(hand, game, p1, p2, vira, p1DealtCards, rounds, i));
        }

        return roundDtos;
    }

    private static IncreasePointsDto createDtoForRound(Hand hand, Game game, Player p1, Player p2, Card vira,
                                                       List<Card> p1DealtCards, List<Round> rounds, int currentRoundIndex) {

        List<Card> cardsPlayedUntilNow = new ArrayList<>();
        for (int j = 0; j <= currentRoundIndex; j++) {
            cardsPlayedUntilNow.add(rounds.get(j).getFirstCard());
            cardsPlayedUntilNow.add(rounds.get(j).getLastCard());
        }

        double r1 = getRoundWinnerValue(rounds.get(0), game);
        double r2 = (currentRoundIndex >= 1) ? getRoundWinnerValue(rounds.get(1), game) : -1.0;
        double r3 = (currentRoundIndex >= 2) ? getRoundWinnerValue(rounds.get(2), game) : -1.0;

        return new IncreasePointsDto(
                hand.getGameId(),
                checkIfPlayed(p1DealtCards, cardsPlayedUntilNow, 0, vira),
                checkIfPlayed(p1DealtCards, cardsPlayedUntilNow, 1, vira),
                checkIfPlayed(p1DealtCards, cardsPlayedUntilNow, 2, vira),
                p1.getClass().getSimpleName(),
                hand.isMaoDeOnze(),
                getPile(hand, vira),
                r1,
                r2,
                r3,
                p1.getScore(),
                p2.getScore(),
                hand.getPoints().get(),
                determineOpponentAcceptance(hand),
                getGeneralScoreImpact(hand)
        );
    }

    private static List<Integer> getPile(Hand hand, Card vira) {
        return hand.getDealtCards().stream().map(c -> c.getRelativeValue(vira)).toList();
    }

    private static Integer getGeneralScoreImpact(Hand hand) {
        return hand.getResult().map(HandResult::getPoints).map(HandPoints::get).orElse(0);
    }

    private static int checkIfPlayed(List<Card> dealt, List<Card> played, int index, Card vira) {
        if (index >= dealt.size()) return 0;
        Card card = dealt.get(index);
        return played.contains(card) ? -1 : card.getRelativeValue(vira);
    }

    private static double getRoundWinnerValue(Round round, Game game) {
        return round.getWinner()
                .map(winner -> winner.equals(game.getPlayer1()) ? 1.0 : 2.0)
                .orElse(0.0);
    }

    private static int determineOpponentAcceptance(Hand hand) {
        if (hand.getResult().isPresent()) {
            return hand.getPoints().get() > 1 ? 1 : -1;
        }
        return 0;
    }
}