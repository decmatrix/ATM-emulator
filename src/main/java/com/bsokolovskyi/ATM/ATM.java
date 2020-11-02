package com.bsokolovskyi.ATM;

import com.bsokolovskyi.CreditCards.CardTransaction;
import com.bsokolovskyi.CreditCards.CreditCard;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.bsokolovskyi.postBoxUtils.PostBox;

import java.util.*;

public class ATM{
    private static final Logger logger = LogManager.getLogger("ATM");
    private static final Random rnd = new Random();
    private static final int RANDOM_COUNT_OF_DENOMINATIONS = 100;

    private final PostBox<CardTransaction> postBoxContext;
    private CardTransaction[] cardTransactions = null;
    private Map<Denomination, Long> moneyStorage = null;

    private ATM(PostBox<CardTransaction> postBoxContext) {
        this.postBoxContext = postBoxContext;
    }

    public enum Denomination {
        _1(1),
        _2(2),
        _5(5),
        _10(10),
        _20(20),
        _50(50),
        _100(100);

        private final long val;

        Denomination(long val) {
            this.val = val;
        }

        public long getVal() {
            return val;
        }
    }

    public static class Builder {
        private int countOfDenominations1 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations2 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations5 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations10 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations20 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations50 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);
        private int countOfDenominations100 = rnd.nextInt(RANDOM_COUNT_OF_DENOMINATIONS);

        private final PostBox<CardTransaction> postBoxContext;
        private CardTransaction[] cardTransactions;

        public Builder(PostBox<CardTransaction> postBoxContext) {
            this.postBoxContext = postBoxContext;
        }

        public Builder setDenomination1(int count) {
            this.countOfDenominations1 = count;
            return this;
        }

        public Builder setDenomination2(int count) {
            this.countOfDenominations2 = count;
            return this;
        }

        public Builder setDenomination5(int count) {
            this.countOfDenominations5 = count;
            return this;
        }

        public Builder setDenomination10(int count) {
            this.countOfDenominations10 = count;
            return this;
        }

        public Builder setDenomination20(int count) {
            this.countOfDenominations20 = count;
            return this;
        }

        public Builder setDenomination50(int count) {
            this.countOfDenominations50 = count;
            return this;
        }

        public Builder setDenomination100(int count) {
            this.countOfDenominations100 = count;
            return this;
        }

        public Builder setCardTransactions(CardTransaction[] cardTransactions) {
            this.cardTransactions = cardTransactions;
            return this;
        }

