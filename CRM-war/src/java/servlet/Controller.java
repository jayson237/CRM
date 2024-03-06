package servlet;

import entity.Customer;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import manager.CustomerManager;
import session.CustomerSessionLocal;

public class Controller extends HttpServlet {

    //Inject EJB
    @EJB
    private CustomerSessionLocal customerSessionLocal;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CustomerManager customerManager = new CustomerManager(customerSessionLocal);
            String path = request.getPathInfo();
            path = path.split("/")[1];

            switch (path) {
                case "createCustomer":
                    // display page - do nothing
                    break;
                case "editCustomer": {
                    //Show the Edit Customer page with the current customer details
                    // Logic: read in the customer id, retrieve the record
                    // and pass to the frontend
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);
                    Customer customer = customerManager.getCustomer(cId);
                    request.setAttribute("customer", customer);

                    break;
                }
                case "doCreateCustomer": {
                    // Handle a create customer action
                    String name = request.getParameter("name");
                    String gender = request.getParameter("gender");
                    String dob = request.getParameter("dob");

                    customerManager.createCustomer(name, Byte.parseByte(gender), dob);

                    //redirect to the customer listing page
                    //note that for sendRedirect we should include the context path
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "doDeleteCustomer": {
                    // Handle a delete customer action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);
                    customerManager.deleteCustomer(cId);

                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "searchCustomers": {
                    String field = request.getParameter("field");
                    String searchValue = request.getParameter("searchValue");
                    System.out.println("#searchValue : " + searchValue);

                    if (field == null || searchValue == null || searchValue.equals("")) {
                        List<Customer> customers = customerManager.searchCustomer(null);
                        request.setAttribute("customers", customers);
                    } else {
                        List<Customer> customers = null;
                        switch (field) {
                            case "PHONE":
                                customers = customerManager.searchCustomersByPhone(searchValue);
                                System.out.println("#phone : " + searchValue);
                                break;
                            case "EMAIL":
                                customers = customerManager.searchCustomersByEmail(searchValue);
                                System.out.println("#email : " + searchValue);
                                break;
                            case "NAME":
                                customers = customerManager.searchCustomer(searchValue);
                                break;
                            default:
                                customers = customerManager.searchCustomersByField(field, searchValue);
                                break;
                        }

                        request.setAttribute("customers", customers);
                    }

                    Set<String> fieldNames = customerManager.getAllFieldNames();
                    request.setAttribute("fields", fieldNames);
                    break;
                }
                case "doAddField": {
                    // Handle an add field action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);
                    String name = request.getParameter("name");
                    String value = request.getParameter("value");

                    try {
                        customerManager.addField(cId, name, value);
                    } catch (Exception e1) {
                        //ignore exceptions
                    }

                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "doAddContact": {
                    // Handle an add contact action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);

                    String type = request.getParameter("type");
                    String value = request.getParameter("value");

                    if (type.equals("PHONE")) {
                        customerManager.addPhone(cId, value);
                    } else if (type.equals("EMAIL")) {
                        customerManager.addEmail(cId, value);
                    }

                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "doDeleteField": {
                    // Handle a delete field action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);
                    String fIdStr = request.getParameter("fId");
                    Long fId = Long.parseLong(fIdStr);

                    customerManager.deleteField(cId, fId);

                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "doDeleteContact": {
                    // Handle a delete contact action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);

                    customerManager.deleteContact(cId);
                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                case "doUpdateCustomer": {
                    // Handle an update customer action
                    String cIdStr = request.getParameter("cId");
                    Long cId = Long.parseLong(cIdStr);
                    String name = request.getParameter("name");
                    String gender = request.getParameter("gender");
                    String dob = request.getParameter("dob");

                    customerManager.updateCustomer(cId, name, Byte.parseByte(gender), dob);
                    //redirect to the customer listing page
                    response.sendRedirect(request.getContextPath()
                            + "/Controller/searchCustomers");
                    return;
                }
                default:
                    //unmatched path will to go error.jsp
                    path = "error";
                    break;
            }

            //forward to <path>.jsp
            //don’t need to include context path (automatically added)
            request.getRequestDispatcher("/" + path + ".jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getRequestDispatcher("/error.jsp")
                    .forward(request, response);

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
