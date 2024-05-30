/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Models.User;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author admin
 */
public class UserDAO extends DBContext {

    public User getAccountByEmail(String email) {
        try {
            String sql = """
                         SELECT [id]
                               ,[address]
                               ,[avatar]
                               ,[created_date]
                               ,[email]
                               ,[expire_date]
                               ,[full_name]
                               ,[is_active]
                               ,[is_delete]
                               ,[modified_date]
                               ,[otp]
                               ,[password]
                               ,[phone]
                               ,[username]
                               ,[roleid]
                           FROM [dbo].[user] where email = ? and is_delete = 0""";
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, email);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                User user = new User();
                setDataUser(rs, user);
                return user;
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public User getAccountById(int id) {
        try {
            String sql = """
                         SELECT [id]
                               ,[address]
                               ,[avatar]
                               ,[created_date]
                               ,[email]
                               ,[expire_date]
                               ,[full_name]
                               ,[is_active]
                               ,[is_delete]
                               ,[modified_date]
                               ,[otp]
                               ,[password]
                               ,[phone]
                               ,[username]
                               ,[roleid]
                           FROM [dbo].[user] where id = ? and is_delete = 0""";
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setInt(1, id);
            ResultSet rs = ptm.executeQuery();
            while (rs.next()) {
                User user = new User();
                setDataUser(rs, user);
                return user;
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void setDataUser(ResultSet rs, User user) throws SQLException {
        user.setId(rs.getInt(1));
        user.setAddress(rs.getString(2));
        user.setAvatar(rs.getString(3));
        user.setEmail(rs.getString(5));
        user.setFullName(rs.getString(7));
        user.setIsActive(rs.getBoolean(8));
        user.setIsDelete(rs.getBoolean(9));
        user.setPassword(rs.getString(12));
        user.setPhone(rs.getString(13));
        user.setUsername(rs.getString(14));
    }

    public void insertUser(String email, String username, String phone, String password) {
        String salt = BCrypt.gensalt(12);
        try {
            String sql = """
                         INSERT INTO [dbo].[user]
                                      (
                                       [email]
                                      ,[password]
                                      ,[phone]
                                      ,[username]
                                      ,[roleid])
                                VALUES
                                      (?
                                      ,?
                                      ,?
                                      ,?
                                      ,1)""";
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, email);
            ptm.setString(2, BCrypt.hashpw(password, salt));
            ptm.setString(4, username);
            ptm.setString(3, phone);
            ptm.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void insertToken(String token, String email) {
        User user = getAccountByEmail(email);
        try {
            String sql = """
                         INSERT INTO [dbo].[password_reset_token]
                                    ([expiry_date]
                                    ,[token]
                                    ,[user_id])
                              VALUES
                                    (DATEADD(day, 5, GETDATE())
                                    ,?
                                    ,?)
                         """;
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, token);
            ptm.setLong(2, user.getId());
            ptm.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User getUserByToken(String token) {
        try {
            String sql = """
                         SELECT [expiry_date]
                                          ,[user_id]
                                      FROM [dbo].[password_reset_token] where token = ?
                         """;
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, token);
            ResultSet rs = ptm.executeQuery();
            if(rs.next()){
                Date date = rs.getDate(1);
                if(date.before(new Date())){
                    return null;
                }
                else {
                    int id = rs.getInt(2);
                    return getAccountById(id);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void updateOTP(String otp, String email) {
        try {
            String sql = """
                         update [user] set otp = ?,expire_date = DATEADD(day, 5, GETDATE()) where email = ? 
                         """;
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, otp);
            ptm.setString(2, email);
            ptm.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String checkOtp(String email, String otp) {
        try {
            String sql = """
                         select otp,expire_date from [user] where email = ?
                         """;
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, email);
            ResultSet rs = ptm.executeQuery();
            if(rs.next()){
                Date date = rs.getDate(2);
                if(date.before(new Date())){
                    return "expire";
                }
                else {
                    String old_otp = rs.getString(1);
                    if(old_otp.equals(otp)){
                        return "success";
                    }
                    else {
                        return "otp wrong";
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "network failed";
    }

    public void changePassword(String email, String password) {
        String salt = BCrypt.gensalt(12);
        try {
            String sql = """
                         update [user] set [password] = ? where email = ?
                         """;
            Connection connection = getConnection();
            PreparedStatement ptm = connection.prepareStatement(sql);
            ptm.setString(1, BCrypt.hashpw(password, salt));
            ptm.setString(2, email);
            ptm.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
