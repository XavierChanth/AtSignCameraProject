package up;


import org.atsign.client.api.AtClient;
import org.atsign.common.AtException;
import org.atsign.common.AtSign;
import org.atsign.common.KeyBuilders;
import org.atsign.common.Keys;
import org.atsign.common.Keys.AtKey;
import org.atsign.common.Keys.PublicKey;
import java.util.Base64;
import org.atsign.common.Keys.SharedKey;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import static up.constants.*;
public class ReceiveAudio extends AtException implements Runnable{
    AtClient atClient;
    Keys.AtKey key;
    boolean publicKey;
    PublicKey pk;
    SharedKey sk;
    AtSign atSign, SHARED_WITH;
    String s;
    String prev;
    SourceDataLine sourceDataLine;
    public ReceiveAudio(String  theirAt, String pk){
        super("atsign exception");
        this.atSign = new AtSign(theirAt);
        this.key = new KeyBuilders.PublicKeyBuilder(this.atSign).key(pk).build();
        this.pk = new KeyBuilders.PublicKeyBuilder(this.atSign).key(pk).build();
        this.publicKey = true;
        this.prev = null;
        AtSign MyAtsign = new AtSign("@22easy");
        try {
            atClient = AtClient.withRemoteSecondary("root.atsign.org:64", MyAtsign);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not get to at client");
        }
        try {
            this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(SourceDataLineInfo);
            this.sourceDataLine.open(format);
            this.sourceDataLine.start();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public ReceiveAudio(String me, String theirAt, String sk){
        super("atsign exception");
        this.atSign = new AtSign(me);
        SHARED_WITH = new AtSign(theirAt);
         this.sk = new KeyBuilders.SharedKeyBuilder(atSign, SHARED_WITH).key(sk).build();
        
        this.publicKey = false;
        AtSign MyAtsign = new AtSign("@22easy");
        try {
            atClient = AtClient.withRemoteSecondary("root.atsign.org:64", MyAtsign);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not get to at client");
        }
        try {
            this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(SourceDataLineInfo);
            this.sourceDataLine.open(format);
            this.sourceDataLine.start();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void run() {
        while (true){
            try{
                String data = null;
                if (this.publicKey){
                    String command = "plookup:bypassCache:true:" + this.pk.name + this.pk.sharedBy.toString();
                    data = this.atClient.executeCommand(command, false).data;
                }
                else {
                    data = this.atClient.get(sk).get();
                }
                if (!data.equals(prev)) {
                prev = data;
                byte[] bytes = Base64.getDecoder().decode(data);
                System.out.println(bytes);
                sourceDataLine.write(bytes, 0, bytes.length);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not get audio");
            }
            
        }
    }

    public static void main( String[] args )
    {
        
        ReceiveAudio receive = new ReceiveAudio("", "test3");
        Thread rec = new Thread (receive);
        rec.start();
    }
}