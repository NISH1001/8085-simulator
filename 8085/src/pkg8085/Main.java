/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8085;

 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
//import pkg8085.core.*;

/**
 *
 * @author navin
 */






public class Main extends Application {
   
    
    public static class memorytableView{
        private final SimpleStringProperty address;
        private final SimpleStringProperty value;

        private memorytableView(String ad , String v) {
            this.address = new SimpleStringProperty(ad) ;
            this.value = new SimpleStringProperty(v) ; 
            
        }
        
        public String getAddress(){
            return address.get() ;
        }
        
        public String getValue(){
            return value.get() ;
        }
        
        public void setmemoryValue(String _value){
            value.set(_value); 
            memory.writeByte(Integer.parseInt(this.address.get()), Integer.parseInt(_value,16));
        }
        
        public void setIOValue(String _value){
            value.set(_value); 
            memory.writeByte(Integer.parseInt(this.address.get()), Integer.parseInt(_value));
        }
        
    }
    
   
     
    static BorderPane border ;
    static MemoryModule memory ;
    static Processor proc;
    static TableView<memorytableView> memory_table ;
    static TableView<memorytableView> IO_table ;
    static final ObservableList<memorytableView> memory_data = FXCollections.observableArrayList();
    static final ObservableList<memorytableView> IO_data = FXCollections.observableArrayList();
    static TabPane editorframe ;
    static HBox topbar;
    static Stage mainStage ;
    static int tabNum = 1 ;
    static Parser parser ;

    
    //register lable
                    static Label registerAvalue = new Label("00") ;                    
                    static Label registerBvalue = new Label("00") ;    
                    static Label registerCvalue = new Label("00") ;
                    static Label registerDvalue = new Label("00") ;
                    static Label registerEvalue = new Label("00") ;
                    static Label registerHvalue = new Label("00") ;
                    static Label registerLvalue = new Label("00") ;
                    static Label registerPvalue = new Label("00") ;
                    static Label registerWvalue = new Label("00") ;
                    static Label registerPC1value = new Label("00") ;
                    static Label registerPC2value = new Label("00") ;
                    static Label registerSP1value = new Label("00") ;
                    static Label registerSP2value = new Label("00") ;
                    
