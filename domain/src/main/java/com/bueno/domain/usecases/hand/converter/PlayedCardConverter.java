package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.Round;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.PlayedCardDto;
import com.bueno.domain.usecases.hand.enums.CardChoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bueno.domain.usecases.hand.converter.IncreasedPointsConverter.getPlayedCards;

public class PlayedCardConverter {

    private static final CardChoice[] OPEN_CHOICES = {
            CardChoice.FRACA_ABERTA, CardChoice.MEDIA_ABERTA, CardChoice.FORTE_ABERTA
    };

    private static final CardChoice[] CLOSED_CHOICES = {
            CardChoice.FRACA_FECHADA, CardChoice.MEDIA_FECHADA, CardChoice.FORTE_FECHADA
    };

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

        if (!p1Dealt.isEmpty()) {
            playedCards.add(createInitialDto(game, p1, vira, p1Dealt, hand));
        }
        if (!p2Dealt.isEmpty()) {
            playedCards.add(createInitialDto(game, p2, vira, p2Dealt, hand));
        }

        for (int i = 0; i < rounds.size(); i++) {
            Round round = rounds.get(i);
            processMove(playedCards, round.getFirstToPlay(), round.getFirstCard(), hands, hand, game, rounds, i);
            processMove(playedCards, round.getLastToPlay(), round.getLastCard(), hands, hand, game, rounds, i);
        }

        return playedCards;
    }

    private static void processMove(List<PlayedCardDto> resultList, Player player, Card card, Map<Player, List<Card>> decks, Hand hand, Game game, List<Round> rounds, int roundIndex) {
        if (card == null || player == null) return;

        List<Card> dealt = decks.getOrDefault(player, List.of());
        if (dealt.isEmpty()) return;

        resultList.add(createDto(
                hand, game, player, hand.getVira(), dealt, rounds, roundIndex, card
        ));
    }

    private static List<Card> getReconstructedHand(Player player, Hand hand, Card vira) {
        return getPlayedCards(player, hand, vira);
    }

    private static PlayedCardDto createInitialDto(Game game, Player player, Card vira, List<Card> dealt, Hand hand) {
        int weakVal = !dealt.isEmpty() ? dealt.get(0).getRelativeValue(vira) : 0;
        int medVal = dealt.size() > 1 ? dealt.get(1).getRelativeValue(vira) : 0;
        int strongVal = dealt.size() > 2 ? dealt.get(2).getRelativeValue(vira) : 0;

        return new PlayedCardDto(
                game.getUuid(),
                weakVal,
                medVal,
                strongVal,
                player.getClass().getSimpleName(),
                false,
                hand.getDealtCards().size(),
                0,
                -1.0, -1.0, -1.0,
                false,
                CardChoice.NONE.name()
        );
    }

    private static PlayedCardDto createDto(Hand hand, Game game, Player player, Card vira,
                                           List<Card> dealt, List<Round> rounds,
                                           int roundIndex, Card playedCard) {

        List<Integer> playedValuesPool = new ArrayList<>();
        for (int j = 0; j < roundIndex; j++) {
            IncreasedPointsConverter.getPlayedValuesPool(player, vira, rounds, playedValuesPool, j);
        }

        int weakVal = !dealt.isEmpty() ? getDisplayValue(dealt.get(0), playedValuesPool, vira) : 0;
        int medVal = dealt.size() > 1 ? getDisplayValue(dealt.get(1), playedValuesPool, vira) : 0;
        int strongVal = dealt.size() > 2 ? getDisplayValue(dealt.get(2), playedValuesPool, vira) : 0;

        return new PlayedCardDto(
                game.getUuid(),
                weakVal,
                medVal,
                strongVal,
                player.getClass().getSimpleName(),
                !playedCard.equals(Card.closed()),
                hand.getDealtCards().size(),
                roundIndex + 1,
                getRoundResultForPlayer(rounds.get(0), player),
                (rounds.size() > 1 && roundIndex >= 1) ? getRoundResultForPlayer(rounds.get(1), player) : -1.0,
                (rounds.size() > 2 && roundIndex >= 2) ? getRoundResultForPlayer(rounds.get(2), player) : -1.0,

                hand.getResult().map(res -> res.getWinner().isPresent() &&
                        res.getWinner().get().equals(player)).orElse(false),
                determineChoice(playedCard, dealt, vira).name()
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

    private static CardChoice determineChoice(Card played, List<Card> dealt, Card vira) {
        if (dealt.isEmpty()) return CardChoice.FRACA_ABERTA;

        boolean isClosed = played.equals(Card.closed());
        int strengthIndex = getStrengthIndex(played, dealt, vira, isClosed);

        if (strengthIndex < 0 || strengthIndex >= 3) strengthIndex = 0;

        return isClosed ? CLOSED_CHOICES[strengthIndex] : OPEN_CHOICES[strengthIndex];
    }

    private static int getStrengthIndex(Card played, List<Card> dealt, Card vira, boolean isClosed) {
        for (int i = Math.min(2, dealt.size() - 1); i > 0; i--) {
            if (isCardMatch(played, dealt.get(i), vira, isClosed)) {
                return i;
            }
        }
        return 0;
    }

    private static boolean isCardMatch(Card played, Card target, Card vira, boolean isClosed) {
        if (isClosed) return played.equals(target);
        return played.getRelativeValue(vira) == target.getRelativeValue(vira);
    }

    private static double getRoundResultForPlayer(Round round, Player player) {
        return round.getWinner()
                .map(winner -> winner.equals(player) ? 1.0 : 0.0)
                .orElse(0.5);
    }
}