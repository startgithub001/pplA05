/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author alvin
 */
@WebServlet(urlPatterns = {"/registerHandler"})
@MultipartConfig
public class registerHandler extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        
        if(session == null || !session.getAttribute("logged").toString().equals("true")){
            response.sendRedirect("index.html");
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
        
        try {
            
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/handyman", "root", "");
            Statement statement = connection.createStatement();
            
            String username = request.getParameter("username");
            
            ResultSet resultset = statement.executeQuery("select * from worker where username = '" + username + "'");
            
            if(resultset.next()){
                HttpSession session = request.getSession(false);
                session.setAttribute("exist", "Username already exist");
                response.sendRedirect("register");
            }else{
            
                String password = request.getParameter("password");
                String name = request.getParameter("name");
                String address = request.getParameter("address");
                String tag = request.getParameter("tag");
                //name of the photo is the same with username for default
                //can change to id or something if needed
                String photo = username;
                Double latitude = 0.0;
                Double longitude = 0.0;
                if(!request.getParameter("latitude").isEmpty()){
                    latitude = Double.parseDouble(request.getParameter("latitude"));
                }
                if(!request.getParameter("longitude").isEmpty()){
                    longitude = Double.parseDouble(request.getParameter("longitude"));
                }

                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                crypt.reset();
                crypt.update(password.getBytes("UTF-8"));
                password = new BigInteger(1, crypt.digest()).toString(16);

                statement.executeUpdate("insert into worker (username, password, name, address, tag, photo, latitude, longitude) values ('" + username + "','" + password + "','" + name + "','" + address + "','" + tag + "','" + photo + "', " + latitude + ", "  + longitude + ")");

                //The path where photo is uploaded
                final String path = "D:\\";
                final Part filePart = request.getPart("photo");
                final String fileName = photo + ".jpg";

                OutputStream out = null;
                InputStream filecontent = null;
                final PrintWriter writer = response.getWriter();

                try {
                    out = new FileOutputStream(new File(path + File.separator + fileName));
                    filecontent = filePart.getInputStream();

                    int read = 0;
                    final byte[] bytes = new byte[1024];

                    while ((read = filecontent.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    response.sendRedirect("register");
                } catch (FileNotFoundException fne) {
                    writer.println("Error in file upload  ERROR:" + fne.getMessage());

                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (filecontent != null) {
                        filecontent.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(loginHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(registerHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(registerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
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
