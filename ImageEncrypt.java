import java.awt.image.BufferedImage;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class ImageEncrypt{

    private boolean verbose=false;
    private Random generator;

    private Cipher cipher;
    private SecretKeySpec skeySpec;

    ImageEncrypt() {

        try{
            generator = new Random();

            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);

            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            skeySpec = new SecretKeySpec(raw, "AES");

            cipher = Cipher.getInstance("AES/ECB/NoPadding");

        }catch(Exception e){ System.out.println("ERROR: " + e);}

    }

    public void setKey(byte [] key){

        skeySpec = new SecretKeySpec(key,"AES");
    }

    byte [] getKey(){ return skeySpec.getEncoded();}

    public BufferedImage map(BufferedImage image,boolean encrypt,boolean trick) throws Exception{



        if(image.getWidth() % 2 != 0 || image.getHeight() % 2 != 0){
            throw(new Exception("Image size not multiple of 2 :("));
        }

        BufferedImage encImage = new BufferedImage(image.getWidth(),image.getHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);

        if(encrypt){
            System.out.println("Encrypting Image ... trick=" + trick);//systemoutprintln was meant to be for a command line users
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        }
        else{
            System.out.println("Decrypting Image ... trick=" + trick);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        }

        for(int x=0;x<image.getWidth(); x+=2){
            for(int y=0;y<image.getHeight(); y+=2){
                if(verbose) System.out.println("Block: (" + x+","+y+") -----");

                int counter =0;
                byte [] pixelBytes = new byte[16];

                for (int i=0;i<2;i++){
                    for (int j=0;j<2;j++){
                        int val = image.getRGB(x+i,y+j);
                       if(trick && encrypt) val +=x*y;
                        byte [] sub  = intToByteArray(val);

                        if(verbose){
                            System.out.println("Val: " + val + " Bytes: ");
                            printByteArray(sub);
                        }
                        for(int k=0;k<4;k++) pixelBytes[(counter)*4+k] = sub[k];
                        counter++;
                   }
                }

                byte [] enc = cipher.doFinal(pixelBytes);
                if(verbose){
				    printByteArray(pixelBytes);
					printByteArray(enc);
                }
                counter =0;

                for (int i=0;i<2;i++){
                  for (int j=0;j<2;j++){
                     byte [] sub = new byte[4];
					for(int k=0;k<4;k++) 
					sub[k] = enc[(counter)*4+k];

                int val = byteArrayToInt(sub);
                if(trick && !encrypt) val -=x*y;

                encImage.setRGB(x+i,y+j,val);

                counter++;
                    }
                }
            }
        }
        return encImage;
    }

    public static final byte[] intToByteArray(int value)
    {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static final int byteArrayToInt(byte [] b)
    {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    public static void printByteArray(byte [] array)
    {
        System.out.print("{");
        for(int i=0;i<array.length;i++)
            System.out.print(" " + array[i]);
        System.out.println(" }");
    }
}
