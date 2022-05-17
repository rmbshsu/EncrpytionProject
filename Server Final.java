import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Server{


	public Server(int port) throws IOException{
	ServerSocket serverSocket = null;
	System.out.println("Server started");

    System.out.println("Waiting for a client ...");

        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            socket = serverSocket.accept();
            System.out.println("Client accepted");
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
			//path to the cipher text file
            out = new FileOutputStream("ciphertext.txt");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[16*1024];

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
        serverSocket.close();
        System.out.println("Ciphertext file saved");
        System.out.println("Closing connection");


    String filename;
    String message = "";

    //creating a decrypted file to be able to write to it
       try{
          File myDecryptMsg = new File ("decrypted.txt");
          if (myDecryptMsg.createNewFile()) {
             System.out.println("File created: " + myDecryptMsg.getName());
          } else {
             System.out.println("File already exists.");
          }
       } catch (IOException e) {
          System.out.println("Error in creating file.");
       }

       //Open ciphertext file, read data from it
       String line = "";

       //Read input file and get message data, store in String called data
       try{
 	        File myMsg = new File("ciphertext.txt");
 	        Scanner fileRead = new Scanner (myMsg);
 	           while(fileRead.hasNextLine())
 	           {
 	              String data = fileRead.nextLine();
 	              line += data;
 	           }
 	           fileRead.close();
 	      } catch (FileNotFoundException e) {
 	           System.out.println("Error in reading file.");
 	           e.printStackTrace();
          }

    try{
       File myDecryptMsg = new File ("decrypted.txt");
       if (myDecryptMsg.createNewFile()) {
          System.out.println("File created: " + myDecryptMsg.getName());
       } else {
          System.out.println("File already exists.");
       }
    } catch (IOException e) {
       System.out.println("Error in creating file.");
       }

    try{
       FileWriter decrypter = new FileWriter ("decrypted.txt");
       decrypter.write(decryptMessage(line));
       decrypter.close();
       System.out.println("Successfully created decrypted file.");
    } catch (IOException e) {
       System.out.println("An error occured during decryption.");
       e.printStackTrace();
      }


    }


   //Method for decrypting file
   //Add 19 X
   //Flip bits 2 and 6 X
   //Subtract 7 X
   //Swap bits 1 and 5 X
   //Rotate 2 bits right X
   private static char[] decryptMessage (String data) {
      char [] ch = data.toCharArray();
      int currentChar;
      int tempNum = 0;
      String binary;
      final char ZERO = '0';
      final char ONE = '1';

      for (int i = 0; i < ch.length; i++)
      {
         currentChar = ch[i];
         binary = Integer.toBinaryString(currentChar);
         //convert all chars into 8-bit representations
         //adding bits less than 8
         switch (binary.length()){
            case 4: binary = "0000" + binary;
                    break;
            case 5: binary = "000" + binary;
                    break;
            case 6: binary = "00" + binary;
                    break;
            case 7: binary = "0" + binary;
                    break;
            }

		  // Add 19
 	      //1st - binary string to integer then subtract and check
          try {
            tempNum = Integer.parseInt(binary, 2);
             }
         catch (NumberFormatException e)
            {
            System.out.println("Error. Invalid binary string.");
            }
         tempNum = tempNum + 19;
         //overflow check
         if (tempNum > 255){
         tempNum = tempNum - 256;
         }

         //Flip bits 2 and 6
         //back to binary String, and add back leading 0's to get 8 bits
         binary = Integer.toBinaryString(tempNum);
         switch (binary.length()){
            case 1: binary = "0000000" + binary;
                    break;
            case 2: binary = "000000" + binary;
                    break;
            case 3: binary = "00000" + binary;
                    break;
            case 4: binary = "0000" + binary;
                    break;
            case 5: binary = "000" + binary;
                    break;
            case 6: binary = "00" + binary;
                    break;
            case 7: binary = "0" + binary;
                    break;
            }
         // If pos 2 is a 1, flip it to 0, else flip to 1
         if(binary.charAt(2) == ONE){
            binary = flipChar (binary, 2, ZERO);
         } else binary = flipChar (binary, 2, ONE);
         //if pos 6 is a 1, flip to 0, else flip to 1
         if(binary.charAt(6) == ONE){
            binary = flipChar (binary, 6, ZERO);
         } else binary = flipChar (binary, 6, ONE);

         //subtract 7
         try {
            tempNum = Integer.parseInt(binary, 2);
             }
         catch (NumberFormatException e)
            {
            System.out.println("Error. Invalid binary string.");
            }
         tempNum = tempNum - 7;
         //System.out.println("Step 3: " + binary);
         //underflow check
         if (tempNum < 0){
         tempNum = tempNum + 256;
         }

          //convert tempNum back to 8-bit binary String
          binary = Integer.toBinaryString(tempNum);
          switch (binary.length()){
             case 4: binary = "0000" + binary;
                     break;
             case 5: binary = "000" + binary;
                     break;
             case 6: binary = "00" + binary;
                     break;
             case 7: binary = "0" + binary;
                     break;
             }

         //swap bits 1 and 5
         binary = swapChar (binary, 1, 5);

         //Rotate 2 bits right
         binary = RotateRight(binary, 2);


          //Parse 8-bit binary String back to an integer
          tempNum = Integer.parseInt (binary, 2);
          //cast integer to char value, store back into its position in array
          ch[i] = (char)tempNum;
          //System.out.println(ch[i]);
       }
       return ch;
   }

   //Swap characters in a string, return new String
   static String swapChar (String str, int i, int j)
   {
      char chars [] = str.toCharArray();
      char temp = chars[i];
      chars[i] = chars[j];
      chars[j] = temp;
      String swapString = new String (chars);
      return swapString;

   }
   //Flip a character in a string @ position i, to letter
   static String flipChar (String str, int i, char letter)
   {
      char chars [] = str.toCharArray();
      chars [i] = letter;
      String flipString = new String (chars);
      return flipString;
   }

   static String RotateLeft (String str, int s)
   {
      String rotate = str.substring(s) + str.substring(0,s);
      return rotate;

   }

   static String RotateRight (String str, int s)
   {
      return RotateLeft (str, str.length() - s);
   }

//create tehe file and next block write to it





//put line instead
//dont need to enter file name part

   public static void main (String args[])throws IOException{

   	Server server = new Server (5000);

}
}

