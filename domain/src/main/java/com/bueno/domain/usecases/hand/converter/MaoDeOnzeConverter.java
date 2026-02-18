package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.HandResult;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.MaoDeOnzeDto;
import java.util.List;
import static com.bueno.domain.usecases.hand.converter.IncreasedPointsConverter.getPlayedCards;

public class MaoDeOnzeConverter {

    public static MaoDeOnzeDto of(Game game) {
        Hand hand = game.currentHand();
        Card vira = hand.getVira();
        Player p1 = hand.getFirstToPlay();
        Player p2 = hand.getLastToPlay();
        Player playerOnze = p1.getScore() == 11 ? p1 : p2;
        Player oponente = playerOnze.equals(p1) ? p2 : p1;

        Player vencedorDaMao = hand.getResult().flatMap(HandResult::getWinner).orElse(null);
        List<Card> cards = getReconstructedHand(playerOnze, hand, vira);

        return new MaoDeOnzeDto(
                game.getUuid(),
                !cards.isEmpty() ? cards.get(0).getRelativeValue(vira) : 0,
                cards.size() > 1 ? cards.get(1).getRelativeValue(vira) : 0,
                cards.size() > 2 ? cards.get(2).getRelativeValue(vira) : 0,
                playerOnze.getClass().getSimpleName(),
                playerOnze.getScore(),
                oponente.getScore(),
                hand.isMaoDeOnze(),
                playerOnze.equals(vencedorDaMao)
        );
    }

    private static List<Card> getReconstructedHand(Player player, Hand hand, Card vira) {
        return getPlayedCards(player, hand, vira);
    }
}