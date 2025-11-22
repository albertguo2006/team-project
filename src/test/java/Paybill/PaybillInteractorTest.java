package Paybill;

import data_access.Paybill.InMemoryPaybillDataAccessObject;
import entity.Bill;
import entity.Player;
import io.opencensus.internal.DefaultVisibilityForTesting;
import use_case.paybills.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

class PaybillInteractorTest {

    @Test
    void successPaySingleBillTest(){
        InMemoryPaybillDataAccessObject paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(1000.0);

        // Create test bills
        Bill bill1 = new Bill("rent_bill", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        Bill bill2 = new Bill("electricity_bill", 300.0, "Electricity", new Date(), false,
                Bill.BillType.ELECTRICITY);
        paybillRepository.saveBill(bill1);
        paybillRepository.saveBill(bill2);

        // Create success presenter
        PaybillOutputBoundary successPresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                assertTrue(paybillOutputData.isSuccess());
                assertEquals("Paid: Rent", paybillOutputData.getMessage());
                assertEquals(500.0, paybillOutputData.getAmount(), 0.01); // Total amount paid
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                fail("Should not reach failure case");
            }
        };
        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, successPresenter, player);
        interactor.paySingleBill("rent_bill");

        // Verify only the specific bill is paid
        Bill paidBill = paybillRepository.getBillById("rent_bill");
        assertTrue(paidBill.getPaid());

        Bill unpaidBill = paybillRepository.getBillById("electricity_bill");
        assertFalse(unpaidBill.getPaid());

        // Verify player balance is updated
        assertEquals(500.0, player.getBalance(), 0.01);

    }

    @Test
    void successPayAllBillsTest(){
        InMemoryPaybillDataAccessObject paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(5000.0);

        // Create test bills
        Bill bill1 = new Bill("bill1", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        Bill bill2 = new Bill("bill2", 300.0, "Electricity", new Date(), false,
                Bill.BillType.ELECTRICITY);
        paybillRepository.saveBill(bill1);
        paybillRepository.saveBill(bill2);

        // Create success presenter
        PaybillOutputBoundary successPresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                assertTrue(paybillOutputData.isSuccess());
                assertEquals("All bills paid successfully!", paybillOutputData.getMessage());
                assertEquals(800.0, paybillOutputData.getAmount(), 0.01); // Total amount paid
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                fail("Should not reach failure case");
            }
        };
        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, successPresenter, player);
        interactor.payAllBills();

        // Verify bills are marked as paid
        List<Bill> unpaidBills = paybillRepository.getUnpaidBills();
        assertTrue(unpaidBills.isEmpty());

        // Verify player balance is updated
        assertEquals(4200.0, player.getBalance(), 0.01);

    }
}
