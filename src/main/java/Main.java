import frontcontroller.FrontController;
import io.javalin.Javalin;
//import utilities.Encryption;

/**
 * initialization of project
 * */
public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.enableCorsForOrigin("http://localhost:9001/");
            javalinConfig.addStaticFiles("/frontend");
        }).start(9001).error(404,ctx -> {
            ctx.redirect("/404");
        });

        FrontController frontController = new FrontController(app);

    }
}
