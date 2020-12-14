package aplikacja.wybory2;

import javax.persistence.*;

@Entity
@Table(name="Wyborca")
public class Wyborca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imie;
    @Column(name = "nazwisko", nullable = false, length = 128)
    private String nazwisko;
    private String login;
    private String haslo;
    private boolean mozeZaglosowac;
    private boolean kandyduje;
    private boolean kandyduje2tura;
    private Integer glosow;
    private Integer glosow2tura;

    @Transient
    boolean  nowy;

/*
    @OneToOne(cascade = CascadeType.ALL)
    private Adres adres;

    @ManyToOne(cascade = CascadeType.ALL)
    private Firma firma;
    */

    public Wyborca(Integer id, String imie, String nazwisko, String login, String haslo,
                   boolean mozeZaglosowac, boolean kandyduje,
                   boolean kandyduje2tura,
                   Integer glosow, Integer glosow2tura, boolean nowy) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.kandyduje2tura = kandyduje2tura;
        this.glosow = glosow;
        this.glosow2tura = glosow2tura;
        this.nowy = nowy;
    }

    public Wyborca(String imie, String nazwisko, String login, String haslo,
                   boolean mozeZaglosowac, boolean kandyduje,
                   boolean kandyduje2tura,
                   Integer glosow, Integer glosow2tura, boolean nowy) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.kandyduje2tura = kandyduje2tura;
        this.glosow = glosow;
        this.glosow2tura = glosow2tura;
        this.nowy = nowy;
    }

    public Wyborca(){}

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
                ", login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                ", mozeZaglosowac=" + mozeZaglosowac +
                ", kandyduje=" + kandyduje +
                ", kandyduje2tura=" + kandyduje2tura +
                ", głosów=" + glosow +
                ", głosów2tura=" + glosow2tura +
                ", nowy=" + nowy +
                '}';
    }

}
