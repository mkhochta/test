//zcat Clothing_\&_Accessories.txt.gz | head -n 55 
//TO COMPILE: javac -g mainFile.java
//TO RUN:     Usage: java mainFile "<Item_Name>" "<Item_Description>"
/*from the directory right above: 
 * update the file path that we read from in the constructor
 * javac -cp "./ProCapstoneRecommendationsAlgo/" ./ProCapstoneRecommendationsAlgo/mainFile.java
 * java -cp "./ProCapstoneRecommendationsAlgo/"  mainFile     "Jacket"     "reed nice jacket"
*/
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import util.*;

public class mainFile {
  private ArrayList<Product> allProds;
  String[] prodDataset;
  
  public mainFile(String[] args){
    try{
      prodDataset= args[3].split("</br>");      
      allProds   = new ArrayList<Product>();
    }catch (Exception e){System.err.format("\nException occurred trying to readddddddd '%s'.\n\n", "args[3]");return;}
  }
  
  public ArrayList<Product> get_allProds(){
    return allProds;
  }
  public boolean notContains(String ID){
    for (Product prod : allProds){
      if( prod.getID().equals(ID) ) return false;
    }
    return true;
  }
  public Product getByID(String ID){
    for (Product prod : allProds){
      if(prod.getID().equals(ID) ) return prod;
    }
    System.out.println("NULLLLLL");
    return null;
  }
  public double vectVal(Counter<String> vect){
    double retVal=0.0;
    for(String s : vect.keySet()){
      retVal+=Math.pow(vect.getCount(s), 2);
    }
    return Math.sqrt(retVal);
  }
  public double simCosine(Product prod1, Product prod2){
    Counter<String> vect1, vect2;
    vect1=prod1.getVect(); vect2=prod2.getVect();
    //size=(size1<size2)? size1 : size2;
    double temp=0.0;
    for(String str : vect1.keySet()){
      if(vect2.containsKey(str) ){
        temp+=(vect1.getCount(str)*vect2.getCount(str) );
      }
    }
    return temp/(vectVal(vect1)*vectVal(vect2));
  }
  public void printAllProds(){
    System.out.println("allProds: ");
    for(int i=0; i<allProds.size(); i++)
      System.out.println(allProds.get(i) );
  }  
  
  public void parse(){//Parse FUNCTION----------
    try{int count=0;
      String retStr="", line, ID="";
      for( int i=0;i<prodDataset.length;i+=3 ){
        
        line=prodDataset[i];
        if(line.length()<3){continue;}
        if(!line.substring(0,3).equals("ID ") ){continue;}
        retStr=""; ID="";
        ID=line.substring(3);//ID of the product
        //System.out.println("ID "+ID);//DEBUGGING STATEMENT
        line = prodDataset[i+1];//read next line which has the title
        retStr+=line.substring(6);//product title
        line = prodDataset[i+2];//read next line which has the product description
        retStr+=line.substring(12);//product description
        //System.out.println("ID="+ID+" count="+count);
        if(notContains(ID)){
          //System.out.println("AM hereeeeeeeeee!!!");//DEBUGGING STATEMENT
          Product prod = new Product(ID); get_allProds().add(prod);
          getByID(ID).makeVector(retStr);
        }
        /*We do this if we hae different input we want to read for the same product, and if we do this we 
         * have to take off the other call inside the conditional operation. */
        //getByID(ID).makeVector(retStr); 
      }
      //printAllProds();
    }catch (Exception e){System.err.format("\nException occurred trying to read '%s'.\n\n", "args[3]");return;}
  }//END of Parse FUNCTION-------------------------
  
  public void getRecommendations(Product inpProd){
    //System.out.println("=================================== Recommendations ===================================</br>");
    PriorityQueue<Product> pq = new PriorityQueue<Product>();
    int pqSize=6;//this determines how many recommendations we will turn back
    double temp=0.0;
    for(Product tempProd : get_allProds()){
      temp=simCosine(inpProd, tempProd);
      //System.out.println("simCosine "+temp);
      pq.add(tempProd,temp);      
      //System.out.println("After adding "+tempProd.getID()+" pq:"+pq);
      if(pq.size()<=pqSize) continue;
      pq.removeLast();
    }
    pq.removeFirst();//Because the first item in the list will automatically be the one the user just bought (it will score the highest)
    while(pq.getPriorityLast()<0.0){//threshold for minimum sim-Cosine score
      pq.removeLast();
    }
    //print the primary queue in a php-convenient way
	String toReturn="";
	while (pq.hasNext()) {      
      Product  prod = pq.next();
	  toReturn+=prod.getID();
      toReturn+="</br>";
    }
	System.out.println(toReturn);
  }
  
  public static void main(String[] args) {
    if(args.length!=4){
      System.out.println("Usage: java mainFile <ID> \"<Item_Name>\" \"<Item_Description>\" \"<All_Items>\""); System.exit(0);
    }
    mainFile program = new mainFile(args);
    program.parse();
    String input = args[1]+" "+args[2];
    //System.out.println("</br>args[0]: "+args[0]+" args[1]: "+args[1]);
    Product inpProd=new Product(args[0]);
    inpProd.makeVector(input);
    //System.out.println("</br>inpProd: "+inpProd+"</br>");
    program.getRecommendations(inpProd);
    
  }
}
