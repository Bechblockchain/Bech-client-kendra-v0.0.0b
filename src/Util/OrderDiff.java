package Util;

public class OrderDiff {

	 public static int orderedTx(int arr[]) {
		 
	      //  int arr[]= {5,6,8,7,11,1,9,10};//new int[n];    //Array Declaration
	       	        
	        int temp = 0;    //Temporary variable to store the element
	        
	         for (int i = 0; i < arr.length; i++)   //Holds each Array element
	         {     
	            for (int j = i+1; j < arr.length; j++)    //compares with remaining Array elements
	            {     
	               if(arr[i] < arr[j]) //Compare and swap
	               {    
	                   temp = arr[i];    
	                   arr[i] = arr[j];    
	                   arr[j] = temp;    
	               }     
	            }     
	        }    
	          
	        System.out.println();    
	        int max = 0;
	        int diff = 0;
	        if (arr.length == 0){ 
		        return -1;
		    }
	        //Displaying elements of array after sorting    
	      
	        for (int i = 0; i < arr.length-1; i++) {     
	        	 diff = Math.abs(arr[i] - arr[i+1]);
			        if(max < diff)
			            max = diff;
	        	//System.out.print(max + " ");
	            
	        }    
	        return max;
	 }
	 
	 
	 public static int MaxDiff(int[] array){

		    int diff = 0;

		    if (array.length == 0){ 
		        return -1;
		    }

		    int max = 0;
		    for (int i = 0; i < array.length-1; ++i){

		        diff = Math.abs(array[i] - array[i+1]);
		        if(max < diff)
		            max = diff;
		    }

		    return max;
	 } 
	
	
	
}
