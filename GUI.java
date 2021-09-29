import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
//packages to use images from files
import javax.imageio.ImageIO;
import java.io.File;
//
import java.util.Random;
//java cryptographic extenaion Engines
import javax.crypto.*;	
import javax.crypto.spec.*;

//Main class which holds the main method
//inhrits from JFrame and Implements action listner
class GUI extends JFrame implements ActionListener{

    private ImageRead panel; //Variable ImageRead class
    private ImageEncrypt encrypter; //variable Image Encrypt class
    private File fileName;

    public GUI(){

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Image encrypt and decrypt");
        setLayout(new BorderLayout());
        panel = new ImageRead(); //Image Read object
        getContentPane().add(panel); //adds ImageRead object(panel)
        pack(); //fits the size
        setJMenuBar(MainMenu()); //calls the MainMenu method

        setSize(new Dimension(550, 550));
        encrypter = new ImageEncrypt();
    }

   /**Main Menu constructs the menu and adds action listner
    * */
    private JMenuBar MainMenu(){

        JMenuBar menuBar = new JMenuBar();
	//the three main menus
        JMenu file, menu;
        JMenuItem open, save, saveas, close,		//menu items 
                setkey, Encrypt, Decrypt, content;
 
        file = new JMenu("File");
        menu = new JMenu("Action");

        open = new JMenuItem("Open  ..", new ImageIcon("./icon/folder.png"));
        save = new JMenuItem("Save", new ImageIcon("./icon/save.png"));
        saveas = new JMenuItem("Save as   ..");
        close = new JMenuItem("Close ", new ImageIcon("./icon/close.png"));

        setkey = new JMenuItem("Secret key", new ImageIcon("./icon/key.png"));
        Encrypt = new JMenuItem("Encrypt Image", new ImageIcon("./icon/lock.png"));
        Decrypt = new JMenuItem("Decrypt Image", new ImageIcon("./icon/unlock.png"));

        

        //adds the items to their related menu containers      
        file.add(open); file.addSeparator(); file.add(save); file.add(saveas); file.addSeparator();file.add(close);
        menu.add(setkey); menu.addSeparator(); menu.add(Encrypt); menu.add(Decrypt);
        
        //Adding the jmenues to jmenu bar
        menuBar.add(file);      menuBar.add(menu); 

        //Adding action listner for the menu items
        open.addActionListener(this);       setkey.addActionListener(this);     close.addActionListener(this);
        save.addActionListener(this);       Encrypt.addActionListener(this);
        saveas.addActionListener(this);     Decrypt.addActionListener(this);    
            return menuBar;
    }

    /** set File methd for implementing encapsuletion
     * accesing private variables with public methods
     */
    public void setFile(File file){
        fileName = file;
    }

    /**		 Action listener
     * overiding the actionPerformed method of the abstract class ActionListner
     *  **/
    public void actionPerformed(ActionEvent action) {

        String text = action.getActionCommand();

        try{
            
            if(text == "Open  .."){ actionLoadImage(null); } //open menu item listner
            else if(text == "Save"){ actionSaveImage(fileName); } //overites the file with the new image
            else if(text == "Save as   .."){ actionSaveImage(null);} //passes null for file name to add name by user 
            else if(text == "Close "){ System.exit(0);} //exits the program
            else if(text == "Secret key"){  
              actionKeyDialog(); //calls the actionDialog method which shows joption pane to accept key
            }
            else if(text == "Encrypt Image"){
                panel.setImage(encrypter.map(panel.getImage(),true,false)); //Encrypt action listner
            }
            else if(text == "Decrypt Image"){
                panel.setImage(encrypter.map(panel.getImage(),false,false)); //Decrypt action listner
            }
         
        }catch(Exception err)
        { System.out.println("ERROR:" + err);}
    }
    /** Set the key **/
    public void actionKeyDialog(){
        String key = new String(encrypter.getKey());

        key = (String)JOptionPane.showInputDialog(this,
                "Enter a 16 byte secret key (current key= " +
                        key.getBytes().length + " bytes) \n Do not share this secret key with anyone",key);

        while(key != null && key.getBytes().length != 16){

            key = (String)JOptionPane.showInputDialog(this,
                    "Enter a 16 byte secret key (current key= " +
                            key.getBytes().length + " bytes) \n Do not share this secret key with anyone" ,key);
        }

        if(key != null) encrypter.setKey(key.getBytes());
    }

    /** Load an image from a file
     */
    public void actionLoadImage(File imageFile){

        if(imageFile == null){
            JFileChooser fc = new JFileChooser(fileName);
            fc.setControlButtonsAreShown(false);
            fc.showOpenDialog(this);
            imageFile = fc.getSelectedFile();
        }
		//sets the file if it is not null
        if(imageFile != null){

            panel.setImage(imageFromFile(imageFile));
            setFile(imageFile);
        }
    }

    /** Load an image from a file
     */
    private BufferedImage imageFromFile(File file){

        BufferedImage img = null;
        try{
            img = ImageIO.read(file);
        }catch(Exception e){
            System.out.println("Error:" + e);
        }
        return img;
    }


    /** Save an image from a file
     * @param file the name of the file to save, use "null" to access a dialog
     */
    public void actionSaveImage(File imageFile){

        if(imageFile == null){
            JFileChooser filechooser = new JFileChooser(fileName);
            filechooser.showSaveDialog(this);
            imageFile = filechooser.getSelectedFile();
        }

        if(imageFile != null){
            try{
                ImageIO.write(panel.getImage(), "png", imageFile);
            }catch(Exception e){
                System.out.println("Error:" + e);
            }
            setFile(imageFile);
        }
    }
   
    /** Main function **/
    public static void main(String args[])
    {
        GUI win = new GUI();
        win.setVisible(true);
	//cli for setting image file with argument parameter
        if(args.length > 0){
            win.actionLoadImage(new File(args[0]));
        }
    }
}

//	ImageRead class draws image panel
// as well as override the paintcomponent which is called automatically in background
class ImageRead extends JPanel{

    private BufferedImage image;

    public ImageRead()
    {
        this.image = null;

        setFocusable(true);

        setLayout(null);
        setOpaque(true);

    }

    /** Sets the main Image **/
    public void setImage(BufferedImage image){

        this.image = image;
        repaint();
    }

    /** returns the image
     * image geter methd
     */
    public BufferedImage getImage(){
        return image;
    }

    /** Overwriding paint event for drawing 
     * 
     * the paintComponent is called internally when images change positions by 
     * resizing of jframe or changing focus. No method call for PaintComponent.
     * **/
    public void paintComponent(Graphics g) {
        g.setColor(new Color(34, 33, 33));
        g.fillRect(0,0,getSize().width,getSize().height);

        if(image != null){

            int center_x = getSize().width/2 - image.getWidth() /2;
            int center_y = getSize().height/2 - image.getHeight() /2;

            if(center_x < 10){ center_x = 10;}
            if(center_y < 10){ center_y = 10;}

            g.drawImage(image,center_x,center_y,null);
        }
    }
}

