package com.northwindx;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.northwindx.model.jpa.Customer;
import com.northwindx.util.PersistenceUtil;

@Path("/customerservice")
public class NorthwindWebservice {

	@GET
	@Path("{key}")
	@Produces("application/json")
	public List<Customer> search(@PathParam("key") String key){
		EntityManager em = PersistenceUtil.getEntityManager();
		em.getTransaction().begin();
		
		List<Customer> set = em.createQuery("select c from Customer c where c.contactName like '%"+key+"%' or c.companyName like '%"+key+"%'", Customer.class).getResultList();
		
		em.getTransaction().commit();
		return set;
	}
}