        public ATM build() {
            ATM atm = new ATM(postBoxContext);
            atm.cardTransactions = cardTransactions;
            atm.moneyStorage = new HashMap<>();

            atm.moneyStorage.put(Denomination._1, Denomination._1.getVal() * countOfDenominations1);
            atm.moneyStorage.put(Denomination._2, Denomination._2.getVal() * countOfDenominations2);
            atm.moneyStorage.put(Denomination._5, Denomination._5.getVal() * countOfDenominations5);
            atm.moneyStorage.put(Denomination._10, Denomination._10.getVal() * countOfDenominations10);
            atm.moneyStorage.put(Denomination._20, Denomination._20.getVal() * countOfDenominations20);
            atm.moneyStorage.put(Denomination._50, Denomination._50.getVal() * countOfDenominations50);
            atm.moneyStorage.put(Denomination._100, Denomination._100.getVal() * countOfDenominations100);

            return atm;
        }
    }

    public void outInfoAboutCreditCards(String point) {
        if(cardTransactions != null) {
            for(CardTransaction transaction : cardTransactions) {
                logger.info(String.format("%s | %s", point, transaction.getCreditCard()));
            }
        }
    }

    public long getCurrentBalanceOfMoneyStorage() {
        long sum = 0;

        if(moneyStorage != null) {
            for (Map.Entry<Denomination, Long> entry : moneyStorage.entrySet()) {
                sum += entry.getValue();
            }
        }

        return sum;
    }

    public void runATM() {
        outInfoAboutCreditCards("<start>");
        logger.info(String.format("<start> | balance of bank: %s", getCurrentBalanceOfMoneyStorage()));

        try {
            Thread checkerThread = new Thread(new Checker());
            Thread executorThread = new Thread(new ATM.Executor());

            checkerThread.start();
            executorThread.start();

            checkerThread.join();
            executorThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        outInfoAboutCreditCards("<end>");
        logger.info(String.format("<end> | balance of bank: %s", getCurrentBalanceOfMoneyStorage()));
    }

    private WithdrawMoneyResult calculateDenominationsForWithdrawSum(CardTransaction transaction) {
        WithdrawMoneyResult result = null;

        for(Map.Entry<Denomination, Long> entry : moneyStorage.entrySet()) {
            Denomination key = entry.getKey();
            long keyValue = key.getVal();
            long value = entry.getValue();
            long sum = transaction.getSum();

            if(sum <= value && ((value - sum) % keyValue == 0)) {
                entry.setValue(value - sum);
                result = new WithdrawMoneyResult(key, sum / keyValue);
                break;
            }
        }

        return result;
    }

    private class Checker implements Runnable {
        private final Logger logger = LogManager.getLogger("Checker");

        @Override
        public void run() {
            CardTransaction currentTransaction;
            for (CardTransaction cardTransaction : cardTransactions) {

                currentTransaction = cardTransaction;
                currentTransaction.checkAndSetStatus("withdrawing sum more than balance");

                while (true) {
                    if (!postBoxContext.isFull()) break;
                }

                postBoxContext.sendMessage(currentTransaction);
                outReportAboutTransaction(currentTransaction);
            }

            postBoxContext.closePostBox();
        }

        private void outReportAboutTransaction(CardTransaction cardTransaction) {
            String detailOfStatus = cardTransaction.getDetailsOfStatus();
            CreditCard creditCard = cardTransaction.getCreditCard();

            logger.info(String.format("<send message> | card name: %s, sum: %s, type of transaction: %s, balance %s, status: %s, details: %s",
                    creditCard.getNameOfCard(),
                    cardTransaction.getSum(),
                    cardTransaction.getTypeOfTransaction(),
                    creditCard.getBalance(),
                    cardTransaction.getStatus(),
                    detailOfStatus != null ? detailOfStatus : "<none>"));
        }
    }

    private class Executor implements Runnable {
        private final Logger logger = LogManager.getLogger("Executor");

        @Override
        public void run() {
            while (!postBoxContext.isEmpty() || !postBoxContext.postBoxClosed()) {

                while (true) {
                    if (!postBoxContext.isEmpty()) break;
                }

                CardTransaction currentTransaction = postBoxContext.nextMessage();

                if(currentTransaction.getStatus().equals(CardTransaction.StatusOfTransaction.OK)) {
                    WithdrawMoneyResult denominationsForWithdrawSum = calculateDenominationsForWithdrawSum(currentTransaction);

                    if (denominationsForWithdrawSum != null) {
                        currentTransaction.execute();
                    } else {
                        currentTransaction.setStatus(CardTransaction.StatusOfTransaction.FAIL);
                        currentTransaction.setDetailsOfStatus("no have suitable denominations for withdraw this sum");
                    }

                    outReportAboutTransaction(currentTransaction, denominationsForWithdrawSum);
                } else {
                    outReportAboutTransaction(currentTransaction, null);
                }
            }
        }

        private void outReportAboutTransaction(CardTransaction cardTransaction, WithdrawMoneyResult denominations) {
            String detailOfStatus = cardTransaction.getDetailsOfStatus();
            CreditCard creditCard = cardTransaction.getCreditCard();

            logger.info(String.format("<get message> | card name: %s, Sum: %s, type of transaction: %s, balance: %s, status: %s, details: %s, denominations for transactions: %s",
                    creditCard.getNameOfCard(),
                    cardTransaction.getSum(),
                    cardTransaction.getTypeOfTransaction(),
                    creditCard.getBalance(),
                    cardTransaction.getStatus(),
                    detailOfStatus != null ? detailOfStatus : "<none>",
                    denominations != null ? denominations : "<none>"));
        }
    }
}
