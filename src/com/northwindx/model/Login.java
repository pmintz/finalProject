/*************************************************************************
 *
 * DELOITTE CONSULTING
 * ___________________
 *
 *  [2013] - [2014] Deloitte Consulting, LLP
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Deloitte Consulting, LLP and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Deloitte Consulting, LLP
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Deloitte Consulting, LLP.
 *
 *************************************************************************/
package com.northwindx.model;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.northwindx.model.jpa.Customer;
import com.northwindx.util.PersistenceUtil;

public class Login {
	
	private Login() { } // Private default constructor makes it truly static

	private static final int MAX_COOKIE_AGE = 24 * 60 * 60; // One day in seconds

	/**
	 * Returns the currently logged in user based on a "oatmealRaisin" cookie.
	 * Returns null if a user is not logged in.
	 * 
	 * @return	the Customers object
	 */
	public static Customer getLoggedInUser() {
		Cookie temp = null;
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest r = (HttpServletRequest) fc.getExternalContext()
				.getRequest();
		Cookie[] mycookies = r.getCookies();
		if (mycookies != null) {
		for (Cookie c : mycookies) {
			if (c.getName().equals("oatmealRaisin")) {
				temp = c;
				
				break;
			}
		}
		}

		if (temp == null) {
			return null;
		}
		
		
		

		PersistenceUtil.getEntityManager().getTransaction().begin();

		Customer user = PersistenceUtil.getEntityManager()
				.createQuery("from Customer c" + " where c.customerID = ?",
						Customer.class).setParameter(1, temp.getValue())
				.getSingleResult();
		
		PersistenceUtil.getEntityManager().getTransaction().commit();

		return user;
	}
		

	/**
	 * Method overload for login(String, String, boolean).
	 *
	 * @param username	the CustomerID of the customer trying to log in
	 * @param password	the Password of the customer trying to log in
	 * @return			a boolean that determines if the login was successful
	 */
	public static boolean login(String username, String password) {
		return login(username, password, false);
	}

	/**
	 * Determines if a user's credentials are correct and if so, sets a cookie variable.
	 * <p>
	 * Begin by loading the EntityManager to access the database. Start by trying to
	 * find a row where where the CustomerID matches the given username. If we do have
	 * one (size() != 0) we then check if the given password matches the password we have
	 * stored. If it's a match, we create the cookie (see Day 5's State Tracking slides). If
	 * the rememberMe argument was true, we set the cookie to exist for a day. Otherwise the
	 * cookie gets deleted at the end of the session (when the browser closes)
	 * 
	 * @param username		the customerID of the customer trying to log in
	 * @param password		the password of the customer trying to log in
	 * @param rememberMe 	whether or not to keep the cookie after the session
	 * @return				a boolean that determines if the login was successful
	 */
	public static boolean login(String username, String password, boolean rememberMe) {
		
		boolean flag = false;
		EntityManager em = PersistenceUtil.getEntityManager();

		em.getTransaction().begin();

		Customer user = em
				.createQuery("from Customer c" + " where c.customerID = ?",
						Customer.class).setParameter(1, username)
				.getSingleResult();

		em.getTransaction().commit();

		if (user == null) {
			return false;
		} else {
			if (password.equals(user.getPassword())) {
				Cookie myCookie = new Cookie("oatmealRaisin",
						user.getCustomerID());

				FacesContext fc = FacesContext.getCurrentInstance();
				HttpServletResponse r = (HttpServletResponse) fc
						.getExternalContext().getResponse();

				if (rememberMe) {
					myCookie.setMaxAge(MAX_COOKIE_AGE);
				}
				r.addCookie(myCookie);
				
				return true;
			}
		}
		return false;

	}

	/**
	 * Logs the user out by deleting the cookie. Setting the maxAge to 0 tells the browser
	 * to delete it.
	 */
	public static void logout() {
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest r = (HttpServletRequest) fc.getExternalContext()
				.getRequest();
		Cookie[] mycookies = r.getCookies();
		for (Cookie c : mycookies) {
			if (c.getName().equals("oatmealRaisin")) {
				c.setMaxAge(0);
				HttpServletResponse re = (HttpServletResponse) fc.getExternalContext().getResponse();
				re.addCookie(c);
				break;
			}
		}
	}
}
