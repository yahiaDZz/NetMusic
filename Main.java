import java.util.ArrayList;
import java.util.List;

class NetMusic {
    public static final Catalogue catalogue = new Catalogue();
    public static final List<Client> clients = new ArrayList<>();
    public static final List<Compte> comptes = new ArrayList<>();
    public final String URL;
    public final String description;
    public NetMusic(String URL, String description) {
    this.URL=URL;
    this.description=description;
    }

    public static void CreateClient(Client client) throws Exception {
        if (clients.contains(client))
            throw new Exception("Client already exists!");
        clients.add(client);
        if (client.hasAccount())
            comptes.add(client.getCompte());
    }

    public static void CreateAccount(Client client, String username, String password, double solde) throws Exception {
        if (client.hasAccount()) {
            throw new Exception("Client already has an account!");
        }
        for (Compte c : comptes) {
            if (c.getUsername().equals(username)) {
                throw new Exception("Username Already Exists!");
            }
        }
        client.CreateAccount(username, password, solde);
        System.out.println("Compte crée: Username " + username + " !");
    }

    public static void Login(Client client, String username, String password) throws Exception {
        if (!clients.contains(client))
            throw new Exception("Client does not exist!");
        Client c = clients.get(clients.indexOf(client));
        if (!c.hasAccount())
            throw new Exception("This client does not have an account!");
        if (!c.getCompte().getPassword().equals(password))
            throw new Exception("Incorrect Password");
        System.out.println("Welcome Home " + c.getPrenom() + " !");
    }
}

class Catalogue {
    private final List<Album> albums = new ArrayList<>();
    private final List<Magazine> magazines = new ArrayList<>();
    public final List<Product> products = new ArrayList<>();
    public Catalogue(Product... products) {
        for (Product a : products) {
            this.products.add(a);
            if(a instanceof Album)albums.add((Album)a);
            if(a instanceof Magazine)magazines.add((Magazine)a);
        }
    }

    public void addMagazines(Magazine... magazines) {
        for (Magazine m : magazines)
            this.magazines.add(m);
    }

    public void AddAlbum(Album e) {
        this.albums.add(e);
    }

    public void Consulter() {
        System.out.println("************************CONSULTATION DU CATALOGUE************************");
        System.out.printf("Il y a %d albums dans le catalogue..\n", albums.size());
        for (int i = 0; i < albums.size(); i++) {
            Album a = albums.get(i);
            System.out.printf("Album #%d :\n", i + 1);
            System.out.printf("\t Title: %s| Author: %s| Price: %f | Product House:%s | Year:%d\n", a.getTitre(),
      
            a.getAuteur(), a.getPrix(), a.getMaison_de_production(), a.getAnne_de_production());
            System.out.printf("\t\t This album contains %d titles:\n", a.getTitres().size());
            for (int j = 0; j < a.getTitres().size(); j++) {
                System.out.printf("\t\t\t Titre #%d: %s\n", j+1, a.getTitres().get(j));
            }
        } 
        System.out.printf("Il y a %d magazines dans le catalogue..\n", magazines.size());
        for (int i = 0; i < magazines.size(); i++) {
            Magazine m = magazines.get(i);
            System.out.printf("Magazine #%d :\n", i + 1);
            System.out.printf("\t Title: %s | Price: %f \n", m.getTitre(), m.getPrix());
        }
        
        System.out.println("************************FIN CONSULTATION DU CATALOGUE************************");
    }
}

enum Style {
    CLASSIC, JAZZ, POP, ROCK, RAP, RNB
}

abstract class Product {
    String titre;
    double prix;
    
    public Product(String titre, double prix) {
        this.titre = titre;
        this.prix = prix;
    }
    abstract double CalculerPrix();
    public String getTitre() {
        return titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }
    public double getPrix() {
        return prix;
    }
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
}

abstract class Album extends Product {
    String auteur, maison_de_production;
    int anne_de_production;
    private final List<String> titres = new ArrayList<>();
    double prix;
    Style style;

