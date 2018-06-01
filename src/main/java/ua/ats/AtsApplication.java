package ua.ats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ua.ats.dao.ProductRepository;
import ua.ats.util.ParseExelForDB;


@SpringBootApplication
public class AtsApplication extends Application {



    private ConfigurableApplicationContext springContext;
    private Parent root;

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(AtsApplication.class);
        springContext.getAutowireCapableBeanFactory().autowireBean(this);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/view.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Calculation");
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.stop();
    }

    public static void main(String[] args) {
        launch(AtsApplication.class, args);


        /* ConfigurableApplicationContext context = SpringApplication.run(AtsApplication.class);
         ProductRepository repository = context.getBean(ProductRepository.class);
        ParseExelForDB parseExelForDB = new ParseExelForDB();
        parseExelForDB.parseExel(repository);*/
    }
}

