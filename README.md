# Databázový systém zaměstnanců

Tento projekt je konzolová aplikace napsaná v Javě, která slouží ke správě zaměstnanců technologické firmy. Systém umožňuje evidovat zaměstnance, mapovat jejich vzájemné vztahy (úroveň spolupráce) a na základě těchto dat provádět pokročilé analýzy.

## 👥 Role zaměstnanců

Každý zaměstnanec je při přijetí do firmy zařazen do jedné ze dvou specializovaných skupin. Každá skupina disponuje unikátní dovedností:

* **Datoví analytici:** Dokážou analyzovat podnikovou síť a určit, se kterým kolegou mají nejvíce společných spolupracovníků (průnik množin kontaktů).
* **Bezpečnostní specialisté:** Vyhodnocují rizikovost spolupráce na základě počtu vazeb a průměrné kvality spolupráce. Vypočítávají vlastní rizikové skóre sítě daného zaměstnance.

## ⚙️ Hlavní funkcionality

* Přidání nového zaměstnance s automatickým generováním ID.
* Zaznamenání spolupráce mezi dvěma zaměstnanci (s hodnocením: *špatná, průměrná, dobrá*).
* Kompletní odebrání zaměstnance ze systému (včetně smazání všech jeho vazeb u ostatních kolegů).
* Vyhledávání zaměstnanců podle ID s výpisem detailních informací.
* Spouštění speciálních dovedností podle zařazení do skupiny (polymorfismus).
* Abecední výpisy, globální statistiky sítě a export/import dat ze souborů a SQL databáze.
