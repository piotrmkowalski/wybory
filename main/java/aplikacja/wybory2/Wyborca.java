package aplikacja.wybory2;

import javax.persistence.*;

@Entity
@Table(name="Wyborca")
public class Wyborca implements Comparable<Wyborca> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imie;
    @Column(name = "nazwisko", nullable = false, length = 128)
    private String nazwisko;
    private Integer rok_urodzenia;
    private String login;
    private String haslo;
    private boolean mozeZaglosowac;
    private boolean kandyduje;
    private boolean kandyduje2tura;
    private Integer glosow;
    private Integer glosow2tura;
    private String czy_w_drugiej_turze;
    private String czy_zwyciezca;

    @Transient
    boolean  nowy;

/*
    @OneToOne(cascade = CascadeType.ALL)
    private Adres adres;

    @ManyToOne(cascade = CascadeType.ALL)
    private Firma firma;
    */

    public Wyborca(Integer id, String imie, String nazwisko, Integer rok_urodzenia, String login, String haslo,
                   boolean mozeZaglosowac, boolean kandyduje, boolean kandyduje2tura,
                   Integer glosow, Integer glosow2tura,
                   String czy_w_drugiej_turze, String czy_zwyciezca, boolean nowy) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.rok_urodzenia = rok_urodzenia;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.kandyduje2tura = kandyduje2tura;
        this.glosow = glosow;
        this.glosow2tura = glosow2tura;
        this.czy_w_drugiej_turze = czy_w_drugiej_turze;
        this.czy_zwyciezca = czy_zwyciezca;
        this.nowy = nowy;
    }

    public Wyborca(String imie, String nazwisko, Integer rok_urodzenia, String login, String haslo,
                   boolean mozeZaglosowac, boolean kandyduje, boolean kandyduje2tura,
                   Integer glosow, Integer glosow2tura,
                   String czy_w_drugiej_turze, String czy_zwyciezca, boolean nowy) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.rok_urodzenia = rok_urodzenia;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.kandyduje2tura = kandyduje2tura;
        this.glosow = glosow;
        this.glosow2tura = glosow2tura;
        this.czy_w_drugiej_turze = czy_w_drugiej_turze;
        this.czy_zwyciezca = czy_zwyciezca;
        this.nowy = nowy;
    }

    public Wyborca(){}

    public String getCzy_w_drugiej_turze() {
        return czy_w_drugiej_turze;
    }

    public void setCzy_w_drugiej_turze(String czy_w_drugiej_turze) {
        this.czy_w_drugiej_turze = czy_w_drugiej_turze;
    }

    public String getCzy_zwyciezca() {
        return czy_zwyciezca;
    }

    public void setCzy_zwyciezca(String czy_zwyciezca) {
        this.czy_zwyciezca = czy_zwyciezca;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public Integer getRok_urodzenia() {
        return rok_urodzenia;
    }

    public void setRok_urodzenia(Integer rok_urodzenia) {
        this.rok_urodzenia = rok_urodzenia;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public boolean isMozeZaglosowac() {
        return mozeZaglosowac;
    }

    public void setMozeZaglosowac(boolean mozeZaglosowac) {
        this.mozeZaglosowac = mozeZaglosowac;
    }

    public boolean isKandyduje() {
        return kandyduje;
    }

    public void setKandyduje(boolean kandyduje) {this.kandyduje = kandyduje;}

    public boolean isKandyduje2tura() {
        return kandyduje2tura;
    }

    public void setKandyduje2tura(boolean kandyduje2tura) {
        this.kandyduje2tura = kandyduje2tura;
    }

    public Integer getGlosow() {
        return glosow;
    }

    public void setGlosow(Integer glosow) {
        this.glosow = glosow;
    }

    public Integer getGlosow2tura() {
        return glosow2tura;
    }

    public void setGlosow2tura(Integer glosow2tura) {
        this.glosow2tura = glosow2tura;
    }

    public boolean isNowy() {
        return nowy;
    }

    public void setNowy(boolean nowy) {
        this.nowy = nowy;
    }



    @Override
    public String toString() {
        return "Wyborca{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", rok_urodzenia='" + rok_urodzenia + '\'' +
                ", login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                ", mozeZaglosowac=" + mozeZaglosowac +
                ", kandyduje=" + kandyduje +
                ", kandyduje2tura=" + kandyduje2tura +
                ", głosów=" + glosow +
                ", głosów2tura=" + glosow2tura +
                ", czy_w_drugiej_turze=" + czy_w_drugiej_turze +
                ", czy_w_drugiej_turze=" + czy_zwyciezca +
                ", nowy=" + nowy +
                '}';
    }

    @Override
    public int compareTo(Wyborca o) {
        int porownanie_nazwisk = this.nazwisko.compareTo(o.nazwisko);
        int porownanie_imion = this.imie.compareTo(o.imie);
        if(porownanie_nazwisk != 0)
            return porownanie_nazwisk;
        else
            return porownanie_imion;
    }
}
