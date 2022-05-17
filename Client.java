import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Client {

   private Socket socket = null;
   private FileInputStream input;
   private DataOutputStream out = null;
   
   //Client constructor, creates connection with Server, and sends encrypted file
   public Client (String address, int port) throws IOException
   {
      try
      {
         socket = new Socket(address, port);
         System.out.println("Connected");
         input = new FileInputStream("encrypted.txt");
         // sends output to the socket
         out = new DataOutputStream(socket.getOutputStream());
      }
      catch(UnknownHostException u)
      {
         System.out.println(u);
      }
      catch(IOException i)
      {
         System.out.println(i);
      }
      //wrap FileReader into a BufferedReader
      String message;
      DataInputStream in = new DataInputStream (input);
      BufferedReader br = new BufferedReader (new InputStreamReader (in));
      while ((message = br.readLine()) != null) 
      {
         try
         {
         
            out.writeBytes(message);
           
         }
         catch(IOException i)
         {
            System.out.println(i);
         }
      }

      // close the connection
      try
      {
         input.close();
         out.close();
         socket.close();
      }
      catch(IOException i)
      {
         System.out.println(i);
      }
   
}
 
   //Method for encrypting file
   // 5 steps total
   // Rotate 2 bits left
   // Swap bits 1 and 5
   // Add 7
   // Flip bits 2 and 6
   // Subtract 19
   
   //To decrypt:
   //Add 19
   //Flip bits 2 and 6
   //Subtract 7
   //Swap bits 1 and 5
   //Rotate 2 bits right
   private static char[] encryptMessage (String data) {
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
         //Rotate 2 bits left
         binary = RotateLeft(binary, 2);
         //swap bits 1 and 5
         binary = swapChar (binary, 1, 5);
         //add 7
         try {
            tempNum = Integer.parseInt(binary, 2);
             }
         catch (NumberFormatException e)
            {
            System.out.println("Error. Invalid binary string.");
            }  
         tempNum = tempNum + 7;
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
         // Subtract 19
         //1st - binary string to integer then subtract and check
          try {
            tempNum = Integer.parseInt(binary, 2);
             }
         catch (NumberFormatException e)
            {
            System.out.println("Error. Invalid binary string.");
            }
         tempNum = tempNum - 19;
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
         //Parse 8-bit binary String back to an integer
         tempNum = Integer.parseInt (binary, 2);
         //cast integer to char value, store back into its position in array
         ch[i] = (char)tempNum;
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
   
   public static void main (String [] args) throws IOException {
   
   String filename;
   Scanner input = new Scanner (System.in);
   String message = "";
   String serverIP;
   int port;
   
   System.out.println("Enter the filename of the .txt file you want to encrypt.");
   filename = input.next();
   //Read input file and get message data, store in String called data
   try{
      File myMsg = new File(filename);
      Scanner fileRead = new Scanner (myMsg);
         while(fileRead.hasNextLine())
         {
            String data = fileRead.nextLine();
            message += data; 
         } 
         fileRead.close();
    } catch (FileNotFoundException e) {
         System.out.println("Error in reading file.");
         e.printStackTrace();
         }
     
     
     //Create a new file that will contain ciphertext, and will be sent to server
     
   try{
      File myEncryptMsg = new File ("encrypted.txt");
      if (myEncryptMsg.createNewFile()) {
         System.out.println("File created: " + myEncryptMsg.getName());
      } else {
         System.out.println("File already exists.");
      }
   } catch (IOException e) {
      System.out.println("Error in creating file.");
      }
     //Use that new file and fill it with ciphertext
   try{
      FileWriter encrypter = new FileWriter ("encrypted.txt");
      encrypter.write(encryptMessage(message));
      encrypter.close();
      System.out.println("Successfully created encrypted file.");
   } catch (IOException e) {
      System.out.println("An error occured during encryption.");
      e.printStackTrace();
      }
      
   //After creating ciphertext file, send to Server, then done
   
   System.out.println("Enter server IP address in proper IP format ie: XXX.XXX.XXX.XXX");
   serverIP = input.next();
   //make new connection to server with provided info, on port# 5000
   Client client = new Client(serverIP, 5000);           
         
 } 
   
}