    //PPi label
                    static Label ppiCWvalue = new Label("00") ;
                    static Label ppiPAvalue = new Label("00");
                    static Label ppiPBvalue = new Label("00");
                    static Label ppiPCvalue = new Label("00");


    
    @Override
    public void start(Stage primaryStage) {
        
        Callback<TableColumn<memorytableView, String>, 
             TableCell<memorytableView, String>> cellFactory
                = (TableColumn<memorytableView, String> p) -> new EditingCell();
       
       //elements 
        memory = new MemoryModule(0, 64*1024) ;
        proc = new Processor();
        proc.addDevice(memory);
        
        Label registerlabel = new Label("Register") ;
        Label flaglabel = new Label("Flag") ;
       
        //frame for top bar(eg about)
        topbar = new HBox() ;
        topbar.setPadding(new Insets(5,5,5,5));
        createMenuBar();
        
        //frame(tabs) for editor
                editorframe = new TabPane();
                editorframe.setPadding(new Insets(10,10,10,10));
               editortab primaryTab = new editortab() ;
               
               
        //frame for showing memory //right panel as tab
           VBox left_panel = new VBox() ;
          TabPane tabPane = new TabPane();
          tabPane.setPadding(new Insets(10,10,10,10));

          //tabs
                Tab memory_tab = new Tab();
                Tab IO_tab = new Tab();
                //tab label
                memory_tab.setText("Memory");
                IO_tab.setText("IO Port");
                //inner box for tab used in setContent
                VBox vbox_memory_table = new VBox();
                vbox_memory_table.setAlignment(Pos.CENTER);
                vbox_memory_table.setSpacing(10);
                VBox vbox_IO_table = new VBox();
                vbox_IO_table.setAlignment(Pos.CENTER);
                vbox_IO_table.setSpacing(10);
                
                //box added to tab
                memory_tab.setContent(vbox_memory_table);
                memory_tab.setClosable(false);
                IO_tab.setContent(vbox_IO_table);
                IO_tab.setClosable(false);
                
                //tab added to tabPane(frame for tabs)
                tabPane.getTabs().addAll(memory_tab,IO_tab);
          
                    //for memory
      
           //memory tab contens ..tables 
            memory_table = new TableView() ;
            memory_table.setEditable(true);
            
            //tables column ..declared
            TableColumn<memorytableView, String> memory_table_address = new TableColumn<>("Address");            
            TableColumn<memorytableView, String> memory_table_value = new TableColumn<>("Value");
            
            //width and protertyvalue set ....uses the memorytableView function getAdress() to get the value of the object set in memory_table_update function
            memory_table_address.setMinWidth(130);
            memory_table_address.setCellValueFactory(
                new PropertyValueFactory<>("address"));
            
            memory_table_value.setMinWidth(130);
            memory_table_value.setCellValueFactory(
                new PropertyValueFactory<>("value"));

            //editable cell for value ...some override ....
            memory_table_value.setCellFactory(cellFactory);  
            memory_table_value.setOnEditCommit(
                (CellEditEvent<memorytableView, String> t) -> {
                    ((memorytableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setmemoryValue(t.getNewValue());
            });
            
           
            
            //added data to table 
            memory_table.setItems(memory_data);
            //set columns for the table
            memory_table.getColumns().addAll(memory_table_address,memory_table_value) ;
            
            //changing the content(data) for the table for required value of address
            TextField memory_table_start_address = new TextField() ;
            Button memory_table_start_address_button = new Button("Submit") ;
            memory_table_start_address.setPromptText("Enter Start Adress for memory") ;
            memory_table_start_address_button.setOnAction(e -> {
                String start_address = memory_table_start_address.getText() ;
                memory_table_update(Integer.parseInt(start_address));
            });
            HBox memory_table_start_address_box = new HBox(memory_table_start_address,memory_table_start_address_button) ;
            memory_table_start_address_box.setPadding(new Insets(10,10,10,10));
            memory_table_start_address_box.setSpacing(10);
            vbox_memory_table.getChildren().addAll(memory_table , memory_table_start_address_box);
         
                                                    //for IO
           //memory tab contens ..tables 
            IO_table = new TableView() ;
            IO_table.setEditable(true);
            
            //tables column ..declared
            TableColumn<memorytableView, String> IO_table_address = new TableColumn<>("IO");            
            TableColumn<memorytableView, String> IO_table_value = new TableColumn<>("Value");
            
            //width and protertyvalue set ....uses the memorytableView function getAdress() to get the value of the object set in memory_table_update function
            IO_table_address.setMinWidth(130);
            IO_table_address.setCellValueFactory(
                new PropertyValueFactory<>("address"));
            
            IO_table_value.setMinWidth(130);
            IO_table_value.setCellValueFactory(
                new PropertyValueFactory<>("value"));

            //editable cell for value ...some override ....
            IO_table_value.setCellFactory(cellFactory);  
            IO_table_value.setOnEditCommit(
                (CellEditEvent<memorytableView, String> t) -> {
                    ((memorytableView) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setIOValue(t.getNewValue());
            });
            
            //initial table from 1
            IO_table_update(1);
            
            //added data to table 
            IO_table.setItems(IO_data);
            //set columns for the table
            IO_table.getColumns().addAll(IO_table_address,IO_table_value) ;
            
            //changing the content(data) for the table for required value of address
            TextField IO_table_start_address = new TextField() ;
            Button IO_table_start_address_button = new Button("Submit") ;
            IO_table_start_address.setPromptText("Enter Start Adress for IO") ;
            IO_table_start_address_button.setOnAction(e -> {
                String start_address = IO_table_start_address.getText() ;
                IO_table_update(Integer.parseInt(start_address));
            });
            HBox IO_table_start_address_box = new HBox(IO_table_start_address,IO_table_start_address_button) ;
            IO_table_start_address_box.setPadding(new Insets(10,10,10,10));
            IO_table_start_address_box.setSpacing(10);
            vbox_IO_table.getChildren().addAll(IO_table ,IO_table_start_address_box );
            
            
            
            
            //for error 
            TextArea show_error = new TextArea() ;
            show_error.setEditable(false);
            show_error.setMaxWidth(2*130);
            VBox show_error_box = new VBox() ;
            show_error_box.setPadding(new Insets(10,10,10,10));
            show_error_box.setSpacing(10);
            show_error_box.getChildren().addAll(new Label("Errors"),show_error) ;
            //added tabPane to the left panel 
            left_panel.getChildren().addAll(tabPane,show_error_box) ;

            
            
            
        
        
        
        //frame for showing register
        GridPane registerframe = new GridPane();
        registerframe.setPadding(new Insets(10,10,10,10)) ;
        registerframe.setAlignment(Pos.TOP_LEFT);
        registerframe.setHgap(10);
        registerframe.setVgap(10);
        
        //Register Vbox
            HBox registerframeMain = new HBox() ;
                 registerframeMain.setSpacing(20);
                 registerframeMain.getStyleClass().add("registershow");        
            
           VBox registergeneral = new VBox() ;
                   Label registerLabel = new Label("Registers") ;
                 registergeneral.setSpacing(10);
                 registergeneral.getStyleClass().add("registerinnershow");        
                 
            VBox registerflag = new VBox() ;
                   Label registerflagLabel = new Label("Flag") ;
                 registerflag.setSpacing(10);
                 registerflag.getStyleClass().add("registerinnershow");        
                 
                 
                //general register
                int registergap = 30 ;
                HBox registerA = new HBox() ;
                    registerA.setSpacing(registergap*2);
                    registerA.setAlignment(Pos.CENTER);
                    Label registerALabel = new Label("A") ;
                    registerA.getChildren().addAll( registerAvalue) ;
                HBox registerBC = new HBox() ;
                    registerBC.setSpacing(registergap);
                    Label registerBCLabel = new Label("BC") ;
                    registerBC.getChildren().addAll( registerBvalue,registerCvalue) ;
                HBox registerDE = new HBox() ;
                    registerDE.setSpacing(registergap);
                    Label registerDELabel = new Label("DE") ;
                    registerDE.getChildren().addAll( registerDvalue,registerEvalue) ;
                HBox registerHL = new HBox() ;
                    registerHL.setSpacing(registergap);
                    Label registerHLLabel = new Label("HL") ;
                    registerHL.getChildren().addAll( registerHvalue,registerLvalue) ;
                HBox registerPSW = new HBox() ;
                    registerPSW.setSpacing(registergap);
                    Label registerPSWLabel = new Label("PSW") ;
                    registerPSW.getChildren().addAll( registerPvalue,registerWvalue) ;
                HBox registerPC = new HBox() ;
                    registerPC.setSpacing(registergap);
                    Label registerPCLabel = new Label("PC") ;
                    registerPC.getChildren().addAll( registerPC1value,registerPC2value) ;                
                HBox registerSP = new HBox() ;
                    registerSP.setSpacing(registergap);
                    Label registerSPLabel = new Label("SP") ;
                    registerSP.getChildren().addAll( registerSP1value,registerSP2value) ;  
                    
                //fitting general register
                        //fitting genral register label in a vertical form
                        VBox registergenerallabel = new VBox( );
                            registergenerallabel.getChildren().addAll(registerALabel,registerBCLabel,registerDELabel,registerHLLabel,
                                registerPSWLabel,registerPCLabel,registerSPLabel);
                        ////fitting genral register valuein a vertical form
                        VBox registergeneralvalue = new VBox () ;
                            registergeneralvalue.getChildren().addAll(registerA,registerBC,registerDE,registerHL,registerPSW,registerPC,registerSP) ;
                        HBox registergeneralbody = new HBox();
                            registergeneralbody.setSpacing(registergap);
                            //combining both of the above fitting in single horizontal form
                            registergeneralbody.getChildren().addAll(registergenerallabel,registergeneralvalue) ;
                 
                registergeneral.getChildren().addAll(registerLabel,registergeneralbody);
                    
               //flag
               HBox registerflagS = new HBox() ;
                    registerflagS.setSpacing(registergap);
                    Label registerflagSLabel = new Label("S") ;
                    Label registerflagSvalue = new Label("00");
                    registerflagS.getChildren().addAll(registerflagSLabel ,registerflagSvalue) ;
               HBox registerflagZ = new HBox() ;
                    registerflagZ.setSpacing(registergap);
                    Label registerflagZLabel = new Label("Z") ;
                    Label registerflagZvalue = new Label("00");
                    registerflagZ.getChildren().addAll(registerflagZLabel ,registerflagZvalue) ;
               HBox registerflagAC = new HBox() ;
                    registerflagAC.setSpacing(registergap);
                    Label registerflagACLabel = new Label("AC") ;
                    Label registerflagACvalue = new Label("00");
                    registerflagAC.getChildren().addAll(registerflagACLabel ,registerflagACvalue) ;    
               HBox registerflagP = new HBox() ;
                    registerflagP.setSpacing(registergap);
                    Label registerflagPLabel = new Label("P") ;
                    Label registerflagPvalue = new Label("00");
                    registerflagP.getChildren().addAll(registerflagPLabel ,registerflagPvalue) ;
               HBox registerflagC = new HBox() ;
                    registerflagC.setSpacing(registergap);
                    Label registerflagCLabel = new Label("C") ;
                    Label registerflagCvalue = new Label("00");
                    registerflagC.getChildren().addAll(registerflagCLabel ,registerflagCvalue) ;
            
               //fitting flags
                        VBox registerflaglabel = new VBox( );
                            registerflaglabel.getChildren().addAll(registerflagSLabel,registerflagZLabel,registerflagACLabel,
                                    registerflagPLabel,registerflagCLabel);
                        VBox registerflagvalue = new VBox () ;
                            registerflagvalue.getChildren().addAll(registerflagSvalue,registerflagZvalue,registerflagACvalue,
                                    registerflagPvalue,registerflagCvalue) ;
                        HBox registerflagbody = new HBox();
                            registerflagbody.setSpacing(registergap);
                            registerflagbody.getChildren().addAll(registerflaglabel,registerflagvalue) ;
                 
                registerflag.getChildren().addAll(registerflagLabel,registerflagbody);
            
            //final register panel General register and flags
                registerframeMain.getChildren().addAll(registergeneral,registerflag) ;
         //PPI 
                VBox ppi_main_box = new VBox();
                ppi_main_box.setPadding(new Insets(10,10,10,10));
                ppi_main_box.getStyleClass().add("IOshow") ;

                HBox ppi_box = new HBox() ;
                ppi_box.getStyleClass().add("IOinnershow") ;
                VBox ppilabel_box = new VBox() ;
                VBox ppivalue_box = new VBox() ;
                
                
                ppilabel_box.getChildren().addAll(new Label("Control Word") ,new Label("Port A") ,new Label("Port B"), new Label("Port C") );
                ppivalue_box.getChildren().addAll(ppiCWvalue,ppiPAvalue,ppiPBvalue,ppiPCvalue);
                
                ppi_box.getChildren().addAll(ppilabel_box,ppivalue_box) ;
                ppi_main_box.getChildren().addAll(new Label("PPI") , ppi_box);
        //final left panel
        registerframe.add(registerframeMain, 0,0);
        registerframe.add(ppi_main_box, 0,1);
        
       
        
        //border
        border = new BorderPane() ;
        border.setTop(topbar);
        border.setCenter(editorframe);
        border.setLeft(registerframe);
        border.setRight(left_panel);
        Scene scene = new Scene(border, 1000, 600);
        scene.getStylesheets().add("pkg8085/general.css");
        
        //final stage show
            //maximize
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        mainStage = new Stage() ;
        mainStage.setX(bounds.getMinX());
        mainStage.setY(bounds.getMinY());
        mainStage.setWidth(bounds.getWidth());
        mainStage.setHeight(bounds.getHeight());
        
        
        
        //
        //parser object
        //parser = new Parser();

        int start_addr = 0x8000;

        /*if parser is successful get memory
        if(parser.InitializeFile("temp.txt", start_addr))
        {
            parser.ShowOriginalLines();
            System.out.println("--------------------------");
            parser.ShowData();
            parser.WriteToMemory(memory, start_addr);
                //initial table from 8000
               memory_table_update(start_addr);
        }*/

        Timeline updater = new Timeline(new KeyFrame(Duration.
            millis(75), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (proc!=null) {
                    registerAvalue.setText(Integer.toHexString(
                                proc.getRegI("A")));
                    registerBvalue.setText(Integer.toHexString(
                                proc.getRegI("B")));
                    registerCvalue.setText(Integer.toHexString(
                                proc.getRegI("C")));
                    registerDvalue.setText(Integer.toHexString(
                                proc.getRegI("D")));
                    registerEvalue.setText(Integer.toHexString(
                                proc.getRegI("E")));
                    registerHvalue.setText(Integer.toHexString(
                                proc.getRegI("H")));
                    registerLvalue.setText(Integer.toHexString(
                                proc.getRegI("L")));
                    registerPC1value.setText(
                            Integer.toHexString(
                                proc.getRegI("PC")/0x100));
                    registerPC2value.setText(
                            Integer.toHexString(
                                proc.getRegI("PC")%0x100));
                }
            }
        }));
        
        updater.setCycleCount(Timeline.INDEFINITE);

        mainStage.setTitle("8085 Simulator :P");
        mainStage.setScene(scene);
        mainStage.show();
        updater.play();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
 
    
    public static void memory_table_update(int start_address){
        memory_data.clear();
        for(int i = start_address ; i < start_address+60 ; i++){
            String value_HEX = Integer.toHexString((int)memory.readByte(i)) ;
            if(value_HEX.length()>2)
                value_HEX = value_HEX.substring(value_HEX.length()-2) ;
            memory_data.add(new memorytableView( Integer.toString(i) , value_HEX )) ;
        }
        
    }
    
    public static void IO_table_update(int start_address){
        IO_data.clear();
        for(int i = start_address ; i < start_address+60 ; i++){
            String value_HEX = Integer.toHexString((int)memory.readByte(i)) ;
            IO_data.add(new memorytableView( Integer.toString(i) , value_HEX )) ;
        }
        
    }
    
    private void createMenuBar() {
        MenuBar menubar = new MenuBar();

        Menu file = new Menu("File");
        Menu more = new Menu("More");
        
        MenuItem aboutBtn = new MenuItem("About") ;
        MenuItem fullscreen = new MenuItem("Fullscreen") ;
        fullscreen.setAccelerator(KeyCombination.keyCombination("Ctrl+shift+f"));
        
        aboutBtn.setOnAction(e -> alertBox.displayAbout());
        fullscreen.setOnAction(e -> {
            if(mainStage.isFullScreen())
                mainStage.setFullScreen(false);
            else
                mainStage.setFullScreen(true);
                });
        more.getItems().addAll(aboutBtn,fullscreen) ;
        
        MenuItem item_f_new = new MenuItem("New");
        item_f_new.setOnAction((ActionEvent t)-> {NewFileAction();});
        item_f_new.setAccelerator(KeyCombination.keyCombination("Ctrl+n"));
        MenuItem item_f_open = new MenuItem("Open");
        item_f_open.setAccelerator(KeyCombination.keyCombination("Ctrl+o"));
        item_f_open.setOnAction(e -> {
                try {
                    OpenFileAction();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
        });
        MenuItem item_f_save = new MenuItem("Save");
        item_f_save.setAccelerator(KeyCombination.keyCombination("Ctrl+s"));
        item_f_save.setOnAction(e -> {
            saveFileAction();
        });
        MenuItem item_f_exit = new MenuItem("Exit");
        item_f_exit.setAccelerator(KeyCombination.keyCombination("Ctrl+q"));
        item_f_exit.setOnAction(e -> {
                  System.exit(0);
        } );

        file.getItems().addAll(item_f_new,item_f_open,item_f_save,item_f_exit);
        
        menubar.getMenus().addAll(file,more);
        topbar.getChildren().addAll(menubar);
    }
    
    public void  OpenFileAction() throws FileNotFoundException, IOException  {
            FileChooser fdia = new FileChooser();
            ExtensionFilter filter = new ExtensionFilter(
                    "Assembly files", "asm" , "txt");
            fdia.setSelectedExtensionFilter(filter);
             File file = fdia.showOpenDialog(mainStage);
            if (file != null) {
                editortab opentab = new editortab();
                opentab.setText(file.getName());
                BufferedReader reader = new BufferedReader( new FileReader(file.getAbsolutePath()));
                String text = "" , line ;
                while((line = reader.readLine()) != null){
                    text += line+"\n" ;
                }
                opentab.seteditor(text);
            }
        
    }
    
    
    public void saveFileAction(){
              FileChooser fileChooser = new FileChooser();
  
              //Set extension filter
              FileChooser.ExtensionFilter extFilter =
                  new FileChooser.ExtensionFilter(
                          "TXT files (*.txt)", "*.txt");
              fileChooser.getExtensionFilters().add(extFilter);
              
              //Show save file dialog
              File file = fileChooser.showSaveDialog(
                      mainStage);
              
              if(file != null){
                  String string = ((editortab)editorframe.getSelectionModel().getSelectedItem()).geteditor() ;
                  System.out.print(string);
                  SaveFile(string, file);
              }
    }
    public void NewFileAction()  {
        editortab editortab = new editortab();
    }
     
    
    public void start_parser(TextArea editor){
            try {
                PrintWriter just_write =
                    new PrintWriter("temp.txt");
                just_write.println(editor.getText());
                just_write.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).
                    log(Level.SEVERE, null, ex);
            }

        int start_addr = 0x8000;
        MultipleParser mp = new MultipleParser(memory);
        mp.initializeString(editor.getText(),start_addr);
        proc.setRegI("PC",start_addr);
        Thread proc_thread = new Thread(proc);
        proc_thread.start();
        memory_table_update(start_addr);
    }

    
    
    class editortab extends Tab {
        
        public final TextArea editor ;
        
        public editortab() {
                setText("New Tab "+tabNum++);
                setClosable(true);
                
                setOnCloseRequest(e->{
                    if(!alertBox.display_warning("Are you sure"))
                        e.consume();
                });
                
                //vbox for both
                VBox editor_tab_box = new VBox() ;
                editor_tab_box.setSpacing(10);
                
                HBox editor_button_box = new HBox() ;
                editor_button_box.setSpacing(10);
                
                
                //textarea
                editor = new TextArea() ;
                
                Button editor_run = new Button("RUN") ;
                Button editor_run_step = new Button("Step") ;
                Button editor_run_step_next = new Button("Next") ;
                Button editor_run_step_prev = new Button("Previous") ;
                
                
                editor.setMinHeight(400);
                
                editor_run_step_next.setVisible(false);
                editor_run_step_prev.setVisible(false);
                
                editor_run.setOnAction(e-> {
                    start_parser(editor);        
                });
                editor_run_step_next.setOnAction(e->{
                    if(true){//end of execution
                        editor_run_step_next.setVisible(false);
                        editor_run_step_prev.setVisible(false);
                    }else{
                        
                    }
                });
                editor_run_step_prev.setOnAction(e->{});
                
                editor_button_box.getChildren().addAll(editor_run,editor_run_step,editor_run_step_prev,editor_run_step_next);
                //        editor.setMinHeight(500);
                editor_tab_box.getChildren().addAll(editor,editor_button_box) ;
                
                setContent(editor_tab_box);
                editorframe.getTabs().addAll(this) ;
        }

        public void seteditor(String text) {
            editor.setText(text);
        }
        
        public String geteditor() {
            return editor.getText();
        }
    }
    
    
    
    
    
    
    
class EditingCell extends TableCell<memorytableView, String> {
 
        private TextField textField;
 
        public EditingCell() {
        }
 
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }
 
        @Override
        public void cancelEdit() {
            super.cancelEdit();
 
            setText((String) getItem());
            setGraphic(null);
        }
 
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }
 
        
        
    
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()* 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                  
                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        if(textField.getText().matches("^([A-Fa-f0-9]{2})$"))
                            commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
            
            textField.focusedProperty().addListener(
                (ObservableValue<? extends Boolean> arg0, 
                Boolean arg1, Boolean arg2) -> {
                    if (!arg2) {
                        if(textField.getText().matches("^([A-Fa-f0-9]{2})$"))
                            commitEdit(textField.getText());
                    }
            });
        }
 
        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }    

     private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
}
