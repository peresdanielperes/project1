package controllers;

import dao.ProductionConfig;
import io.javalin.http.Context;
import models.Token;
import models.User;
import models.UserRole;
import services.UserService;
import services.UserServiceImpl;
import utilities.AccountCreationEmail;
import utilities.Encryption;

import javax.mail.MessagingException;

/**
 * controller for all users
 * */
public class UserController {
    static UserService userService;

    public UserController() {
        userService = new UserServiceImpl(ProductionConfig.url, ProductionConfig.username, ProductionConfig.password);
    }

    /**
     * get all users from service
     * */
    public void getAllUsers(Context ctx){
        ctx.json(userService.getAll());
    };

    /**
     * get user from service
     * */
    public void getOneUser(Context ctx){
        String username = ctx.pathParam("username");
        ctx.json(userService.getOneByUsername(username));
    };

    /**
     * login user given username and password
     * */
    public void loginUser(Context ctx){
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        User res = userService.validateCredentials(username, Encryption.encrypt(password));
        if(res != null){
            //give user token
            //String token = Encryption.encrypt(res.getUsername());
            //Token t = new Token(token,res.getRole().getId());
            ctx.sessionAttribute("killua",res);
            if(res.getRole().getId() == 1) {
                ctx.redirect("/employee/dashboard");
            }else if(res.getRole().getId() == 2){
                ctx.redirect("/financemanager/dashboard");
            }
        }else {
            ctx.json("invalid credentials");
        }


    };

    /**
     * logout user
     * */
    public void logout(Context ctx){
        ctx.sessionAttribute("killua",null);
        ctx.redirect("/login");
    };

    /**
     * check if user has auth token
     * */
    public void checkSession(Context ctx){
        //check if session
        User user = ctx.sessionAttribute("killua");
        System.out.println("CHECK SESSION: " + user);
        ctx.json(user);
    };

    /**
     * register user as manager
     * */
    public void registerUser(Context ctx) throws MessagingException {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String firstName = ctx.formParam("firstname");
        String lastName = ctx.formParam("lastname");
        String email = ctx.formParam("email");
        String role = ctx.formParam("role");
        UserRole userRole = new UserRole(Integer.parseInt(role));

        //create encrypted password;
        String encPassword = Encryption.encrypt(password);
        System.out.println(encPassword);
        String decPassword = Encryption.decrypt(encPassword);
        System.out.println(decPassword);

        User user = new User(username,encPassword,firstName,lastName,email,userRole);

        //check if username exists in db
        User exists = userService.getOneByUsername(username);
        if(exists != null) {
            ctx.json("username exists");
        }else{
            userService.createOne(user);
            AccountCreationEmail.sendMail(user);

            //ctx.json("user has been created");
            ctx.redirect("/financemanager/dashboard");
        }
        //check if email exists in db
    }
}
