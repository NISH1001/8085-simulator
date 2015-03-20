/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg8085;

 
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import pkg8085.core.*;

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
    
   
     
    BorderPane border ;
    static MemoryModule memory ;
    TableView<memorytableView> memory_table ;
    TableView<memorytableView> IO_table ;
    static final ObservableList<memorytableView> memory_data = FXCollections.observableArrayList();
    static final ObservableList<memorytableView> IO_data = FXCollections.observableArrayList();
    
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
        
        
        Label registerlabel = new Label("Register") ;
        Label flaglabel = new Label("Flag") ;
        Button aboutBtn = new Button("About") ;
        ToggleButton fullscreen = new ToggleButton("Fullscreen") ;
        Button btn2 = new Button("2 bro") ;
        
        
         
        //action of elements
        aboutBtn.setOnAction(e -> alertBox.displayAbout());
        fullscreen.setOnAction(e -> {
            if(primaryStage.isFullScreen())
                primaryStage.setFullScreen(false);
            else
                primaryStage.setFullScreen(true);
                });
        
        //frame for top bar(eg about)
        HBox topbar = new HBox() ;
        topbar.setPadding(new Insets(5,5,5,5));
        topbar.getChildren().addAll(aboutBtn,fullscreen) ;
        
        //frame(tabs) for editor
                TabPane editorframe = new TabPane();
                editorframe.setPadding(new Insets(10,10,10,10));

                //tabs
                Tab editor_tab = new Tab("              Editor                                ") ;
                editor_tab.setClosable(false);
                Tab hexeditor_tab = new Tab("              Hex                                ") ;
                hexeditor_tab.setClosable(false);

                //vbox for both
                VBox editor_tab_box = new VBox() ;
                editor_tab_box.setSpacing(10);
                
                VBox hexeditor_tab_box = new VBox() ;
                hexeditor_tab_box.setMinHeight(800);
                hexeditor_tab_box.setSpacing(10);
                
                HBox editor_button_box = new HBox() ;
                HBox hexeditor_button_box = new HBox() ;
                editor_button_box.setSpacing(10);
                hexeditor_button_box.setSpacing(10);
                
                //textarea
                TextArea editor = new TextArea() ;
                TextArea hexeditor = new TextArea() ;
                
                Button editor_run = new Button("RUN") ;
                Button hexeditor_run = new Button("RUN") ;
                Button editor_run_step = new Button("Step") ;
                Button hexeditor_run_step = new Button("Step") ;
                Button editor_run_step_next = new Button("Next") ;
                Button hexeditor_run_step_next = new Button("Next") ;
                Button editor_run_step_prev = new Button("Previous") ;
                Button hexeditor_run_step_prev = new Button("Previous") ;
                
                
                editor.setMinHeight(400);
                hexeditor.setMinHeight(400);
                
                editor_run_step_next.setVisible(false);
                editor_run_step_prev.setVisible(false);
                hexeditor_run_step_next.setVisible(false);
                hexeditor_run_step_prev.setVisible(false);
                
                editor_run.setOnAction(e-> {});
                hexeditor_run.setOnAction(e-> {});
                editor_run_step.setOnAction(e->{
                    editor_run_step_next.setVisible(true);
                    editor_run_step_prev.setVisible(true);
                    //other method here ..loading to memory kind of stuff
                }) ;
                hexeditor_run_step.setOnAction(e->{
                    hexeditor_run_step_next.setVisible(true);
                    hexeditor_run_step_prev.setVisible(true);
                    //other method here ..loading to memory kind of stuff
                }) ;
                editor_run_step_next.setOnAction(e->{
                    if(true){//end of execution
                        editor_run_step_next.setVisible(false);
                        editor_run_step_prev.setVisible(false);
                    }else{
                        
                    }
                });
                hexeditor_run_step_next.setOnAction(e->{
                    if(true){//end of execution
                        hexeditor_run_step_next.setVisible(false);
                        hexeditor_run_step_prev.setVisible(false);
                    }else{
                        
                    }
                });
                editor_run_step_prev.setOnAction(e->{});
                hexeditor_run_step_prev.setOnAction(e->{});
                
                editor_button_box.getChildren().addAll(editor_run,editor_run_step,editor_run_step_prev,editor_run_step_next);
                hexeditor_button_box.getChildren().addAll(hexeditor_run,hexeditor_run_step,hexeditor_run_step_prev,hexeditor_run_step_next);
//        editor.setMinHeight(500);
                editor_tab_box.getChildren().addAll(editor,editor_button_box) ;
                hexeditor_tab_box.getChildren().addAll(hexeditor,hexeditor_button_box) ;
                
                editor_tab.setContent(editor_tab_box);
                hexeditor_tab.setContent(hexeditor_tab_box);
                editorframe.getTabs().addAll(editor_tab,hexeditor_tab) ;
        
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

        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());
        
        
        
        //
        //parser object
        Parser p = new Parser();

        int start_addr = 8000;

        //if parser is successful get memory
        if(p.Initialize("test.txt", start_addr))
        {
            p.ShowOriginalLines(editor);
            System.out.println("--------------------------");
            p.ShowData();
            p.WriteToMemory(memory, start_addr);
                //initial table from 8000
               memory_table_update(start_addr);
        }

        
        primaryStage.setTitle("8085 Simulator :P");
        primaryStage.setScene(scene);
        primaryStage.show();
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
}
