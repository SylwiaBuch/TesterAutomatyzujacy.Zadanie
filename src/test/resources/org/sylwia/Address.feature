Feature: Testowanie funkcjonalości adresów

  Scenario Outline: Zarzadzanie adresem
    Given Uzytkownik otworzy okno przegladarki
    And Uzytkownik otwiera strone logowania
    When Uzytkownik wpisuje poprawne dane logowania
    And Uzytkownik przechodzi do zakladki Addresses
    And Uzytkownik kliknie w + Create new address
    And Uzytkownik wypelnia formularz adresu z "<alias>", "<address>", "<city>", "<zip>", "<country>", "<phone>"
    Then System potwierdza dodanie nowego adresu i przekierowuje do listy adresow
    And Uzytkownik sprawdza, czy wszystkie szczegoly adresu "<alias>", "<address>", "<city>", "<zip>", "<country>", "<phone>" sa zgodne z wprowadzonymi danymi
    And Uzytkownik usuwa adres o aliasie "<alias>"
    Then System potwierdza usuniecie adresu o aliasie "<alias>" i przekierowuje do listy adresow

    Examples:
      | alias  | address              | city       | zip     | country        | phone      |
      | Home   | 123 Baker Street     | London     | NW1 6XE | United Kingdom | 07123456789 |
      | Office | 200 Business Park Rd | Manchester | M15 4FN | United Kingdom | 07234567890 |