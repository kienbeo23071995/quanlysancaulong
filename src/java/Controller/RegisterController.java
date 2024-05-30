/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Dao.UserDAO;
import Ulti.Helper;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 *
 * @author admin
 */
public class RegisterController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("Signup.jsp").forward(request, response);
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
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String phone = request.getParameter("email");
        String password = request.getParameter("password");
        String repassword = request.getParameter("re_password");
        request.setAttribute("email", email);
        request.setAttribute("username", username);
        request.setAttribute("phone", phone);
        request.setAttribute("password", password);
        request.setAttribute("repassword", repassword);
        UserDAO userDAO = new UserDAO();
        if (password.equals(repassword)) {
            request.setAttribute("report", "Mật khẩu không trùng nhau");
        } else if (userDAO.getAccountByEmail(email) != null) {
            request.setAttribute("report", "Email đã được đăng ký!");
        } else {
            userDAO.insertUser(email, username, phone, password);
            String token = UUID.randomUUID().toString();
            userDAO.insertToken(token,email);
            String link = "http://localhost:9999/SWP391/verify?token=" + UUID.randomUUID().toString();
            String message = "<!DOCTYPE html>\n"
                    + "<html lang=\"en\">\n"
                    + "<head></head>\n"
                    + "<body style=\"color:#000;\">\n"
                    + "    <h3>CHÀO MỪNG BẠN ĐÃ ĐẾN VỚI WEBSITE</h3>\n"
                    + "    <p>Xin hãy bấm vào đây để xác thực tài khoản</p>\n"
                    //                        + "    <form id=\"myForm\" method=\"POST\" action=" + Base.LINK_CHANGE_PASSWORD + ">\n"
                    //                        + "        <input type=\"hidden\" value=" + email + " id=\"email\" name=\"email\">\n"
                    //                        + "        <input type=\"submit\" value=\"Change password\" \n"
                    //                        + "            style=\"padding: 10px 15px;color: #fff;background-color: rgb(0, 149, 255);border-radius: 10px;border:none\">\n"
                    //                        + "    </form>\n"
                    + "<a href=\"" + link + "\""
                    + "style=\"padding: 10px 15px;color: #fff;background-color: rgb(0, 149, 255);border-radius: 10px;border:none\">here</a>"
                    + "    <h4>Thank you very much</h4>\n"
                    + "</body>\n"
                    + "</html>";
            Helper.sendEmail(email, "Xác thực tài khoản", message);
            request.setAttribute("report", "Đăng ký thành công!Kiểm tra email để xác thực tài khoản");
        }
        doGet(request, response);
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
