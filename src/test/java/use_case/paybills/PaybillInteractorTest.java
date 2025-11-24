package use_case.paybills;

import data_access.Paybill.InMemoryPaybillDataAccessObject;
import entity.Bill;
import entity.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

class PaybillInteractorTest {

    @Test
    void successPaySingleBillTest(){
        InMemoryPaybillDataAccessObject paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(1000.0);

        // Create test bills
        Bill bill1 = new Bill("001", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        Bill bill2 = new Bill("002", 300.0, "Electricity", new Date(), false,
                Bill.BillType.ELECTRICITY);
        paybillRepository.saveBill(bill1);
        paybillRepository.saveBill(bill2);

        // Create success presenter
        PaybillOutputBoundary successPresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                assertTrue(paybillOutputData.isSuccess());
                assertEquals("Paid: " + bill1.getName(), paybillOutputData.getMessage());
                assertEquals(500.0, paybillOutputData.getAmount(), 0.01); // Total amount paid
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                fail("Should not reach failure case");
            }
        };

        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, successPresenter, player);
        interactor.paySingleBill("001");

        // Verify only the specific bill is paid
        Bill paidBill = paybillRepository.getBillById("001");
        assertTrue(paidBill.getPaid());

        Bill unpaidBill = paybillRepository.getBillById("002");
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
        Bill bill1 = new Bill("123", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        Bill bill2 = new Bill("456", 300.0, "Electricity", new Date(), false,
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

    @Test
    void failureInsufficientFundsPayAllTest(){
        PaybillDataAccessInterface paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(100.0); // Not enough

        Bill bill = new Bill("0012", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        paybillRepository.saveBill(bill);

        PaybillOutputBoundary failurePresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                fail("Should not reach success case");
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                assertFalse(paybillOutputData.isSuccess());
                assertEquals("Insufficient funds!", paybillOutputData.getMessage());
                assertEquals(500.0, paybillOutputData.getAmount(), 0.01);
            }
        };

        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, failurePresenter, player);
        interactor.payAllBills();

        Bill  unpaidBill = paybillRepository.getBillById("0012");
        assertFalse(unpaidBill.getPaid());
        assertEquals(100.0, player.getBalance(), 0.01);
    }

    @Test
    void failureInsufficientFundsPaySingleTest(){
        PaybillDataAccessInterface paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(100.0); // Not enough

        Bill bill = new Bill("0012", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        paybillRepository.saveBill(bill);

        PaybillOutputBoundary failurePresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                fail("Should not reach success case");
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                assertFalse(paybillOutputData.isSuccess());
                assertEquals("Insufficient funds to pay " + bill.getName(), paybillOutputData.getMessage());
                assertEquals(500.0, paybillOutputData.getAmount(), 0.01);
            }
        };

        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, failurePresenter, player);
        interactor.paySingleBill("0012");

        Bill  unpaidBill = paybillRepository.getBillById("0012");
        assertFalse(unpaidBill.getPaid());
        assertEquals(100.0, player.getBalance(), 0.01);
    }

    @Test
    void failureBillNotFoundTest(){
        PaybillDataAccessInterface paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(1000.0);

        PaybillOutputBoundary failurePresenter =  new PaybillOutputBoundary(){
            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                fail("Should not reach success case");
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                assertFalse(paybillOutputData.isSuccess());
                assertEquals("Bill not found!", paybillOutputData.getMessage());
            }
        };

        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, failurePresenter, player);
        interactor.paySingleBill("1803801");

        assertEquals(1000.0, player.getBalance(), 0.01);
    }

    @Test
    void failureBillAlreadyPaidTest(){
        PaybillDataAccessInterface paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        player.setBalance(1000.0);

        // Create a bill that's already paid
        Bill paidBill = new Bill("6767", 200.0, "Internet", new Date(), true, Bill.BillType.INTERNET);
        paybillRepository.saveBill(paidBill);

        PaybillOutputBoundary failurePresenter =  new PaybillOutputBoundary(){

            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {
                fail("Should not reach success case");
            }

            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {
                assertFalse(paybillOutputData.isSuccess());
                assertEquals("Bill already paid: " + paidBill.getName(), paybillOutputData.getMessage());
            }
        };

        PaybillInputBoundary interactor = new PaybillInteractor(paybillRepository, failurePresenter, player);
        interactor.paySingleBill("6767");
        assertEquals(1000.0, player.getBalance(), 0.01);
    }

    @Test
    void testFindBillById_LineCoverage() throws Exception {
        // Setup
        InMemoryPaybillDataAccessObject paybillRepository = new InMemoryPaybillDataAccessObject();
        Player player = new Player("TestPlayer");
        PaybillOutputBoundary presenter = new PaybillOutputBoundary() {
            @Override
            public void prepareSuccessView(PaybillOutputData paybillOutputData) {}
            @Override
            public void prepareFailureView(PaybillOutputData paybillOutputData) {}
        };
        PaybillInteractor interactor = new PaybillInteractor(paybillRepository, presenter, player);

        // Create test bills
        Bill bill1 = new Bill("001", 500.0, "Rent", new Date(), false, Bill.BillType.RENT);
        Bill bill2 = new Bill("002", 300.0, "Electricity", new Date(), false, Bill.BillType.ELECTRICITY);
        List<Bill> bills = List.of(bill1, bill2);

        // Use reflection to test private method
        Method findBillByIdMethod = PaybillInteractor.class.getDeclaredMethod("findBillById", List.class, String.class);
        findBillByIdMethod.setAccessible(true);

        // Test 1: Find existing bill (covers the if statement and return bill)
        Bill foundBill = (Bill) findBillByIdMethod.invoke(interactor, bills, "001");
        assertNotNull(foundBill);
        assertEquals("001", foundBill.getId());

        // Test 2: Find non-existing bill (covers the loop completion and return null)
        Bill notFoundBill = (Bill) findBillByIdMethod.invoke(interactor, bills, "999");
        assertNull(notFoundBill);
    }
}
