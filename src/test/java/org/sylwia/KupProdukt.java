package org.sylwia;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KupProdukt {
    private WebDriver webDriver;
    private WebDriverWait wait;
    private String expectedOrderReference;
    private String expectedTotalPrice;

    @Before
    @Given("Uzytkownik otworzy przegladarke")
    public void uzytkownik_otworzy_przegladarke() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\WebDrivers\\chromedriver.exe");

        webDriver = new ChromeDriver();

        wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
    }

    @After
    public void uzytkownik_zamknie_przegladarke() {
        webDriver.quit();
    }

    @Given("Uzytkownik przejdzie do strony logowania")
    public void uzytkownik_otwiera_strone_logowania() {
        webDriver.get("https://mystore-testlab.coderslab.pl/index.php?controller=authentication&back=my-account");
    }

    @And("Uzytkownik wprowadzi {string} jako email")
    public void uzytkownik_wprowadzi_email(String email) {
        webDriver.findElement(By.name("email"))
                .sendKeys(email);
    }

    @And("Uzytkownik wprowadzi {string} jako haslo")
    public void uzytkownik_wprowadzi_haslo(String haslo) {
        webDriver.findElement(By.name("password"))
                .sendKeys(haslo);
    }

    @And("Uzytkownik kinkine w przycisk SIGN IN")
    public void uzytkownik_kinkine_przycisk_sign_in() {
        webDriver.findElement(By.id("submit-login"))
                .click();
    }

    @When("Uzytkownik przechodzi do strony produktu {string}")
    public void uzytkownik_przechodzi_do_strony_produktu(String nazwaProduktu) {
        var searchBox = webDriver.findElement(By.name("s"));
        searchBox.sendKeys(nazwaProduktu);
        searchBox.submit();

        var productLink = webDriver.findElement(By.linkText(nazwaProduktu));
        productLink.click();
    }

    @And("Uzytkownik sprawdzi czy rabat na niego wynosi {string}%")
    public void uzytkownik_sprawdzi_czy_rabat_wynosi(String oczekiwanyRabat) {
        var expectedDiscount = String.format("SAVE %s%%", oczekiwanyRabat);
        var productDiscount = webDriver.findElement(By.cssSelector("span.discount-percentage"))
                .getText();

        assertEquals(expectedDiscount, productDiscount, "Rabat nie jest zgodny z oczekiwaniami");
    }

    @And("Uzytkownik wybierze rozmiar {string}")
    public void uzytkownik_wybierze_rozmiar(String rozmiar) {
        var rozmiarDropdown = new Select(webDriver.findElement(By.id("group_1")));
        rozmiarDropdown.selectByVisibleText(rozmiar);
    }

    @And("Uzytkownik wybierze {string} sztuk")
    public void uzytkownik_wybierze_ile_sztuk(String ilosc) {
        var quantityElement = webDriver.findElement(By.id("quantity_wanted"));

        quantityElement.clear();

        if (!quantityElement.getAttribute("value").isEmpty()) {
            quantityElement.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        }

        quantityElement.sendKeys(ilosc);
    }

    @And("Uzytkownik dodaje produkt do koszyka")
    public void uzytkownik_dodaje_produkt_do_koszyka() {
        var addToCartButton = webDriver.findElement(By.cssSelector("button.add-to-cart"));
        addToCartButton.click();
    }

    @And("Uzytkownik przechodzi do opcji checkout")
    public void uzytkownik_przechodzi_do_opcji_checkout() {
        var xpath = "//a[contains(text(),'Proceed to checkout')]";
        var checkoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        checkoutButton.click();
    }

    @And("Uzytkownik przechodzi do opcji checkout drugi krok")
    public void uzytkownik_przechodzi_do_opcji_checkout_drugi_krok() {
        var xpath = "//a[contains(text(),'Proceed to checkout')]";
        var checkoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        checkoutButton.click();
    }

    @And("Uzytkownik potwierdza adres")
    public void uzytkownik_potwierdza_adres() {
        var continueButton = webDriver.findElement(By.name("confirm-addresses"));
        continueButton.click();
    }

    @And("Uzytkownik wybiera metode odbioru PrestaShop pick up in store")
    public void uzytkownik_wybiera_metode_odbioru() {
        var pickUpInStoreOption = webDriver.findElement(By.id("delivery_option_8"));

        if (!pickUpInStoreOption.isSelected()) {
            pickUpInStoreOption.click();
        }

        var continueButton = webDriver.findElement(By.name("confirmDeliveryOption"));
        continueButton.click();
    }

    @And("Uzytkownik wybiera opcje platnosci Pay by Check")
    public void uzytkownik_wybiera_opcje_platnosci_pay_by_check() {
        var payByCheckOption = webDriver.findElement(By.id("payment-option-1"));

        if (!payByCheckOption.isSelected()) {
            payByCheckOption.click();
        }

        var termsCheckbox = webDriver.findElement(By.id("conditions_to_approve[terms-and-conditions]"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }

        var placeOrderButton = webDriver.findElement(By.cssSelector("#payment-confirmation .btn-primary"));
        placeOrderButton.click();
    }

    @Then("Uzytkownik robi screenshot z potwierdzeniem zamowienia i kwota")
    public void uzytkownik_robi_screenshot_z_potwierdzeniem_zamowienia_i_kwota() {
        var orderRefElement = webDriver.findElement(By.id("order-reference-value"));
        expectedOrderReference = orderRefElement.getText().replace("Order reference: ", "");

        var totalPriceElement = webDriver.findElement(By.xpath("//tr[@class='total-value font-weight-bold']/td[2]"));
        expectedTotalPrice = totalPriceElement.getText();

        var scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);

        try {
            String resourcePath = "src/test/resources/screenshots";
            String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
            File destFile = new File(resourcePath, fileName);

            FileUtils.copyFile(scrFile, destFile);
            System.out.println("Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error while saving screenshot: " + e.getMessage());
        }
    }

    @And("Uzytkownik wchodzi na strone konta uzytkownika")
    public void uzytkownik_wchodzi_na_strone_konta_uzytkownika() {
        WebElement accountLink = webDriver.findElement(By.cssSelector("a.account"));
        accountLink.click();
    }

    @And("Uzytkownik wchodzi w historie zamowien")
    public void uzytkownik_wchodzi_w_historie_zamowien() {
        var orderHistoryLink = webDriver.findElement(By.id("history-link"));
        orderHistoryLink.click();
    }

    @And("Uzytkownik weryfikuje zamowienie w historii")
    public void uzytkownik_weryfikuje_zamowienie_w_historii() {
        var orderRows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".table tbody tr")));

        boolean orderFound = false;
        for (WebElement row : orderRows) {
            var reference = row.findElement(By.xpath("th[1]")).getText();
            var price = row.findElement(By.xpath("td[2]")).getText(); // First 'td' is actually the third column
            var status = row.findElement(By.xpath("td[4]")).getText().trim();

            if (reference.equals(expectedOrderReference) && status.equals("Awaiting check payment")) {
                assertEquals(expectedTotalPrice, price, "Total price does not match");
                orderFound = true;
                break;
            }
        }

        assertTrue(orderFound, "Order with reference " + expectedOrderReference + " and price " + expectedTotalPrice + " not found or status is incorrect.");
    }
}
