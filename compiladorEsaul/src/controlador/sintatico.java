/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import compiladoresaul.vista.Ventana;
import java.util.ArrayList;

/**
 *
 * @author JM LOPEZ HURTADO
 */
public class sintatico {
    
    private Ventana v;
     String tokens = "";
    int tipodeToken = 0;
    // Variables para manejar los tokens
    private String[] token;
    private int[] tiposTokens;
    private int posiciondeToken = 0;
    public sintatico(String[] token, int[] tiposTokens){
        this.tokens=tokens;
        this.tiposTokens=tiposTokens;
        this.posiciondeToken=0;
        if(token.length>0){
            this.tokens=token[0];
            tipodeToken=tiposTokens[0];
        }
   }
    public void avanzar(){
        posiciondeToken++;
        if(posiciondeToken<token.length){
            tokens = token[posiciondeToken];
            tipodeToken = tiposTokens[posiciondeToken];

        }else{
            tokens="$";//la terminacion de la cadena 
            tipodeToken = -1;
        }
    }
    //programa -> bloque .
     public void programa(){
         if(tipodeToken!=4){
             System.out.println("Error se esperaba un '.'");
             return;
         }
         bloque();
         avanzar();
     }
     public void bloque(){    
        ArrayList<Integer> firstBloque = new ArrayList<>();
        firstBloque.add(8);  // const
        firstBloque.add(14); // var
        firstBloque.add(16); // proced
        firstBloque.add(30); // {
        firstBloque.add(2);  // id
        firstBloque.add(34); // print
        firstBloque.add(36); // input
        firstBloque.add(38); // exec
        firstBloque.add(40); // if
        firstBloque.add(44); // while
        firstBloque.add(46); // for
         
         if(!firstBloque.contains(tipodeToken)){
             System.out.println("Error en el bloque");
             return;
         }
        c2();
        c4();
        c6();
        proposicion();
     }
      // c1 -> id = num c1_x
     public void c1(){
         if (tipodeToken != 2){
             System.out.println("error se esperaba 'identificador'");
             return;
         }
         avanzar();
         if(tipodeToken != 10){
             System.out.println("Error se esperaba el '=' ");
             return;
         }
         avanzar();
         if(tipodeToken != 1){
             System.out.println("Error se esperaba 'nuemero'");
             return;
         }
         avanzar();
         c1_x();
     } 
      // c1_x -> , c1 | null
     public void c1_x(){
        if(tipodeToken != 6){
            System.out.println("Error se esperaba ',' ");
            return;
        }
        avanzar();
        c1();
     }
       // c2 -> const c1 ; | null
     public void c2(){
         if(tipodeToken != 8){
             System.out.println("Error se esperaba 'const' ");
         }
         avanzar();
         c1();
        if (tipodeToken != 12){
            System.out.println("Error se esperaba ';'");
            return;
        }
        avanzar();
     }
     // c3 -> id c3_x
     public void c3(){
         if(tipodeToken != 2){
             System.out.println("Error se esperaba 'identificador'");
             return;
         }
         avanzar();
         c3_x();
     }
     // c3_x -> , c3 | null
       public void c3_x(){
        if(tipodeToken != 6){
            System.out.println("Error se esperaba ',' ");
            return;
        }
        avanzar();
        c3();
     }
       // c4 -> var c3 | null
       public void c4(){
          if(tipodeToken != 14){
              System.out.println("Error se esperaba 'var'");
              return;
          }
          avanzar();
          c3();
       }
         // c6 -> proced id ; bloque ; | null
       public void c6(){
        if (tipodeToken != 16){
             System.out.println("error se esperaba 'proced'");
             return;
         }
         avanzar();
         if(tipodeToken != 2){
             System.out.println("Error se esperaba el 'identificador' ");
             return;
         }
         avanzar();
         if(tipodeToken != 12){
             System.out.println("Error se esperaba ';'");
             return;
         }
         avanzar();
         bloque();
         if(tipodeToken != 12){
             System.out.println("Error se esperaba ';'");
             return;
         }
         avanzar();
       }
       // condición -> expresion e1 expresion
       public void condicion(){
           if (tipodeToken != 56 || tipodeToken != 2 || tipodeToken != 1 ){
               System.out.println("Error en condicion");
               return;
           }
           expresion();
           e1();
           expresion();
       }
        // e1 -> == | <> | < | > | <= | >=
       public void e1(){
           if(tipodeToken != 18){
               System.out.println("Errror se esperaba '=='");
               return;
           }
           avanzar();
           if(tipodeToken != 20){
               System.out.println("Errror se esperaba '=='");
               return;
       }
           avanzar();
           if(tipodeToken != 22){
               System.out.println("Errror se esperaba '<'");
               return;
       }
           avanzar();
            if(tipodeToken != 24){
               System.out.println("Errror se esperaba '>'");
               return;
       }  
            avanzar();
               if(tipodeToken != 26){
               System.out.println("Errror se esperaba '<='");
               return;
       }
               avanzar();
                  if(tipodeToken != 28){
               System.out.println("Errror se esperaba '>='");
               return;
       }
                  avanzar();
  }
          // expresión -> e3
       public void expresion(){
           ArrayList<Integer> firtsexpresion = new ArrayList();
           firtsexpresion.add(56);
           firtsexpresion.add(2);
           firtsexpresion.add(1);
           
           if(!firtsexpresion.contains(tipodeToken)){
               System.out.println("Error en expresion");
               return;
           }
           e3();
       }
       /// e2 -> + | -
       public void e2(){
           switch(tipodeToken){
               case 52:
               case 54:
                   avanzar();
                break;
                default:
                    System.out.println("Error se esperaba el '+' o '-'");
           }
       }
        // e3 -> termino e3_x
      
