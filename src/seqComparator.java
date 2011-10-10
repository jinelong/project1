import java.util.Comparator;
import java.util.Scanner;
/*
 *  @author: Jin
 *  helper class for buffer sorting
 * 
 */

class seqComparator implements Comparator<String>{
   
    public int compare(String emp1, String emp2){
   
     
       String p1 = emp1;
       String p2 = emp2;
       Scanner getNum = new Scanner(p1).useDelimiter("@");
       Scanner getNum2 = new Scanner(p2).useDelimiter("@");
       //"b"+"@"+myName+"@"+msgCounter+"@"+msg+"@";

       getNum.next();
       getNum.next();
       int n1 = getNum.nextInt();
       getNum2.next();
       getNum2.next();
       int n2 = getNum2.nextInt();
       
        if(n1 < n2)
            return -1;
        else 
            return 1;
    }
   
}