Feature: Testowanie funkcjonalosci zakupu produktu

  Scenario Outline: Zakup wybranego produktu
    Given Uzytkownik otworzy przegladarke
    And Uzytkownik przejdzie do strony logowania
    And Uzytkownik wprowadzi "sylwiatesty@pl" jako email
    And Uzytkownik wprowadzi "Testy@123" jako haslo
    And Uzytkownik kinkine w przycisk SIGN IN
    When Uzytkownik przechodzi do strony produktu "<produkt>"
    And Uzytkownik sprawdzi czy rabat na niego wynosi "<rabat>"%
    And Uzytkownik wybierze rozmiar "<rozmiar>"
    And Uzytkownik wybierze "<ilosc>" sztuk
    And Uzytkownik dodaje produkt do koszyka
    And Uzytkownik przechodzi do opcji checkout
    And Uzytkownik przechodzi do opcji checkout drugi krok
    And Uzytkownik potwierdza adres
    And Uzytkownik wybiera metode odbioru PrestaShop pick up in store
    And Uzytkownik wybiera opcje platnosci Pay by Check
    Then Uzytkownik robi screenshot z potwierdzeniem zamowienia i kwota
    And Uzytkownik wchodzi na strone konta uzytkownika
    And Uzytkownik wchodzi w historie zamowien
    And Uzytkownik weryfikuje zamowienie w historii

    Examples:
      | produkt                     | rabat | rozmiar | ilosc |
      | Hummingbird Printed Sweater | 20    | S       | 5     |
      | Hummingbird Printed Sweater | 20    | M       | 4     |
      | Hummingbird Printed Sweater | 20    | L       | 3     |
      | Hummingbird Printed Sweater | 20    | XL      | 2     |