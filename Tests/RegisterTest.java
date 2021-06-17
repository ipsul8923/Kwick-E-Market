import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.notification.Failure;

import stev.kwikemart.AmountException;
import stev.kwikemart.InvalidQuantityException;
import stev.kwikemart.Item;
import stev.kwikemart.PaperRoll;
import stev.kwikemart.Register;
import stev.kwikemart.StoreException;
import stev.kwikemart.Upc;

/*
 *
 * @author Nicolas_Kerneis (KERN08049808), Nesrine_Chekou(CHEN04619700), David_Lhullier(LHUD14029704 ), Vincent Vanbalberghe(VANV20019704)
 * 
 * 
 * Le récapitulatif des informations est disponible dans le fichier googleSheet suivant : https://docs.google.com/spreadsheets/d/1cPd9vzdvoahySq95WYiF_o4gW4YOdTzs1c3JMQwRRQg/edit?usp=sharing
 * 
 * 
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegisterTest {


	String amountTooBigException = "Exception for item 123456789012: the amount exceeds the maximum value allowed by the register";
	String amountNegativeException = "Exception for item 123456789012: the amount is negative";
	String quantityNegativeException ="Item 123456789012 is not already in the list";
	String quantityFractionnalException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcLastDigitException ="Exception for item 123456789012: fractional quantities are not possible for this item category";
	String upcCaracterexception ="Exception for item 1ebc5678901-1: the UPC is too long";
	String upcShortException ="begin 8, end 9, length 8";
	String upcLongException ="Exception for item 123456789011112: the UPC is too long";
	String upcVoidException = "begin 0, end 1, length 0";
	String paperRollException = "The roll has run out of paper";
	String voidListException = "The grocery list is empty";
	String tooLongListException = "Too many items in the grocery list";
	String couponNegativeAmountException = "Exception for item 543234323434: the amount is negative";
	String couponSameUpcException = "Item 543234323434 is already in the list";
	String couponAmountException = "";
	String discountException = "";
	String doubleItemException = "Item 123456789012 is already in the list";

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
	 * Une fois les PCE déterminées, on crée les combinaisons de classes valides pour tous les paramètres 
	 * jusqu'à ce que tous les ensembles aient été inclus au moins une fois. Les tests 1 à 5 correspondent à ces combinaisons
	 * On teste ensuite chaque classe invalide de chaque paramètre individuellement, en donnant une valeur valide quelconque aux autres paramètres.
	 * Les tests 6 à 23 correspondent à ces combinaisons.
	 */
	
	@Test
	@Order(1)
	void test1() {
		try {
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
		} catch (Exception e) {
			fail("Le test 1 a échoué");
		}
	}



	@Test
	@Order(2)
	void test2() {
		try {
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
		} catch (Exception e) {
			fail("Le test 2 a échoué");
		}
	}


	@Test
	@Order(3)
	void test3() {
		try {
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
		} catch (Exception e) {
			fail("Le test 3 a échoué");
		}
	}

	@Test
	@Order(4)
	void test4() {
		try {
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
		} catch (Exception e) {
			fail("Le test 4 a échoué");
		}
	}


	@Test
	@Order(5)
	void test5() {
		try {
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
		} catch (Exception e) {
			fail("Le test 5 a échoué");
		}
	}


	@Test
	@Order(6)
	void test6() {
		register.changePaper(PaperRoll.LARGE_ROLL);
		try {
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, 50));//I1
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(amountTooBigException, e.getMessage());
		}
	}

	@Test
	@Order(7)
	void test7() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 3, -3)); //I2
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(amountNegativeException, e.getMessage());
		}
	}

	@Test
	@Order(8)
	void test8() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", -1, 1.5)); //I3
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(quantityNegativeException, e.getMessage());
		}
	}

	@Test
	@Order(9)
	void test9() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1.5, 1.5)); //I4
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(quantityFractionnalException, e.getMessage());
		}
	}

	@Test
	@Order(10)
	void test10() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678908"), "Bananas", 1, 1.5)); //I5
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(upcLastDigitException, e.getMessage());
		}
	}


	@Test
	@Order(11)
	void test11() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("1ebc5678901"), "Bananas", 1, 1.5)); //I6
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(upcCaracterexception, e.getMessage());
		}
	}

	@Test
	@Order(12)
	void test12() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode(""), "Bananas", 1, 1.5)); //I7
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(upcVoidException, e.getMessage());
		}
	}

	@Test
	@Order(13)
	void test13() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901111"), "Bananas", 1, 1.5)); //I8
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(upcLongException, e.getMessage());
		}
	}

	@Test
	@Order(14)
	void test14() {

		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("15678901"), "Bananas", 1, 1.5)); //I9
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(upcShortException, e.getMessage());
		}
	}


	@Test
	@Order(15)
	void test15() {
		try {
			register.changePaper(PaperRoll.SMALL_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", -1, 0.5));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));//I10
		} catch (Exception e) {
			assertEquals(paperRollException, e.getMessage());
		}
	}


	@Test
	@Order(23)
	void test16() {
		try {
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
			//I11
		} catch (Exception e) {
			assertEquals(paperRollException, e.getMessage());
		}
	}


	@Test
	@Order(16)
	void test17() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			System.out.println(register.print(grocery));//I12
		} catch (Exception e) {
			assertEquals(voidListException, e.getMessage());
		}
	}


	@Test
	@Order(17)
	void test18() {	
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678902"), "Strawberries", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678903"), "Orange", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678904"), "Cherry", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678905"), "Eggs", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678906"), "Bread", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("12345678907"), "Chicken", 1, 1.5));  
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("61519314159"), "Doritos", 1, 1.25));
			System.out.println(register.print(grocery));//I13
		} catch (Exception e) {
			assertEquals(tooLongListException, e.getMessage());
		}
	}

	@Test
	@Order(18)
	void test19() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, -0.5));//I14
			System.out.println(register.print(grocery));

		} catch (Exception e) {
			assertEquals(couponNegativeAmountException, e.getMessage());
		}
	}



	@Test
	@Order(19)
	void test20() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 0.5));//I15
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(couponSameUpcException, e.getMessage());
		}
	}


	@Test
	@Order(20)
	void test21() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 1.5)); 
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 5.75)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.99));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 1.44));
			grocery.add(new Item(Upc.generateCode("54323432343"), "Doritos Club", 1, 15));//I16
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(couponAmountException, e.getMessage());
		}
	}


	@Test
	@Order(21)
	void test22() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1)); 
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 0.1)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.1));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 0.1));
			grocery.add(new Item(Upc.generateCode("12345678905"), "Eggs", 1, 0.1)); 
			//I17
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			fail("Le test 22 a échoué.");
		}
	}



	@Test
	@Order(22)
	void test23() {
		try {
			register.changePaper(PaperRoll.LARGE_ROLL);
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1)); 
			grocery.add(new Item(Upc.generateCode("12345678901"), "Bananas", 1, 0.1)); //I18
			grocery.add(new Item(Upc.generateCode("22804918500"), "Beef", 0.5, 0.1)); 
			grocery.add(new Item(Upc.generateCode("44348225996"), "Gobstoppers", 1, 0.1));
			grocery.add(new Item(Upc.generateCode("34323432343"), "Nerds", 2, 0.1));
			System.out.println(register.print(grocery));
		} catch (Exception e) {
			assertEquals(doubleItemException, e.getMessage());
		}
	}
}
