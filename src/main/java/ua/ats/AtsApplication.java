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

//@Lazy
@SpringBootApplication
//@Configuration
//@EnableAutoConfiguration
//@EnableJpaRepositories
public class AtsApplication  extends Application {         //AbstractJavaFxApplicationSupport {

   /* public static void main(String[] args) {
        //SpringApplication.run(AtsApplication.class, args);

       *//* ConfigurableApplicationContext context = SpringApplication.run(AtsApplication.class);
        ProductRepository repository = context.getBean(ProductRepository.class);

        System.out.println();
//        repository.findByArticul("SY.C50.G07").forEach(System.out::println);

        System.out.println("HHHHHHHHHHHHHHHHHHHH");
        System.out.println(repository.findProductByArticul("SY.C50.G07"));
        System.out.println();


        Calculation calculation = new Calculation();*//*

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/view.fxml"));
    }*/


   /* @Value("${ui.title:JavaFX приложение}")//
    private String windowTitle;

    @Autowired
    private ConfigurationControllers.View view;
    //private ConfigurationControllers.View view;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view.getView()));            //getParent()));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }*/





  /*  private ConfigurableApplicationContext context;
    private Parent rootNode;

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AtsApplication.class);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ats/view/fxml/view.fxml"));
        loader.setControllerFactory(context::getBean);
        rootNode = loader.load();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double width = visualBounds.getWidth();
        double height = visualBounds.getHeight();

        primaryStage.setScene(new Scene(rootNode, width, height));
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }

    public static void main(String[] args) {
        launch(AtsApplication.class, args);
    }*/




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
        primaryStage.setTitle("Hello World");
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
    }
}

