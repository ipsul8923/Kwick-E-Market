import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import stev.kwikemart.AmountException;
import stev.kwikemart.InvalidQuantityException;
import stev.kwikemart.Item;
import stev.kwikemart.PaperRoll;
import stev.kwikemart.Register;
import stev.kwikemart.StoreException;
import stev.kwikemart.Upc;

/*
 * Montant
 * 
 * Valide :
 * 	V1 : 0<x<35	
 * Invalide :
 * 	i1 : x>35, 
 * 	i2 : x<0
 * 
 * Quantité	
 * Valide :
 * 	V2 : x=>0
 * 	V3 : x=-Qté( si présent en position antérieur)
 * 	V4 : x peut être un float si UPC commence par 2
 * Invalide :
 * 	i3 : x<0 (si non présent)
 * 	i4 : x float si UPC ne commence pas par 2
 *
 * UPC
 * Valide :
 * 	V5 :L'UPC coupons commence par un 5
 * 	V6 : L'UPC classique commence par un 1
 * 	V7 : L'UPC des items avec un poid commence par 2
 * 	V8 : L'UPC correspond à un item du magasin
 * 	V9 : L'UPC doit être de longueur 12
 * Invalide :
 * 	i5 : UPC ne correspond pas à un UPC valide(last digit)
 * 	i6 : UPC composé de caractères différents de 0-9
 * 	i7 : UPC est un string vide
 * 	i8 : UPC de longueur > à 12 digit
 *	i9 : UPC de longueur < à 12 digit
 * 
 * Nombre de ligne (papier)	
 * Valide : 
 * 	V10 : 0<x<SMALL_ROLL (25)
 * 	V11 : 0<x<LARGE_ROLL (1000)
 * Invalide :
 * 	i10 : Nombre de ligne supérieur à SMALL_ROLL
 * 	i11 : Nombre de ligne supérieur à LARGE_ROLL
 * 
 * Liste d'entrée caisse	
 * Valide :
 * 	V12 : 0<x<10
 * Invalide :
 * 	i12 : Liste de course vide
 * 	i13 : Liste de course de plus de 10 items distinct
 * 
 * Coupons	
 * Valide :
 * 	V13 : Le montant du coupon est positif
 * 	V14 : Un seul coupon possédant le même UPC
 * 	V15 : Plusieurs coupons avec des UPC différents
 * 	V16 : Coupon ok que si montantCoupon<MontantTotal
 * 	V17 : x=-QuantitéCoupon si déjà présent
 * Invalide :
 * 	i14 : Montant négatif
 * 	i15 : Plusieurs coupons de même UPC
 * 	i16 : Coupon de montant supérieur au total
 * 
 * Réductions $	
 * Valide :
 * 	V18 : si total avant taxe > 2$ et nbrItem=>5, reduction de 1$	
 * Invalide :
 * 	i17 : totalAvantTaxe < 2$ et reduction de 1$
 * 
 * Item
 * Valide :
 * 	V19 : Item unique
 * 	V20 : Item doublon avec quantité négative
 * Invalide :
 * 	i18 : Doublon de quantité positive
 */


class RegisterTest {

	
	String amountTooBigException = "Exception for item 123456789012: the amount exceeds the maximum value allowed by the register";
	String amountNegativeException = "Exception for item 123456789012: the amount exceeds the maximum value allowed by the register";
	String quantityNegativeException ="";
	String quantityFractionnalException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcLastDigitException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcCaracterexception ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcShortException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcLongException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcVoidException = "";
	String lineSmallRollException = "";
	String lineLargeRollException = "";
	String voidListException = "";
	String tooLongListException = "";
	String couponNegativeAmountException = "";
	String couponSameUpcException = "";
	String couponAmountException = "";
	String discountException = "";
	String doubleItemException = "";

	Register register = Register.getRegister();
	List<Item> grocery;
	
	
	
	
	
	
	