        public void e3(){
           ArrayList<Integer> firtse3 = new ArrayList();
           firtse3.add(56);
           firtse3.add(2);
           firtse3.add(1);
           
           if(!firtse3.contains(tipodeToken)){
               System.out.println("Error en e3");
               return;
           }
           e3_x();
       }
         
    // e3_x -> e2 e3 | null
        public void e3_x(){
           ArrayList<Integer> firtse3_x = new ArrayList();
           firtse3_x.add(52);
           firtse3_x.add(54);
            if(!firtse3_x.contains(tipodeToken)){
                e2();
                e3();
            }
            
        }
         // e4 -> * | /
        public void e4(){
           switch(tipodeToken){
               case 60:
               case 62:
                   avanzar();
                break;
                default:
                    System.out.println("Error se esperaba el '*' o '/'");
           }
       }
        // e5 -> factor e5_x
        public void e5(){
        ArrayList<Integer> firtse5 = new ArrayList<>();
        firtse5.add(56);
        firtse5.add(2);
        firtse5.add(1);
        
        if(!firtse5.contains(tipodeToken)){
            System.out.println("Error en el metodo e5");
            return;
        }
        factor();
        e5_x();
        
        }
         // e5_x -> e4 e5 | null
        public void e5_x(){
            ArrayList<Integer> firtse5_x = new ArrayList<>();
             firtse5_x.add(60);
             firtse5_x.add(62);
             
             if(!firtse5_x.contains(tipodeToken)){
                 e4();
                 e5();
             }
        }
         // termino -> e5
         public void termino (){
        ArrayList<Integer> termino = new ArrayList<>();
        termino.add(56);
        termino.add(2);
        termino.add(1);
        
        if(!termino.contains(tipodeToken)){
            System.out.println("Error en termino");
            return;
        }
        e5();
        
        }
        // factor -> e6 | id | num   
        public void factor(){
           switch(tipodeToken){
               case 56:
               case 2:
               case 1:
                   avanzar();
                break;
                default:
                    System.out.println("Error se esperaba el '(' o 'identificador' o 'numero'");
           }
       }
         // e6 -> ( expresion )
        public void e6(){
            if(tipodeToken!=56){
                System.out.println("Error se esperaba '('");
                return;
            }
            avanzar();
            expresion();
            if(tipodeToken!=58){
                System.out.println("Error se esperaba ')'");
                return;
            }
            avanzar();
        }
        // proposición -> p2 | p3 | p4 | p5 | p6 | p7 | p8 | p10
        public void proposicion(){
            switch(tipodeToken){
                case 30:
                    p2();
                    break;
                case 2:
                    p3();
                    break;
                case 34:
                    p4();
                    break;
                case 36:
                    p5();
                    break;
                case 38:
                    p6();
                    break;
                case 40:
                    p7();
                    break;
                case 44: 
                    p8();
                    break;
                case 46:
                    p10();
                    break;
                default:
                    System.out.println("Error en proposicion");
            }
        }
           // p1 -> proposicion p1_x
        public void p1(){
            ArrayList<Integer> firtsp1 = new ArrayList<>();
            firtsp1.add(30);
            firtsp1.add(2);
            firtsp1.add(34);
            firtsp1.add(36);
            firtsp1.add(38);
            firtsp1.add(40);
            firtsp1.add(44);
            firtsp1.add(46);
            if(!firtsp1.contains(tipodeToken)){
                System.out.println("Error en p1");
            }
            proposicion();
            p1_x();
        }
         // p1_x -> ; p1 | null
        public void p1_x(){
            if(tipodeToken!=12){
                System.out.println("Error se esperaba ';'");
                return;
            }
            avanzar();
            p1();
        }
         // p2 -> { p1 }
        public void p2(){
            if(tipodeToken!=30){
                System.out.println("Error se esperaba '{'");
                return;
            }
           avanzar();
           p1();
          if(tipodeToken!=32){
              System.out.println("Error se esperaba ']'");
                return;
          }
          avanzar();
        }
         // p3 -> id = expresion
         public void p3(){
            if(tipodeToken!=2){
                System.out.println("Error se esperaba 'identificador'");
                return;
            }
           avanzar();
          if(tipodeToken!=10){
              System.out.println("Error se esperaba '='");
                return;
          }
          avanzar();
          expresion();
        }
            // p4 -> print p4_x
         public void p4(){
             if (tipodeToken!=34){
                 System.out.println("Error se esperaba 'print'");
                 return;
             }
             avanzar();
             p4_x();
         }
             // p4_x -> num | id
         public void p4_x(){
             switch(tipodeToken){
                 case 1:
                 case 2:
                     avanzar();
                     break;
                 default:
                     System.out.println("Error se esperaba 'numero' o 'identifiacador'  ");
             }
         }
           // p5 -> input id
          public void p5(){
            if(tipodeToken!=36){
                System.out.println("Error se esperaba 'input'");
                return;
            }
           avanzar();
          if(tipodeToken!=2){
              System.out.println("Error se esperaba 'identificador'");
                return;
          }
          avanzar();
        }
          // p6 -> exec id
          public void p6(){
            if(tipodeToken!=38){
                System.out.println("Error se esperaba 'exec'");
                return;
            }
           avanzar();
          if(tipodeToken!=2){
              System.out.println("Error se esperaba 'identificador'");
                return;
          }
          avanzar();
        }
           // p7 -> if condicion : proposicion
          public void p7(){
              if(tipodeToken!=40){
                  System.out.println("Error se esperaba 'if'");
                  return;
              }
              avanzar();
              condicion();
              if(tipodeToken!=42){
                  System.out.println("Error se esperaba ':'");
                  return;
              }
              avanzar();
              proposicion();
          }
            // p8 -> while condicion : proposicion
           public void p8(){
              if(tipodeToken!=44){
                  System.out.println("Error se esperaba 'while'");
                  return;
              }
              avanzar();
              condicion();
              if(tipodeToken!=42){
                  System.out.println("Error se esperaba ':'");
                  return;
              }
              avanzar();
              proposicion();
          }
            // p9 -> expresion p9_x
           public void p9(){      
         ArrayList<Integer> firtsp9 = new ArrayList<>();
               firtsp9.add(56);
               firtsp9.add(2);
               firtsp9.add(1);
               
               if(!firtsp9.contains(tipodeToken)){
                   System.out.println("Error en p9");
                   return;
               }
               expresion();
               p9_x();
      }
           // p9_x -> -> | <-
           public void p9_x(){
               switch(tipodeToken){
                   case 48:
                   case 50:
                   avanzar();
                   break;
                   default:
                       System.out.println("Error se esperaba '->' o '<-'");
               }
           }
             // p10 -> for id = p9 expresion : proposicion
           public void p10(){
               if(tipodeToken!=46){
                   System.out.println("Error se esperaba 'for'");
                   return;
               }
               avanzar();
                if(tipodeToken!=2){
                   System.out.println("Error se esperaba 'identificadores'");
                   return;
             }
                avanzar();
                if(tipodeToken!=10){
                   System.out.println("Error se esperaba '='");
                   return;
             } 
                avanzar();
                p9();
                 if(tipodeToken!=42){
                   System.out.println("Error se esperaba ':'");
                   return;
             }
                 avanzar();
                 proposicion();
           } 


}

