package managedbean;

import entity.Contact;
import entity.Customer;
import entity.Field;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import session.CustomerSessionLocal;

@Named(value = "customerManagedBean")
@ViewScoped
public class CustomerManagedBean implements Serializable {

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    private String name;
    private byte gender;
    private Date dob;
    private List<Customer> customers;

    //used by editCustomer.xhtml
    private Long cId;
    private Customer selectedCustomer;

    private String fieldName;
    private String fieldValue;
    private String contactType = "PHONE";
    private String contactValue;

    private String searchType = "NAME";
    private String searchString;
    private Set<String> allFields;

    public CustomerManagedBean() {
    }

    @PostConstruct
    public void init() {
        if (searchString == null || searchString.equals("")) {
            customers = customerSessionLocal.searchCustomers(null);
        } else {
            switch (searchType) {
                case "NAME":
                    customers = customerSessionLocal.searchCustomers(searchString);
                    break;
                case "PHONE": {
                    Contact c = new Contact();
                    c.setPhone(searchString);
                    customers = customerSessionLocal.searchCustomersByContact(c);
                    break;
                }
                case "EMAIL": {
                    Contact c = new Contact();
                    c.setEmail(searchString);
                    customers = customerSessionLocal.searchCustomersByContact(c);
                    break;
                }
                default:
                    Field f = new Field();
                    f.setName(searchType);
                    f.setFieldValue(searchString);
                    customers = customerSessionLocal.searchCustomersByField(f);
                    break;
            }
        }

        allFields = customerSessionLocal.getAllFieldNames();
    }

    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.selectedCustomer = customerSessionLocal.getCustomer(cId);
            name = this.selectedCustomer.getName();
            gender = this.selectedCustomer.getGender();
            dob = this.selectedCustomer.getDob();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    } //end loadSelectedCustomer

    public void handleSearch() {
        init();
    } //end handleSearch

    public void addCustomer(ActionEvent evt) {
        Customer c = new Customer();
        c.setName(name);
        c.setGender(gender);
        c.setDob(dob);
        c.setCreated(new Date());

        customerSessionLocal.createCustomer(c);
    } //end addCustomer

    public void updateCustomer(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedCustomer.setName(name);
        selectedCustomer.setGender(gender);
        selectedCustomer.setDob(dob);

        try {
            customerSessionLocal.updateCustomer(selectedCustomer);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update customer"));
            return;
        }

        //need to make sure reinitialize the customers collection
        init();
        context.addMessage(null, new FacesMessage("Success", "Successfully updated customer"));
    } //end updateCustomer

    public void deleteCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String cIdStr = params.get("cId");
        Long cId = Long.parseLong(cIdStr);

        try {
            customerSessionLocal.deleteCustomer(cId);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete customer"));
            return;
        }

        context.addMessage(null, new FacesMessage("Success", "Successfully deleted customer"));
        init();

    } //end deleteCustomer

    public void deleteField(Long cId, Long fId) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            customerSessionLocal.deleteField(cId, fId);
            this.selectedCustomer = customerSessionLocal.getCustomer(cId);
            init();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete field"));
        }
    }

    public void deleteContact(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            customerSessionLocal.deleteContact(cId);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());
            init();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete contact"));
        }
    }

    public void addField() {
        FacesContext context = FacesContext.getCurrentInstance();
        Field f = new Field();
        f.setName(this.fieldName);
        f.setFieldValue(this.fieldValue);

        try {
            customerSessionLocal.addField(this.selectedCustomer.getId(), f);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());

            //reset values
            this.fieldName = "";
            this.fieldValue = "";
            init();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to add field"));
        }
    }

    public void addContact() {
        FacesContext context = FacesContext.getCurrentInstance();

        Contact c = new Contact();

        if (this.contactType.equals("PHONE")) {
            c.setPhone(this.contactValue);
        } else if (this.contactType.equals("EMAIL")) {
            c.setEmail(this.contactValue);
        }

        try {
            customerSessionLocal.addContact(this.selectedCustomer.getId(), c);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());

            //reset values
            this.contactType = "PHONE";
            this.contactValue = "";
            init();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to add contact"));
        }
    } //end addContact

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Long getcId() {
        return cId;
    }

    public void setcId(Long cId) {
        this.cId = cId;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Set<String> getAllFields() {
        return allFields;
    }

    public void setAllFields(Set<String> allFields) {
        this.allFields = allFields;
    }
}
