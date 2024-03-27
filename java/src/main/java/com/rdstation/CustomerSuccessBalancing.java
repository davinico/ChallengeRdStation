package com.rdstation;

import java.util.*;

public class CustomerSuccessBalancing {

	private final List<CustomerSuccess> customerSuccess;
	private final List<Customer> customers;
	private final List<Integer> customerSuccessAway;

	public CustomerSuccessBalancing(List<CustomerSuccess> customerSuccess, List<Customer> customers,
			List<Integer> customerSuccessAway) {
		
		Set<Integer> uniqueScores = new HashSet<>();
	    for (CustomerSuccess cs : customerSuccess) {
	        if (!uniqueScores.add(cs.getScore())) {
	            throw new IllegalArgumentException("The scores of Customer Success must be unique.");
	        }
	    }
	    
	    int maxAwayCustomerSuccess = customerSuccess.size() / 2;
	    if (customerSuccessAway.size() > maxAwayCustomerSuccess) {
	        throw new IllegalArgumentException("The number of unavailable Customer Success exceeds the permitted limit.");
	    }
	    
		this.customerSuccess = customerSuccess;
		this.customers = customers;
		this.customerSuccessAway = customerSuccessAway;
	}

	public int run() {
		List<CustomerSuccess> customersSuccess = excludeAwayCustomerSuccess(customerSuccess, customerSuccessAway);
		
		return distributeCustomersPerCustomerSuccess(customersSuccess,customers);
	}

	private List<CustomerSuccess> excludeAwayCustomerSuccess(List<CustomerSuccess> customerSuccess,
			List<Integer> awayCustomerSuccess) {
		Iterator<CustomerSuccess> iterator = customerSuccess.iterator();
		while (iterator.hasNext()) {
			CustomerSuccess cs = iterator.next();
			if (awayCustomerSuccess.contains(cs.getId())) {
				iterator.remove();
			}
		}
		return customerSuccess;
	}

	private int findClosestCustomerSuccess(Customer customer, List<CustomerSuccess> customerSuccessList) {
	    int closestCustomerSuccessId = 0;
	    Integer minDifference = 10000;

	    for (CustomerSuccess cs : customerSuccessList) {
	        if (cs.getScore() > customer.getScore()) {
	            int difference = Math.abs(cs.getScore() - customer.getScore());
	            if (difference < minDifference) {
	                minDifference = difference;
	                closestCustomerSuccessId = cs.getId();
	            }
	        }
	    }
	    return closestCustomerSuccessId;
	}


	private int distributeCustomersPerCustomerSuccess(List<CustomerSuccess> customerSuccessList, List<Customer> customersList) {
	    Map<Integer, Integer> customerSuccessCustomerCount = new HashMap<>(); 
	    
	    for (CustomerSuccess cs : customerSuccessList) {
	        customerSuccessCustomerCount.put(cs.getId(), 0);
	    }

	    for (Customer customer : customersList) {
	        int closestCustomerSuccessId = findClosestCustomerSuccess(customer, customerSuccessList);
	        if (closestCustomerSuccessId != 0) {
	            customerSuccessCustomerCount.put(closestCustomerSuccessId, customerSuccessCustomerCount.get(closestCustomerSuccessId) + 1); 
	        }
	    }

	    int maxCustomers = 0;
	    int bestCustomerSuccessId = 0;
	    boolean isTie = false;
	    for (Map.Entry<Integer, Integer> entry : customerSuccessCustomerCount.entrySet()) {
	        int csId = entry.getKey();
	        int customerCount = entry.getValue();
	        if (customerCount > maxCustomers) {
	            maxCustomers = customerCount;
	            bestCustomerSuccessId = csId;
	            isTie = false; 
	        } else if (customerCount == maxCustomers) {
	            isTie = true; 
	        }
	    }

	    if (isTie) {
	        return 0;
	    }

	    return bestCustomerSuccessId;
	}
	
	public static void main(String[] args) {
		
		// Example Test 1 
		List<CustomerSuccess> customerSuccessList = new ArrayList<>();
        customerSuccessList.add(new CustomerSuccess(1, 100));
        customerSuccessList.add(new CustomerSuccess(2, 50));

        List<Customer> customersList = new ArrayList<>();
        customersList.add(new Customer(1, 20));
        customersList.add(new Customer(2, 30));
        customersList.add(new Customer(3, 35));
        customersList.add(new Customer(4, 40));
        customersList.add(new Customer(5, 60));
        customersList.add(new Customer(6, 80));

        List<Integer> customerSuccessAway = new ArrayList<>();

        CustomerSuccessBalancing customerSuccessBalancing = new CustomerSuccessBalancing(customerSuccessList, customersList, customerSuccessAway);

        int bestCustomerSuccessId = customerSuccessBalancing.run();

        System.out.println(bestCustomerSuccessId);
    }

}
