package com.bsokolovskyi.ATM;

public class WithdrawMoneyResult {
    private final ATM.Denomination denomination;
    private final long count;

    public WithdrawMoneyResult(ATM.Denomination denomination, long count) {
        this.denomination = denomination;
        this.count = count;
    }

    public ATM.Denomination getDenomination() {
        return denomination;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.format("type - %s, count - %s", denomination.getVal(), count);
    }
}
