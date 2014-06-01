package ViewController;

import BusinessLogic.BlContacts;
import Interfaces.IBlContacts;
import Interfaces.IContact;
import Interfaces.IContactList;
import Model.ContactList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * ViewController.MainController-Klasse für alle Views
 * Kontrolliert alle Click-Events
 * befüllt etwaige Elemente mit Objekten
 */
public class MainController {
    /**
     * Tabelle zum anzeigen der Kontakte
     */
    public TableView<IContact> ContactTable;
    /**
     * Button zum suchen von Kontakten
     */
    public Button SearchButton;
    /**
     * TextField für Suchbegriffe
     */
    public TextField SearchBox;
    /**
     * Liste aller Kontakte aus der Datenbank
     */
    private IContactList contactList = new ContactList();
    /**
     * IBlContacts für den Datenbankzugriff
     */
    private IBlContacts blContacts = new BlContacts();

    /**
     * Init-Methode, erstellt die benötigten Tabellen
     */
    private void initContactTable() {
        ObservableList<IContact> displayList = FXCollections.observableList(contactList);
        ContactTable.setItems(displayList);
    }

    /**
     * Zeigt Dialog zum anlegen von neuen Kontakten an
     */
    public void CreateContactClick() {
        initCreateEditContactView(null);
    }

    /**
     * Beendet Applikation
     */
    public void QuitApplication() {
        Platform.exit();
    }

    /**
     * Erstellt ein neues Addressbuch und initialisiert die Datenbank
     */
    public void CreateAddressBookClick() {
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("DB (*.db)", "*.db");
        chooser.getExtensionFilters().add(extensionFilter);

        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".db")) {
                filePath += ".db";
            }
            blContacts.setDbPath(filePath);
            blContacts.initDB();
        }
    }

    /**
     * Öffnet Adressbuch
     * sollte prüfen ob Datenbank valide ist und ggf. Datenbank initialisieren
     */
    public void OpenAddressBookClick() {
        FileChooser chooser = new FileChooser();

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("DB (*.db)", "*.db");
        chooser.getExtensionFilters().add(extensionFilter);

        File file = chooser.showSaveDialog(new Stage());
        if (file != null) {
            blContacts.setDbPath(file.getAbsolutePath());
            blContacts.getContactsFromDB();
            initContactTable();

            /*
            TODO Benachrichtigung für aktuelle Geburtstag anzeigen
            Notifications.create().title("Test").text("Hello World").showInformation();
             */
        }
    }

    /**
     * löscht Kontakt aus Datenbank und aus der Liste
     */
    public void deleteContactClick() {
        //TODO Kontakt aus Liste und DB löschen, Table updaten
    }

    /**
     * Sucht Kontakt in der Liste und zeigt Result-List im ContactTable an
     */
    public void SearchButtonClick() {
        //TODO Kontakte in der Liste suchen
    }

    /**
     * Ruft den Dialog zum verschlüsselten Speichern des Adressbuchs auf
     */
    public void SaveAddressBookEncryptedClick() {
        initSaveAddressBookEncrypted();
    }

    /**
     * Ruft Dialog zum öffnen eines verschlüsselten Adressbuchs auf
     */
    public void OpenEncryptedAddressBookClick() {
        initOpenEncryptedAddressBook();
    }

    /**
     * Getter für die zwischengespeicherte IContactList für die anderen Controller
     *
     * @return IContactList welche aus der DB gelesen wurde
     */
    public IContactList getContactList() {
        return contactList;
    }

    /**
     * Getter für die zwischengespeicherte IBlContacts für die anderen Controller
     * benötigt für den DB-Zugriff
     *
     * @return IBlContacts-Objekt
     */
    public IBlContacts getBlContacts() {
        return blContacts;
    }

    public void initMainCointroller() {

    }

    private void initCreateEditContactView(IContact contactToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateEditContact.fxml"));
            Parent createContactRoot = loader.load();
            Stage createContactStage = new Stage();
            createContactStage.setTitle("Neuer Kontakt");
            createContactStage.setScene(new Scene(createContactRoot));
            createContactStage.show();

            CreateEditController createEditController = loader.getController();
            createEditController.initController(this);
            if (contactToEdit != null) {

            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initOpenEncryptedAddressBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OpenEncrypted.fxml"));
            OpenEncryptedController openEncryptedController = loader.getController();

            Parent createContactRoot = loader.load();
            Stage createContactStage = new Stage();
            createContactStage.setTitle("Verschlüsseltes Adressbuch öffnen");
            createContactStage.setScene(new Scene(createContactRoot));
            createContactStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initSaveAddressBookEncrypted() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SaveEncrypted.fxml"));

            Parent createContactRoot = loader.load();
            Stage createContactStage = new Stage();
            createContactStage.setTitle("Adressbuch verschlüsselt speichern");
            createContactStage.setScene(new Scene(createContactRoot));
            createContactStage.show();
            SaveEncryptedController saveEncryptedController = loader.getController();
            saveEncryptedController.initSaveEncryptedController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
