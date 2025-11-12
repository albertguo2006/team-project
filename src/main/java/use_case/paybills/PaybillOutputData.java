package use_case.paybills;

import entity.Bill;

import java.util.List;

public class PaybillOutputData {
    private final List<Bill> billList;
    private final boolean useCaseFailed;

    public PaybillOutputData(List<Bill> billList, boolean useCaseFailed) {
        this.billList = billList;
        this.useCaseFailed = useCaseFailed;
    }

    public List<Bill> getBillList() {
        return billList;
    }

    public boolean isUseCaseFailed() {
        return useCaseFailed;
    }
}
