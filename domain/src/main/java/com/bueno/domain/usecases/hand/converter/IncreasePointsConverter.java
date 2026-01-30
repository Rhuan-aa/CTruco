package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.HandPoints;
import com.bueno.domain.entities.hand.HandResult;
import com.bueno.domain.entities.hand.Round;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.IncreasePointsDto;

import java.util.List;
import java.util.stream.Collectors;

public class IncreasePointsConverter {

    public static List<IncreasePointsDto> ofGame(Game game) {
        return game.getHands().stream()
                .map(IncreasePointsConverter::ofHand)
                .collect(Collectors.toList());
    }

    private static IncreasePointsDto ofHand(Hand hand) {
        Player p1 = hand.getFirstToPlay();
        Player p2 = hand.getLastToPlay();

        List<Card> p1DealtCards = hand.getDealtCards().stream()
                .filter(c -> !c.equals(hand.getVira()))
                .sorted((c1, c2) -> c1.compareValueTo(c2, hand.getVira()))
                .collect(Collectors.toList());

        List<Card> playedCards = hand.getOpenCards();
        List<Round> rounds = hand.getRoundsPlayed();
        Card vira = hand.getVira();

        String r1 = !rounds.isEmpty() ? getRoundWinnerName(rounds.get(0)) : "NONE";
        String r2 = rounds.size() > 1 ? getRoundWinnerName(rounds.get(1)) : "NONE";
        String r3 = rounds.size() > 2 ? getRoundWinnerName(rounds.get(2)) : "NONE";

        return new IncreasePointsDto(
                hand.getGameId(),
                checkIfPlayed(p1DealtCards, playedCards, 0, vira),
                checkIfPlayed(p1DealtCards, playedCards, 1, vira),
                checkIfPlayed(p1DealtCards, playedCards, 2, vira),
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

    private static String getRoundWinnerName(Round round) {
        return round.getWinner().map(Player::getUsername).orElse("DRAW");
    }

    private static int determineOpponentAcceptance(Hand hand) {
        if (hand.getResult().isPresent()) {
            return hand.getPoints().get() > 1 ? 1 : -1;
        }
        return 0;
    }
}
