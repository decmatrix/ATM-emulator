package com.bsokolovskyi.CreditCards;

import java.util.Objects;
import java.util.Random;

public class CardTransaction {
    private final CreditCard creditCard;
    private final TypeOfTransaction typeOfTransaction;
    private final long sum;

    private StatusOfTransaction status;
    private String detailsOfStatus;

    private static final Random rnd = new Random();
    private static final int RANDOM_STEP_TRANSACTION = 100;

    private CardTransaction(CreditCard creditCard, TypeOfTransaction typeOfTransaction, long sum) {
        Objects.requireNonNull(this.creditCard = creditCard);
        Objects.requireNonNull(this.typeOfTransaction = typeOfTransaction);
        this.sum = sum;
        this.status = StatusOfTransaction.OK;
    }

    public StatusOfTransaction getStatus() {
        return status;
    }

    public String getDetailsOfStatus() {
        return detailsOfStatus;
    }

    public long getSum() {
        return sum;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public TypeOfTransaction getTypeOfTransaction() {
        return typeOfTransaction;
    }

    public void setStatus(StatusOfTransaction status) {
        this.status = status;
    }

    public void setDetailsOfStatus(String detailsOfStatus) {
        this.detailsOfStatus = detailsOfStatus;
    }

    public static CardTransaction makeTransaction(CreditCard creditCard,
                                                  TypeOfTransaction typeOfTransaction,
                                                  long sum) {
        return new CardTransaction(creditCard, typeOfTransaction, sum);
    }

    public static CardTransaction generateRandomTransaction(CreditCard creditCard,
                                                            TypeOfTransaction typeOfTransaction,
                                                            int randomStepOfTransaction) {
        return makeTransaction(creditCard,
                typeOfTransaction,
                (rnd.nextInt(2) + 1) == 1 ?
                        rnd.nextInt(randomStepOfTransaction) * 10 :
                        rnd.nextInt(randomStepOfTransaction));
    }

    public static CardTransaction generateRandomTransaction(CreditCard creditCard,
                                                            TypeOfTransaction typeOfTransaction) {
        return generateRandomTransaction(creditCard, typeOfTransaction, RANDOM_STEP_TRANSACTION);
    }

    public static CardTransaction[] generateRandomTransactions(CreditCard creditCard,
                                                               TypeOfTransaction typeOfTransaction,
                                                               int countOfTransactions,
                                                               int randomStepOfTransaction) {
        CardTransaction[] randomTransactions = new CardTransaction[countOfTransactions];

        for(int i = 0; i < countOfTransactions; i++) {
            randomTransactions[i] = generateRandomTransaction(creditCard, typeOfTransaction, randomStepOfTransaction);
        }

        return randomTransactions;
    }

    public static CardTransaction[] generateRandomTransactions(CreditCard creditCard,
                                                               TypeOfTransaction typeOfTransaction,
                                                               int countOfTransactions) {
        return generateRandomTransactions(creditCard, typeOfTransaction, countOfTransactions, RANDOM_STEP_TRANSACTION);
    }

    public static CardTransaction[] generateRandomTransactionsFromCards(CreditCard[] creditCards,
                                                                        TypeOfTransaction typeOfTransaction,
                                                                        int randomStepOfTransaction) {
        CardTransaction[] randomTransactions = new CardTransaction[creditCards.length];

        for(int i = 0; i < creditCards.length; i++) {
            randomTransactions[i] = generateRandomTransaction(creditCards[i], typeOfTransaction, randomStepOfTransaction);
        }

        return randomTransactions;
    }

    public static CardTransaction[] generateRandomTransactionsFromCards(CreditCard[] creditCards,
                                                                        TypeOfTransaction typeOfTransaction) {
        return generateRandomTransactionsFromCards(creditCards, typeOfTransaction, RANDOM_STEP_TRANSACTION);
    }

    public enum TypeOfTransaction {
        PUT("PUT"),
        GET("GET");

        String value;

        TypeOfTransaction(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum StatusOfTransaction {
        OK("OK"),
        FAIL("FAIL");

        private final String value;

        StatusOfTransaction(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public void checkAndSetStatus(String reasonIfStatusFalse) {
        switch (typeOfTransaction) {
            case PUT:
                status = creditCard.canPutMoney(sum) ? StatusOfTransaction.OK : StatusOfTransaction.FAIL;
                break;
            case GET:
                status = creditCard.canGetSum(sum) ? StatusOfTransaction.OK : StatusOfTransaction.FAIL;
                break;
            default:
                throw new IllegalArgumentException("Unknown type of transaction");
        }

        if(status.equals(StatusOfTransaction.FAIL)) {
            setDetailsOfStatus(reasonIfStatusFalse);
        }
    }

    public void execute() {
        switch (typeOfTransaction) {
            case PUT:
                creditCard.putMoney(sum);
                break;
            case GET:
                creditCard.getMoney(sum);
                break;
            default:
                throw new IllegalArgumentException("Unknown type of transaction");
        }
    }
}
