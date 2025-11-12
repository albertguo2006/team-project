package use_case.paybills;

public class PaybillInteractor implements PaybillInputBoundary {
    private final PaybillDataAccessInterface paybillDataAccess;
    private final PaybillOutputBoundary paybillPresenter;

    public PaybillInteractor(PaybillDataAccessInterface paybillDataAccess, PaybillOutputBoundary paybillPresenter) {
        this.paybillDataAccess = paybillDataAccess;
        this.paybillPresenter = paybillPresenter;
    }

    @Override
    public void execute() {
        // TODO: implement logic of paybill use case
        //* set respective bill to paid and update player balance
        //* instantiate the 'paybillOutputData' which needs to contain everything
        //* tell the presenter to prepare a success view
    }
}
