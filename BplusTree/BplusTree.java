package BplusTree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class BplusTree {
   static class Pair {
      public int key;
      public int value; 
      
      Pair(int key, int value) {
         this.key = key;
         this.value = value;
      }
           
      public int getKey() {
         return key;
      }
      
      public int getvalue() {
         return value;
      }
   }

   
   static class Node {
      public List<Pair> list;
      public boolean isleaf;
      public boolean isroot;
      public List<Node> child;
      public Node leftsibling;
      public Node rightsibling;
      public int nodeSize;		
      
      Node() {
        this.list = new ArrayList<>();
         for(int i = 0; i < maxSize + 2; i++) {
            this.list.add(i, null);
         }
         this.isleaf = true;
         this.isroot = false;
         this.child = new ArrayList<>();
        
         for(int i = 0; i < maxSize + 2; i++) {
            this.child.add(i, null);
         }
         this.leftsibling = null;
         this.rightsibling = null;
         this.nodeSize = 0;
      }  
      
      
   }   
   
   public static Node root; 
   public static int maxSize;
   public static int minkeyNum;
   public static List<Node> Tree = new ArrayList<>();            

   public static void CreateTree() {
      if(maxSize == 2) {
         minkeyNum = 1;
      }
      else {
         minkeyNum = (int)Math.ceil(maxSize / 2.0) - 1;
      }
      
      maxSize--; 
      root = new Node();
      root.isroot = true;

      return;
   }
   
   public static Node split(Node curNode) {

      Node newParent = new Node();
      Node newSibling = new Node();
      
      newParent.isleaf = false;
      if(curNode.isroot) {
         newParent.isroot = true;
         root = newParent;
         curNode.isroot = false;
      }
      
      if(curNode.isleaf) {
         newSibling.isleaf = true;
      }
      
      else {
         newSibling.isleaf = false;
      }    
      
      int mid = (maxSize + 1) / 2;
      
      newParent.list.set(0, curNode.list.get(mid));
      newParent.nodeSize++;
      
      newParent.child.set(0, curNode);
      newParent.child.set(1, newSibling);
      
      newSibling.rightsibling = curNode.rightsibling;
      if(newSibling.rightsibling != null) {
         newSibling.rightsibling.leftsibling = newSibling;
      }	
      newSibling.leftsibling = curNode;
      curNode.rightsibling = newSibling;   
      
      if(curNode.isleaf) {

         for(int i = mid; i < maxSize + 1; i++) {
            newSibling.list.set(i - mid, curNode.list.get(i));
            newSibling.nodeSize++;
         }
         
         for(int i = maxSize; i >= mid; i--) {
            curNode.list.set(i, null);
            curNode.nodeSize--;
         }
      }
      
      else {
        for(int i = mid + 1; i < maxSize + 1; i++) {
           newSibling.list.set(i - (mid + 1), curNode.list.get(i));
           newSibling.child.set(i - (mid + 1), curNode.child.get(i));
           newSibling.nodeSize++;
        }
        
        newSibling.child.set(newSibling.nodeSize, curNode.child.get(maxSize + 1));
        
        for(int i = maxSize; i > mid; i--) {
           curNode.list.set(i, null);
           curNode.child.set(i, null);
           curNode.nodeSize--;
        }
        
        curNode.list.set(mid, null);
        curNode.nodeSize--;
        curNode.child.set(maxSize + 1, null);
      }      
      return newParent;
   }
   
   public static void singlekeySearch(int key) {
         Node curNode = root;
         int idx;
         for(idx = 0; idx < curNode.nodeSize; idx++) {
           if(curNode.isleaf) {
               if(key == curNode.list.get(idx).getKey()) {
                  System.out.println(curNode.list.get(idx).getvalue());
                  return;
               }
            }
            if(key < curNode.list.get(idx).getKey()) {
               System.out.print(curNode.list.get(idx).getKey() + ",");
               break;
            }
            else {
               System.out.print(curNode.list.get(idx).getKey() + ",");
            }
         }
         curNode = curNode.child.get(idx);
         while(true) {
            int pos;
            for(pos = 0; pos < curNode.nodeSize; pos++) {
               if(key < curNode.list.get(pos).getKey()) {
                  System.out.print(curNode.list.get(pos).getKey() + ",");
                  break;
               }
               else {
                  System.out.print(curNode.list.get(pos).getKey() + ",");
               }
            }            
            curNode = curNode.child.get(pos);
            if(curNode.isleaf) {
               System.out.println();
               break;
            }            
         }
         
         for(int i = 0; i < curNode.nodeSize; i++) {
            if(curNode.list.get(i).getKey() == key) {
               System.out.println(curNode.list.get(i).getvalue());
               return;
            }
         }
         System.out.println("NOT FOUND");
   }
   
   public static void rangedSearch(int start, int end) {

      Node curNode = root;
      while(true) {
         if(curNode.isleaf) {
            break;
         }
         int pos;
         for(pos = 0; pos < curNode.nodeSize; pos++) {
            if(start < curNode.list.get(pos).getKey()) {
               break;
            }
         }
         curNode = curNode.child.get(pos);
      }

      for(int i = 0; i < curNode.nodeSize; i++) {
    	 if(end < curNode.list.get(i).getKey()) {
    		 return;
    	 }
    	 
         if(start <= curNode.list.get(i).getKey()) {
            System.out.println(curNode.list.get(i).getKey() + "," + curNode.list.get(i).getvalue());
         }
      }
      

      while(true) {
         curNode = curNode.rightsibling;

         if(curNode == null) {
            return;
         }
         
         for(int i = 0; i < curNode.nodeSize; i++) {
            if(curNode.list.get(i).getKey() > end) {
               return;
            }
            System.out.println(curNode.list.get(i).getKey() + "," + curNode.list.get(i).getvalue());
         }
      }
   }      
      
   public static Node insert(Node curNode, int key, int value) {
      int idx, pos;
      Pair insPair = new Pair(key, value);
      if(curNode.isleaf) {
         for(pos = 0; pos < curNode.nodeSize; pos++) {
            if(key < curNode.list.get(pos).getKey()) {
               break;
            }
         }

         for(idx = curNode.nodeSize; idx > pos; idx--) {
            curNode.list.set(idx, curNode.list.get(idx - 1));   
         }

         curNode.list.set(pos, insPair);
         curNode.nodeSize++;

         if(curNode.nodeSize == maxSize + 1) {
           if(curNode.isroot) {
              return split(curNode);    
           }
           else {
              return curNode;
           }
         }

         return curNode;
      }
      
      else {
         for(pos = 0; pos < curNode.nodeSize; pos++) {
            if(key < curNode.list.get(pos).getKey()) {
               break;
            }
         }

         curNode.child.set(pos, insert(curNode.child.get(pos), key, value));

         if(curNode.child.get(pos).nodeSize == maxSize + 1) {

            Node newNode = split(curNode.child.get(pos));

            for(idx = curNode.nodeSize; idx > pos; idx--) {
               curNode.list.set(idx, curNode.list.get(idx - 1));
               curNode.child.set(idx + 1, curNode.child.get(idx));
            }
            curNode.list.set(pos, newNode.list.get(0));
            curNode.nodeSize++;
            curNode.child.set(pos, newNode.child.get(0));
            curNode.child.set(pos + 1, newNode.child.get(1));
         }
         
         if(curNode.nodeSize == maxSize + 1) {
           if(curNode.isroot) {
              return split(curNode);
           }
           else {
              return curNode;
           }
         }
         
         return curNode;
      }   
   }
   
   
   public static Node delete(Node curNode, int key) {
      int pos;         
      boolean internalDeleted = false;
      if(curNode.isleaf) {
         for(pos = 0; pos < curNode.nodeSize; pos++) {
            if(curNode.list.get(pos).getKey() == key) {
               break;
            }
         }
            
         if(pos == curNode.nodeSize) {
            System.out.println("no key to delete!!" + key);
            return curNode;
         }
            
         for(int i = pos; i < curNode.nodeSize - 1; i++) {
            curNode.list.set(i, curNode.list.get(i + 1));
         }
        
         
        curNode.list.set(curNode.nodeSize - 1, null);
        curNode.nodeSize--;
        
        return curNode;
      }
         
      else {   
         for(pos = 0; pos < curNode.nodeSize; pos++) {
            if(key == curNode.list.get(pos).getKey()) {
               curNode.list.set(pos, null);
               internalDeleted = true;
               pos++;
               break;
            }
               
            if(key < curNode.list.get(pos).getKey()) {                             
               break;
            }
         }                           
            
         curNode.child.set(pos, delete(curNode.child.get(pos), key));
                        
         if(internalDeleted && curNode.list.get(pos - 1) == null) {           
            boolean successor = false;
            if(curNode.child.get(pos) != null) {
               Node tmpNode = curNode.child.get(pos);
            
                while(true) {
                      
                   if(tmpNode == null) {
                      break;
                    }
                      
                    if(tmpNode.isleaf) {
                       successor = true;
                       break;
                    }
                    tmpNode = tmpNode.child.get(0);
                }

                if(successor) {
                   curNode.list.set(pos - 1, tmpNode.list.get(0));   
                }
                   
                tmpNode = null;
               }
               
         }
            
            
         if(curNode.child.get(pos).nodeSize < minkeyNum) {
            if(curNode.child.get(pos).isleaf) {
                 
              if(pos - 1 >= 0) {
                 if(curNode.child.get(pos - 1) != null) {
                    if(curNode.child.get(pos - 1).nodeSize > minkeyNum) {

                       for(int i = curNode.child.get(pos).nodeSize; i > 0; i--) {
                          curNode.child.get(pos).list.set(i, curNode.child.get(pos).list.get(i - 1));
                       }
                          
                       curNode.list.set(pos - 1, curNode.child.get(pos - 1).list.get(curNode.child.get(pos - 1).nodeSize - 1));   
                       curNode.child.get(pos).list.set(0, curNode.list.get(pos - 1));            
                          
                          
                       curNode.child.get(pos - 1).list.set(curNode.child.get(pos - 1).nodeSize - 1, null);
                          
                       curNode.child.get(pos).nodeSize++;
                       curNode.child.get(pos - 1).nodeSize--;
                       return curNode;
                    }                    
                 }                 
              }
                 
              if(pos + 1 <= curNode.nodeSize) {
                 if(curNode.child.get(pos + 1) != null) {
                    if(curNode.child.get(pos + 1).nodeSize > minkeyNum) {
                       curNode.child.get(pos).list.set(curNode.child.get(pos).nodeSize, curNode.list.get(pos));
                       if(curNode.child.get(pos + 1).list.get(0).getKey() == curNode.list.get(pos).getKey()) {
                          for(int i = 0; i < curNode.child.get(pos + 1).nodeSize - 1; i++) {
                             curNode.child.get(pos + 1).list.set(i, curNode.child.get(pos + 1).list.get(i + 1));
                          }
                          curNode.child.get(pos + 1).list.set(curNode.child.get(pos + 1).nodeSize - 1, null);
                       }
                          
                       if(internalDeleted) {
                          if(curNode.list.get(pos - 1) == null) {
                               curNode.list.set(pos - 1, curNode.child.get(pos).list.get(0));
                            }   
                       }
                          
                       curNode.list.set(pos, curNode.child.get(pos + 1).list.get(0));
                                                                       
                       curNode.child.get(pos).nodeSize++;
                       curNode.child.get(pos + 1).nodeSize--;
                       return curNode;
                    }
                 }
                    
              }
                    
              if(pos - 1 >= 0) {
                 if(curNode.child.get(pos - 1) != null) {
                    if(curNode.child.get(pos - 1).nodeSize + curNode.child.get(pos).nodeSize <= maxSize) {
                        for(int i = curNode.child.get(pos).nodeSize + curNode.child.get(pos - 1).nodeSize - 1; i >= curNode.child.get(pos - 1).nodeSize; i--) {
                           curNode.child.get(pos).list.set(i, curNode.child.get(pos).list.get(i - curNode.child.get(pos - 1).nodeSize));
                        }
                           
                        for(int i = 0; i < curNode.child.get(pos - 1).nodeSize; i++) {
                           curNode.child.get(pos).list.set(i, curNode.child.get(pos - 1).list.get(i));
                        }
                                                 
                       if(curNode.child.get(pos - 1).leftsibling != null) {
                          curNode.child.get(pos - 1).leftsibling.rightsibling = curNode.child.get(pos - 1).rightsibling;
                          curNode.child.get(pos - 1).rightsibling.leftsibling = curNode.child.get(pos - 1).leftsibling;                            
                       }
                          
                       else {
                          curNode.child.get(pos - 1).rightsibling.leftsibling = null;
                       }
                          
                       curNode.child.get(pos).nodeSize += curNode.child.get(pos - 1).nodeSize;
                       curNode.child.get(pos - 1).nodeSize = 0;                         
                          
                          
                       for(int i = pos - 1; i < curNode.nodeSize - 1; i++) {
                          curNode.list.set(i, curNode.list.get(i + 1));
                       }
                       curNode.list.set(curNode.nodeSize - 1, null);
                          
                       for(int i = pos - 1; i < curNode.nodeSize; i++) {
                          curNode.child.set(i, curNode.child.get(i + 1));
                       }
                       curNode.child.set(curNode.nodeSize, null);
                       curNode.nodeSize--;
                                           
                          
                       if(curNode.isroot && curNode.nodeSize == 0) {
                          curNode.child.get(pos - 1).isroot = true;
                          root = curNode.child.get(pos - 1);
                          curNode = null;
                          return root;
                       }
                          
                       return curNode;
                    }
                 }
              }                                                                     
                               
              if(pos + 1 <= curNode.nodeSize) {
                 if(curNode.child.get(pos + 1) != null) {
                    if(curNode.child.get(pos).nodeSize + curNode.child.get(pos + 1).nodeSize <= maxSize) {                    
                       
                       for(int i = 0; i < curNode.child.get(pos + 1).nodeSize; i++) {
                          curNode.child.get(pos).list.set(i + curNode.child.get(pos).nodeSize, curNode.child.get(pos + 1).list.get(i));
                       }            
                          
                       if(curNode.child.get(pos + 1).rightsibling != null) {
                          curNode.child.get(pos + 1).rightsibling.leftsibling = curNode.child.get(pos + 1).leftsibling;
                          curNode.child.get(pos + 1).leftsibling.rightsibling = curNode.child.get(pos + 1).rightsibling;                            
                       }
                          
                       else {
                          curNode.child.get(pos + 1).leftsibling.rightsibling = null;
                       }
                          
                       curNode.child.get(pos).nodeSize += curNode.child.get(pos + 1).nodeSize;
                       curNode.child.get(pos + 1).nodeSize = 0;
                       
                       for(int i = pos; i < curNode.nodeSize - 1; i++) {
                          curNode.list.set(i, curNode.list.get(i + 1));
                       }
                       curNode.list.set(curNode.nodeSize - 1, null);                          
                        
                       for(int i = pos + 1; i < curNode.nodeSize; i++) {
                          curNode.child.set(i, curNode.child.get(i + 1));
                       }
                       curNode.child.set(curNode.nodeSize, null);
                       
                       curNode.nodeSize--;
                          
                       if(curNode.isroot && curNode.nodeSize == 0) {
                          curNode.child.get(pos).isroot = true;
                          root = curNode.child.get(pos);
                          curNode = null;

                          return root;
                       }
                          
                       return curNode;
                    }
                 }
              }
              
            }
               
               
               
            
            else {
                if(pos - 1>= 0) {
                    if(curNode.child.get(pos - 1) != null) {
                       if(curNode.child.get(pos - 1).nodeSize > minkeyNum) {
                        for(int i = curNode.child.get(pos).nodeSize; i >= 1; i--) {
                           curNode.child.get(pos).list.set(i, curNode.child.get(pos).list.get(i - 1));
                        }
                          
                       curNode.child.get(pos).list.set(0, curNode.list.get(pos - 1));
                       
                       curNode.list.set(pos - 1, curNode.child.get(pos - 1).list.get(curNode.child.get(pos - 1).nodeSize - 1));
                       curNode.child.get(pos - 1).list.set(curNode.child.get(pos - 1).nodeSize - 1, null);
                          
                       for(int i = curNode.child.get(pos).nodeSize + 1; i >= 1; i--) {
                          curNode.child.get(pos).child.set(i, curNode.child.get(pos).child.get(i - 1));
                       }
                       curNode.child.get(pos).child.set(0, curNode.child.get(pos - 1).child.get(curNode.child.get(pos - 1).nodeSize));
                       curNode.child.get(pos - 1).child.set(curNode.child.get(pos - 1).nodeSize, null);
                          
                       curNode.child.get(pos - 1).nodeSize--;
                       curNode.child.get(pos).nodeSize++;

                       return curNode;
                    }
                 }
               }
                  
                  
               if(pos + 1 <= curNode.nodeSize) {
                    if(curNode.child.get(pos + 1) != null) {
                       if(curNode.child.get(pos + 1).nodeSize > minkeyNum) {
                          curNode.child.get(pos).list.set(curNode.child.get(pos).nodeSize, curNode.list.get(pos));
                          curNode.list.set(pos, curNode.child.get(pos + 1).list.get(0));
                                               
                          for(int i = 0; i < curNode.child.get(pos + 1).nodeSize - 1; i++) {
                             curNode.child.get(pos + 1).list.set(i, curNode.child.get(pos + 1).list.get(i + 1));
                          }
                          curNode.child.get(pos + 1).list.set(curNode.child.get(pos + 1).nodeSize - 1, null);
                             
                          curNode.child.get(pos).child.set(curNode.child.get(pos).nodeSize + 1, curNode.child.get(pos + 1).child.get(0));
                          
                          for(int i = 0; i < curNode.child.get(pos + 1).nodeSize; i++) {
                             curNode.child.get(pos + 1).child.set(i, curNode.child.get(pos + 1).child.get(i + 1));
                          }
                          curNode.child.get(pos + 1).child.set(curNode.child.get(pos + 1).nodeSize, null);
                          
                          curNode.child.get(pos).nodeSize++;
                          curNode.child.get(pos + 1).nodeSize--;
                          
                          return curNode;
                       }
                    }
               }

               if(pos - 1 >= 0) {
                    if(curNode.child.get(pos - 1) != null) {
                       if(curNode.child.get(pos - 1).nodeSize + curNode.child.get(pos).nodeSize + 1 <= maxSize) {
                         for(int i = curNode.child.get(pos).nodeSize; i >= 1; i--) {
                            curNode.child.get(pos).list.set(i, curNode.child.get(pos).list.get(i - 1));
                         }  
                         
                         curNode.child.get(pos).list.set(0, curNode.list.get(pos - 1));
                         
                         for(int i = pos - 1; i < curNode.nodeSize - 1; i++) {
                            curNode.list.set(i, curNode.list.get(i + 1));
                         }
                         curNode.list.set(curNode.nodeSize - 1, null);
                         curNode.child.get(pos).nodeSize++;
                          
                          for(int i = curNode.child.get(pos).nodeSize; i >= 1; i--) {
                             curNode.child.get(pos).child.set(i, curNode.child.get(pos).child.get(i - 1));
                          }
                          
                          for(int i = curNode.child.get(pos).nodeSize + curNode.child.get(pos - 1).nodeSize - 1; i >= curNode.child.get(pos - 1).nodeSize; i--) {
                             curNode.child.get(pos).list.set(i, curNode.child.get(pos).list.get(i - curNode.child.get(pos - 1).nodeSize));
                          }
                          
                          for(int i = 0; i < curNode.child.get(pos - 1).nodeSize; i++) {
                             curNode.child.get(pos).list.set(i, curNode.child.get(pos - 1).list.get(i));
                          }
                             
                          for(int i = curNode.child.get(pos).nodeSize + curNode.child.get(pos - 1).nodeSize; i > curNode.child.get(pos - 1).nodeSize; i--) {
                             curNode.child.get(pos).child.set(i, curNode.child.get(pos).child.get(i - curNode.child.get(pos - 1).nodeSize));
                          }
                          
                          for(int i = 0; i <= curNode.child.get(pos - 1).nodeSize; i++) {
                             curNode.child.get(pos).child.set(i, curNode.child.get(pos - 1).child.get(i));
                          }
                          
                          curNode.child.get(pos).nodeSize += curNode.child.get(pos - 1).nodeSize;
                          curNode.child.get(pos - 1).nodeSize = 0;
                          
                          for(int i = pos - 1; i < curNode.nodeSize; i++) {
                             curNode.child.set(i, curNode.child.get(i + 1));
                          }
                          curNode.child.set(curNode.nodeSize, null);
                                                    
                          
                          curNode.nodeSize--;
                          
                          if(curNode.isroot && curNode.nodeSize == 0) {
                             curNode.child.get(pos - 1).isroot = true;
                             root = curNode.child.get(pos - 1);
                             curNode = null;
                             return root;
                          }
                          return curNode;
                       }
                    }
               }
                  
               if(pos + 1 <= curNode.nodeSize) {
                    if(curNode.child.get(pos + 1) != null) {                       
                       if(curNode.child.get(pos).nodeSize + curNode.child.get(pos + 1).nodeSize + 1 <= maxSize) {
                          curNode.child.get(pos).list.set(curNode.child.get(pos).nodeSize, curNode.list.get(pos));
                          
                          curNode.child.get(pos).nodeSize++;
                          
                          for(int i = pos; i < curNode.nodeSize - 1; i++) {
                             curNode.list.set(i, curNode.list.get(i + 1));
                          }         
                          curNode.list.set(curNode.nodeSize - 1, null);
                          
                          for(int i = 0; i < curNode.child.get(pos + 1).nodeSize; i++) {
                             curNode.child.get(pos).list.set(i + curNode.child.get(pos).nodeSize, curNode.child.get(pos + 1).list.get(i));
                          }                                                    
                          
                          for(int i = 0; i <= curNode.child.get(pos + 1).nodeSize; i++) {
                             curNode.child.get(pos).child.set(i + curNode.child.get(pos).nodeSize, curNode.child.get(pos + 1).child.get(i));
                          }
                          
                          
                          curNode.child.get(pos).nodeSize += curNode.child.get(pos + 1).nodeSize;
                          curNode.child.get(pos + 1).nodeSize = 0;
                          
                          for(int i = pos + 1; i < curNode.nodeSize; i++) {
                             curNode.child.set(i, curNode.child.get(i + 1));
                          }
                          curNode.child.set(curNode.nodeSize, null);
                          
                          
                          
                          curNode.nodeSize--;
                          
                          if(curNode.isroot && curNode.nodeSize == 0) {
                             curNode.child.get(pos).isroot = true;
                             root = curNode.child.get(pos);
                             curNode = null;

                             return root;
                          }
                          
                          return curNode;
                       }
                    }
               }

               
            }
         }
         
         
         else {
            

            return curNode;
         }

      }
      return curNode;
   }     
   
   public static void read_File(String data_file) {      
     for(int i = 0; i < 2100000; i++) {
        Tree.add(null);
     }     
     
     int lastindexNum = 0;
     int firstleafNum = 0;
     boolean firstleafFlag = false;
      boolean firstread = true;
      
      try {
         
         Scanner sc = new Scanner(new BufferedReader(new FileReader(data_file)));
         
         while(sc.hasNextLine()) {                                    
           
           if(firstread) {
              maxSize = Integer.parseInt(sc.nextLine());
              firstread = false;
             
             // if index.dat is empty except for maxSize.
              if(!sc.hasNextLine()) {
                 CreateTree();
                 return;
              }
           }
           
           // make tree_structure.
           else {
              if(maxSize == 2) {
                   minkeyNum = 1;
               }
              
               else {
                  minkeyNum = (int)Math.ceil((maxSize)/ 2.0) - 1;
               }
               
              String data = sc.nextLine();
                String[] datas = data.substring(0).split("/");
                int nodeidx;
                
                String[] nodenumarr = datas[0].substring(0).split("#");
                if(nodenumarr.length == 2) {
                   nodeidx = Integer.parseInt(nodenumarr[1]);
                }
                else {
                   nodeidx = Integer.parseInt(nodenumarr[0]);
                }
                
                Node tmpNode = new Node();
                int idx = 0;
                
                for(int i = 1; i < datas.length; i++) {
                   int key = Integer.parseInt(datas[i].split(",")[0]);
                   int value = Integer.parseInt(datas[i].split(",")[1]);                   
                                  
                   tmpNode.list.set(idx, new Pair(key, value));
                   tmpNode.nodeSize++;
                                      
                   idx++;
                }  
                
                if(nodeidx == 1) {
                    root = tmpNode;
                    root.isroot = true;
                }
                 
                if(nodenumarr.length != 2) {
                   tmpNode.isleaf = false;
                }
                 
                else {
                    if(!firstleafFlag) {
                       firstleafFlag = true;
                       firstleafNum = nodeidx;
                     }
                }
                
                Tree.set(nodeidx, tmpNode);  
                lastindexNum = nodeidx;
           }                       
         }

         for(int i = firstleafNum; i < lastindexNum; i++) {
            Tree.get(i).rightsibling = Tree.get(i + 1);
            Tree.get(i + 1).leftsibling = Tree.get(i);
         }
         Tree.get(firstleafNum).leftsibling = null;
         Tree.get(lastindexNum).rightsibling = null;
      } catch(FileNotFoundException e) {
         e.printStackTrace();
      }
      
      // tree set clear.

      // make_tree with queue.
      Queue<Node> q = new LinkedList<Node>();      
      int indexNum = 1;
      q.offer(Tree.get(indexNum));
      indexNum++;            
      boolean treemakeflag = false;
      
      while(!q.isEmpty()) {
         Node curNode = q.poll();         
         for(int i = 0; i < curNode.nodeSize; i++) {
            if(indexNum > lastindexNum) {
               treemakeflag = true;
               break;
            }
            
            Node childNode = Tree.get(indexNum);
            q.offer(childNode);
            curNode.child.set(i, childNode);
            indexNum++;
         }
         
         if(treemakeflag) {
            break;
         }
         
         curNode.child.set(curNode.nodeSize, Tree.get(indexNum));
         q.offer(Tree.get(indexNum));
         indexNum++;
         
      }            
      
//      print(root, 0);
   }     
   
   public static void save(Node root, String data_file) {
      Queue<Node> q = new LinkedList<Node>();
      int indexNum = 1;
      q.offer(root);
      
      try {
         FileWriter writer = new FileWriter(data_file);
         writer.write(Integer.toString(maxSize));
                  
         while(!q.isEmpty()) {
            writer.write('\n');
            Node curNode = q.poll();
            if(curNode.isleaf) {
               writer.write("#");
            }
            writer.write(Integer.toString(indexNum++));            
            for(int i = 0; i < curNode.nodeSize; i++) {
               if(!curNode.isleaf) {
                  q.offer(curNode.child.get(i));  
               }
               writer.write("/" + curNode.list.get(i).getKey() + "," + curNode.list.get(i).getvalue());
               
            }
            if(!curNode.isleaf) {
               q.offer(curNode.child.get(curNode.nodeSize));  
            }            
         }
         
         writer.close();
      } catch(FileNotFoundException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
      }
   }
   
   public static void create_File(String data_file) {
      try {
         FileWriter writer = new FileWriter(data_file);
         writer.write(Integer.toString(maxSize));
         writer.close();
      } catch(FileNotFoundException e) {
         e.printStackTrace();
      } catch(IOException e) {
         e.printStackTrace();
      }
   }
   
   public static void main(String[] args) {
      
      String command = args[0];
      String data_file = args[1];
      Scanner sc;
      FileWriter writer;
      
      switch(command) {
            
         case "-c":         
            
            maxSize = Integer.parseInt(args[2]);          
            create_File(data_file);                        


            break;                           
                    
         case "-i":
            String insert_file = args[2];
                                              
            try {
               read_File(data_file);
            
               sc = new Scanner(new BufferedReader(new FileReader(insert_file)));            
               writer = new FileWriter(data_file);
               while(sc.hasNextLine()) {               
                  String input = sc.nextLine();

                  String[] pair = input.split(",");
                  int key = Integer.parseInt(pair[0]);
                  int value = Integer.parseInt(pair[1]);
                  insert(root, key, value);  
               }  
               save(root, data_file);
                        
               writer.close();
               sc.close();
            } catch(FileNotFoundException e) {
               e.printStackTrace();
            } catch(IOException e) {
               e.printStackTrace();
            }                  
                  
         break;
         
         case "-d" :        
            String delete_file = args[2];
                                    
         
            try {
               read_File(data_file);
               
               sc = new Scanner(new BufferedReader(new FileReader(delete_file)));
               
               while(sc.hasNextLine()) {         
                  String input = sc.nextLine();
                  String[] key = input.split("\n");
                  int delete_key = Integer.parseInt(key[0]);
                  delete(root, delete_key);   
               }
               writer = new FileWriter(data_file);
               save(root, data_file);
            
               sc.close();           
               writer.close();
            } catch(FileNotFoundException e) {
               e.printStackTrace();
            } catch(IOException e) {
               e.printStackTrace();
            }
         
         break;
         
      
      case "-s":         
         int key = Integer.parseInt(args[2]);
         
         read_File(data_file);
         singlekeySearch(key);
         
         break;
         
      case "-r":
         
         int start = Integer.parseInt(args[2]);
         int end = Integer.parseInt(args[3]);
         
         read_File(data_file);
         rangedSearch(start, end);
         
         
         break;
      }            

    }
}