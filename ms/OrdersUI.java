/******************************************************************************************************************
* File:OrdersUI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class is the console for the an orders database. This interface uses a webservices or microservice
* client class to update the ms_orderinfo MySQL database. 
*
* Parameters: None
*
* Internal Methods: None
*
* External Dependencies (one of the following):
*	- MSlientAPI - this class provides an interface to a set of microservices
*	- RetrieveServices - this is the server-side micro service for retrieving info from the ms_orders database
*	- CreateServices - this is the server-side micro service for creating new orders in the ms_orders database
*
******************************************************************************************************************/
import java.lang.Exception;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Console;

public class OrdersUI
{
	public static void main(String args[]) throws Exception
	{
		boolean done = false;						// main loop flag
		boolean error = false;						// error flag
		char    option;								// Menu choice from user
		Console c = System.console();				// Press any key
		String  date = null;						// order date
		String  first = null;						// customer first name
		String  last = null;						// customer last name
		String  address = null;						// customer address
		String  phone = null;						// customer phone number
		String  orderid = null;						// order ID
		String 	response = null;					// response string from REST 
		Scanner keyboard = new Scanner(System.in);	// keyboard scanner object for user input
		DateTimeFormatter dtf = null;				// Date object formatter
		LocalDate localDate = null;					// Date object
		MSClientAPI api = new MSClientAPI();	// RESTful api object

		/////////////////////////////////////////////////////////////////////////////////
		// Main UI loop
		/////////////////////////////////////////////////////////////////////////////////

		while (!done)
		{	
			// Here, is the main menu set of choices

			System.out.println( "\n\n\n\n" );
			System.out.println( "Orders Database User Interface: \n" );
			System.out.println( "Select an Option: \n" );
			System.out.println( "1: Retrieve all orders in the order database." );
			System.out.println( "2: Retrieve an order by ID." );
			System.out.println( "3: Add a new order to the order database." );				
			System.out.println( "X: Exit\n" );
			System.out.print( "\n>>>> " );
			option = keyboard.next().charAt(0);	
			keyboard.nextLine();	// Removes data from keyboard buffer. If you don't clear the buffer, you blow 
									// through the next call to nextLine()

			//////////// option 1 ////////////

			if ( option == '1' )
			{
				// Here we retrieve all the orders in the ms_orderinfo database

				System.out.println( "\nRetrieving All Orders::" );
				try
				{
					response = api.retrieveOrders();
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);

				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 2 ////////////

			if ( option == '2' )
			{
				// Here we get the order ID from the user

				error = true;

				while (error)
				{
					System.out.print( "\nEnter the order ID: " );
					orderid = keyboard.nextLine();

					try
					{
						Integer.parseInt(orderid);
						error = false;
					} catch (NumberFormatException e) {

						System.out.println( "Not a number, please try again..." );
						System.out.println("\nPress enter to continue..." );

					} // if

				} // while

				try
				{
					response = api.retrieveOrders(orderid);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
					
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 3 ////////////

			if ( option == '3' )
			{
				// Here we create a new order entry in the database

				dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				localDate = LocalDate.now();
				date = localDate.format(dtf);

				System.out.println("Enter first name:");
				first = keyboard.nextLine();

				System.out.println("Enter last name:");
				last = keyboard.nextLine();
		
				System.out.println("Enter address:");
				address = keyboard.nextLine();

				System.out.println("Enter phone:");
				phone = keyboard.nextLine();

				System.out.println("Creating the following order:");
				System.out.println("==============================");
				System.out.println(" Date:" + date);		
				System.out.println(" First name:" + first);
				System.out.println(" Last name:" + last);
				System.out.println(" Address:" + address);
				System.out.println(" Phone:" + phone);
				System.out.println("==============================");					
				System.out.println("\nPress 'y' to create this order:");

				option = keyboard.next().charAt(0);

				if (( option == 'y') || (option == 'Y'))
				{
					try
					{
						System.out.println("\nCreating order...");
						response = api.newOrder(date, first, last, address, phone);
						System.out.println(response);

					} catch(Exception e) {

						System.out.println("Request failed:: " + e);

					}

				} else {

					System.out.println("\nOrder not created...");
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

				option = ' '; //Clearing option. This incase the user enterd X/x the program will not exit.

			} // if

			//////////// option X ////////////

			if ( ( option == 'X' ) || ( option == 'x' ))
			{
				// Here the user is done, so we set the Done flag and halt the system

				done = true;
				System.out.println( "\nDone...\n\n" );

			} // if

		} // while

  	} // main

} // OrdersUI
