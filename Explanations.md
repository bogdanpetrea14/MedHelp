# MedConnect — Descriere Proiect

Acest proiect implementează un sistem care face legătura între doctori și farmacii,
cu scopul de a ușura transmiterea rețetelor medicale și de a centraliza informațiile
medicale ale pacienților într-un mod sigur și accesibil.

---

## 1. Autentificare & Roluri

Sistemul are patru tipuri de utilizatori: **Admin**, **Doctor**, **Farmacie** și **Pacient**.

- **Pacientul** își creează singur un cont *(self-register)*, completând datele personale de bază (nume, prenume, email, parolă, data nașterii, CNP). După înregistrare, poate folosi imediat aplicația.

- **Doctorul** și **Farmacia** își creează și ele singure un cont, însă contul rămâne în stare *„în așteptare"* până când Adminul îl validează și îl activează. Până la validare, aceștia nu pot accesa funcționalitățile sistemului.
    - La înregistrare, doctorul va specifica **specialitatea medicală**.
    - Farmacia va specifica **datele de contact și locația**.

- **Adminul** este creat direct în baza de date (printr-un seed inițial) și are rolul de a valida și gestiona conturile doctorilor și farmaciilor.

---

## 2. Profilul Pacientului

Pacientul își creează singur contul și, după autentificare, poate vizualiza:

- Datele personale introduse în sistem (de el la înregistrare sau completate ulterior de doctor)
- Rețetele medicale prescrise și istoricul medical
- Alergiile și alte probleme de sănătate înregistrate de doctor
- Farmaciile din proximitatea sa care au toate medicamentele din rețeta activă pe stoc

> ⚠️ Pacientul **nu poate modifica** datele medicale (alergii, diagnostic, istoric) — acestea sunt introduse și gestionate exclusiv de doctor, pentru a preveni erori sau modificări neautorizate.

### Notificare prin email

Când i se prescrie o rețetă, pacientul primește automat un email de notificare care conține:
- Detaliile rețetei (medicamente prescrise, doze)
- **Codul unic de ridicare** generat pentru acea rețetă

---

## 3. Profilul Doctorului

Doctorul își creează un cont specificând specialitatea medicală. După validarea de către admin, poate:

- Crea și gestiona profilurile medicale ale pacienților (date medicale, alergii, istoric, diagnostice)
- Prescrie rețete medicale, cu **verificarea automată a alergiilor** pacientului înainte de confirmare
- Vizualiza istoricul complet al rețetelor prescrise unui pacient
- Vedea avertismentele generate de modulul AI privind interacțiunile medicamentoase

> Sistemul va **restricționa prescrierea** medicamentelor în afara specialității doctorului, pentru a preveni erori medicale.

---

## 4. Profilul Farmaciei

Farmacia își creează un cont cu datele de contact și locație. După validarea de către admin, poate:

- Gestiona stocul de medicamente (adăugare, editare, ștergere, actualizare cantitate)
- Accesa rețetele medicale ale unui pacient pe baza **codului unic de ridicare** sau a unor date specifice pacientului (ex: CNP + cod rețetă), fără a putea vedea rețetele altor pacienți — pentru protejarea confidențialității
- Marca o rețetă ca **ridicată** după eliberarea medicamentelor

> Stocul farmaciei este vizibil în timp real pentru pacienți, astfel încât aceștia să știe unde pot ridica medicamentele prescrise.

---

## 5. Modulul Admin

Adminul are acces la un panou de control prin care poate:

- Vizualiza și gestiona conturile în așteptare ale doctorilor și farmaciilor
- **Activa** sau **respinge** un cont (cu motiv de respingere)
- Gestiona utilizatorii existenți din sistem
- Vizualiza feedback-ul trimis de utilizatori

---

## 6. Codul Unic de Ridicare

La fiecare rețetă prescrisă se generează automat un **cod unic**. Acesta este:

- Trimis pacientului pe email împreună cu detaliile rețetei
- Folosit de farmacie pentru a accesa și valida rețeta
- **Single-use** — după ridicare, rețeta este marcată automat ca finalizată și codul devine invalid

---

## 7. Modulul AI — Interacțiuni Medicamentoase

Un modul AI analizează medicamentele dintr-o rețetă (și din rețetele recente ale pacientului) pentru a detecta potențiale interacțiuni periculoase.

- Dacă este detectată o interacțiune, doctorul primește un **avertisment clar** înainte de a confirma prescrierea
- Rețeta **poate fi prescrisă în continuare**, dar decizia finală aparține doctorului *(avertisment, nu blocare automată)*

---

## 8. Formular de Feedback

Toți utilizatorii autentificați (pacient, doctor, farmacie) au acces la un formular de feedback prin care pot raporta probleme, sugera îmbunătățiri sau evalua sistemul.

Formularul conține:

| Câmp | Tip | Detalii |
|------|-----|---------|
| Categoria | `select` | Bug / Sugestie / Altele |
| Evaluare generală | `radio` | Pozitiv / Neutru / Negativ |
| Acord contact | `checkbox` | Acordul utilizatorului de a fi contactat |
| Detalii | `textarea` | Text liber |

Feedback-ul este salvat în baza de date și vizibil pentru admin.

---

## 9. Date Actualizate în Timp Real

Stocul farmaciilor și statusul rețetelor *(prescrisă / ridicată)* sunt actualizate în timp real, astfel încât pacienții să aibă mereu informații corecte despre disponibilitatea medicamentelor.
