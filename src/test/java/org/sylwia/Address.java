package org.sylwia;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class Address {
    private WebDriver webDriver;

    @Before
    @Given("Uzytkownik otworzy okno przegladarki")
    public void uzytkownik_otworzy_przegladarke() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\WebDrivers\\chromedriver.exe");

        webDriver = new ChromeDriver();
    }

    @After
    public void uzytkownik_zamknie_przegladarke() {
        webDriver.quit();
    }

    @And("Uzytkownik otwiera strone logowania")
    public void uzytkownik_otwiera_strone_logowania() {
        webDriver.get("https://mystore-testlab.coderslab.pl/index.php?controller=authentication&back=my-account");
    }

    @When("Uzytkownik wpisuje poprawne dane logowania")
    public void uzytkownik_wpisuje_poprawne_dane_logowania() {
        webDriver.findElement(By.name("email"))
                .sendKeys("sylwiatesty@pl");

        webDriver.findElement(By.name("password"))
                .sendKeys("Testy@123");

        webDriver.findElement(By.id("submit-login")).click();
    }


    @And("Uzytkownik przechodzi do zakladki Addresses")
    public void uzytkownik_przechodzi_do_zakladki_addresses() {
        var addressesLink = webDriver.findElement(By.id("addresses-link"));
        addressesLink.click();
    }

    @And("Uzytkownik kliknie w + Create new address")
    public void uzytkownik_kliknie_create_new_address() {
        var createAddressButton = webDriver.findElement(By.cssSelector("a[data-link-action='add-address']"));
        createAddressButton.click();
    }

    @And("Uzytkownik wypelnia formularz adresu z {string}, {string}, {string}, {string}, {string}, {string}")
    public void uzytkownik_wypelnia_formularz_adresu(String alias, String address, String city, String zip, String country, String phone) {
        webDriver.findElement(By.id("field-alias")).sendKeys(alias);
        webDriver.findElement(By.id("field-address1")).sendKeys(address);
        webDriver.findElement(By.id("field-city")).sendKeys(city);
        webDriver.findElement(By.id("field-postcode")).sendKeys(zip);
        new Select(webDriver.findElement(By.id("field-id_country"))).selectByVisibleText(country);
        webDriver.findElement(By.id("field-phone")).sendKeys(phone);
        webDriver.findElement(By.cssSelector(".form-footer button[type='submit']")).click();
    }

    @Then("System potwierdza dodanie nowego adresu i przekierowuje do listy adresow")
    public void nowy_adres_zostaje_dodany() {
        var isSuccessMessagePresent = webDriver.findElement(By.cssSelector("article.alert-success"))
                .getText()
                .contains("Address successfully added!");

        assertTrue(isSuccessMessagePresent, "Błąd: success alert nie zostal wyswietlony");

        System.out.println("success alert zostal wyswietlony");

    }

    @And("Uzytkownik sprawdza, czy wszystkie szczegoly adresu {string}, {string}, {string}, {string}, {string}, {string} sa zgodne z wprowadzonymi danymi")
    public void uzytkownik_sprawdza_czy_wszystkie_szczegoly_adresu_sa_zgodne_z_wprowadzonymi_danymi(String alias, String address, String city, String zip, String country, String phone) {
        String xpath = String.format("//div[@class='address-body']/h4[text()='%s']/following-sibling::address", alias);

        var addressElements = webDriver.findElements(By.xpath(xpath));

        for (WebElement addressElement : addressElements) {
            var actualAddressParts = addressElement.getText().split("\n");

            assertEquals(address, actualAddressParts[1], "Address nie jest zgodny z oczekiwaniami");
            assertEquals(city, actualAddressParts[2], "City nie jest zgodny z oczekiwaniami");
            assertEquals(zip, actualAddressParts[3], "Zip nie jest zgodny z oczekiwaniami");
            assertEquals(country, actualAddressParts[4], "Country nie jest zgodny z oczekiwaniami");
            assertEquals(phone, actualAddressParts[5], "Phone nie jest zgodny z oczekiwaniami");
        }
    }

    @And("Uzytkownik usuwa adres o aliasie {string}")
    public void uzytkowni_usuwa_adres_o_aliasie(String alias) {
        String deleteButtonXpath = String.format("//div[@class='address-body']/h4[text()='%s']/ancestor::article//a[contains(@data-link-action, 'delete-address')]", alias);
        WebElement deleteButton = webDriver.findElement(By.xpath(deleteButtonXpath));
        deleteButton.click();
    }

    @Then("System potwierdza usuniecie adresu o aliasie {string} i przekierowuje do listy adresow")
    public void system_potwierdza_usuniecie_adresu(String alias) {
        var isSuccessMessagePresent = webDriver.findElement(By.cssSelector("article.alert-success"))
                .getText()
                .contains("Address successfully deleted!");

        assertTrue(isSuccessMessagePresent, "Błąd: success alert nie zostal wyswietlony");

        System.out.println("success alert zostal wyswietlony");

        var addressElements = webDriver.findElements(By.xpath("//div[@class='address-body']/h4"));

        var isAliasPresent = addressElements.stream()
                .anyMatch(element -> element.getText().equals(alias));

        assertFalse(isAliasPresent, "Błąd: Adres o aliasie [" + alias + "] nadal widoczny na liście");
    }
}
