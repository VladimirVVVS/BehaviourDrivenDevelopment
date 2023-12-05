package ru.netology.web.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static ru.netology.web.data.DataHelper.*;


public class MoneyTransferTest {

    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode();
        dashboardPage = verificationPage.validVerify(verificationCode);

    }

    @Test
    void shouldTransferMoneyFromFirstCardToSecond() {
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = getValidAmount(firstCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(getSecondCardInfo());
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), getFirstCardInfo());
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var actualBalanceFirstCard = dashboardPage.getCardBalance(getFirstCardInfo());
        var actualBalanceSecondCard = dashboardPage.getCardBalance(getSecondCardInfo());
        Assertions.assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        Assertions.assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCardInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCardInfo());
        var amount = getInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(getFirstCardInfo());
        transferPage.makeTransfer(String.valueOf(amount), getSecondCardInfo());
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(getFirstCardInfo());
        var actualBalanceSecondCard = dashboardPage.getCardBalance(getSecondCardInfo());
        Assertions.assertEquals(firstCardBalance, actualBalanceFirstCard);
        Assertions.assertEquals(secondCardBalance, actualBalanceSecondCard);
    }

}