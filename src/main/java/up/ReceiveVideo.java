package up;


import org.atsign.client.api.AtClient;
import org.atsign.common.AtException;
import org.atsign.common.AtSign;
import org.atsign.common.KeyBuilders;
import org.atsign.common.Keys;
import org.atsign.common.Keys.PublicKey;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import org.atsign.common.Keys.SharedKey;
import java.awt.FlowLayout;
import javax.swing.JLabel;
//import static up.constants.*;


public class ReceiveVideo extends AtException implements Runnable{
    JLabel label;
    Keys.AtKey key;
    AtClient atClient;
    AtSign atSign;
    String prev;
    JFrame frame;
    boolean publicKey;
    PublicKey pk;
    SharedKey sk;   
    public ReceiveVideo(String  theirAt, String pk){
        super("atsign exception");
        this.atSign = new AtSign(theirAt);
        this.key = new KeyBuilders.PublicKeyBuilder(this.atSign).key(pk).build();
        this.pk = new KeyBuilders.PublicKeyBuilder(this.atSign).key(pk).build();
        this.publicKey = true;
        AtSign MyAtsign = new AtSign("@22easy");
        try {
            atClient = AtClient.withRemoteSecondary("root.atsign.org:64", MyAtsign);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not get to at client");
        }
    


        this.label = new JLabel();
        this.frame = new JFrame();
        prev = null;
    }
    public ReceiveVideo(String me, String sharedby, String sk){
        super("atsign exception");
        this.atSign = new AtSign(me);
        AtSign SHARED_BY = new AtSign(sharedby);
        this.sk = new KeyBuilders.SharedKeyBuilder( SHARED_BY, atSign).key(sk).build();
        try {
            atClient = AtClient.withRemoteSecondary("root.atsign.org:64", atSign);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not get to atclient");
        }
    


        this.label = new JLabel();
        this.frame = new JFrame();
        prev = null;

    }

    public void run(){
        while (true){
            try {
                String data = null;
                if (this.publicKey){
                    String command = "plookup:bypassCache:true:" + this.pk.name + this.pk.sharedBy.toString();
                    data = atClient.executeCommand(command, false).data;
                }
                else {
                    data = atClient.get(sk).get();
                }

                if (!data.equals(prev)) {
                    
                    prev = data;
                    byte[] bytes = Base64.getDecoder().decode(data);
                    System.out.println(bytes);
                    //turn byte array to ByteArrayInputStream
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
                    // turn the ByteArrayInputStream into a bufferImage
                    BufferedImage bImage2 = ImageIO.read(byteStream);
                    ImageIcon icon = new ImageIcon(bImage2);
                    label.setIcon(icon);
                    this.frame.add(label);
                    this.frame.setVisible(true);
                    this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    this.frame.setLayout(new FlowLayout());
                    this.frame.setSize(640,480);
        
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not get string from server");
            }
        }
    }
    public static void main( String[] args )
    {

        ReceiveVideo receiveVideo= new ReceiveVideo("", "demo");
        Thread rVideo = new Thread(receiveVideo);
        rVideo.start();
    }
}