package com.bsokolovskyi;

import com.bsokolovskyi.ATM.ATM;
import com.bsokolovskyi.CreditCards.CardTransaction;
import com.bsokolovskyi.CreditCards.CreditCard;
import com.bsokolovskyi.postBoxUtils.PostBox;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;

public class Main {
    public static void main(String[] args) {
        //logger configuration
        BasicConfigurator.configure();

        //atm configuration
        PostBox<CardTransaction> postBoxContext = new PostBox<>(3);
        CreditCard[] simpleCreditCards = CreditCard.generateRandomCards(10, "Bohdan Sokolovskyi - ");
        CardTransaction[] cardTransactions = CardTransaction.generateRandomTransactionsFromCards(
                simpleCreditCards,
                CardTransaction.TypeOfTransaction.GET
        );

        new ATM.Builder(postBoxContext)
                .setDenomination1(100)
                .setDenomination2(50)
                .setDenomination5(50)
                .setDenomination10(50)
                .setDenomination20(20)
                .setDenomination50(10)
                .setDenomination100(5)
                .setCardTransactions(cardTransactions).build().runATM();
    }
}