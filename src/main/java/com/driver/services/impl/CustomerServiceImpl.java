package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);

	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer=customerRepository2.findById(customerId).get();
		customer.setCustomerId(0);
		customer.setMobile(null);
		customer.setPassword(null);

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Driver driver;
		int i = 1;
		while(true) {

			   if (driverRepository2.findById(i).get().getCab().isAvailable() == true) {
				   driver = driverRepository2.findById(i).get();
				   break;
		          }
		      i++;
	   }
	       Customer customer=customerRepository2.findById(customerId).get();
		   TripBooking tripBooking=new TripBooking();
		   tripBooking.setCustomer(customer);
		   tripBooking.setTripStatus(TripStatus.CONFIRMED);
		   tripBooking.setDriver(driver);
		   tripBooking.setDistanceInKm(distanceInKm);
		   tripBooking.setFromLocation(fromLocation);
		   tripBooking.setToLocation(toLocation);
		   tripBooking.setBill(distanceInKm*10);

		   driver.getCab().setAvailable(false);
		   customer.getTripBookingList().add(tripBooking);
		   driver.getTripBookingList().add(tripBooking);

		   customerRepository2.save(customer);
		   driverRepository2.save(driver);
		   return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly

		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.CANCELED);
		Driver driver=tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking=tripBookingRepository2.findById(tripId).get();
		tripBooking.setTripStatus(TripStatus.COMPLETED);
		Driver driver=tripBooking.getDriver();
		driver.getCab().setAvailable(true);
		driver.getTripBookingList().add(tripBooking);
		driverRepository2.save(driver);
	}
}