	@BeforeEach
	void setUp() throws Exception{
		grocery = new ArrayList<Item>();

	}
	@AfterEach
	void tearDown() throws Exception {
		grocery.clear();

	}
	
	
	
	
	/**
	 * 
	 */
	@Test
	void test1() {
		register.changePaper(PaperRoll.SMALL_ROLL);//V10
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 1.5)); //V1, V2
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 0, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));//V5, V13
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		//V12
		//V18
		//V19
		System.out.println(register.print(grocery));
		assertEquals(1.5, grocery.get(0).getRetailPrice());
	}

	
	
	@Test
	void test2() {
		register.changePaper(PaperRoll.LARGE_ROLL);//V11
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 1.5)); //V1,V6
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //V3 V20
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 0, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432333"), "Chewing gum discount", 1, 0.3));//14
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		//V12
		//V18
		System.out.println(register.print(grocery));
	}
	
	
	@Test
	void test3() {
		register.changePaper(PaperRoll.LARGE_ROLL);//V11
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 1.5)); //V1
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); //V4, V7
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //V20
		grocery.add(new Item(Upc.generateCode("64748119599"), "Chewing gum", 0, 0.99));
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));//v15
		grocery.add(new Item(Upc.generateCode("54323432333"), "Chewing gum discount", 1, 0.3));//V15
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		//V12
		//V18
		System.out.println(register.print(grocery));
	}
	
	@Test
	void test4() {
		register.changePaper(PaperRoll.LARGE_ROLL);//V11
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 1.5)); //V1
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); //V4, V8
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //V20
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));//v16
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		//V12
		//V18
		System.out.println(register.print(grocery));
	}
	
	
	@Test
	void test5() {
		register.changePaper(PaperRoll.LARGE_ROLL);//V11
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 1.5)); //V1
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); //V4, V9
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //V20
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));//v17
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		//V12
		//V18
		System.out.println(register.print(grocery));
	}
	
	
	@Test
	void test6() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 50))); //I1
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(amountTooBigException, exception.getMessage());

	}

	@Test
	void test7() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, -3))); //I2
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(amountNegativeException, exception.getMessage());

	}
	
	@Test
	void test8() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5))); //I3
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(quantityNegativeException, exception.getMessage());

	}
	
	@Test
	void test9() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1.5, 1.5))); //I4
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(quantityFractionnalException, exception.getMessage());

	}
	
	@Test
	void test10() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678908"), "Bananas", 1, 1.5))); //I5 pas d'exception, a revoir 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(upcLastDigitException, exception.getMessage());

	}
	
	@Test
	void test11() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("1ebc5678901"), "Bananas", 1, 1.5))); //I6
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(upcCaracterexception, exception.getMessage());

	}
	
	@Test
	void test12() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode(""), "Bananas", 1, 1.5))); //I7
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(upcVoidException, exception.getMessage());

	}
	
	@Test
	void test13() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("12345678901111"), "Bananas", 1, 1.5))); //I8
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(upcLongException, exception.getMessage());

	}
	@Test
	void test14() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		Exception exception = Assertions.assertThrows(StoreException.class, () -> grocery.add(new Item(Upc.generateCode("15678901"), "Bananas", 1, 1.5))); //I9
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
        assertEquals(upcShortException, exception.getMessage());

	}
	@Test
	void test15() {
		register.changePaper(PaperRoll.SMALL_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		System.out.println(register.print(grocery));
		Exception exception = Assertions.assertThrows(StoreException.class, ()->	System.out.println(register.print(grocery)));//I10

        assertEquals(lineSmallRollException, exception.getMessage());

	}
	
	@Test
	void test16() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
		grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
		
		for(int i=0; i<63; i++) {
		System.out.println(register.print(grocery));}
		Exception exception = Assertions.assertThrows(StoreException.class, ()->	System.out.println(register.print(grocery)));//I11

        assertEquals(lineLargeRollException, exception.getMessage());

	}
	@Test
	void test17() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		
		Exception exception = Assertions.assertThrows(StoreException.class, ()->	System.out.println(register.print(grocery)));//I12

        assertEquals(voidListException, exception.getMessage());

	}
	@Test
	void test18() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678902"), "Strawberries", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678903"), "Orange", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678904"), "Cherry", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678905"), "Eggs", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678906"), "Bread", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("12345678907"), "Chicken", 1, 1.5)); //Attention check les UPC 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		Exception exception = Assertions.assertThrows(StoreException.class, ()->grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25)));
		System.out.println(register.print(grocery));//I13

        assertEquals(tooLongListException, exception.getMessage());

	}
	@Test
	void test19() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		Exception exception = Assertions.assertThrows(StoreException.class, ()->grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, -0.5)));//I14
		System.out.println(register.print(grocery));

        assertEquals(couponNegativeAmountException, exception.getMessage());

	}
	@Test
	void test20() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
		Exception exception = Assertions.assertThrows(StoreException.class, ()->grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5)));//I15
		System.out.println(register.print(grocery));

        assertEquals(couponSameUpcException, exception.getMessage());

	}
	@Test
	void test21() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
		Exception exception = Assertions.assertThrows(StoreException.class, ()->grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 15)));//I16
		System.out.println(register.print(grocery));

        assertEquals(couponAmountException, exception.getMessage());

	}
	@Test
	void test22() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1)); 
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 0.1)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.1));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 0.1));
		grocery.add(new Item(Upc.generateCode("12345678905"), "Eggs", 1, 0.1)); //Attention UPC
		//I17 Pas sur de pouvoir le faire apparaitre
		System.out.println(register.print(grocery));


	}
	@Test
	void test23() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1)); 
		Exception exception = Assertions.assertThrows(StoreException.class, ()->grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1))); //I18
		grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 0.1)); 
		grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.1));
		grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 0.1));
		System.out.println(register.print(grocery));
        assertEquals(doubleItemException, exception.getMessage());

	}
}
