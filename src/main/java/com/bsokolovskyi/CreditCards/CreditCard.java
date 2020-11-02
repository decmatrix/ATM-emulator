package com.bsokolovskyi.CreditCards;

import java.util.Random;

public class CreditCard {
    private long balance;
    private final String nameOfCard;
    private static final Random rnd = new Random();
    private static final long LIMIT_OF_BALANCE = 10000;

    public CreditCard(String nameOfCard, long balance) {
        this.nameOfCard = nameOfCard;
        this.balance = balance;
    }

    public CreditCard(String nameOfCard) {
        this(nameOfCard, rnd.nextInt(getRandomBalanceRange()));
    }

    public static CreditCard[] generateRandomCards(int size, String prefixOfName) {
        CreditCard[] creditCards = new CreditCard[size];

        for(int i = 0; i < creditCards.length; i++) {
            creditCards[i] = new CreditCard(prefixOfName + i);
        }

        return creditCards;
    }

    public static CreditCard[] generateRandomCardsFromNames(String[] names) {
        CreditCard[] creditCards = new CreditCard[names.length];

        for(int i = 0; i < creditCards.length; i++) {
            creditCards[i] = new CreditCard(names[i] + i);
        }

        return creditCards;
    }

    public String getNameOfCard() {
        return nameOfCard;
    }

    public long getBalance() {
        return balance;
    }

    public boolean canGetSum(long sum) {
        return sum <= balance;
    }

    public boolean canPutMoney(long sum) {
        return sum + balance <= LIMIT_OF_BALANCE;
    }

    public void getMoney(long sum) {
        if(canGetSum(sum)) {
            balance -= sum;
        }
    }

    public void putMoney(long sum) {
        if(canPutMoney(sum)) {
            balance += sum;
        }
    }

    @Override
    public String toString() {
        return String.format("name of card: %s, balance: %s", nameOfCard, balance);
    }

    private static int getRandomBalanceRange() {
        int rangeBalance = 1;
        switch (rnd.nextInt(4)) {
            case 0:
                rangeBalance = 1000;
                break;
            case 1:
                rangeBalance = 100;
                break;
            case 2:
                rangeBalance = 10;
                break;
            case 3:
                rangeBalance = 10000;
        }

        return rangeBalance;
    }
}
