package com.bueno.domain.usecases.hand.converter;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.game.Game;
import com.bueno.domain.entities.hand.Hand;
import com.bueno.domain.entities.hand.HandResult;
import com.bueno.domain.entities.player.Player;
import com.bueno.domain.usecases.hand.dtos.MaoDeOnzeDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MaoDeOnzeConverter {

    public static MaoDeOnzeDto of(Game game) {
        Hand hand = game.currentHand();
        Player playerOnze = hand.getFirstToPlay().getScore() == 11 ? hand.getFirstToPlay() : hand.getLastToPlay();
        Player oponente = playerOnze.equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
        Player vencedorDaMao = hand.getResult().flatMap(HandResult::getWinner).orElse(null);
        Card vira = game.getIntel().vira();
        List<Card> cards = new ArrayList<>(playerOnze.getCards());
        cards.sort(Comparator.comparingInt(c -> c.getRelativeValue(vira)));

        return new MaoDeOnzeDto(
                game.getUuid(),
                !cards.isEmpty() ? cards.get(0).getRelativeValue(vira) : 0,
                cards.size() > 1 ? cards.get(1).getRelativeValue(vira) : 0,
                cards.size() > 2 ? cards.get(2).getRelativeValue(vira) : 0,
                playerOnze.getClass().getSimpleName(),
                playerOnze.getScore(),
                oponente.getScore(),
                game.getFirstToPlay().equals(playerOnze),
                playerOnze.equals(vencedorDaMao)
        );
    }
}