    public Album(String titre, String auteur, String maison_de_production, int anne_de_production, double prix,
            Style style, String... titres) {
                super(titre,prix);
        this.auteur = auteur;
        this.maison_de_production = maison_de_production;
        this.anne_de_production = anne_de_production;
        for (String t : titres)
            this.titres.add(t);
        this.style = style;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getMaison_de_production() {
        return maison_de_production;
    }

    public int getAnne_de_production() {
        return anne_de_production;
    }

    public List<String> getTitres() {
        return titres;
    }

    public void addTitre(String titre) {
        this.titres.add(titre);
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

}

interface RemboursableEchangeable {
    int MAX_DELAI = 7;

    void Rembourser(Compte compte) throws Exception;

    void Echanger(RemboursableEchangeable album2, Compte compte) throws Exception;
}

class AlbumPhysique extends Album implements RemboursableEchangeable {
    private int quantite;
    private double frais_de_livraison;
    private int delai;

    public AlbumPhysique(String titre, String auteur, String maison_de_production, int anne_de_production,
            double prix, Style style, int quantite, String... titres) {
        super(titre, auteur, maison_de_production, anne_de_production, prix, style, titres);
        this.quantite = quantite;
        frais_de_livraison = prix * 10. / 100.;
    }

    public void StartDelai() {
        delai = MAX_DELAI;
    }

    public double CalculerPrix() {
        return this.prix + frais_de_livraison;
    }

    public int getDelai() {
        return delai;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }

    @Override
    public void Rembourser(Compte compte) throws Exception {
        if (this.getDelai() > 7)
            throw new Exception("Delai has passed ! cannot reimburse");
        compte.Crediter(this.getPrix());
    }

    @Override
    public void Echanger(RemboursableEchangeable album2, Compte compte)
            throws Exception {
        compte.Debiter(((AlbumPhysique) album2).getPrix());
        compte.Crediter(this.getPrix());
    }

}

class AlbumNumerique extends Album {
    private String URL;
    private double reduction;

    public AlbumNumerique(String titre, String auteur, String maison_de_production, int anne_de_production,
            double prix, Style style, String URL, String... titres) {
        super(titre, auteur, maison_de_production, anne_de_production, prix, style, titres);
        this.URL = URL;
        reduction = prix * 5. / 100.;
    }

    public double CalculerPrix() {
        return this.prix - reduction;
    }

    public String getURL() {
        return URL;
    }

    public double getReduction() {
        return reduction;
    }

}

class Magazine extends Product implements RemboursableEchangeable {
    
    private int delai;

    public Magazine(String title, double prix) {
        super(title, prix);
        this.delai = MAX_DELAI;
    }

    @Override
    public void Rembourser(Compte compte) throws Exception {
        compte.Crediter(this.CalculerPrix());
    }

    @Override
    public void Echanger(RemboursableEchangeable album, Compte compte)
            throws Exception {
        compte.Debiter(((Magazine) album).CalculerPrix());
        compte.Crediter(this.CalculerPrix());
    }

    public double CalculerPrix() {
        return prix;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getDelai() {
        return delai;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }

}

abstract class Accountable {
    String username, password;
    double solde;

    public Accountable(String username, String password, double solde) {
        this.username = username;
        this.password = password;
        this.solde = solde;
    }

    public void Debiter(double v) throws Exception {
        if (v > getSolde())
            throw new Exception("Cette compte ne possede pas assez de solde pour effectuer ce requete!");
        setSolde(solde - v);
    }

    public void Crediter(double v) {
        setSolde(solde + v);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSolde() {
        return solde;
    }

    private void setSolde(double solde) {
        this.solde = solde;
    }

}

class Compte extends Accountable {

    private final List<Product> panier = new ArrayList<Product>();
    private final List<Product> achetes = new ArrayList<Product>();

    public Compte(String username, String password, double solde) {
        super(username, password, solde);
    }

    public void SelectProduct(Product e) {
        panier.add(e);
    }

    public void UnselectProduct(Product e) {
        panier.remove(e);
    }

    public void Buy() throws Exception {
        for (Product e : panier) {
            this.Debiter(e.CalculerPrix());
            if (e instanceof AlbumPhysique) {
                ((AlbumPhysique) e).StartDelai();
            }
            achetes.add(e);
        }
        panier.clear();
    }
}

class Client {
    private String nom, prenom, address;
    private Compte compte;

    public Client(String nom, String prenom, String address, Compte compte) {
        this.nom = nom;
        this.prenom = prenom;
        this.address = address;
        this.compte = compte;
    }

    public boolean hasAccount() {
        return this.compte != null;
    }

    public void CreateAccount(String username, String password, double solde) {
        this.compte = new Compte(username, password, solde);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Compte getCompte() throws Exception {
        if (hasAccount())
            return compte;
        throw new Exception("This client doesn't have an account");
    }

}

public class Main {
    public static void main(String[] args) throws Exception {
        // Création de NetMusic
        NetMusic site = new NetMusic("http://netmusic.com","Vente d'albums de musique et des magazine");
        /*** I. Gestion des clients ***/
        // A. Création de Compte
        // 1. Création d'un client qui n'existe pas
        Client client1 = new Client("Benyahia", "Yahia Abdeldjalil", "Batna",
                new Compte("Yahia_ddz", "mypassword1@@", 2000.0d));
        NetMusic.CreateClient(client1);

        // 2. Test de création d'un client qui existe
        NetMusic.CreateClient(client1);
        // 3. Test de création d'un compte avec un nom d'utilisateur existant
        Client client2 = new Client("Nom2", "Prenom2", "Address2", null);// client without an account
        NetMusic.CreateAccount(client2, "Yahia_ddz", "mypassword2@@",1500.0d);
        // 4. Création d'un compte avec un nom d'utilisateur différent
        NetMusic.CreateAccount(client2, "Nom2_Prenom2", "mypassword2@@",2000.0d);
        // B. Authentification
        // 1. Test d’authentification d'un client qui n'existe pas
        Client client3 = new Client("Nom3", "Prenom3", "Address3", null);// client without an account
          NetMusic.Login(client3, "Nom3_Prenom3", "mypassword3@@");
        // 2. Test d’authentification d'un client qui existe mais mot de passe
        // incorrecte
        NetMusic.Login(client1, "Yahia_ddz", "mypassword99@@");
        // 3. Test d’authentification d'un client qui existe avec mot de passe correcte
        NetMusic.Login(client1, "Yahia_ddz", "mypassword1@@");

        /*** II. Gestion du catalogue d'Album ***/
        // A. Création et affichage des albums
        // 1. Ajouter des albums physiques et numériques au catalogue
        Album album1 = new AlbumPhysique("aphy1", "aaphy1", "mp", 2018, 300.0d, Style.CLASSIC, 4, "titre1", "titre2",
                "titre3", "titre4");
        Album album2 = new AlbumPhysique("aphy2", "aaphy2", "mp", 2018, 350.0d, Style.RAP, 2, "titre1", "titre2");
        Album album3 = new AlbumNumerique("an1", "aan1", "mp2", 2019, 200.0d, Style.JAZZ, "http://mp2.com/albums/an1",
                "titre1", "titre2", "titre3");
        Album album4 = new AlbumNumerique("an2", "aan2", "mp2", 2019, 250.0d, Style.POP, "http://mp2.com/albums/an2",
                "titre1", "titre2");

        NetMusic.catalogue.AddAlbum(album1);
        NetMusic.catalogue.AddAlbum(album2);
        NetMusic.catalogue.AddAlbum(album3);
        NetMusic.catalogue.AddAlbum(album4);
        // 2. Consulter le catalogue d’album
        NetMusic.catalogue.Consulter();
        // B. Achat et Remboursement des albums
        // 1. Achat de deux albums physique
        client1.getCompte().SelectProduct(album1);
        client1.getCompte().SelectProduct(album2);
        client1.getCompte().Buy();
        // 2. Achat d'un album numérique
        client1.getCompte().SelectProduct(album3);
        client1.getCompte().SelectProduct(album4);
        client1.getCompte().Buy();
        // 3. Remboursement d'un album physique dans un délai >7 jours
        ((AlbumPhysique) album1).setDelai(8);
        ((AlbumPhysique) album1).Rembourser(client1.getCompte());
        // 4. Remboursement d'un album physique dans un délai de 7 jours
        ((AlbumPhysique) album2).setDelai(7);
        ((AlbumPhysique) album2).Rembourser(client1.getCompte());
        /*** III. Gestion du catalogue de magazines ***/
        // A. Création et affichage des magazines
        // 1. Ajouter des magazines au catalogue
        Magazine mag1 = new Magazine("magazine1", 600.0d);
        Magazine mag2 = new Magazine("magazine2", 700.0d);
        Magazine mag3 = new Magazine("magazine3", 600.0d);
        NetMusic.catalogue.addMagazines(mag1, mag2, mag3);
        // 2. Consulter le catalogue de magazines
        NetMusic.catalogue.Consulter();
        // B. Achat et Remboursement des magazines
        // 1. Achat d’un magazine sans avoir le solde suffisant pour le faire
        client1.getCompte().SelectProduct(mag1);
        client1.getCompte().SelectProduct(mag2);
        client1.getCompte().Buy();
        // 2. Créditer le compte avec 500 DA
        client1.getCompte().Crediter(500.0d);
        // 3. Achat de deux magazines
        client1.getCompte().SelectProduct(mag1);
        client1.getCompte().SelectProduct(mag2);
        client1.getCompte().Buy();
        // 4. Remboursement d'un magazine dans un délai >7 jours

        ((Magazine) mag1).setDelai(8);
        ((Magazine) mag1).Rembourser(client1.getCompte());
        // 5. Remboursement d'un magazine dans un délai de 7 jours
        ((Magazine) mag2).setDelai(7);
        ((Magazine) mag2).Rembourser(client1.getCompte());
    }
}
