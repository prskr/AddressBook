package ViewController;

import Interfaces.IAddress;
import Interfaces.IContact;
import Interfaces.IContactList;
import Interfaces.IUtil;
import Model.Address;
import Model.Contact;
import Model.ContactNumberType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

import java.time.LocalDate;

/**
 * Controller für die CreateEditView
 *
 * @author baez
 */
public class CreateEditController {
    /**
     * TextField für Vorname
     */
    @FXML
    private TextField FirstNameBox;
    /**
     * TextField für Nachmame
     */
    @FXML
    private TextField NameBox;
    /**
     * TextField für Email-Adresse
     */
    @FXML
    private TextField MailBox;
    /**
     * TextField für Strasse u. Hausnummer
     */
    @FXML
    private TextField StreetAddressBox;
    /**
     * TextField für die PLZ
     */
    @FXML
    private TextField ZipCodeBox;
    /**
     * TextField für den Wohnort
     */
    @FXML
    private TextField CityBox;
    /**
     * Zugriff auf den DatePicker um Startdatum etwas weiter in die Vergangenheit setzen zu können
     */
    @FXML
    private DatePicker BirthdayDatePicker;
    /**
     * IContactList als Zwischenspeicher um Veränderungen ablegen zu können.
     */
    private IContactList contactList;
    /**
     * Zwischenspeicher für Kontakt-Objekt zum editieren
     */
    private IContact contactToEdit = null;
    /**
     * MainController für den Zugriff auf die BlContacts, ContactList, ContactTable usw
     */
    private MainController mainController;

    /**
     * Initialisiert den Controller mit Objekten aus dem MainController
     *
     * @param con  MainController-Objekt
     * @param edit boolean ob Edit oder Create
     */
    public void initController(MainController con, boolean edit) {
        contactList = con.getContactList();
        mainController = con;
        BirthdayDatePicker.setValue(LocalDate.of(1992, 1, 1));
        if (edit) {
            //TODO Rufnummern berücksichtigen
            contactToEdit = mainController.getSelectedContact();
            FirstNameBox.setText(contactToEdit.getFirstName());
            NameBox.setText(contactToEdit.getLastName());
            MailBox.setText(contactToEdit.getMailAddress());
            BirthdayDatePicker.setValue(contactToEdit.getBirthDate());
            StreetAddressBox.setText(contactToEdit.getAddress().getStreetAddress());
            ZipCodeBox.setText(contactToEdit.getAddress().getZipCode());
            CityBox.setText(contactToEdit.getAddress().getCity());
        }
    }

    /**
     * Wrapper für Update und Create, prüft anhand des contactToEdit-Objekts ob ein neuer Kontakt angelegt wird oder nicht
     *
     * @param actionEvent Event um auf den Dialog zugreifen zu können
     */
    public void SaveNewContactClick(ActionEvent actionEvent) {
        if (contactToEdit != null) {
            updateContact(actionEvent);
        } else {
            saveNewContact(actionEvent);
        }
    }

    /**
     * Schließt etwaige Dialoge
     *
     * @param actionEvent Event um auf den Dialog zugreifen zu können
     */
    public void CancelModalClick(ActionEvent actionEvent) {
        closeModal(actionEvent);
    }

    /**
     * fügt eine ChoiceBox und ein TextField in die VBox ein um zusätzliche Telefonummern eintragen zu können
     */
    public void addPhoneButtonClick() {
        /*
        ChoiceBox für Telefonnummer-Typ
         */
        ChoiceBox<ContactNumberType> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().add(ContactNumberType.Mobile);
        choiceBox.getItems().add(ContactNumberType.Home);
        choiceBox.getItems().add(ContactNumberType.Work);
        choiceBox.setValue(choiceBox.getItems().get(0));

        //TODO implement Create/Edit PhoneNumbers
    }

    /**
     * entfernt unterste Telefonnummer aus der Liste
     */
    public void RemovePhoneNumber() {
        //TODO implement
    }

    private void saveNewContact(ActionEvent actionEvent) {
        if (!IUtil.validateMailAddress(MailBox.getText())) {
            Dialogs.create().title("Info").masthead("Validierungsfehler").message("Die Email-Adresse hat kein gültiges Format").showInformation();
            return;
        }

        IContact newContact = new Contact(FirstNameBox.getText(), NameBox.getText(), MailBox.getText().toLowerCase(), BirthdayDatePicker.getValue());
        IAddress newAddress = new Address(StreetAddressBox.getText(), ZipCodeBox.getText(), CityBox.getText());
        newContact.setAddress(newAddress);
        //TODO implement phone numbers

        newContact.setContactID(mainController.getBlContacts().createContactInDB(newContact));
        switch (newContact.getContactID()) {
            case 0:
                Dialogs.create().title("Info").masthead("Kontakt bereits vorhanden").message("Ein Kontakt mit dem selben Namen ist bereits vorhanden. Dieser Kontakt wurde geupdated.").showInformation();
                break;
            case -1:
                Dialogs.create().title("Info").masthead("Fehler aufgetreten").message("Beim anlegen des Kontakts ist ein Fehler aufgetreten.").showInformation();
                break;
            default:
                contactList.add(newContact);
        }
        mainController.updateContactTable(contactList);
        closeModal(actionEvent);
    }

    private void updateContact(ActionEvent actionEvent) {
        if (!IUtil.validateMailAddress(MailBox.getText())) {
            Dialogs.create().title("Info").masthead("Validierungsfehler").message("Die Email-Adresse hat kein gültiges Format").showInformation();
            return;
        }

        contactToEdit.setFirstName(FirstNameBox.getText());
        contactToEdit.setLastName(NameBox.getText());
        contactToEdit.setMailAddress(MailBox.getText());
        contactToEdit.setBirthDate(BirthdayDatePicker.getValue());
        contactToEdit.getAddress().setStreetAddress(StreetAddressBox.getText());
        contactToEdit.getAddress().setZipCode(ZipCodeBox.getText());
        contactToEdit.getAddress().setCity(CityBox.getText());

        mainController.getBlContacts().updateContactInDB(contactToEdit);

        closeModal(actionEvent);
    }

    /**
     * Schließt ein Fenster
     *
     * @param actionEvent Event um auf den Dialog zugreifen zu können
     */
    private void closeModal(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}